package org.example.registry;

import org.example.serializer.Codec;
import org.example.util.Identifier;

public class DataRegistry<T> extends SimpleRegistry<T> {
    private final Codec<? extends T> codec;

    public DataRegistry(Identifier registerId, Codec<? extends T> codec) {
        super(registerId);

        this.codec = codec;
    }

    public Codec<? extends T> getCodec() {
        return this.codec;
    }
}
