package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ProfileStorageTest {

    @TempDir
    Path dir;

    private static final WindowState LOG = new WindowState(10, 10, 300, 800, false, false, false);
    private static final WindowState GAME = new WindowState(320, 40, 400, 400, true, false, true);

    @Test
    void savedProfileCanBeLoaded() throws IOException {
        ProfileStorage storage = new ProfileStorage(dir.resolve("profile.properties"));

        storage.save(Map.of("log", LOG, "game", GAME));

        assertEquals(Optional.of(Map.of("log", LOG, "game", GAME)), storage.load());
    }

    @Test
    void missingFileMeansNoProfile() {
        ProfileStorage storage = new ProfileStorage(dir.resolve("profile.properties"));

        assertFalse(storage.load().isPresent());
    }

    @Test
    void brokenFileMeansNoProfile() throws IOException {
        Path file = dir.resolve("profile.properties");
        Files.writeString(file, "log=это не профиль\n");

        assertFalse(new ProfileStorage(file).load().isPresent());
    }

    @Test
    void emptyFileMeansNoProfile() throws IOException {
        Path file = dir.resolve("profile.properties");
        Files.writeString(file, "");

        assertFalse(new ProfileStorage(file).load().isPresent());
    }

    @Test
    void saveCreatesMissingFolders() throws IOException {
        Path file = dir.resolve("a").resolve("b").resolve("profile.properties");

        new ProfileStorage(file).save(Map.of("log", LOG));

        assertTrue(Files.isRegularFile(file));
    }

    @Test
    void saveReplacesOldProfile() throws IOException {
        ProfileStorage storage = new ProfileStorage(dir.resolve("profile.properties"));
        storage.save(Map.of("log", LOG));

        storage.save(Map.of("game", GAME));

        assertEquals(Optional.of(Map.of("game", GAME)), storage.load());
    }
}