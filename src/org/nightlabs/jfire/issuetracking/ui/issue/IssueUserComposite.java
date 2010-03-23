package org.nightlabs.jfire.issuetracking.ui.issue;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.base.ui.security.UserSearchComposite;
import org.nightlabs.jfire.base.ui.security.UserSearchDialog;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.jfire.security.User;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class IssueUserComposite extends XComposite
{
	private User selectedUser;
	private Text userNameText;

	public enum Orientation {
		TOP,
		LEFT
	}

	/**
	 * @param parent
	 * @param style
	 * @param caption
	 */
	public IssueUserComposite(Composite parent, int style, String caption, Orientation orientation) {
		super(parent, style);
		create(this, caption, orientation);
	}

	/**
	 *
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 * @param caption
	 */
	public IssueUserComposite(Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode, String caption, Orientation orientation) {
		super(parent, style, layoutMode, layoutDataMode);
		create(this, caption, orientation);
	}

	public User getSelectedUser() {
		return selectedUser;
	}

	protected void create(Composite parent, String caption, Orientation orientation)
	{
		if (caption != null && orientation != null)
		{
			if (orientation == Orientation.TOP) {
				Label label = new Label(parent, SWT.NONE);
				label.setText(caption);
			}
			else if (orientation == Orientation.LEFT) {
				parent = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL, 2);
				Label label = new Label(parent, SWT.NONE);
				label.setText(caption);
			}
		}
		XComposite userComp = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL, 3);
		userNameText = new Text(userComp, userComp.getBorderStyle());
		userNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button chooseUserButton = new Button(userComp, SWT.NONE);
		chooseUserButton.setText("..."); //$NON-NLS-1$
		chooseUserButton.setToolTipText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueUserComposite.button.chooseUser.tooltip")); //$NON-NLS-1$
		chooseUserButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				UserSearchDialog dialog = new UserSearchDialog(getShell(), "", UserSearchComposite.FLAG_TYPE_USER); //$NON-NLS-1$
				int returnCode = dialog.open();
				if (returnCode == Window.OK) {
					selectedUser = dialog.getSelectedUser();
					if (selectedUser != null) {
						userNameText.setText(selectedUser.getName());
					}
				}
			}
		});

		Button deleteUserButton = new Button(userComp, SWT.NONE);
		deleteUserButton.setImage(SharedImages.DELETE_16x16.createImage());
		deleteUserButton.setToolTipText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueUserComposite.button.removeUser.tooltip")); //$NON-NLS-1$
		deleteUserButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedUser = null;
				userNameText.setText(""); //$NON-NLS-1$
			}
		});
	}

	public void addModifyListener(ModifyListener modifyListener)
	{
		userNameText.addModifyListener(modifyListener);
	}

	public void removeModifyListener(ModifyListener modifyListener)
	{
		userNameText.removeModifyListener(modifyListener);
	}
}
