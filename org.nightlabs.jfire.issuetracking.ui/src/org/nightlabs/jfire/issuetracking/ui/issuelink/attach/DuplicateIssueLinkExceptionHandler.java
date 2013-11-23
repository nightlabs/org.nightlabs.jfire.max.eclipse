package org.nightlabs.jfire.issuetracking.ui.issuelink.attach;

import org.eclipse.jface.dialogs.MessageDialog;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerParam;
import org.nightlabs.base.ui.exceptionhandler.IExceptionHandler;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

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
				Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.attach.DuplicateIssueLinkExceptionHandler.title"), //$NON-NLS-1$
				Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.attach.DuplicateIssueLinkExceptionHandler.message")); //$NON-NLS-1$
		return true;
	}

}
