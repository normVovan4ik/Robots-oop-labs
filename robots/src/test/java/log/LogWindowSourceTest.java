package log;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class LogWindowSourceTest {

    @Test
    void appendAddsMessageToLog() {
        LogWindowSource source = new LogWindowSource(10);

        source.append(LogLevel.Debug, "hello");

        assertEquals(1, source.size());
        assertEquals("hello", source.all().iterator().next().getMessage());
    }

    @Test
    void sizeNeverExceedsQueueLength() {
        LogWindowSource source = new LogWindowSource(3);

        for (int i = 0; i < 10; i++) {
            source.append(LogLevel.Debug, "m" + i);
        }

        assertEquals(3, source.size());
    }

    @Test
    void oldestMessagesAreDroppedFirst() {
        LogWindowSource source = new LogWindowSource(3);

        for (int i = 0; i < 5; i++) {
            source.append(LogLevel.Debug, "m" + i);
        }

        assertEquals(List.of("m2", "m3", "m4"), messages(source.all()));
    }

    @Test
    void rangeReturnsRequestedPartOfLog() {
        LogWindowSource source = new LogWindowSource(5);
        for (int i = 0; i < 5; i++) {
            source.append(LogLevel.Debug, "m" + i);
        }

        assertEquals(List.of("m1", "m2"), messages(source.range(1, 2)));
    }

    @Test
    void rangeIsLimitedByEndOfLog() {
        LogWindowSource source = new LogWindowSource(5);
        for (int i = 0; i < 3; i++) {
            source.append(LogLevel.Debug, "m" + i);
        }

        assertEquals(List.of("m1", "m2"), messages(source.range(1, 10)));
    }

    @Test
    void rangeStartingOutsideOfLogIsEmpty() {
        LogWindowSource source = new LogWindowSource(5);
        source.append(LogLevel.Debug, "m0");

        assertTrue(messages(source.range(10, 5)).isEmpty());
        assertTrue(messages(source.range(-1, 5)).isEmpty());
    }

    @Test
    void allReturnsSnapshotThatDoesNotChangeLater() {
        LogWindowSource source = new LogWindowSource(10);
        source.append(LogLevel.Debug, "first");
        Iterable<LogEntry> snapshot = source.all();

        source.append(LogLevel.Debug, "second");

        assertEquals(List.of("first"), messages(snapshot));
    }

    private static List<String> messages(Iterable<LogEntry> entries) {
        List<String> result = new ArrayList<>();
        for (LogEntry entry : entries) {
            result.add(entry.getMessage());
        }
        return result;
    }
}