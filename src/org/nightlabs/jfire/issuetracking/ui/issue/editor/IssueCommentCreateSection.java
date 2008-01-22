package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import javax.jdo.FetchPlan;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueComment;

public class IssueCommentCreateSection 
extends AbstractIssueEditorGeneralSection
{
	private Issue issue;
	private Text commentText;
	
	public IssueCommentCreateSection(FormPage page, Composite parent, final IssueEditorPageController controller) {
		super(page, parent, controller);
		getSection().setText("New Comment");
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1; 

		commentText = new Text(client, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		commentText.setFont(new Font(getSection().getDisplay(), new FontData("Courier", 10, SWT.NORMAL)));
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 60;
		commentText.setLayoutData(gridData);
		commentText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if(!commentText.getText().equals(""))
					markDirty();
				else
					markUndirty();
			}
		});
		
		getSection().setClient(client);
	}
	@Override
	protected void doSetIssue(Issue issue) {
		this.issue = issue;
		commentText.setText("");
	}
	
	@Override
	public void commit(boolean onSave) {
		super.commit(onSave);
		if (commentText != null && !commentText.equals("")) {
			IssueComment comment = new IssueComment(issue.getOrganisationID(), 
					issue.getIssueID(), 
					Long.toString(IDGenerator.nextID(IssueComment.class)), 
					commentText.getText(), 
					Login.sharedInstance().getUser(new String[]{FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new org.eclipse.core.runtime.NullProgressMonitor()));
			issue.getComments().add(comment);
		}
	}
}
