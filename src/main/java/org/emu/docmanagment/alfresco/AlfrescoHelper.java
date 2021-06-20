package org.emu.docmanagment.alfresco;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.jpeg.JpegParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
/**
 * Copyright 2021-2022 By Dirac Systems.
 *
 * Created by {@khalid.nouh on 26/5/2021}.
 */
public class AlfrescoHelper {
    public static final String FILE_TYPE = "image/jpeg";

    /**
     * Use the CMIS API to create a document in a folder
     *
     * @param cmisSession
     * @param parentFolder
     * @param file
     * @param fileType
     * @param props
     * @return
     * @throws FileNotFoundException
     * @author jpotts
     */
    public static Document createDocument(Session cmisSession,
                                          Folder parentFolder,
                                          File file,
                                          String fileType,
                                          Map<String, Object> props)
        throws FileNotFoundException {
        String fileName = file.getName();
        // create a map of properties if one wasn't passed in
        if (props == null) {
            props = new HashMap<String, Object>();
        }
        // Add the object type ID if it wasn't already
        if (props.get("cmis:objectTypeId") == null) {
            props.put("cmis:objectTypeId", "cmis:document");
        }
        // Add the name if it wasn't already
        if (props.get("cmis:name") == null) {
            props.put("cmis:name", fileName);
        }
        ContentStream contentStream = cmisSession.getObjectFactory().
            createContentStream(
                fileName,
                file.length(),
                fileType,
                new FileInputStream(file)
            );

        Document document = null;
        try {
            document = parentFolder.createDocument(props, contentStream, VersioningState.MAJOR);
            System.out.println("Created new document: " + document.getId());
        } catch (CmisContentAlreadyExistsException ccaee) {
            document = (Document) cmisSession.getObjectByPath(parentFolder.getPath() + "/" + fileName);
            System.out.println("Document already exists: " + fileName);
            // Now update it with a new version
            if (document.getAllowableActions().getAllowableActions().contains(org.apache.chemistry.opencmis.commons.enums.Action.CAN_CHECK_OUT)) {
                document.refresh();
                String testName = document.getContentStream().getFileName();
                ObjectId idOfCheckedOutDocument = document.checkOut();
                Document pwc = (Document) cmisSession.getObject(idOfCheckedOutDocument);
                contentStream = cmisSession.getObjectFactory().
                    createContentStream(
                        fileName,
                        file.length(),
                        fileType,
                        new FileInputStream(file)
                    );
                ObjectId objectId = pwc.checkIn(false, null, contentStream, "just a minor change");
                document = (Document) cmisSession.getObject(objectId);
                System.out.println("Version label is now:" + document.getVersionLabel());
            }
        }
        return document;
    }

    /**
     * Returns the properties that need to be set on an object for a given file.
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static Map<String, Object> getProperties(String objectTypeId, File file)
        throws FileNotFoundException, IOException {

        Map<String, Object> props = new HashMap<String, Object>();

        //Tika tika = new Tika();

        String fileName = file.getName();
        System.out.println("File: " + fileName);
        InputStream stream = new FileInputStream(file);
        try {
            // if the target type is the CMIS Book image type, let's extract some metadata from the file
            // and return them as properties to be set on the object
            if (objectTypeId.equals("D:cmisbook:image")) {

                Metadata metadata = new Metadata();
                ContentHandler handler = new DefaultHandler();
                Parser parser = new JpegParser();
                ParseContext context = new ParseContext();

                //String mimeType = tika.detect(stream); // broken for my jpegs
                String mimeType = FILE_TYPE;
                metadata.set(Metadata.CONTENT_TYPE, mimeType);

                parser.parse(stream, handler, metadata, context);
                String lat = metadata.get("geo:lat");
                String lon = metadata.get("geo:long");
                stream.close();

                // create a map of properties

                props.put("cmis:objectTypeId", objectTypeId);
                props.put("cmis:name", fileName);
                if (lat != null && lon != null) {
                    System.out.println("LAT:" + lat);
                    System.out.println("LON:" + lon);
                    props.put("cmisbook:gpsLatitude", BigDecimal.valueOf(Float.parseFloat(lat)));
                    props.put("cmisbook:gpsLongitude", BigDecimal.valueOf(Float.parseFloat(lon)));
                }
            } else {
                // otherwise, just set the object type and name and be done
                props.put("cmis:objectTypeId", objectTypeId);
                props.put("cmis:name", fileName);
            }
        } catch (TikaException te) {
            System.out.println("Caught tika exception, skipping");
        } catch (SAXException se) {
            System.out.println("Caught SAXException, skipping");
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return props;
    }


}
