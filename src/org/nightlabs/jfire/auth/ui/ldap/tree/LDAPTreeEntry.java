package org.nightlabs.jfire.auth.ui.ldap.tree;

import java.util.ArrayList;
import java.util.Collection;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.auth.ui.ldap.LdapUIPlugin;
import org.nightlabs.jfire.base.security.integration.ldap.attributes.LDAPAttributeSet;
import org.nightlabs.jfire.base.security.integration.ldap.connection.ILDAPConnectionParamsProvider;
import org.nightlabs.jfire.base.security.integration.ldap.connection.LDAPConnection;
import org.nightlabs.jfire.base.security.integration.ldap.connection.LDAPConnectionManager;
import org.nightlabs.jfire.security.integration.UserManagementSystemCommunicationException;
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
	 * LDAP entry attributes will be loaded if any names are specified. Note that the same attributes will be loaded for all children of this {@link LDAPTreeEntry}.
	 */
	private String[] attributeNames;
	
	/**
	 * Loaded LDAP entry attributes. Loading of attributes will occur lazily inside {@link #getAttributes()} method. 
	 * For children of this {@link LDAPTreeEntry} loading will occur inside {@link #getChildren(BindCredentials, LDAPTreeEntryLoadCallback...)} if {@link #attributeNames} are given
	 */
	private LDAPAttributeSet entryAttributes;
	
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
		this(entryName, null);
	}
	
	/**
	 * Constructs new {@link LDAPTreeEntry} with given name and sets attribute names to be loaded
	 * 
	 * @param entryName
	 * @param attributeNames
	 */
	public LDAPTreeEntry(String entryName, String[] attributeNames){
		this.entryName = entryName;
		this.attributeNames = attributeNames;
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
	public String getName() {
		return entryName;
	}
	
	/**
	 * Get attributes of this {@link LDAPTreeEntry} represented with an {@link LDAPAttributeSet}.
	 * If attributes are not loaded yet, they will be fetched from LDAP lazily here. Attributes to be 
	 * loaded could be specified by {@link #setAttributeNames(String[])}, all attributes will be loaded otherwise.
	 * 
	 * As this method could communicate to LDAP directory you should consider to use it within some {@link ProgressMonitor}.
	 * 
	 * @param bindCredentials Login(entry name) and password for bind, could be <code>null</code> but in this case anonymous access hould be enabled in LDAP directory
	 * @return {@link LDAPAttributeSet} with attributes of this entry
	 * @throws UserManagementSystemCommunicationException 
	 * @throws LoginException 
	 */
	public LDAPAttributeSet getAttributes(BindCredentials bindCredentials) throws UserManagementSystemCommunicationException, LoginException {
		if (entryAttributes == null){
			LDAPConnection connection = null;
			try {
				connection = LDAPConnectionManager.sharedInstance().getConnection(ldapConnectionParamsProvider);
				if (bindCredentials != null){
					connection.bind(bindCredentials.getLogin(), bindCredentials.getPassword());
				}
				
				entryAttributes = connection.getAttributesForEntry(entryName, attributeNames);
				return entryAttributes;

			} finally {
				if (connection != null && bindCredentials != null){
					connection.unbind();
				}
				LDAPConnectionManager.sharedInstance().releaseConnection(connection);
			}

		}
		return entryAttributes;
	}
	
	/**
	 * Set attribute names to be loaded.
	 * 
	 * @param attributeNames
	 */
	public void setAttributeNames(String[] attributeNames) {
		this.attributeNames = attributeNames;
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
							LDAPTreeEntry ldapTreeEntry = new LDAPTreeEntry(entryName, attributeNames);
							ldapTreeEntry.setLdapConnectionParamsProvider(ldapConnectionParamsProvider);
							ldapTreeEntry.getAttributes(bindCredentials);	// loading attributes here to save time later
							childLdapEntries.add(ldapTreeEntry);
						}

						return Status.OK_STATUS;

					} catch (Exception e){
						return new Status(Status.ERROR, LdapUIPlugin.PLUGIN_ID, "Error occured during entry loading!", e);
					} finally {
						if (connection != null && bindCredentials != null){
							connection.unbind();
						}
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
	 * Check if attributes have been already loaded for this entry.
	 * 
	 * @return <code>true</code> if attributes were loaded
	 */
	public boolean hasAttributesLoaded(){
		return entryAttributes != null;
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
