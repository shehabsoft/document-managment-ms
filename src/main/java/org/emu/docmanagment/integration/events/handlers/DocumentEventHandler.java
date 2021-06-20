package org.emu.docmanagment.integration.events.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.emu.common.dto.DocumentDto;

import org.emu.common.events.DocumentEvent;
import org.emu.common.events.NotificationEvent;
import org.emu.common.status.DocumentStatus;
import org.emu.common.status.NotificationStatus;
import org.emu.docmanagment.integration.events.publishers.DocumentPublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;

@Service
public class DocumentEventHandler {



    @Autowired
    private DocumentPublisher publisher;

    private final Logger log = LoggerFactory.getLogger(DocumentEventHandler.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public void sendDocument(DocumentEvent me) {

        DocumentDto documentDto=(DocumentDto)me.getGenericDto();
        publisher.raiseDocumentEvent(documentDto, DocumentStatus.SENT, me.getTraceid());
        //
    }
}
