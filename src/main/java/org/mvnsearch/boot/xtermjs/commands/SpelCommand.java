package org.mvnsearch.boot.xtermjs.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.expression.BeanExpressionContextAccessor;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import javax.annotation.PostConstruct;

/**
 * spel command to execute SpEL expression
 *
 * @author linux_china
 */
public class SpelCommand implements CustomizedCommand {

	@Autowired
	private ConfigurableListableBeanFactory beanFactory;

	final ExpressionParser spelParser = new SpelExpressionParser();

	private StandardEvaluationContext spelContext;

	private BeanExpressionContext rootObject;

	@PostConstruct
	public void init() {
		this.spelContext = new StandardEvaluationContext();
		this.spelContext.setBeanResolver(new BeanFactoryResolver(this.beanFactory));
		this.spelContext.addPropertyAccessor(new BeanExpressionContextAccessor());
		this.rootObject = new BeanExpressionContext(beanFactory, null);
	}

	@Override
	public String[] getNames() {
		return new String[] { "spel" };
	}

	@Override
	@Nullable
	public Object execute(@NotNull String command, @Nullable String expressionText) {
		if (expressionText == null || expressionText.isEmpty()) {
			return new Exception("No expression");
		}
		Expression expression;
		if (expressionText.contains("#{")) {
			expression = spelParser.parseExpression(expressionText, new TemplateParserContext());
		}
		else {
			expression = spelParser.parseExpression(expressionText);
		}
		return expression.getValue(spelContext, rootObject);
	}

}
