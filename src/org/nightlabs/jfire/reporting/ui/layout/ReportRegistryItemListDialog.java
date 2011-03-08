package org.nightlabs.jfire.reporting.ui.layout;

import java.util.Collection;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.eclipse.ui.dialog.ResizableTrayDialog;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;

public class ReportRegistryItemListDialog 
extends ResizableTrayDialog 
{
	private ReportRegistryItemTable layoutTable;
	private Collection<ReportRegistryItemID> itemIDs;
	private ReportRegistryItemID selectedItemID;
	
	public ReportRegistryItemListDialog(Shell parentShell, Collection<ReportRegistryItemID> itemIDs) {
		super(parentShell, null);
		this.itemIDs = itemIDs;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		layoutTable = new ReportRegistryItemTable(parent, SWT.NONE);
		layoutTable.setReportRegistryItemIDs(itemIDs, new String[] {FetchPlan.DEFAULT,
				ReportRegistryItem.FETCH_GROUP_NAME, ReportRegistryItem.FETCH_GROUP_DESCRIPTION});
		layoutTable.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent e) {
				ReportRegistryItem selectedItem = layoutTable.getSelectedElements().iterator().next();
				ReportRegistryItemListDialog.this.selectedItemID = (ReportRegistryItemID)JDOHelper.getObjectId(selectedItem);

			}
		});
		return layoutTable;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Select report layout");		
	}
	
	public ReportRegistryItemID getSelectedReportRegistryItem() {
		return selectedItemID;
	}
}
