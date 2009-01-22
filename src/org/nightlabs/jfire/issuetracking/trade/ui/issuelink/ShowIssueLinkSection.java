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
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTable;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTableItem;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class ShowIssueLinkSection 
extends ToolBarSectionPart 
{
	private ShowIssueLinkPageController controller;
	private IssueLinkTable issueLinkTable;
	
	/**
	 * @param page
	 * @param parent
	 * @param controller
	 */
	public ShowIssueLinkSection(IFormPage page, Composite parent, final ShowIssueLinkPageController controller) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR, "Issue Links");
		this.controller = controller;
		
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1; 

		getSection().setClient(client);

		issueLinkTable = new IssueLinkTable(client, SWT.NONE);
		issueLinkTable.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	public void setIssueLinkTableItems(Collection<IssueLinkTableItem> issueLinkTableItems) {
		issueLinkTable.setInput(issueLinkTableItems);
	}
}
