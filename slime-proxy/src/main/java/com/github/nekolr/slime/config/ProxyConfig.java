package com.github.nekolr.slime.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Getter
public class ProxyConfig {

    /**
     * The URL used when checking if the proxy is effective.
     */
    @Value("${spider.proxy.check-url}")
    private String checkUrl;

    /**
     * The timeout for checking if the proxy is effective，Seconds
     */
    @Value("${spider.proxy.check-timeout}")
    private Integer checkTimeout;

    /**
     * Check interval
     */
    @Value("${spider.proxy.check-interval}")
    private Duration checkInterval;
}
