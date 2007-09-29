package org.nightlabs.jfire.trade.admin.gridpriceconfig.wizard.cellreference;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.gridpriceconfig.CellReferenceProductTypeSelector;
import org.nightlabs.jfire.trade.admin.gridpriceconfig.PriceConfigComposite;
import org.nightlabs.jfire.trade.admin.resource.Messages;

public class ProductTypeComposite extends AbstractCellReferenceComposite
{
	/**
	 * This is created via {@link PriceConfigComposite#createCellReferenceProductTypeSelector()}
	 * or <code>null</code>, if this method returns <code>null</code>, because the concrete price config
	 * doesn't need this dimension.
	 */
	private Composite productSelectorComposite = null;
	private CellReferenceProductTypeSelector cellReferenceProductTypeSelector = null;
	
	private ProductTypeID selectedProductTypeID = null;
	private CellReferencePage cellReferencePage = null;
	
	public ProductTypeComposite(CellReferencePage cellReferencePage, Composite parent, PriceConfigComposite priceConfigComposite) {
		super(priceConfigComposite, parent, SWT.None);
		this.cellReferencePage = cellReferencePage;

		Group productTypeSelectorGroup = new Group(this, SWT.NONE);
		productTypeSelectorGroup.setText(Messages.getString("org.nightlabs.jfire.trade.admin.gridpriceconfig.wizard.cellreference.ProductTypeComposite.productTypeSelectorGroup.text")); //$NON-NLS-1$
		productTypeSelectorGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		productTypeSelectorGroup.setLayout(new GridLayout());

		cellReferenceProductTypeSelector = priceConfigComposite.createCellReferenceProductTypeSelector();
		if (cellReferenceProductTypeSelector != null) { // this can be null, because not every price config needs packaging of product types
			cellReferenceProductTypeSelector.setPriceConfigComposite(priceConfigComposite);
			cellReferenceProductTypeSelector.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event)
				{
					ProductTypeID productTypeID = cellReferenceProductTypeSelector.getSelectedProductTypeID();
					if(productTypeID != null){
						selectedProductTypeID = cellReferenceProductTypeSelector.getSelectedProductTypeID();
						checked(true);
					}					
				}
			});
			productSelectorComposite = cellReferenceProductTypeSelector.createComposite(productTypeSelectorGroup);

//@Chairat: Because you didn't fix it till now, I've refactored it. See above. Marco.
//			// FIXME What's this?!?! This is a very big NO-NO! The framework must not rely on anything in this composite!
//			// That's the reason for the interface CellReferenceProductTypeSelector: This interface defines the contract between the framework
//			// and its clients. Therefore, the interface CellReferenceProductTypeSelector should have a method to add a SelectionListener -
//			// Therefore, I have modified it to extend ISelectionProvider.
//			Control c[] = productSelectorComposite.getChildren();
//			for(Control control : c){
//				if(control instanceof Tree){
//					Tree tree = (Tree)control;
//					tree.addSelectionListener(new SelectionAdapter(){
//						@Override
//						public void widgetSelected(SelectionEvent e) {
//							if (cellReferenceProductTypeSelector == null)
//								return;
//
//							ProductTypeID productTypeID = cellReferenceProductTypeSelector.getSelectedProductTypeID();
//							if(productTypeID != null){
//								selectedProductTypeID = cellReferenceProductTypeSelector.getSelectedProductTypeID();
//								checked(true);
//							}
//						}
//					});
//				}
//			}
		}
	}

	protected void createScript(){
		StringBuffer scriptBuffer = new StringBuffer();

		scriptBuffer
			.append("ProductTypeID.create") //$NON-NLS-1$
			.append(CellReferenceWizard.L_BRACKET)
			.append(CellReferenceWizard.DOUBLE_QUOTE)
			.append(selectedProductTypeID.organisationID)
			.append(CellReferenceWizard.DOUBLE_QUOTE)
			.append(',')
			.append(CellReferenceWizard.DOUBLE_QUOTE)
			.append(selectedProductTypeID.productTypeID)
			.append(CellReferenceWizard.DOUBLE_QUOTE)
			.append(CellReferenceWizard.R_BRACKET);
		
		cellReferencePage.addDimensionScript(this.getClass().getName(), scriptBuffer.toString());
	}
	
	@Override
	protected void doEnable() {
		if(selectedProductTypeID != null)
			createScript();	
	}
	
	@Override
	protected void doDisable() {
		cellReferencePage.removeDimensionScript(this.getClass().getName());
	}
	
	public CellReferenceProductTypeSelector getCellReferenceProductTypeSelector(){
		return cellReferenceProductTypeSelector;
	}
}
