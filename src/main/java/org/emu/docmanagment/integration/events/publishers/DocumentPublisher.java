package org.emu.docmanagment.integration.events.publishers;

import org.emu.common.dto.DocumentDto;

import org.emu.common.events.DocumentEvent;

import org.emu.common.status.DocumentStatus;
import org.emu.common.status.NotificationStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

@Service
public class DocumentPublisher {

    @Autowired
    private Sinks.Many<DocumentEvent> documentSink;

    public void raiseDocumentEvent( DocumentDto documentDto,DocumentStatus documentStatus,String traceId) {

        var memberEvent = new DocumentEvent(documentDto, documentStatus,traceId);
        memberEvent.setTraceid(traceId);
        memberEvent.setType("document");
        this.documentSink.tryEmitNext(memberEvent);
    }
}
