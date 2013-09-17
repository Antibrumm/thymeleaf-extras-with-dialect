package org.antibrumm.thymeleaf.extras.with;

import java.util.Collection;
import java.util.Map;

import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.IProcessorMatcher;
import org.thymeleaf.processor.ProcessorMatchingContext;

public class WithMatcher implements IProcessorMatcher<Element> {

	public boolean matches(Node node, ProcessorMatchingContext context) {
		Element element = (Element) node;

		if (context.getDialect() instanceof WithDialect) {
			String dialectPrefix = context.getDialectPrefix();
			Map<String, Attribute> attributeMap = element.getAttributeMap();
			Collection<Attribute> values = attributeMap.values();
			for (Attribute attribute : values) {
				if (dialectPrefix.equals(attribute.getNormalizedPrefix())) {
					return true;
				}
			}

		}
		return false;

	}

	public Class<? extends Element> appliesTo() {
		return Element.class;
	}
}