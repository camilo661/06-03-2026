package com.globaldocs.observer;

import com.globaldocs.model.Document;

/**
 * PATTERN: Observer
 * Listener interface for document processing events.
 */
public interface ProcessingEventListener {
    void onEvent(String eventType, Document documento);
}
