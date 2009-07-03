package org.nightlabs.jfire.issuetracking.ui.issuelink.attach;

import org.eclipse.jface.dialogs.MessageDialog;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerParam;
import org.nightlabs.base.ui.exceptionhandler.IExceptionHandler;
import org.nightlabs.base.ui.util.RCPUtil;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class DuplicateIssueLinkExceptionHandler
implements IExceptionHandler
{
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.exceptionhandler.IExceptionHandler#handleException(org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerParam)
	 */
	@Override
	public boolean handleException(ExceptionHandlerParam handlerParam)
	{
		MessageDialog.openError(
				RCPUtil.getActiveShell(),
				"Same issue link already existing",
				"There exists already an issue link with the same issue and issue link type");
		return true;
	}

}
