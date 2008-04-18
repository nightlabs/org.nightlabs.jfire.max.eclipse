/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkItemChangedEvent;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkWizard;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class IssueLinkAdderComposite 
extends XComposite
{
	private IssueLinkTable issueLinkTable;
	private ListenerList tableItemChangeListeners = new ListenerList();

	private boolean haveButton;
	
	private Issue issue;
	
	/**
	 * @param parent
	 * @param style
	 * @param haveButton the boolean flag uses for displaying button.
	 */
	public IssueLinkAdderComposite(Composite parent, int style, boolean haveButton, Issue issue) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);

		this.haveButton = haveButton;
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

		if (haveButton) {
			XComposite linkedButtonComposite = new XComposite(this, SWT.NONE,
					LayoutMode.TIGHT_WRAPPER);
			linkedButtonComposite.getGridLayout().makeColumnsEqualWidth = true;
			linkedButtonComposite.getGridData().grabExcessHorizontalSpace = false;
			gridData = new GridData(GridData.FILL_VERTICAL);
			gridData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
			linkedButtonComposite.setLayoutData(gridData);

			Button addLinkButton = new Button(linkedButtonComposite, SWT.PUSH);
			addLinkButton.setText("Add Link");
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			addLinkButton.setLayoutData(gridData);

			Button removeLinkButton = new Button(linkedButtonComposite, SWT.PUSH);
			removeLinkButton.setText("Remove Link");
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			removeLinkButton.setLayoutData(gridData);

			addLinkButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(
							new IssueLinkWizard(IssueLinkAdderComposite.this, issue));
					dialog.open();
				}
			});

			removeLinkButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
//					removeItems(issueLinkTable.getSelectedElements());
				}
			});
		}
	}

	private Set<IssueLinkTableItem> issueLinkTableItems = new HashSet<IssueLinkTableItem>();
	
	public void addItem(IssueLinkTableItem item) {
		issueLinkTableItems.add(item);
		issueLinkTable.addElement(item);
	}

	public void removeItem(IssueLinkTableItem item) {
		issueLinkTableItems.remove(item);
		issueLinkTable.removeElement(item);
	}

	public Set<IssueLinkTableItem> getIssueLinkTableItems() {
		return issueLinkTableItems;
	}
	
	public IssueLinkTable getIssueLinkTable() {
		return issueLinkTable;
	}

	public void addIssueLinkTableItemListener(
			IssueLinkTableItemChangedListener listener) {
		tableItemChangeListeners.add(listener);
	}

	public void removeIssueLinkTableItemListener(
			IssueLinkTableItemChangedListener listener) {
		tableItemChangeListeners.remove(listener);
	}

	protected void notifyIssueLinkTableItemListeners() {
		Object[] listeners = tableItemChangeListeners.getListeners();
		IssueLinkItemChangedEvent evt = new IssueLinkItemChangedEvent(this);
		for (Object l : listeners) {
			if (l instanceof IssueLinkTableItemChangedListener) {
				((IssueLinkTableItemChangedListener) l)
						.issueLinkItemChanged(evt);
			}
		}
	}
}