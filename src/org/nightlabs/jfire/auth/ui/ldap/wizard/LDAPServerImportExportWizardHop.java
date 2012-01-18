package org.nightlabs.jfire.auth.ui.ldap.wizard;

import java.util.Collection;
import java.util.HashSet;

import javax.jdo.FetchPlan;
import javax.jdo.JDODetachedFieldAccessException;
import javax.security.auth.login.LoginException;

import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.jfire.auth.ui.ldap.LdapUIPlugin;
import org.nightlabs.jfire.auth.ui.ldap.resource.Messages;
import org.nightlabs.jfire.auth.ui.wizard.GenericExportWizardPage;
import org.nightlabs.jfire.auth.ui.wizard.ISynchronizationPerformerHop;
import org.nightlabs.jfire.auth.ui.wizard.ImportExportWizard;
import org.nightlabs.jfire.auth.ui.wizard.ISynchronizationPerformerHop.SyncDirection;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPServer;
import org.nightlabs.jfire.base.security.integration.ldap.sync.LDAPSyncEvent;
import org.nightlabs.jfire.base.security.integration.ldap.sync.LDAPSyncEvent.FetchEventTypeDataUnit;
import org.nightlabs.jfire.base.security.integration.ldap.sync.LDAPSyncEvent.SendEventTypeDataUnit;
import org.nightlabs.jfire.security.GlobalSecurityReflector;
import org.nightlabs.jfire.security.dao.UserManagementSystemDAO;
import org.nightlabs.jfire.security.integration.SynchronizableUserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystemCommunicationException;
import org.nightlabs.jfire.security.integration.UserManagementSystemManagerRemote;
import org.nightlabs.jfire.security.integration.UserManagementSystemSyncEvent.SyncEventGenericType;
import org.nightlabs.jfire.security.integration.UserManagementSystemSyncException;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

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
	public void configurePages(SynchronizableUserManagementSystem<?> userManagementSystem, SyncDirection syncDirection) {
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
	public void performSynchronization(SynchronizableUserManagementSystem<?> userManagementSystem, SyncDirection syncDirection, ProgressMonitor monitor) throws LoginException, UserManagementSystemCommunicationException {
		if ( !(userManagementSystem instanceof LDAPServer) ){
			throw new IllegalArgumentException(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.wizard.LDAPServerImportExportWizardHop.inputNotLDAPServerExceptionText")); //$NON-NLS-1$
		}
		
		LDAPServer ldapServer = (LDAPServer) userManagementSystem;
		LDAPSyncEvent syncEvent = null;

		try {
			String monitorMessage = SyncDirection.IMPORT.equals(syncDirection) ? Messages.getString("org.nightlabs.jfire.auth.ui.wizard.ImportExportWizard.monitorMessageImport") : Messages.getString("org.nightlabs.jfire.auth.ui.wizard.ImportExportWizard.monitorMessageExport"); //$NON-NLS-1$ //$NON-NLS-2$
			monitor.beginTask(monitorMessage, 2);
			
			if (SyncDirection.IMPORT.equals(syncDirection)){
				
				syncEvent = new LDAPSyncEvent(SyncEventGenericType.FETCH_USER);
				Collection<String> entriesToSync = null;
				if (getImportPage().shouldImportAll()){
					try{
						ldapServer.getLdapScriptSet();
					}catch(JDODetachedFieldAccessException e){
						ldapServer = (LDAPServer) UserManagementSystemDAO.sharedInstance().getUserManagementSystem(
								ldapServer.getUserManagementSystemObjectID(), 
								new String[]{
									FetchPlan.DEFAULT, LDAPServer.FETCH_GROUP_LDAP_SCRIPT_SET, 
									UserManagementSystem.FETCH_GROUP_TYPE, UserManagementSystem.FETCH_GROUP_NAME}, 
								2, new SubProgressMonitor(monitor, 1));
					}
					entriesToSync = ldapServer.getAllUserEntriesForSync();
				}else{
					entriesToSync = getImportPage().getSelectedEntries();
				}
				Collection<FetchEventTypeDataUnit> dataUnits = new HashSet<FetchEventTypeDataUnit>();
				for (String ldapName : entriesToSync){
					dataUnits.add(new FetchEventTypeDataUnit(ldapName));
				}
				syncEvent.setFetchEventTypeDataUnits(dataUnits);
				monitor.worked(1);
				
			}else if (SyncDirection.EXPORT.equals(syncDirection)){
				
				syncEvent = new LDAPSyncEvent(SyncEventGenericType.SEND_USER);
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
				monitor.worked(1);
				
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
			monitor.worked(1);
			
		} catch (UserManagementSystemSyncException e) {
			throw new UserManagementSystemCommunicationException(e);
		} finally {
			monitor.done();
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
