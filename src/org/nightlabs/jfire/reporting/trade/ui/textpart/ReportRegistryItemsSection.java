/**
 * 
 */
package org.nightlabs.jfire.reporting.trade.ui.textpart;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportRegistryItemsSection extends ToolBarSectionPart {

	private ReportTextPartConfigurationPageController controller;
	private XComboComposite<ReportRegistryItem> reportItemCombo;
	
	/**
	 * @param page
	 * @param parent
	 * @param style
	 * @param title
	 */
	public ReportRegistryItemsSection(FormPage page, Composite parent, final ReportTextPartConfigurationPageController controller) {
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
		reportItemCombo = new XComboComposite<ReportRegistryItem>(client, SWT.READ_ONLY, new TableLabelProvider() {
			@Override
			public String getColumnText(Object element, int columnIndex) {
				return ((ReportRegistryItem) element).getName().getText();
			}
		});
	}

	@Override
	public void commit(boolean onSave) {
		// nothing to do
		super.commit(onSave);
	}
	
	public void updateReportRegistryItems() {
		reportItemCombo.setInput(controller.getReportRegistryItems());
		if (controller.getDefaultReportRegistryItem() != null)
			reportItemCombo.selectElement(controller.getDefaultReportRegistryItem());
	}
	
	public ReportRegistryItem getSelectedReportRegistryItem() {
		return reportItemCombo.getSelectedElement();
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		reportItemCombo.addSelectionChangedListener(listener);
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		reportItemCombo.removeSelectionChangedListener(listener);
	}
}
