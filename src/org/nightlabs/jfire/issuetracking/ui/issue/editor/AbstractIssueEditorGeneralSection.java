package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jfire.issue.Issue;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public abstract class AbstractIssueEditorGeneralSection extends ToolBarSectionPart {

	private IssueEditorPageController controller;

	private XComposite client;
	
	private Issue issue;
	
	/**
	 * @param page
	 * @param parent
	 * @param style
	 * @param title
	 */
	public AbstractIssueEditorGeneralSection(FormPage page, Composite parent, IssueEditorPageController controller) {
		super(
				page, parent, 
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE,
				"Section Title"
		);
		this.controller = controller;
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());
		
		client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1; 
		
		getSection().setClient(client);
	}
	
	public IssueEditorPageController getController() {
		return controller;
	}

	public XComposite getClient() {
		return client;
	}
	
	public void setIssue(Issue issue) {
		this.issue = issue;
		doSetIssue(issue);
	}
	
	public Issue getIssue() {
		return issue;
	}
	
	protected abstract void doSetIssue(Issue issue);
}
