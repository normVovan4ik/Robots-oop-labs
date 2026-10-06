package gui;

/**
 * Модель робота: положение, направление и движение к цели.
 * Не зависит от Swing, поэтому её легко тестировать.
 */
public class RobotModel
{
    public static final double MAX_VELOCITY = 0.1;
    public static final double MAX_ANGULAR_VELOCITY = 0.001;

    private volatile double m_positionX = 100;
    private volatile double m_positionY = 100;
    private volatile double m_direction = 0;

    private volatile int m_targetX = 150;
    private volatile int m_targetY = 100;

    public double getPositionX()
    {
        return m_positionX;
    }

    public double getPositionY()
    {
        return m_positionY;
    }

    public double getDirection()
    {
        return m_direction;
    }

    public int getTargetX()
    {
        return m_targetX;
    }

    public int getTargetY()
    {
        return m_targetY;
    }

    public void setTarget(int x, int y)
    {
        m_targetX = x;
        m_targetY = y;
    }

    /**
     * Один шаг движения к цели.
     * @param duration длительность шага
     */
    public void update(double duration)
    {
        double distance = distance(m_targetX, m_targetY, m_positionX, m_positionY);
        if (distance < 0.5)
        {
            return;
        }
        double angleToTarget = angleTo(m_positionX, m_positionY, m_targetX, m_targetY);
        double angularVelocity = 0;
        if (angleToTarget > m_direction)
        {
            angularVelocity = MAX_ANGULAR_VELOCITY;
        }
        if (angleToTarget < m_direction)
        {
            angularVelocity = -MAX_ANGULAR_VELOCITY;
        }

        move(MAX_VELOCITY, angularVelocity, duration);
    }

    private void move(double velocity, double angularVelocity, double duration)
    {
        velocity = applyLimits(velocity, 0, MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);
        double newX = m_positionX + velocity / angularVelocity *
                (Math.sin(m_direction + angularVelocity * duration) -
                        Math.sin(m_direction));
        if (!Double.isFinite(newX))
        {
            newX = m_positionX + velocity * duration * Math.cos(m_direction);
        }
        double newY = m_positionY - velocity / angularVelocity *
                (Math.cos(m_direction + angularVelocity * duration) -
                        Math.cos(m_direction));
        if (!Double.isFinite(newY))
        {
            newY = m_positionY + velocity * duration * Math.sin(m_direction);
        }
        m_positionX = newX;
        m_positionY = newY;
        m_direction = asNormalizedRadians(m_direction + angularVelocity * duration);
    }

    private static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY)
    {
        double diffX = toX - fromX;
        double diffY = toY - fromY;

        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    private static double applyLimits(double value, double min, double max)
    {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    private static double asNormalizedRadians(double angle)
    {
        while (angle < 0)
        {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI)
        {
            angle -= 2 * Math.PI;
        }
        return angle;
    }
}