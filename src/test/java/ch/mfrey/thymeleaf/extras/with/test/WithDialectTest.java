package ch.mfrey.thymeleaf.extras.with.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        ExpressionContext expressionContext = new ExpressionContext(templateEngine.getConfiguration());
        String expected = templateEngine.process("templates/expected.html",
                expressionContext);

        String result = templateEngine.process("templates/withTest.html",
                expressionContext);
        Assert.assertEquals(expected, result);
    }

    public static class A {
        private String label;
        private List<A> children = new ArrayList<A>();

        public A(String label) {
            this.label = label;
        }

        public List<A> getChildren() {
            return children;
        }

        public void setChildren(List<A> children) {
            this.children = children;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("A [label=").append(label).append(", children=").append(children).append("]");
            return builder.toString();
        }

    }

    private static final Logger log = LoggerFactory.getLogger(WithDialectTest.class);

    @Test
    public void testRecursion() {
        A a = new A("Top");
        for (int i = 1; i < 3; i++) {
            A a2 = new A(String.valueOf(i));
            a.getChildren().add(a2);
            for (int j = 1; j < 4; j++) {
                a2.getChildren().add(new A(String.valueOf(i * 10 + j)));
            }
        }

        ExpressionContext expressionContext = new ExpressionContext(templateEngine.getConfiguration());
        expressionContext.setVariable("top", a);
        String expected = templateEngine.process("templates/recursionParam.html",
                expressionContext);
        log.info("{}", expected);

        String with = templateEngine.process("templates/recursionWith.html",
                expressionContext);
        log.info("{}", with);
        Assert.assertEquals(expected, with);

        String with2 = templateEngine.process("templates/recursionWith2.html",
                expressionContext);
        log.info("{}", with2);
        Assert.assertEquals(expected, with2);
    }

    @Test
    public void testEach() {
        A a = new A("Top");
        for (int i = 1; i < 3; i++) {
            A a2 = new A(String.valueOf(i));
            a.getChildren().add(a2);
        }
        ExpressionContext expressionContext = new ExpressionContext(templateEngine.getConfiguration());
        expressionContext.setVariable("top", a);
        String expected = templateEngine.process("templates/eachStd.html",
                expressionContext);
        log.info("{}", expected);

        String with = templateEngine.process("templates/eachWith.html",
                expressionContext);
        log.info("{}", with);
        Assert.assertEquals(expected, with);
        
        String with2 = templateEngine.process("templates/eachWith2.html",
                expressionContext);
        log.info("{}", with);
        Assert.assertEquals(expected, with2);
    }
}
