package com.github.nekolr.slime.executor.node.event;

import com.github.nekolr.slime.constant.Constants;
import com.github.nekolr.slime.constant.OutputType;
import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.model.SpiderNode;
import com.github.nekolr.slime.model.SpiderOutput.OutputItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutputEventPublisher {

    @Autowired
    @SuppressWarnings("all")
    private ApplicationEventPublisher eventPublisher;

    /**
     * An output event has occurred
     *
     * @param context     Executable Context
     * @param node        15th Last
     * @param outputItems All data output
     */
    public void publish(SpiderContext context, SpiderNode node, List<OutputItem> outputItems) {
        OutputType[] outputTypes = OutputType.values();
        for (OutputType outputType : outputTypes) {
            if (Constants.YES.equals(node.getJsonProperty(outputType.getVariableName()))) {
                eventPublisher.publishEvent(new OutputEventBean(context, node, outputItems, outputType.getVariableName()));
            }
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    class OutputEventBean {
        private SpiderContext context;
        private SpiderNode node;
        private List<OutputItem> outputItems;
        private String event;
    }

}
