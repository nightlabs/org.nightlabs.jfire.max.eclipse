package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import java.text.DateFormat;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueComment;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueCommentListSection 
extends AbstractIssueEditorGeneralSection
{
	private Issue issue;
	private FormToolkit toolkit;
	private XComposite commentComposite;

	private int oldSize;
	
	public IssueCommentListSection(FormPage page, Composite parent, final IssueEditorPageController controller) {
		super(page, parent, controller);
		getSection().setText("Comment");
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Sets up the toolkit.
		toolkit = new FormToolkit(getSection().getShell().getDisplay());
		
		commentComposite = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		commentComposite.getGridLayout().numColumns = 1; 

		getSection().setClient(commentComposite);
	}
	
	private boolean firstLoaded = true;
	@Override
	protected void doSetIssue(Issue newIssue) {
		
		if (issue != null && newIssue.getComments().size() == oldSize) {
			return;
		}
		
		issue = newIssue;
		oldSize = issue.getComments().size();
		
		if (firstLoaded) {
			List<IssueComment> comments = newIssue.getComments();
			for (int i = 0; i < comments.size(); i++) {
				addComment(comments.get(i), i == comments.size() - 1 ? true : false);
			}
			firstLoaded = false;
		}
		else {
			if (newIssue.getComments().size() > 0) {
				addComment(newIssue.getComments().get(newIssue.getComments().size() - 1), true);
			}
		}
		
		reflowParentForm();
	}

	protected void reflowParentForm() {
		getManagedForm().getForm().getBody().layout(true, true);
		getManagedForm().getForm().reflow(true);
	}
	
	private static DateFormat dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
	
	public void addComment(IssueComment comment, boolean expand) {
		ExpandableComposite commentEntry = new ExpandableComposite(commentComposite, SWT.NONE, ExpandableComposite.COMPACT | ExpandableComposite.TREE_NODE | ExpandableComposite.EXPANDED);
		commentEntry.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		commentEntry.setFont(new Font(getSection().getDisplay(), new FontData("Courier", 10, SWT.BOLD)));
		commentEntry.setText(String.format("%s - %s", 
				comment.getUser().getName(), 
				dateTimeFormat.format(comment.getCreateTimestamp())));

		toolkit.adapt(commentEntry);
		
		/********Using Text********/
		Text text = toolkit.createText(commentEntry, comment.getText(), SWT.MULTI | SWT.WRAP);
		text.setEditable(false);
		
		text.setFont(new Font(getSection().getDisplay(), new FontData("Courier", 10, SWT.NORMAL)));
		commentEntry.setClient(text);

		commentEntry.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				// resizes the application window.				
				reflowParentForm();
			}
		});
		
		commentEntry.setExpanded(expand);
	}
}