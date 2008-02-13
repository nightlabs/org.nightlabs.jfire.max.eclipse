package org.nightlabs.jfire.scripting.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.scripting.ScriptManager;
import org.nightlabs.jfire.scripting.ScriptManagerUtil;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class for this plugin.
 */
public class ScriptingPlugin extends AbstractUIPlugin {

	//The shared instance.
	private static ScriptingPlugin plugin;
	/**
	 * The constructor.
	 */
	public ScriptingPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static ScriptingPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.nightlabs.jfire.scripting.ui", path); //$NON-NLS-1$
	}
	
	
	/**
	 * Returns a new ScriptManager bean.
	 */
	public static ScriptManager getScriptManager() {
		try {
			return ScriptManagerUtil.getHome(
					Login.getLogin().getInitialContextProperties()
				).create();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
