/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.layout.scheduled.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jfire.reporting.scheduled.ScheduledReport;
import org.nightlabs.jfire.reporting.ui.layout.scheduled.delivery.DeliveryDelegateEditComposite;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ScheduledReportDeliveryDelegateSection extends ToolBarSectionPart {

	private ScheduledReport scheduledReport;
	private DeliveryDelegateEditComposite editComposite;
	
	public ScheduledReportDeliveryDelegateSection(final IFormPage page, Composite parent) {
		super(page, parent, ExpandableComposite.TITLE_BAR, "Report delivery");
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1;

		getSection().setClient(client);
		
		editComposite = new DeliveryDelegateEditComposite(client, SWT.NONE, LayoutDataMode.GRID_DATA_HORIZONTAL, this) {
			@Override
			protected void layoutEnvironment() {
				page.getManagedForm().getForm().reflow(true);
			}
		};
	}
	
	@Override
	public boolean setFormInput(Object input) {
		if (input instanceof ScheduledReport) {
			this.scheduledReport = (ScheduledReport) input;
		}
		return super.setFormInput(input);
	}
	
	@Override
	public void refresh() {
		super.refresh();
		if (scheduledReport != null) {
			editComposite.setDeliveryDelegate(scheduledReport.getDeliveryDelegate());
		}
	}
	
	@Override
	public void commit(boolean onSave) {
		super.commit(onSave);
		scheduledReport.setDeliveryDelegate(editComposite.getDeliveryDelegate());
	}
}
