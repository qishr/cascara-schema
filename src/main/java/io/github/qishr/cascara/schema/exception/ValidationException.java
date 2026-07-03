package io.github.qishr.cascara.schema.exception;

import io.github.qishr.cascara.common.diagnostic.code.DiagnosticCode;
import io.github.qishr.cascara.common.lang.ast.AstNode;

public class ValidationException extends SchemaException {

    // TODO: Retain line, column, node, etc

	public ValidationException(String schemaPath, int line, int column, DiagnosticCode code, Object[] details) {
		super(null, schemaPath, code, details);
		//TODO Auto-generated constructor stub
	}

	public ValidationException(String schemaPath, AstNode node, DiagnosticCode code, Object[] details) {
		super(null, schemaPath, code, details);
		//TODO Auto-generated constructor stub
	}

	public ValidationException(String schemaPath, AstNode node, Throwable cause, DiagnosticCode code, Object[] details) {
		super(null, schemaPath, cause, code, details);
		//TODO Auto-generated constructor stub
	}

}
