package com.github.nekolr.slime.support;

import com.github.nekolr.slime.config.ProxyConfig;
import com.github.nekolr.slime.domain.dto.ProxyDTO;
import com.github.nekolr.slime.service.ProxyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class ProxyManager {

    @Resource
    private ProxyConfig proxyConfig;

    @Resource
    private ProxyService proxyService;


    private List<ProxyDTO> proxies = new CopyOnWriteArrayList<>();


    @PostConstruct
    private void initialize() {
        this.proxies.addAll(proxyService.findAll());
    }

    public void remove(ProxyDTO proxy) {
        this.proxies.remove(proxy);
        this.proxyService.removeById(proxy.getId());
    }

    public boolean add(ProxyDTO proxy) {
        if (proxy.getId() != null && this.proxies.contains(proxy)) {
            return true;
        }
        if (check(proxy) != -1) {
            ProxyDTO entity = proxyService.save(proxy);
            this.proxies.add(entity);
            return true;
        }
        return false;
    }

    /**
     * Get a random one http proxy
     *
     * @return
     */
    public ProxyDTO getHttpProxy() {
        return getHttpProxy(true);
    }

    /**
     * Get a random one https proxy
     *
     * @return
     */
    public ProxyDTO getHttpsProxy() {
        return getHttpsProxy(true);
    }

    /**
     * Get a random one http proxy
     *
     * @return
     */
    public ProxyDTO getHttpProxy(boolean anonymous) {
        return random(get("http", anonymous));
    }

    /**
     * Get a random one HTTPS proxy
     *
     * @return
     */
    public ProxyDTO getHttpsProxy(boolean anonymous) {
        return random(get("https", anonymous));
    }

    /**
     * Randomly returns a proxy from the list of proxies
     *
     * @param proxies List of proxies
     * @return a proxy
     */
    private ProxyDTO random(List<ProxyDTO> proxies) {
        int size;
        if (proxies != null && (size = proxies.size()) > 0) {
            return proxies.get(RandomUtils.nextInt(0, size));
        }
        return null;
    }

    /**
     * Query the state of a given proxy profile IP List
     *
     * @param type      Proxy Type http or https
     * @param anonymous Whether to support anonymous connections
     * @return proxy IP List
     */
    private List<ProxyDTO> get(String type, boolean anonymous) {
        List<ProxyDTO> proxyList = new ArrayList<>();
        if (this.proxies != null) {
            for (ProxyDTO proxy : this.proxies) {
                if (type.equalsIgnoreCase(proxy.getType()) && proxy.getAnonymous().equals(anonymous)) {
                    proxyList.add(proxy);
                }
            }
        }
        return proxyList;
    }

    /**
     *  Checking proxy
     *
     * @param proxy proxy
     * @return Delay time
     */
    public long check(ProxyDTO proxy) {
        try {
            long st = System.currentTimeMillis();
            Jsoup.connect(proxyConfig.getCheckUrl())
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .timeout(proxyConfig.getCheckTimeout())
                    .proxy(proxy.getIp(), proxy.getPort())
                    .execute();
            st = System.currentTimeMillis() - st;
            log.info(" Checking proxy：{}:{}，Delay：{} ms", proxy.getIp(), proxy.getPort(), st);
            return st;
        } catch (Exception e) {
            log.info(" Checking proxy：{}:{}，Time Elapsed", proxy.getIp(), proxy.getPort());
            return -1;
        }
    }

}
