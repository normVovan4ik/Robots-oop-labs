package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

class RobotPainterTest {

    private static final int BODY_COLOR = 0xFF00FF;   // Color.MAGENTA
    private static final int TARGET_COLOR = 0x00FF00; // Color.GREEN

    private final BufferedImage image = new BufferedImage(600, 400, BufferedImage.TYPE_INT_RGB);
    private final Graphics2D graphics = image.createGraphics();

    RobotPainterTest() {
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
    }

    @Test
    void robotIsDrawnAtItsPosition() {
        RobotPainter.drawRobot(graphics, new RobotModel(200, 100, 0.1, 0.001), Color.MAGENTA);

        double[] center = centerOf(BODY_COLOR);
        assertEquals(200, center[0], 3);
        assertEquals(100, center[1], 3);
    }

    @Test
    void robotFollowsScreenScale() {
        graphics.scale(1.5, 1.5); // как при масштабе экрана 150%

        RobotPainter.drawRobot(graphics, new RobotModel(200, 100, 0.1, 0.001), Color.MAGENTA);

        double[] center = centerOf(BODY_COLOR);
        assertEquals(300, center[0], 4);
        assertEquals(150, center[1], 4);
    }

    @Test
    void targetFollowsScreenScale() {
        graphics.scale(1.5, 1.5);

        RobotPainter.drawTarget(graphics, 100, 80);

        double[] center = centerOf(TARGET_COLOR);
        assertEquals(150, center[0], 3);
        assertEquals(120, center[1], 3);
    }

    @Test
    void drawingDoesNotChangeTransformOfCallerGraphics() {
        graphics.scale(1.5, 1.5);
        AffineTransform before = graphics.getTransform();

        RobotPainter.drawRobot(graphics, new RobotModel(), Color.MAGENTA);

        assertEquals(before, graphics.getTransform());
    }

    @Test
    void robotIsRotatedAccordingToItsDirection() {
        RobotModel robot = new RobotModel(200, 100, 0.1, 0.002);
        for (int i = 0; i < 79; i++) {
            robot.drive(1, false, 10); // поворачиваем на месте примерно на четверть круга
        }

        RobotPainter.drawRobot(graphics, robot, Color.MAGENTA);

        int[] box = boundingBoxOf(BODY_COLOR);
        int width = box[1] - box[0] + 1;
        int height = box[3] - box[2] + 1;
        assertTrue(height > 2 * width, "robot is not turned: " + width + "x" + height);
    }

    private double[] centerOf(int rgb) {
        long sumX = 0;
        long sumY = 0;
        long count = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if ((image.getRGB(x, y) & 0xFFFFFF) == rgb) {
                    sumX += x;
                    sumY += y;
                    count++;
                }
            }
        }
        assertTrue(count > 0, "nothing was drawn in color " + Integer.toHexString(rgb));
        return new double[] {sumX / (double) count, sumY / (double) count};
    }

    private int[] boundingBoxOf(int rgb) {
        int minX = Integer.MAX_VALUE;
        int maxX = -1;
        int minY = Integer.MAX_VALUE;
        int maxY = -1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if ((image.getRGB(x, y) & 0xFFFFFF) == rgb) {
                    minX = Math.min(minX, x);
                    maxX = Math.max(maxX, x);
                    minY = Math.min(minY, y);
                    maxY = Math.max(maxY, y);
                }
            }
        }
        return new int[] {minX, maxX, minY, maxY};
    }
}