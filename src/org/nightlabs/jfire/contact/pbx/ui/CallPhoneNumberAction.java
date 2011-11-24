/**
 * 
 */
package org.nightlabs.jfire.contact.pbx.ui;

import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.LabeledText;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.jfire.base.login.ui.action.LSDWorkbenchWindowActionDelegate;
import org.nightlabs.jfire.contact.pbx.ui.resource.Messages;
import org.nightlabs.jfire.pbx.SimpleCall;
import org.nightlabs.jfire.pbx.ui.call.CallHandlerRegistry;

/**
 * @author abieber
 *
 */
public class CallPhoneNumberAction extends LSDWorkbenchWindowActionDelegate {

	private class EnterNumberDialog extends ResizableTitleAreaDialog {

		private String phoneNumber;
		private LabeledText phoneNumberText;
		
		
		
		public EnterNumberDialog(Shell shell, ResourceBundle resourceBundle) {
			super(shell, resourceBundle);
		}
		
		@Override
		protected Control createDialogArea(Composite parent) {
			Composite area = (Composite) super.createDialogArea(parent);
			getShell().setText(Messages.getString("org.nightlabs.jfire.contact.pbx.ui.CallPhoneNumberAction.dialog.title")); //$NON-NLS-1$
			setTitle(Messages.getString("org.nightlabs.jfire.contact.pbx.ui.CallPhoneNumberAction.dialog.message")); //$NON-NLS-1$
			XComposite wrapper = new XComposite(area, SWT.NONE);
			phoneNumberText = new LabeledText(wrapper, "Phone number");
			phoneNumberText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			phoneNumberText.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent arg0) {
					phoneNumber = phoneNumberText.getText();
				}
			});
			return wrapper;
		}
	}
	
	/**
	 * 
	 */
	public CallPhoneNumberAction() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.login.ui.action.LSDWorkbenchWindowActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action) {
		EnterNumberDialog dlg = new EnterNumberDialog(getShell(), Messages.RESOURCE_BUNDLE);
		if (dlg.open() == Window.OK) {
			String phoneNumber = dlg.phoneNumber;
			CallHandlerRegistry.sharedInstance().call(new SimpleCall(phoneNumber));
		}
	}

}
