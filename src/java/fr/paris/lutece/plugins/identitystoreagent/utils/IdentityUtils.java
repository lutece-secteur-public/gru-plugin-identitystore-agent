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
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.identitystore.web.rs.dto.AppRightDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.ApplicationRightsDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.AttributeDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.AuthorDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.IdentityChangeDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.IdentityDto;
import fr.paris.lutece.plugins.identitystore.web.service.AuthorType;
import fr.paris.lutece.plugins.identitystore.web.service.IdentityService;
import fr.paris.lutece.portal.business.user.AdminUser;
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
    private static String _strApplicationName = AppPropertiesService.getProperty( "identitystoreagent.application.name" );
    
    /**
     * private constructor
     */
    private IdentityUtils( )
    {
    }

    /**
     * Retrieve IdentityDto for the given customer
     * 
     * @param strConnectionId
     * @param strCustomerId
     * @return IdentityDto
     */
    public static IdentityDto getIdentity( String strConnectionId, String strCustomerId )
    {
        return _identityService.getIdentity( strConnectionId, strCustomerId, _strApplicationCode );
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

	/**
	 * make an updateIdentity from request
	 * 
	 * @param _strConnectionId
	 * @param _strCustomerId
	 * @param user
	 * @param listAttributeModify
	 * @param request
	 */
	public static boolean updateIdentity( String strConnectionId, String strCustomerId, AdminUser user, List<AppRightDto> listAttributRight, HttpServletRequest request )
	{
		if( listAttributRight == null || listAttributRight.size( ) == 0 )
		{
			return false;
		}
		IdentityChangeDto identityChange = new IdentityChangeDto( );
		
		AuthorDto author = new AuthorDto( );
		author.setApplicationCode( _strApplicationCode );
		author.setApplicationCode( _strApplicationName );
		author.setEmail( user.getEmail( ) );
		author.setType( AuthorType.TYPE_USER_ADMINISTRATOR.getTypeValue( ) );
		author.setUserName( user.getLastName( ) + " " + user.getFirstName( ) );
		identityChange.setAuthor( author );
		
		IdentityDto identityBase = getIdentity( strConnectionId, strCustomerId );
		IdentityDto identityUpdate = new IdentityDto( );
		identityUpdate.setAttributes( new HashMap<String, AttributeDto>() );
		if( identityBase != null )
		{
			identityUpdate.setConnectionId( identityBase.getConnectionId( ) );
			identityUpdate.setCustomerId( identityBase.getCustomerId( ) );
		}
		if( identityBase.getAttributes( ) != null )
		{
			for ( AppRightDto appRight : listAttributRight )
			{
				String strAttrKey = appRight.getAttributeKey( );
				String strNewValue = request.getParameter( strAttrKey );
				if( strNewValue!=null && appRight.isWritable( ) )
				{
					AttributeDto attribute = new AttributeDto( );
					attribute.setKey( appRight.getAttributeKey( ) );
					attribute.setValue( strNewValue );
					if( identityBase.getAttributes( ).containsKey( strAttrKey ) )
					{
						// add update only if distinct
						if( strNewValue!=null && !StringUtils.equals( identityBase.getAttributes( ).get( strAttrKey ).getValue( ), strNewValue ) )
						{
							identityUpdate.getAttributes( ).put( strAttrKey, attribute );
						}
					}
					else
					{
						identityUpdate.getAttributes( ).put( strAttrKey, attribute );
					}
				}
			}
		}
		
		if( identityUpdate.getAttributes( ).size( )>0)
		{
			identityChange.setIdentity( identityUpdate );
			_identityService.updateIdentity( identityChange, null );
			return true;
		}
		else
		{
			return false;
		}
	}
}
