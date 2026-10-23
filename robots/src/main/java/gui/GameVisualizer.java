package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class GameVisualizer extends JPanel
{
    private final Timer m_timer = initTimer();

    private static Timer initTimer()
    {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    private static final int PREDATOR_COUNT = 2;

    private final GameModel m_model = new GameModel(PREDATOR_COUNT);
    private final KeyboardControls m_controls = new KeyboardControls();
    private final Confirmer m_confirmer;

    /** Вопрос «Начать заново?» сейчас на экране. */
    private volatile boolean m_askingRestart = false;
    /** Пользователь отказался начинать заново: игра стоит, пока он снова не нажмёт на поле. */
    private volatile boolean m_restartDeclined = false;

    public GameVisualizer(Confirmer confirmer)
    {
        m_confirmer = confirmer;
        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onRedrawEvent();
            }
        }, 0, 50);
        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onModelUpdateEvent();
            }
        }, 0, 10);
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                requestFocusInWindow();
                m_restartDeclined = false;
                setTargetPosition(e.getPoint());
                repaint();
            }
        });
        setFocusable(true);
        addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                m_controls.keyPressed(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                m_controls.keyReleased(e.getKeyCode());
            }
        });
        addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusLost(FocusEvent e)
            {
                m_controls.releaseAll();
            }
        });
        setDoubleBuffered(true);
    }

    protected void setTargetPosition(Point p)
    {
        m_model.getRobot().setTarget(p.x, p.y);
    }

    protected void onRedrawEvent()
    {
        EventQueue.invokeLater(this::repaint);
    }

    protected void onModelUpdateEvent()
    {
        m_model.setFieldSize(getWidth(), getHeight());
        m_model.update(10, m_controls);
        if (m_model.isGameOver() && !m_askingRestart && !m_restartDeclined)
        {
            m_askingRestart = true;
            EventQueue.invokeLater(this::askRestart);
        }
    }

    /** Вопрос показывается в потоке событий Swing. */
    private void askRestart()
    {
        m_restartDeclined = !m_model.askRestart(m_confirmer, this);
        m_askingRestart = false;
    }

    private static int round(double value)
    {
        return (int)(value + 0.5);
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        for (RobotModel predator : m_model.getPredators())
        {
            drawRobot(g2d, predator, Color.RED);
        }
        RobotModel robot = m_model.getRobot();
        drawRobot(g2d, robot, Color.MAGENTA);
        if (!robot.isManual())
        {
            drawTarget(g2d, robot.getTargetX(), robot.getTargetY());
        }
        if (m_model.isGameOver())
        {
            drawGameOver(g2d);
        }
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, RobotModel robot, Color color)
    {
        int x = round(robot.getPositionX());
        int y = round(robot.getPositionY());
        AffineTransform t = AffineTransform.getRotateInstance(robot.getDirection(), x, y);
        g.setTransform(t);
        g.setColor(color);
        fillOval(g, x, y, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, x + 10, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x + 10, y, 5, 5);
    }

    private void drawGameOver(Graphics2D g)
    {
        g.setTransform(new AffineTransform());
        g.setColor(Color.BLACK);
        g.drawString("Игра окончена. Нажмите на поле, чтобы начать заново", 10, 20);
    }

    private void drawTarget(Graphics2D g, int x, int y)
    {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }
}