package com.github.nekolr.slime.util;

import com.alibaba.fastjson.JSON;
import com.github.nekolr.slime.model.SpiderNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Classify Message as & Spam
 */
public class SpiderFlowUtils {

    public static SpiderNode parseXmlToSpiderNode(String xml) {
        Document document = Jsoup.parse(xml);
        Elements cells = document.getElementsByTag("mxCell");
        Map<String, SpiderNode> nodeMap = new HashMap<>();
        SpiderNode root = null;
        SpiderNode firstNode = null;
        Map<String, Map<String, String>> edgeMap = new HashMap<>();
        for (Element element : cells) {
            Map<String, Object> jsonProperty = getSpiderFlowJsonProperty(element);
            SpiderNode node = new SpiderNode();
            node.setJsonProperty(jsonProperty);
            String nodeId = element.attr("id");
            node.setNodeName(element.attr("value"));
            node.setNodeId(nodeId);
            nodeMap.put(nodeId, node);
            if (element.hasAttr("edge")) {    // Determine if a connection is established
                edgeMap.put(nodeId, Collections.singletonMap(element.attr("source"), element.attr("target")));
            } else if (jsonProperty != null && node.getJsonProperty("shape") != null) {
                if ("start".equals(node.getJsonProperty("shape"))) {
                    root = node;
                }
            }
            if ("0".equals(nodeId)) {
                firstNode = node;
            }
        }
        // Answer calls
        Set<String> edges = edgeMap.keySet();
        for (String edgeId : edges) {
            Set<Map.Entry<String, String>> entries = edgeMap.get(edgeId).entrySet();
            SpiderNode edgeNode = nodeMap.get(edgeId);
            for (Map.Entry<String, String> edge : entries) {
                SpiderNode sourceNode = nodeMap.get(edge.getKey());
                SpiderNode targetNode = nodeMap.get(edge.getValue());
                // Set Condition
                targetNode.setCondition(sourceNode.getNodeId(), edgeNode.getJsonProperty("condition"));
                // Set the properties of the transformation
                targetNode.setConditionType(sourceNode.getNodeId(), edgeNode.getJsonProperty("exception-flow"));
                targetNode.setTransmitVariable(sourceNode.getNodeId(), edgeNode.getJsonProperty("transmit-variable"));
                sourceNode.addNextNode(targetNode);
            }
        }
        firstNode.addNextNode(root);
        return firstNode;
    }

    /**
     * Extracting the files from "%s" json Property
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> getSpiderFlowJsonProperty(Element element) {
        Elements elements = element.getElementsByTag("JsonProperty");
        if (!CollectionUtils.isEmpty(elements)) {
            return JSON.parseObject(elements.get(0).html(), Map.class);
        }
        return null;
    }
}
