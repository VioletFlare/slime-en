package com.github.nekolr.slime.support;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class UserAgentManager {

    private static final String USERAGENT_FILE_PATH = "fake_useragent.json";

    private static List<BrowserUserAgent> user_agents;

    @PostConstruct
    private void initialize() {
        InputStream input = null;
        try {
            input = ClassUtils.getDefaultClassLoader().getResourceAsStream(USERAGENT_FILE_PATH);
            String json = IOUtils.toString(input, StandardCharsets.UTF_8);
            user_agents = JSON.parseArray(json, BrowserUserAgent.class);
        } catch (IOException e) {
            log.error("Speak {} 1 hour before appointment", USERAGENT_FILE_PATH, e);
        } finally {
            try {
                IOUtils.close(input);
            } catch (IOException e) {
                log.error("Failed to turn off", e);
            }
        }
    }

    @Data
    static class BrowserUserAgent {
        private String browser;
        private List<String> useragent = new ArrayList<>();
    }

    /**
     * Get a random User-Agent
     *
     * @return A random User-Agent
     */
    public String getRandom() {
        int browserIndex = RandomUtils.nextInt(0, user_agents.size());
        List<String> userAgents = user_agents.get(browserIndex).useragent;
        int useragentIndex = RandomUtils.nextInt(0, userAgents.size());
        return user_agents.get(browserIndex).useragent.get(useragentIndex);
    }

    /**
     * Get the latest version of any browser User-Agent
     *
     * @return A most recent User-Agent
     */
    public String getNewest() {
        int browserIndex = RandomUtils.nextInt(0, user_agents.size());
        return user_agents.get(browserIndex).useragent.get(0);
    }

    /**
     * Get Chrome What is the latest version of the browser? User-Agent
     *
     * @return A most recent User-Agent
     */
    public String getChromeNewest() {
        return user_agents.get(0).useragent.get(0);
    }

    /**
     * Get FireFox What is the latest version of the browser? User-Agent
     *
     * @return A most recent User-Agent
     */
    public String getFireFoxNewest() {
        return user_agents.get(1).useragent.get(0);
    }

    /**
     * Get Edge What is the latest version of the browser? User-Agent
     *
     * @return A most recent User-Agent
     */
    public String getEdgeNewest() {
        return user_agents.get(2).useragent.get(0);
    }
}
