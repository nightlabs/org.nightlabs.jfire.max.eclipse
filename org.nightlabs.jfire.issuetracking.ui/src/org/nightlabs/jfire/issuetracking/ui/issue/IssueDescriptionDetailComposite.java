package org.nightlabs.jfire.issuetracking.ui.issue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.jfire.issue.IssueDescription;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class IssueDescriptionDetailComposite
extends XComposite
{
	private IssueDescription issueDescription;
	private I18nTextEditor descriptionText;

	public IssueDescriptionDetailComposite(Composite parent, int style) {
		super(parent, style);

		FormToolkit toolkit = new FormToolkit(getDisplay());

		Composite mainComposite = toolkit.createComposite(this);
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		mainComposite.setLayout(new GridLayout(1, false));

		descriptionText = new I18nTextEditorMultiLine(mainComposite);
		descriptionText.setEditable(false);
		descriptionText.setLayoutData(new GridData(GridData.FILL_BOTH));

		if (this.issueDescription != null) {
			descriptionText.setI18nText(issueDescription);
		}
	}

	public void setIssueDescription(IssueDescription issueDescription) {
		this.issueDescription = issueDescription;
		descriptionText.setI18nText(issueDescription);
	}
}