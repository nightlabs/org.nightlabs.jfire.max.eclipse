package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.RestorableSectionPart;
import org.nightlabs.jfire.issue.IssueType;

public class IssueTypePrioritySection extends RestorableSectionPart {

	private IssueTypeEditorPageController controller;
	private IssuePriorityTable issuePriorityTable;
	
	public IssueTypePrioritySection(FormPage page, Composite parent, IssueTypeEditorPageController controller) {
		super(parent, page.getEditor().getToolkit(), ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		this.controller = controller;
		getSection().setText("Section Title");
		getSection().setLayoutData(new GridData(GridData.FILL_BOTH));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1; 

		issuePriorityTable = new IssuePriorityTable(client, SWT.NONE);

		getSection().setClient(client);
	}

	public void setIssueType(IssueType issueType){
		issuePriorityTable.setInput(issueType.getIssuePriorities());
	}
}
