package org.nightlabs.jfire.jbpm.ui;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.nightlabs.jfire.jbpm.ui.state.CurrentStateComposite;
import org.nightlabs.jfire.jbpm.ui.transition.next.NextTransitionComposite;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JFireJbpmPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.nightlabs.jfire.jbpm.ui"; //$NON-NLS-1$

	// The shared instance
	private static JFireJbpmPlugin plugin;

	/**
	 * Self-conflicting {@link ISchedulingRule} to prevent code in {@link CurrentStateComposite#setStatable(org.nightlabs.jfire.jbpm.graph.def.Statable, org.nightlabs.progress.ProgressMonitor)}
	 * and {@link NextTransitionComposite#setStatable(org.nightlabs.jfire.jbpm.graph.def.Statable, org.nightlabs.progress.ProgressMonitor)}
	 * run simultaneously which may cause a deadlock situation (at least I had it on a Win7x64 machine), Denis Dudnik.
	 */
	public static final ISchedulingRule stateCompositeSchedulingRule = new ISchedulingRule() {
		@Override
		public boolean isConflicting(ISchedulingRule rule) {
			return rule == this;
		}
		
		@Override
		public boolean contains(ISchedulingRule rule) {
			return rule == this;
		}
	};

	/**
	 * The constructor
	 */
	public JFireJbpmPlugin() {
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
	public static JFireJbpmPlugin getDefault() {
		return plugin;
	}

}
