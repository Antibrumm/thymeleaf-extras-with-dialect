package ch.mfrey.thymeleaf.extras.with.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.ExpressionContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import ch.mfrey.thymeleaf.extras.with.WithDialect;

public class WithDialectTest {

    private TemplateEngine templateEngine;

    @Before
    public void setUpTemplateEngine() {
        ClassLoaderTemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
        classLoaderTemplateResolver.setCacheable(false);
        classLoaderTemplateResolver.setTemplateMode(TemplateMode.HTML);
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(classLoaderTemplateResolver);
        templateEngine.addDialect(new WithDialect());
    }

    @Test
    public void testWith() {
        String result = templateEngine.process("templates/withTest.html", new ExpressionContext(templateEngine.getConfiguration()));
        Assert.assertTrue(!result.contains("with:"));
        Assert.assertTrue(result.contains("Hello Under World"));
        Assert.assertTrue(result.contains("21"));
        Assert.assertTrue(result.contains("Data Works"));
    }
}
