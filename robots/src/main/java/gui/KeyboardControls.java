package gui;

import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Запоминает, какие клавиши управления сейчас нажаты, и управляет роботом.
 * Стрелки и клавиши WASD делают одно и то же.
 */
public class KeyboardControls
{
    public enum Command
    {
        TURN_LEFT, TURN_RIGHT, FORWARD
    }

    private static final Map<Integer, Command> BINDINGS = Map.of(
            KeyEvent.VK_LEFT, Command.TURN_LEFT,
            KeyEvent.VK_A, Command.TURN_LEFT,
            KeyEvent.VK_RIGHT, Command.TURN_RIGHT,
            KeyEvent.VK_D, Command.TURN_RIGHT,
            KeyEvent.VK_UP, Command.FORWARD,
            KeyEvent.VK_W, Command.FORWARD);

    /** Нажатые клавиши: пишет поток событий Swing, читает поток таймера. */
    private final Set<Integer> m_pressedKeys = ConcurrentHashMap.newKeySet();

    public void keyPressed(int keyCode)
    {
        if (BINDINGS.containsKey(keyCode))
        {
            m_pressedKeys.add(keyCode);
        }
    }

    public void keyReleased(int keyCode)
    {
        m_pressedKeys.remove(keyCode);
    }

    /** Отпускает все клавиши, например когда окно потеряло фокус. */
    public void releaseAll()
    {
        m_pressedKeys.clear();
    }

    public boolean isAnyKeyPressed()
    {
        return !m_pressedKeys.isEmpty();
    }

    public boolean isForward()
    {
        return isActive(Command.FORWARD);
    }

    /**
     * @return -1, если нажат поворот влево, 1, если вправо, 0, если ничего или обе клавиши сразу
     */
    public int getTurn()
    {
        return (isActive(Command.TURN_RIGHT) ? 1 : 0) - (isActive(Command.TURN_LEFT) ? 1 : 0);
    }

    /**
     * Один шаг робота: пока нажаты клавиши, он едет вручную,
     * иначе работает обычная логика (цель, заданная мышью).
     */
    public void steer(RobotModel robot, double duration)
    {
        if (isAnyKeyPressed())
        {
            robot.drive(getTurn(), isForward(), duration);
        }
        else
        {
            robot.update(duration);
        }
    }

    private boolean isActive(Command command)
    {
        return m_pressedKeys.stream().anyMatch(key -> BINDINGS.get(key) == command);
    }
}