// # License & Terms
//
// This file is part of **Cascara**.
//
// **Cascara** is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <https://www.gnu.org/licenses/>.
//
// ---
//
// ## Special Runtime Exception
//
// As a special exception, the copyright holders of this library give you
// permission to link this library with independent modules to produce an
// executable, regardless of the license terms of these independent modules,
// and to copy and distribute the resulting executable under terms of your
// choice, provided that you also meet, for each linked independent module,
// the terms and conditions of the license of that module.
//
// An independent module is a module which is not derived from or based on
// this library. If you modify this library, you may extend this exception
// to your version of the library, but you are not obligated to do so. If
// you do not wish to do so, delete this exception statement from your
// version.

package test.spl;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Map.Entry;

import io.github.qishr.cascara.common.service.SPL;
import io.github.qishr.cascara.logging.log4j.Log4jLogger;
import io.github.qishr.cascara.common.annotation.SchemaDefinition;
import io.github.qishr.cascara.common.annotation.SchemaProperty;
import io.github.qishr.cascara.common.diagnostic.Diagnostic.Level;
import io.github.qishr.cascara.common.diagnostic.log.ConsoleLogger;
import io.github.qishr.cascara.common.diagnostic.report.GlobalReporter;
import io.github.qishr.cascara.common.diagnostic.log.TimeSequencedAggregatorLogger;
import io.github.qishr.cascara.schema.util.Schema;
import io.github.qishr.cascara.schema.util.SchemaResolver;
import io.github.qishr.cascara.test.common.junit.util.VfsTestBase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class SchemaTestBase extends VfsTestBase {

    @SchemaDefinition
    public static class TestClass {
        @SchemaProperty
        private LocalDateTime dateTime;
    }

    @SchemaDefinition
    public static class OuterTestClass {
        @SchemaProperty
        private TestClass inner;
    }

    protected SchemaResolver resolver;

    @BeforeAll
    static void init() {
        // TODO: Make this configurable at *TestBase level
        // Configurator.setLevel("integration.test", org.apache.logging.log4j.Level.DEBUG);
        // ConsoleLogger logger = new ConsoleLogger();
        // TimeSequencedAggregatorLogger aggregator = new TimeSequencedAggregatorLogger(logger, 800);
        // GlobalReporter.globalInstance().addLogger(aggregator);
        // GlobalReporter.globalInstance().setSystemOutputEnabled(false);
    }

    @BeforeEach
    protected void setUp() throws IOException {
        super.setUp();
        reporter.setLevel(Level.INFO);
        resolver = SPL.load(SchemaResolver.class);
    }

    @AfterEach
    protected void tearDown() throws IOException {
        super.tearDown();
    }

    protected void listCachedSchemas() {
        Map<URI, Schema> schemas = resolver.getCachedSchemas();
        for (Entry<URI, Schema> entry : schemas.entrySet()) {
            System.out.println(entry.getKey());
        }
    }
}