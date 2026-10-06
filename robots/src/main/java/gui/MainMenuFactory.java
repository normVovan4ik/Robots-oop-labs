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

    /**
     * @param lookAndFeelSwitcher действие, которое переключает схему оформления
     *                            по имени класса Look and Feel
     */
    public MainMenuFactory(Consumer<String> lookAndFeelSwitcher)
    {
        this.lookAndFeelSwitcher = lookAndFeelSwitcher;
    }

    public JMenuBar createMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());
        return menuBar;
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
        menu.add(createLogMessageItem());
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
        JMenuItem item = new JMenuItem(title, mnemonic);
        item.addActionListener((event) -> lookAndFeelSwitcher.accept(lookAndFeelClassName));
        return item;
    }

    private JMenuItem createLogMessageItem()
    {
        JMenuItem item = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        item.addActionListener((event) -> Logger.debug("Новая строка"));
        return item;
    }
}