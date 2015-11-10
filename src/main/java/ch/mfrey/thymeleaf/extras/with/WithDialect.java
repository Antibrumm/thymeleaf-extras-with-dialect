package ch.mfrey.thymeleaf.extras.with;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.templatemode.TemplateMode;

public class WithDialect extends AbstractProcessorDialect {

    public WithDialect() {
        super(DIALECT_NAMESPACE, DIALECT_PREFIX, 0);
    }

    public static final String DIALECT_NAMESPACE = "http://www.thymeleaf.org/extras/with";

    static final String DIALECT_PREFIX = "with";

    public Set<IProcessor> getProcessors(String arg0) {
        HashSet<IProcessor> processors = new HashSet<IProcessor>();
        processors.add(new WithProcessor(this, TemplateMode.HTML, getPrefix()));
        return processors;
    }

}
