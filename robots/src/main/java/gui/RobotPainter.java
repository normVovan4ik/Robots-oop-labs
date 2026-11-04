package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Рисует робота и точку цели. Размеры тела берёт у RobotModel, поэтому нарисованный робот
 * совпадает с тем, что модель считает его телом (по нему определяется касание стенки).
 */
public final class RobotPainter
{
    private static final int EYE_OFFSET = 10;
    private static final int EYE_SIZE = 5;
    private static final int TARGET_SIZE = 5;

    private RobotPainter()
    {
    }

    /**
     * Рисует робота, повёрнутого по его направлению.
     * Поворот добавляется к преобразованиям, которые уже стоят у graphics (положение панели,
     * масштаб экрана), и действует только внутри этого метода.
     */
    public static void drawRobot(Graphics2D graphics, RobotModel robot, Color color)
    {
        int x = round(robot.getPositionX());
        int y = round(robot.getPositionY());
        int length = (int) (2 * RobotModel.ROBOT_HALF_SIZE);
        int width = (int) (2 * RobotModel.ROBOT_HALF_WIDTH);

        Graphics2D g = (Graphics2D) graphics.create();
        g.rotate(robot.getDirection(), x, y);
        g.setColor(color);
        fillOval(g, x, y, length, width);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, length, width);
        g.setColor(Color.WHITE);
        fillOval(g, x + EYE_OFFSET, y, EYE_SIZE, EYE_SIZE);
        g.setColor(Color.BLACK);
        drawOval(g, x + EYE_OFFSET, y, EYE_SIZE, EYE_SIZE);
        g.dispose();
    }

    /**
     * Рисует точку цели.
     */
    public static void drawTarget(Graphics2D g, int x, int y)
    {
        g.setColor(Color.GREEN);
        fillOval(g, x, y, TARGET_SIZE, TARGET_SIZE);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, TARGET_SIZE, TARGET_SIZE);
    }

    private static int round(double value)
    {
        return (int) Math.round(value);
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }
}