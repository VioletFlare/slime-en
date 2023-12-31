package com.github.nekolr.slime.job;

import com.github.nekolr.slime.Spider;
import com.github.nekolr.slime.config.SpiderConfig;
import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.context.SpiderContextHolder;
import com.github.nekolr.slime.context.SpiderJobContext;
import com.github.nekolr.slime.domain.SpiderFlow;
import com.github.nekolr.slime.domain.SpiderTask;
import com.github.nekolr.slime.service.SpiderFlowService;
import com.github.nekolr.slime.service.SpiderTaskService;
import com.github.nekolr.slime.constant.Constants;
import com.github.nekolr.slime.util.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SpiderJob extends QuartzJobBean {

    private Spider spider;

    @Resource
    private SpiderConfig spiderConfig;

    @Resource
    private SpiderTaskService spiderTaskService;

    @Resource
    private SpiderFlowService spiderFlowService;

    @Autowired
    public void setSpider(Spider spider) {
        this.spider = spider;
    }

    private static Map<Long, SpiderContext> contextMap = new ConcurrentHashMap<>();

    @Override
    protected void executeInternal(JobExecutionContext context) {
        if (spiderConfig.getJobEnabled()) {
            JobDataMap jobDataMap = context.getMergedJobDataMap();
            SpiderFlow spiderFlow = (SpiderFlow) jobDataMap.get(Constants.QUARTZ_SPIDER_FLOW_PARAM_NAME);
            run(spiderFlow, context.getNextFireTime());
        }
    }

    /**
     * Executable
     *
     * @param flowId 流程 ID
     */
    public void run(Long flowId) {
        run(spiderFlowService.getById(flowId), null);
    }

    /**
     * Executable
     *
     * @param flow     流程
     * @param nextTime Next time the reminder will be triggered
     */
    private void run(SpiderFlow flow, Date nextTime) {
        // Current Time
        Date now = new Date();
        // Create a new process task
        SpiderTask task = createSpiderTask(flow.getId());
        // Create Executable Context
        SpiderJobContext context = null;
        try {
            context = SpiderJobContext.create(spiderConfig.getWorkspace(), flow.getId(), task.getId(), false);
            SpiderContextHolder.set(context);
            log.info("流程：{} Start Implementation，Tasks ID 为：{}", flow.getName(), task.getId());
            contextMap.put(task.getId(), context);
            spider.run(flow, context);
            log.info("流程：{} Finished，Tasks ID 为：{}，Next execution time：{}", flow.getName(), task.getId(), TimeUtils.format(nextTime));
        } catch (FileNotFoundException e) {
            log.error("Failed to create log file.", e);
        } catch (Throwable t) {
            log.error("流程：{} Executable，Tasks ID 为：{}", flow.getName(), task.getId());
        } finally {
            // Close stream
            if (context != null) {
                context.close();
            }
            contextMap.remove(task.getId());
            SpiderContextHolder.remove();
            // Update the task end time
            task.setEndTime(new Date());
            spiderTaskService.save(task);
        }
        spiderFlowService.executeCountIncrement(flow.getId(), now, nextTime);
    }

    /**
     * Create a new process task
     *
     * @param flowId 流程 ID
     * @return Process Task
     */
    private SpiderTask createSpiderTask(Long flowId) {
        SpiderTask task = new SpiderTask();
        task.setFlowId(flowId);
        task.setBeginTime(new Date());
        spiderTaskService.save(task);
        return task;
    }

    /**
     * Get contexts
     *
     * @param taskId Tasks ID
     * @return Executable Context
     */
    public static SpiderContext getSpiderContext(Long taskId) {
        return contextMap.get(taskId);
    }
}
