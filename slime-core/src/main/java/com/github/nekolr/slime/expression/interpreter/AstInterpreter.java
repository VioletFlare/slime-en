
package com.github.nekolr.slime.expression.interpreter;

import com.github.nekolr.slime.expression.ExpressionError;
import com.github.nekolr.slime.expression.ExpressionError.*;
import com.github.nekolr.slime.expression.ExpressionTemplate;
import com.github.nekolr.slime.expression.ExpressionTemplateContext;
import com.github.nekolr.slime.expression.parsing.Ast;
import com.github.nekolr.slime.expression.parsing.Ast.*;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * Interprets a Template given a TemplateContext to lookup variable values in and writes the evaluation results to an output
 * stream. Uses the global {@link AbstractReflection} instance as returned by {@link AbstractReflection#getInstance()} to access members and call
 * methods.
 * </p>
 *
 * <p>
 * The interpeter traverses the AST as stored in {@link ExpressionTemplate#getNodes()}. the interpeter has a method for each AST node type
 * (see {@link Ast} that evaluates that node. A node may return a value, to be used in the interpretation of a parent node or to
 * be written to the output stream.
 * </p>
 **/
public class AstInterpreter {
    public static Object interpret(ExpressionTemplate template, ExpressionTemplateContext context) {
        try {
            return interpretNodeList(template.getNodes(), template, context);
        } catch (Throwable t) {
            if (t instanceof TemplateException) {
                throw (TemplateException) t;
            } else {
                ExpressionError.error("Executing expression errors " + t.getMessage(), template.getNodes().get(0).getSpan(), t);
                return null; // never reached
            }
        }
    }

    public static Object interpretNodeList(List<Node> nodes, ExpressionTemplate template, ExpressionTemplateContext context) throws IOException {
        String result = "";
        for (int i = 0, n = nodes.size(); i < n; i++) {
            Node node = nodes.get(i);
            Object value = node.evaluate(template, context);
            if (node instanceof Text) {
                result += node.getSpan().getText();
            } else if (value == null) {
                if (i == 0 && i + 1 == n) {
                    return null;
                }
                result += "null";
            } else {
                if (i == 0 && i + 1 == n) {
                    return value;
                }
                result += value;
            }
        }
        return result;
    }
}
