package gui;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class GameWindow extends JInternalFrame
{
    private final GameVisualizer m_visualizer;
    public GameWindow()
    {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer(new SwingConfirmer());
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        // Клавиши управления получает панель с игрой, поэтому отдаём ей фокус при активации окна.
        addInternalFrameListener(new InternalFrameAdapter()
        {
            @Override
            public void internalFrameActivated(InternalFrameEvent e)
            {
                m_visualizer.requestFocusInWindow();
            }
        });
    }
}