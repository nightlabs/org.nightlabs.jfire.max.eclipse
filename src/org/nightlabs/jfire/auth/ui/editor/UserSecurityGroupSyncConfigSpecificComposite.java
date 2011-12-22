package org.nightlabs.jfire.auth.ui.editor;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.statushandlers.StatusManager;
import org.nightlabs.jfire.auth.ui.JFireAuthUIPlugin;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserSecurityGroupSyncConfig;

/**
 * Abstract class for composites which are to be displayed in {@link UserSecurityGroupSyncConfigSpecificSection} for editing
 * {@link UserSecurityGroupSyncConfig} for specific {@link UserManagementSystem}. For now this abstract class does not add
 * any UI components, it is expected that everything would be created by its subclasses.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public abstract class UserSecurityGroupSyncConfigSpecificComposite extends Composite{
	
	/**
	 * {@link Collection} of {@link SyncConfigChangedListener}s to be notified of {@link UserSecurityGroupSyncConfig} change
	 */
	private Collection<SyncConfigChangedListener> listeners;
	
	/**
	 * Edited {@link UserSecurityGroupSyncConfig}
	 */
	private UserSecurityGroupSyncConfig<?, ?> syncConfig;

	/**
	 * Constructs new {@link UserSecurityGroupSyncConfigSpecificComposite}
	 * 
	 * @param parent Parent {@link Composite}
	 * @param style {@link SWT} style
	 * @param toolkit {@link FormToolkit} to be used for creation of composite's content
	 */
	public UserSecurityGroupSyncConfigSpecificComposite(Composite parent, int style, FormToolkit toolkit) {
		super(parent, style);
		listeners = new LinkedList<SyncConfigChangedListener>();
	}


	/**
	 * Commit all the changes made in UI to given {@link UserSecurityGroupSyncConfig} instance.
	 * Is called by {@link UserSecurityGroupSyncConfigSpecificSection#commit(boolean)}
	 */
	public abstract void commitChanges();

	/**
	 * Refresh UI of this composite with the current {@link UserSecurityGroupSyncConfig} data.
	 * Is called by {@link UserSecurityGroupSyncConfigSpecificSection#refresh()}
	 */
	public abstract void refresh();
	

	/**
	 * Provide this composite with given {@link UserSecurityGroupSyncConfig} data
	 * 
	 * @param syncConfig {@link UserSecurityGroupSyncConfig} to be edited
	 */
	public void setCompositeInput(UserSecurityGroupSyncConfig<?, ?> syncConfig){
		this.syncConfig = syncConfig;
		refresh();
	}

	/**
	 * Adds given {@link SyncConfigChangedListener}.
	 * 
	 * @param listener {@link SyncConfigChangedListener} to be added, can't be <code>null</code> - {@link IllegalArgumentException} will be thrown
	 */
	public void addSyncConfigChangedListener(SyncConfigChangedListener listener){
		if (listener == null){
			throw new IllegalArgumentException("SyncConfigChangedListener can't be null!");
		}
		listeners.add(listener);
	}

	/**
	 * Removes given {@link SyncConfigChangedListener}.
	 * 
	 * @param listener {@link SyncConfigChangedListener} to be removed
	 */
	public void removeSyncConfigChangedListener(SyncConfigChangedListener listener){
		listeners.remove(listener);
	}
	
	/**
	 * Method to provide edited {@link UserSecurityGroupSyncConfig} to subclasses.
	 * 
	 * @return {@link UserSecurityGroupSyncConfig} instance
	 */
	protected UserSecurityGroupSyncConfig<?, ?> getSyncConfig(){
		return syncConfig;
	}
	
	/**
	 * Fire change of {@link UserSecurityGroupSyncConfig} to registered listeners.
	 */
	protected void fireSyncConfigChanged(){
		Throwable lastThrowable = null;
		for (SyncConfigChangedListener listener : listeners){
			try{
				listener.objectChanged(syncConfig);
			}catch(Exception e){
				lastThrowable = e;
				StatusManager.getManager().handle(
						new Status(
								Status.ERROR, JFireAuthUIPlugin.PLUGIN_ID, lastThrowable.getMessage(), lastThrowable), 
						StatusManager.LOG);
			}
		}
		if (lastThrowable != null){
			StatusManager.getManager().handle(
					new Status(
							Status.WARNING, JFireAuthUIPlugin.PLUGIN_ID, 
							"Exception(s) occured during notifying listeners of object change in. Please see log for details, last one was: " + lastThrowable.getMessage(), lastThrowable), 
					StatusManager.SHOW);
		}
	}

	/**
	 * Interface for simple listeners which are triggered whenever {@link UserSecurityGroupSyncConfig} being edited by this
	 * composite is changed.
	 * 
	 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
	 *
	 */
	interface SyncConfigChangedListener{
		void objectChanged(UserSecurityGroupSyncConfig<?, ?> changedSyncConfig);
	}

}
