package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.issue.Issue;

public class IssueCommentListSection 
extends AbstractIssueEditorGeneralSection
{
	private Issue issue;
	public IssueCommentListSection(FormPage page, Composite parent, final IssueEditorPageController controller) {
		super(page, parent, controller);
		getSection().setText("Comment(s)");
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1; 

		ExpandableComposite commentEntry = new ExpandableComposite(client, ExpandableComposite.TREE_NODE | ExpandableComposite.EXPANDED);
		
		XComposite commentTextComposite = new XComposite(commentEntry, SWT.NONE);
		Button b = new Button(commentTextComposite, SWT.PUSH);
		b.setText("Hello World!!!!!!!!!!");
		
		commentEntry.setClient(commentTextComposite);
		
		getSection().setClient(client);
	}
	@Override
	protected void doSetIssue(Issue issue) {
		// TODO Auto-generated method stub

	}
	
}
