package org.example.core.registry;

import java.util.Map;
import java.util.stream.Stream;

import org.example.util.Identifier;

public interface Registry<T> extends Iterable<Map.Entry<Identifier, T>> {
    T register(Identifier key, T entry);

    Identifier getKey(T entry);
    T getValue(Identifier id);

    Stream<T> stream();
}
