package io.github.qishr.cascara.schema.rule;

import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.lang.ast.AstNode;

// TODO: Does this not belong elsewhere?
public class FileExtensionRule extends AbstractValidationRule implements ValidationRule {
    private final String[] extensions;

    public FileExtensionRule(String[] extensions) {
        this.extensions = extensions;
    }

    public String[] getExtensions() {
        return extensions;
    }

    @Override
    public boolean validate(AstNode node, String path, Reporter reporter) {
        // Optional: Implement actual validation logic here if desired
        return true;
    }
}