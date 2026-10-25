package gui;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * Хранит профиль (состояния окон по их именам) в файле.
 */
public class ProfileStorage
{
    private final Path m_file;

    public ProfileStorage(Path file)
    {
        m_file = file;
    }

    /** Файл профиля по умолчанию: папка .robots в домашней папке пользователя. */
    public static Path defaultFile()
    {
        return Paths.get(System.getProperty("user.home"), ".robots", "profile.properties");
    }

    /**
     * Записывает профиль в файл, заменяя прежний.
     */
    public void save(Map<String, WindowState> states) throws IOException
    {
        Properties properties = new Properties();
        states.forEach((name, state) -> properties.setProperty(name, state.toLine()));
        Files.createDirectories(m_file.toAbsolutePath().getParent());
        try (Writer writer = Files.newBufferedWriter(m_file, StandardCharsets.UTF_8))
        {
            properties.store(writer, "Профиль окон приложения Robots");
        }
    }

    /**
     * Читает профиль из файла.
     * @return профиль или пустой результат, если файла нет, он пуст или повреждён
     */
    public Optional<Map<String, WindowState>> load()
    {
        if (!Files.isRegularFile(m_file))
        {
            return Optional.empty();
        }
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(m_file, StandardCharsets.UTF_8))
        {
            properties.load(reader);
            Map<String, WindowState> states = new LinkedHashMap<>();
            for (String name : properties.stringPropertyNames())
            {
                states.put(name, WindowState.fromLine(properties.getProperty(name)));
            }
            return states.isEmpty() ? Optional.empty() : Optional.of(states);
        }
        catch (IOException | IllegalArgumentException e)
        {
            return Optional.empty();
        }
    }
}