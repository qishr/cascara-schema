package io.github.qishr.cascara.schema.structure;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.common.lang.ast.MapAstNode;
import io.github.qishr.cascara.common.lang.type.PrimitiveType;
import io.github.qishr.cascara.schema.exception.SchemaDiagnosticCode;
import io.github.qishr.cascara.schema.exception.ValidationException;

public class ObjectSchemaNode extends AbstractSchemaNode {
    private final Map<String, SchemaNode> properties = new LinkedHashMap<>();

    private boolean additionalPropertiesAllowed = true;
    private SchemaNode additionalPropertiesSchema;

    private boolean unevaluatedPropertiesAllowed = true;
    private SchemaNode unevaluatedPropertiesSchema;

    public ObjectSchemaNode(SchemaNode metaSchema) {
        super(PrimitiveType.OBJECT, metaSchema);
    }

    public void addProperty(String name, SchemaNode node) {
        this.properties.put(name, node);
    }

    @Override
    public Map<String, SchemaNode> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public SchemaNode getItemSchema() { return null; }

    @Override
    public boolean validate(AstNode node, String path, Reporter reporter) {
        boolean valid = super.validate(node, path, reporter);

        if (node instanceof MapAstNode mapNode) {
            for (Entry<String, SchemaNode> entry : properties.entrySet()) {
                String key = entry.getKey();
                SchemaNode childSchema = entry.getValue();
                // Use the Map interface's 'get' for cleaner lookups
                AstNode dataNode = mapNode.get(key);
                String childPath = path.isEmpty() ? key : path + "/" + key;

                if (dataNode != null) {
                    childSchema.validate(dataNode, childPath, reporter);
                } else {
                    error(childPath, node, reporter, null, SchemaDiagnosticCode.MISSING_REQUIRED_PROPERTY, key);
                    valid = false;
                }
            }
        }

        return valid;
    }

    @Override
    public SchemaNode getPropertySchema(String key) {
        // BaseSchemaNode now handles the recursive allOf search
        return super.getPropertySchema(key);
    }

    public void setAdditionalPropertiesSchema(SchemaNode schema) {
        this.additionalPropertiesSchema = schema;
        // If a schema is set, it implies additional properties are allowed
        this.additionalPropertiesAllowed = (schema != null);
    }

    public SchemaNode getAdditionalPropertiesSchema() {
        return additionalPropertiesSchema;
    }

    public void setUnevaluatedPropertiesSchema(SchemaNode schema) {
        this.unevaluatedPropertiesSchema = schema;
        this.unevaluatedPropertiesAllowed = (schema != null);
    }

    public SchemaNode getUnevaluatedPropertiesSchema() {
        return unevaluatedPropertiesSchema;
    }

    public void setUnevaluatedPropertiesAllowed(boolean allowed) { this.unevaluatedPropertiesAllowed = allowed; }
    public boolean areUnevaluatedPropertiesAllowed() { return unevaluatedPropertiesAllowed; }
    public void setAdditionalPropertiesAllowed(boolean b) { additionalPropertiesAllowed = b; }
    @Override
    public boolean areAdditionalPropertiesAllowed() { return additionalPropertiesAllowed; }

    //
    //
    //

    private void error(String fragmentPath, AstNode node, Reporter reporter, Throwable cause, SchemaDiagnosticCode code, Object... details) {
        if (reporter == null || !reporter.collectsProblems()) {
            throw new ValidationException(fragmentPath, node, cause, code, details);
        }

        // AstNode should not contain a URI, but for reporting we need a URI,
        // or at least the fragment (from # onwards).
        //
        // TODO: errorAt method that takes URI and AstNode
        reporter.errorAt(node.getStartLine(), node.getStartColumn(), cause, code, details);
    }
}