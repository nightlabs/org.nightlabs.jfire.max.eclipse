/**
 * 
 */
package org.nightlabs.jfire.reporting.trade.ui.textpart;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.language.LanguageChooserCombo;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.textpart.ReportTextPartConfiguration;
import org.nightlabs.jfire.reporting.trade.ui.resource.Messages;
import org.nightlabs.jfire.reporting.ui.textpart.IReportTextPartConfigurationChangedListener;
import org.nightlabs.jfire.reporting.ui.textpart.ReportTextPartConfigurationChangedEvent;
import org.nightlabs.jfire.reporting.ui.textpart.ReportTextPartConfigurationEditComposite;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportTextPartConfigurationSection extends ToolBarSectionPart {

	private ReportTextPartConfigurationPageController controller;
	private XComboComposite<ReportRegistryItem> reportItemCombo;
	private ReportTextPartConfigurationEditComposite configurationEditComposite;
	private LanguageChooserCombo languageChooser;

	
	/**
	 * @param page
	 * @param parent
	 * @param style
	 * @param title
	 */
	public ReportTextPartConfigurationSection(FormPage page, Composite parent, final ReportTextPartConfigurationPageController controller) {
		super(
			page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR,
			Messages.getString("org.nightlabs.jfire.reporting.trade.ui.textpart.ReportTextPartConfigurationSection.title") //$NON-NLS-1$
		);
		this.controller = controller;
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1; 

		getSection().setClient(client);
		
		XComposite header = new XComposite(client, SWT.NONE, LayoutDataMode.GRID_DATA_HORIZONTAL);
		
		GridLayout layout = new GridLayout(2, false);
		XComposite.configureLayout(LayoutMode.TIGHT_WRAPPER, layout);
		header.setLayout(layout);
		
		reportItemCombo = new XComboComposite<ReportRegistryItem>(
				header, SWT.READ_ONLY,
				Messages.getString("org.nightlabs.jfire.reporting.trade.ui.textpart.ReportTextPartConfigurationSection.reportItemCombo.caption"), //$NON-NLS-1$
				new TableLabelProvider() {
			@Override
			public String getColumnText(Object element, int columnIndex) {
				return ((ReportRegistryItem) element).getName().getText();
			}
		});
		reportItemCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_END));
		
		languageChooser = new LanguageChooserCombo(header);
		languageChooser.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_END));
		
		configurationEditComposite = new ReportTextPartConfigurationEditComposite(client, SWT.NONE, languageChooser, false);
		configurationEditComposite.addReportTextPartConfigurationChangedListener(new IReportTextPartConfigurationChangedListener() {
			@Override
			public void reportTextPartConfigurationChanged(ReportTextPartConfigurationChangedEvent evt) {
				controller.markDirty(evt.getReportTextPartConfiguration());
				markDirty();
			}
		});
		
		reportItemCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateConfiguration();
			}
		});		
		
	}

	@Override
	public void commit(boolean onSave) {
		super.commit(onSave);
		configurationEditComposite.updateReportTextPartConfiguration();
	}
	
	private void updateConfiguration() {
		ReportRegistryItem selection = reportItemCombo.getSelectedElement();
		if (selection != null)
			loadReportTextPartConfiguration(selection);
	}
	
	public void updateReportRegistryItems() {
		reportItemCombo.setInput(controller.getReportRegistryItems());
		if (controller.getDefaultReportRegistryItem() != null)
			reportItemCombo.selectElement(controller.getDefaultReportRegistryItem());
		updateConfiguration();
	}

	protected void setReportTextPartConfiguration(ReportTextPartConfiguration reportTextPartConfiguration) {
		configurationEditComposite.setReportTextPartConfiguration(reportTextPartConfiguration);
	}
	
	protected void loadReportTextPartConfiguration(final ReportRegistryItem reportRegistryItem) {
		configurationEditComposite.setEnabled(false);
		Job loadJob = new Job(Messages.getString("org.nightlabs.jfire.reporting.trade.ui.textpart.ReportTextPartConfigurationSection.loadJob.name")) { //$NON-NLS-1$
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
