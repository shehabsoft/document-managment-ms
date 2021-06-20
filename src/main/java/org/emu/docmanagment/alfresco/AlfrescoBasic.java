package org.emu.docmanagment.alfresco;


import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
/**
 * Copyright 2021-2022 By Dirac Systems.
 *
 * Created by {@khalid.nouh on 26/5/2021}.
 */
public interface AlfrescoBasic {

     Session getCmisSession() throws Exception;

     Folder getParentFolder(Session cmisSession) throws Exception;

     String getObjectTypeId();

}
