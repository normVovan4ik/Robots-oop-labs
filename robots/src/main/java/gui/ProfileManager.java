package gui;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JInternalFrame;

/**
 * Сохраняет профиль (состояние окон приложения) в файл.
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
}