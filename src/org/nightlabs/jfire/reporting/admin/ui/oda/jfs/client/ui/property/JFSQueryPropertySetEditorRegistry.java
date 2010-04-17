/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.eclipse.extension.AbstractEPProcessor;
import org.nightlabs.jfire.reporting.admin.ui.ReportingAdminPlugin;
import org.nightlabs.jfire.reporting.oda.jfs.JFSQueryPropertySet;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;

/**
 * {@link JFSQueryPropertySetEditorRegistry} manages the extensions to the point
 * <code>org.nightlabs.jfire.reporting.admin.queryPropertySetEditor</code> that
 * are used to edit the {@link JFSQueryPropertySet} of a JFS data-set. 
 * <p>
 * For a selected script ({@link ScriptRegistryItemID}) this registry can return 
 * the according {@link IJFSQueryPropertySetEditorFactory}. This is either a factory
 * that was registered as extension and matches the script, or the default factory
 * providing the default implementation. 
 * </p> 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class JFSQueryPropertySetEditorRegistry extends AbstractEPProcessor {

	public static final String EXTENSION_POINT_ID = ReportingAdminPlugin.class.getPackage().getName()  + ".queryPropertySetEditor"; //$NON-NLS-1$
	
	/**
	 * Create a {@link JFSQueryPropertySetEditorRegistry}.
	 */
	public JFSQueryPropertySetEditorRegistry() {
	}

	private List<IJFSQueryPropertySetEditorFactory> factories = new LinkedList<IJFSQueryPropertySetEditorFactory>();
	private IJFSQueryPropertySetEditorFactory defaultFactory = new DefaultJFSQueryPropertySetEditorFactory();
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#processElement(org.eclipse.core.runtime.IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	public void processElement(IExtension extension, IConfigurationElement element) throws Exception {
		if (element.getName().equals("queryPropertySetEditor")) { //$NON-NLS-1$
			IJFSQueryPropertySetEditorFactory factory = (IJFSQueryPropertySetEditorFactory) element.createExecutableExtension("class"); //$NON-NLS-1$
			factories.add(factory);
		}
	}

	/**
	 * Get the first {@link IJFSQueryPropertySetEditorFactory} whose 
	 * {@link IJFSQueryPropertySetEditorFactory#matches(ScriptRegistryItemID)} method returns <code>true</code>
	 * for the given scriptID. If no factory can be found a default factory will be
	 * returned: {@link DefaultJFSQueryPropertySetEditorFactory}.
	 * 
	 * @param scriptID The scriptID to find a {@link IJFSQueryPropertySetEditorFactory} for.
	 * @return Either a {@link IJFSQueryPropertySetEditorFactory} that matches the given scriptID or {@link DefaultJFSQueryPropertySetEditorFactory}.
	 */
	public IJFSQueryPropertySetEditorFactory getJFSQueryPropertySetFactory(ScriptRegistryItemID scriptID) {
		for (IJFSQueryPropertySetEditorFactory factory : factories) {
			if (factory.matches(scriptID))
				return factory;
		}
		return defaultFactory;
	}
	
	private static JFSQueryPropertySetEditorRegistry sharedInstance;

	/**
	 * Returns and lazily creates a static instance of JFSQueryPropertySetEditorRegistry
	 */
	public static JFSQueryPropertySetEditorRegistry sharedInstance() {
		if (sharedInstance == null) {
			synchronized (JFSQueryPropertySetEditorRegistry.class) {
				if (sharedInstance == null) {
					sharedInstance = new JFSQueryPropertySetEditorRegistry();
					sharedInstance.process();
				}
			}
		}
		return sharedInstance;
	}
}
