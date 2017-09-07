<jsp:useBean id="manageCustomerIdentity" scope="session" class="fr.paris.lutece.plugins.identitystoreagent.web.ManageCustomerIdentityJspBean" />

<% String strContent = manageCustomerIdentity.processController ( request , response ); %>

<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../../AdminFooter.jsp" %>