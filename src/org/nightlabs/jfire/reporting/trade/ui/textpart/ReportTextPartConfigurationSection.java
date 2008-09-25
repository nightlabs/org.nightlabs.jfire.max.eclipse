/**
 * 
 */
package org.nightlabs.jfire.reporting.trade.ui.textpart;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.textpart.ReportTextPartConfiguration;
import org.nightlabs.jfire.reporting.ui.textpart.ReportTextPartConfigurationEditComposite;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportTextPartConfigurationSection extends ToolBarSectionPart {

	private ReportTextPartConfigurationPageController controller;
	private ReportTextPartConfigurationEditComposite configurationEditComposite;
	
	/**
	 * @param page
	 * @param parent
	 * @param style
	 * @param title
	 */
	public ReportTextPartConfigurationSection(FormPage page, Composite parent, final ReportTextPartConfigurationPageController controller) {
		super(
			page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR,
			"Text part configuration"
		);
		this.controller = controller;
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1; 

		getSection().setClient(client);
		configurationEditComposite = new ReportTextPartConfigurationEditComposite(client, SWT.NONE);
	}

	@Override
	public void commit(boolean onSave) {
		// TODO: Implement correctly
		super.commit(onSave);
		controller.getControllerID();
		configurationEditComposite.updateReportTextPartConfiguration();
	}
	
	public void setReportTextPartConfiguration(ReportTextPartConfiguration reportTextPartConfiguration) {
		configurationEditComposite.setReportTextPartConfiguration(reportTextPartConfiguration);
	}
	
	public void loadReportTextPartConfiguration(final ReportRegistryItem reportRegistryItem) {
		configurationEditComposite.setEnabled(false);
		Job loadJob = new Job("Loading ReportTextPartConfiguration") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				ReportRegistryItemID reportRegistryItemID = (ReportRegistryItemID) JDOHelper.getObjectId(reportRegistryItem);
				final ReportTextPartConfiguration config = controller.getReportTextPartConfiguration(reportRegistryItemID, monitor);
				getSection().getDisplay().asyncExec(new Runnable() {
					public void run() {
						configurationEditComposite.setEnabled(true);
						setReportTextPartConfiguration(config);
					}
				});
				return Status.OK_STATUS;
			}
		};
		loadJob.schedule();
	}
}
