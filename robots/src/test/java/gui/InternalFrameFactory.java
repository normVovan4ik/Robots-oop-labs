package gui;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

/**
 * Вспомогательные методы для тестов с внутренними окнами.
 */
final class InternalFrameFactory {

    private InternalFrameFactory() {
    }

    /**
     * Создаёт окно внутри рабочего стола: без него окно нельзя развернуть и свернуть.
     */
    static JInternalFrame newFrame(JDesktopPane desktop, int x, int y, int width, int height) {
        desktop.setSize(800, 600);
        JInternalFrame frame = new JInternalFrame("Окно", true, true, true, true);
        frame.setBounds(x, y, width, height);
        desktop.add(frame);
        return frame;
    }
}   