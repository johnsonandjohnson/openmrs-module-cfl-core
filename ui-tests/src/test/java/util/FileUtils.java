package util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class FileUtils {

    public static String readFile(String resource) {
        try {
            String path = FileUtils.class.getClassLoader().getResource(resource).getPath();
            return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Unable to read file %s", resource), e);
        }
    }

    private FileUtils() {
    }
}
