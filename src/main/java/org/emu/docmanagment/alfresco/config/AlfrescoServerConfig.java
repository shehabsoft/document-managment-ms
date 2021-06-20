package org.emu.docmanagment.alfresco.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Copyright 2021-2022 By Dirac Systems.
 *
 * Created by {@khalid.nouh on 26/5/2021}.
 */

@Configuration
public class AlfrescoServerConfig {

    // Change these to match your network, site, and folder in Alfresco in the Cloud
    /**
     * Specify the cloud user's home network. In real life you'd probably make an API call to determine this.
     */

    @Value("${alfresco.home.network}")
    public  String HOME_NETWORK ;
    /**
     * Specify the short name of the Alfresco cloud site where the files should be uploaded.
     */
    @Value("${alfresco.site}")
    public  String SITE ;
    @Value("${alfresco.api.url}")
    public   String ALFRESCO_API_URL;
    @Value("${alfresco.atompub.url}")
    public   String ATOMPUB_URL ;
    @Value("${alfresco.scope}")
    public   String SCOPE ;
    @Value("${alfresco.content.type}")
    public   String CONTENT_TYPE;
    @Value("${alfresco.sites.url}")
    public   String SITES_URL ;

}
