package ch.mfrey.thymeleaf.extras.with;

import java.util.Collection;
import java.util.Map;

import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.NestableAttributeHolderNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.IProcessorMatcher;
import org.thymeleaf.processor.ProcessorMatchingContext;

public class WithMatcher implements IProcessorMatcher<NestableAttributeHolderNode> {

	public boolean matches(Node node, ProcessorMatchingContext context) {
		NestableAttributeHolderNode element = (NestableAttributeHolderNode) node;

		if (context.getDialect() instanceof WithDialect) {
			String dialectPrefix = context.getDialectPrefix();
			Map<String, Attribute> attributeMap = element.getAttributeMap();
			Collection<Attribute> values = attributeMap.values();
			for (Attribute attribute : values) {
				if (dialectPrefix.equals(Attribute.getPrefixFromAttributeName(attribute.getNormalizedName()))) {
					return true;
				}
			}
		}
		return false;
	}

	public Class<? extends NestableAttributeHolderNode> appliesTo() {
		return NestableAttributeHolderNode.class;
	}
}