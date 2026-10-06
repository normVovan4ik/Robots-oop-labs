package log;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogWindowSourceTest {

    @Test
    void appendAddsMessageToLog() {
        LogWindowSource source = new LogWindowSource(10);

        source.append(LogLevel.Debug, "hello");

        assertEquals(1, source.size());
        assertEquals("hello", source.all().iterator().next().getMessage());
    }
}