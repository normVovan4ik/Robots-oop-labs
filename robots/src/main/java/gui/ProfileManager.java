package gui;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import javax.swing.JInternalFrame;

/**
 * Сохраняет профиль (состояние окон приложения) в файл и восстанавливает его.
 */
public class ProfileManager
{
    private final ProfileStorage m_storage;

    public ProfileManager(ProfileStorage storage)
    {
        m_storage = storage;
    }

    /**
     * Сохраняет состояние всех окон.
     * @param windows окна приложения по именам
     */
    public void save(Map<String, JInternalFrame> windows) throws IOException
    {
        Map<String, WindowState> states = new LinkedHashMap<>();
        windows.forEach((name, frame) -> states.put(name, WindowState.capture(frame)));
        m_storage.save(states);
    }

    /**
     * Применяет сохранённый профиль к окнам. Окна, которых нет в профиле, остаются как есть,
     * а записи профиля про неизвестные окна пропускаются.
     * @param windows окна приложения по именам
     * @return true, если профиль найден и применён
     */
    public boolean restore(Map<String, JInternalFrame> windows)
    {
        Optional<Map<String, WindowState>> profile = m_storage.load();
        if (profile.isEmpty())
        {
            return false;
        }
        profile.get().forEach((name, state) ->
        {
            JInternalFrame frame = windows.get(name);
            if (frame != null)
            {
                state.applyTo(frame);
            }
        });
        return true;
    }
}