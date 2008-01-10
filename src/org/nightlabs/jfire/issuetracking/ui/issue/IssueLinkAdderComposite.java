/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkWizard;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class IssueLinkAdderComposite 
extends XComposite 
{
	private IssueLinkTable issueLinkTable;
	/**
	 * @param parent
	 * @param style
	 */
	public IssueLinkAdderComposite(Composite parent, int style) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);

		createComposite();
	}

	private void createComposite() {
		getGridLayout().numColumns = 2;
		getGridLayout().makeColumnsEqualWidth = false;
		getGridData().grabExcessHorizontalSpace = true;

		issueLinkTable = new IssueLinkTable(this, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.verticalSpan = 3;
		issueLinkTable.setLayoutData(gridData);

		XComposite linkedButtonComposite = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		linkedButtonComposite.getGridLayout().makeColumnsEqualWidth = true;
		linkedButtonComposite.getGridData().grabExcessHorizontalSpace = false;

		Button addLinkButton = new Button(linkedButtonComposite, SWT.PUSH);
		addLinkButton.setText("Add Link");
		Button removeLinkButton = new Button(linkedButtonComposite, SWT.PUSH);
		removeLinkButton.setText("Remove Link");

		addLinkButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(new IssueLinkWizard(IssueLinkAdderComposite.this));
				dialog.open();
			}
		});
	}

	public void setItems(Set<String> items) {
		this.items = items;
		if (items != null) {
			issueLinkTable.setInput(items);
		}
	}
	
	private Set<String> items;
	
	public Set<String> getItems() {
		return items;
	}

	public IssueLinkTable getIssueLinkTable() {
		return issueLinkTable;
	}
}
