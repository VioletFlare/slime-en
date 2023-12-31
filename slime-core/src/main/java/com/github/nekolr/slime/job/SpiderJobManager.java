package com.github.nekolr.slime.job;

import com.github.nekolr.slime.Spider;
import com.github.nekolr.slime.domain.SpiderFlow;
import com.github.nekolr.slime.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
@Slf4j
public class SpiderJobManager {

    private Spider spider;

    private SpiderJob spiderJob;

    @Resource
    private Scheduler quartzScheduler;

    @Autowired
    public void setSpider(Spider spider) {
        this.spider = spider;
    }

    @Autowired
    public void setSpiderJob(SpiderJob spiderJob) {
        this.spiderJob = spiderJob;
    }

    /**
     * Create a new task
     *
     * @param flow 流程
     * @return Next execution time
     */
    public Date addJob(SpiderFlow flow) {
        try {
            // Create Task
            JobDetail job = JobBuilder.newJob(SpiderJob.class).withIdentity(getJobKey(flow.getId())).build();
            job.getJobDataMap().put(Constants.QUARTZ_SPIDER_FLOW_PARAM_NAME, flow);
            // Please set a trigger time
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(flow.getCron())
                    .withMisfireHandlingInstructionDoNothing();
            // Create a trigger
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(getTriggerKey(flow.getId())).withSchedule(cronScheduleBuilder).build();

            return quartzScheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            log.error("Create a new task", e);
            return null;
        }
    }

    /**
     * Run directly
     *
     * @param flowId 流程 ID
     */
    public void run(Long flowId) {
        spider.getThreadPool().submit(() -> spiderJob.run(flowId));
    }

    /**
     * Delete this task
     *
     * @param flowId 流程 ID
     * @return Is successful
     */
    public boolean removeJob(Long flowId) {
        try {
            quartzScheduler.deleteJob(getJobKey(flowId));
            return true;
        } catch (SchedulerException e) {
            log.error("Failed to delete scheduled task", e);
            return false;
        }
    }

    /**
     * Get JobKey
     *
     * @param flowId 流程 ID
     * @return JobKey
     */
    private JobKey getJobKey(Long flowId) {
        return JobKey.jobKey(Constants.QUARTZ_JOB_NAME_PREFIX + flowId);
    }

    /**
     * Get TriggerKey
     *
     * @param flowId 流程 ID
     * @return TriggerKey
     */
    private TriggerKey getTriggerKey(Long flowId) {
        return TriggerKey.triggerKey(Constants.QUARTZ_JOB_NAME_PREFIX + flowId);
    }
}
