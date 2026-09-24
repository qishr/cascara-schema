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


package io.github.qishr.cascara.schema.internal;

import io.github.qishr.cascara.common.lang.type.PrimitiveType;
import io.github.qishr.cascara.schema.structure.SchemaNode;

public final class SchemaNodeDumper {

    public static String dump(SchemaNode node) {
        StringBuilder sb = new StringBuilder();
        dump(node, sb, 0);
        return sb.toString();
    }

    private static void dump(SchemaNode node, StringBuilder sb, int indent) {
        if (node == null) {
            indent(sb, indent).append("null\n");
            return;
        }

        indent(sb, indent).append(node.getClass().getSimpleName()).append(" {\n");

        // Type
        indent(sb, indent + 1).append("type: ").append(node.getType()).append("\n");

        // Object properties
        if (node.getType() == PrimitiveType.OBJECT) {
            indent(sb, indent + 1).append("properties:\n");
            for (var e : node.getProperties().entrySet()) {
                indent(sb, indent + 2).append(e.getKey()).append(":\n");
                dump(e.getValue(), sb, indent + 3);
            }
        }

        // Array items
        if (node.getType() == PrimitiveType.ARRAY) {
            indent(sb, indent + 1).append("items:\n");
            dump(node.getItemSchema(), sb, indent + 2);
        }

        // Rules
        if (!node.getRules().isEmpty()) {
            indent(sb, indent + 1).append("rules:\n");
            for (var r : node.getRules()) {
                indent(sb, indent + 2).append(r.getClass().getSimpleName()).append("\n");
            }
        }

        indent(sb, indent).append("}\n");
    }

    private static StringBuilder indent(StringBuilder sb, int indent) {
        return sb.append("  ".repeat(indent));
    }
}
