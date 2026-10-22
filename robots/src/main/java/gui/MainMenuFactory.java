package gui;

import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import log.Logger;

/**
 * Создаёт главное меню приложения.
 */
public class MainMenuFactory
{
    private final Consumer<String> lookAndFeelSwitcher;
    private final Runnable exitAction;

    /**
     * @param lookAndFeelSwitcher действие, которое переключает схему оформления
     *                            по имени класса Look and Feel
     * @param exitAction          действие пункта «Выйти»
     */
    public MainMenuFactory(Consumer<String> lookAndFeelSwitcher, Runnable exitAction)
    {
        this.lookAndFeelSwitcher = lookAndFeelSwitcher;
        this.exitAction = exitAction;
    }

    public JMenuBar createMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());
        return menuBar;
    }

    private JMenu createFileMenu()
    {
        JMenu menu = createMenu("Файл", KeyEvent.VK_F, "Управление приложением");
        menu.add(createItem("Выйти", KeyEvent.VK_X, exitAction));
        return menu;
    }

    private JMenu createLookAndFeelMenu()
    {
        JMenu menu = createMenu("Режим отображения", KeyEvent.VK_V,
                "Управление режимом отображения приложения");
        menu.add(createLookAndFeelItem("Системная схема", KeyEvent.VK_S,
                UIManager.getSystemLookAndFeelClassName()));
        menu.add(createLookAndFeelItem("Универсальная схема", KeyEvent.VK_U,
                UIManager.getCrossPlatformLookAndFeelClassName()));
        return menu;
    }

    private JMenu createTestMenu()
    {
        JMenu menu = createMenu("Тесты", KeyEvent.VK_T, "Тестовые команды");
        menu.add(createItem("Сообщение в лог", KeyEvent.VK_S, () -> Logger.debug("Новая строка")));
        return menu;
    }

    private JMenu createMenu(String title, int mnemonic, String description)
    {
        JMenu menu = new JMenu(title);
        menu.setMnemonic(mnemonic);
        menu.getAccessibleContext().setAccessibleDescription(description);
        return menu;
    }

    private JMenuItem createLookAndFeelItem(String title, int mnemonic, String lookAndFeelClassName)
    {
        return createItem(title, mnemonic, () -> lookAndFeelSwitcher.accept(lookAndFeelClassName));
    }

    /**
     * Единственное место, где создаётся пункт меню с действием.
     */
    private JMenuItem createItem(String title, int mnemonic, Runnable action)
    {
        JMenuItem item = new JMenuItem(title, mnemonic);
        item.addActionListener((event) -> action.run());
        return item;
    }
}   