package org.nightlabs.jfire.auth.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.IWizardHop;
import org.nightlabs.eclipse.extension.AbstractEPProcessor;
import org.nightlabs.jfire.auth.ui.editor.IUserSecurityGroupSyncConfigDelegate;
import org.nightlabs.jfire.auth.ui.editor.UserManagementSystemEditor;
import org.nightlabs.jfire.auth.ui.editor.UserSecurityGroupEditorSyncConfigPage;
import org.nightlabs.jfire.security.integration.UserManagementSystemType;

/**
 * Registry for org.nightlabs.jfire.auth.ui.userManagementSystemUIMapping extension point.
 *
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 * 
 */
public class UserManagementSystemUIMappingRegistry extends AbstractEPProcessor{
	
	public static final String EXTENSION_POINT_ID = "org.nightlabs.jfire.auth.ui.userManagementSystemUIMapping"; //$NON-NLS-1$

	/**
	 * Lazily instantiated shared instance.
	 */
	private static UserManagementSystemUIMappingRegistry sharedInstance = null;

	/**
	 * Get the lazily created shared instance.
	 * @return The shared instance
	 */
	public static UserManagementSystemUIMappingRegistry sharedInstance(){
		if (sharedInstance == null){
			sharedInstance = new UserManagementSystemUIMappingRegistry();
		}
		return sharedInstance;
	}

	private UserManagementSystemUIMappingRegistry() { }

	/**
	 * {@link Map} for holding class names of {@link EntityEditor} page factories for every {@link UserManagementSystemType} specific class.
	 */
	private Map<Class<? extends UserManagementSystemType<?>>, Set<String>> pageFactoriesByClass = new HashMap<Class<? extends UserManagementSystemType<?>>, Set<String>>();

	/**
	 * {@link Map} for holding a {@link IWizardHop}s for every {@link UserManagementSystemType} specific class.
	 */
	private Map<Class<? extends UserManagementSystemType<?>>, Map<Class<? extends DynamicPathWizard>, Class<? extends IWizardHop>>> wizardHopsByClass = new HashMap<Class<? extends UserManagementSystemType<?>>, Map<Class<? extends DynamicPathWizard>,Class<? extends IWizardHop>>>();

	/**
	 * {@link Map} for holding class implementing {@link IUserSecurityGroupSyncConfigDelegate}s  for every {@link UserManagementSystemType} specific class.
	 */
	private Map<Class<? extends UserManagementSystemType<?>>, Class<? extends IUserSecurityGroupSyncConfigDelegate>> userGroupSyncConfigDelgatesByClass = new HashMap<Class<? extends UserManagementSystemType<?>>, Class<? extends IUserSecurityGroupSyncConfigDelegate>>();

	/**
	 * Get class names of {@link EntityEditor} page factories by specific {@link UserManagementSystemType} class.
	 * These class names are then used for filtering {@link UserManagementSystemEditor} pages.
	 * 
	 * @param userManagementSystemTypeClass Class of specific {@link UserManagementSystemType}
	 * @return {@link Set} of class names for page factories
	 */
	public Set<String> getPageFactoryClassNames(Class<? extends UserManagementSystemType<?>> userManagementSystemTypeClass){
		checkProcessing();
		return pageFactoriesByClass==null ? null : pageFactoriesByClass.get(userManagementSystemTypeClass);
	}

	/**
	 * Get {@link IWizardHop} for specified {@link DynamicPathWizard} by specific {@link UserManagementSystemType} class.
	 * 
	 * @param userManagementSystemTypeClass Class of specific {@link UserManagementSystemType}
	 * @param wizardClass Class of specific {@link DynamicPathWizard}
	 * @return instance of {@link IWizardHop}
	 */
	public IWizardHop getWizardHop(Class<? extends UserManagementSystemType<?>> userManagementSystemTypeClass,
			Class<? extends DynamicPathWizard> wizardClass){
		checkProcessing();
		try{
			if (wizardHopsByClass.get(userManagementSystemTypeClass) instanceof Map){
				Class<? extends IWizardHop> hopClass = wizardHopsByClass.get(userManagementSystemTypeClass).get(wizardClass);
				return hopClass != null ? hopClass.newInstance() : null;
			}
			return null;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get {@link IUserSecurityGroupSyncConfigDelegate} by specific {@link UserManagementSystemType} class.
	 * These delegate is then used for creating UI in {@link UserSecurityGroupEditorSyncConfigPage}.
	 * 
	 * @param userManagementSystemTypeClass Class of specific {@link UserManagementSystemType}
	 * @return new {@link IUserSecurityGroupSyncConfigDelegate} instance
	 */
	public IUserSecurityGroupSyncConfigDelegate getUserGroupSyncConfigDelegate(Class<? extends UserManagementSystemType<?>> userManagementSystemTypeClass){
		checkProcessing();
		Class<? extends IUserSecurityGroupSyncConfigDelegate> delegateClass = userGroupSyncConfigDelgatesByClass==null ? null : userGroupSyncConfigDelgatesByClass.get(userManagementSystemTypeClass);
		try{
			return delegateClass != null ? delegateClass.newInstance() : null;
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getExtensionPointID(){
		return EXTENSION_POINT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void processElement(IExtension extension, IConfigurationElement element)	throws Exception{
		
		if ("userManagementSystemType".equalsIgnoreCase(element.getName())) { //$NON-NLS-1$
			
			Class<? extends UserManagementSystemType<?>> userManagementSystemTypeClass = (Class<? extends UserManagementSystemType<?>>) Class.forName(element.getAttribute("class")); //$NON-NLS-2$
			if (pageFactoriesByClass.get(userManagementSystemTypeClass) == null){
				pageFactoriesByClass.put(userManagementSystemTypeClass, new HashSet<String>());
			}
			
			IConfigurationElement[] children = element.getChildren();
			for (IConfigurationElement child : children) {
				if ("pageFactoryMapping".equalsIgnoreCase(child.getName())){ //$NON-NLS-3$
					
					pageFactoriesByClass.get(userManagementSystemTypeClass).add(child.getAttribute("class")); //$NON-NLS-4$
					
				}else if ("wizardHopMapping".equalsIgnoreCase(child.getName())){ //$NON-NLS-5$
					
					Class<? extends IWizardHop> hopClass = (Class<? extends IWizardHop>) Class.forName(child.getAttribute("class"));
					Class<? extends DynamicPathWizard> wizardClass = (Class<? extends DynamicPathWizard>) Class.forName(child.getAttribute("wizardClass"));
					
					if (wizardHopsByClass.get(userManagementSystemTypeClass) instanceof Map){
						wizardHopsByClass.get(userManagementSystemTypeClass).put(wizardClass, hopClass);
					}else{
						HashMap<Class<? extends DynamicPathWizard>, Class<? extends IWizardHop>> wizardHops = new HashMap<Class<? extends DynamicPathWizard>, Class<? extends IWizardHop>>();
						wizardHops.put(wizardClass, hopClass);
						wizardHopsByClass.put(userManagementSystemTypeClass, wizardHops);
					}
					
				}else if ("userSecurityGroupSyncConfigMapping".equalsIgnoreCase(child.getName())){ //$NON-NLS-3$
					
					userGroupSyncConfigDelgatesByClass.put(
							userManagementSystemTypeClass, 
							(Class<? extends IUserSecurityGroupSyncConfigDelegate>) Class.forName(child.getAttribute("class"))); //$NON-NLS-4$
					
				}
			}
		}
	}

}
