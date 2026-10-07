package org.example.registry;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.example.serializer.Codec;
import org.example.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class DynamicRegistry<T> extends SimpleRegistry<T> {
    private final Codec<? extends T> codec;

    public DynamicRegistry(Identifier registerId, Codec<? extends T> codec) {
        super(registerId);

        this.codec = codec;
    }

    public Codec<? extends T> getCodec() {
        return this.codec;
    }
}
