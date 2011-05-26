package org.nightlabs.jfire.auth.ui.ldap.tree;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.auth.ui.ldap.LdapUIPlugin;
import org.nightlabs.jfire.base.security.integration.ldap.connection.ILDAPConnectionParamsProvider;
import org.nightlabs.jfire.base.security.integration.ldap.connection.LDAPConnection;
import org.nightlabs.jfire.base.security.integration.ldap.connection.LDAPConnectionManager;
import org.nightlabs.progress.ProgressMonitor;

/**
 * This class represents a node in {@link LDAPTree}. Actual communication with LDAP directory happens here.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class LDAPTreeEntry {
	
	/**
	 * Name of LDAP entry
	 */
	private String entryName;
	
	/**
	 * Connection parameters. They are automatically set to all child entries of this {@link LDAPTreeEntry}.
	 */
	private ILDAPConnectionParamsProvider ldapConnectionParamsProvider;
	
	/**
	 * {@link Job} for loading this entry's children
	 */
	private Job loadJob;
	
	/**
	 * Loaded children on this {@link LDAPTreeEntry}
	 */
	private Collection<LDAPTreeEntry> childLdapEntries;
	
	/**
	 * Constructs new {@link LDAPTreeEntry} with given name
	 * 
	 * @param entryName
	 */
	public LDAPTreeEntry(String entryName) {
		this.entryName = entryName;
	}
	
	/**
	 * If this entry is a root, {@link ILDAPConnectionParamsProvider} should be set. All child entries will get the same 
	 * {@link ILDAPConnectionParamsProvider} automatically when they are loaded. Can't be <code>null</code>.
	 * 
	 * @param ldapConnectionParamsProvider can't be <code>null</code>, {@link IllegalArgumentException} is thrown otherwise
	 */
	public void setLdapConnectionParamsProvider(ILDAPConnectionParamsProvider ldapConnectionParamsProvider) {
		if (ldapConnectionParamsProvider == null){
			throw new IllegalArgumentException("ILDAPConnectionParamsProvider can't be null!");
		}
		this.ldapConnectionParamsProvider = ldapConnectionParamsProvider;
	}
	
	/**
	 * Get name of this entry
	 * 
	 * @return Name of this entry
	 */
	public String getEntryName() {
		return entryName;
	}

	/**
	 * Get children of this {@link LDAPTreeEntry}. If they are not loaded, starts a {@link Job} and returns <code>null</code>.
	 * {@link LDAPTreeEntryLoadCallback}s are called when the {@link Job} is done.
	 * 
	 * @param bindCredentials Credentials used for binding against LDAP directory if not <code>null</code>
	 * @param entryLoadCallback Callback(s) to be called after load is done 
	 * @return children of this {@link LDAPTreeEntry} or <code>null</code> if they are not loaded yet
	 */
	public LDAPTreeEntry[] getChildren(final BindCredentials bindCredentials, final LDAPTreeEntryLoadCallback... entryLoadCallback){

		if (childLdapEntries != null){
			
			LDAPTreeEntry[] entries = new LDAPTreeEntry[childLdapEntries.size()];
			return childLdapEntries.toArray(entries);
			
		}else{
			
			loadJob = new Job("loading ldap entries...") {
	
				@Override
				protected IStatus run(ProgressMonitor monitor) throws Exception {
					
					LDAPConnection connection = null;
					try {
						connection = LDAPConnectionManager.sharedInstance().getConnection(ldapConnectionParamsProvider);
						if (bindCredentials != null){
							connection.bind(bindCredentials.getLogin(), bindCredentials.getPassword());
						}
						Collection<String> childEntries = connection.getChildEntries(entryName);
						
						childLdapEntries = new ArrayList<LDAPTreeEntry>();
						for (String entryName : childEntries) {
							LDAPTreeEntry ldapTreeEntry = new LDAPTreeEntry(entryName);
							ldapTreeEntry.setLdapConnectionParamsProvider(ldapConnectionParamsProvider);
							childLdapEntries.add(ldapTreeEntry);
						}

						return Status.OK_STATUS;

					} catch (Exception e){
						return new Status(Status.ERROR, LdapUIPlugin.PLUGIN_ID, "Error occured during entry loading!", e);
					} finally {
						LDAPConnectionManager.sharedInstance().releaseConnection(connection);
					}
				}
			};
			
			loadJob.addJobChangeListener(new JobChangeAdapter(){
				@Override
				public void done(IJobChangeEvent event) {
					if (Status.OK_STATUS == event.getResult()
							&& entryLoadCallback != null){
						
						for (LDAPTreeEntryLoadCallback ldapTreeEntryLoadedCallback : entryLoadCallback) {
							ldapTreeEntryLoadedCallback.treeEntryLoaded(LDAPTreeEntry.this);
						}
						
					}else if (Status.ERROR == event.getResult().getSeverity()){
						
						for (LDAPTreeEntryLoadCallback ldapTreeEntryLoadedCallback : entryLoadCallback) {
							ldapTreeEntryLoadedCallback.hadleTreeEntryLoadError(event.getResult().getException());
						}
						
					}
					super.done(event);
				}
				
			});
			
			loadJob.schedule();
			
		}
		
		return null;
	}
	
	/**
	 * Simple interface representing credentials for binding agains LDAP directory.
	 * 
	 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
	 *
	 */
	public static interface BindCredentials{
		String getLogin();
		String getPassword();
	}
	
	/**
	 * Interface for callbacks which are called after load {@link Job} is done.
	 * 
	 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
	 *
	 */
	public static interface LDAPTreeEntryLoadCallback{
		
		/**
		 * Called when child entries are loaded successfully by {@link Job} and its return {@link Status} is OK. 
		 * 
		 * @param loadedEntry entry whose children were loaded
		 */
		void treeEntryLoaded(LDAPTreeEntry loadedEntry);
		
		/**
		 * Called when some exception was caught during loading process and {@link Job} returned ERROR {@link Status}.
		 * 
		 * @param cause exception caught during loading process
		 */
		void hadleTreeEntryLoadError(Throwable cause);
	}
}
