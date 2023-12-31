package com.github.nekolr.slime.executor.node;

import com.github.nekolr.slime.constant.RequestBodyType;
import com.github.nekolr.slime.support.Grammarly;
import com.github.nekolr.slime.constant.Constants;
import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.model.Grammar;
import com.github.nekolr.slime.model.SpiderNode;
import com.github.nekolr.slime.support.UserAgentManager;
import com.github.nekolr.slime.websocket.WebSocketEvent;
import com.github.nekolr.slime.executor.NodeExecutor;
import com.github.nekolr.slime.io.HttpRequest;
import com.github.nekolr.slime.io.HttpResponse;
import com.github.nekolr.slime.io.SpiderResponse;
import com.github.nekolr.slime.support.ExpressionParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Request Executor
 */
@Component
@Slf4j
public class RequestExecutor implements NodeExecutor, Grammarly {

    /**
     * Delay the request by
     */
    private static final String REQUEST_SLEEP = "sleep";

    /**
     * The following text is a request URL
     */
    private static final String REQUEST_URL = "url";

    /**
     * The requested proxy
     */
    private static final String REQUEST_PROXY = "proxy";

    /**
     * Request Method
     */
    private static final String REQUEST_METHOD = "method";

    /**
     * Query parameter name requested
     */
    private static final String REQUEST_QUERY_PARAM_NAME = "query-param-name";

    /**
     * Query parameter value requested
     */
    private static final String REQUEST_QUERY_PARAM_VALUE = "query-param-value";

    /**
     * Form action parameter name
     */
    private static final String FORM_PARAM_NAME = "form-param-name";

    /**
     * Form parameter value requested
     */
    private static final String FORM_PARAM_VALUE = "form-param-value";

    /**
     * Form parameter type requested
     */
    private static final String FORM_PARAM_TYPE = "form-param-type";

    /**
     * Form input type
     */
    private static final String FORM_PARAM_FILENAME = "form-param-filename";

    /**
     * Request type
     */
    private static final String BODY_TYPE = "body-type";

    /**
     * Request body type（MIME Type）
     */
    private static final String BODY_CONTENT_TYPE = "body-content-type";

    /**
     * Request body
     */
    private static final String REQUEST_BODY = "request-body";

    /**
     * The following text is a request Cookie Name
     */
    private static final String REQUEST_COOKIE_NAME = "cookie-name";

    /**
     * The following text is a request Cookie Translate the following text to english
     */
    private static final String REQUEST_COOKIE_VALUE = "cookie-value";

    /**
     * Request Name
     */
    private static final String REQUEST_HEADER_NAME = "header-name";

    /**
     * Request Header Value
     */
    private static final String REQUEST_HEADER_VALUE = "header-value";

    /**
     * Request timed out
     */
    private static final String REQUEST_TIMEOUT = "request-timeout";

    /**
     * Failed attempt limit
     */
    private static final String REQUEST_RETRY_COUNT = "request-retry-count";

    /**
     * Retry delay
     */
    private static final String REQUEST_RETRY_INTERVAL = "request-retry-interval";

    /**
     * Follow Up
     */
    private static final String REQUEST_FOLLOW_REDIRECT = "request-follow-redirect";

    /**
     * Auto Management Cookie
     */
    private static final String REQUEST_AUTO_COOKIE = "request-cookie-auto";

    /**
     * Random User-Agent
     */
    private static final String RANDOM_USERAGENT = "request-random-useragent";

    /**
     * Respond to the content code
     */
    private static final String RESPONSE_CHARSET = "response-charset";


    @Resource
    private ExpressionParser expressionParser;

    @Resource
    private UserAgentManager userAgentManager;


    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        // Please set a delay
        this.setupSleepTime(node, context);
        // 执行
        this.doExecute(node, context, variables);
    }

    private void doExecute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        // Request type
        RequestBodyType bodyType = RequestBodyType.geRequestBodyType(node.getJsonProperty(BODY_TYPE, "none"));
        // Retry Count
        int retryCount = NumberUtils.toInt(node.getJsonProperty(REQUEST_RETRY_COUNT), 0);
        // Retry delay，Seconds
        long retryInterval = NumberUtils.toLong(node.getJsonProperty(REQUEST_RETRY_INTERVAL), 0L);

        boolean success = false;
        for (int i = 0; i < retryCount + 1 && !success; i++) {
            HttpRequest request = HttpRequest.create();
            // 1 hour before appointment URL
            String url = this.setupUrl(request, node, context, variables);
            // Please set the request timeout
            this.setupTimeout(request, node);
            // Set Request Method
            this.setupMethod(request, node);
            // Whether to follow redirects
            this.setupFollowRedirects(request, node);
            // Set Random User-Agent
            this.setupRandomUserAgent(request, node);
            // Set Headers
            this.setupHeaders(request, node, context, variables);
            // 1 hour before appointment Cookies
            this.setupCookies(request, node, context, variables);

            List<InputStream> streams = null;
            switch (bodyType) {
                case RAW_BODY_TYPE:
                    // Set Request Body
                    this.setupRequestBody(request, node, context, variables);
                    break;
                case FORM_DATA_BODY_TYPE:
                    // Set Up Request Form
                    streams = this.setupRequestFormParam(request, node, context, variables);
                    break;
                default:
                    // Set Request Parameters
                    this.setupQueryParams(request, node, context, variables);
            }

            // Set Up Proxy
            this.setupProxy(request, node, context, variables);

            Throwable throwable = null;
            try {
                // Launch Request
                HttpResponse response = request.execute();
                if (success = response.getStatusCode() == 200) {
                    // Set Encoding
                    String charset = node.getJsonProperty(RESPONSE_CHARSET);
                    if (StringUtils.isNotBlank(charset)) {
                        response.setCharset(charset);
                        log.debug("Set the response encoding：{}", charset);
                    }
                    // Whether to auto-manage Cookie
                    String cookeAuto = node.getJsonProperty(REQUEST_AUTO_COOKIE);
                    if (Constants.YES.equals(cookeAuto)) {
                        // Will respond to Cookie Add to Cookie In and Out
                        context.getCookieContext().putAll(response.getCookies());
                    }
                    // Add the results to the given variable collection
                    variables.put(Constants.RESPONSE_VARIABLE, response);
                }
            } catch (IOException e) {
                success = false;
                throwable = e;
            } finally {
                // Close stream
                if (streams != null) {
                    for (InputStream is : streams) {
                        try {
                            IOUtils.close(is);
                        } catch (Exception ignored) {
                        }
                    }
                }
                if (!success) {
                    if (i < retryCount) {
                        // Sleep on it. Retry after a while.
                        if (retryInterval > 0) {
                            try {
                                TimeUnit.MILLISECONDS.sleep(retryInterval);
                            } catch (InterruptedException ignored) {
                            }
                        }
                        log.info("1st {} Next Retry URL：{}", i + 1, url);
                    } else {
                        log.error("Request URL：{} Error", url, throwable);
                    }
                }
            }
        }
    }

    /**
     * Set Up Proxy
     *
     * @param request   Request Package
     * @param node      15th Last
     * @param variables Passed Variable & Value
     */
    private void setupProxy(HttpRequest request, SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        String proxy = node.getJsonProperty(REQUEST_PROXY);
        if (StringUtils.isNotBlank(proxy)) {
            try {
                Object value = expressionParser.parse(proxy, variables);
                if (value != null) {
                    String[] proxyArr = StringUtils.split((String) value, Constants.PROXY_HOST_PORT_SEPARATOR);
                    if (proxyArr.length == 2) {
                        context.pause(node.getNodeId(), WebSocketEvent.COMMON_EVENT, REQUEST_PROXY, value);
                        log.info("Proxy configuration mode：{}", value);
                        request.proxy(proxyArr[0], Integer.parseInt(proxyArr[1]));
                    }
                }
            } catch (Exception e) {
                log.error("Set Proxy Error", e);
            }
        }
    }

    /**
     * 1 hour before appointment Cookies
     *
     * @param request   Request Package
     * @param node      15th Last
     * @param context   Executable Context
     * @param variables Passed Variable & Value
     */
    private void setupCookies(HttpRequest request, SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        // Get root node
        SpiderNode root = context.getRoot();

        // 根15th Last（Global）的 Cookie
        Map<String, String> cookies = this.getCookies(root.getJsonArrayProperty(REQUEST_COOKIE_NAME, REQUEST_COOKIE_VALUE), context, root, variables);
        request.cookies(cookies);

        // Cookie Contexte，Previously On This is Us...1 hour before appointment的 Cookie
        Map<String, String> cookieContext = context.getCookieContext();
        String cookeAuto = node.getJsonProperty(REQUEST_AUTO_COOKIE);
        if (Constants.YES.equals(cookeAuto) && !context.getCookieContext().isEmpty()) {
            context.pause(node.getNodeId(), WebSocketEvent.REQUEST_AUTO_COOKIE_EVENT, REQUEST_AUTO_COOKIE, cookieContext);
            request.cookies(cookieContext);
            log.info("Auto Setup Cookies：{}", cookieContext);
        }

        // Current node's Cookie
        cookies = this.getCookies(node.getJsonArrayProperty(REQUEST_COOKIE_NAME, REQUEST_COOKIE_VALUE), context, node, variables);
        request.cookies(cookies);

        // Save current settings as global settings Cookie And nodes Cookie Add Sphere Cookie In and Out
        if (Constants.YES.equals(cookeAuto)) {
            cookieContext.putAll(cookies);
        }
    }

    /**
     * Analyse Cookies
     *
     * @param cookies   Needs analysis Cookies
     * @param variables Passed Variable & Value
     * @return Analysed Cookies
     */
    private Map<String, String> getCookies(List<Map<String, String>> cookies, SpiderContext context, SpiderNode node, Map<String, Object> variables) {
        Map<String, String> result = new HashMap<>();
        if (cookies != null) {
            for (Map<String, String> cookie : cookies) {
                String cookieName = cookie.get(REQUEST_COOKIE_NAME);
                if (StringUtils.isNotBlank(cookieName)) {
                    String cookieValue = cookie.get(REQUEST_COOKIE_VALUE);
                    try {
                        Object value = expressionParser.parse(cookieValue, variables);
                        result.put(cookieName, (String) value);
                        context.pause(node.getNodeId(), WebSocketEvent.REQUEST_COOKIE_EVENT, cookieName, value);
                        log.info("1 hour before appointment Cookie：{} = {}", cookieName, value);
                    } catch (Exception e) {
                        log.error("Analysing request Cookie：{} Error", cookieName, e);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Set Request Body
     *
     * @param request   Request Package
     * @param node      15th Last
     * @param context   Executable Context
     * @param variables Passed Variable & Value
     */
    private void setupRequestBody(HttpRequest request, SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        String contentType = node.getJsonProperty(BODY_CONTENT_TYPE);
        request.contentType(contentType);
        try {
            Object requestBody = expressionParser.parse(node.getJsonProperty(REQUEST_BODY), variables);
            context.pause(node.getNodeId(), WebSocketEvent.REQUEST_BODY_EVENT, REQUEST_BODY, requestBody);
            request.requestBody(requestBody);
            log.info("Setup Request Body：{}", requestBody);
        } catch (Exception e) {
            log.debug("Setup Request Body Error", e);
        }
    }

    /**
     * Set Request Form Parameters
     *
     * @param request   Request Package
     * @param node      15th Last
     * @param context   Executable Context
     * @param variables Passed Variable & Value
     * @return Form input binary data array
     */
    private List<InputStream> setupRequestFormParam(HttpRequest request, SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        List<Map<String, String>> formParams = node.getJsonArrayProperty(FORM_PARAM_NAME, FORM_PARAM_VALUE, FORM_PARAM_TYPE, FORM_PARAM_FILENAME);
        List<InputStream> streams = new ArrayList<>();
        if (formParams != null) {
            for (Map<String, String> nameValue : formParams) {
                Object value;
                String paramName = nameValue.get(FORM_PARAM_NAME);
                if (StringUtils.isNotBlank(paramName)) {
                    String paramValue = nameValue.get(FORM_PARAM_VALUE);
                    String paramType = nameValue.get(FORM_PARAM_TYPE);
                    String paramFilename = nameValue.get(FORM_PARAM_FILENAME);
                    boolean hasFile = "file".equals(paramType);
                    try {
                        value = expressionParser.parse(paramValue, variables);
                        if (hasFile) {
                            InputStream stream = null;
                            if (value instanceof byte[]) {
                                stream = new ByteArrayInputStream((byte[]) value);
                            } else if (value instanceof String) {
                                stream = new ByteArrayInputStream(((String) value).getBytes());
                            } else if (value instanceof InputStream) {
                                stream = (InputStream) value;
                            }
                            if (stream != null) {
                                streams.add(stream);
                                request.data(paramName, paramFilename, stream);
                                context.pause(node.getNodeId(), WebSocketEvent.REQUEST_BODY_EVENT, paramName, paramFilename);
                                log.info("Set Request Form Parameters：{} = {}", paramName, paramFilename);
                            } else {
                                log.warn("Set Request Form Parameters：{} Failure，No binary content", paramName);
                            }
                        } else {
                            request.data(paramName, value);
                            context.pause(node.getNodeId(), WebSocketEvent.REQUEST_BODY_EVENT, paramName, value);
                            log.info("Set Request Form Parameters：{} = {}", paramName, value);
                        }
                    } catch (Exception e) {
                        log.error("Set Request Form Parameters：{} Error", paramName, e);
                    }
                }
            }
        }
        return streams;
    }

    /**
     * Set Query Parameters
     *
     * @param request   Request Package
     * @param node      15th Last
     * @param context   Executable Context
     * @param variables Passed Variable & Value
     */
    private void setupQueryParams(HttpRequest request, SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        // Get root node
        SpiderNode root = context.getRoot();
        // Please set the root node（Global）Query Parameters
        List<Map<String, String>> rootParams = root.getJsonArrayProperty(REQUEST_QUERY_PARAM_NAME, REQUEST_QUERY_PARAM_VALUE);
        this.setQueryParams(request, root, rootParams, context, variables);
        // Please set the query parameters for the current node
        List<Map<String, String>> params = node.getJsonArrayProperty(REQUEST_QUERY_PARAM_NAME, REQUEST_QUERY_PARAM_VALUE);
        this.setQueryParams(request, node, params, context, variables);
    }

    /**
     * Set Query Parameters
     *
     * @param request   Request Package
     * @param params    解析后的查询参数
     * @param variables Passed Variable & Value
     */
    private void setQueryParams(HttpRequest request, SpiderNode node, List<Map<String, String>> params, SpiderContext context, Map<String, Object> variables) {
        if (params != null) {
            for (Map<String, String> param : params) {
                String paramName = param.get(REQUEST_QUERY_PARAM_NAME);
                if (StringUtils.isNotBlank(paramName)) {
                    String paramValue = param.get(REQUEST_QUERY_PARAM_VALUE);
                    try {
                        Object value = expressionParser.parse(paramValue, variables);
                        request.data(paramName, value);
                        context.pause(node.getNodeId(), WebSocketEvent.REQUEST_PARAM_EVENT, paramName, value);
                        log.info("Set Request Query Parameters：{} = {}", paramName, value);
                    } catch (Exception e) {
                        log.error("Set Request Query Parameters：{} Error", paramName, e);
                    }
                }
            }
        }
    }

    /**
     * Set Random User-Agent
     *
     * @param request Request Package
     */
    private void setupRandomUserAgent(HttpRequest request, SpiderNode node) {
        // Whether to use a random User-Agent
        boolean randomUserAgent = Constants.YES.equals(node.getJsonProperty(RANDOM_USERAGENT));
        if (randomUserAgent) {
            String userAgent = userAgentManager.getRandom();
            log.info("Setup Request Header：{} = {}", "User-Agent", userAgent);
            request.header("User-Agent", userAgent);
        }
    }


    /**
     * Set Headers
     *
     * @param request   Request Package
     * @param node      15th Last
     * @param context   Executable Context
     * @param variables Passed Variable & Value
     */
    private void setupHeaders(HttpRequest request, SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        // Get root node
        SpiderNode root = context.getRoot();
        // Please set the root node（Global）The following text is from the applet server at %s
        List<Map<String, String>> rootHeaders = root.getJsonArrayProperty(REQUEST_HEADER_NAME, REQUEST_HEADER_VALUE);
        this.setHeaders(request, rootHeaders, context, root, variables);
        // Please set the title of the current node
        List<Map<String, String>> headers = node.getJsonArrayProperty(REQUEST_HEADER_NAME, REQUEST_HEADER_VALUE);
        this.setHeaders(request, headers, context, node, variables);
    }

    /**
     * Set Headers
     *
     * @param request   Request Package
     * @param headers   The following text is not translated:
     * @param variables Passed Variable & Value
     */
    private void setHeaders(HttpRequest request, List<Map<String, String>> headers, SpiderContext context, SpiderNode node, Map<String, Object> variables) {
        if (headers != null) {
            for (Map<String, String> header : headers) {
                String headerName = header.get(REQUEST_HEADER_NAME);
                if (StringUtils.isNotBlank(headerName)) {
                    String headerValue = header.get(REQUEST_HEADER_VALUE);
                    try {
                        Object value = expressionParser.parse(headerValue, variables);
                        request.header(headerName, value);
                        context.pause(node.getNodeId(), WebSocketEvent.REQUEST_HEADER_EVENT, headerName, value);
                        log.info("Setup Request Header：{} = {}", headerName, value);
                    } catch (Exception e) {
                        log.error("Setup Request Header：{} Error", headerName, e);
                    }
                }
            }
        }
    }

    /**
     * Whether to follow redirects
     *
     * @param request Request Package
     * @param node    15th Last
     */
    private void setupFollowRedirects(HttpRequest request, SpiderNode node) {
        String followRedirect = node.getJsonProperty(REQUEST_FOLLOW_REDIRECT);
        boolean following = Constants.YES.equals(followRedirect);
        log.debug("Whether to follow redirects：{}", following);
        request.followRedirects(following);
    }

    /**
     * Set Request Method
     *
     * @param request Request Package
     * @param node    15th Last
     */
    private void setupMethod(HttpRequest request, SpiderNode node) {
        String method = node.getJsonProperty(REQUEST_METHOD, "GET");
        log.debug("Set Request Method：{}", method);
        request.method(method);
    }

    /**
     * Please set the request timeout
     *
     * @param request Request Package
     * @param node    15th Last
     */
    private void setupTimeout(HttpRequest request, SpiderNode node) {
        // Default 20s
        int timeout = NumberUtils.toInt(node.getJsonProperty(REQUEST_TIMEOUT), 20000);
        log.debug("Please set the request timeout：{} ms", timeout);
        request.timeout(timeout);
    }

    /**
     * 1 hour before appointment URL
     *
     * @param request   Request Package
     * @param node      15th Last
     * @param variables Passed Variable & Value
     */
    private String setupUrl(HttpRequest request, SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        String url = null;
        try {
            url = (String) expressionParser.parse(node.getJsonProperty(REQUEST_URL), variables);
            context.pause(node.getNodeId(), WebSocketEvent.COMMON_EVENT, REQUEST_URL, url);
            log.info("Setup Request URL：{}", url);
            request.url(url);
        } catch (Exception e) {
            log.error("Setup Request URL Error", e);
            // Throw exception directly
            ExceptionUtils.wrapAndThrow(e);
        }
        return url;
    }

    /**
     * Please set a delay
     *
     * @param node    15th Last
     * @param context Executable Context
     */
    private void setupSleepTime(SpiderNode node, SpiderContext context) {
        // Getting sleep times
        String sleep = node.getJsonProperty(REQUEST_SLEEP);
        long sleepTime = NumberUtils.toLong(sleep, 0L);
        try {
            // Expected Time = Next time the alarm will be triggered + Sleep time - Current Time
            Long lastTime = (Long) context.getExtends_map().get(Constants.LAST_REQUEST_EXECUTE_TIME + node.getNodeId());
            if (lastTime != null) {
                sleepTime = lastTime + sleepTime - System.currentTimeMillis();
            }
            if (sleepTime > 0) {
                context.pause(node.getNodeId(), WebSocketEvent.COMMON_EVENT, REQUEST_SLEEP, sleepTime);
                log.debug("Please set a delay：{} ms", sleepTime);
                // Sleep
                TimeUnit.MILLISECONDS.sleep(sleepTime);
            }
            // Last time a question was answered
            context.getExtends_map().put(Constants.LAST_REQUEST_EXECUTE_TIME + node.getNodeId(), System.currentTimeMillis());
        } catch (Throwable t) {
            log.error("Failed to set delay.", t);
        }
    }

    @PostConstruct
    void initialize() {
        // Allow setting of restricted request headers
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
    }

    @Override
    public String supportType() {
        return "request";
    }

    @Override
    public List<Grammar> grammars() {
        List<Grammar> grammars = Grammar.findGrammars(SpiderResponse.class, "resp", "SpiderResponse", false);
        Grammar grammar = new Grammar();
        grammar.setFunction("resp");
        grammar.setComment("Fetch Results");
        grammar.setOwner("SpiderResponse");
        grammars.add(grammar);
        return grammars;
    }
}
