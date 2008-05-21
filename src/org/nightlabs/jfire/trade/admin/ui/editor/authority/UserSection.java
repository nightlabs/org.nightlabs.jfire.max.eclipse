package org.nightlabs.jfire.trade.admin.ui.editor.authority;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jfire.base.admin.ui.editor.authority.AuthorityPageControllerHelper;
import org.nightlabs.jfire.base.admin.ui.editor.authority.UserTableViewer;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypePageController;
import org.nightlabs.jfire.trade.admin.ui.editor.IProductTypeSectionPart;

public class UserSection
extends ToolBarSectionPart
implements IProductTypeSectionPart
{
	private UserTableViewer userTable;

	public UserSection(IFormPage page, Composite parent) {
		super(page, parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED, "Users && user groups in authority");

		userTable = new UserTableViewer(getContainer(), this);
		userTable.setInput(users);
	}

	private ProductType productType;
	private List<Map.Entry<User, Boolean>> users = new ArrayList<Map.Entry<User,Boolean>>();
	private AuthorityPageController authorityPageController;
	private AuthorityPageControllerHelper authorityPageControllerHelper;

	@Override
	public ProductType getProductType() {
		return productType;
	}

	@Override
	public AuthorityPageController getProductTypePageController() {
		return authorityPageController;
	}

	public AuthorityPageControllerHelper getAuthorityPageControllerHelper() {
		return authorityPageControllerHelper;
	}

	@Override
	public void setProductTypePageController(AbstractProductTypePageController<ProductType> productTypeDetailPageController) {
		authorityPageController = (AuthorityPageController) productTypeDetailPageController;
		authorityPageControllerHelper = authorityPageController.getAuthorityPageControllerHelper();

		getSection().getDisplay().asyncExec(new Runnable() {
			public void run() {
				authorityChanged();
			}
		});

		authorityPageControllerHelper.addPropertyChangeListener(
				AuthorityPageControllerHelper.PROPERTY_NAME_AUTHORITY_LOADED, 
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						getSection().getDisplay().asyncExec(new Runnable() {
							public void run() {
								authorityChanged();
							}
						});						
					}
				}
		);
	}

	private void authorityChanged()
	{
		users.clear();
		if (authorityPageControllerHelper.getAuthority() != null)
			users.addAll(authorityPageControllerHelper.createModifiableUserList());
		
		userTable.refresh();
	}
}
