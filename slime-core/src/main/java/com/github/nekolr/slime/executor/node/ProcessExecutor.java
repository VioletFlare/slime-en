package com.github.nekolr.slime.executor.node;

import com.github.nekolr.slime.Spider;
import com.github.nekolr.slime.constant.Constants;
import com.github.nekolr.slime.domain.SpiderFlow;
import com.github.nekolr.slime.model.SpiderNode;
import com.github.nekolr.slime.service.SpiderFlowService;
import com.github.nekolr.slime.util.SpiderFlowUtils;
import lombok.extern.slf4j.Slf4j;
import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.executor.NodeExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Sub-process Executor
 */
@Component
@Slf4j
public class ProcessExecutor implements NodeExecutor {

    private Spider spider;

    @Autowired
    public void setSpider(Spider spider) {
        this.spider = spider;
    }

    @Resource
    private SpiderFlowService spiderFlowService;

    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        Long flowId = Long.parseLong(node.getJsonProperty(Constants.FLOW_ID));
        SpiderFlow spiderFlow = spiderFlowService.getById(flowId);
        if (spiderFlow != null) {
            log.info("Executable：{}", spiderFlow.getName());
            SpiderNode root = SpiderFlowUtils.parseXmlToSpiderNode(spiderFlow.getXml());
            spider.executeNode(null, root, context, variables);
        } else {
            log.info("Executable：{} Failure，Cannot find the sub-process", flowId);
        }
    }

    @Override
    public String supportType() {
        return "process";
    }
}
