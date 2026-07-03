package io.github.qishr.cascara.schema.internal;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import io.github.qishr.cascara.schema.util.SchemaResolver;

public class ValidationContext {
    private final SchemaResolver resolver;
    private final Deque<String> pathStack = new ArrayDeque<>();
    private final Map<String, Object> metadata = new HashMap<>();

    public ValidationContext(SchemaResolver resolver) {
        this.resolver = resolver;
        this.pathStack.push(""); // Root path
    }

    // Path management for error reporting
    public void pushPath(String segment) {
        String current = pathStack.peek();
        pathStack.push(current.isEmpty() ? segment : current + "/" + segment);
    }

    public void popPath() {
        pathStack.pop();
    }

    public String getCurrentPath() {
        return pathStack.peek();
    }

    // Access to resolver for dynamic resolution during validation
    public SchemaResolver getResolver() {
        return resolver;
    }

    // Allows rules to share state (e.g., cross-field validation)
    public void setAttribute(String key, Object value) { metadata.put(key, value); }
    public Object getAttribute(String key) { return metadata.get(key); }
}