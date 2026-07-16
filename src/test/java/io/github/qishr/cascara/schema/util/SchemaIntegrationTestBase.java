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


package io.github.qishr.cascara.schema.util;

import org.junit.jupiter.api.BeforeEach;


public class SchemaIntegrationTestBase {
    // protected ContentLoader mockLoader;
    // protected SchemaParser JsonAstParser;
    protected SchemaResolver resolver;

    @BeforeEach
    void setup() {
        // mockLoader = mock(ContentLoader.class);

        // Bridge: extract the 'content' string from the record to feed the JsonAstParser
        // JsonAstParser = resource -> new JsonAstParser().parse(resource.content());

        // System Under Test
        resolver = new SchemaResolver();
    }

    // protected void mockRemoteFile(String uri, String contentStr) throws IOException {
    //     URI targetUri = URI.create(uri);

    //     // Match the record: (String content, ContentType contentType)
    //     // We pass null for ContentType unless the test explicitly validates it
    //     ResourceContent resource = new ResourceContent(contentStr, null);

    //     // Match the interface: getContent(URI)
    //     when(mockLoader.getContent(eq(targetUri))).thenReturn(resource);
    // }

    // protected void mockRemoteFile(String uri, String contentStr) throws IOException {
    //     // Match by the string representation of the URI to avoid instance-equality issues
    //     when(mockLoader.getContent(org.mockito.ArgumentMatchers.argThat(u ->
    //         u != null && u.toString().equals(uri)
    //     ))).thenReturn(new ResourceContent(contentStr, null));
    // }
}