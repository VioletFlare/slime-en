package com.github.nekolr.slime.context;

import com.alibaba.fastjson.JSON;
import com.github.nekolr.slime.model.SpiderLog;
import com.github.nekolr.slime.model.SpiderOutput;
import com.github.nekolr.slime.websocket.WebSocketEvent;
import com.github.nekolr.slime.serializer.FastJsonSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Date;

/**
 * WebSocket Communication context to use in replies
 */
public class SpiderWebSocketContext extends SpiderContext {

    @Getter
    @Setter
    private boolean debug;

    private WebSocketSession session;

    private Object lock = new Object();


    public SpiderWebSocketContext(WebSocketSession session) {
        this.session = session;
    }

    @Override
    public void addOutput(SpiderOutput output) {
        this.write(new WebSocketEvent<>(WebSocketEvent.OUTPUT_EVENT_TYPE, output));
    }

    public void log(SpiderLog log) {
        write(new WebSocketEvent<>(WebSocketEvent.LOG_EVENT_TYPE,
                DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"), log));
    }

    public <T> void write(WebSocketEvent<T> event) {
        try {
            String message = JSON.toJSONString(event, FastJsonSerializer.serializeConfig);
            if (session.isOpen()) {
                synchronized (session) {
                    session.sendMessage(new TextMessage(message));
                }
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    public void pause(String nodeId, String event, String key, Object value) {
        if (this.debug && this.isRunning()) {
            synchronized (this) {
                if (this.debug && this.isRunning()) {
                    synchronized (lock) {
                        try {
                            // Send Message to Client
                            write(new WebSocketEvent<>(WebSocketEvent.DEBUG_EVENT_TYPE,
                                    new DebugInfo(nodeId, event, key, value)));
                            lock.wait();
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
            }
        }
    }

    @Override
    public void resume() {
        if (this.debug) {
            synchronized (lock) {
                lock.notify();
            }
        }
    }

    @Override
    public void stop() {
        if (this.debug) {
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    class DebugInfo {
        /**
         * 15th Last ID
         */
        private String nodeId;

        /**
         * Assistant Phone
         */
        private String event;

        /**
         * Property name（When debugging，The client needs to know the name of the property the current breakpoint is in）
         */
        private String key;

        /**
         * Property Value（When debugging，The client needs to know the value of the property the current breakpoint is in，The value is computed by the expression parser）
         */
        private Object value;
    }
}
