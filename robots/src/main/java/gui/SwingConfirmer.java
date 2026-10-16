package gui;

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 * Показывает окно с вопросом и кнопками «Да» и «Нет».
 */
public class SwingConfirmer implements Confirmer
{
    @Override
    public boolean confirm(Component parent, String message, String title)
    {
        Object[] options = {"Да", "Нет"};
        int answer = JOptionPane.showOptionDialog(parent, message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[1]);
        return answer == 0;
    }
}