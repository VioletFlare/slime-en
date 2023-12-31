package com.github.nekolr.slime.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Crawler Configuration
 */
@Configuration
@Getter
public class SpiderConfig {

    /**
     * Maximum number of threads in thread pool
     */
    @Value("${spider.thread-pool.max-threads}")
    private Integer maxThreads;

    /**
     * Default maximum thread count for a single task
     */
    @Value("${spider.default-threads}")
    private Integer defaultThreads;

    /**
     * Text to translate: D-Bus Call（The number of times to execute the node when a dead loop is detected）
     */
    @Value("${spider.dead-cycle}")
    private Integer deadCycle;

    /**
     * Working on the To-do
     */
    @Value("${spider.workspace}")
    private String workspace;

    /**
     * Whether to start the timer.
     */
    @Value("${spider.job.enabled}")
    private Boolean jobEnabled;

}
