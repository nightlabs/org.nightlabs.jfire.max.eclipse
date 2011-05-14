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
import org.nightlabs.jfire.auth.ui.wizard.IUserManagementSystemBuilderHop;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
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
	 * {@link Map} for holding a {@link IUserManagementSystemBuilderHop} for every {@link UserManagementSystemType} specific class.
	 */
	private Map<Class<? extends UserManagementSystemType<?>>, Class<? extends IUserManagementSystemBuilderHop>> wizardHopsByClass = new HashMap<Class<? extends UserManagementSystemType<?>>, Class<? extends IUserManagementSystemBuilderHop>>();

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
	 * Get {@link IUserManagementSystemBuilderHop} by specific {@link UserManagementSystemType} class.
	 * These hop is then used in {@link UserManagementSystem} creation wizard.
	 * 
	 * @param userManagementSystemTypeClass Class of specific {@link UserManagementSystemType}
	 * @return instance of {@link IUserManagementSystemBuilderHop}
	 */
	public IUserManagementSystemBuilderHop getUserManagementSystemBuilderWizardHop(Class<? extends UserManagementSystemType<?>> userManagementSystemTypeClass){
		checkProcessing();
		try{
			return wizardHopsByClass==null ? null : wizardHopsByClass.get(userManagementSystemTypeClass).newInstance();
		}catch(Exception e){
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
					
				}else if ("creationWizardHopMapping".equalsIgnoreCase(child.getName())){ //$NON-NLS-5$
					
					wizardHopsByClass.put(userManagementSystemTypeClass, (Class<? extends IUserManagementSystemBuilderHop>) Class.forName(child.getAttribute("class"))); //$NON-NLS-6$
					
				}
			}
		}
	}

}
