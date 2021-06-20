package org.emu.docmanagment.alfresco;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.springframework.stereotype.Component;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Copyright 2021-2022 By Dirac Systems.
 * <p>
 * Created by {@khalid.nouh on 26/5/2021}.
 */
@Component
public class AlfrescoUtils extends AlfrescoBasicImpl {
    public static final String FILE_TYPE = "image/jpeg";
    public static final String CONTENT_TYPE = "cmis:document";

    public List<Document> doDownload(String[] documentsIds)
        throws IOException {
        // Get a CMIS session
        Session cmisSession;
        List<Document> documentslist = null;
        try {
            cmisSession = getCmisSession();
//            Folder rootFolder = cmisSession.getRootFolder();
//            Folder memberDataFolder = (Folder) cmisSession.getObjectByPath("/MembersData");

//            Folder folderParent = memberDataFolder.getFolderParent();
//            ItemIterable<CmisObject> children = memberDataFolder.getChildren();
//            for (CmisObject o : children) {
//                Folder f= (Folder) o;
//                String name1 = f.getName();
//                String path = f.getPath();
//            }
//            System.out.println(rootFolder.getName());
//            System.out.println(rootFolder.getPath());
//            documentslist=new ArrayList<>();
            if (documentsIds.length > 0) {
                for (String documentId : documentsIds) {
                    Document tempDocument = (Document) cmisSession.getObject(documentId);
                    documentslist.add(tempDocument);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return documentslist;
    }

    /**
     * Download a document by documentId from the CMIS server.
     *
     * @throws IOException
     */
    public String doDownload(String documentId)
        throws IOException {
        Session cmisSession;
        try {
            cmisSession = getCmisSession();
            Document tempDocument = (Document) cmisSession.getObject(documentId);
            String versionLabel = tempDocument.getVersionLabel();
            String name = tempDocument.getName();
            List<Document> allVersions = tempDocument.getAllVersions();
            String versionLabel1 = allVersions.get(0).getVersionLabel();

            String contentStreamMimeType = tempDocument.getContentStreamMimeType();
            System.out.println(contentStreamMimeType);
            String fileName = Utility.getFileName(name);
            String exten = Utility.getFileExtension(name);
            InputStream stream = tempDocument.getContentStream().getStream();
            byte[] buffer = Utility.getArrayFromInputStream(stream);
                File targetFile = new File("D://" + fileName + "." + exten);
                OutputStream outStream = new FileOutputStream(targetFile);
                outStream.write(buffer);
                outStream.flush();
                outStream.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return "done downloaded " + documentId;
    }

    /**
     * Uploads a document passed as inputStream to the CMIS server.
     *
     * @throws IOException
     */
    public Document doUpload( byte[] buffer, Map<String, Object> extraProps)
        throws IOException {
        // Get a CMIS session
        Session cmisSession;
        Folder folder;
        try {
            cmisSession = getCmisSession();
            folder = (Folder) cmisSession.getObjectByPath("/khalid");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        File tempFile = new File(extraProps.get("fileName").toString());
        OutputStream outStream = new FileOutputStream(tempFile);
        outStream.write(buffer);
        Map<String, Object> propss = AlfrescoHelper.getProperties(CONTENT_TYPE, tempFile);
        Document document = AlfrescoHelper.createDocument(cmisSession, folder, tempFile, FILE_TYPE, propss);
        return document;
    }

    /**
     * Delete a document by documentId from the CMIS server.
     *
     * @throws IOException
     */
    public void doDelete(String documentId)
        throws IOException {
        // Get a CMIS session
        Session cmisSession;
        try {
            cmisSession = getCmisSession();
            Document document = (Document) cmisSession.getObject(documentId);
//            document.delete(true);
            document.deleteAllVersions();
            System.out.println(documentId + ": deleted successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
    }
}
