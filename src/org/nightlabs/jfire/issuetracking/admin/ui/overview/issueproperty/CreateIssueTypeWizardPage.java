package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.i18n.I18nText;

/**
 * @author Daniel Mazurek
 *
 */
public class CreateIssueTypeWizardPage extends DynamicPathWizardPage 
{
	private I18nTextEditor nameEditor;
	
	/**
	 * 
	 */
	public CreateIssueTypeWizardPage() {
		super(CreateIssueTypeWizardPage.class.getName(), "Create Issue Type");
		setDescription("Please define a name for the issue type");
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) 
	{
		XComposite wrapper = new XComposite(parent, SWT.NONE);
		nameEditor = new I18nTextEditor(wrapper);
		return wrapper;
	}

	protected I18nText getIssueTypeName() {
		return nameEditor.getI18nText();
	}
}
