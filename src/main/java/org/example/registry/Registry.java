package org.example.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.example.util.Identifier;
import org.jetbrains.annotations.NotNull;

public interface Registry<T> extends Iterable<Map.Entry<Identifier, T>> {
    T register(Identifier key, T entry);

    Identifier getKey(T entry);
    T getValue(Identifier id);
}
