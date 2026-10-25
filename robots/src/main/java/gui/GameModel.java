package gui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Игровая модель: робот и хищники, которые за ним гонятся.
 * Не зависит от Swing, поэтому её легко тестировать.
 */
public class GameModel
{
    /** Хищник медленнее робота, но поворачивает круче. */
    public static final double PREDATOR_VELOCITY = 0.09;
    public static final double PREDATOR_ANGULAR_VELOCITY = 0.005;
    /** Хищник поймал робота, если расстояние между центрами меньше этого значения. */
    public static final double CATCH_DISTANCE = 25;

    static final String RESTART_QUESTION = "Вас поймали. Начать заново?";
    static final String RESTART_TITLE = "Игра окончена";

    private static final int DEFAULT_FIELD_SIZE = 400;
    private static final int SPAWN_MARGIN = 50;

    private final int m_predatorCount;
    private volatile RobotModel m_robot;
    private volatile List<RobotModel> m_predators;
    private volatile boolean m_gameOver = false;
    private int m_fieldWidth = 0;
    private int m_fieldHeight = 0;

    public GameModel(int predatorCount)
    {
        m_predatorCount = predatorCount;
        start();
    }

    public RobotModel getRobot()
    {
        return m_robot;
    }

    public List<RobotModel> getPredators()
    {
        return m_predators;
    }

    public boolean isGameOver()
    {
        return m_gameOver;
    }

    /**
     * Отправляет робота к точке: он едет к ней сам, а не по клавишам.
     */
    public void setRobotTarget(int x, int y)
    {
        m_robot.setTarget(x, y);
    }

    public synchronized void setFieldSize(int width, int height)
    {
        m_fieldWidth = width;
        m_fieldHeight = height;
        applyFieldSize(m_robot);
        m_predators.forEach(this::applyFieldSize);
    }

    /**
     * Один шаг игры: робот едет (вручную или к цели), а каждый хищник едет к роботу.
     * Когда хищник догнал робота, игра заканчивается и модель перестаёт двигаться.
     */
    public synchronized void update(double duration, KeyboardControls controls)
    {
        if (m_gameOver)
        {
            return;
        }
        RobotModel robot = m_robot;
        controls.steer(robot, duration);
        for (RobotModel predator : m_predators)
        {
            predator.chase(robot);
            predator.update(duration);
        }
        if (isRobotCaught())
        {
            m_gameOver = true;
        }
    }

    /**
     * Если игра окончена, спрашивает, начать ли заново, и при согласии начинает новую игру.
     * @return true, если игра началась заново
     */
    public boolean askRestart(Confirmer confirmer, Component parent)
    {
        if (!m_gameOver)
        {
            return false;
        }
        if (!confirmer.confirm(parent, RESTART_QUESTION, RESTART_TITLE))
        {
            return false;
        }
        restart();
        return true;
    }

    public synchronized void restart()
    {
        start();
        m_gameOver = false;
    }

    private boolean isRobotCaught()
    {
        RobotModel robot = m_robot;
        for (RobotModel predator : m_predators)
        {
            double distance = predator.distanceTo(robot);
            if (distance < CATCH_DISTANCE)
            {
                return true;
            }
        }
        return false;
    }

    private synchronized void start()
    {
        RobotModel robot = new RobotModel();
        List<RobotModel> predators = new ArrayList<RobotModel>();
        for (int i = 0; i < m_predatorCount; i++)
        {
            predators.add(createPredator(i));
        }
        applyFieldSize(robot);
        predators.forEach(this::applyFieldSize);
        m_robot = robot;
        m_predators = Collections.unmodifiableList(predators);
    }

    /**
     * Хищники появляются в углах поля, подальше от робота.
     */
    private RobotModel createPredator(int index)
    {
        int width = m_fieldWidth > 0 ? m_fieldWidth : DEFAULT_FIELD_SIZE;
        int height = m_fieldHeight > 0 ? m_fieldHeight : DEFAULT_FIELD_SIZE;
        double x = index % 2 == 0 ? width - SPAWN_MARGIN : SPAWN_MARGIN;
        double y = index / 2 % 2 == 0 ? height - SPAWN_MARGIN : SPAWN_MARGIN;
        return new RobotModel(x, y, PREDATOR_VELOCITY, PREDATOR_ANGULAR_VELOCITY);
    }

    private void applyFieldSize(RobotModel model)
    {
        model.setFieldSize(m_fieldWidth, m_fieldHeight);
    }
}