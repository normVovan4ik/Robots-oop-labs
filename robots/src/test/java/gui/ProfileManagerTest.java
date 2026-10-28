package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Rectangle;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ProfileManagerTest {

    @TempDir
    Path dir;

    private static final Confirmer YES = (parent, message, title) -> true;
    private static final Confirmer NO = (parent, message, title) -> false;

    private final JDesktopPane desktop = new JDesktopPane();

    @Test
    void saveWritesStateOfEveryWindow() throws IOException {
        ProfileStorage storage = storage();
        Map<String, JInternalFrame> windows = new LinkedHashMap<>();
        windows.put("log", InternalFrameFactory.newFrame(desktop, 10, 20, 300, 800));
        windows.put("game", InternalFrameFactory.newFrame(desktop, 320, 40, 400, 400));

        new ProfileManager(storage).save(windows);

        Map<String, WindowState> saved = storage.load().orElseThrow();
        assertEquals(new WindowState(10, 20, 300, 800, false, false, false), saved.get("log"));
        assertEquals(new WindowState(320, 40, 400, 400, false, false, false), saved.get("game"));
    }

    @Test
    void closedWindowIsSavedAsClosed() throws Exception {
        ProfileStorage storage = storage();
        JInternalFrame frame = InternalFrameFactory.newFrame(desktop, 10, 20, 300, 800);
        frame.setClosed(true);

        new ProfileManager(storage).save(Map.of("log", frame));

        assertTrue(storage.load().orElseThrow().get("log").closed());
    }

    @Test
    void restoreAppliesSavedStateToWindows() throws IOException {
        ProfileManager manager = new ProfileManager(storage());
        manager.save(Map.of("log", InternalFrameFactory.newFrame(desktop, 10, 20, 300, 800)));
        JInternalFrame fresh = InternalFrameFactory.newFrame(desktop, 0, 0, 100, 100);

        boolean restored = manager.restore(Map.of("log", fresh));

        assertTrue(restored);
        assertEquals(new Rectangle(10, 20, 300, 800), fresh.getBounds());
    }

    @Test
    void restoreReturnsFalseWhenThereIsNoProfile() {
        JInternalFrame frame = InternalFrameFactory.newFrame(desktop, 5, 6, 100, 100);

        boolean restored = new ProfileManager(storage()).restore(Map.of("log", frame));

        assertFalse(restored);
        assertEquals(new Rectangle(5, 6, 100, 100), frame.getBounds());
    }

    @Test
    void restoreLeavesWindowsThatAreNotInProfileAsTheyAre() throws IOException {
        ProfileManager manager = new ProfileManager(storage());
        manager.save(Map.of("log", InternalFrameFactory.newFrame(desktop, 10, 20, 300, 800)));
        JInternalFrame log = InternalFrameFactory.newFrame(desktop, 0, 0, 100, 100);
        JInternalFrame other = InternalFrameFactory.newFrame(desktop, 7, 8, 120, 130);

        manager.restore(Map.of("log", log, "other", other));

        assertEquals(new Rectangle(7, 8, 120, 130), other.getBounds());
    }

    @Test
    void restoreSkipsProfileEntriesForUnknownWindows() throws IOException {
        ProfileManager manager = new ProfileManager(storage());
        manager.save(Map.of("ghost", InternalFrameFactory.newFrame(desktop, 10, 20, 300, 800)));
        JInternalFrame log = InternalFrameFactory.newFrame(desktop, 5, 6, 100, 100);

        boolean restored = manager.restore(Map.of("log", log));

        assertTrue(restored);
        assertEquals(new Rectangle(5, 6, 100, 100), log.getBounds());
    }

    @Test
    void restoreClosesWindowThatWasClosedAtExit() throws Exception {
        ProfileManager manager = new ProfileManager(storage());
        JInternalFrame closedBefore = InternalFrameFactory.newFrame(desktop, 10, 20, 300, 800);
        closedBefore.setClosed(true);
        manager.save(Map.of("log", closedBefore));
        JInternalFrame fresh = InternalFrameFactory.newFrame(desktop, 0, 0, 100, 100);

        manager.restore(Map.of("log", fresh));

        assertTrue(fresh.isClosed());
    }

    @Test
    void noQuestionIsAskedWhenThereIsNoProfile() {
        List<String> questions = new ArrayList<>();
        JInternalFrame frame = InternalFrameFactory.newFrame(desktop, 5, 6, 100, 100);

        boolean restored = new ProfileManager(storage()).restoreIfConfirmed(
                Map.of("log", frame), (parent, message, title) -> questions.add(message), null);

        assertFalse(restored);
        assertTrue(questions.isEmpty());
    }

    @Test
    void noQuestionIsAskedWhenProfileFileIsBroken() throws IOException {
        Files.writeString(dir.resolve("profile.properties"), "log=это не профиль\n");
        List<String> questions = new ArrayList<>();
        JInternalFrame frame = InternalFrameFactory.newFrame(desktop, 5, 6, 100, 100);

        boolean restored = new ProfileManager(storage()).restoreIfConfirmed(
                Map.of("log", frame), (parent, message, title) -> questions.add(message), null);

        assertFalse(restored);
        assertTrue(questions.isEmpty());
    }

    @Test
    void questionWarnsAboutSavedProfile() throws IOException {
        ProfileManager manager = new ProfileManager(storage());
        manager.save(Map.of("log", InternalFrameFactory.newFrame(desktop, 10, 20, 300, 800)));
        List<String> shown = new ArrayList<>();

        manager.restoreIfConfirmed(Map.of("log", InternalFrameFactory.newFrame(desktop, 0, 0, 100, 100)),
                (parent, message, title) -> {
                    shown.add(message);
                    shown.add(title);
                    return false;
                }, null);

        assertEquals(List.of("Найден сохранённый профиль окон. Восстановить его?", "Профиль"), shown);
    }

    @Test
    void confirmedQuestionRestoresWindows() throws IOException {
        ProfileManager manager = new ProfileManager(storage());
        manager.save(Map.of("log", InternalFrameFactory.newFrame(desktop, 10, 20, 300, 800)));
        JInternalFrame fresh = InternalFrameFactory.newFrame(desktop, 0, 0, 100, 100);

        boolean restored = manager.restoreIfConfirmed(Map.of("log", fresh), YES, null);

        assertTrue(restored);
        assertEquals(new Rectangle(10, 20, 300, 800), fresh.getBounds());
    }

    @Test
    void declinedQuestionLeavesWindowsAsTheyAre() throws IOException {
        ProfileManager manager = new ProfileManager(storage());
        manager.save(Map.of("log", InternalFrameFactory.newFrame(desktop, 10, 20, 300, 800)));
        JInternalFrame fresh = InternalFrameFactory.newFrame(desktop, 0, 0, 100, 100);

        boolean restored = manager.restoreIfConfirmed(Map.of("log", fresh), NO, null);

        assertFalse(restored);
        assertEquals(new Rectangle(0, 0, 100, 100), fresh.getBounds());
    }

    private ProfileStorage storage() {
        return new ProfileStorage(dir.resolve("profile.properties"));
    }
}