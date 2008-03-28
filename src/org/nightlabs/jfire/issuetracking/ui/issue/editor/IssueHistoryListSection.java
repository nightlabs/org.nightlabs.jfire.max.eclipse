package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.editor.RestorableSectionPart;
import org.nightlabs.jfire.issuetracking.ui.issuehistory.IssueHistoryTable;

/* 
* @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
*/
public class IssueHistoryListSection extends RestorableSectionPart{

	private IssueHistoryTable issueHistoryTable;
	private IssueEditorPageController controller;
	
	public IssueHistoryListSection(FormPage page, Composite parent, IssueEditorPageController controller) {
		super(parent, page.getEditor().getToolkit(), ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		this.controller = controller;
		getSection().setText("Issue History");
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());
		
//		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
//		client.getGridLayout().numColumns = 1; 
		
		issueHistoryTable = new IssueHistoryTable(getSection(), SWT.NONE);
		issueHistoryTable.getGridData().grabExcessHorizontalSpace = true;
		
		getSection().setClient(issueHistoryTable);
	}
}
