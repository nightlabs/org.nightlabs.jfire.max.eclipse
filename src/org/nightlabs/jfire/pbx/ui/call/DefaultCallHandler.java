package org.nightlabs.jfire.pbx.ui.call;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.nightlabs.jfire.pbx.Call;
import org.nightlabs.jfire.pbx.PhoneSystemException;
import org.nightlabs.jfire.pbx.dao.PhoneSystemDAO;
import org.nightlabs.progress.ProgressMonitor;

public class DefaultCallHandler implements CallHandler
{
	private String phoneSystemClass;
	private String id;

	@Override
	public void setInitializationData(IConfigurationElement element, String propertyName, Object data) throws CoreException {
		id = element.getAttribute(ATTRIBUTE_ID);
		phoneSystemClass = element.getAttribute(ATTRIBUTE_PHONE_SYSTEM_CLASS);
	}

	@Override
	public void call(Call call, ProgressMonitor monitor) throws PhoneSystemException {
		PhoneSystemDAO.sharedInstance().call(call, monitor);
	}

	public String getId() {
		return id;
	}

	public String getPhoneSystemClass() {
		return phoneSystemClass;
	}
}
