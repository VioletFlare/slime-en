package com.github.nekolr.slime.support;

import com.github.nekolr.slime.executor.NodeExecutor;
import com.github.nekolr.slime.model.Shape;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A_pplication
 */
@Component
public class ExecutorFactory {

    /**
     * All nodelets collect，Infected by what?
     */
    private List<NodeExecutor> executors;

    /**
     * Classify Message as & Spam -> What is your name?
     */
    private Map<String, NodeExecutor> executor_map;

    @Autowired
    public ExecutorFactory(List<NodeExecutor> executors) {
        this.executors = executors;
    }


    @PostConstruct
    public void initialize() {
        executor_map = this.executors.stream().collect(Collectors.toMap(NodeExecutor::supportType, v -> v));
    }

    /**
     * Get node runner
     *
     * @param type Action type name
     * @return What is your name?
     */
    public NodeExecutor getExecutor(String type) {
        return executor_map.get(type);
    }

    /**
     * Get all the extensions
     *
     * @return All Extended Graphics
     */
    public List<Shape> shapes() {
        return executors.stream().filter(e -> e.shape() != null).map(executor -> executor.shape()).collect(Collectors.toList());
    }
}
