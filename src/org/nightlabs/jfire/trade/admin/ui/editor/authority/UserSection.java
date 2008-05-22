package org.nightlabs.jfire.trade.admin.ui.editor.authority;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.jfire.base.admin.ui.editor.authority.AbstractUserSection;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypePageController;
import org.nightlabs.jfire.trade.admin.ui.editor.IProductTypeSectionPart;

public class UserSection
extends AbstractUserSection
implements IProductTypeSectionPart
{
	public UserSection(IFormPage page, Composite parent) {
		super(page, parent);
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
		setAuthorityPageControllerHelper(authorityPageController.getAuthorityPageControllerHelper());
	}
}
