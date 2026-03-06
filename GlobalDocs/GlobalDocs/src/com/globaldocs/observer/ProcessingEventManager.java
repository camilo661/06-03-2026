package com.globaldocs.observer;

import com.globaldocs.model.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PATTERN: Observer - Event Manager
 * Manages listeners and dispatches events during document processing.
 * Events: STATUS_CHANGE, COMPLETED, ERROR, BATCH_PROGRESS
 */
public class ProcessingEventManager {
    private final Map<String, List<ProcessingEventListener>> listeners = new HashMap<>();

    public void subscribe(String eventType, ProcessingEventListener listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    public void unsubscribe(String eventType, ProcessingEventListener listener) {
        List<ProcessingEventListener> list = listeners.get(eventType);
        if (list != null) list.remove(listener);
    }

    public void notify(String eventType, Document documento) {
        List<ProcessingEventListener> list = listeners.getOrDefault(eventType, new ArrayList<>());
        list.forEach(listener -> listener.onEvent(eventType, documento));
    }
}
