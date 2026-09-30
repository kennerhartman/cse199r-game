package org.example.util;

import org.jetbrains.annotations.NotNull;

public final class Identifier implements Comparable<Identifier> {
    public final String namespace;
    public final String path;

    Identifier(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }

    public static Identifier ofDefault(String path) {
        return of("hero_of_the_draw", path);
    }

    public static Identifier of(String namespace, String path) {
        return new Identifier(namespace, path);
    }

    @Override
    public String toString() {
        return String.format("%s:%s", namespace, path);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof Identifier other && other.path.equals(this.path) && other.namespace.equals(this.namespace);
        }
    }

    @Override
    public int compareTo(@NotNull Identifier o) {
        int i = this.path.compareTo(o.path);

        if (i == 0) {
            return this.namespace.compareTo(o.namespace);
        }

        return i;
    }

    public int hashCode() {
        return 31 * this.namespace.hashCode() + this.path.hashCode();
    }
}
