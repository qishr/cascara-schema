package test.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.qishr.cascara.common.service.ServiceMetadata;
import io.github.qishr.cascara.common.service.ServiceProviderLayer;
import io.github.qishr.cascara.common.util.ContentTypeResolver;

public class SplOverrideTests extends SchemaTestBase {
    @Test
    void test_modulesLoad() throws IOException {
        ServiceProviderLayer rootLayer = ServiceProviderLayer.getRoot();
        List<String> modules = rootLayer.getModules();
        assertTrue(modules.contains("cascara.common.io"));
    }



    @Test
    void test_splPrefsOverridesSchemaStore() throws IOException {
        spl.registerClass(TestSchemaStore.class);

        // Verify Alternative CTS is present
        List<ServiceMetadata> providers = spl.findAllProviders(ContentTypeResolver.class);
        assertEquals(2, providers.size());

        boolean found = false;
        for (ServiceMetadata m : providers) {
            if (m.getType().equals(TestSchemaStore.class)) {
                found = true;
            }
        }

        assertTrue(found);
    }
}
