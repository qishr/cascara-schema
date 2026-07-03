package io.github.qishr.cascara.schema;

import java.net.URI;
import java.util.Collection;

import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.lang.ast.AstNode;
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

    void validate(AstNode root);
    boolean validate(AstNode root, Reporter reporter);

    Schema setResolver(SchemaResolver resolver);

}
