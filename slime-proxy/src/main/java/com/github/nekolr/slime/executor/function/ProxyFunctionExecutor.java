package com.github.nekolr.slime.executor.function;

import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.domain.dto.ProxyDTO;
import com.github.nekolr.slime.executor.FunctionExecutor;
import com.github.nekolr.slime.support.ProxyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProxyFunctionExecutor implements FunctionExecutor {

    private static ProxyManager proxyManager;

    @Override
    public String getFunctionPrefix() {
        return "proxy";
    }

    @Comment("Get a random one http 代理")
    public static String http(boolean anonymous) {
        return convertToString(proxyManager.getHttpProxy(anonymous));
    }

    @Comment("Get a random high score http 代理")
    public static String http() {
        return http(true);
    }

    @Comment("Get a random one https 代理")
    public static String https(boolean anonymous) {
        return convertToString(proxyManager.getHttpsProxy(anonymous));
    }

    @Comment("Get a random high score https 代理")
    public static String https() {
        return https(true);
    }

    private static String convertToString(ProxyDTO proxy) {
        if (proxy == null) {
            return null;
        }
        return String.format("%s:%s", proxy.getIp(), proxy.getPort());
    }

    @Comment("Add Proxy to Internal Proxy Pool")
    public void add(String ip, Integer port, String type, boolean anonymous) {
        ProxyDTO proxy = new ProxyDTO();
        proxy.setIp(ip);
        proxy.setPort(Integer.valueOf(port));
        proxy.setType(type);
        proxy.setAnonymous(anonymous);
        proxyManager.add(proxy);
    }

    @Autowired
    public void setProxyManager(ProxyManager proxyManager) {
        ProxyFunctionExecutor.proxyManager = proxyManager;
    }
}
