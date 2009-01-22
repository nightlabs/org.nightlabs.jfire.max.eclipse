package org.nightlabs.jfire.issuetracking.trade.ui.issuelink;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueTable;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class ShowLinkedIssueSection 
extends ToolBarSectionPart 
{
	private ShowLinkedIssuePageController controller;
	private IssueTable issueTable;
	
	/**
	 * @param page
	 * @param parent
	 * @param controller
	 */
	public ShowLinkedIssueSection(IFormPage page, Composite parent, final ShowLinkedIssuePageController controller) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR, "Linked Issue");
		this.controller = controller;
		
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1; 

		getSection().setClient(client);

		issueTable = new IssueTable(client, SWT.NONE);
		issueTable.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	public void setLinkedIssues(Collection<Issue> issues) {
		issueTable.setInput(issues);
	}
}
