package org.nightlabs.jfire.dynamictrade.admin.ui.createproducttype;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.FadeableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.jfire.dynamictrade.admin.ui.tree.DynamicProductTypeTree;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.store.ProductType;

public class DynamicProductTypeSelectPage
extends DynamicPathWizardPage
{
	private DynamicProductTypeTree dynamicProductTypeTree;
	private DynamicProductType selectedProductType;
	
	/**
	 * @param pageName
	 */
	public DynamicProductTypeSelectPage()
	{
		super(DynamicProductTypeSelectPage.class.getName(), "Title");
		this.setDescription("Description");
	}
 
	/**
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent)
	{
		final FadeableComposite page = new FadeableComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		XComposite comp = new XComposite(page, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		comp.getGridLayout().numColumns = 2;
		
		dynamicProductTypeTree = new DynamicProductTypeTree(comp);
		dynamicProductTypeTree.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				getContainer().updateButtons();
			}
		});

		return page;
	}

	private boolean isPageAdded = false;
	@Override
	public boolean canFlipToNextPage() {
		selectedProductType = dynamicProductTypeTree.getFirstSelectedElement();
		
		if (selectedProductType != null && selectedProductType.getInheritanceNature() == ProductType.INHERITANCE_NATURE_BRANCH) {
			CreateDynamicProductTypeNewWizard newWizard = (CreateDynamicProductTypeNewWizard)getWizard();
			newWizard.setParentProductTypeID(selectedProductType.getObjectId());
			
			if (!isPageAdded) {
				newWizard.addRemainingPages();
				isPageAdded = true;
			}
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean isPageComplete() {
		return selectedProductType != null;
	}
	
	@Override
	public boolean canBeLastPage() {
		return false;
	}
}