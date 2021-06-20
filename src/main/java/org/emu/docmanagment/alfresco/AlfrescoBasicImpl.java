package org.emu.docmanagment.alfresco;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.emu.docmanagment.alfresco.config.AlfrescoServerConfig;
import org.emu.docmanagment.alfresco.model.ContainerEntry;
import org.emu.docmanagment.alfresco.model.ContainerList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.*;
/**
 * Copyright 2021-2022 By Dirac Systems.
 *
 * Created by {@khalid.nouh on 26/5/2021}.
 */
/**
 * Knows how to provide the values specific to Alfresco in the cloud. Extend this
 * class to load files into an existing site you've created in the cloud.
 */
@Component
public class AlfrescoBasicImpl implements AlfrescoBasic {
    @Autowired
    AlfrescoServerConfig alfrescoServerConfig;
    public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    public static final JsonFactory JSON_FACTORY = new JacksonFactory();

    public HttpRequestFactory requestFactory;
    public Session cmisSession;

    /**
     * Gets a CMIS Session by connecting to the Alfresco Cloud.
     *
     * @return Session
     */
    public Session getCmisSession() throws Exception {
        if (cmisSession == null) {
            SessionFactory factory=null;
            try {
                // default factory implementation
                factory = SessionFactoryImpl.newInstance();
                Map<String, String> parameter = new HashMap<String, String>();

                // connection settings
                parameter.put(SessionParameter.ATOMPUB_URL, alfrescoServerConfig.ATOMPUB_URL);
                parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
//                parameter.put(SessionParameter.AUTH_HTTP_BASIC, "false");
//                parameter.put(SessionParameter.HEADER + ".0", "Authorization: Bearer " + getAccessToken());
                parameter.put(SessionParameter.HEADER + ".0", "Authorization: Basic "+ Base64.getEncoder().encodeToString(("admin:admin").getBytes("UTF-8")));
//                parameter.put(SessionParameter.USER, "admin");
//                parameter.put(SessionParameter.PASSWORD, "admin");
//                parameter.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");
                List<Repository> repositories = factory.getRepositories(parameter);

                this.cmisSession = repositories.get(0).createSession();
            }
            catch (Exception e){
                if(factory!=null)
                    factory=null;
            }
        }

        return this.cmisSession;
    }

    /**
     * Get the Folder object where the demo folder is to be created.
     */
    public Folder getParentFolder(Session cmisSession) throws Exception {
//        VerificationCodeReceiver receiver = new LocalServerReceiver();
        String redirectUri = "receiver.getRedirectUri()";
//        final Credential credential = authorize(receiver, redirectUri);
            this.requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                HttpHeaders headers=new HttpHeaders();
                headers.setAuthorization("Basic "+ Base64.getEncoder().encodeToString(("admin:admin").getBytes("UTF-8")));
                request.setHeaders(headers);
                request.setParser(new JsonObjectParser(JSON_FACTORY));
            }
        });
        String rootFolderId = getRootFolderId(this.requestFactory, alfrescoServerConfig.HOME_NETWORK, alfrescoServerConfig.SITE);

        Folder folder = (Folder) cmisSession.getObject(rootFolderId);

        return folder;

    }

    /**
     * Return the object type ID of the objects we want to create
     */
    public String getObjectTypeId() {
        return alfrescoServerConfig.CONTENT_TYPE;
    }

    /**
     * Get the OAuth2 access token.
     * @return
     * @throws Exception
     */
   /* public String getAccessToken() throws Exception {
        String accessToken = "";
        // authorization
        VerificationCodeReceiver receiver = new LocalServerReceiver();
        try {
            String redirectUri = receiver.getRedirectUri();
            launchInBrowser("google-chrome", redirectUri, OAuth2ClientCredentials.CLIENT_ID, SCOPE);
            final Credential credential = authorize(receiver, redirectUri);

            this.requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) throws IOException {
                    credential.initialize(request);
                    request.setParser(new JsonObjectParser(JSON_FACTORY));
                }
            });

            accessToken = credential.getAccessToken();

            System.out.println("Access token:" + accessToken);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            receiver.stop();
        }

        return accessToken;

    }
*//*
    public Credential authorize(VerificationCodeReceiver receiver, String redirectUri)
        throws IOException {

        String code = receiver.waitForCode();

        AuthorizationCodeFlow codeFlow = new AuthorizationCodeFlow.Builder(
            BearerToken.authorizationHeaderAccessMethod(),
            HTTP_TRANSPORT,
            JSON_FACTORY,
            new GenericUrl(TOKEN_SERVER_URL),
            new ClientParametersAuthentication(
                OAuth2ClientCredentials.CLIENT_ID, OAuth2ClientCredentials.CLIENT_SECRET),
            OAuth2ClientCredentials.CLIENT_ID,
            AUTHORIZATION_SERVER_URL).setScopes(SCOPE).build();

        TokenResponse response = codeFlow.newTokenRequest(code)
            .setRedirectUri(redirectUri).setScopes(SCOPE).execute();

        return codeFlow.createAndStoreCredential(response, null);

    }
*/
    /*
    public void launchInBrowser(
        String browser, String redirectUrl, String clientId, String scope) throws IOException {

        String authorizationUrl = new AuthorizationCodeRequestUrl(
            alfrescoServerConfig.AUTHORIZATION_SERVER_URL, clientId).setRedirectUri(redirectUrl)
            .setScopes(Arrays.asList(scope)).build();

        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Action.BROWSE)) {
                desktop.browse(URI.create(authorizationUrl));
                return;
            }
        }

        if (browser != null) {
            Runtime.getRuntime().exec(new String[] {browser, authorizationUrl});
        } else {
            System.out.println("Open the following address in your favorite browser:");
            System.out.println("  " + authorizationUrl);
        }
    }*/

    /**
     * Use the REST API to find the documentLibrary folder, then return its ID.
     *
     * @param requestFactory
     * @param homeNetwork
     * @param site
     * @return
     * @throws IOException
     */
    public String getRootFolderId(HttpRequestFactory requestFactory, String homeNetwork, String site) throws IOException {
        GenericUrl containersUrl = new GenericUrl(alfrescoServerConfig.ALFRESCO_API_URL +
            alfrescoServerConfig.SITES_URL +
            "/" +
            site +
            "/containers");

        HttpRequest request = requestFactory.buildGetRequest(containersUrl);
        ContainerList containerList = request.execute().parseAs(ContainerList.class);
        String rootFolderId = null;
        for (ContainerEntry containerEntry : containerList.list.entries) {
            if (containerEntry.entry.folderId.equals("documentLibrary")) {
                rootFolderId = containerEntry.entry.id;
                break;
            }
        }
        return rootFolderId;
    }

}
