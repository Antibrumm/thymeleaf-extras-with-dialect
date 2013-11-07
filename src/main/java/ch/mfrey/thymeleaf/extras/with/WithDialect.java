package ch.mfrey.thymeleaf.extras.with;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

public class WithDialect extends AbstractDialect {
	public static final String LAYOUT_NAMESPACE = "http://www.thymeleaf.org/extras/with";

	static final String DIALECT_PREFIX = "with";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<IProcessor> getProcessors() {
		HashSet<IProcessor> processors = new HashSet<IProcessor>();
		processors.add(new WithProcessor());
		return processors;
	}

	public String getPrefix() {
		return DIALECT_PREFIX;
	}

	public boolean isLenient() {
		return false;
	}

}
