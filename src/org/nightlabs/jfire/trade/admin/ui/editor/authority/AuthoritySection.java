package org.nightlabs.jfire.trade.admin.ui.editor.authority;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.jfire.base.admin.ui.editor.authority.AbstractAuthoritySection;
import org.nightlabs.jfire.base.admin.ui.editor.authority.InheritedAuthorityResolver;
import org.nightlabs.jfire.security.Authority;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypePageController;
import org.nightlabs.jfire.trade.admin.ui.editor.IProductTypeSectionPart;
import org.nightlabs.progress.ProgressMonitor;

public class AuthoritySection
extends AbstractAuthoritySection
implements IProductTypeSectionPart
{
	public AuthoritySection(IFormPage page, Composite parent) {
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
		productType = productTypeDetailPageController.getControllerObject();
		setAuthorityPageControllerHelper(authorityPageController.getAuthorityPageControllerHelper());
	}

	@Override
	protected InheritedAuthorityResolver createInheritedAuthorityResolver() {
		return new InheritedAuthorityResolver() {
			@Override
			public Authority getInheritedAuthority(ProgressMonitor monitor) {
				if (productType.getExtendedProductTypeID() == null)
					return null;

				ProductType extendedProductType = getProductTypePageController().getExtendedProductType(
						monitor,
						productType.getExtendedProductTypeID());

				return extendedProductType.getProductTypeLocal().getSecuringAuthority();
			}
		};
	}
}
