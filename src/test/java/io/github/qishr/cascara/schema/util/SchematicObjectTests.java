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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;

import org.junit.jupiter.api.Test;

import io.github.qishr.cascara.common.property.Property;
import io.github.qishr.cascara.common.property.StringProperty;
import io.github.qishr.cascara.schema.annotation.SchemaDefinition;
import io.github.qishr.cascara.schema.annotation.SchemaProperty;
import io.github.qishr.cascara.common.trackable.property.TrackableProperty;

public class SchematicObjectTests {

    @SchemaDefinition
    public static class TestObject1 extends SchematicObject {
        @SchemaProperty
        TrackableProperty<String> s1;
    }

    @Test
    void test() {
        TestObject1 o1 = new TestObject1();
        o1.set("s1", "Hello");
        assertEquals("Hello", o1.get("s1"));
    }

    //
    //
    //

    @SchemaDefinition
    public static class TestObject2 extends SchematicObject {
        @SchemaProperty
        StringProperty s1;
    }

    @Test
    void test2() {
        TestObject2 o = new TestObject2();
        o.set("s1", "Hello");
        assertEquals("Hello", o.get("s1"));
    }

    //
    //
    //

    @SchemaDefinition
    public static class TestObject3 extends SchematicObject {
        @SchemaProperty
        Property<String> s1;
    }

    @Test
    void test3() {
        TestObject3 o = new TestObject3();
        o.set("s1", "Hello");
        assertEquals("Hello", o.get("s1"));
    }

    //
    //
    //

    @SchemaDefinition
    public static class TestObject4 extends SchematicObject {
        @SchemaProperty
        Property<URI> s1;
    }

    @Test
    void test4() {
        URI uri = URI.create("https://github.com");
        TestObject4 o = new TestObject4();
        o.set("s1", uri);
        assertEquals(uri, o.get("s1"));
    }
}
