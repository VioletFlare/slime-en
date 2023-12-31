package com.github.nekolr.slime.model;

import com.alibaba.fastjson.JSONArray;
import com.github.nekolr.slime.constant.ConditionType;
import com.github.nekolr.slime.constant.Constants;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 15th Last
 */
public class SpiderNode {

    /**
     * 15th Last ID
     */
    @Getter
    @Setter
    private String nodeId;

    /**
     * Question Name
     */
    @Getter
    @Setter
    private String nodeName;

    /**
     * The current node is running %d tasks.
     */
    private AtomicInteger taskCounter = new AtomicInteger();

    /**
     * Add a new node
     */
    @Setter
    private Map<String, Object> jsonProperty = new HashMap<>();

    /**
     * List of nodes in front of you
     */
    private List<SpiderNode> prevNodes = new ArrayList<>();

    /**
     * List of nodes below
     */
    @Getter
    private List<SpiderNode> nextNodes = new ArrayList<>();

    /**
     * Whether to pass the variables and values of the previous node to the current node as default values.（1 Needs action 0 Not required）
     */
    private Map<String, String> transmitVariables = new HashMap<>();

    /**
     * Save the condition expression that got the last node into the current node
     */
    private Map<String, String> conditions = new HashMap<>();

    /**
     * Save the current condition type to the given node
     *
     * @see ConditionType
     */
    private Map<String, String> conditionTypes = new HashMap<>();

    /**
     * Getting node properties
     *
     * @param key Property name
     * @return Property Value
     */
    public String getJsonProperty(String key) {
        String value = (String) this.jsonProperty.get(key);
        if (value != null) {
            // Remove Escaping
            value = StringEscapeUtils.unescapeHtml4(value);
        }
        return value;
    }

    /**
     * Getting node properties
     *
     * @param key          Property name
     * @param defaultValue Default Value
     * @return Property Value
     */
    public String getJsonProperty(String key, String defaultValue) {
        String value = this.getJsonProperty(key);
        return StringUtils.isNotBlank(value) ? value : defaultValue;
    }

    /**
     * Add the following node
     *
     * @param nextNode Next Activity
     */
    public void addNextNode(SpiderNode nextNode) {
        nextNode.prevNodes.add(this);
        this.nextNodes.add(nextNode);
    }

    /**
     * Get multiple values for a node，These property values are JSON The following text is a sample answer to the question "What is your name?":My name is John Doe.
     *
     * @param keys Property name array
     * @return Multiple Value
     */
    public List<Map<String, String>> getJsonArrayProperty(String... keys) {
        int size = -1;
        List<JSONArray> arrays = new ArrayList<>();
        List<Map<String, String>> result = new ArrayList<>();
        for (int i = 0; i < keys.length; i++) {
            JSONArray jsonArray = (JSONArray) this.jsonProperty.get(keys[i]);
            if (jsonArray != null) {
                // 保证 size Only set it once
                if (size == -1) {
                    size = jsonArray.size();
                }
                // Ensure consistent element count
                if (size != jsonArray.size()) {
                    throw new ArrayIndexOutOfBoundsException();
                }
                arrays.add(jsonArray);
            }
        }
        for (int i = 0; i < size; i++) {
            Map<String, String> item = new HashMap<>();
            for (int j = 0; j < keys.length; j++) {
                String value = arrays.get(j).getString(i);
                if (value != null) {
                    value = StringEscapeUtils.unescapeHtml4(value);
                }
                item.put(keys[j], value);
            }
            result.add(item);
        }
        return result;
    }

    /**
     * Whether to pass the variables and values of the previous node to the current node
     *
     * @param fromNodeId Last Output ID
     * @return Do I need to pass a variable and a value
     */
    public boolean needTransmit(String fromNodeId) {
        String value = this.transmitVariables.get(fromNodeId);
        // If value is empty, default to passing variables
        return StringUtils.isBlank(value) || Constants.YES.equals(value);
    }

    /**
     * The following tasks are currently being executed: +1
     */
    public void increment() {
        taskCounter.incrementAndGet();
    }

    /**
     * The following tasks are currently being executed: -1
     */
    public void decrement() {
        taskCounter.decrementAndGet();
    }

    /**
     * Whether the current node and all of its parent's tasks are finished
     *
     * @return Are all of the tasks complete
     */
    public boolean isDone() {
        return isDone(new HashSet<>());
    }

    /**
     * Whether the current node and all of its parent's tasks are finished
     *
     * @param visited Record visited nodes
     * @return Are all of the tasks complete
     */
    public boolean isDone(Set<String> visited) {
        if (this.taskCounter.get() == 0) {
            for (SpiderNode prevNode : prevNodes) {
                if (visited.add(nodeId) && !prevNode.isDone(visited)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Next activity in %1Condition expression
     *
     * @param fromNodeId Last Output ID
     * @return Condition expression
     */
    public String getCondition(String fromNodeId) {
        return this.conditions.get(fromNodeId);
    }

    /**
     * Next activity in %1Condition type
     *
     * @param fromNodeId Last Output ID
     * @return Condition type
     */
    public String getConditionType(String fromNodeId) {
        return this.conditionTypes.get(fromNodeId);
    }

    /**
     * Whether to pass the variables and values of the previous node to the current node
     *
     * @param fromNodeId Last Output ID
     * @param value      Whether to pass the variable and value settings
     */
    public void setTransmitVariable(String fromNodeId, String value) {
        this.transmitVariables.put(fromNodeId, value);
    }

    /**
     * Set the condition expression for the last node to the current node
     *
     * @param fromNodeId Last Output ID
     * @param value      Condition expression
     */
    public void setCondition(String fromNodeId, String value) {
        this.conditions.put(fromNodeId, value);
    }

    /**
     * Set the condition type for the last node to the current node
     *
     * @param fromNodeId Last Output ID
     * @param value      Condition type
     */
    public void setConditionType(String fromNodeId, String value) {
        this.conditionTypes.put(fromNodeId, value);
    }

    @Override
    public String toString() {
        return "SpiderNode{" + "nodeId='" + nodeId + '\'' + ", nodeName='" + nodeName + '\'' + '}';
    }
}
