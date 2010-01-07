package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.PriceFragmentType;
import org.nightlabs.jfire.accounting.dao.PriceFragmentTypeDAO;
import org.nightlabs.jfire.trade.ui.accounting.PriceFragmentTypeTable;
import org.nightlabs.progress.NullProgressMonitor;

public class PriceFragmentTypeComposite extends AbstractCellReferenceComposite{

	private PriceFragmentType selectedPriceFragmentType = null;
	private CellReferencePage cellReferencePage = null;
	public PriceFragmentTypeComposite(CellReferencePage cellReferencePage, Composite parent) {
		super(parent, SWT.None);
		this.cellReferencePage = cellReferencePage;

//		Group priceFragmentTypeTableGroup = new Group(this, SWT.NONE);
//		priceFragmentTypeTableGroup.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference.PriceFragmentTypeComposite.priceFragmentTypeTableGroup.text")); //$NON-NLS-1$
//		priceFragmentTypeTableGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
//		priceFragmentTypeTableGroup.setLayout(new GridLayout());

		final PriceFragmentTypeTable priceFragmentTypeTable = new PriceFragmentTypeTable(this, SWT.NONE);
		priceFragmentTypeTable.getGridData().grabExcessHorizontalSpace = true;
		priceFragmentTypeTable.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				PriceFragmentType pf = priceFragmentTypeTable.getSelectedPriceFragmentType();
				if(pf != null){
					selectedPriceFragmentType = pf;
					checked(true);
				}//if
			}
		});
		
		priceFragmentTypeTable.loadPriceFragmentTypes();
	}
	
	@Override
	protected void createScript(){
		StringBuffer scriptBuffer = new StringBuffer();
		
		scriptBuffer.append("PriceFragmentTypeID.create") //$NON-NLS-1$
			.append(CellReferenceWizard.L_BRACKET)
				.append(CellReferenceWizard.DOUBLE_QUOTE).append(selectedPriceFragmentType.getOrganisationID()).append(CellReferenceWizard.DOUBLE_QUOTE)
				.append(",") //$NON-NLS-1$
				.append(CellReferenceWizard.DOUBLE_QUOTE).append(selectedPriceFragmentType.getPriceFragmentTypeID()).append(CellReferenceWizard.DOUBLE_QUOTE)
			.append(CellReferenceWizard.R_BRACKET);
		
		cellReferencePage.setDimensionScript(this.getClass().getName(), scriptBuffer.toString());
	}
	
	@Override
	protected void doEnable() {
		if(selectedPriceFragmentType != null)
			createScript();
	}
	
	@Override
	protected void doDisable() {
		cellReferencePage.clearDimensionScript(this.getClass().getName());
	}
}