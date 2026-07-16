package io.github.qishr.cascara.schema.rule;


import io.github.qishr.cascara.common.diagnostic.LocatableException;
import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.common.lang.ast.ScalarAstNode;
import io.github.qishr.cascara.common.lang.type.ScalarDescriptor;
import io.github.qishr.cascara.common.lang.type.TypeDescriptorFactory;

public class FormatRule extends AbstractValidationRule implements ValidationRule {
    // TODO: Configurable ServiceProviderLayer...
    private static final TypeDescriptorFactory FACTORY = new TypeDescriptorFactory();

    private final String format;
    private ScalarDescriptor<?> scalarDescriptor;
    private boolean askedFactory;

    public FormatRule(String format) {
        this.format = format;
    }

    public FormatRule(ScalarDescriptor<?> scalarDescriptor) {
        this.format = scalarDescriptor.getFormat();
        this.scalarDescriptor = scalarDescriptor;
    }

    @Override
    public boolean validate(AstNode node, String path, Reporter reporter) {
        if (node instanceof ScalarAstNode scalar) {
            return validateValue(scalar.getPrimitive(), path, node.getStartLine(), node.getStartColumn(), reporter);
        }
        return true;
    }

    @Override
    public boolean validateValue(Object value, String path, Reporter reporter) {
        return validateValue(value, path, LocatableException.UNKNOWN_COORD, LocatableException.UNKNOWN_COORD, reporter);
    }

    private boolean validateValue(Object value, String path, int line, int column, Reporter reporter) {
        if (value != null) {
            ScalarDescriptor<?> descriptor = getScalarDescriptor();
            if (descriptor != null) {
                String strValue = String.valueOf(value);
                return descriptor.validate(strValue, reporter);
            } else {
                // TODO: Handle this elegantly. Warning or Error?
                System.out.println("Format with no type descriptor: " + format);
            }
        }
        return true;
    }

    public ScalarDescriptor<?> getScalarDescriptor() {
        if (scalarDescriptor == null && !askedFactory) {
            scalarDescriptor = FACTORY.createScalarDescriptor(format);
            askedFactory = true;
        }
        return scalarDescriptor;
    }

    public String getFormat() {
        return format;
    }
}