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
package fr.paris.lutece.plugins.identitystoreagent.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.plexus.util.StringUtils;

import fr.paris.lutece.plugins.identitystore.web.rs.dto.AppRightDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.ApplicationRightsDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.IdentityDto;
import fr.paris.lutece.plugins.identitystoreagent.utils.IdentityConstants;
import fr.paris.lutece.plugins.identitystoreagent.utils.IdentityUtils;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

/**
 * Main Controller for manage customer identity for an agent
 */
@Controller( controllerJsp = "ManageCustomerIdentityJspBean.jsp", controllerPath = "jsp/admin/plugins/identitystore/agent", right = "AGENT_IDS_MANAGEMENT" )
public class ManageCustomerIdentityJspBean extends MVCAdminJspBean
{
    private static final String TEMPLATE_VIEW_CUSTOMER_IDENTITY = "/admin/plugins/identitystore/agent/view_customer_identity.html";
    private static final String PROPERTY_PAGE_TITLE_CUSTOMER_IDENTITY = "identitystoreagent.customer_identity.pageTitle";
    private static final String VIEW_CUSTOMER_IDENTITY = "customerIdentity";
    private static final String ERROR_IDENTITY_NOT_FOUND = "identitystoreagent.error.identity.not_found";
    private static final long serialVersionUID = 1L;
    private Map<String, ReferenceList> _mapReferenceList;
    private List<String> _listAttributeDisplayed;
    private String _strConnectionId;
    private String _strCustomerId;

    /**
     * Base constructor with reference list init
     */
    public ManageCustomerIdentityJspBean( )
    {
        // init map of referencelist
        this._mapReferenceList = new HashMap<>( );
        for ( String strRefListKey : IdentityConstants.PROPERTY_IDS_REFERENCE_LIST.split( IdentityConstants.SPLIT_PATTERN ) )
        {
            String strPropListRef = AppPropertiesService.getProperty( IdentityConstants.PROPERTY_IDS_REFERENCE_LIST_PREFIX + strRefListKey );
            String strPropListRefKeys = AppPropertiesService.getProperty( IdentityConstants.PROPERTY_IDS_REFERENCE_LIST_PREFIX_KEY + strRefListKey );
            if ( strPropListRef == null )
            {
                AppLogService.error( "Property " + IdentityConstants.PROPERTY_IDS_REFERENCE_LIST_PREFIX + strRefListKey + " not found " );
            }
            else
            {
                String [ ] listRef = strPropListRef.split( IdentityConstants.SPLIT_PATTERN );
                String [ ] listRefKeys = new String [ 0];
                if ( strPropListRefKeys != null )
                {
                    listRefKeys = strPropListRefKeys.split( IdentityConstants.SPLIT_PATTERN );
                    if ( listRef.length != listRefKeys.length )
                    {
                        AppLogService.error( "Properties " + IdentityConstants.PROPERTY_IDS_REFERENCE_LIST_PREFIX + strRefListKey + " and "
                                + IdentityConstants.PROPERTY_IDS_REFERENCE_LIST_PREFIX_KEY + strRefListKey + " don't have same size " );
                        continue;
                    }
                }
                ReferenceList refList = new ReferenceList( );
                for ( int nIdx = 0; nIdx < listRef.length; nIdx++ )
                {
                    ReferenceItem refItem = new ReferenceItem( );
                    refItem.setName( listRef [nIdx] );
                    if ( listRefKeys.length > 0 )
                    {
                        refItem.setCode( listRefKeys [nIdx] );
                    }
                    else
                    {
                        refItem.setCode( listRef [nIdx] );
                    }
                    refList.add( refItem );
                }
                if ( refList.size( ) > 0 )
                {
                    this._mapReferenceList.put( strRefListKey, refList );
                }
            }
        }

        // erase _listAttributeDisplayed
        _listAttributeDisplayed = null;
    }

    /**
     * default view
     * 
     * @param request
     * @return
     */
    @View( value = VIEW_CUSTOMER_IDENTITY, defaultView = true )
    public String getCustomerIdentity( HttpServletRequest request )
    {
        initFromRequest( request );
        IdentityDto identityView = IdentityUtils.getIdentityForView( _strConnectionId, _strCustomerId, getUser( ) );
        if ( identityView == null )
        {
            addError( ERROR_IDENTITY_NOT_FOUND, getLocale( ) );
            return getPage( PROPERTY_PAGE_TITLE_CUSTOMER_IDENTITY, TEMPLATE_VIEW_CUSTOMER_IDENTITY );
        }
        Map<String, Object> model = getModel( );
        model.put( IdentityConstants.MARK_IDENTITY, identityView );
        return getPage( PROPERTY_PAGE_TITLE_CUSTOMER_IDENTITY, TEMPLATE_VIEW_CUSTOMER_IDENTITY, model );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Object> getModel( )
    {
        Map<String, Object> model = super.getModel( );

        model.put( IdentityConstants.MARK_MAP_REFERENCE_LIST, _mapReferenceList );
        model.put( IdentityConstants.MARK_VIEW_ATTR_LIST, _listAttributeDisplayed );

        return model;
    }

    /**
     * init session parameter
     * 
     * @param request
     */
    private void initFromRequest( HttpServletRequest request )
    {
        this._strConnectionId = "";
        this._strCustomerId = "";
        if ( request.getParameter( IdentityConstants.PARAMETER_CONNECTION_ID ) != null )
        {
            this._strConnectionId = request.getParameter( IdentityConstants.PARAMETER_CONNECTION_ID );
        }
        if ( request.getParameter( IdentityConstants.PARAMETER_CUSTOMER_ID ) != null )
        {
            this._strCustomerId = request.getParameter( IdentityConstants.PARAMETER_CUSTOMER_ID );
        }

        // merge property, application right and RBAC
        if ( _listAttributeDisplayed == null )
        {
            _listAttributeDisplayed = new ArrayList<String>( );
            ApplicationRightsDto appRightsDto = IdentityUtils.getApplicationRights( );
            for ( String strAttributeKey : IdentityConstants.PROPERTY_IDS_VIEW_ATTR_LIST )
            {
                for ( AppRightDto appRight : appRightsDto.getAppRights( ) )
                {
                    if ( appRight != null && StringUtils.equals( strAttributeKey, appRight.getAttributeKey( ) )
                            && RBACService.isAuthorized( "IDENTITY_AGENT", strAttributeKey, "READ_IDENTITY", getUser( ) ) )
                    {
                        _listAttributeDisplayed.add( strAttributeKey );
                        break;
                    }
                }
            }
        }
    }
}
