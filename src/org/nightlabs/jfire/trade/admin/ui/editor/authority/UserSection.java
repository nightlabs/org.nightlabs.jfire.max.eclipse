package org.nightlabs.jfire.trade.admin.ui.editor.authority;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jfire.base.ui.security.UserTable;
import org.nightlabs.jfire.security.Authority;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypePageController;
import org.nightlabs.jfire.trade.admin.ui.editor.IProductTypeSectionPart;

public class UserSection
extends ToolBarSectionPart
implements IProductTypeSectionPart
{
	private UserTable userTable;

	public UserSection(IFormPage page, Composite parent) {
		super(page, parent, ExpandableComposite.TITLE_BAR, "Users && user groups in authority");

		userTable = new UserTable(parent, SWT.NONE);
		userTable.setInput(users);
	}

	private ProductType productType;
	private Authority authority;
	private Set<User> users = new HashSet<User>();
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
		setAuthority(productType == null ? null : productType.getProductTypeLocal().getAuthority());
	}


	private void setAuthority(Authority authority)
	{
		this.authority = authority;
		if (authority == null) {
			users.clear();
		}
		else {
		}
		userTable.refresh();
	}
}
