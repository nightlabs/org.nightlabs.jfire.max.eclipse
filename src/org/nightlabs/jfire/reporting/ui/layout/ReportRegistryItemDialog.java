package org.nightlabs.jfire.reporting.ui.layout;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.JDOHelper;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.eclipse.ui.dialog.ResizableTrayDialog;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;

/**
 * A dialog that lets the user choose one or more items from an unfiltered
 * {@link ReportRegistryItemTree}.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * 
 */
public class ReportRegistryItemDialog extends ResizableTrayDialog {

	/**
	 * An instance of {@link ISelectionVerifier} can be set for a dialog and will be consulted when
	 * the selection changes in order to verify the selection for the use-case of the caller.
	 */
	public static interface ISelectionVerifier {
		/**
		 * Will be called when the selection in the tree changes and returns whether this selection
		 * is valid for the use-case of the implementation.
		 * 
		 * @param reportRegistryItems The currently selected {@link ReportRegistryItem}s.
		 * @return Whether the given selection is valid.
		 */
		boolean isSelectionValid(Collection<ReportRegistryItem> reportRegistryItems);
	}

	
	private ReportRegistryItemTree layoutTree;
	private Collection<ReportRegistryItemID> selectedItemIDs;
	private ISelectionVerifier selectionVerifier;

	/**
	 * Create a new {@link ReportRegistryItemDialog} with the given Shell as parent.
	 * 
	 * @param parentShell The parent Shell for the new dialog.
	 * @param selectionVerifier A {@link ISelectionVerifier} that is asked whether the current
	 *            selection can be passed as result. This can be <code>null</code>, in this case
	 *            every selection is valid.
	 */
	public ReportRegistryItemDialog(Shell parentShell, ISelectionVerifier selectionVerifier) {
		super(parentShell, null);
		this.selectionVerifier = selectionVerifier;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		layoutTree = new ReportRegistryItemTree(parent, false, null);
		layoutTree.getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				Set<ReportRegistryItem> selectedElements = layoutTree.getSelectedElements();
				if (selectionVerifier != null && selectionVerifier.isSelectionValid(selectedElements)) { 
					Collection<ReportRegistryItemID> selectedIDs = new HashSet<ReportRegistryItemID>();
					for (ReportRegistryItem item : selectedElements) {
						selectedIDs.add((ReportRegistryItemID) JDOHelper.getObjectId(item));
					}
					selectedItemIDs = selectedIDs;
				}  else {
					selectedItemIDs = null;
				}
				updateOKButton();
			}
		});
		layoutTree.getTreeViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (selectedItemIDs != null)
					close();
			}
		});
		layoutTree.getTreeViewer().expandToLevel(3);
		return layoutTree;
	}

	/**
	 * Updates the OK Button to be only enabled when the selection was valid.
	 */
	private void updateOKButton() {
		Button okButton = getButton(IDialogConstants.OK_ID);
		if (okButton != null) {
			okButton.setEnabled(selectedItemIDs != null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Select report layout");		
	}

	/**
	 * @return The ID-Objects of the {@link ReportRegistryItem}s that were the last valid selection
	 *         in this dialog, or <code>null</code> if the last selection was not valid.
	 */
	public Collection<ReportRegistryItemID> getSelectedItemIDs() {
		return selectedItemIDs;
	}

	/**
	 * Opens a {@link ReportRegistryItemDialog} with the given shell as parent and returns the last
	 * valid selection, or <code>null</code> if the user cancels the dialog.
	 * 
	 * @param parentShell The parent Shell for the new dialog.
	 * @param selectionVerifier An {@link ISelectionVerifier} for the dialog, that checks the
	 *            selection, or <code>null</code> if all selections are valid.
	 * @return The last valid selection in the dialog, or <code>null</code> if the user cancels the
	 *         dialog.
	 */
	public static Collection<ReportRegistryItemID> openDialog(Shell parentShell, ISelectionVerifier selectionVerifier) {
		ReportRegistryItemDialog dlg = new ReportRegistryItemDialog(parentShell, selectionVerifier);
		if (dlg.open() == Window.OK)
			return dlg.getSelectedItemIDs();
		return null;
	}

	/**
	 * Opens a {@link ReportRegistryItemDialog} with the given shell as parent and no
	 * {@link ISelectionVerifier} (i.e. all selections are valid regardless whether ReportCategory
	 * or ReportLayout).
	 * 
	 * @param parentShell The parent Shell for the new dialog.
	 * @return The last valid selection in the dialog, or <code>null</code> if the user cancels the
	 *         dialog.
	 */
	public static Collection<ReportRegistryItemID> openDialog(Shell parentShell) {
		return openDialog(parentShell, null);
	}
}
