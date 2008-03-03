/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.Collection;
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
import org.nightlabs.jdo.ObjectID;
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
	
	/**
	 * @param parent
	 * @param style
	 * @param haveButton the boolean flag uses for displaying button.
	 */
	public IssueLinkAdderComposite(Composite parent, int style, boolean haveButton) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);

		this.haveButton = haveButton;
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
							new IssueLinkWizard(IssueLinkAdderComposite.this));
					dialog.open();
				}
			});

			removeLinkButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					removeItems(issueLinkTable.getSelectedElements());
				}
			});
		}
	}

	/*
	 * The old referenced object id set.
	 */
	private Set<ObjectID> oObjectIDs = null;
	
	/*
	 * The modified referenced object id set.
	 */
	private Set<ObjectID> mObjectIDs = new HashSet<ObjectID>();

	
	public void setObjectIDs(Set<ObjectID> newObjectIDs) {
		mObjectIDs.addAll(newObjectIDs);
		if (oObjectIDs == null) {
			oObjectIDs = newObjectIDs;
		}
		issueLinkTable.setInput(mObjectIDs);
	}

	public void addObjectIDs(Set<ObjectID> objectIDs) {
		mObjectIDs.addAll(objectIDs);
		if (oObjectIDs == null) {
			oObjectIDs = objectIDs;
		} else {
			if (!mObjectIDs.equals(oObjectIDs))
				notifyIssueLinkTableItemListeners();
		}
		issueLinkTable.setInput(mObjectIDs);
	}

	public boolean removeItems(Collection<ObjectID> removedItems) {
		if (mObjectIDs == null) {
			mObjectIDs = new HashSet<ObjectID>();
			mObjectIDs.addAll(oObjectIDs);
		}

		boolean result = mObjectIDs.removeAll(removedItems);

		if (!mObjectIDs.equals(oObjectIDs))
			notifyIssueLinkTableItemListeners();

		issueLinkTable.setInput(mObjectIDs);
		return result;
	}

	public Set<ObjectID> getItems() {
		return mObjectIDs;
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