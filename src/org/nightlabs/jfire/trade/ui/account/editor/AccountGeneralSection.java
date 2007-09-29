package org.nightlabs.jfire.trade.ui.account.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.nightlabs.base.ui.editor.RestorableSectionPart;
import org.nightlabs.base.ui.entity.editor.EntityEditorUtil;
import org.nightlabs.jfire.trade.ui.resource.Messages;

public class AccountGeneralSection 
extends RestorableSectionPart 
{
	private AccountGeneralComposite accountGeneralComposite;
	
	public AccountGeneralSection(FormPage page, Composite parent) {
		super(parent, page.getEditor().getToolkit(), ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		getSection().setText(Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.AccountGeneralSection.title")); //$NON-NLS-1$
		createClient(getSection(), page.getEditor().getToolkit());
	}

	/**
	 * Create the content for this section.
	 * @param section The section to fill
	 * @param toolkit The toolkit to use
	 */
	protected void createClient(Section section, FormToolkit toolkit) 
	{
		GridData gridData = new GridData(GridData.FILL_BOTH);

		section.setLayout(new GridLayout());
		section.setLayoutData(gridData);

		Composite container = EntityEditorUtil.createCompositeClient(toolkit, section, 1);
		accountGeneralComposite = new AccountGeneralComposite(container, SWT.NONE, this);
	}

	public AccountGeneralComposite getAccountEditorComposite(){
		return accountGeneralComposite;
	}
}
