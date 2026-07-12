package io.github.qishr.cascara.schema;

import java.net.URI;
import java.util.Collection;

import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.schema.exception.ValidationException;
import io.github.qishr.cascara.schema.structure.SchemaNode;
import io.github.qishr.cascara.schema.util.SchemaResolver;

public interface Schema {

    Collection<SchemaNode> getProperties();
    Collection<SchemaNode> getDefinitions();
    SchemaNode getRoot();
    URI getOriginUri();
    URI getId();
    URI getSchemaUri();
    SchemaNode getDefinition(String name);
    SchemaNode getProperty(String name);

    /// Validates an AST against this schema.
    /// @param data an `AstNode` representing a document or part of a
    ///             structured document (such as JSON or YAML) to validate.
    /// @throws ValidationException if the AST is not valid.
    void validate(AstNode root);

    /// Validates an AST against this schema.
    /// @param data an `AstNode` representing a document or part of a
    ///             structured document (such as JSON or YAML) to validate.
    /// @return true on success, false on failure.
    /// @param reporter a reporter for collecting problem diagnostics.
    boolean validate(AstNode root, Reporter reporter);

    Schema setResolver(SchemaResolver resolver);

}
