package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueComment;

public class IssueCommentListSection 
extends AbstractIssueEditorGeneralSection
{
	private Issue issue;
	private FormToolkit toolkit;
	private XComposite commentComposite;

	
	public IssueCommentListSection(FormPage page, Composite parent, final IssueEditorPageController controller) {
		super(page, parent, controller);
		getSection().setText("Comment(s)");
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		// Sets up the toolkit.
		toolkit = new FormToolkit(getSection().getShell().getDisplay());
		
		commentComposite = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		commentComposite.getGridLayout().numColumns = 1; 

		getSection().setClient(commentComposite);
	}
	
	private boolean firstLoaded = true;
	@Override
	protected void doSetIssue(Issue issue) {
		
		this.issue = issue;

		if (firstLoaded) {
			List<IssueComment> comments = issue.getComments();
			for (int i = 0; i < comments.size(); i++) {
				addComment(comments.get(i), i == comments.size() - 1 ? true : false);
			}
			firstLoaded = false;
		}
		else {
			if (issue.getComments().size() > 0) {
				addComment(issue.getComments().get(issue.getComments().size() - 1), true);
			}
		}
		getManagedForm().getForm().reflow(true);
	}

	public void addComment(IssueComment comment, boolean expand) {
		ExpandableComposite commentEntry = new ExpandableComposite(commentComposite, SWT.NONE, ExpandableComposite.COMPACT | ExpandableComposite.TREE_NODE | ExpandableComposite.EXPANDED);
		commentEntry.setText(String.format("%s - %s", 
				comment.getUser().getName(), 
				comment.getCreateTimestamp().toString()));

		FormText text = toolkit.createFormText(commentEntry, false);
		text.setFont(new Font(getSection().getDisplay(), new FontData("Courier", 10, SWT.NORMAL)));
		
		text.setText(comment.getText(),
				false,
				false);
		
		
		commentEntry.setClient(text);

		commentEntry.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				// resizes the application window.
				getManagedForm().getForm().reflow(true);
			}
		});
		
		commentEntry.setExpanded(expand);
	}
}