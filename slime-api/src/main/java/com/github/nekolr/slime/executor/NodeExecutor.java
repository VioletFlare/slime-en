package com.github.nekolr.slime.executor;

import com.github.nekolr.slime.model.Shape;
import com.github.nekolr.slime.model.SpiderNode;
import com.github.nekolr.slime.context.SpiderContext;

import java.util.Map;

/**
 * What is your name?
 */
public interface NodeExecutor {

    /**
     * The following sources are available:
     *
     * @param node      15th Last
     * @param context   Executable Context
     * @param variables Passed Variable & Value
     */
    void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables);

    /**
     * Whether to allow the next node to be executed
     *
     * @param node      15th Last
     * @param context   Executable Context
     * @param variables Passed Variable & Value
     * @return Whether to allow the next node to be executed
     */
    default boolean allowExecuteNext(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        return true;
    }

    /**
     * The corresponding graphical node（Only the Expand node has this data）
     *
     * @return The corresponding graphical node
     */
    default Shape shape() {
        return null;
    }

    /**
     * Whether to execute in background
     *
     * @return Whether to launch a new thread for the command
     */
    default boolean isAsync() {
        return true;
    }

    /**
     * Supported node types
     *
     * @return Action type name
     */
    String supportType();
}
