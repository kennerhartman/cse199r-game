package org.example.registry.loader;

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.stream.Stream;

import org.example.client.texture.TextureLoader;
import org.example.registry.DataRegistry;
import org.example.serializer.JsonOps;
import org.example.util.Identifier;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JsonDataLoader {
    private static final Gson GSON = new Gson();

    private static Path getData() {
        try {
            URI uri = TextureLoader.class.getClassLoader().getResource("data").toURI();
            Path path;

            if (uri.getScheme().equals("jar")) {
                FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap());
                path = fs.getPath("/data");
            } else {
                path = Paths.get(uri);
            }

            return path;
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void populate(Identifier name, DataRegistry<T> registry) {
        String namespace = name.namespace;
        String path = name.path;

        Path data = getData();

        Path cards = data.resolve(namespace + "/" + path);

        try (Stream<Path> files = Files.list(cards)) {
            files
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try (FileReader reader = new FileReader(file.toString())) {
                            JsonObject json = GSON.fromJson(reader, JsonObject.class);

                            T value = registry.getCodec().decode(JsonOps.INSTANCE, json);

                            int lastSlash = file.toString().lastIndexOf("/");

                            String substring = file.toString().substring(lastSlash + 1);
                            int lastPeriod = substring.lastIndexOf(".");
                            substring = substring.substring(0, lastPeriod);

                            registry.register(Identifier.of(namespace, substring), value);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
