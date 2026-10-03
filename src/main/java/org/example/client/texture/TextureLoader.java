package org.example.client.texture;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.example.serializer.JsonOps;
import org.example.util.Identifier;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.raylib.Raylib;

public class TextureLoader {
    private static final Gson GSON = new Gson();

    private static Path getAssets() {
        try {
            URI uri = TextureLoader.class.getClassLoader().getResource("assets").toURI();
            Path path;

            if (uri.getScheme().equals("jar")) {
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

    public static Map<Identifier, UploadedTexture.@Nullable TextureMetadata> prepare(Identifier location) {
        Map<Identifier, UploadedTexture.@Nullable TextureMetadata> textures = new HashMap<>();

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
                                UploadedTexture.TextureMetadata metadata = null;

                                if (assetHasMetadata(path)) {
                                    File encodedMetadata = Paths.get(path + ".json").toFile();

                                    try (FileReader reader = new FileReader(encodedMetadata.toString())) {
                                        JsonElement element = GSON.fromJson(reader, JsonElement.class);
                                        metadata = UploadedTexture.CODEC.decode(JsonOps.INSTANCE, element);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                                textures.put(Identifier.of(namespace, textureLocation), metadata);
                            }
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return textures;
    }

    public static Map<Identifier, UploadedTexture> upload(Map<Identifier, UploadedTexture.@Nullable TextureMetadata> textureIds) {
        Map<Identifier, UploadedTexture> textures = new HashMap<>();

        for  (Map.Entry<Identifier, UploadedTexture.@Nullable TextureMetadata> entry : textureIds.entrySet()) {
            Identifier textureId = entry.getKey();

            String location = String.format("assets/%s/textures/%s", textureId.namespace, textureId.path);

            try (InputStream inputStream = TextureLoader.class.getClassLoader().getResourceAsStream(location)) {
                if (inputStream == null) {
                    throw new RuntimeException("Resource file not found: " + location);
                }

                byte[] bytes = inputStream.readAllBytes();

                Raylib.Image image = Raylib.LoadImageFromMemory(".png", bytes, bytes.length);
                Raylib.Texture texture = Raylib.LoadTextureFromImage(image);
                Raylib.UnloadImage(image);

                if (entry.getValue() == null) {
                    textures.put(textureId, new UploadedTexture(texture, UploadedTexture.TextureType.NONE, null));
                } else {
                    textures.put(textureId, new UploadedTexture(texture, entry.getValue().type(), entry.getValue()));
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to read texture asset from resources", e);
            }
        }

        return textures;
    }

    private static boolean assetHasMetadata(Path path) {
        int index = path.toString().lastIndexOf(".");
        String extension = path.toString().substring(index + 1);

        if (extension.equals("png")) {
            return Paths.get(path + ".json").toFile().exists();
        }

        return false;
    }
}