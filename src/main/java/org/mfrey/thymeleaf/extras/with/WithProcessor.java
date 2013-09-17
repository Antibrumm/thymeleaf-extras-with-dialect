package org.mfrey.thymeleaf.extras.with;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.processor.IProcessorMatcher;
import org.thymeleaf.processor.ProcessorMatchingContext;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

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
	protected ProcessorResult doProcess(Arguments arguments,
			ProcessorMatchingContext processorMatchingContext, Node node) {

		Element element = ((Element) node);
		Map<String, Attribute> attributeMap = element.getAttributeMap();
		String dialectPrefix = processorMatchingContext.getDialectPrefix();
		int prefixIndex = dialectPrefix.length() + 1;

		final Map<String, Object> newLocalVariables = new HashMap<String, Object>(
				1, 1.0f);

		for (Attribute attribute : attributeMap.values()) {
			if (dialectPrefix.equals(attribute.getNormalizedPrefix())) {
				final String attributeName = attribute.getOriginalName()
						.substring(prefixIndex);
				final String attributeValue = attribute.getValue();
				final Object result = StandardExpressionProcessor
						.processExpression(arguments, attributeValue);
				newLocalVariables.put(attributeName, result);
				element.removeAttribute(attribute.getNormalizedName());
			}
		}
		return ProcessorResult.setLocalVariables(newLocalVariables);
	}

}
