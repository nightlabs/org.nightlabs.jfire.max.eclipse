package org.nightlabs.jfire.voucher.editor2d.action;

import javax.security.auth.login.LoginException;

import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.action.NewFileAction;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.voucher.editor2d.dialog.VoucherChooseDialog;
import org.nightlabs.jfire.voucher.editor2d.scripting.VoucherScriptResultProvider;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class NewVoucherFileAction 
extends NewFileAction 
{

	public NewVoucherFileAction() {
		super();
	}

	public void run() 
	{		
		try {
			Login.getLogin();
			if (VoucherScriptResultProvider.sharedInstance().getScriptResults() == null) {
				Runnable runnable = new Runnable() {		
					public void run() {
						VoucherChooseDialog voucherChooseDialog = new VoucherChooseDialog(
								RCPUtil.getActiveWorkbenchShell());
						voucherChooseDialog.open();
					}
				};

				if (Display.getDefault().getThread() == Thread.currentThread())
					runnable.run();
				else
					Display.getDefault().syncExec(runnable);
			}
			super.run();
		} catch (LoginException e1) {

		}		
	}
}
