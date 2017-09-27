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

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.identitystore.web.rs.dto.AppRightDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.ApplicationRightsDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.IdentityDto;
import fr.paris.lutece.plugins.identitystoreagent.service.IdentityAgentManagementResourceIdService;
import fr.paris.lutece.plugins.identitystoreagent.utils.IdentityConstants;
import fr.paris.lutece.plugins.identitystoreagent.utils.IdentityUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import net.sf.json.JSONObject;

/**
 * Main Controller for manage customer identity for an agent
 */
@Controller( controllerJsp = "ManageCustomerIdentity.jsp", controllerPath = "jsp/admin/plugins/identitystore/agent", right = "AGENT_IDS_MANAGEMENT" )
public class ManageCustomerIdentityJspBean extends MVCAdminJspBean
{
    private static final String TEMPLATE_VIEW_CUSTOMER_IDENTITY = "/admin/plugins/identitystore/agent/view_customer_identity.html";
    private static final String PROPERTY_PAGE_TITLE_CUSTOMER_IDENTITY = "identitystoreagent.customer_identity.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CUSTOMER_IDENTITY_MODIFY = "identitystoreagent.customer_identity.modify.pageTitle";
    private static final String VIEW_CUSTOMER_IDENTITY = "customerIdentity";
    private static final String VIEW_MODIFY_IDENTITY = "modifyIdentity";
    private static final String ACTION_MODIFY_IDENTITY = "modifyIdentity";
    private static final String ACTION_CERTIFY_ATTRIBUTE = "certifyAttribute";
    private static final String ACTION_CERTIFY_ATTRIBUTE_AJAX = "certifyAttributeAjax";
    private static final String INFO_IDENTITY_UPDATED = "identitystoreagent.info.identity.updated";
    private static final String INFO_IDENTITY_NOT_UPDATED = "identitystoreagent.info.identity.not_updated";
    private static final String INFO_CERTIFIER_OK = "identitystoreagent.info.certifier.ok";
    private static final String ERROR_IDENTITY_NOT_FOUND = "identitystoreagent.error.identity.not_found";
    private static final String ERROR_IDENTITY_LATER = "identitystoreagent.error.identity.later";
    private static final long serialVersionUID = 1L;
    private Map<String, ReferenceList> _mapReferenceList;
    private List<AppRightDto> _listAttributRight;
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

        // erase _listAttributRight
        _listAttributRight = null;
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
        if ( StringUtils.isEmpty( _strConnectionId ) && StringUtils.isEmpty( _strCustomerId ) )
        {
            addError( ERROR_IDENTITY_NOT_FOUND, getLocale( ) );
            return getPage( PROPERTY_PAGE_TITLE_CUSTOMER_IDENTITY, TEMPLATE_VIEW_CUSTOMER_IDENTITY );
        }
        IdentityDto identityView = IdentityUtils.getIdentity( _strConnectionId, _strCustomerId );
        if ( identityView == null )
        {
            addError( ERROR_IDENTITY_NOT_FOUND, getLocale( ) );
            return getPage( PROPERTY_PAGE_TITLE_CUSTOMER_IDENTITY, TEMPLATE_VIEW_CUSTOMER_IDENTITY );
        }
        Map<String, Object> model = getModel( );
        model.put( IdentityConstants.MARK_IDENTITY, identityView );
        model.put( IdentityConstants.MARK_VIEW_MODE, true );
        return getPage( PROPERTY_PAGE_TITLE_CUSTOMER_IDENTITY, TEMPLATE_VIEW_CUSTOMER_IDENTITY, model );
    }

    /**
     * modify identity view
     * 
     * @param request
     * @return the page
     */
    @View( value = VIEW_MODIFY_IDENTITY )
    public String getModifyIdentity( HttpServletRequest request )
    {
        // check session init
        if ( StringUtils.isEmpty( _strConnectionId ) && StringUtils.isEmpty( _strCustomerId ) )
        {
            return getCustomerIdentity( request );
        }
        // retrieve identity for modify
        IdentityDto identityModify = IdentityUtils.getIdentity( _strConnectionId, _strCustomerId );
        if ( identityModify == null )
        {
            addError( ERROR_IDENTITY_NOT_FOUND, getLocale( ) );
            return getPage( PROPERTY_PAGE_TITLE_CUSTOMER_IDENTITY, TEMPLATE_VIEW_CUSTOMER_IDENTITY );
        }
        Map<String, Object> model = getModel( );
        model.put( IdentityConstants.MARK_IDENTITY, identityModify );
        model.put( IdentityConstants.MARK_VIEW_MODE, false );
        return getPage( PROPERTY_PAGE_TITLE_CUSTOMER_IDENTITY_MODIFY, TEMPLATE_VIEW_CUSTOMER_IDENTITY, model );
    }

    /**
     * modify identity action
     * 
     * @param request
     * @return redirect to default view
     */
    @Action( ACTION_MODIFY_IDENTITY )
    public String doModifyIdentity( HttpServletRequest request )
    {
        String strRedirectView = VIEW_CUSTOMER_IDENTITY;
        Map<String, String> mapParameters = new HashMap<>( );
        // control session is init
        if ( StringUtils.isNotEmpty( _strConnectionId ) || StringUtils.isNotEmpty( _strCustomerId ) )
        {
            mapParameters.put( IdentityConstants.PARAMETER_CONNECTION_ID, _strConnectionId );
            mapParameters.put( IdentityConstants.PARAMETER_CUSTOMER_ID, _strCustomerId );
            try
            {
                boolean bUpdated = IdentityUtils.updateIdentity( _strConnectionId, _strCustomerId, getUser( ), _listAttributRight, request );
                if ( bUpdated )
                {
                    addInfo( INFO_IDENTITY_UPDATED, getLocale( ) );
                }
                else
                {
                    addInfo( INFO_IDENTITY_NOT_UPDATED, getLocale( ) );
                }
            }
            catch( Exception e )
            {
                strRedirectView = VIEW_MODIFY_IDENTITY;
                addError( ERROR_IDENTITY_LATER, getLocale( ) );
            }
        }

        return redirect( request, strRedirectView, mapParameters );
    }

    /**
     * Certification action, return message in default after certification try
     * 
     * @param request
     * @return redirect to default view
     */
    @Action( ACTION_CERTIFY_ATTRIBUTE )
    public String doCertifyAttribute( HttpServletRequest request )
    {
        Map<String, String> mapParameters = new HashMap<>( );
        // control session is init
        if ( StringUtils.isNotEmpty( _strConnectionId ) || StringUtils.isNotEmpty( _strCustomerId ) )
        {
            mapParameters.put( IdentityConstants.PARAMETER_CONNECTION_ID, _strConnectionId );
            mapParameters.put( IdentityConstants.PARAMETER_CUSTOMER_ID, _strCustomerId );
            String strAttributeKey = request.getParameter( IdentityConstants.PARAMETER_ATTRIBUTE_KEY );
            AppRightDto attributeRight = null;
            for ( AppRightDto appRightDto : _listAttributRight )
            {
                if ( appRightDto.getAttributeKey( ).equals( strAttributeKey ) )
                {
                    attributeRight = appRightDto;
                    break;
                }
            }
            String strAttributeLabel = I18nService.getLocalizedString( IdentityConstants.PROPERTY_ATTR_LABEL_PREFIX + strAttributeKey, getLocale( ) );
            String strCertifierResult = IdentityUtils.agentCertification( _strConnectionId, _strCustomerId, getUser( ), attributeRight, getLocale( ) );
            if ( strCertifierResult == null )
            {
                addInfo( I18nService.getLocalizedString( INFO_CERTIFIER_OK, new Object [ ] {
                    strAttributeLabel
                }, getLocale( ) ) );
            }
            else
            {
                addError( strCertifierResult );
            }
        }

        return redirect( request, VIEW_CUSTOMER_IDENTITY, mapParameters );
    }

    /**
     * same as doCertifyAttribute, but return JSON for Ajax request
     * 
     * @param request
     * @return JSON
     */
    @Action( ACTION_CERTIFY_ATTRIBUTE_AJAX )
    public String doCertifyAttributeAjax( HttpServletRequest request )
    {
        JSONObject json = new JSONObject( );
        String strMessage = null;
        String strAttributeKey = request.getParameter( IdentityConstants.PARAMETER_ATTRIBUTE_KEY );
        String strAttributeLabel = I18nService.getLocalizedString( IdentityConstants.PROPERTY_ATTR_LABEL_PREFIX + strAttributeKey, getLocale( ) );

        if ( StringUtils.isNotEmpty( _strConnectionId ) || StringUtils.isNotEmpty( _strCustomerId ) )
        {
            AppRightDto attributeRight = null;
            for ( AppRightDto appRightDto : _listAttributRight )
            {
                if ( appRightDto.getAttributeKey( ).equals( strAttributeKey ) )
                {
                    attributeRight = appRightDto;
                    break;
                }
            }
            strMessage = IdentityUtils.agentCertification( _strConnectionId, _strCustomerId, getUser( ), attributeRight, getLocale( ) );
        }

        if ( strMessage == null )
        {
            strMessage = I18nService.getLocalizedString( INFO_CERTIFIER_OK, new Object [ ] {
                strAttributeLabel
            }, getLocale( ) );
            json.put( "status", "success" );
            json.put( "message", strMessage );
        }
        else
        {
            json.put( "status", "error" );
            json.put( "message", strMessage );
        }

        return json.toString( );
    }

    /**
     * init model
     * 
     * @param bViewMode
     *            true if model is for view
     */
    @Override
    protected Map<String, Object> getModel( )
    {
        Map<String, Object> model = super.getModel( );

        model.put( IdentityConstants.MARK_MAP_REFERENCE_LIST, _mapReferenceList );
        model.put( IdentityConstants.MARK_ATTR_LIST, _listAttributRight );

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
        if ( _listAttributRight == null )
        {
            _listAttributRight = new ArrayList<AppRightDto>( );
            ApplicationRightsDto appRightsDto = IdentityUtils.getApplicationRights( );

            if ( appRightsDto != null )
            {
                for ( String strAttributeKey : IdentityConstants.PROPERTY_IDS_VIEW_ATTR_LIST )
                {
                    for ( AppRightDto appRight : appRightsDto.getAppRights( ) )
                    {
                        if ( appRight != null
                                && StringUtils.equals( strAttributeKey, appRight.getAttributeKey( ) )
                                && appRight.isReadable( )
                                && RBACService.isAuthorized( IdentityAgentManagementResourceIdService.RESOURCE_TYPE, strAttributeKey,
                                        IdentityAgentManagementResourceIdService.PERMISSION_READ_IDENTITY, getUser( ) ) )
                        {

                            if ( appRight.isWritable( ) )
                            {
                                appRight.setWritable( RBACService.isAuthorized( IdentityAgentManagementResourceIdService.RESOURCE_TYPE, strAttributeKey,
                                        IdentityAgentManagementResourceIdService.PERMISSION_WRITE_IDENTITY, getUser( ) ) );
                            }
                            List<String> listCertifiers = new ArrayList<>( );
                            if ( appRight.getCertifiers( ) != null && appRight.getCertifiers( ).contains( IdentityConstants.AGENT_CERTIFIER_CODE ) )
                            {
                                listCertifiers.add( IdentityConstants.AGENT_CERTIFIER_CODE );
                            }
                            appRight.setCertifiers( listCertifiers );

                            _listAttributRight.add( appRight );
                            break;
                        }
                    }
                }
            }
        }
    }
}
