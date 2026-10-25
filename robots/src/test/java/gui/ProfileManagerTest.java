package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ProfileManagerTest {

    @TempDir
    Path dir;

    private final JDesktopPane desktop = new JDesktopPane();

    @Test
    void saveWritesStateOfEveryWindow() throws IOException {
        ProfileStorage storage = new ProfileStorage(dir.resolve("profile.properties"));
        Map<String, JInternalFrame> windows = new LinkedHashMap<>();
        windows.put("log", newFrame(10, 20, 300, 800));
        windows.put("game", newFrame(320, 40, 400, 400));

        new ProfileManager(storage).save(windows);

        Map<String, WindowState> saved = storage.load().orElseThrow();
        assertEquals(new WindowState(10, 20, 300, 800, false, false, false), saved.get("log"));
        assertEquals(new WindowState(320, 40, 400, 400, false, false, false), saved.get("game"));
    }

    @Test
    void closedWindowIsSavedAsClosed() throws Exception {
        ProfileStorage storage = new ProfileStorage(dir.resolve("profile.properties"));
        JInternalFrame frame = newFrame(10, 20, 300, 800);
        frame.setClosed(true);

        new ProfileManager(storage).save(Map.of("log", frame));

        assertTrue(storage.load().orElseThrow().get("log").closed());
    }

    private JInternalFrame newFrame(int x, int y, int width, int height) {
        desktop.setSize(800, 600);
        JInternalFrame frame = new JInternalFrame("Окно", true, true, true, true);
        frame.setBounds(x, y, width, height);
        desktop.add(frame);
        return frame;
    }
}