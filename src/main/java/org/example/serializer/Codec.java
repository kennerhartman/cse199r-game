package org.example.serializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonElement;

// Lightweight implementation of codecs derived from Mojang's
// DFU Library: https://github.com/Mojang/DataFixerUpper
public interface Codec<T> {
    Object encode(T object);
    T decode(Object object);

    default String encode(JsonOps ops, T object) {
        return ops.encode(this, object);
    }

    default T decode(JsonOps ops, JsonElement element) {
        return ops.decode(this, element);
    }

    default MapCodec<T> fieldOf(String key) {
        return new MapCodec<>() {
            @Override
            public String key() {
                return key;
            }

            @Override
            public Object encode(T object) {
                Map<String, Object> map = new HashMap<>();
                map.put(key, Codec.this.encode(object));
                return map;
            }

            @Override
            public T decode(Object object) {
                if (object instanceof Map<?, ?> map) {
                    if (map.containsKey(key)) {
                        return Codec.this.decode(map.get(key));
                    } else {
                        throw new NullPointerException("Expected value for key '" + key + "' but found null instead!");
                    }
                }

                return Codec.this.decode(object);
            }
        };
    }

    default MapCodec<T> optionalFieldOf(String key, T defaultValue) {
        return new MapCodec<>() {
            @Override
            public String key() {
                return key;
            }

            @Override
            public Object encode(T object) {
                return object == null ? Codec.this.decode(defaultValue) : Codec.this.decode(object);
            }

            @Override
            public T decode(Object object) {
                return Codec.this.decode(object);
            }

            @Override
            public <O> RecordCodecBuilder<O, T> forGetter(Function<O, T> getter) {
                return new RecordCodecBuilder<>(
                        (map) -> {
                            if (map.containsKey(this.key())) {
                                return decode(map.get(this.key()));
                            } else {
                                return defaultValue;
                            }
                        },
                        (object, map) -> {
                            map.put(this.key(), getter.apply(object));
                            return map;
                        }
                );
            }
        };
    }

    default Codec<List<T>> listOf() {
        return new Codec<>() {
            @Override
            public Object encode(List<T> objects) {
                List<Object> list = new ArrayList<>();

                for (T object : objects) {
                    list.add(Codec.this.encode(object));
                }

                return list;
            }

            @Override
            public List<T> decode(Object objects) {
                if (objects instanceof List<?> rawList) {
                    List<T> list = new ArrayList<>();

                    for (Object object : rawList) {
                        list.add(Codec.this.decode(object));
                    }

                    return list;
                } else {
                    throw new IllegalStateException("Expected type 'List' but got '" + (objects == null ? "null" : objects.getClass().getName()) + "'!");
                }
            }
        };
    }

    static <E extends Enum<E>> Codec<E> ofEnum(Class<E> enumClass) {
        return new Codec<>() {
            @Override
            public Object encode(E input) {
                if (input == null) {
                    throw new NullPointerException("Expected value for enum, but found null instead!");
                } else {
                    return input.name().toLowerCase();
                }
            }

            @Override
            public E decode(Object object) {
                if (object instanceof String s) {
                    return Enum.valueOf(enumClass, s.toUpperCase());
                } else {
                    throw new IllegalStateException("Expected type 'String' for Enum but got '" + object.getClass() + "'!");
                }
            }
        };
    }

    interface MapCodec<F> extends Codec<F> {
        String key();

        default <O> RecordCodecBuilder<O, F> forGetter(Function<O, F> getter) {
            return new RecordCodecBuilder<>(
                (map) -> {
                    if (map.containsKey(this.key())) {
                        return decode(map.get(this.key()));
                    } else {
                        throw new NullPointerException("Expected value for key '" + this.key() + "' but found null instead!");
                    }
                },
                (object, map) -> {
                    map.put(this.key(), getter.apply(object));
                    return map;
                }
            );
        }
    }

    static <K, V> Codec<Map<K, V>> unboundedMap(Codec<K> keyCodec, Codec<V> valueCodec) {
        return new Codec<>() {
            @Override
            public Object encode(Map<K, V> input) {
                if (input == null) {
                    return null;
                }

                Map<Object, Object> map = new HashMap<>();

                for (Map.Entry<K, V> entry : input.entrySet()) {
                    map.put(keyCodec.encode(entry.getKey()), valueCodec.encode(entry.getValue()));
                }

                return map;
            }

            @Override
            public Map<K, V> decode(Object object) {
                Map<K, V> decodedMap = new HashMap<>();

                if (object instanceof Map<?, ?> rawMap) {
                    rawMap.forEach((k, v) -> {
                        K decodedKey = keyCodec.decode(k);
                        V decodedValue = valueCodec.decode(v);

                        if (decodedKey != null) {
                            decodedMap.put(decodedKey, decodedValue);
                        }
                    });
                }

                return decodedMap;
            }
        };
    }

    Codec<String> STRING = new Codec<>() {
        @Override
        public Object encode(String s) {
            return s;
        }

        @Override
        public String decode(Object object) {
            if (object instanceof String s) {
                return s;
            } else {
                throw new IllegalStateException("Expected type 'String' but got '" + object.getClass() + "'!");
            }
        }
    };

    Codec<Integer> INT = new Codec<>() {
        @Override
        public Object encode(Integer i) {
            return i;
        }

        @Override
        public Integer decode(Object object) {
            if (object instanceof Number number) {
                return number.intValue();
            } else {
                throw new IllegalStateException("Expected type 'Integer' but got '" + object.getClass() + "'!");
            }
        }
    };

    Codec<Float> FLOAT = new Codec<>() {
        @Override
        public Object encode(Float f) {
            return f;
        }

        @Override
        public Float decode(Object object) {
            if (object instanceof Number number) {
                return number.floatValue();
            } else {
                throw new IllegalStateException("Expected type 'Float' but got '" + object.getClass() + "'!");
            }
        }
    };

    Codec<Double> DOUBLE = new Codec<>() {
        @Override
        public Object encode(Double d) {
            return d;
        }

        @Override
        public Double decode(Object object) {
            if (object instanceof Number number) {
                return number.doubleValue();
            } else {
                throw new IllegalStateException("Expected type 'Double' but got '" + object.getClass() + "'!");
            }
        }
    };

    Codec<Boolean> BOOL = new Codec<>() {
        @Override
        public Object encode(Boolean b) {
            return b;
        }

        @Override
        public Boolean decode(Object object) {
            if (object instanceof Boolean b) {
                return b;
            } else {
                throw new IllegalStateException("Expected type 'Boolean' but got '" + object.getClass() + "'!");
            }
        }
    };
}

