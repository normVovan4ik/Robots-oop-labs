package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private int exitRequests;
    private JMenuBar menuBar;

    @BeforeEach
    void setUp()
    {
        requestedLookAndFeels = new ArrayList<>();
        exitRequests = 0;
        menuBar = new MainMenuFactory(requestedLookAndFeels::add, () -> exitRequests++).createMenuBar();
    }

    @Test
    void menuBarContainsThreeMenusInExpectedOrder()
    {
        List<String> titles = new ArrayList<>();
        for (int i = 0; i < menuBar.getMenuCount(); i++)
        {
            titles.add(menuBar.getMenu(i).getText());
        }

        assertEquals(List.of("Файл", "Режим отображения", "Тесты"), titles);
    }

    @Test
    void fileMenuContainsExitItem()
    {
        JMenu fileMenu = menu("Файл");

        assertEquals(1, fileMenu.getItemCount());
        assertEquals("Выйти", fileMenu.getItem(0).getText());
    }

    @Test
    void exitItemRunsExitActionOnlyWhenClicked()
    {
        assertEquals(0, exitRequests);

        click(menu("Файл").getItem(0));

        assertEquals(1, exitRequests);
    }

    @Test
    void systemLookAndFeelItemRequestsSystemScheme()
    {
        click(menu("Режим отображения").getItem(0));

        assertEquals(List.of(UIManager.getSystemLookAndFeelClassName()), requestedLookAndFeels);
    }

    @Test
    void crossPlatformLookAndFeelItemRequestsCrossPlatformScheme()
    {
        click(menu("Режим отображения").getItem(1));

        assertEquals(List.of(UIManager.getCrossPlatformLookAndFeelClassName()), requestedLookAndFeels);
    }

    @Test
    void logMessageItemAddsMessageToDefaultLog()
    {
        int sizeBefore = Logger.getDefaultLogSource().size();

        click(menu("Тесты").getItem(0));

        assertEquals(sizeBefore + 1, Logger.getDefaultLogSource().size());
    }

    @Test
    void mnemonicsInsideOneMenuAreUnique()
    {
        JMenu menu = menu("Режим отображения");

        assertNotEquals(menu.getItem(0).getMnemonic(), menu.getItem(1).getMnemonic());
    }

    @Test
    void topLevelMenuMnemonicsAreUnique()
    {
        Set<Integer> mnemonics = new HashSet<>();
        for (int i = 0; i < menuBar.getMenuCount(); i++)
        {
            mnemonics.add(menuBar.getMenu(i).getMnemonic());
        }

        assertEquals(menuBar.getMenuCount(), mnemonics.size());
    }

    private JMenu menu(String title)
    {
        for (int i = 0; i < menuBar.getMenuCount(); i++)
        {
            if (menuBar.getMenu(i).getText().equals(title))
            {
                return menuBar.getMenu(i);
            }
        }
        throw new IllegalStateException("Нет меню: " + title);
    }

    private static void click(JMenuItem item)
    {
        item.getActionListeners()[0].actionPerformed(
                new ActionEvent(item, ActionEvent.ACTION_PERFORMED, ""));
    }
}