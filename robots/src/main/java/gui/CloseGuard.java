package gui;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

/**
 * Не даёт закрыть окно без подтверждения пользователя.
 */
public class CloseGuard
{
    static final String DIALOG_TITLE = "Подтверждение";

    private final Confirmer confirmer;

    public CloseGuard(Confirmer confirmer)
    {
        this.confirmer = confirmer;
    }

    /**
     * Задаёт вопрос и выполняет действие только если пользователь согласился.
     * @return true, если действие выполнено
     */
    public boolean confirmAndRun(Component parent, String message, Runnable action)
    {
        if (!confirmer.confirm(parent, message, DIALOG_TITLE))
        {
            return false;
        }
        action.run();
        return true;
    }

    /**
     * Внутреннее окно закрывается только после подтверждения.
     */
    public void guard(JInternalFrame frame)
    {
        frame.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
        frame.addInternalFrameListener(new InternalFrameAdapter()
        {
            @Override
            public void internalFrameClosing(InternalFrameEvent e)
            {
                confirmAndRun(frame, "Закрыть окно «" + frame.getTitle() + "»?", frame::dispose);
            }
        });
    }

    /**
     * Главное окно закрывается (выполняется exitAction) только после подтверждения.
     */
    public void guard(JFrame frame, Runnable exitAction)
    {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                confirmAndRun(frame, "Выйти из приложения?", exitAction);
            }
        });
    }
}