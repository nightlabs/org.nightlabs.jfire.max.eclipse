/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issuelink;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTable;
import org.nightlabs.jfire.issuetracking.ui.issuelink.create.CreateIssueLinkWizard;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class IssueLinkObjectChooserComposite 
extends XComposite
{
	private IssueLinkTable issueLinkTable;
	private Issue dummyIssue = new Issue(true);
	
	public IssueLinkObjectChooserComposite(Composite parent, int style) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
		createComposite();
	}

	private void createComposite() {
		getGridLayout().numColumns = 2;
		getGridLayout().makeColumnsEqualWidth = false;
		getGridData().grabExcessHorizontalSpace = true;

		issueLinkTable = new IssueLinkTable(this, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 200;
		issueLinkTable.setLayoutData(gridData);
		issueLinkTable.setIssue(dummyIssue);

		XComposite linkedButtonComposite = new XComposite(this, SWT.NONE,
				LayoutMode.TIGHT_WRAPPER);
		linkedButtonComposite.getGridLayout().makeColumnsEqualWidth = true;
		linkedButtonComposite.getGridData().grabExcessHorizontalSpace = false;
		gridData = new GridData(GridData.FILL_VERTICAL);
		gridData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		linkedButtonComposite.setLayoutData(gridData);

		Button addLinkButton = new Button(linkedButtonComposite, SWT.PUSH);
		addLinkButton.setText("Add");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		addLinkButton.setLayoutData(gridData);

		Button removeLinkButton = new Button(linkedButtonComposite, SWT.PUSH);
		removeLinkButton.setText("Remove");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		removeLinkButton.setLayoutData(gridData);

		addLinkButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CreateIssueLinkWizard wizard = new CreateIssueLinkWizard(issueLinkTable, dummyIssue);
				DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(getShell(),
						wizard);
				dialog.open();
			}
		});

		removeLinkButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				issueLinkTable.removeIssueLinkTableItems(issueLinkTable.getSelectedElements());
			}
		});
	}

	public IssueLinkTable getIssueLinkTable() {
		return issueLinkTable;
	}	
}