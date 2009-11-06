package org.nightlabs.jfire.contact.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ContactPlugin extends AbstractUIPlugin {

	public static final String ZONE_PROPERTY = ContactPlugin.class.getName() + "#ZONE_PROPERTY"; //$NON-NLS-1$

	// The plug-in ID
	public static final String PLUGIN_ID = "org.nightlabs.jfire.contact.ui";

	// The shared instance
	private static ContactPlugin plugin;

	/**
	 * The constructor
	 */
	public ContactPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ContactPlugin getDefault() {
		return plugin;
	}

}
