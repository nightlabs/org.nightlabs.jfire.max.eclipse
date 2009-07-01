/**
 *
 */
package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.FormPage;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class IssueSubjectAndDescriptionSection extends AbstractIssueEditorGeneralSection {

	private Label subjectLabel;
	private I18nTextEditor subjectText;
	private Label descriptionLabel;
	private I18nTextEditor descriptionText;

	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent arg0) {
			markDirty();
		}
	};

	/**
	 * @param section
	 * @param managedForm
	 */
	public IssueSubjectAndDescriptionSection(FormPage page, Composite parent, IssueEditorPageController controller) {
		super(page, parent, controller);
		getClient().getGridLayout().numColumns = 2;
		getClient().getGridLayout().makeColumnsEqualWidth = false;
		getSection().setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueSubjectAndDescriptionSection.section.text")); //$NON-NLS-1$

		subjectLabel = new Label(getClient(), SWT.WRAP);
		subjectLabel.setLayoutData(new GridData());
		subjectLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueSubjectAndDescriptionSection.label.subject.text")); //$NON-NLS-1$

		subjectText = new I18nTextEditor(getClient());
		subjectText.addModifyListener(modifyListener);

		descriptionLabel = new Label(getClient(), SWT.WRAP);
		descriptionLabel.setLayoutData(new GridData());
		descriptionLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueSubjectAndDescriptionSection.label.description.text")); //$NON-NLS-1$

		descriptionText = new I18nTextEditorMultiLine(getClient(), subjectText.getLanguageChooser());
		descriptionText.addModifyListener(modifyListener);
	}

	private boolean isIDSet = false;
	@Override
	protected void doSetIssue(Issue issue) {
		subjectText.setI18nText(issue.getSubject(), EditMode.DIRECT);
		descriptionText.setI18nText(issue.getDescription(), EditMode.DIRECT);
//		((GridData) descriptionText.getLayoutData()).heightHint = descriptionLabel.getBounds().height * 10;

		// Show also the IssueID? On the title panel, since this value is unmodifiable.
		if (!isIDSet) {
			getSection().setText(
				Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueSubjectAndDescriptionSection.section.text") //$NON-NLS-1$
				+ " (ID:" + ObjectIDUtil.longObjectIDFieldToString(issue.getIssueID()) + ")"
			);

			isIDSet = true;
		}
	}

//	@Override
//	public void commit(boolean onSave) {
//		super.commit(onSave);
//		getIssue().getSubject().setText(subjectText.getLanguageChooser().getLanguage().getLanguageID(), subjectText.getEditText());
//		getIssue().getDescription().setText(descriptionText.getLanguageChooser().getLanguage().getLanguageID(), descriptionText.getEditText());
//	}
}
