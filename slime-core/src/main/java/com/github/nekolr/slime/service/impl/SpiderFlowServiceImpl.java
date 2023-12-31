package com.github.nekolr.slime.service.impl;

import com.github.nekolr.slime.config.SpiderConfig;
import com.github.nekolr.slime.constant.Constants;
import com.github.nekolr.slime.dao.SpiderFlowRepository;
import com.github.nekolr.slime.domain.SpiderFlow;
import com.github.nekolr.slime.io.Line;
import com.github.nekolr.slime.io.RandomAccessFileReader;
import com.github.nekolr.slime.job.SpiderJobManager;
import com.github.nekolr.slime.service.SpiderFlowService;
import com.github.nekolr.slime.service.SpiderTaskService;
import com.github.nekolr.slime.util.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerUtils;
import org.quartz.spi.OperableTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

@Service
@Slf4j
public class SpiderFlowServiceImpl implements SpiderFlowService {

    @Resource
    private SpiderConfig spiderConfig;

    @Resource
    private SpiderJobManager spiderJobManager;

    @Resource
    private SpiderTaskService spiderTaskService;

    @Resource
    private SpiderFlowRepository spiderFlowRepository;

    @Autowired
    @SuppressWarnings("all")
    private PlatformTransactionManager txManager;

    /**
     * Add a new task after the current one
     */
    @PostConstruct
    public void initializeJobs() {
        TransactionTemplate tmpl = new TransactionTemplate(txManager);
        tmpl.execute(new TransactionCallbackWithoutResult() {
            @Override
            // 保证 doInTransactionWithoutResult Code in the Methods
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                // Next run of all processes
                clearNextExecuteTime();
                // Get all active scheduled tasks
                List<SpiderFlow> flows = findByJobEnabled(Boolean.TRUE);
                if (flows != null && !flows.isEmpty()) {
                    for (SpiderFlow flow : flows) {
                        if (StringUtils.isNotBlank(flow.getCron())) {
                            Date nextTime = spiderJobManager.addJob(flow);
                            log.info("Initializing Scheduled Tasks：{}，Next execution time：{}", flow.getName(), TimeUtils.format(nextTime));
                            if (nextTime != null) {
                                flow.setNextExecuteTime(nextTime);
                                updateNextExecuteTime(flow);
                            }
                        }
                    }
                }
            }
        });
    }


    @Override
    public SpiderFlow getById(Long id) {
        return spiderFlowRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeCountIncrement(Long id, Date lastExecuteTime, Date nextExecuteTime) {
        if (nextExecuteTime == null) {
            spiderFlowRepository.executeCountIncrement(lastExecuteTime, id);
        } else {
            spiderFlowRepository.executeCountIncrementAndExecuteNextTime(lastExecuteTime, nextExecuteTime, id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearNextExecuteTime() {
        spiderFlowRepository.clearNextExecuteTime();
    }

    @Override
    public List<SpiderFlow> findByJobEnabled(Boolean jobEnabled) {
        return spiderFlowRepository.findByJobEnabled(jobEnabled);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SpiderFlow save(SpiderFlow flow) {
        if (flow.getId() != null) {
            Optional<SpiderFlow> entity = spiderFlowRepository.findById(flow.getId());
            if (entity.isPresent()) {
                // Please fill in the following text
                flow.setCron(entity.get().getCron());
                flow.setJobEnabled(entity.get().getJobEnabled());
                flow.setExecuteCount(entity.get().getExecuteCount());
                flow.setLastExecuteTime(entity.get().getLastExecuteTime());
                // If a question does not make any sense, or is not factually coherent, explain why instead of answering something not correct. If you don't know the answer to a question, please don't share false information.，Next time the action should be run
                if (StringUtils.isNotBlank(flow.getCron()) && flow.getJobEnabled()) {
                    CronTrigger trigger = TriggerBuilder.newTrigger()
                            .withSchedule(CronScheduleBuilder.cronSchedule(flow.getCron()))
                            .build();
                    flow.setNextExecuteTime(trigger.getFireTimeAfter(null));
                    // Reissue the task
                    if (spiderJobManager.removeJob(flow.getId())) {
                        spiderJobManager.addJob(flow);
                    }
                } else {
                    flow.setNextExecuteTime(entity.get().getNextExecuteTime());
                }
            }
        }
        return spiderFlowRepository.save(flow);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNextExecuteTime(SpiderFlow flow) {
        spiderFlowRepository.updateNextExecuteTime(flow.getNextExecuteTime(), flow.getId());
    }

    @Override
    public Page<SpiderFlow> findAll(SpiderFlow flow, Pageable pageable) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withNullHandler(ExampleMatcher.NullHandler.IGNORE)
                .withMatcher("name", ExampleMatcher.GenericPropertyMatcher.of(ExampleMatcher.StringMatcher.CONTAINING));
        Page<SpiderFlow> page = spiderFlowRepository.findAll(Example.of(flow, matcher), pageable);
        page.get().forEach(sf -> sf.setRunningCount(spiderTaskService.getRunningCountByFlowId(sf.getId())));
        return page;
    }

    @Override
    public List<SpiderFlow> findAll() {
        return spiderFlowRepository.findAll();
    }

    @Override
    public List<SpiderFlow> findOtherFlows(Long id) {
        return spiderFlowRepository.findByIdNot(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Long id) {
        spiderFlowRepository.deleteById(id);
    }

    @Override
    public void run(Long id) {
        spiderJobManager.run(id);
    }

    @Override
    public List<String> getRecentTriggerTime(String cron, int numTimes) {
        List<String> list = new ArrayList<>();
        CronTrigger trigger;
        try {
            trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        } catch (Exception e) {
            throw new RuntimeException("cron Answer " + cron + " 有误");
        }
        List<Date> dates = TriggerUtils.computeFireTimes((OperableTrigger) trigger, null, numTimes);
        for (Date date : dates) {
            list.add(TimeUtils.format(date));
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCronAndNextExecuteTime(Long id, String cron) {
        // Create a trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        // Delete this task
        if (spiderJobManager.removeJob(id)) {
            // Next time the alarm will run，Update process
            spiderFlowRepository.updateCronAndNextExecuteTime(id, cron, trigger.getFireTimeAfter(null));
            SpiderFlow flow = getById(id);
            // Scheduled Tasks
            if (flow.getJobEnabled()) {
                // Add a new task
                spiderJobManager.addJob(flow);
            } else {
                spiderFlowRepository.updateCronAndNextExecuteTime(id, cron, null);
            }
        } else {
            spiderFlowRepository.updateCronAndNextExecuteTime(id, cron, null);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void start(Long id) {
        // First try to delete the task
        if (spiderJobManager.removeJob(id)) {
            // Set timed task status to 'on'
            spiderFlowRepository.updateJobEnabled(id, Boolean.TRUE);
            SpiderFlow flow = getById(id);
            if (flow != null) {
                // Add a new task
                Date nextExecuteTime = spiderJobManager.addJob(flow);
                if (nextExecuteTime != null) {
                    // Next execution time
                    flow.setNextExecuteTime(nextExecuteTime);
                    spiderFlowRepository.updateNextExecuteTime(flow.getNextExecuteTime(), flow.getId());
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stop(Long id) {
        spiderFlowRepository.updateJobEnabled(id, Boolean.FALSE);
        spiderFlowRepository.updateNextExecuteTime(null, id);
        spiderJobManager.removeJob(id);
    }

    @Override
    public List<Line> log(Long id, Long taskId, String keywords, Long index, Integer count, Boolean reversed, Boolean matchCase, Boolean regex) {

        if (Objects.isNull(taskId)) {
            Long maxId = spiderTaskService.getMaxTaskIdByFlowId(id);
            if (Objects.isNull(maxId)) {
                throw new RuntimeException("There is no task to run");
            } else {
                taskId = maxId;
            }
        }

        List<Line> lines;
        String flowFolder = Constants.SPIDER_FLOW_LOG_DIR_PREFIX + id;
        String taskFolder = Constants.SPIDER_TASK_LOG_DIR_PREFIX + taskId;
        File logFile = new File(new File(spiderConfig.getWorkspace()), "logs" + File.separator + flowFolder + File.separator + "logs" + File.separator + taskFolder + ".log");

        try (RandomAccessFileReader reader = new RandomAccessFileReader(new RandomAccessFile(logFile, "r"), index == null ? -1 : index, reversed == null || reversed)) {
            lines = reader.readLine(count == null ? 10 : count, keywords, matchCase != null && matchCase, regex != null && regex);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Logfile does not exist", e);
        } catch (IOException e) {
            throw new RuntimeException("Read error from logfile", e);
        }
        return lines;
    }
}
