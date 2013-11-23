/**
 *
 */
package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.jfire.prop.StructLocal;
import org.nightlabs.jfire.prop.dao.StructLocalDAO;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueStructLocalScopeSection extends ToolBarSectionPart {

	private String structScope;
	private String structLocalScope;
	private Text structLocalScopeText;

	/**
	 * @param page
	 * @param parent
	 * @param style
	 * @param title
	 */
	public IssueStructLocalScopeSection(IFormPage page, Composite parent, int style)
	{
		super(page, parent, style, Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueStructLocalScopeSection.title")); //$NON-NLS-1$
		updateToolBarManager();
		structLocalScopeText = new Text(getContainer(), XComposite.getBorderStyle(getContainer()) | SWT.SINGLE | SWT.READ_ONLY);
		structLocalScopeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	public void setIssue(Issue issue) {
		structScope = issue.getPropertySet().getStructScope();
		structLocalScope = issue.getPropertySet().getStructLocalScope();
		StructLocal sl = StructLocalDAO.sharedInstance().getStructLocal(
				issue.getPropertySet().getStructLocalObjectID(),
//				Issue.class, structScope, structLocalScope,
				new NullProgressMonitor()
		);
		if (sl != null && sl.getName() != null) {
			structLocalScopeText.setText(sl.getName().getText());
		} else {
			structLocalScopeText.setText(structLocalScope);
		}
	}

}
