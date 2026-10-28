package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import log.Logger;

public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final Confirmer confirmer = new SwingConfirmer();
    private final CloseGuard closeGuard = new CloseGuard(confirmer);
    /** Внутренние окна приложения по именам: из них собирается профиль. */
    private final Map<String, JInternalFrame> windows = new LinkedHashMap<>();
    private final ProfileManager profileManager =
            new ProfileManager(new ProfileStorage(ProfileStorage.defaultFile()));

    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);

        LogWindow logWindow = createLogWindow();
        addWindow("log", logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow("game", gameWindow);

        setJMenuBar(new MainMenuFactory(this::switchLookAndFeel, this::requestClose).createMenuBar());
        closeGuard.guard(this, this::exitApplication);
    }

    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(String name, JInternalFrame frame)
    {
        windows.put(name, frame);
        closeGuard.guard(frame);
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    /**
     * Если есть профиль, сохранённый при прошлом выходе, предлагает вернуть окна в то состояние.
     */
    public void restoreProfile()
    {
        profileManager.restoreIfConfirmed(windows, confirmer, this);
    }

    private void requestClose()
    {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private void exitApplication()
    {
        saveProfile();
        dispose();
        System.exit(0);
    }

    private void saveProfile()
    {
        try
        {
            profileManager.save(windows);
        }
        catch (IOException e)
        {
            Logger.error("Не удалось сохранить профиль: " + e.getMessage());
        }
    }

    private void switchLookAndFeel(String className)
    {
        setLookAndFeel(className);
        this.invalidate();
    }

    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
               | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
}