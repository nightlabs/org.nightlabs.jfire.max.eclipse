package org.nightlabs.jfire.trade.admin.ui.editor.authority;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypePageController;
import org.nightlabs.jfire.trade.admin.ui.editor.IProductTypeSectionPart;

public class AuthoritySection
extends ToolBarSectionPart
implements IProductTypeSectionPart
{
	private I18nTextEditor name;
	private I18nTextEditor description;

	public AuthoritySection(IFormPage page, Composite parent) {
		super(page, parent, ExpandableComposite.TITLE_BAR, "Authority");
		((GridData)getSection().getLayoutData()).grabExcessVerticalSpace = false;

		name = new I18nTextEditor(parent);
		description = new I18nTextEditorMultiLine(parent);
	}

	private ProductType productType;
	private AuthorityPageController authorityPageController;

	@Override
	public ProductType getProductType() {
		return productType;
	}

	@Override
	public AuthorityPageController getProductTypePageController() {
		return authorityPageController;
	}

	@Override
	public void setProductTypePageController(AbstractProductTypePageController<ProductType> productTypeDetailPageController) {
		authorityPageController = (AuthorityPageController) productTypeDetailPageController;
		productType = authorityPageController.getControllerObject();
		if (productType == null || productType.getProductTypeLocal().getSecuringAuthority() == null) {
			setMessage("There is no authority assigned to this product type.");
			setEnabled(false);
		}
		else {
			setMessage(null);
			setEnabled(true);
		}
	}

	private void setEnabled(boolean enabled) {
		name.setEnabled(enabled);
		description.setEnabled(enabled);
	}
}
