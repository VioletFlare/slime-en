package com.github.nekolr.slime.listener;

import com.github.nekolr.slime.context.SpiderContext;

/**
 * Mission Execution Listener
 */
public interface SpiderListener {

    /**
     * Before starting a task
     *
     * @param context Executable Context
     */
    void beforeStart(SpiderContext context);

    /**
     * After the mission is over,
     *
     * @param context Executable Context
     */
    void afterEnd(SpiderContext context);
}
