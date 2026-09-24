package test.schema;

import java.util.List;

import io.github.qishr.cascara.common.util.ContentType;
import io.github.qishr.cascara.common.util.ContentTypeResolver;

// TODO: Make SchemaStore a neo-singleton and test with its interface instead of this...
public class TestSchemaStore implements ContentTypeResolver {

    @Override
    public ContentType resolve(String type) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'resolve'");
    }

    @Override
    public void add(ContentType contentType) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'add'");
    }

    @Override
    public void addAll(List<? extends ContentType> contentTypes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addAll'");
    }

}
