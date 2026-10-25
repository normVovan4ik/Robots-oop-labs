package gui;

import java.awt.Rectangle;

import javax.swing.JInternalFrame;

/**
 * Состояние внутреннего окна: положение, размер, развёрнутость, свёрнутость и закрытость.
 */
public record WindowState(int x, int y, int width, int height,
                          boolean maximum, boolean icon, boolean closed)
{
    private static final int VALUES_COUNT = 7;

    /**
     * Запоминает текущее состояние окна.
     */
    public static WindowState capture(JInternalFrame frame)
    {
        // У развёрнутого окна запоминаем обычное положение, а не размеры на весь экран
        Rectangle bounds = frame.isMaximum() ? frame.getNormalBounds() : frame.getBounds();
        return new WindowState(bounds.x, bounds.y, bounds.width, bounds.height,
                frame.isMaximum(), frame.isIcon(), frame.isClosed());
    }

    /**
     * Состояние одной строкой вида «10,20,300,200,false,false,false».
     */
    public String toLine()
    {
        return String.join(",", String.valueOf(x), String.valueOf(y),
                String.valueOf(width), String.valueOf(height),
                String.valueOf(maximum), String.valueOf(icon), String.valueOf(closed));
    }

    /**
     * Читает состояние из строки, которую записал {@link #toLine()}.
     * @throws IllegalArgumentException если строка записана неправильно
     */
    public static WindowState fromLine(String line)
    {
        String[] parts = line.split(",", -1);
        if (parts.length != VALUES_COUNT)
        {
            throw new IllegalArgumentException("Ожидалось значений: " + VALUES_COUNT + ", строка: " + line);
        }
        return new WindowState(
                Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()),
                Integer.parseInt(parts[2].trim()), Integer.parseInt(parts[3].trim()),
                parseBoolean(parts[4]), parseBoolean(parts[5]), parseBoolean(parts[6]));
    }

    private static boolean parseBoolean(String text)
    {
        String value = text.trim();
        if (value.equals("true"))
        {
            return true;
        }
        if (value.equals("false"))
        {
            return false;
        }
        throw new IllegalArgumentException("Ожидалось true или false: " + text);
    }
}