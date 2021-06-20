package org.emu.docmanagment.web.rest;

import org.emu.common.dto.DocumentDetails;
import org.emu.common.dto.DocumentDto;
import org.emu.common.status.DocumentStatus;
import org.emu.docmanagment.alfresco.AlfrescoUtils;
import org.emu.docmanagment.alfresco.config.AlfrescoServerConfig;
import org.emu.docmanagment.integration.events.handlers.DocumentEventHandler;
import org.emu.docmanagment.integration.events.publishers.DocumentPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Copyright 2021-2022 By Dirac Systems.
 *
 * Created by {@khalid.nouh on 26/5/2021}.
 */
@RestController
@RequestMapping("/api")
public class AlfrescoResource {

    private final Logger log = LoggerFactory.getLogger(AlfrescoResource.class);
@Autowired
AlfrescoServerConfig alfrescoServerConfig;
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public AlfrescoResource() {

    }
    @Autowired
    DocumentPublisher documentPublisher;

    @Autowired
    AlfrescoUtils alfrescoUtils;

    @PostMapping("/alf")
    public String upload(MultipartFile multipartFile) throws IOException {
        Map<String, Object> extraProps = new HashMap<String, Object>();
        extraProps.put("fileName", multipartFile.getOriginalFilename());
        extraProps.put("ContentType", multipartFile.getContentType());
        alfrescoUtils.doUpload(multipartFile.getInputStream().readAllBytes(), extraProps);
        return "done";
    }

    @PostMapping("/uploadEvent")
    public String uploadEvent(@RequestPart("file") MultipartFile multipartFile) throws IOException {
        Map<String, Object> extraProps = new HashMap<String, Object>();
        extraProps.put("fileName", multipartFile.getOriginalFilename());
        extraProps.put("ContentType", multipartFile.getContentType());
        DocumentDto documentDto=new DocumentDto();
       // documentDto.setFileBytes(multipartFile.getBytes());
       // documentDto.setDocType(multipartFile.getContentType());
        documentPublisher.raiseDocumentEvent(documentDto, DocumentStatus.NEW,"1224");






       // alfrescoUtils.doUpload(multipartFile.getInputStream(), extraProps);
        return "done";
    }

    @PostMapping("/testMulipleDocs")
    public String testMulipleDocs(@RequestPart("file") MultipartFile multipartFile) throws IOException {
        Map<String, Object> extraProps = new HashMap<String, Object>();
        extraProps.put("fileName", multipartFile.getOriginalFilename());
        extraProps.put("ContentType", multipartFile.getContentType());
        DocumentDto documentDto=new DocumentDto();
        documentDto.setDocumentOwnerId(12l);
        DocumentDetails documentDetails=new DocumentDetails();
        documentDetails.setFileBytes(multipartFile.getBytes());
        documentDetails.setDocType("pdf");
        documentDetails.setFileType("nid");
        List<DocumentDetails> documentDetailsList=new ArrayList<>();
        documentDetailsList.add(documentDetails);
        documentDto.setDocumentDetailsList(documentDetailsList);
        // documentDto.setFileBytes(multipartFile.getBytes());
        // documentDto.setDocType(multipartFile.getContentType());
        documentPublisher.raiseDocumentEvent(documentDto, DocumentStatus.NEW,"1224");






        // alfrescoUtils.doUpload(multipartFile.getInputStream(), extraProps);
        return "done";
    }

    @DeleteMapping("/alf/{documentId}")
    public String delete(@PathVariable("documentId") String documentId) throws IOException {

        alfrescoUtils.doDelete(documentId);
        return "done";
    }
    @GetMapping("/get")
    public void getD(){
        System.out.println(alfrescoServerConfig.HOME_NETWORK);
        System.out.println(alfrescoServerConfig.SITE);
    }


    @GetMapping("/alf")
    public String download(String documentId) throws IOException {
       return  alfrescoUtils.doDownload(documentId);

    }
}
