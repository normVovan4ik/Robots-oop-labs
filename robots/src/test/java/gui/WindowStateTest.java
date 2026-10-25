package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import org.junit.jupiter.api.Test;

class WindowStateTest {

    private final JDesktopPane desktop = new JDesktopPane();

    @Test
    void captureReadsPositionAndSize() {
        JInternalFrame frame = newFrame();
        frame.setBounds(10, 20, 300, 200);

        WindowState state = WindowState.capture(frame);

        assertEquals(new WindowState(10, 20, 300, 200, false, false, false), state);
    }

    @Test
    void captureOfMaximizedWindowKeepsNormalBounds() throws Exception {
        JInternalFrame frame = newFrame();
        frame.setBounds(10, 20, 300, 200);
        frame.setMaximum(true);

        WindowState state = WindowState.capture(frame);

        assertTrue(state.maximum());
        assertEquals(10, state.x());
        assertEquals(20, state.y());
        assertEquals(300, state.width());
        assertEquals(200, state.height());
    }

    @Test
    void captureReadsIconifiedAndClosedState() throws Exception {
        JInternalFrame iconified = newFrame();
        iconified.setIcon(true);
        JInternalFrame closed = newFrame();
        closed.setClosed(true);

        assertTrue(WindowState.capture(iconified).icon());
        assertFalse(WindowState.capture(iconified).closed());
        assertTrue(WindowState.capture(closed).closed());
    }

    @Test
    void lineRoundTripGivesSameState() {
        WindowState state = new WindowState(1, 2, 3, 4, true, false, true);

        assertEquals("1,2,3,4,true,false,true", state.toLine());
        assertEquals(state, WindowState.fromLine(state.toLine()));
    }

    @Test
    void wrongNumberOfValuesIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> WindowState.fromLine("1,2,3"));
    }

    @Test
    void notANumberIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> WindowState.fromLine("a,2,3,4,false,false,false"));
    }

    @Test
    void notABooleanIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> WindowState.fromLine("1,2,3,4,yes,false,false"));
    }

    private JInternalFrame newFrame() {
        desktop.setSize(800, 600);
        JInternalFrame frame = new JInternalFrame("Окно", true, true, true, true);
        desktop.add(frame);
        return frame;
    }
}