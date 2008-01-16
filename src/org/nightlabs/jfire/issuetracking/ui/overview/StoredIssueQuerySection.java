package org.nightlabs.jfire.issuetracking.ui.overview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;

public class StoredIssueQuerySection 
extends ToolBarSectionPart 
{
	private XComposite client;
	
	public StoredIssueQuerySection(FormToolkit toolkit, Composite parent) {
		super(toolkit, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR, "Stored Filters");
		
		getSection().setLayoutData(new GridData(GridData.FILL_BOTH));
		getSection().setLayout(new GridLayout());
		
		client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1; 
		
		StoredIssueQueryTable storedIssueQueryTable = new StoredIssueQueryTable(client, SWT.NONE);
		
		getSection().setClient(client);
	} 

}
