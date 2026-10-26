package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Rectangle;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import org.junit.jupiter.api.Test;

class WindowStateTest {

    private final JDesktopPane desktop = new JDesktopPane();

    @Test
    void captureReadsPositionAndSize() {
        JInternalFrame frame = InternalFrameFactory.newFrame(desktop, 10, 20, 300, 200);

        WindowState state = WindowState.capture(frame);

        assertEquals(new WindowState(10, 20, 300, 200, false, false, false), state);
    }

    @Test
    void captureOfMaximizedWindowKeepsNormalBounds() throws Exception {
        JInternalFrame frame = InternalFrameFactory.newFrame(desktop, 10, 20, 300, 200);
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
        JInternalFrame iconified = InternalFrameFactory.newFrame(desktop, 0, 0, 100, 100);
        iconified.setIcon(true);
        JInternalFrame closed = InternalFrameFactory.newFrame(desktop, 0, 0, 100, 100);
        closed.setClosed(true);

        assertTrue(WindowState.capture(iconified).icon());
        assertFalse(WindowState.capture(iconified).closed());
        assertTrue(WindowState.capture(closed).closed());
    }

    @Test
    void applyMovesAndResizesWindow() {
        JInternalFrame frame = InternalFrameFactory.newFrame(desktop, 0, 0, 100, 100);

        new WindowState(10, 20, 300, 200, false, false, false).applyTo(frame);

        assertEquals(new Rectangle(10, 20, 300, 200), frame.getBounds());
    }

    @Test
    void applyMaximizesWindowAndRemembersNormalBounds() {
        JInternalFrame frame = InternalFrameFactory.newFrame(desktop, 0, 0, 100, 100);

        new WindowState(10, 20, 300, 200, true, false, false).applyTo(frame);

        assertTrue(frame.isMaximum());
        assertEquals(new Rectangle(10, 20, 300, 200), frame.getNormalBounds());
    }

    @Test
    void applyIconifiesWindow() {
        JInternalFrame frame = InternalFrameFactory.newFrame(desktop, 0, 0, 100, 100);

        new WindowState(10, 20, 300, 200, false, true, false).applyTo(frame);

        assertTrue(frame.isIcon());
    }

    @Test
    void applyClosesWindow() {
        JInternalFrame frame = InternalFrameFactory.newFrame(desktop, 0, 0, 100, 100);

        new WindowState(10, 20, 300, 200, false, false, true).applyTo(frame);

        assertTrue(frame.isClosed());
    }

    @Test
    void capturedStateAppliedToAnotherWindowGivesSameState() throws Exception {
        JInternalFrame source = InternalFrameFactory.newFrame(desktop, 15, 25, 320, 210);
        source.setIcon(true);
        WindowState state = WindowState.capture(source);
        JInternalFrame target = InternalFrameFactory.newFrame(desktop, 0, 0, 100, 100);

        state.applyTo(target);

        assertEquals(state, WindowState.capture(target));
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
}