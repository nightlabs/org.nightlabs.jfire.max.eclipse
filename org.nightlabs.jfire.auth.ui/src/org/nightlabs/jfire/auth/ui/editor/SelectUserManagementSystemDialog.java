package org.nightlabs.jfire.auth.ui.editor;

import java.util.Collection;
import java.util.ResourceBundle;
import java.util.Set;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.jfire.auth.ui.JFireAuthUIPlugin;
import org.nightlabs.jfire.auth.ui.UserManagementSystemActiveJDOTable;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.util.CollectionUtil;

/**
 * Dialog with a {@link UserManagementSystemActiveJDOTable} showing all available {@link UserManagementSystem} objects for selection.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class SelectUserManagementSystemDialog extends ResizableTitleAreaDialog{

	private UserManagementSystemActiveJDOTable userManagementSystemTable;
	private Collection<UserManagementSystem> selectedElements;
	
	/**
	 * Constructs a new {@link SelectUserManagementSystemDialog}. 
	 * 
	 * @param shell Parent {@link Shell}
	 * @param resourceBundle The resource bundle to use for initial size and location hints. May be <code>null</code>.
	 */
	public SelectUserManagementSystemDialog(Shell shell, ResourceBundle resourceBundle) {
		super(shell, resourceBundle);
	}
	
	/**
	 * Get selected {@link UserManagementSystem}s.
	 * 
	 * @return {@link Set} of selected {@link UserManagementSystem}s
	 */
	public Collection<UserManagementSystem> getSelectedElements() {
		return selectedElements;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite wrapper = (Composite) super.createDialogArea(parent);
		wrapper.setLayout(new GridLayout(1, false));
		wrapper.setLayoutData(new GridData(GridData.FILL_BOTH));

		setTitle("Select User management system(s)");
		setMessage("Select at least one User management system");
		setTitleImage(
				SharedImages.getSharedImage(JFireAuthUIPlugin.sharedInstance(), SelectUserManagementSystemDialog.class, "titleImage", "66x75", ImageFormat.png)); //$NON-NLS-1$ //$NON-NLS-2$

		userManagementSystemTable = new UserManagementSystemActiveJDOTable(wrapper, AbstractTableComposite.DEFAULT_STYLE_SINGLE);
		userManagementSystemTable.setLinesVisible(false);
		userManagementSystemTable.setHeaderVisible(true);
		userManagementSystemTable.getTableViewer().setSorter(new ViewerSorter());
		userManagementSystemTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		userManagementSystemTable.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Button okButton = getButton(OK);
				if (okButton != null && !okButton.isDisposed()){
					okButton.setEnabled(event.getSelection() != null && !event.getSelection().isEmpty());
				}
			}
		});
		userManagementSystemTable.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent doubleclickevent) {
				ISelection selection = doubleclickevent.getSelection();
				if (selection instanceof StructuredSelection
						&& ((StructuredSelection) selection).getFirstElement() instanceof UserManagementSystem){
					UserManagementSystem userManagementSystem = (UserManagementSystem) ((StructuredSelection) selection).getFirstElement();
					selectedElements = CollectionUtil.createArrayList(userManagementSystem);
					close();
				}
			}
		});
		userManagementSystemTable.load();

		return wrapper;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void okPressed() {
		selectedElements = userManagementSystemTable.getSelectedElements();
		super.okPressed();
	}
	
}
