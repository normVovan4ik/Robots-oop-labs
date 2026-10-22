package gui;

/**
 * Модель робота: положение, направление и движение.
 * Робот либо едет к цели, которую задали мышью, либо управляется вручную.
 * Не зависит от Swing, поэтому её легко тестировать.
 */
public class RobotModel
{
    /** Скорости по умолчанию (у основного робота). */
    public static final double MAX_VELOCITY = 0.1;
    public static final double MAX_ANGULAR_VELOCITY = 0.001;
    /** Половина длины робота: до границы поля центр робота не должен подходить ближе. */
    public static final double ROBOT_HALF_SIZE = 15;

    private final double m_maxVelocity;
    private final double m_maxAngularVelocity;

    private volatile double m_positionX;
    private volatile double m_positionY;
    private volatile double m_direction = 0;

    private volatile int m_targetX = 150;
    private volatile int m_targetY = 100;

    /** true, если робот сейчас управляется вручную, а не едет к цели. */
    private volatile boolean m_manual = false;

    /** Размер игрового поля. Ноль означает, что размер ещё неизвестен. */
    private volatile int m_fieldWidth = 0;
    private volatile int m_fieldHeight = 0;

    /** Робот с обычными скоростями в точке (100, 100). */
    public RobotModel()
    {
        this(100, 100, MAX_VELOCITY, MAX_ANGULAR_VELOCITY);
    }

    /**
     * @param positionX          начальное положение по X
     * @param positionY          начальное положение по Y
     * @param maxVelocity        наибольшая скорость
     * @param maxAngularVelocity наибольшая угловая скорость (чем больше, тем круче поворот)
     */
    public RobotModel(double positionX, double positionY, double maxVelocity, double maxAngularVelocity)
    {
        m_positionX = positionX;
        m_positionY = positionY;
        m_maxVelocity = maxVelocity;
        m_maxAngularVelocity = maxAngularVelocity;
    }

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

    public boolean isManual()
    {
        return m_manual;
    }

    /**
     * Задаёт цель и возвращает робота к движению по цели.
     */
    public void setTarget(int x, int y)
    {
        m_targetX = x;
        m_targetY = y;
        m_manual = false;
    }

    /**
     * Задаёт размер игрового поля. Если робот оказался за его границей
     * (например, окно уменьшили), он сразу возвращается внутрь.
     */
    public void setFieldSize(int width, int height)
    {
        m_fieldWidth = width;
        m_fieldHeight = height;
        keepInsideField();
    }

    /**
     * Один шаг движения к цели. В ручном режиме ничего не делает.
     * @param duration длительность шага
     */
    public void update(double duration)
    {
        if (m_manual)
        {
            return;
        }
        double distance = distance(m_targetX, m_targetY, m_positionX, m_positionY);
        if (distance < 0.5)
        {
            return;
        }
        double angleToTarget = angleTo(m_positionX, m_positionY, m_targetX, m_targetY);
        // Разница между нужным и текущим направлением, от 0 до 2π. Поворачиваем туда, где дуга короче.
        double angleDiff = asNormalizedRadians(angleToTarget - m_direction);
        double angularVelocity = 0;
        if (angleDiff > 0)
        {
            angularVelocity = angleDiff < Math.PI ? m_maxAngularVelocity : -m_maxAngularVelocity;
        }

        move(m_maxVelocity, angularVelocity, duration);
    }

    /**
     * Один шаг ручного управления. После него робот остаётся в ручном режиме,
     * пока не будет задана новая цель.
     * @param turn     -1 повернуть влево, 0 не поворачивать, 1 повернуть вправо
     * @param forward  ехать вперёд
     * @param duration длительность шага
     */
    public void drive(int turn, boolean forward, double duration)
    {
        m_manual = true;
        double velocity = forward ? m_maxVelocity : 0;
        double angularVelocity = Integer.signum(turn) * m_maxAngularVelocity;
        move(velocity, angularVelocity, duration);
    }

    private void move(double velocity, double angularVelocity, double duration)
    {
        velocity = applyLimits(velocity, 0, m_maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -m_maxAngularVelocity, m_maxAngularVelocity);
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
        keepInsideField();
    }

    private void keepInsideField()
    {
        if (m_fieldWidth > 0)
        {
            m_positionX = limitToField(m_positionX, m_fieldWidth);
        }
        if (m_fieldHeight > 0)
        {
            m_positionY = limitToField(m_positionY, m_fieldHeight);
        }
    }

    private static double limitToField(double value, int fieldSize)
    {
        double margin = Math.min(ROBOT_HALF_SIZE, fieldSize / 2.0);
        return applyLimits(value, margin, fieldSize - margin);
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