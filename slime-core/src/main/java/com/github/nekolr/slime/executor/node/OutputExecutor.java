package com.github.nekolr.slime.executor.node;

import com.alibaba.fastjson.JSON;
import com.github.nekolr.slime.constant.Constants;
import com.github.nekolr.slime.executor.node.event.OutputEventPublisher;
import com.github.nekolr.slime.model.SpiderNode;
import com.github.nekolr.slime.model.SpiderOutput;
import com.github.nekolr.slime.model.SpiderOutput.OutputItem;
import com.github.nekolr.slime.websocket.WebSocketEvent;
import com.github.nekolr.slime.serializer.FastJsonSerializer;
import com.github.nekolr.slime.support.ExpressionParser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.executor.NodeExecutor;
import com.github.nekolr.slime.io.SpiderResponse;
import com.github.nekolr.slime.listener.SpiderListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * 输出执行器
 */
@Component
@Slf4j
public class OutputExecutor implements NodeExecutor, SpiderListener {

    /**
     * Other Variables
     */
    String OUTPUT_OTHERS = "output-others";

    /**
     * The name of the output item
     */
    String OUTPUT_NAME = "output-name";

    /**
     * Value of the output variable
     */
    String OUTPUT_VALUE = "output-value";


    @Resource
    private ExpressionParser expressionParser;

    @Resource
    private OutputEventPublisher outputEventPublisher;

    /**
     * One node per CSVPrinter
     */
    @Getter
    private static Map<String, CSVPrinter> cachePrinter = new HashMap<>();


    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        // Generate output objects
        SpiderOutput output = new SpiderOutput();
        output.setNodeName(node.getNodeName());
        output.setNodeId(node.getNodeId());
        // Do you need any other variables（Only output on test）
        boolean outputOthers = Constants.YES.equals(node.getJsonProperty(OUTPUT_OTHERS));
        // If you don't know the answer to a question, please don't share false information.
        if (outputOthers) {
            // Other Variables
            this.outputOtherVariables(output, variables);
        }
        // Get all output data
        List<OutputItem> outputItems = this.getOutputItems(variables, context, node);
        // An output event has occurred
        outputEventPublisher.publish(context, node, outputItems);
        // Put all output items into
        output.getOutputItems().addAll(outputItems);
        // Add results to context
        context.addOutput(output);
    }

    /**
     * Get all the data from all the output defined by the user
     *
     * @param variables Passed Variable and Value
     * @param context   Executable Context
     * @param node      15th Last
     * @return All data on all output types
     */
    private List<OutputItem> getOutputItems(Map<String, Object> variables, SpiderContext context, SpiderNode node) {
        List<OutputItem> outputItems = new ArrayList<>();
        // Get all the output of a user-defined command
        List<Map<String, String>> items = node.getJsonArrayProperty(OUTPUT_NAME, OUTPUT_VALUE);
        for (Map<String, String> item : items) {
            Object value = null;
            String itemName = item.get(OUTPUT_NAME);
            String itemValue = item.get(OUTPUT_VALUE);
            try {
                value = expressionParser.parse(itemValue, variables);
                context.pause(node.getNodeId(), WebSocketEvent.COMMON_EVENT, itemName, value);
                log.debug("Translate the following text to english：{} = {}", itemName, value);
            } catch (Exception e) {
                log.error("Analyze Data Item：{} Error", itemName, e);
            }
            outputItems.add(new OutputItem(itemName, value));
        }
        return outputItems;
    }


    /**
     * Fill in other variables
     *
     * @param output    Output results
     * @param variables Passed Variable and Value
     */
    private void outputOtherVariables(SpiderOutput output, Map<String, Object> variables) {
        for (Map.Entry<String, Object> item : variables.entrySet()) {
            Object value = item.getValue();
            // resp 变量
            if (value instanceof SpiderResponse) {
                SpiderResponse resp = (SpiderResponse) value;
                output.addItem(item.getKey() + ".html", resp.getHtml());
                continue;
            }
            // Remove non-output information
            if (Constants.EXCEPTION_VARIABLE.equals(item.getKey())) {
                continue;
            }
            // Remove unsequenced parameters
            try {
                JSON.toJSONString(value, FastJsonSerializer.serializeConfig);
            } catch (Exception e) {
                continue;
            }
            // Other cases Add normally
            output.addItem(item.getKey(), item.getValue());
        }
    }

    @Override
    public void beforeStart(SpiderContext context) {

    }

    @Override
    public void afterEnd(SpiderContext context) {
        String key = context.getId();
        // After performing the above actions, please press the "Apply" button below to release the cache.，Only release one cache per execution context
        this.releasePrinter(key);
    }


    @Override
    public String supportType() {
        return "output";
    }

    private void releasePrinter(String key) {
        log.debug("release printer：key = {}", key);
        for (Iterator<Map.Entry<String, CSVPrinter>> iterator = cachePrinter.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, CSVPrinter> entry = iterator.next();
            CSVPrinter printer = entry.getValue();
            if (entry.getKey().contains(key)) {
                if (printer != null) {
                    try {
                        printer.flush();
                        printer.close();
                        iterator.remove();
                    } catch (IOException e) {
                        log.error("The file output failed", e);
                        ExceptionUtils.wrapAndThrow(e);
                    }
                }
            }
        }
    }
}
