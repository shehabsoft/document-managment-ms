package org.emu.docmanagment.integration.events.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.chemistry.opencmis.client.api.Document;
import org.emu.common.dto.DocumentDetails;
import org.emu.common.dto.DocumentDto;
import org.emu.common.dto.MemberDto;
import org.emu.common.events.DocumentEvent;
import org.emu.common.events.NotificationEvent;

import org.emu.docmanagment.alfresco.AlfrescoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Configuration
public class DocumentConsumer {

@Autowired
ObjectMapper objectMapper;

    @Autowired
    AlfrescoUtils alfrescoUtils;
    @Bean
    public Consumer<DocumentEvent> documentEventConsumer() {
        return ie -> {
            if (ie.getStatus().toString().equals("NEW")) {
                DocumentDto memberDto2 = objectMapper.convertValue(ie.getGenericDto(), DocumentDto.class);
                for(DocumentDetails documentDetails:memberDto2.getDocumentDetailsList()){
                    Map<String, Object> extraProps = new HashMap<String, Object>();
                    extraProps.put("fileName",documentDetails.getFileName());
                    extraProps.put("ContentType", documentDetails.getFileType());
                    try {
                        Document document = alfrescoUtils.doUpload(documentDetails.getFileBytes(), extraProps);
                        if(document!=null){
                            document.getId()
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
//                System.out.println(documentDetails.getDocType());

            }
        };
    }
}
