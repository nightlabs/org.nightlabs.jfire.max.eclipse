package org.nightlabs.jfire.trade.ui.exceptionhandler;

import org.eclipse.jface.dialogs.MessageDialog;
import org.nightlabs.base.ui.exceptionhandler.IExceptionHandler;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.trade.ui.resource.Messages;

public class NotAvailableExceptionHandler
		implements IExceptionHandler
{

	@Override
	public void handleException(Thread thread, Throwable thrownException, Throwable triggerException)
	{
		MessageDialog.openError(RCPUtil.getActiveShell(), Messages.getString("org.nightlabs.jfire.trade.ui.exceptionhandler.NotAvailableExceptionHandler.dialog.title"), Messages.getString("org.nightlabs.jfire.trade.ui.exceptionhandler.NotAvailableExceptionHandler.dialog.message")); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
