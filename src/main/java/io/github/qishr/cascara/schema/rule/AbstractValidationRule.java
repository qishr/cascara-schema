package io.github.qishr.cascara.schema.rule;

import java.net.URI;

import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.schema.exception.SchemaDiagnosticCode;
import io.github.qishr.cascara.schema.exception.ValidationException;

public abstract class AbstractValidationRule {

    protected AbstractValidationRule() {
        // Nothing to see here
    }

    protected void error(String fragmentPath, AstNode node, Reporter reporter, SchemaDiagnosticCode code, Object... details) {
        if (reporter == null || !reporter.collectsProblems()) {
            throw new ValidationException(fragmentPath, node, code, details);
        }
        URI uri = URI.create(formatFragment(fragmentPath));
        reporter.errorAt(uri, node.getStartLine(), node.getStartColumn(), code, details);
    }

    protected void error(String fragmentPath, int line, int column, Reporter reporter, SchemaDiagnosticCode code, Object... details) {
        if (reporter == null || !reporter.collectsProblems()) {
            throw new ValidationException(fragmentPath, line, column, code, details);
        }
        URI uri = URI.create(formatFragment(fragmentPath));
        reporter.errorAt(uri, line, column, code, details);
    }

    private String formatFragment(String path) {
        String fragment = path == null ? "" : path.startsWith("#/") ? path : path.startsWith("/") ? "#" + path : "#/" + path;
        return fragment;
    }
}
