package org.nightlabs.jfire.pbx.ui.call;

import org.eclipse.core.runtime.IExecutableExtension;
import org.nightlabs.jfire.pbx.Call;
import org.nightlabs.jfire.pbx.PhoneSystemException;
import org.nightlabs.progress.ProgressMonitor;

public interface CallHandler extends IExecutableExtension
{
	public static final String ATTRIBUTE_ID = "id";
	public static final String ATTRIBUTE_CLASS = "class";
	public static final String ATTRIBUTE_PHONE_SYSTEM_CLASS = "phoneSystemClass";

	public void call(Call call, ProgressMonitor monitor) throws PhoneSystemException;
}
