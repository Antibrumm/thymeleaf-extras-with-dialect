package ch.mfrey.thymeleaf.extras.with;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.processor.IProcessorMatcher;
import org.thymeleaf.processor.ProcessorMatchingContext;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;

public class WithProcessor extends AbstractProcessor {

	private final WithMatcher matcher;

	protected WithProcessor() {
		super();
		this.matcher = new WithMatcher();
	}

	@Override
	public int getPrecedence() {
		return ATTR_PRECEDENCE;
	}

	public static final int ATTR_PRECEDENCE = 600;

	public IProcessorMatcher<? extends Node> getMatcher() {
		return matcher;
	}

	@Override
	protected ProcessorResult doProcess(Arguments arguments, ProcessorMatchingContext processorMatchingContext, Node node) {

		Element element = ((Element) node);
		Map<String, Attribute> attributeMap = element.getAttributeMap();
		String dialectPrefix = processorMatchingContext.getDialectPrefix();

		Configuration configuration = arguments.getConfiguration();
		final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);

		final Map<String, Object> newLocalVariables = new HashMap<String, Object>(1, 1.0f);

		Arguments withExecutionArguments = arguments;

		for (Attribute attribute : attributeMap.values()) {
			if (dialectPrefix.equals(Attribute.getPrefixFromAttributeName(attribute.getNormalizedName()))) {
				final String newVariableName = Attribute.getUnprefixedAttributeName(attribute.getOriginalName());
				final IStandardExpression expression = expressionParser.parseExpression(arguments.getConfiguration(),
						withExecutionArguments, attribute.getValue());
				final Object newVariableValue = expression.execute(configuration, withExecutionArguments);
				withExecutionArguments = withExecutionArguments.addLocalVariables(Collections.singletonMap(newVariableName,
						newVariableValue));
				newLocalVariables.put(newVariableName, newVariableValue);
				element.removeAttribute(attribute.getNormalizedName());
			}
		}
		return ProcessorResult.setLocalVariables(newLocalVariables);
	}
}
