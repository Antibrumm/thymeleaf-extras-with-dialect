package ch.mfrey.thymeleaf.extras.with;

import java.util.List;

import org.attoparser.util.TextUtil;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IElementAttributes;
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

    public static final int PRECEDENCE = StandardWithTagProcessor.PRECEDENCE;

    private final String dialectPrefix;
    private final MatchingAttributeName matchingAttributeName;

    public WithProcessor(final IProcessorDialect dialect, final TemplateMode templateMode, final String dialectPrefix) {
        super(dialect, templateMode, PRECEDENCE);
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
        final IElementAttributes attributes = tag.getAttributes();
        // Currently we need to work on the complete attribute name (with prefix) as we loose case sensitivity.
        final List<String> completeNames = attributes.getAllCompleteNames();

        // Normally we would just allow the structure handler to be in charge of declaring the local variables
        // by using structureHandler.setLocalVariable(...) but in this case we want each variable defined at an
        // expression to be available for the next expressions, and that forces us to cast our ITemplateContext into
        // a more specific interface --which shouldn't be used directly except in this specific, special case-- and
        // put the local variables directly into it.
        IEngineContext engineContext = null;
        if (context instanceof IEngineContext) {
            // NOTE this interface is internal and should not be used in users' code
            engineContext = (IEngineContext) context;
        }
        for (final String completeName : completeNames) {
            AttributeName attributeName = attributes.getAttributeDefinition(completeName).getAttributeName();
            if (attributeName.isPrefixed() && TextUtil.equals(templateMode.isCaseSensitive(), attributeName.getPrefix(), this.dialectPrefix)) {
                processWithAttribute(context, engineContext, tag, completeName, attributeName, structureHandler);
            }

        }

    }

    private void processWithAttribute(
            final ITemplateContext context, final IEngineContext engineContext,
            final IProcessableElementTag tag, final String completeName, final AttributeName attributeName, IElementTagStructureHandler structureHandler) {
        try {

            final String attributeValue = EscapedAttributeUtils.unescapeAttribute(
                    context.getTemplateMode(), tag.getAttributes().getValue(attributeName));

            /*
             * Compute the new attribute name (case sensitive)
             */
            final String newVariableName;
            if (completeName.contains(":")) {
                // prefix version: 'with:varName'
                newVariableName = completeName.substring(this.dialectPrefix.length() + 1);
            } else {
                // data version 'data-with-varName'
                newVariableName = completeName.substring(this.dialectPrefix.length() + 6);
            }

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

            if (engineContext != null) {
                // The advantage of this vs. using the structure handler is that we will be able to
                // use this newly created value in other expressions in the same 'th:with'
                engineContext.setVariable(newVariableName, expressionResult);
            } else {
                // The problem is, these won't be available until we execute the next processor
                structureHandler.setLocalVariable(newVariableName, expressionResult);
            }

            tag.getAttributes().removeAttribute(attributeName);
        } catch (final TemplateProcessingException e) {
            // This is a nice moment to check whether the execution raised an error and, if so, add location information
            // Note this is similar to what is done at the superclass AbstractElementTagProcessor, but we can be more
            // specific because we know exactly what attribute was being executed and caused the error
            if (!e.hasTemplateName()) {
                e.setTemplateName(tag.getTemplateName());
            }
            if (!e.hasLineAndCol()) {
                e.setLineAndCol(tag.getAttributes().getLine(completeName), tag.getAttributes().getCol(completeName));
            }
            throw e;
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Error during execution of processor '" + WithProcessor.class.getName() + "'",
                    tag.getTemplateName(), tag.getAttributes().getLine(completeName), tag.getAttributes().getCol(completeName), e);
        }
    }

}
