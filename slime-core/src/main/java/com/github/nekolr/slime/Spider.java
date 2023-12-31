package com.github.nekolr.slime;

import com.alibaba.ttl.TtlRunnable;
import com.github.nekolr.slime.concurrent.SpiderThreadPoolExecutor.SubThreadPoolExecutor;
import com.github.nekolr.slime.concurrent.SpiderThreadPoolExecutor;
import com.github.nekolr.slime.config.SpiderConfig;
import com.github.nekolr.slime.constant.ConditionType;
import com.github.nekolr.slime.constant.Constants;
import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.context.SpiderContextHolder;
import com.github.nekolr.slime.domain.SpiderFlow;
import com.github.nekolr.slime.executor.NodeExecutor;
import com.github.nekolr.slime.listener.SpiderListener;
import com.github.nekolr.slime.model.SpiderNode;
import com.github.nekolr.slime.model.SpiderOutput;
import com.github.nekolr.slime.support.ExecutorFactory;
import com.github.nekolr.slime.support.ExpressionParser;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.github.nekolr.slime.util.*;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The bulk of the reptile's logic is here.
 */
@Component
@Slf4j
public class Spider {

    /**
     * Configure KSpread...
     */
    @Resource
    private SpiderConfig spiderConfig;

    /**
     * Listeners Unite
     */
    @SuppressWarnings("all")
    @Autowired(required = false)
    private List<SpiderListener> listeners;

    /**
     * A_pplication
     */
    private ExecutorFactory executorFactory;

    /**
     * An expression parser
     */
    @Resource
    private ExpressionParser expressionParser;

    /**
     * Threads
     */
    @Getter
    private SpiderThreadPoolExecutor threadPool;


    @Autowired
    public void setExecutorFactory(ExecutorFactory executorFactory) {
        this.executorFactory = executorFactory;
    }


    @PostConstruct
    public void initialize() {
        initializeListeners();
        threadPool = new SpiderThreadPoolExecutor(spiderConfig.getMaxThreads());
    }

    /**
     * Initialize listener collection
     */
    private void initializeListeners() {
        if (this.listeners == null) {
            this.listeners = Collections.EMPTY_LIST;
        }
    }

    /**
     * Answer the following questions as helpful, polite and honest as possible. If a question does not make any sense, or is not factually coherent, explain why instead of answering something not correct. If you don't know the answer to a question, please don't share false information.
     *
     * @param root    根15th Last
     * @param context Executable Context
     */
    public void runWithTest(SpiderNode root, SpiderContext context) {
        // Set the execution context
        SpiderContextHolder.set(context);
        // Counter for Death Rattle checks
        AtomicInteger executeCount = new AtomicInteger(0);
        // Save to Context，For follow-up inspection
        context.getExtends_map().put(Constants.ATOMIC_DEAD_CYCLE, executeCount);
        // 执行
        doRun(root, context, new HashMap<>());
        // When the debugging task has finished，判断是否超出预期
        if (executeCount.get() > spiderConfig.getDeadCycle()) {
            log.error("Detected possible death loop, aborting，Test canceled");
        } else {
            log.info("Test Complete");
        }
        // Remove from overview，Prevent memory leaks
        SpiderContextHolder.remove();
    }


    /**
     * Enter passphrase for key %1
     *
     * @param spiderFlow 流程
     * @param context    Executable Context
     * @return Output results
     */
    public List<SpiderOutput> run(SpiderFlow spiderFlow, SpiderContext context) {
        return run(spiderFlow, context, new HashMap<>());
    }

    /**
     * Enter passphrase for key %1
     *
     * @param spiderFlow 流程
     * @param context    Executable Context
     * @param variables  Passed Variable & Value
     * @return Output results
     */
    public List<SpiderOutput> run(SpiderFlow spiderFlow, SpiderContext context, Map<String, Object> variables) {
        if (variables == null) {
            variables = new HashMap<>();
        }
        // Analyze the flow chart for the node information
        SpiderNode root = SpiderFlowUtils.parseXmlToSpiderNode(spiderFlow.getXml());
        // The real method of implementation
        doRun(root, context, variables);
        // Return results to
        return context.getOutputs();
    }


    /**
     * The real method of implementation
     *
     * @param root      根15th Last
     * @param context   Executable Context
     * @param variables Passed Variable & Value
     */
    private void doRun(SpiderNode root, SpiderContext context, Map<String, Object> variables) {
        // Get the number of threads for a single instance of the user's configuration
        int threads = NumberUtils.toInt(root.getJsonProperty(Constants.THREAD_COUNT), spiderConfig.getDefaultThreads());
        // Create child thread pool，+1 If a question does not make any sense, or is not factually coherent, explain why instead of answering something not correct. If you don't know the answer to a question, please don't share false information.
        SubThreadPoolExecutor subThreadPool = threadPool.createSubThreadPoolExecutor(Math.max(threads, 1) + 1);
        context.setSubThreadPool(subThreadPool);
        context.setRoot(root);
        // Trigger Listener
        listeners.forEach(listener -> listener.beforeStart(context));
        // Schedule the task
        Future<?> future = runWithScheduler(subThreadPool, root, context, variables);
        try {
            // Canceled
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Appear serious abnormalities", e);
        }
    }

    /**
     * Schedule the task
     *
     * @param subThreadPool Threads
     * @param root          根15th Last
     * @param context       Executable Context
     * @param variables     Passed Variable & Value
     * @return Schedule the task of another thread
     */
    private Future<?> runWithScheduler(SubThreadPoolExecutor subThreadPool, SpiderNode root,
                                       SpiderContext context, Map<String, Object> variables) {
        Future<?> future = subThreadPool.submitAsync(TtlRunnable.get(() -> {
            try {
                // Import a CSV file
                this.executeNode(null, root, context, variables);
                // The following sources are available:
                Queue<Future<?>> queue = context.getFutureTaskQueue();
                // Spin，Until the queue is empty
                while (!queue.isEmpty()) {
                    // Complete the first task
                    Optional<Future<?>> firstTask = queue.stream().filter(Future::isDone).findFirst();
                    // Skip _Backwards
                    if (firstTask.isPresent()) {
                        try {
                            // Remove completed tasks
                            queue.remove(firstTask.get());
                            // Whether to stop the server
                            if (context.isRunning()) {
                                // GetTaskReturnValue
                                Task task = (Task) firstTask.get().get();
                                // Notes Executed，Number of Tasks -1
                                task.node.decrement();
                                NodeExecutor executor = task.executor;
                                // The assistant is a helpful, respectful and honest assistant. Always answer as helpfully as possible.
                                if (executor.allowExecuteNext(task.node, context, task.variables)) {
                                    log.debug("执行15th Last {} 完毕", task.node);
                                    // Answer the following question
                                    executeNextNodes(task.node, context, task.variables);
                                } else {
                                    log.debug("执行15th Last {} 完毕，Ignore the next node", task.node);
                                }
                            }

                        } catch (InterruptedException | ExecutionException e) {
                            log.error("Getting task return value failed，Skip this task", e);
                        }
                    }
                }
                // Waiting for all worker threads in the thread pool to exit
                subThreadPool.awaitTermination();
            } finally {
                // Trigger Listener
                listeners.forEach(listener -> listener.afterEnd(context));
            }
        }), null);

        return future;
    }

    /**
     * Execute the next node
     *
     * @param node      Current node
     * @param context   Executable Context
     * @param variables Passed Variable & Value
     */
    public void executeNextNodes(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        List<SpiderNode> nextNodes = node.getNextNodes();
        if (nextNodes != null) {
            for (SpiderNode next : nextNodes) {
                this.executeNode(node, next, context, variables);
            }
        }
    }

    /**
     * Execute current node
     *
     * @param fromNode  The previous node that was invoked
     * @param node      Current node
     * @param context   Executable Context
     * @param variables Passed Variable & Value
     */
    public void executeNode(SpiderNode fromNode, SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        String nodeType = node.getJsonProperty(Constants.NODE_TYPE);
        // If the current node type is empty, then execute the next node
        if (StringUtils.isBlank(nodeType)) {
            this.executeNextNodes(node, context, variables);
            return;
        }
        // Are the transfer conditions met?
        if (!validateCondition(fromNode, node, variables)) {
            return;
        }
        // Get the corresponding exec for a node
        NodeExecutor executor = executorFactory.getExecutor(nodeType);
        if (executor == null) {
            log.error("15th Last {} Failed to execute，No matching launcher found：{}", node, nodeType);
            context.setRunning(Boolean.FALSE);
            return;
        }

        int loopCount = 1; // Default number of cycles 1
        int loopStartIndex = 0; // Default start index in a list of strings 0
        int loopEndIndex = 0; // Default End Markup 0
        // Remove the selected node
        String loopCountValue = node.getJsonProperty(Constants.NODE_LOOP_COUNT);
        // Do not repeat play if already playing
        if (StringUtils.isNotBlank(loopCountValue)) {
            // Number of parsecs:
            Object loopCountObj = expressionParser.parse(loopCountValue, variables);
            // Please try to convert the following text to a number，Failure return 0
            loopCount = NumberUtils.toInt(loopCountObj.toString(), 0);

            if (loopCount > 0) {
                int start = NumberUtils.toInt(node.getJsonProperty(Constants.NODE_LOOP_START_INDEX), 0);
                // Ensure that the start index is non-negative
                loopStartIndex = Math.max(start, 0);
                // Ensure that the starting index is not greater than the number of elements in the list
                loopStartIndex = (loopStartIndex >= loopCount ? 0 : loopStartIndex);
                // -1 Default to using (Number of cycles - 1) Signature State Icon
                int end = NumberUtils.toInt(node.getJsonProperty(Constants.NODE_LOOP_END_INDEX), -1);
                if (end >= 0) {
                    // Ensure finish below marker does not exceed cycle count
                    loopEndIndex = Math.min(end, loopCount - 1);
                } else {
                    loopEndIndex = loopCount - 1;
                }
            }
            log.info("Get the number of cycles {} The value of the progress bar：{}", loopCountValue, loopCount);
        }

        if (loopCount > 0) {
            // Get the value of a cycle counter
            String indexName = node.getJsonProperty(Constants.NODE_LOOP_INDEX);
            // Temporarily store tasks
            List<Task> tasks = Lists.newArrayListWithExpectedSize(loopCount);
            for (int i = loopStartIndex; i < loopEndIndex + 1; i++) {
                node.increment(); // Number of tasks +1
                if (context.isRunning()) {
                    // Needed variables and values
                    Map<String, Object> newVariables = new HashMap<>();
                    // Determine if a variable and value should be passed
                    if (fromNode == null || node.needTransmit(fromNode.getNodeId())) {
                        newVariables.putAll(variables);
                    }
                    // Enter the name of the variable and its value in the cycle sub-tag
                    if (StringUtils.isNotBlank(indexName)) {
                        newVariables.put(indexName, i);
                    }
                    tasks.add(new Task(TtlRunnable.get(() -> {
                        if (context.isRunning()) {
                            try {
                                // Text to translate: D-Bus Call
                                deadCycleCheck(context);
                                // Execute specific logic
                                executor.execute(node, context, newVariables);
                                // When no exception occurs，Remove exception variables from the context
                                newVariables.remove(Constants.EXCEPTION_VARIABLE);
                            } catch (Throwable t) {
                                newVariables.put(Constants.EXCEPTION_VARIABLE, t);
                                log.error("执行15th Last {} An error has occured", node, t);
                            }
                        }
                    }), node, newVariables, executor));
                }
            }
            LinkedBlockingQueue<Future<?>> taskQueue = context.getFutureTaskQueue();
            for (Task task : tasks) {
                // Get root node
                SpiderNode root = context.getRoot();
                // Whether to sync the local notes with the online notes
                boolean runSync = Constants.YES.equals(root.getJsonProperty(Constants.RUN_SYNC));
                if (executor.isAsync() && !runSync) {
                    // Can be run in background，Submit to thread pool
                    taskQueue.add(context.getSubThreadPool().submitAsync(task.runnable, task));
                } else {
                    // Can't be done out of order，Then run directly in the current thread
                    FutureTask<Task> futureTask = new FutureTask<>(task.runnable, task);
                    futureTask.run();
                    taskQueue.add(futureTask);
                }
            }
        }
    }

    /**
     * Text to translate: D-Bus Call
     *
     * @param context Executable Context
     */
    private void deadCycleCheck(SpiderContext context) {
        AtomicInteger count = (AtomicInteger) context.getExtends_map().get(Constants.ATOMIC_DEAD_CYCLE);
        if (count != null && count.incrementAndGet() > spiderConfig.getDeadCycle()) {
            log.error("Suspected death cycle found，Stop Operation");
            context.setRunning(Boolean.FALSE);
        }
    }

    /**
     * Check if the conditions for a transfer are met
     *
     * @param fromNode  The previous node that was invoked
     * @param node      Current node
     * @param variables Passed Variable & Value
     * @return Are conditions met
     */
    private boolean validateCondition(SpiderNode fromNode, SpiderNode node, Map<String, Object> variables) {
        if (fromNode != null) {
            // Is there an exception
            boolean hasException = variables.get(Constants.EXCEPTION_VARIABLE) != null;
            String conditionType = node.getConditionType(fromNode.getNodeId());
            // Condition not met
            if ((ConditionType.ON_EXCEPTION.getCode().equals(conditionType) && !hasException) ||
                    (ConditionType.NO_EXCEPTION.getCode().equals(conditionType) && hasException)) {
                return false;
            }
            // Then, given the conditional expression
            String condition = node.getCondition(fromNode.getNodeId());
            if (StringUtils.isNotBlank(condition)) {
                try {
                    // Translate the following text to english
                    Object result = expressionParser.parse(condition, variables);
                    if (result != null) {
                        boolean match = Boolean.toString(true).equals(result) || Objects.equals(result, true);
                        log.debug("Condition expression {} The result is {}", condition, match);
                        return match;
                    }
                } catch (Throwable t) {
                    log.error("Condition {} Error", condition, t);
                    return false;
                }
            }
        }
        return true;
    }


    @AllArgsConstructor
    class Task {

        /**
         * Tasks
         */
        Runnable runnable;

        /**
         * 15th Last
         */
        SpiderNode node;

        /**
         * Passed Variable & Value
         */
        Map<String, Object> variables;

        /**
         * A_pplication
         */
        NodeExecutor executor;
    }
}
