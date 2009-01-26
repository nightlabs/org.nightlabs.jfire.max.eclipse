/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.issuelink.create.CreateIssueLinkWizard;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

/**
 * A composite that contains UIs for adding {@link IssueLink}.
 * 
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class IssueLinkAdderComposite 
extends XComposite
{
	private IssueLinkTable issueLinkTable;

	private boolean haveButtons;

	private Issue issue;

	/**
	 * Contructs a composite used for adding {@link IssueLink}.
	 * 
	 * @param parent -the parent composite
	 * @param style - the SWT style flag 
	 * @param haveButton - the boolean flag uses for displaying button
	 * @param issue - the {@link Issue} used in adding process
	 */
	public IssueLinkAdderComposite(Composite parent, int style, boolean haveButton, Issue issue) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);

		this.haveButtons = haveButton;
		this.issue = issue;

		createComposite();
	}

	private void createComposite() {
		getGridLayout().numColumns = 2;
		getGridLayout().makeColumnsEqualWidth = false;
		getGridData().grabExcessHorizontalSpace = true;

		issueLinkTable = new IssueLinkTable(this, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 100;
		issueLinkTable.setLayoutData(gridData);

		if (haveButtons) {
			XComposite linkedButtonComposite = new XComposite(this, SWT.NONE,
					LayoutMode.TIGHT_WRAPPER);
			linkedButtonComposite.getGridLayout().makeColumnsEqualWidth = true;
			linkedButtonComposite.getGridData().grabExcessHorizontalSpace = false;
			gridData = new GridData(GridData.FILL_VERTICAL);
			gridData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
			linkedButtonComposite.setLayoutData(gridData);

			Button addLinkButton = new Button(linkedButtonComposite, SWT.PUSH);
			addLinkButton.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkAdderComposite.button.addLink.text")); //$NON-NLS-1$
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			addLinkButton.setLayoutData(gridData);

			Button removeLinkButton = new Button(linkedButtonComposite, SWT.PUSH);
			removeLinkButton.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkAdderComposite.button.removeLink.text")); //$NON-NLS-1$
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			removeLinkButton.setLayoutData(gridData);

			addLinkButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(
							new CreateIssueLinkWizard(issueLinkTable, issue));
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

		if (issue != null)
			issueLinkTable.setIssue(issue);
	}

	/**
	 * Gets the {@link IssueTable}.
	 * @return Returns the {@link IssueTable}
	 */
	public IssueLinkTable getIssueLinkTable() {
		return issueLinkTable;
	}
}