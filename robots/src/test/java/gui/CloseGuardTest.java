package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JInternalFrame;
import javax.swing.WindowConstants;

import org.junit.jupiter.api.Test;

class CloseGuardTest {

    private static final Confirmer YES = (parent, message, title) -> true;
    private static final Confirmer NO = (parent, message, title) -> false;

    @Test
    void actionRunsWhenUserConfirms() {
        int[] runs = {0};

        boolean result = new CloseGuard(YES).confirmAndRun(null, "Закрыть?", () -> runs[0]++);

        assertTrue(result);
        assertEquals(1, runs[0]);
    }

    @Test
    void actionDoesNotRunWhenUserRefuses() {
        int[] runs = {0};

        boolean result = new CloseGuard(NO).confirmAndRun(null, "Закрыть?", () -> runs[0]++);

        assertFalse(result);
        assertEquals(0, runs[0]);
    }

    @Test
    void refusedCloseKeepsInternalFrameOpen() {
        JInternalFrame frame = newFrame("Окно");
        new CloseGuard(NO).guard(frame);

        frame.doDefaultCloseAction();

        assertFalse(frame.isClosed());
    }

    @Test
    void confirmedCloseClosesInternalFrame() {
        JInternalFrame frame = newFrame("Окно");
        new CloseGuard(YES).guard(frame);

        frame.doDefaultCloseAction();

        assertTrue(frame.isClosed());
    }

    @Test
    void questionContainsWindowTitle() {
        List<String> messages = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        CloseGuard guard = new CloseGuard((parent, message, title) -> {
            messages.add(message);
            titles.add(title);
            return false;
        });
        JInternalFrame frame = newFrame("Протокол работы");
        guard.guard(frame);

        frame.doDefaultCloseAction();

        assertEquals(List.of("Закрыть окно «Протокол работы»?"), messages);
        assertEquals(List.of(CloseGuard.DIALOG_TITLE), titles);
    }

    @Test
    void guardDisablesImmediateClose() {
        JInternalFrame frame = newFrame("Окно");

        new CloseGuard(YES).guard(frame);

        assertEquals(WindowConstants.DO_NOTHING_ON_CLOSE, frame.getDefaultCloseOperation());
    }

    private static JInternalFrame newFrame(String title) {
        return new JInternalFrame(title, true, true, true, true);
    }
}