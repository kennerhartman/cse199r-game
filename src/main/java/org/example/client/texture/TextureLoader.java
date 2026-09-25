package org.example.client.texture;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.example.util.Identifier;

import com.raylib.Raylib;

public class TextureLoader {
    public static boolean isJar = false;

    private static Path getAssets() {
        try {
            URI uri = TextureLoader.class.getClassLoader().getResource("assets").toURI();
            Path path;

            if (uri.getScheme().equals("jar")) {
                isJar = true;
                FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap());
                path = fs.getPath("/assets");
            } else {
                path = Paths.get(uri);
            }

            return path;
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Identifier> prepare(Identifier location) {
        System.out.println(location);
        List<Identifier> textures = new ArrayList<>();

        Path assetsRoot = getAssets();

        try (Stream<Path> stream = Files.walk(assetsRoot)) {
            stream
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        Path relativeToAssets = assetsRoot.relativize(path);

                        String cleanAssetPath = relativeToAssets.toString().replace("\\", "/");
                        String[] segments = cleanAssetPath.split("/");

                        String namespace = segments[0];

                        if (namespace.equals(location.namespace)) {
                            int texturesDirIndex = cleanAssetPath.indexOf("textures/");

                            if (texturesDirIndex == -1) {
                                return;
                            }

                            String textureLocation = cleanAssetPath.substring(texturesDirIndex + "textures/".length());

                            if (textureLocation.startsWith(location.path + "/")) {
                                textures.add(Identifier.of(namespace, textureLocation));
                            }
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return textures;
    }

    public static Map<Identifier, Raylib.Texture> upload(List<Identifier> textureIds) {
        Map<Identifier, Raylib.Texture> textures = new HashMap<>();

        for (Identifier textureId : textureIds) {
            String location = String.format("assets/%s/textures/%s", textureId.namespace, textureId.path);

            try (InputStream inputStream = TextureLoader.class.getClassLoader().getResourceAsStream(location)) {
                if (inputStream == null) {
                    throw new RuntimeException("Resource file not found: " + location);
                }

                byte[] bytes = inputStream.readAllBytes();

                Raylib.Image image = Raylib.LoadImageFromMemory(".png", bytes, bytes.length);
                Raylib.Texture texture = Raylib.LoadTextureFromImage(image);
                Raylib.UnloadImage(image);

                textures.put(textureId, texture);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read texture asset from resources", e);
            }
        }

        return textures;
    }

    private static boolean isPng(Path path) {
        String name = path.getFileName().toString();
        String extension = name.substring(name.lastIndexOf('.') + 1);

        return extension.equalsIgnoreCase("png");
    }

    private static String getNamespaceFromPath(String path) {
        int start = path.indexOf("/assets/") + "/assets/".length();
        int end = path.indexOf("/", start);

        return path.substring(start, end);
    }

    public static Raylib.Texture loadTexture(String path) {
        try (InputStream inputStream = TextureLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new RuntimeException("Resource file not found: " + path);
            }

            byte[] bytes = inputStream.readAllBytes();

            Raylib.Image image = Raylib.LoadImageFromMemory(".png", bytes, bytes.length);
            Raylib.Texture texture = Raylib.LoadTextureFromImage(image);
            Raylib.UnloadImage(image);

            return texture;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read texture asset from resources", e);
        }
    }
}
