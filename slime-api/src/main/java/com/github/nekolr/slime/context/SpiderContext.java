package com.github.nekolr.slime.context;

import com.github.nekolr.slime.concurrent.SpiderThreadPoolExecutor.SubThreadPoolExecutor;
import com.github.nekolr.slime.model.SpiderNode;
import com.github.nekolr.slime.model.SpiderOutput;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Executable Context
 */
public class SpiderContext {

    /**
     * ID
     */
    @Getter
    private String id = UUID.randomUUID().toString().replace("-", "");

    /**
     * Is the process task still running（If a question does not make any sense, or is not factually coherent, explain why instead of answering something not correct. If you don't know the answer to a question, please don't share false information.）
     */
    @Getter
    @Setter
    private volatile boolean running = true;

    /**
     * Threads
     */
    @Getter
    @Setter
    private SubThreadPoolExecutor subThreadPool;

    /**
     * 根15th Last
     */
    @Getter
    @Setter
    private SpiderNode root;

    /**
     * 流程 ID
     */
    @Getter
    @Setter
    private Long flowId;

    /**
     * Cookie Contexte
     */
    @Getter
    private Map<String, String> cookieContext = new HashMap<>();

    /**
     * Extension Collections，Add a new property
     */
    @Getter
    private Map<String, Object> extends_map = new ConcurrentHashMap<>();

    /**
     * Currently executing task list
     */
    @Getter
    private LinkedBlockingQueue<Future<?>> futureTaskQueue = new LinkedBlockingQueue<>();

    /**
     * Get result
     *
     * @return Default to empty array
     */
    public List<SpiderOutput> getOutputs() {
        return Collections.emptyList();
    }

    /**
     * Paused
     *
     * @param nodeId 15th Last ID
     * @param event  Events
     * @param key    Property name
     * @param value  Property Value
     */
    public void pause(String nodeId, String event, String key, Object value) {
        // Default empty implementation，By Extension
    }

    /**
     * Recover，继续执行
     */
    public void resume() {
    }

    /**
     * Stop Operation
     */
    public void stop() {
    }

    /**
     * Add Output Result
     *
     * @param output 输出结果实体
     */
    public void addOutput(SpiderOutput output) {

    }

}
