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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import io.github.qishr.cascara.common.lang.util.QuoteStyle;
import io.github.qishr.cascara.common.lang.generic.GenericAstMapEntry;
import io.github.qishr.cascara.common.lang.generic.GenericAstMap;
import io.github.qishr.cascara.common.lang.generic.GenericAstScalar;
import io.github.qishr.cascara.common.lang.generic.GenericAstSequence;
import io.github.qishr.cascara.common.type.PrimitiveType;
import io.github.qishr.cascara.schema.diagnostic.SchemaDiagnosticMessage;
import io.github.qishr.cascara.schema.diagnostic.SchemaException;
import io.github.qishr.cascara.schema.rule.EnumRule;
import io.github.qishr.cascara.schema.rule.MaxItemsRule;
import io.github.qishr.cascara.schema.rule.MaxValueRule;
import io.github.qishr.cascara.schema.rule.MinItemsRule;
import io.github.qishr.cascara.schema.rule.MinValueRule;
import io.github.qishr.cascara.schema.rule.RequiredRule;
import io.github.qishr.cascara.schema.rule.ValidationRule;
import io.github.qishr.cascara.schema.structure.ArraySchemaNode;
import io.github.qishr.cascara.schema.structure.LazySchemaNode;
import io.github.qishr.cascara.schema.structure.ObjectSchemaNode;
import io.github.qishr.cascara.schema.structure.SchemaNode;
import io.github.qishr.cascara.schema.util.SchemaDecompiler;

public final class SchemaDecompiler {
    private static final String META_SCHEMA_URI = "https://json-schema.org/draft/2020-12/schema";

    private URI originUri;

    public GenericAstMap decompile(Schema compiled) {
        if (compiled == null || compiled.getRoot() == null) return null;

        SchemaNode compiledRoot = compiled.getRoot();
        GenericAstMap root = new GenericAstMap();

        // DYNAMIC DIALECT: Use the Meta-Schema URI actually associated with the node
        // This handles CEMA vs Vanilla automatically.
        URI metaUri = compiledRoot.getMetaSchema().getOriginUri();
        if (metaUri != null) {
            root.put(SchemaKeyword.SCHEMA.asString(), scalarValue(metaUri.toString()));
        } else {
            root.put(SchemaKeyword.SCHEMA.asString(), scalarValue(META_SCHEMA_URI));
        }

        originUri = compiled.getOriginUri();
        root.put(SchemaKeyword.ID.asString(), scalarValue(originUri));

        GenericAstMap decompiled = decompileInternal(compiledRoot);
        for (GenericAstMapEntry entry : decompiled.getEntries()) {
            root.put(entry.getKeyString(), entry.getValue());
        }
        return root;
    }

    private GenericAstMap decompileInternal(SchemaNode compiled) throws SchemaException {
        GenericAstMap decompiled = new GenericAstMap();

        if (compiled.getContentMediaType() instanceof String mediaType) {
            decompiled.put(SchemaKeyword.CONTENT_MEDIA_TYPE.asString(), scalarValue(mediaType));
        }

        for (var e : standardKeywords(compiled).entrySet()) {
            decompiled.put(e.getKey(), scalarValue(e.getValue()));
        }

        compiled.getExtensions().forEach((key, value) -> {
            if (value instanceof Map<?,?> mapValue) {
                decompiled.put(key, convertToSimpleMap(mapValue));
            } else {
                decompiled.put(key, scalarValue(value));
            }
        });

        // type-specific structure
        GenericAstMap node = switch (compiled.getType()) {
            case OBJECT  -> {
                if (compiled instanceof ObjectSchemaNode o) {
                    yield object(o);
                }
                else if (compiled instanceof LazySchemaNode lazy) {
                    // GenericAstMap map = new GenericAstMap();
                    // String reference = bridge.getRef();

                    GenericAstMap refMap = new GenericAstMap();
                    String ref = lazy.getRef();
                    String refKey = (ref != null && ref.startsWith("#") && !ref.contains("/")) ? SchemaKeyword.DYNAMIC_REF.asString() : SchemaKeyword.REF.asString();
                    refMap.put(refKey, scalarValue(ref));
                    yield refMap;
                }
                else {
                    yield null;
                }
            }
            case ARRAY   -> array((ArraySchemaNode) compiled);
            case STRING, BOOLEAN, INTEGER, NUMBER -> scalar(compiled);
            default      -> null;
        };

        if (node != null) {
            for (GenericAstMapEntry entry : node.getEntries()) {
                decompiled.put(entry.getKeyString(), entry.getValue());
            }
        }

        // now apply rules as JSON Schema keywords
        applyRules(compiled, decompiled);

        return decompiled;
    }

    private GenericAstMap object(ObjectSchemaNode object) throws SchemaException {
        GenericAstMap map = new GenericAstMap();
        map.put(SchemaKeyword.TYPE.asString(), scalarValue(PrimitiveType.OBJECT.asString()));

        // definitions
        if (!object.getDefinitions().isEmpty()) {
            GenericAstMap definitions = new GenericAstMap();
            for (var e : object.getDefinitions().entrySet()) {
                definitions.put(e.getKey(), decompileInternal(e.getValue()));
            }
            map.put(SchemaKeyword.DEFS.asString(), definitions);
        }

        // properties
        if (!object.getProperties().isEmpty()) {
            GenericAstMap properties = new GenericAstMap();
            for (var e : object.getProperties().entrySet()) {
                properties.put(e.getKey(), decompileInternal(e.getValue()));
            }
            map.put(SchemaKeyword.PROPERTIES.asString(), properties);
        }

        if (object.getAdditionalPropertiesSchema() != null) {
            map.put("additionalProperties", decompileInternal(object.getAdditionalPropertiesSchema()));
        } else if (!object.areAdditionalPropertiesAllowed()) {
            map.put("additionalProperties", new GenericAstScalar(false));
        }

        // Handle unevaluatedProperties
        if (object.getUnevaluatedPropertiesSchema() != null) {
            map.put("unevaluatedProperties", decompileInternal(object.getUnevaluatedPropertiesSchema()));
        } else if (!object.areUnevaluatedPropertiesAllowed()) {
            map.put("unevaluatedProperties", new GenericAstScalar(false));
        }

        return map;
    }

    private GenericAstMap array(ArraySchemaNode array) {
        GenericAstMap map = new GenericAstMap();
        GenericAstMap items = new GenericAstMap();
        map.put(SchemaKeyword.TYPE.asString(), scalarValue(PrimitiveType.ARRAY.asString()));

        SchemaNode template = array.getItemSchema();
        if (template instanceof LazySchemaNode lazy) {
            if (lazy.getRef() == null || lazy.getRef().isEmpty()) {
                throw new SchemaException(originUri, SchemaDiagnosticMessage.MISSING_REF, array.getOriginUri());
            }
            items.put(SchemaKeyword.REF.asString(), scalarValue(lazy.getRef()));
        }
        else {
            items.put(SchemaKeyword.TYPE.asString(), scalarValue(template.getType().toString().toLowerCase()));
        }

        if (template != null) {
            // Recurse so nested structures in arrays are fully decompiled
            map.put(SchemaKeyword.ITEMS.asString(), decompileInternal(template));
        }

        return map;
    }

    private GenericAstMap scalar(SchemaNode node) {
        GenericAstMap map = new GenericAstMap();
        String type = node.getType().toString().toLowerCase();
        if (type != null && node.getType() != PrimitiveType.ANY) {
            map.put(SchemaKeyword.TYPE.asString(), scalarValue(type));
        }
        return map;
    }

    private GenericAstScalar scalarValue(Object value) {
        GenericAstScalar scalar = new GenericAstScalar(value);
        if (value instanceof String) {
            scalar.setQuoteStyle(QuoteStyle.DOUBLE);
        }
        return scalar;
    }

    private Map<String, Object> standardKeywords(SchemaNode compiled) {
        Map<String, Object> map = new HashMap<>();
        if (compiled.getDefaultValue() != null) {
            map.put(SchemaKeyword.DEFAULT.asString(), compiled.getDefaultValue());
        }
        if (compiled.getDescription() != null && !compiled.getDescription().isEmpty()) {
            map.put(SchemaKeyword.DESCRIPTION.asString(), compiled.getDescription());
        }
        if (compiled.getDynamicAnchor() != null && !compiled.getDynamicAnchor().isEmpty()) {
            map.put(SchemaKeyword.DYNAMIC_ANCHOR.asString(), compiled.getDynamicAnchor());
        }
        if (compiled.getFormat() != null && !compiled.getFormat().isEmpty()) {
            map.put(SchemaKeyword.FORMAT.asString(), compiled.getFormat());
        }
        if (compiled.isReadOnly()) {
            map.put(SchemaKeyword.READ_ONLY.asString(), true);
        }
        if (compiled.getTitle() != null && !compiled.getTitle().isEmpty()) {
            map.put(SchemaKeyword.TITLE.asString(), compiled.getTitle());
        }
        return map;
    }

    private GenericAstMap convertToSimpleMap(Map<?, ?> map) {
        GenericAstMap node = new GenericAstMap();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object val = entry.getValue();
            if (val instanceof Map<?, ?> subMap) {
                node.put(entry.getKey().toString(), convertToSimpleMap(subMap));
            } else {
                node.put(entry.getKey().toString(), scalarValue(val));
            }
        }
        return node;
    }

    private GenericAstSequence sequenceOf(Iterable<?> values) {
        GenericAstSequence seq = new GenericAstSequence();
        for (Object v : values) {
            seq.add(scalarValue(v));
        }
        return seq;
    }

    private void applyRules(SchemaNode node, GenericAstMap target) {
        for (ValidationRule r : node.getRules()) {
            if (r instanceof EnumRule er) {
                target.put("enum", sequenceOf(er.getAllowedValues()));
            } else if (r instanceof MinValueRule min) {
                target.put("minimum", scalarValue(min.getMin()));
            } else if (r instanceof MaxValueRule max) {
                target.put("maximum", scalarValue(max.getMax()));
            } else if (r instanceof MinItemsRule minItems) {
                target.put("minItems", scalarValue(minItems.getMinItems()));
            } else if (r instanceof MaxItemsRule maxItems) {
                target.put("maxItems", scalarValue(maxItems.getMaxItems()));
            } else if (r instanceof RequiredRule req) {
                target.put("required", sequenceOf(req.getRequiredKeys()));
            }
        }
    }
}
