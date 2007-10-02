package org.nightlabs.jfire.jbpm.ui.state;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.jbpm.ui.resource.Messages;

public class StateHistoryDialog
extends Dialog
{
	private ObjectID statableID;
	private StateHistoryComposite stateHistoryComposite;

	public StateHistoryDialog(Shell parentShell, ObjectID statableID)
	{
		super(parentShell);
		this.statableID = statableID;
	}

	@Override
	protected void configureShell(Shell newShell)
	{
		newShell.setSize(600, 400);
		newShell.setMinimumSize(300, 300);
		newShell.setText(Messages.getString("org.nightlabs.jfire.jbpm.ui.state.StateHistoryDialog.shell.text")); //$NON-NLS-1$
		super.configureShell(newShell);
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite area = (Composite) super.createDialogArea(parent);

		stateHistoryComposite = new StateHistoryComposite(area, SWT.NONE);
		stateHistoryComposite.setStatableID(statableID);

		return area;
	}

	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton)
	{
		if (id == CANCEL)
			return null;
		
		return super.createButton(parent, id, label, defaultButton);
	}
}
