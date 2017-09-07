--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'AGENT_IDS_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('AGENT_IDS_MANAGEMENT','identitystoreagent.adminFeature.ManageCustomerIdentity.name',0,'jsp/admin/plugins/identitystore/agent/ManageCustomerIdentity.jsp','identitystoreagent.adminFeature.ManageCustomerIdentity.description',0,'identitystoreagent',NULL,NULL,NULL,1);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'AGENT_IDS_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('AGENT_IDS_MANAGEMENT',1);