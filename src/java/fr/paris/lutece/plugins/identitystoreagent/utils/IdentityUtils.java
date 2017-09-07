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

import java.util.HashMap;

import fr.paris.lutece.plugins.identitystore.web.rs.dto.ApplicationRightsDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.AttributeDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.IdentityDto;
import fr.paris.lutece.plugins.identitystore.web.service.IdentityService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * Utils class for manage identity from identitystore
 */
public final class IdentityUtils
{
    private static IdentityService _identityService = (IdentityService) SpringContextService.getBean( "identitystoreagent.identitystore.service" );
    private static String _strApplicationCode = AppPropertiesService.getProperty( "identitystoreagent.application.code" );

    /**
     * Retrieve IdentityDto for the given customer for 'view' purpose
     * 
     * @param strConnectionId
     * @param strCustomerId
     * @param user
     * @return IdentityDto
     */
    public static IdentityDto getIdentityForView( String strConnectionId, String strCustomerId, AdminUser user )
    {
        return getIdentity( strConnectionId, strCustomerId, user );
    }

    /**
     * Merge IdentityDto from identitystore with Rbac right of the admin user
     * 
     * @param strConnectionId
     * @param strCustomerId
     * @param user
     * @return IdentityDto
     */
    private static IdentityDto getIdentity( String strConnectionId, String strCustomerId, AdminUser user )
    {
        IdentityDto identity = null;
        try
        {
            IdentityDto identityFull = _identityService.getIdentity( strConnectionId, strCustomerId, _strApplicationCode );
            identity = new IdentityDto( );
            identity.setConnectionId( identityFull.getConnectionId( ) );
            identity.setCustomerId( identityFull.getCustomerId( ) );
            identity.setAttributes( new HashMap<>( ) );
            for ( String strAttrKey : identityFull.getAttributes( ).keySet( ) )
            {
                AttributeDto attrDto = (AttributeDto) identityFull.getAttributes( ).get( strAttrKey );
                if ( RBACService.isAuthorized( "IDENTITY_AGENT", strAttrKey, "READ_IDENTITY", user ) )
                {
                    identity.getAttributes( ).put( strAttrKey, attrDto );
                }
            }
        }
        catch( AppException e )
        {
            AppLogService.error( "Unable to merge identity and RBAC " );
        }
        return identity;
    }

    /**
     * Retrieve application rights for the configured application code
     * 
     * @return ApplicationRightsDto object
     */
    public static ApplicationRightsDto getApplicationRights( )
    {
        ApplicationRightsDto appRightsDto = null;
        try
        {
            appRightsDto = _identityService.getApplicationRights( _strApplicationCode );
        }
        catch( AppException e )
        {
            AppLogService.error( "Unable to retrieve application rights for app " + _strApplicationCode );
        }
        return appRightsDto;
    }
}
