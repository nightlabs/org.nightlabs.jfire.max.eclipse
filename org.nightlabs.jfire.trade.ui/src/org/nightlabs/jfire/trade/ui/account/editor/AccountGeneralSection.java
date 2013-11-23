package org.nightlabs.jfire.trade.ui.account.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jfire.trade.ui.resource.Messages;

public class AccountGeneralSection
extends ToolBarSectionPart
{
	private AccountGeneralComposite accountGeneralComposite;

	public AccountGeneralSection(FormPage page, Composite parent) {
		super(page, parent,
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE,
				"Section title" );
		getSection().setText(Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.AccountGeneralSection.title")); //$NON-NLS-1$

		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		accountGeneralComposite = new AccountGeneralComposite(getSection(), SWT.NONE, this);

		getSection().setClient(accountGeneralComposite);
	}

	public AccountGeneralComposite getAccountEditorComposite(){
		return accountGeneralComposite;
	}
}