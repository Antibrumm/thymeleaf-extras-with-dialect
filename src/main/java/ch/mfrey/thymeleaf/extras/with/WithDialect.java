package ch.mfrey.thymeleaf.extras.with;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

public class WithDialect extends AbstractProcessorDialect {

    public static final String DIALECT_NAMESPACE = "http://www.thymeleaf.org/extras/with";

    public static final String DIALECT_PREFIX = "with";

    public WithDialect() {
        super(DIALECT_NAMESPACE, DIALECT_PREFIX, 0);
    }

    public Set<IProcessor> getProcessors(String dialectPrefix) {
        HashSet<IProcessor> processors = new HashSet<IProcessor>();
        processors.add(new StandardXmlNsTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new WithProcessor(TemplateMode.HTML, dialectPrefix));
        return processors;
    }

}
