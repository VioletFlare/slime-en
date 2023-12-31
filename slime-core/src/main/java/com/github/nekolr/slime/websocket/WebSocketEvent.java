package com.github.nekolr.slime.websocket;

import lombok.Getter;
import lombok.Setter;

/**
 * WebSocket Events
 */
@Getter
@Setter
public class WebSocketEvent<T> {

    // Event Type
    public static final String LOG_EVENT_TYPE = "log";
    public static final String DEBUG_EVENT_TYPE = "debug";
    public static final String OUTPUT_EVENT_TYPE = "output";
    public static final String ERROR_EVENT_TYPE = "error";
    public static final String FINISH_EVENT_TYPE = "finish";
    public static final String TEST_EVENT_TYPE = "test";
    public static final String STOP_EVENT_TYPE = "stop";
    public static final String RESUME_EVENT_TYPE = "resume";

    // Assistant Phone
    public static final String COMMON_EVENT = "common";
    public static final String REQUEST_PARAM_EVENT = "request-param";
    public static final String REQUEST_HEADER_EVENT = "request-header";
    public static final String REQUEST_BODY_EVENT = "request-body";
    public static final String REQUEST_AUTO_COOKIE_EVENT = "request-cookie-auto";
    public static final String REQUEST_COOKIE_EVENT = "request-cookie";


    /**
     * Event Type
     */
    private String eventType;

    /**
     * Time Tracker
     */
    private String timestamp;

    /**
     * Messages
     */
    private T message;


    public WebSocketEvent(String eventType, T message) {
        this.eventType = eventType;
        this.message = message;
    }

    public WebSocketEvent(String eventType, String timestamp, T message) {
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.message = message;
    }
}
