package io.github.qishr.cascara.schema.rule;

import java.util.regex.Pattern;

import io.github.qishr.cascara.common.diagnostic.LocatableException;
import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.common.lang.ast.ScalarAstNode;
import io.github.qishr.cascara.schema.exception.SchemaDiagnosticCode;

public class RegexRule extends AbstractValidationRule implements ValidationRule {
    private final Pattern pattern;
    private final String patternString;

    public RegexRule(String pattern) {
        this.patternString = pattern;
        this.pattern = Pattern.compile(pattern);
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
            String strValue = String.valueOf(value);
            if (!pattern.matcher(strValue).matches()) {
                error(path, line, column, reporter, SchemaDiagnosticCode.DOES_NOT_MATCH_PATTERN, patternString);
                return false;
            }
        }
        return true;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String getPatternString() {
        return patternString;
    }
}