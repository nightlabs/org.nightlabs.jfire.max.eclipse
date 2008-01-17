package org.nightlabs.jfire.issuetracking.ui.overview;

import javax.security.auth.login.LoginException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.overview.AbstractCategory;
import org.nightlabs.jfire.base.ui.overview.CategoryFactory;
import org.nightlabs.jfire.base.ui.overview.DefaultCategoryComposite;

public class IssueDefaultCategory 
extends AbstractCategory
{
	private FormToolkit toolkit;
	private ScrolledForm form;
	
	private DefaultCategoryComposite categoryComposite = null;

	/**
	 * Create a new {@link DefaultCategoryComposite}.
	 * <p>
	 * Note that the {@link IssueDefaultCategory} will 
	 * create its entries in the constructor.
	 * </p>
	 * 
	 * @param categoryFactory The factory creating this category.
	 */
	public IssueDefaultCategory(CategoryFactory categoryFactory) {
		super(categoryFactory);
		createEntries();
		
		try {
			Login.getLogin();
		} catch (LoginException e) {
			throw new RuntimeException(e);
		}
	}

	/** 
	 * {@inheritDoc}
	 * <p>
	 * This method is intended to be overridden in order to use
	 * other GUI to display the categorys entries.
	 * </p>
	 * @see org.nightlabs.jfire.base.ui.overview.Category#createComposite(org.eclipse.swt.widgets.Composite)
	 */
	public Composite createComposite(Composite composite) {
		toolkit = new FormToolkit(composite.getDisplay());
		form = toolkit.createScrolledForm(composite);
		
		GridLayout layout = new GridLayout();
//		layout.marginTop = 0;
//		layout.marginBottom = 0;
//		layout.marginLeft = 0;
//		layout.marginRight = 0;
		XComposite.configureLayout(LayoutMode.TIGHT_WRAPPER, layout);
		
		form.getBody().setLayout(layout);
		
		categoryComposite = new DefaultCategoryComposite(form.getBody(), SWT.NONE, this, AbstractTableComposite.DEFAULT_STYLE_SINGLE);
		
		StoredIssueQuerySection section = new StoredIssueQuerySection(toolkit, form.getBody());
		return categoryComposite;
	}

	/**
	 * {@inheritDoc}
	 * @see org.nightlabs.jfire.base.ui.overview.Category#getComposite()
	 */
	public Composite getComposite() {
		return categoryComposite;
	}

	/**
	 * {@inheritDoc}
	 * @see org.nightlabs.jfire.base.ui.overview.AbstractCategory#updateCategoryComposite()
	 */
	@Override
	protected void updateCategoryComposite() {
		if (categoryComposite == null)
			return;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (categoryComposite != null && !categoryComposite.isDisposed()) 
					categoryComposite.setInput(getEntries());
			}
		});
	}
}
