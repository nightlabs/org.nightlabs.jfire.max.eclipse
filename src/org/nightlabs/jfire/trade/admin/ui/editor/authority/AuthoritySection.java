package org.nightlabs.jfire.trade.admin.ui.editor.authority;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypePageController;
import org.nightlabs.jfire.trade.admin.ui.editor.IProductTypeSectionPart;

public class AuthoritySection
extends ToolBarSectionPart
implements IProductTypeSectionPart
{

	public AuthoritySection(IFormPage page, Composite parent) {
		super(page, parent, ExpandableComposite.TITLE_BAR, "Authority");
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
	}

}
