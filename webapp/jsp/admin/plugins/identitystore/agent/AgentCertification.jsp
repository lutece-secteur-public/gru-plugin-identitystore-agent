<jsp:useBean id="manageCustomerIdentity" scope="session" class="fr.paris.lutece.plugins.identitystoreagent.web.ManageCustomerIdentityJspBean" />

<% String strContent = manageCustomerIdentity.processController ( request , response ); %>

<%= strContent %>