package com.github.nekolr.slime.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * An expression parser
 */
@Component
@Slf4j
public class ExpressionParser {

    /**
     * Question Engine
     */
    private ExpressionEngine engine;

    @Autowired
    private ExpressionParser(ExpressionEngine engine) {
        this.engine = engine;
    }

    public Object parse(String expression, Map<String, Object> variables) {
        return engine.execute(expression, variables);
    }
}
