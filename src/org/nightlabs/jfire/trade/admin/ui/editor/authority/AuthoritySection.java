package org.nightlabs.jfire.trade.admin.ui.editor.authority;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.jfire.base.admin.ui.editor.authority.AbstractAuthoritySection;
import org.nightlabs.jfire.base.admin.ui.editor.authority.InheritedAuthorityResolver;
import org.nightlabs.jfire.security.id.AuthorityID;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.progress.ProgressMonitor;

public class AuthoritySection
extends AbstractAuthoritySection
{
	public AuthoritySection(IFormPage page, Composite parent) {
		super(page, parent);
	}

	private ProductType productType;
	private AuthorityPageController authorityPageController;

	@Override
	public void setPageController(IEntityEditorPageController pageController) {
		authorityPageController = (AuthorityPageController) pageController;
		productType = authorityPageController.getControllerObject();
		setAuthorityPageControllerHelper(authorityPageController.getAuthorityPageControllerHelper());
	}

	@Override
	protected InheritedAuthorityResolver createInheritedAuthorityResolver() {
		return new InheritedAuthorityResolver() {
			@Override
			public AuthorityID getInheritedAuthorityID(ProgressMonitor monitor) {
				if (productType.getExtendedProductTypeID() == null)
					return null;

				ProductType extendedProductType = authorityPageController.getExtendedProductType(
						monitor,
						productType.getExtendedProductTypeID());

				return extendedProductType.getProductTypeLocal().getSecuringAuthorityID();
			}
		};
	}
}
