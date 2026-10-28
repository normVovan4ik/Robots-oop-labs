package gui;

import java.awt.Component;
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
    static final String RESTORE_QUESTION = "Найден сохранённый профиль окон. Восстановить его?";
    static final String RESTORE_TITLE = "Профиль";

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
     * Применяет сохранённый профиль к окнам без вопросов.
     * @param windows окна приложения по именам
     * @return true, если профиль найден и применён
     */
    public boolean restore(Map<String, JInternalFrame> windows)
    {
        Optional<Map<String, WindowState>> profile = m_storage.load();
        profile.ifPresent(states -> apply(states, windows));
        return profile.isPresent();
    }

    /**
     * Если есть сохранённый профиль, предупреждает об этом и спрашивает, восстановить ли его.
     * Когда профиля нет (файла нет, он пуст или повреждён), ничего не спрашивает.
     * @param windows окна приложения по именам
     * @param parent  компонент, над которым показывается вопрос
     * @return true, если профиль найден, пользователь согласился и профиль применён
     */
    public boolean restoreIfConfirmed(Map<String, JInternalFrame> windows, Confirmer confirmer, Component parent)
    {
        Optional<Map<String, WindowState>> profile = m_storage.load();
        if (profile.isEmpty() || !confirmer.confirm(parent, RESTORE_QUESTION, RESTORE_TITLE))
        {
            return false;
        }
        apply(profile.get(), windows);
        return true;
    }

    /**
     * Окна, которых нет в профиле, остаются как есть,
     * а записи профиля про неизвестные окна пропускаются.
     */
    private static void apply(Map<String, WindowState> profile, Map<String, JInternalFrame> windows)
    {
        profile.forEach((name, state) ->
        {
            JInternalFrame frame = windows.get(name);
            if (frame != null)
            {
                state.applyTo(frame);
            }
        });
    }
}