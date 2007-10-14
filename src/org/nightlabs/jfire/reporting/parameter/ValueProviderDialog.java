/**
 * 
 */
package org.nightlabs.jfire.reporting.parameter;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.dialog.CenteredDialog;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.reporting.resource.Messages;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ValueProviderDialog extends CenteredDialog {

	private ValueProviderTree valueProviderTree;
	
	private ValueProvider selectedValueProvider;
	
	/**
	 * @param parentShell
	 */
	public ValueProviderDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	/**
	 * @param parentShell
	 */
	public ValueProviderDialog(IShellProvider parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.getString("org.nightlabs.jfire.reporting.parameter.ValueProviderDialog.newShell.text")); //$NON-NLS-1$
	}
	
	@Override
	public void create() {
		super.create();
		setToCenteredLocationPreferredSize(400, 500);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		valueProviderTree = new ValueProviderTree(parent);
		valueProviderTree.getTreeViewer().expandToLevel(2);
		valueProviderTree.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				Object firstSelection = valueProviderTree.getFirstSelectedElement();
				if (firstSelection != null && (firstSelection instanceof ValueProvider))
					selectedValueProvider = (ValueProvider)firstSelection;
				else
					selectedValueProvider = null;
				getButton(IDialogConstants.OK_ID).setEnabled(selectedValueProvider != null);
			}
		});
		valueProviderTree.getTreeViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (selectedValueProvider != null)
					close();
			}
		});
		return valueProviderTree;
	}
	
	public static ValueProvider openDialog() {
		ValueProviderDialog dlg = new ValueProviderDialog(RCPUtil.getActiveWorkbenchShell());
		if (dlg.open() == Window.OK)
			return dlg.selectedValueProvider;
		return null;
	}
	
}
