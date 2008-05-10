package org.nightlabs.jfire.simpletrade.admin.ui.producttype.nestedproducttype;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.store.NestedProductTypeLocal;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeLocalMapFieldMetaData;

public class CreateNestedProductTypeWizard
		extends DynamicPathWizard
{
	private ProductType packageProductType;
	private SelectProductTypePage selectProductTypePage;
	private EditNestedProductTypePage editNestedProductTypePage;

	public CreateNestedProductTypeWizard(ProductType packageProductType)
	{
		this.packageProductType = packageProductType;
	}

	@Override
	public void addPages()
	{
		selectProductTypePage = new SelectProductTypePage();
		addPage(selectProductTypePage);

		editNestedProductTypePage = new EditNestedProductTypePage();
		addPage(editNestedProductTypePage);
	}

	@Override
	public boolean performFinish()
	{
		if (packageProductType.getProductTypeLocal().getFieldMetaData("nestedProductTypeLocals") != null) { //$NON-NLS-1$
			ProductTypeLocalMapFieldMetaData fieldMetaData = (ProductTypeLocalMapFieldMetaData) packageProductType.getProductTypeLocal().getFieldMetaData("nestedProductTypeLocals"); //$NON-NLS-1$
			fieldMetaData.setValueInherited(false);
		}
		NestedProductTypeLocal npt = packageProductType.getProductTypeLocal().createNestedProductTypeLocal(selectProductTypePage.getSelectedProductType().getProductTypeLocal());
		npt.setQuantity(editNestedProductTypePage.getQuantity());
		return true;
	}

}
