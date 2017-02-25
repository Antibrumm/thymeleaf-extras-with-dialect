package ch.mfrey.thymeleaf.extras.with;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

public class WithDialect extends AbstractProcessorDialect {

    public static final String DIALECT_NAMESPACE = "http://www.thymeleaf.org/extras/with";

    public static final String DIALECT_PREFIX = "with";

    public static final int PROCESSOR_PRECEDENCE = StandardDialect.PROCESSOR_PRECEDENCE;

    public WithDialect() {
        super(DIALECT_NAMESPACE, DIALECT_PREFIX, PROCESSOR_PRECEDENCE);
    }

    public Set<IProcessor> getProcessors(String dialectPrefix) {
        HashSet<IProcessor> processors = new HashSet<IProcessor>();
        processors.add(new StandardXmlNsTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new WithProcessor(TemplateMode.HTML, dialectPrefix));
        return processors;
    }

}
