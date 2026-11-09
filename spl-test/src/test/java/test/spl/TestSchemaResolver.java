package test.spl;

import java.net.URI;
import java.util.List;
import java.util.Map;

import io.github.qishr.cascara.common.annotation.NoAutoRegistration;
import io.github.qishr.cascara.common.annotation.Priority;
import io.github.qishr.cascara.schema.diagnostic.SchemaException;
import io.github.qishr.cascara.schema.structure.SchemaNode;
import io.github.qishr.cascara.schema.util.DynamicScope;
import io.github.qishr.cascara.schema.util.Schema;
import io.github.qishr.cascara.schema.util.SchemaResolver;
import io.github.qishr.cascara.schema.util.TypeAnalyzer;

@NoAutoRegistration
@Priority(Priority.LOW)
public class TestSchemaResolver implements SchemaResolver {

    @Override
    public Schema getSchema(URI uri) throws SchemaException {
        return null;
    }

    @Override
    public SchemaNode resolve(String ref, SchemaNode relativeTo) throws SchemaException {
        return null;
    }

    @Override
    public Schema getSchemaForClass(Class<?> clazz) {
        return null;
    }

    @Override
    public Schema getSchemaForClass(Class<?> clazz, List<TypeAnalyzer> typeAnalyzers) {
        return null;
    }

    @Override
    public SchemaNode resolve(String ref, SchemaNode relativeTo, DynamicScope scope) throws SchemaException {
        return null;
    }

    @Override
    public DynamicScope getCurrentScope() {
        return null;
    }

    @Override
    public void registerSchema(URI uri, Schema compiled) {
    }

    @Override
    public void registerSchemaNode(URI uri, SchemaNode node) {
    }

    @Override
    public Map<URI, Schema> getCachedSchemas() {
        return null;
    }

}
