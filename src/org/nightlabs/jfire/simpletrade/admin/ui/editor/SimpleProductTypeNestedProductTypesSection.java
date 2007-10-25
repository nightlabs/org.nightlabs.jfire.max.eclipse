package org.nightlabs.jfire.simpletrade.admin.ui.editor;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.simpletrade.admin.ui.producttype.nestedproducttype.CreateNestedProductTypeWizard;
import org.nightlabs.jfire.simpletrade.admin.ui.resource.Messages;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractNestedProductTypeSection;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SimpleProductTypeNestedProductTypesSection  
extends AbstractNestedProductTypeSection
{

	public SimpleProductTypeNestedProductTypesSection(IFormPage page,
			Composite parent, int style) 
	{
		super(
				page, parent, style,
				Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.editor.SimpleProductTypeNestedProductTypesSection.title")); //$NON-NLS-1$
	}

	@Override
	protected void createNestedProductTypeClicked() 
	{
		ProductType packageProductType = getProductType();
		if (packageProductType == null)
			return;

		CreateNestedProductTypeWizard wizard = new CreateNestedProductTypeWizard(packageProductType);
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
		if (dialog.open() == Window.OK) {
			refreshNestedProductTypes();
			markDirty();
		}
	}

}
