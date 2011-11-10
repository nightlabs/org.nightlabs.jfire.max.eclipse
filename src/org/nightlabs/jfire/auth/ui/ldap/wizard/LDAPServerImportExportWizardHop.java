package org.nightlabs.jfire.auth.ui.ldap.wizard;

import java.util.Collection;
import java.util.HashSet;

import javax.security.auth.login.LoginException;

import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.jfire.auth.ui.ldap.LdapUIPlugin;
import org.nightlabs.jfire.auth.ui.ldap.resource.Messages;
import org.nightlabs.jfire.auth.ui.wizard.GenericExportWizardPage;
import org.nightlabs.jfire.auth.ui.wizard.ISynchronizationPerformerHop;
import org.nightlabs.jfire.auth.ui.wizard.ImportExportWizard;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPServer;
import org.nightlabs.jfire.base.security.integration.ldap.sync.LDAPSyncEvent;
import org.nightlabs.jfire.base.security.integration.ldap.sync.LDAPSyncEvent.FetchEventTypeDataUnit;
import org.nightlabs.jfire.base.security.integration.ldap.sync.LDAPSyncEvent.SendEventTypeDataUnit;
import org.nightlabs.jfire.security.GlobalSecurityReflector;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystemCommunicationException;
import org.nightlabs.jfire.security.integration.UserManagementSystemManagerRemote;
import org.nightlabs.jfire.security.integration.UserManagementSystemSyncEvent.SyncEventGenericType;
import org.nightlabs.jfire.security.integration.UserManagementSystemSyncException;

/**
 * Implementation if {@link ISynchronizationPerformerHop} for running synchronization for {@link LDAPServer} instances.
 * Contributes two pages to {@link ImportExportWizard}: for import and for export. Only one of them is shown depending
 * on given {@link SyncDirection}.
 *  
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class LDAPServerImportExportWizardHop extends WizardHop implements ISynchronizationPerformerHop{
	
	private LDAPServerImportWizardPage importWizardPage;
	private GenericExportWizardPage exportWizardPage;
	
	/**
	 * Default constructor
	 */
	public LDAPServerImportExportWizardHop() {	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configurePages(UserManagementSystem<?> userManagementSystem, SyncDirection syncDirection) {
		if ( !(userManagementSystem instanceof LDAPServer) ){
			throw new IllegalArgumentException(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerImportExportWizardHop.inputNotLDAPServerExceptionText")); //$NON-NLS-1$
		}
		removeAllHopPages();
		if (SyncDirection.IMPORT.equals(syncDirection)){
			LDAPServerImportWizardPage importPage = getImportPage();
			importPage.setLdapConnectionParamsProvider((LDAPServer) userManagementSystem);
			setEntryPage(importPage);
		}else if (SyncDirection.EXPORT.equals(syncDirection)){
			setEntryPage(getExportPage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performSynchronization(UserManagementSystem<?> userManagementSystem, SyncDirection syncDirection) throws LoginException, UserManagementSystemCommunicationException {
		if ( !(userManagementSystem instanceof LDAPServer) ){
			throw new IllegalArgumentException(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerImportExportWizardHop.inputNotLDAPServerExceptionText")); //$NON-NLS-1$
		}
		
		String organisationID = GlobalSecurityReflector.sharedInstance().getUserDescriptor().getOrganisationID();
		LDAPServer ldapServer = (LDAPServer) userManagementSystem;
		LDAPSyncEvent syncEvent = null;

		try {

			if (SyncDirection.IMPORT.equals(syncDirection)){
				
				syncEvent = new LDAPSyncEvent(SyncEventGenericType.FETCH);
				syncEvent.setOrganisationID(organisationID);
				Collection<String> entriesToSync = null;
				if (getImportPage().shouldImportAll()){
					entriesToSync = ldapServer.getAllEntriesForSync();
				}else{
					entriesToSync = getImportPage().getSelectedEntries();
				}
				Collection<FetchEventTypeDataUnit> dataUnits = new HashSet<FetchEventTypeDataUnit>();
				for (String ldapName : entriesToSync){
					dataUnits.add(new FetchEventTypeDataUnit(ldapName));
				}
				syncEvent.setFetchEventTypeDataUnits(dataUnits);
				
			}else if (SyncDirection.EXPORT.equals(syncDirection)){
				
				syncEvent = new LDAPSyncEvent(SyncEventGenericType.SEND);
				syncEvent.setOrganisationID(organisationID);
				Collection<Object> objectIDsToSync = null;
				if (getExportPage().shouldExportAll()){
					
					UserManagementSystemManagerRemote remoteBean = JFireEjb3Factory.getRemoteBean(UserManagementSystemManagerRemote.class, GlobalSecurityReflector.sharedInstance().getInitialContextProperties());
					objectIDsToSync = remoteBean.getAllUserManagementSystemRelatedEntityIDs();
					
				}else{
					objectIDsToSync = getExportPage().getSelectedObjectIDs();	
				}
				Collection<SendEventTypeDataUnit> dataUnits = new HashSet<SendEventTypeDataUnit>();
				for (Object objectId : objectIDsToSync){
					dataUnits.add(new SendEventTypeDataUnit(objectId));
				}
				syncEvent.setSendEventTypeDataUnits(dataUnits);
				
			}else{
				throw new IllegalArgumentException(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerImportExportWizardHop.unknownSyncDirectionExceptionText")); //$NON-NLS-1$
			}
			
			if ((syncEvent.getSendEventTypeDataUnits() != null && !syncEvent.getSendEventTypeDataUnits().isEmpty())
					|| (syncEvent.getFetchEventTypeDataUnits() != null && !syncEvent.getFetchEventTypeDataUnits().isEmpty())){
				
				UserManagementSystemManagerRemote remoteBean = JFireEjb3Factory.getRemoteBean(
						UserManagementSystemManagerRemote.class, GlobalSecurityReflector.sharedInstance().getInitialContextProperties());
				remoteBean.runLDAPServerSynchronization(
						ldapServer.getUserManagementSystemObjectID(), syncEvent);
				
			}
			
		} catch (UserManagementSystemSyncException e) {
			throw new UserManagementSystemCommunicationException(e);
		}
	}
	
	private LDAPServerImportWizardPage getImportPage() {
		if (importWizardPage == null){
			importWizardPage = new LDAPServerImportWizardPage();
		}
		return importWizardPage;
	}
	
	private GenericExportWizardPage getExportPage() {
		if (exportWizardPage == null){
			exportWizardPage = new GenericExportWizardPage();
			exportWizardPage.setImageDescriptor(SharedImages.getWizardPageImageDescriptor(LdapUIPlugin.sharedInstance(), LDAPServerImportExportWizardHop.class));
		}
		return exportWizardPage;
	}
	
}
