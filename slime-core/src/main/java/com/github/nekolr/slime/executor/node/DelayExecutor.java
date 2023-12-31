package com.github.nekolr.slime.executor.node;

import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.executor.NodeExecutor;
import com.github.nekolr.slime.model.SpiderNode;
import com.github.nekolr.slime.support.ExpressionParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Delay Executor
 */
@Component
@Slf4j
public class DelayExecutor implements NodeExecutor {

    /**
     * Delay before executing a command
     */
    private static final String DELAY_TIME = "delayTime";

    @Resource
    private ExpressionParser expressionParser;

    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        String delayTimes = node.getJsonProperty(DELAY_TIME);
        if (StringUtils.isNotBlank(delayTimes)) {
            try {
                Object value = expressionParser.parse(delayTimes, variables);
                Long times;
                if (value instanceof String) {
                    times = NumberUtils.toLong((String) value, 0L);
                } else if (value instanceof Integer) {
                    times = ((Integer) value).longValue();
                } else {
                    times = (Long) value;
                }
                if (times > 0) {
                    // Sleep
                    try {
                        log.info("Please set a delay for the action：{} ms", times);
                        TimeUnit.MILLISECONDS.sleep(times);
                    } catch (Throwable t) {
                        log.error("Failed to set delay before executing a command", t);
                    }
                }
            } catch (Exception e) {
                log.error("Delay before executing a command：{} Failure", delayTimes, e);
            }
        }
    }

    @Override
    public String supportType() {
        return "delay";
    }
}
