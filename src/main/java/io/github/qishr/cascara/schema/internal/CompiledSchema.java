package io.github.qishr.cascara.schema.internal;

import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.schema.Schema;
import io.github.qishr.cascara.schema.exception.ValidationException;
import io.github.qishr.cascara.schema.structure.*;
import io.github.qishr.cascara.schema.util.SchemaResolver;
import io.github.qishr.cascara.schema.util.SchemaValidator;
import io.github.qishr.cascara.schema.util.Schemas;

import java.net.URI;
import java.util.*;

public final class CompiledSchema implements Schema {

    private final SchemaNode root;
    private final URI originUri; // Store explicitly as the schema's identity
    private Map<String, SchemaNode> properties;
    private Map<String, SchemaNode> definitions;
    private SchemaResolver resolver = Schemas.getResolver();

    public CompiledSchema(URI originUri, SchemaNode root) {
        this.originUri = originUri;
        this.root = root;
    }

    public Collection<SchemaNode> getProperties() {
        return ensureProperties().values();
    }

    public Collection<SchemaNode> getDefinitions() {
        return ensureDefinitions().values();
    }

    public SchemaNode getRoot() {
        return root;
    }

    @Override
    public URI getOriginUri() {
        return originUri;
    }

    public URI getId() {
        return originUri;
    }

    @Override
    public URI getSchemaUri() {
        return URI.create("https://json-schema.org/draft/2020-12/schema");
    }

    /// Convenience method to find a definition by name.
    /// Returns null if the definition doesn't exist.
    public SchemaNode getDefinition(String name) {
        if (name == null) return null;
        return ensureDefinitions().get(name);
    }

    /// Convenience method to find a property by name.
    /// Only works if the root is an ObjectSchemaNode.
    public SchemaNode getProperty(String name) {
        if (name == null) return null;
        return ensureProperties().get(name);
    }

    /// Validates and AST against this schema.
    /// @param data an `AstNode` representing a document or part of a
    ///             structured document (such as JSON or YAML) to validate.
    /// @throws ValidationException if the AST is not valid.
    @Override
    public void validate(AstNode data) {
        SchemaValidator runner = new SchemaValidator(resolver);
        runner.validate(data, this);
    }

    /// Validates and AST against this schema.
    /// @param data an `AstNode` representing a document or part of a
    ///             structured document (such as JSON or YAML) to validate.
    /// @return true on success, false on failure.
    /// @param reporter a reporter for collecting problem diagnostics.
    @Override
    public boolean validate(AstNode data, Reporter reporter) {
        SchemaValidator validator = new SchemaValidator(resolver);
        validator.setReporter(reporter);
        return validator.validate(data, this);
    }

    @Override
    public Schema setResolver(SchemaResolver resolver) {
        this.resolver = resolver;
        return this;
    }

    //
    //
    //

    private Map<String, SchemaNode> ensureProperties() {
        if (properties == null) {
            properties = new LinkedHashMap<>();
            if (root instanceof ObjectSchemaNode obj) {
                properties.putAll(obj.getProperties());
            }
        }
        return properties;
    }

    private Map<String, SchemaNode> ensureDefinitions() {
        if (definitions == null) {
            definitions = new LinkedHashMap<>();
            if (root != null) {
                definitions.putAll(root.getDefinitions());
            }
        }
        return definitions;
    }
}