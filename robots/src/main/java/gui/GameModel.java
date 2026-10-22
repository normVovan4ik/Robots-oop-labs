package gui;

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
    public static final double PREDATOR_VELOCITY = 0.06;
    public static final double PREDATOR_ANGULAR_VELOCITY = 0.004;

    private static final int DEFAULT_FIELD_SIZE = 400;
    private static final int SPAWN_MARGIN = 50;

    private final int m_predatorCount;
    private volatile RobotModel m_robot;
    private volatile List<RobotModel> m_predators;
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

    public synchronized void setFieldSize(int width, int height)
    {
        m_fieldWidth = width;
        m_fieldHeight = height;
        applyFieldSize(m_robot);
        m_predators.forEach(this::applyFieldSize);
    }

    /**
     * Один шаг игры: робот едет (вручную или к цели), а каждый хищник едет к роботу.
     */
    public synchronized void update(double duration, KeyboardControls controls)
    {
        RobotModel robot = m_robot;
        controls.steer(robot, duration);
        for (RobotModel predator : m_predators)
        {
            predator.setTarget(round(robot.getPositionX()), round(robot.getPositionY()));
            predator.update(duration);
        }
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

    private static int round(double value)
    {
        return (int) Math.round(value);
    }
}