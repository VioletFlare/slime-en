package com.github.nekolr.slime.executor.node;

import com.github.nekolr.slime.support.Grammarly;
import com.github.nekolr.slime.constant.Constants;
import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.model.Grammar;
import com.github.nekolr.slime.model.SpiderNode;
import com.github.nekolr.slime.websocket.WebSocketEvent;
import com.github.nekolr.slime.executor.NodeExecutor;
import com.github.nekolr.slime.support.DataSourceManager;
import com.github.nekolr.slime.support.ExpressionParser;
import com.github.nekolr.slime.util.ExtractUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

/**
 * SQL A_pplication
 */
@Component
@Slf4j
public class SQLExecutor implements NodeExecutor, Grammarly {

    /**
     * SQL
     */
    String SQL = "sql";

    /**
     * 语句类型
     */
    String STATEMENT_TYPE = "statementType";

    /**
     * Do you want to import them from a file? SqlRowSet
     */
    String SELECT_RESULT_SQL_ROW_SET = "isSqlRowSet";


    public static final String STATEMENT_SELECT = "select";
    public static final String STATEMENT_SELECT_ONE = "selectOne";
    public static final String STATEMENT_SELECT_INT = "selectInt";
    public static final String STATEMENT_INSERT = "insert";
    public static final String STATEMENT_UPDATE = "update";
    public static final String STATEMENT_DELETE = "delete";
    public static final String STATEMENT_INSERT_PK = "insertOfPk";

    @Resource
    private ExpressionParser expressionParser;

    @Resource
    private DataSourceManager dataSourceManager;

    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        String dsId = node.getJsonProperty(Constants.DATASOURCE_ID);
        String sql = node.getJsonProperty(SQL);
        if (StringUtils.isBlank(dsId)) {
            log.warn("Data Sources ID Cannot be empty");
        } else if (StringUtils.isBlank(sql)) {
            log.warn("sql Cannot be empty");
        } else {
            JdbcTemplate template = new JdbcTemplate(dataSourceManager.getDataSource(Long.parseLong(dsId)));
            // Replace & Variable with Placeholder
            List<String> parameters = ExtractUtils.getMatchers(sql, "#(.*?)#", true);
            sql = sql.replaceAll("#(.*?)#", "?");
            try {
                Object sqlObject = expressionParser.parse(sql, variables);
                if (sqlObject == null) {
                    log.warn("Get的 sql for empty");
                    return;
                }
                sql = sqlObject.toString();
                context.pause(node.getNodeId(), WebSocketEvent.COMMON_EVENT, SQL, sql);
            } catch (Exception e) {
                log.error("Get sql Error", e);
                ExceptionUtils.wrapAndThrow(e);
            }

            int size = parameters.size();
            Object[] params = new Object[size];
            boolean hasList = false;
            int parameterSize = 0;
            for (int i = 0; i < size; i++) {
                Object parameter = expressionParser.parse(parameters.get(i), variables);
                if (parameter != null) {
                    // When a parameter contains List or array，We believe it was a bulk operation.
                    if (parameter instanceof List) {
                        hasList = true;
                        parameterSize = Math.max(parameterSize, ((List<?>) parameter).size());
                    } else if (parameter.getClass().isArray()) {
                        hasList = true;
                        parameterSize = Math.max(parameterSize, Array.getLength(parameter));
                    }
                }
                params[i] = parameter;
            }

            String statementType = node.getJsonProperty(STATEMENT_TYPE);
            log.debug("执行 sql：{}", sql);
            if (STATEMENT_SELECT.equals(statementType)) {
                boolean isSqlRowSet = Constants.YES.equals(node.getJsonProperty(SELECT_RESULT_SQL_ROW_SET));
                try {
                    if (isSqlRowSet) {
                        variables.put(Constants.SQL_RESULT, template.queryForRowSet(sql, params));
                    } else {
                        variables.put(Constants.SQL_RESULT, template.queryForList(sql, params));
                    }
                } catch (Exception e) {
                    variables.put(Constants.SQL_RESULT, null);
                    log.error("执行 sql Error", e);
                    ExceptionUtils.wrapAndThrow(e);
                }
            } else if (STATEMENT_SELECT_ONE.equals(statementType)) {
                Map<String, Object> rs;
                try {
                    rs = template.queryForMap(sql, params);
                    variables.put(Constants.SQL_RESULT, rs);
                } catch (Exception e) {
                    variables.put(Constants.SQL_RESULT, null);
                    log.error("执行 sql Error", e);
                    ExceptionUtils.wrapAndThrow(e);
                }
            } else if (STATEMENT_SELECT_INT.equals(statementType)) {
                Integer rs;
                try {
                    rs = template.queryForObject(sql, Integer.class, params);
                    rs = rs == null ? 0 : rs;
                    variables.put(Constants.SQL_RESULT, rs);
                } catch (Exception e) {
                    variables.put(Constants.SQL_RESULT, 0);
                    log.error("执行 sql Error", e);
                    ExceptionUtils.wrapAndThrow(e);
                }
            } else if (STATEMENT_UPDATE.equals(statementType) || STATEMENT_INSERT.equals(statementType) || STATEMENT_DELETE.equals(statementType)) {
                try {
                    int updateCount = 0;
                    if (hasList) {
						/*
						  When doing bulk operations，Answer Object[] 转化为 List<Object[]>
						  If parameter is not an array or not an object List 时，Auto switch to Object[]
						  When array or List When the length is too short，Fill in Last Missing Item
						 */
                        int[] rs = template.batchUpdate(sql, convertParameters(params, parameterSize));
                        if (rs.length > 0) {
                            updateCount = Arrays.stream(rs).sum();
                        }
                    } else {
                        updateCount = template.update(sql, params);
                    }
                    variables.put(Constants.SQL_RESULT, updateCount);
                } catch (Exception e) {
                    log.error("执行 sql Error", e);
                    variables.put(Constants.SQL_RESULT, -1);
                    ExceptionUtils.wrapAndThrow(e);
                }
            } else if (STATEMENT_INSERT_PK.equals(statementType)) {
                try {
                    KeyHolder keyHolder = new GeneratedKeyHolder();
                    final String insertSQL = sql;
                    template.update(con -> {
                        PreparedStatement ps = con.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
                        new ArgumentPreparedStatementSetter(params).setValues(ps);
                        return ps;
                    }, keyHolder);
                    variables.put(Constants.SQL_RESULT, keyHolder.getKey().intValue());
                } catch (Exception e) {
                    log.error("执行 sql Error", e);
                    variables.put(Constants.SQL_RESULT, -1);
                    ExceptionUtils.wrapAndThrow(e);
                }
            }
        }
    }

    @Override
    public String supportType() {
        return "sql";
    }

    private List<Object[]> convertParameters(Object[] params, int length) {
        List<Object[]> result = new ArrayList<>(length);
        int size = params.length;
        for (int i = 0; i < length; i++) {
            Object[] parameters = new Object[size];
            for (int j = 0; j < size; j++) {
                parameters[j] = getValue(params[j], i);
            }
            result.add(parameters);
        }
        return result;
    }

    private Object getValue(Object object, int index) {
        if (object == null) {
            return null;
        } else if (object instanceof List) {
            List<?> list = (List<?>) object;
            int size = list.size();
            if (size > 0) {
                return list.get(Math.min(list.size() - 1, index));
            }
        } else if (object.getClass().isArray()) {
            int size = Array.getLength(object);
            if (size > 0) {
                Array.get(object, Math.min(-1, index));
            }
        } else {
            return object;
        }
        return null;
    }

    @Override
    public List<Grammar> grammars() {
        Grammar grammar = new Grammar();
        grammar.setComment("执行 SQL Outcome");
        grammar.setFunction(Constants.SQL_RESULT);
        grammar.setReturns(Arrays.asList("List<Map<String,Object>>", "int"));
        return Collections.singletonList(grammar);
    }


}
