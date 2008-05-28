package org.nightlabs.jfire.trade.ui.exceptionhandler;

import org.eclipse.jface.dialogs.MessageDialog;
import org.nightlabs.base.ui.exceptionhandler.IExceptionHandler;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.trade.ui.resource.Messages;

public class NotAvailableExceptionHandler
		implements IExceptionHandler
{

	@Override
	public boolean handleException(Thread thread, Throwable thrownException, Throwable triggerException)
	{
		MessageDialog.openError(
				RCPUtil.getActiveShell(),
				Messages.getString("org.nightlabs.jfire.trade.ui.exceptionhandler.NotAvailableExceptionHandler.dialog.title"), //$NON-NLS-1$
				Messages.getString("org.nightlabs.jfire.trade.ui.exceptionhandler.NotAvailableExceptionHandler.dialog.message")); //$NON-NLS-1$
		return true;
	}

}