package org.nightlabs.jfire.simpletrade.admin.producttype.nestedproducttype;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.store.NestedProductType;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeMapFieldMetaData;

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
		if (packageProductType.getFieldMetaData("nestedProductTypes") != null) { //$NON-NLS-1$
			ProductTypeMapFieldMetaData fieldMetaData = (ProductTypeMapFieldMetaData) packageProductType.getFieldMetaData("nestedProductTypes"); //$NON-NLS-1$
			fieldMetaData.setValueInherited(false);			
		}
		NestedProductType npt = packageProductType.createNestedProductType(selectProductTypePage.getSelectedProductType());
		npt.setQuantity(editNestedProductTypePage.getQuantity());
		return true;
	}

}
