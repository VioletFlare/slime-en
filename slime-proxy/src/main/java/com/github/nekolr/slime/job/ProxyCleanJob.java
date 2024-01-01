package com.github.nekolr.slime.job;

import com.github.nekolr.slime.config.ProxyConfig;
import com.github.nekolr.slime.domain.dto.ProxyDTO;
import com.github.nekolr.slime.service.ProxyService;
import com.github.nekolr.slime.support.ProxyManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

@Component
@Slf4j
public class ProxyCleanJob {

    private ScheduledFuture<?> future;

    @Resource
    private ProxyConfig proxyConfig;

    @Resource
    private ProxyService proxyService;

    @Resource
    private ProxyManager proxyManager;

    /**
     * After @EnableWebSocket After，Just a moment, I'm thinking...，Needs manual creation
     */
    @Resource(name = "proxyCleanThreadPoolTaskScheduler")
    private ThreadPoolTaskScheduler scheduler;


    @Bean
    public ThreadPoolTaskScheduler proxyCleanThreadPoolTaskScheduler(TaskSchedulerBuilder builder) {
        builder.poolSize(8);
        return builder.build();
    }


    public void run() {
        // Delay 10 Seconds before executing
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MILLISECOND, 10000);
        future = scheduler.scheduleWithFixedDelay(this::clean, now.getTime(), proxyConfig.getCheckInterval().toMillis());
    }

    public void stop() {
        if (future != null) {
            future.cancel(true);
            future = null;
        }
    }

    public boolean running() {
        if (future != null) {
            return !future.isDone();
        } else {
            return false;
        }
    }

    public void clean() {
        log.debug("Start scanning for embedded proxies IP Effective");
        List<ProxyDTO> proxies = proxyService.findAll();
        if (!proxies.isEmpty()) {
            for (ProxyDTO proxy : proxies) {
                scheduler.submit(() -> {
                    if (proxyManager.check(proxy) == -1) {
                        proxyManager.remove(proxy);
                    } else {
                        // Update Authentication
                        proxy.setValidTime(new Date());
                        proxyService.save(proxy);
                    }
                });
            }
        }
        log.debug("proxy IP Effectiveness Check Complete");
    }
}
