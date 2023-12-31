package com.github.nekolr.slime.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.nekolr.slime.Spider;
import com.github.nekolr.slime.context.SpiderWebSocketContext;
import com.github.nekolr.slime.util.SpiderFlowUtils;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.CompletableFuture;

public class WebSocketEditorHandler extends TextWebSocketHandler {

    /**
     * Needs special handling
     */
    public static Spider spider;

    /**
     * WebSocket Executable Context
     */
    private SpiderWebSocketContext context;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // Convert client-side sent messages to json Text Format
        JSONObject event = JSON.parseObject(message.getPayload());
        // Get event type
        String eventType = event.getString("eventType");
        // 是 debug What kind of event?
        boolean isDebug = WebSocketEvent.DEBUG_EVENT_TYPE.equalsIgnoreCase(eventType);
        // test Type event or debug Class event
        if (WebSocketEvent.TEST_EVENT_TYPE.equalsIgnoreCase(eventType) || isDebug) {
            // Create WebSocket Communication context to use in replies
            context = new SpiderWebSocketContext(session);
            context.setDebug(isDebug);
            context.setRunning(true);
            // SyncInfo
            CompletableFuture.runAsync(() -> {
                String xml = event.getString("message");
                if (xml != null) {
                    // Answer the following questions as helpful, polite and honest as possible. If a question does not make any sense, or is not factually coherent, explain why instead of answering something not correct. If you don't know the answer to a question, please don't share false information.
                    spider.runWithTest(SpiderFlowUtils.parseXmlToSpiderNode(xml), context);
                    // Send Complete Message
                    context.write(new WebSocketEvent<>(WebSocketEvent.FINISH_EVENT_TYPE, null));
                } else {
                    // Send & Financial Information
                    context.write(new WebSocketEvent<>(WebSocketEvent.ERROR_EVENT_TYPE, "xml Invalid"));
                }
                context.setRunning(false);
            });
        }
        // stop Events，End of presentation
        else if (WebSocketEvent.STOP_EVENT_TYPE.equals(eventType) && context != null) {
            context.setRunning(false);
            context.stop();
        }
        // resume Events，唤醒
        else if (WebSocketEvent.RESUME_EVENT_TYPE.equalsIgnoreCase(eventType) && context != null) {
            context.resume();
        }
    }

}
