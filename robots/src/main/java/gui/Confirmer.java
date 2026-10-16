package gui;

import java.awt.Component;

/**
 * Спрашивает у пользователя подтверждение действия.
 * Интерфейс нужен, чтобы логику закрытия можно было проверять тестами без диалоговых окон.
 */
@FunctionalInterface
public interface Confirmer
{
    /**
     * @param parent  компонент, над которым показывается вопрос
     * @param message текст вопроса
     * @param title   заголовок окна
     * @return true, если пользователь согласился
     */
    boolean confirm(Component parent, String message, String title);
}