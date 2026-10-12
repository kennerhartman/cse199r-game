package org.example.core.registries;

import java.util.HashMap;
import java.util.function.Supplier;

import org.example.card.Card;
import org.example.core.registry.DataRegistry;
import org.example.core.registry.Registry;
import org.example.core.registry.RegistryKeys;
import org.example.core.registry.loader.JsonDataLoader;
import org.example.core.serializer.Codec;
import org.example.util.Identifier;

public class Registries {
    private static final HashMap<Identifier, Registry<?>> LOADERS = new HashMap<>();

    public static Registry<Card> CARDS = createDataRegistry(RegistryKeys.CARDS, Card.CODEC);

    /**
     * Creates a registry that is populated dynamically from data.
     * @param name an <code>Identifier</code> that uniquely identifies the registry.
     * @return the registry.
     * @param <T> the registry type.
     */
    private static <T> DataRegistry<T> createDataRegistry(Identifier name, Supplier<Codec<? extends T>> codec) {
        return createDataRegistry(name, codec.get());
    }

    private static <T> DataRegistry<T> createDataRegistry(Identifier name, Codec<? extends T> codec) {
        DataRegistry<T> registry = new DataRegistry<>(name, codec);
        LOADERS.put(name, registry);

        return registry;
    }

    public static void load() {
        JsonDataLoader loader = new JsonDataLoader();

        LOADERS.forEach((key, registry) -> {
            if (registry instanceof DataRegistry<?> dataRegistry) {
                loader.populate(key, dataRegistry);
            }
        });
    }
}
