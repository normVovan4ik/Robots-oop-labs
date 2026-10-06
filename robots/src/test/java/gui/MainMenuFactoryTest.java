package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import log.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MainMenuFactoryTest
{
    private List<String> requestedLookAndFeels;
    private JMenuBar menuBar;

    @BeforeEach
    void setUp()
    {
        requestedLookAndFeels = new ArrayList<>();
        menuBar = new MainMenuFactory(requestedLookAndFeels::add).createMenuBar();
    }

    @Test
    void menuBarContainsTwoMenus()
    {
        assertEquals(2, menuBar.getMenuCount());
        assertEquals("Режим отображения", menuBar.getMenu(0).getText());
        assertEquals("Тесты", menuBar.getMenu(1).getText());
    }

    @Test
    void systemLookAndFeelItemRequestsSystemScheme()
    {
        click(menuBar.getMenu(0).getItem(0));

        assertEquals(List.of(UIManager.getSystemLookAndFeelClassName()), requestedLookAndFeels);
    }

    @Test
    void crossPlatformLookAndFeelItemRequestsCrossPlatformScheme()
    {
        click(menuBar.getMenu(0).getItem(1));

        assertEquals(List.of(UIManager.getCrossPlatformLookAndFeelClassName()), requestedLookAndFeels);
    }

    @Test
    void logMessageItemAddsMessageToDefaultLog()
    {
        int sizeBefore = Logger.getDefaultLogSource().size();

        click(menuBar.getMenu(1).getItem(0));

        assertEquals(sizeBefore + 1, Logger.getDefaultLogSource().size());
    }

    @Test
    void mnemonicsInsideOneMenuAreUnique()
    {
        JMenu menu = menuBar.getMenu(0);

        assertNotEquals(menu.getItem(0).getMnemonic(), menu.getItem(1).getMnemonic());
    }

    private static void click(JMenuItem item)
    {
        item.getActionListeners()[0].actionPerformed(
                new ActionEvent(item, ActionEvent.ACTION_PERFORMED, ""));
    }
}