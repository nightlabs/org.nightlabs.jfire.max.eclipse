package org.nightlabs.jfire.voucher.editor2d.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.nightlabs.jfire.base.JFireEjbFactory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.voucher.VoucherManager;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class VoucherEditorPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.nightlabs.jfire.voucher.editor2d.ui"; //$NON-NLS-1$

	// The shared instance
	private static VoucherEditorPlugin plugin;
	
	/**
	 * The constructor
	 */
	public VoucherEditorPlugin() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
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
	public static VoucherEditorPlugin getDefault() {
		return plugin;
	}

	public VoucherManager getVoucherManager()
	{
		try {
			return JFireEjbFactory.getBean(VoucherManager.class, Login.getLogin().getInitialContextProperties());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
