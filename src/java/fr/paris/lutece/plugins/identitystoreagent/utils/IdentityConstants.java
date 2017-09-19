/*
 * Copyright (c) 2002-2017, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.identitystoreagent.utils;

import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * Constants class
 */
public final class IdentityConstants
{
    public static final String SPLIT_PATTERN = ";";
    public static final String PROPERTY_IDS_REFERENCE_LIST = AppPropertiesService.getProperty( "identitystoreagent.application.listref" );
    public static final String PROPERTY_IDS_REFERENCE_LIST_PREFIX = "identitystoreagent.application.listref.";
    public static final String PROPERTY_IDS_REFERENCE_LIST_PREFIX_KEY = "identitystoreagent.application.listrefkey.";
    public static final String [ ] PROPERTY_IDS_VIEW_ATTR_LIST = AppPropertiesService.getProperty( "identitystoreagent.ids_view.attribute" ).split(
            SPLIT_PATTERN );
    public static final String PROPERTY_ATTR_LABEL_PREFIX = "identitystoreagent.attr_label.";
    public static final String PARAMETER_CONNECTION_ID = "connection_id";
    public static final String PARAMETER_CUSTOMER_ID = "customer_id";
    public static final String PARAMETER_ATTRIBUTE_KEY = "attribute_key";
    public static final String MARK_VIEW_MODE = "viewMode";
    public static final String MARK_IDENTITY = "identity";
    public static final String MARK_MAP_REFERENCE_LIST = "identity_reference_list";
    public static final String MARK_ATTR_LIST = "identity_attr_list";
    public static final String IDS_NO_CUSTOMER_ID = "";
    public static final String IDS_NO_CONNECTION_ID = "";
    public static final String AGENT_CERTIFIER_CODE = "agentcertifier";

    /**
     * private constructor
     */
    private IdentityConstants( )
    {
    }
}
