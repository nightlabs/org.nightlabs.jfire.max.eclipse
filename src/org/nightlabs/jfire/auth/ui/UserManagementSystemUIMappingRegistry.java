package org.nightlabs.jfire.auth.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.eclipse.extension.AbstractEPProcessor;
import org.nightlabs.jfire.auth.ui.editor.UserManagementSystemEditor;
import org.nightlabs.jfire.security.integration.UserManagementSystem;

/**
 * Registry for org.nightlabs.jfire.auth.ui.userManagementSystemUIMapping extension point.
 *
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
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
	 * {@link Map} for holding class names of {@link EntityEditor} page factories for every {@link UserManagementSystem} specific class.
	 */
	private Map<Class<? extends UserManagementSystem>, Set<String>> pageFactoriesByClass = new HashMap<Class<? extends UserManagementSystem>, Set<String>>();

	/**
	 * Get class names of {@link EntityEditor} page factories by specific {@link UserManagementSystem} class.
	 * These class names are then used for filtering {@link UserManagementSystemEditor} pages.
	 * 
	 * @param userManagementSystemClass Class of specific {@link UserManagementSystem}
	 * @return {@link Set} of class names for page factories
	 */
	public Set<String> getPageFactoryClassNames(Class<? extends UserManagementSystem> userManagementSystemClass){
		checkProcessing();
		return pageFactoriesByClass==null ? null : pageFactoriesByClass.get(userManagementSystemClass);
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
		
		if ("userManagementSystem".equalsIgnoreCase(element.getName())) { //$NON-NLS-1$
			
			Class<? extends UserManagementSystem> userManagementSystemClass = (Class<? extends UserManagementSystem>) Class.forName(element.getAttribute("class")); //$NON-NLS-2$
			if (pageFactoriesByClass.get(userManagementSystemClass) == null){
				pageFactoriesByClass.put(userManagementSystemClass, new HashSet<String>());
			}
			
			IConfigurationElement[] children = element.getChildren();
			for (IConfigurationElement child : children) {
				if ("pageFactoryMapping".equalsIgnoreCase(child.getName())){ //$NON-NLS-3$
					
					pageFactoriesByClass.get(userManagementSystemClass).add(child.getAttribute("class")); //$NON-NLS-4$
					
				}
			}
		}
	}

}
