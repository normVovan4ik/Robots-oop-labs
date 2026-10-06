package log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogWindowSource
{
    private final int m_iQueueLength;

    private final ArrayList<LogEntry> m_messages;
    private final ArrayList<WeakReference<LogChangeListener>> m_listeners;

    public LogWindowSource(int iQueueLength)
    {
        m_iQueueLength = iQueueLength;
        m_messages = new ArrayList<LogEntry>(iQueueLength);
        m_listeners = new ArrayList<WeakReference<LogChangeListener>>();
    }

    public void registerListener(LogChangeListener listener)
    {
        synchronized (m_listeners)
        {
            m_listeners.add(new WeakReference<LogChangeListener>(listener));
        }
    }

    public void unregisterListener(LogChangeListener listener)
    {
        synchronized (m_listeners)
        {
            m_listeners.removeIf(ref -> {
                LogChangeListener registered = ref.get();
                return registered == null || registered == listener;
            });
        }
    }

    public void append(LogLevel logLevel, String strMessage)
    {
        LogEntry entry = new LogEntry(logLevel, strMessage);
        synchronized (m_messages)
        {
            m_messages.add(entry);
            if (m_messages.size() > m_iQueueLength)
            {
                m_messages.remove(0);
            }
        }
        for (LogChangeListener listener : getActiveListeners())
        {
            listener.onLogChanged();
        }
    }

    private List<LogChangeListener> getActiveListeners()
    {
        List<LogChangeListener> result = new ArrayList<LogChangeListener>();
        synchronized (m_listeners)
        {
            m_listeners.removeIf(ref -> ref.get() == null);
            for (WeakReference<LogChangeListener> ref : m_listeners)
            {
                LogChangeListener listener = ref.get();
                if (listener != null)
                {
                    result.add(listener);
                }
            }
        }
        return result;
    }

    public int size()
    {
        synchronized (m_messages)
        {
            return m_messages.size();
        }
    }

    public Iterable<LogEntry> range(int startFrom, int count)
    {
        synchronized (m_messages)
        {
            if (startFrom < 0 || startFrom >= m_messages.size())
            {
                return Collections.emptyList();
            }
            int indexTo = Math.min(startFrom + count, m_messages.size());
            return new ArrayList<LogEntry>(m_messages.subList(startFrom, indexTo));
        }
    }

    public Iterable<LogEntry> all()
    {
        synchronized (m_messages)
        {
            return new ArrayList<LogEntry>(m_messages);
        }
    }
}