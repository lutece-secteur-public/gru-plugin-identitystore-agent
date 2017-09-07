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
import java.util.Map;

import org.junit.Test;

import fr.paris.lutece.plugins.identitystore.web.rs.dto.AttributeDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.AuthorDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.IdentityChangeDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.IdentityDto;
import fr.paris.lutece.plugins.identitystore.web.service.AuthorType;
import fr.paris.lutece.plugins.identitystore.web.service.IdentityService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test IdentityUtils, require RBAC and IDS
 */
public class IdentityUtilsTest extends LuteceTestCase
{
    private String _strConnectionId = "conn_1";
    private String _strCustomerId = "cust_1";
    private AdminUser _user = null;

    public IdentityUtilsTest( )
    {
        super( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );

        IdentityService identityService = (IdentityService) SpringContextService.getBean( "identitystoreagent.identitystore.service" );
        if ( identityService == null )
        {
            throw new AppException( "identitystoreagent.identitystore.service is not defined" );
        }
        // retrieve admin user
        _user = AdminUserHome.findByPrimaryKey( 1 );
        // init rbac for admin user

        // add one identity in mock
        AuthorDto author = new AuthorDto( );
        author.setApplicationCode( "IDS_AGENT" );
        author.setApplicationName( "IDS_AGENT" );
        author.setEmail( "strEmail" );
        author.setType( AuthorType.TYPE_APPLICATION.getTypeValue( ) );
        author.setUserName( "strUserName" );

        IdentityDto identity = new IdentityDto( );
        identity.setConnectionId( _strConnectionId );
        identity.setCustomerId( _strCustomerId );
        AttributeDto attrDto = new AttributeDto( );
        attrDto.setCertified( false );
        attrDto.setKey( "attrKey" );
        attrDto.setValue( "strValue" );
        Map<String, AttributeDto> mapAttrDto = new HashMap<String, AttributeDto>( );
        mapAttrDto.put( attrDto.getKey( ), attrDto );
        identity.setAttributes( mapAttrDto );

        IdentityChangeDto identityChange = new IdentityChangeDto( );
        identityChange.setAuthor( author );
        identityChange.setIdentity( identity );

        identityService.createIdentity( identityChange );
    }

    @Test
    public void testGetIdentityForView( )
    {
        IdentityDto identity = IdentityUtils.getIdentityForView( _strConnectionId, _strCustomerId, _user );
        // because appRights is not managed by mock identity shouldn't have attributes
        assertTrue( "attributes map is not empty", identity.getAttributes( ).size( ) == 0 );
    }

    @Test
    public void testGetApplicationRights( )
    {
        // untestable with mock at the moment
    }
}
