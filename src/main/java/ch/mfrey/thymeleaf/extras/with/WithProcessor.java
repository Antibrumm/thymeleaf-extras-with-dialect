package ch.mfrey.thymeleaf.extras.with;

import org.attoparser.util.TextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.processor.StandardWithTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EscapedAttributeUtils;

public class WithProcessor extends AbstractProcessor implements IElementTagProcessor {

    private static final Logger log = LoggerFactory.getLogger(WithProcessor.class);

    public static final int PRECEDENCE = StandardWithTagProcessor.PRECEDENCE;

    private final String dialectPrefix;
    private final MatchingAttributeName matchingAttributeName;

    public WithProcessor(final TemplateMode templateMode, final String dialectPrefix) {
        super(templateMode, PRECEDENCE);
        this.dialectPrefix = dialectPrefix;
        this.matchingAttributeName = MatchingAttributeName.forAllAttributesWithPrefix(getTemplateMode(), dialectPrefix);
    }

    public final MatchingAttributeName getMatchingAttributeName() {
        return this.matchingAttributeName;
    }

    public final MatchingElementName getMatchingElementName() {
        return null;
    }

    public void process(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final IElementTagStructureHandler structureHandler) {

        final TemplateMode templateMode = context.getTemplateMode();
        final IAttribute[] attributes = tag.getAllAttributes();

        // Normally we would just allow the structure handler to be in charge of declaring the local variables
        // by using structureHandler.setLocalVariable(...) but in this case we want each variable defined at an
        // expression to be available for the next expressions, and that forces us to cast our ITemplateContext into
        // a more specific interface --which shouldn't be used directly except in this specific, special case-- and
        // put the local variables directly into it.
        final IEngineContext engineContext;
        if (context instanceof IEngineContext) {
            // NOTE this interface is internal and should not be used in users' code
            engineContext = (IEngineContext) context;
        } else {
            engineContext = null;
        }

        for (IAttribute attribute : attributes) {
            // this matching works are these lists are correlated
            final AttributeName attributeName = attribute.getDefinition().getAttributeName();
            final String completeName = attribute.getCompleteName();
            /*
             * Compute the new attribute name (case sensitive). As the length of the matched attributename (in
             * lowercase) is the same as the variablename without the prefix we can just do a substring.
             */
            final String newVariableName = completeName.substring(completeName.length() - attributeName.getAttributeName().length());

            if (attributeName.isPrefixed() && TextUtil.equals(templateMode.isCaseSensitive(), attributeName.getPrefix(), this.dialectPrefix)) {
                processWithAttribute(context, engineContext, tag, attribute, attributeName, newVariableName, structureHandler);
            }

        }

    }

    private void processWithAttribute(
            final ITemplateContext context, final IEngineContext engineContext,
            final IProcessableElementTag tag, final IAttribute attribute, final AttributeName attributeName, final String newVariableName, IElementTagStructureHandler structureHandler) {
        try {

            final String attributeValue = EscapedAttributeUtils.unescapeAttribute(
                    context.getTemplateMode(), attribute.getValue());

            /*
             * Obtain the parser
             */
            final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());

            /*
             * Execute the expression, handling nulls in a way consistent with the rest of the Standard Dialect
             */
            final Object expressionResult;
            if (attributeValue != null) {
                final IStandardExpression expression = expressionParser.parseExpression(context, attributeValue);
                expressionResult = expression.execute(context);
            } else {
                expressionResult = null;
            }

            log.debug("Setting Variable: {}={}", newVariableName, expressionResult);
         
            if (engineContext != null) {
                // The advantage of this vs. using the structure handler is that we will be able to
                // use this newly created value in other expressions in the same 'th:with'
                engineContext.setVariable(newVariableName, expressionResult);
            } else {
                // The problem is, these won't be available until we execute the next processor
                structureHandler.setLocalVariable(newVariableName, expressionResult);
            }

            structureHandler.removeAttribute(attributeName);
        } catch (final TemplateProcessingException e) {
            // This is a nice moment to check whether the execution raised an error and, if so, add location information
            // Note this is similar to what is done at the superclass AbstractElementTagProcessor, but we can be more
            // specific because we know exactly what attribute was being executed and caused the error
            if (!e.hasTemplateName()) {
                e.setTemplateName(tag.getTemplateName());
            }
            if (!e.hasLineAndCol()) {
                e.setLineAndCol(attribute.getLine(), attribute.getCol());
            }
            throw e;
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Error during execution of processor '" + WithProcessor.class.getName() + "'",
                    tag.getTemplateName(), attribute.getLine(), attribute.getCol(), e);
        }
    }

}
