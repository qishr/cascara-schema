package io.github.qishr.cascara.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;

import org.junit.jupiter.api.Test;

import io.github.qishr.cascara.common.diagnostic.LocalizableIOException;
import io.github.qishr.cascara.common.io.IOUtils;

public class UriTests {
    @Test
    void test_absoluteFilePath() throws LocalizableIOException {
        assertEquals(
            "file:///tmp/a",
            IOUtils.normalizeUri(URI.create(
                "/tmp/a"
            )).toString()
        );
    }

    @Test
    void test_relativeFilePath() throws LocalizableIOException {
        assertEquals(
            "file://" + System.getProperty("user.dir") + "/tmp/a",
            IOUtils.normalizeUri(URI.create(
                "tmp/a"
            )).toString()
        );
    }
}
