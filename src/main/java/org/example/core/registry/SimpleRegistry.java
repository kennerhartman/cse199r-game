package org.example.core.registry;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.example.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class SimpleRegistry<T> implements Registry<T> {
    private final Identifier registerId;

    public SimpleRegistry(Identifier registerId) {
        this.registerId = registerId;
    }

    private final Map<Identifier, T> entryMap = new LinkedHashMap<>();
    private final Map<T, Identifier> inverseMap = new IdentityHashMap<>();

    public T register(Identifier key, T entry) {
        Objects.requireNonNull(key, "Key must not be null!");
        Objects.requireNonNull(entry, "Entry must not be null!");

        if (this.entryMap.containsKey(key)) {
            throw new IllegalArgumentException(String.format("Key '%s' already exists!", key));
        }

        this.entryMap.put(key, entry);
        this.inverseMap.put(entry, key);

        return entry;
    }

    public Identifier getRegisterId() {
        return this.registerId;
    }

    @Override
    public Identifier getKey(T entry) {
        return this.inverseMap.get(entry);
    }

    @Override
    public T getValue(Identifier id) {
        return this.entryMap.get(id);
    }

    @Override
    public Stream<T> stream() {
        return this.entryMap.values().stream();
    }

    @Override
    public @NotNull Iterator<Map.Entry<Identifier, T>> iterator() {
        return Collections.unmodifiableSet(this.entryMap.entrySet()).iterator();
    }
}
