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
package fr.paris.lutece.plugins.identitystoreagent.service;

import java.util.Locale;

import fr.paris.lutece.plugins.identitystore.web.rs.dto.AppRightDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.ApplicationRightsDto;
import fr.paris.lutece.plugins.identitystoreagent.IdentityStoreAgentPlugin;
import fr.paris.lutece.plugins.identitystoreagent.utils.IdentityConstants;
import fr.paris.lutece.plugins.identitystoreagent.utils.IdentityUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.rbac.Permission;
import fr.paris.lutece.portal.service.rbac.ResourceIdService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.util.ReferenceList;

/**
 *
 */
public class IdentityAgentManagementResourceIdService extends ResourceIdService
{
    public static final String PERMISSION_READ_IDENTITY = "READ_IDENTITY";
    public static final String PERMISSION_WRITE_IDENTITY = "WRITE_IDENTITY";
    public static final String RESOURCE_TYPE = "IDENTITY_AGENT";
    private static final String PROPERTY_LABEL_RESOURCE_TYPE = "identitystoreagent.identity_agent.resourceType";
    private static final String PROPERTY_LABEL_READ_IDENTITY = "identitystoreagent.identity_agent.permission.label.read";
    private static final String PROPERTY_LABEL_WRITE_IDENTITY = "identitystoreagent.identity_agent.permission.label.write";

    public IdentityAgentManagementResourceIdService( )
    {
        setPluginName( IdentityStoreAgentPlugin.PLUGIN_NAME );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register( )
    {
        ResourceType rt = new ResourceType( );
        rt.setResourceIdServiceClass( IdentityAgentManagementResourceIdService.class.getName( ) );
        rt.setPluginName( IdentityStoreAgentPlugin.PLUGIN_NAME );
        rt.setResourceTypeKey( RESOURCE_TYPE );
        rt.setResourceTypeLabelKey( PROPERTY_LABEL_RESOURCE_TYPE );

        Permission p = new Permission( );
        p.setPermissionKey( PERMISSION_READ_IDENTITY );
        p.setPermissionTitleKey( PROPERTY_LABEL_READ_IDENTITY );
        rt.registerPermission( p );

        p = new Permission( );
        p.setPermissionKey( PERMISSION_WRITE_IDENTITY );
        p.setPermissionTitleKey( PROPERTY_LABEL_WRITE_IDENTITY );
        rt.registerPermission( p );

        ResourceTypeManager.registerResourceType( rt );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList getResourceIdList( Locale locale )
    {
        ApplicationRightsDto appRights = IdentityUtils.getApplicationRights( );
        ReferenceList refList = new ReferenceList( );
        if ( appRights != null )
        {
            for ( AppRightDto appRight : appRights.getAppRights( ) )
            {
                refList.addItem( appRight.getAttributeKey( ),
                        I18nService.getLocalizedString( IdentityConstants.PROPERTY_ATTR_LABEL_PREFIX + appRight.getAttributeKey( ), locale ) );
            }
        }
        return refList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( String strId, Locale locale )
    {
        return I18nService.getLocalizedString( IdentityConstants.PROPERTY_ATTR_LABEL_PREFIX + strId, locale );
    }

}
