package com.github.nekolr.slime.constant;

/**
 * Constant
 */
public interface Constants {

    //*************************************************************************************
    //                                    Spider Context
    //*************************************************************************************

    /**
     * Death Lock Detect AtomicInteger Name of the program
     */
    String ATOMIC_DEAD_CYCLE = "__atomic_dead_cycle";

    /**
     * Name of the variable stored in the up- and down-context when a node exited abnormally
     */
    String EXCEPTION_VARIABLE = "__ex";

    /**
     * The prefix of the variable to which the last request was sent
     */
    String LAST_REQUEST_EXECUTE_TIME = "__last_request_execute_time_";

    /**
     * Respondent:
     */
    String RESPONSE_VARIABLE = "resp";

    /**
     * SQL EXECUTE RESULT AS VARIABLE
     */
    String SQL_RESULT = "rs";


    //*************************************************************************************
    //                                     Thread Pool
    //*************************************************************************************

    /**
     * Thread Group Name
     */
    String SLIME_THREAD_GROUP_NAME = "slime-thread-group";

    /**
     * Thread name prefix
     */
    String SLIME_THREAD_NAME_PREFIX = "slime-thread-";


    //*************************************************************************************
    //                                 Node Property Name
    //*************************************************************************************

    /**
     * Thread count for single process tasks
     */
    String THREAD_COUNT = "threadCount";

    /**
     * Same suit
     */
    String RUN_SYNC = "runSync";

    /**
     * Classify Message as & Spam
     */
    String NODE_TYPE = "shape";

    /**
     * Repeats the action of the node
     */
    String NODE_LOOP_COUNT = "loopCount";

    /**
     * Subscript when executing node cycles
     */
    String NODE_LOOP_INDEX = "loopIndex";

    /**
     * Start index of the node cycle
     */
    String NODE_LOOP_START_INDEX = "loopStartIndex";

    /**
     * End subscript of a node cycle
     */
    String NODE_LOOP_END_INDEX = "loopEndIndex";

    /**
     * Function
     */
    String FUNCTION = "function";

    /**
     * 流程 ID
     */
    String FLOW_ID = "flowId";

    /**
     * Data Sources ID
     */
    String DATASOURCE_ID = "datasourceId";


    //*************************************************************************************
    //                                 Node Property Value
    //*************************************************************************************

    String YES = "1";


    //*************************************************************************************
    //                                    Quartz Job
    //*************************************************************************************

    /**
     * Default reminder value
     */
    String QUARTZ_JOB_NAME_PREFIX = "SLIME_TASK_";

    /**
     * Context parameters for scheduled tasks：SpiderFlow Name of the program
     */
    String QUARTZ_SPIDER_FLOW_PARAM_NAME = "SLIME_SPIDER_FLOW";


    //*************************************************************************************
    //                                       Others
    //*************************************************************************************

    /**
     * Separator between host name and port number in the proxy host URL
     */
    String PROXY_HOST_PORT_SEPARATOR = ":";

    /**
     * Log category prefix where this action was triggered
     */
    String SPIDER_FLOW_LOG_DIR_PREFIX = "slime_spider_flow_";

    /**
     * Log category prefix for process tasks
     */
    String SPIDER_TASK_LOG_DIR_PREFIX = "slime_spider_task_";
}
