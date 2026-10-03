package org.example.serializer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

// Lightweight implementation of codecs derived from Mojang's
// DFU Library: https://github.com/Mojang/DataFixerUpper
public class RecordCodecBuilder<O, F> {
    final Function<Map<String, Object>, F> decoder;
    final BiFunction<O, Map<String, Object>, Map<String, Object>> encoder;

    public RecordCodecBuilder(Function<Map<String, Object>, F> decoder, BiFunction<O, Map<String, Object>, Map<String, Object>> encoder) {
        this.decoder = decoder;
        this.encoder = encoder;
    }

    public static <O> Codec<O> create(Function<Instance<O>, RecordCodecBuilder<O, O>> builder) {
        Instance<O> instance = new Instance<>();
        RecordCodecBuilder<O, O> product = builder.apply(instance);

        return new Codec<>() {
            @Override
            public Object encode(O object) {
                return product.encoder.apply(object, new HashMap<>());
            }

            @Override
            @SuppressWarnings("unchecked")
            public O decode(Object object) {
                if (object instanceof Map<?, ?> map) {
                    return product.decoder.apply((Map<String, Object>) map);
                } else {
                    throw new IllegalStateException("Expected Map<String, Object>, got '" + object.getClass().getName() + "'");
                }
            }
        };
    }

    public static <O, F> RecordCodecBuilder<O, F> point(F value) {
        return new RecordCodecBuilder<>(
                map -> value,
                (object, map) -> map
        );
    }

    @FunctionalInterface
    public interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }

    @FunctionalInterface
    public interface QuadFunction<A, B, C, D, R> {
        R apply(A a, B b, C c, D d);
    }

    @FunctionalInterface
    public interface PentaFunction<A, B, C, D, E, R> {
        R apply(A a, B b, C c, D d, E e);
    }

    @FunctionalInterface
    public interface HexaFunction<A, B, C, D, E, F, R> {
        R apply(A a, B b, C c, D d, E e, F f);
    }

    @FunctionalInterface
    public interface HeptaFunction<A, B, C, D, E, F, G, R> {
        R apply(A a, B b, C c, D d, E e, F f, G g);
    }

    public static class Instance<O> {
        public <A> Group<O, A> group(RecordCodecBuilder<O, A> a) {
            return new Group<>(a);
        }

        public <A, B> Group2<O, A, B> group(RecordCodecBuilder<O, A> a, RecordCodecBuilder<O, B> b) {
            return new Group2<>(a, b);
        }

        public <A, B, C> Group3<O, A, B, C> group(RecordCodecBuilder<O, A> a, RecordCodecBuilder<O, B> b, RecordCodecBuilder<O, C> c) {
            return new Group3<>(a, b, c);
        }

        public <A, B, C, D> Group4<O, A, B, C, D> group(RecordCodecBuilder<O, A> a, RecordCodecBuilder<O, B> b, RecordCodecBuilder<O, C> c,
                RecordCodecBuilder<O, D> d) {
            return new Group4<>(a, b, c, d);
        }

        public <A, B, C, D, E> Group5<O, A, B, C, D, E> group(RecordCodecBuilder<O, A> a, RecordCodecBuilder<O, B> b, RecordCodecBuilder<O, C> c,
                                                        RecordCodecBuilder<O, D> d, RecordCodecBuilder<O, E> e) {
            return new Group5<>(a, b, c, d, e);
        }

        public <A, B, C, D, E, F> Group6<O, A, B, C, D, E, F> group(RecordCodecBuilder<O, A> a, RecordCodecBuilder<O, B> b, RecordCodecBuilder<O, C> c,
                                                              RecordCodecBuilder<O, D> d, RecordCodecBuilder<O, E> e, RecordCodecBuilder<O, F> f) {
            return new Group6<>(a, b, c, d, e, f);
        }

        public <A, B, C, D, E, F, G> Group7<O, A, B, C, D, E, F, G> group(RecordCodecBuilder<O, A> a, RecordCodecBuilder<O, B> b, RecordCodecBuilder<O, C> c,
                                                                    RecordCodecBuilder<O, D> d, RecordCodecBuilder<O, E> e, RecordCodecBuilder<O, F> f,
                                                                    RecordCodecBuilder<O, G> g) {
            return new Group7<>(a, b, c, d, e, f, g);
        }
    }

    public record Group<O, A>(RecordCodecBuilder<O, A> a) {
        public RecordCodecBuilder<O, O> apply(Function<A, O> constructor) {
            return new RecordCodecBuilder<>(
                    map -> constructor.apply(a.decoder.apply(map)),
                    (o, map) -> {
                        a.encoder.apply(o, map);
                        return map;
                    }
            );
        }
    }

    public record Group2<O, A, B>(RecordCodecBuilder<O, A> a, RecordCodecBuilder<O, B> b) {
        public RecordCodecBuilder<O, O> apply(BiFunction<A, B, O> constructor) {
            return new RecordCodecBuilder<>(
                    map -> constructor.apply(a.decoder.apply(map), b.decoder.apply(map)),
                    (o, map) -> {
                        a.encoder.apply(o, map);
                        b.encoder.apply(o, map);
                        return map;
                    }
            );
        }
    }

    public record Group3<O, A, B, C>(RecordCodecBuilder<O, A> a, RecordCodecBuilder<O, B> b, RecordCodecBuilder<O, C> c) {
        public RecordCodecBuilder<O, O> apply(TriFunction<A, B, C, O> constructor) {
            return new RecordCodecBuilder<>(
                    map -> constructor.apply(a.decoder.apply(map), b.decoder.apply(map), c.decoder.apply(map)),
                    (o, map) -> {
                        a.encoder.apply(o, map);
                        b.encoder.apply(o, map);
                        c.encoder.apply(o, map);
                        return map;
                    }
            );
        }
    }

    public record Group4<O, A, B, C, D>(RecordCodecBuilder<O, A> a, RecordCodecBuilder<O, B> b, RecordCodecBuilder<O, C> c,
                                        RecordCodecBuilder<O, D> d) {
        public RecordCodecBuilder<O, O> apply(QuadFunction<A, B, C, D, O> constructor) {
            return new RecordCodecBuilder<>(
                    map -> constructor.apply(a.decoder.apply(map), b.decoder.apply(map), c.decoder.apply(map), d.decoder.apply(map)),
                    (o, map) -> {
                        a.encoder.apply(o, map);
                        b.encoder.apply(o, map);
                        c.encoder.apply(o, map);
                        d.encoder.apply(o, map);
                        return map;
                    }
            );
        }
    }

    public record Group5<O, A, B, C, D, E>(RecordCodecBuilder<O, A> a, RecordCodecBuilder<O, B> b, RecordCodecBuilder<O, C> c,
                                        RecordCodecBuilder<O, D> d, RecordCodecBuilder<O, E> e) {
        public RecordCodecBuilder<O, O> apply(PentaFunction<A, B, C, D, E, O> constructor) {
            return new RecordCodecBuilder<>(
                    map -> constructor.apply(
                            a.decoder.apply(map),
                            b.decoder.apply(map),
                            c.decoder.apply(map),
                            d.decoder.apply(map),
                            e.decoder.apply(map)
                    ),
                    (o, map) -> {
                        a.encoder.apply(o, map);
                        b.encoder.apply(o, map);
                        c.encoder.apply(o, map);
                        d.encoder.apply(o, map);
                        e.encoder.apply(o, map);
                        return map;
                    }
            );
        }
    }

    public record Group6<O, A, B, C, D, E, F>(RecordCodecBuilder<O, A> a, RecordCodecBuilder<O, B> b, RecordCodecBuilder<O, C> c,
                                           RecordCodecBuilder<O, D> d, RecordCodecBuilder<O, E> e, RecordCodecBuilder<O, F> f) {
        public RecordCodecBuilder<O, O> apply(HexaFunction<A, B, C, D, E, F, O> constructor) {
            return new RecordCodecBuilder<>(
                    map -> constructor.apply(
                            a.decoder.apply(map),
                            b.decoder.apply(map),
                            c.decoder.apply(map),
                            d.decoder.apply(map),
                            e.decoder.apply(map),
                            f.decoder.apply(map)
                    ),
                    (o, map) -> {
                        a.encoder.apply(o, map);
                        b.encoder.apply(o, map);
                        c.encoder.apply(o, map);
                        d.encoder.apply(o, map);
                        e.encoder.apply(o, map);
                        f.encoder.apply(o, map);
                        return map;
                    }
            );
        }
    }

    public record Group7<O, A, B, C, D, E, F, H>(RecordCodecBuilder<O, A> a, RecordCodecBuilder<O, B> b, RecordCodecBuilder<O, C> c,
                                              RecordCodecBuilder<O, D> d, RecordCodecBuilder<O, E> e, RecordCodecBuilder<O, F> f,
                                              RecordCodecBuilder<O, H> h) {
        public RecordCodecBuilder<O, O> apply(HeptaFunction<A, B, C, D, E, F, H, O> constructor) {
            return new RecordCodecBuilder<>(
                    map -> constructor.apply(
                            a.decoder.apply(map),
                            b.decoder.apply(map),
                            c.decoder.apply(map),
                            d.decoder.apply(map),
                            e.decoder.apply(map),
                            f.decoder.apply(map),
                            h.decoder.apply(map)
                    ),
                    (o, map) -> {
                        a.encoder.apply(o, map);
                        b.encoder.apply(o, map);
                        c.encoder.apply(o, map);
                        d.encoder.apply(o, map);
                        e.encoder.apply(o, map);
                        f.encoder.apply(o, map);
                        h.encoder.apply(o, map);
                        return map;
                    }
            );
        }
    }
}
