package org.example.registry;

import java.util.HashMap;
import java.util.function.Supplier;

import org.example.card.Card;
import org.example.registry.loader.JsonDataLoader;
import org.example.serializer.Codec;
import org.example.util.Identifier;

public class Registries {
    private static final JsonDataLoader loader = new JsonDataLoader();
    private static final HashMap<Identifier, Registry<?>> LOADERS = new HashMap<>();

    public static Registry<Card> CARDS = createDataRegistry(RegistryKey.CARDS, Card::getCodec);

    public static <T> T register(Registry<T> registry, Identifier id, T value) {
        registry.register(id, value);

        return value;
    }

    /**
     * Creates a registry that is populated dynamically from data.
     * @param name an <code>Identifier</code> that uniquely identifies the registry.
     * @return the registry.
     * @param <T> the registry type.
     */
    private static <T> DynamicRegistry<T> createDataRegistry(Identifier name, Supplier<Codec<? extends T>> codec) {
        DynamicRegistry<T> registry = new DynamicRegistry<>(name, codec.get());
        LOADERS.put(name, registry);

        return registry;
    }

    public static void load() {
        JsonDataLoader loader = new JsonDataLoader();

        LOADERS.forEach((key, value) -> {
            if (value instanceof DynamicRegistry<?> dynamicRegistry) {
                loader.populate(key, dynamicRegistry);
            }
        });
    }
}
