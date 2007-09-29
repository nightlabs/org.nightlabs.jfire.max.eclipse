package org.nightlabs.jfire.trade.admin.gridpriceconfig.wizard.cellreference;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.trade.CustomerGroup;
import org.nightlabs.jfire.trade.customergroup.CustomerGroupListComposite;

public class CustomerGroupComposite extends AbstractCellReferenceComposite{

	private CustomerGroup selectedCustomerGroup = null;
	private CellReferencePage cellReferencePage = null;
	
	public CustomerGroupComposite(CellReferencePage cellReferencePage, Composite parent) {
		super(parent, SWT.None);
		this.cellReferencePage = cellReferencePage;

//		//Customer List Group
//		Group customerGroupListGroup = new Group(this, SWT.NONE);
//		customerGroupListGroup.setText(Messages.getString("org.nightlabs.jfire.trade.admin.gridpriceconfig.wizard.cellreference.CustomerGroupComposite.customerGroupListGroup.text")); //$NON-NLS-1$
//		customerGroupListGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
//		customerGroupListGroup.setLayout(new GridLayout());

		//Customer Group List Composite
		CustomerGroupListComposite cGroupList = new CustomerGroupListComposite(this, SWT.NONE, false, null);
		cGroupList.getGridData().grabExcessHorizontalSpace = true;
		cGroupList.loadCustomerGroups();
		cGroupList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				CustomerGroupListComposite cgc = (CustomerGroupListComposite)e.getSource();
				CustomerGroup cg = cgc.getSelectedCustomerGroup();
				if(cg != null){
					selectedCustomerGroup = cg;
					checked(true);
				}//if
			}
		});
	}
	
	public CustomerGroup getSelectedCustomerGroup(){
		return selectedCustomerGroup;
	}
	
	protected void createScript(){
		StringBuffer scriptBuffer = new StringBuffer();
		scriptBuffer.append("CustomerGroupID.create") //$NON-NLS-1$
			.append(CellReferenceWizard.L_BRACKET)
				.append(CellReferenceWizard.DOUBLE_QUOTE).append(selectedCustomerGroup.getOrganisationID()).append(CellReferenceWizard.DOUBLE_QUOTE)
				.append(",") //$NON-NLS-1$
				.append(CellReferenceWizard.DOUBLE_QUOTE).append(selectedCustomerGroup.getCustomerGroupID()).append(CellReferenceWizard.DOUBLE_QUOTE)
			.append(CellReferenceWizard.R_BRACKET);
		
		cellReferencePage.addDimensionScript(this.getClass().getName(), scriptBuffer.toString());
	}
	
	@Override
	protected void doEnable() {
		if(selectedCustomerGroup != null)
			createScript();
	}
	
	@Override
	protected void doDisable() {
		cellReferencePage.removeDimensionScript(this.getClass().getName());
	}
}
