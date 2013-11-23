package org.nightlabs.jfire.pbx.ui.call;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.eclipse.extension.AbstractEPProcessor;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.pbx.Call;
import org.nightlabs.jfire.pbx.NoPhoneSystemAssignedException;
import org.nightlabs.jfire.pbx.PhoneNumberDataFieldCall;
import org.nightlabs.jfire.pbx.PhoneSystem;
import org.nightlabs.jfire.pbx.PhoneSystemException;
import org.nightlabs.jfire.pbx.dao.PhoneSystemDAO;
import org.nightlabs.jfire.pbx.ui.call.selectnumber.SelectPhoneNumberDialog;
import org.nightlabs.jfire.pbx.ui.resource.Messages;
import org.nightlabs.jfire.prop.DataField;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.StructLocal;
import org.nightlabs.jfire.prop.dao.PropertySetDAO;
import org.nightlabs.jfire.prop.dao.StructLocalDAO;
import org.nightlabs.jfire.prop.datafield.PhoneNumberDataField;
import org.nightlabs.jfire.prop.id.DataFieldID;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.jfire.workstation.id.WorkstationID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;
import org.nightlabs.util.Util;

public class CallHandlerRegistry extends AbstractEPProcessor
{
	private static volatile CallHandlerRegistry sharedInstance;

	public static CallHandlerRegistry sharedInstance() {
		if (sharedInstance == null) {
			synchronized(CallHandlerRegistry.class) {
				if (sharedInstance == null) {
					sharedInstance = new CallHandlerRegistry();
					sharedInstance.process();
				}
			}
		}
		return sharedInstance;
	}

	protected CallHandlerRegistry() { }

	@Override
	public String getExtensionPointID() {
		return "org.nightlabs.jfire.pbx.ui.callHandler"; //$NON-NLS-1$
	}

	private Map<String, IConfigurationElement> phoneSystemClass2element = new HashMap<String, IConfigurationElement>();

	@Override
	public void processElement(IExtension extension, IConfigurationElement element) throws Exception
	{
		String phoneSystemClass = element.getAttribute(CallHandler.ATTRIBUTE_PHONE_SYSTEM_CLASS);
		phoneSystemClass2element.put(phoneSystemClass, element);
	}

	public void call(final PropertySetID personID)
	{
		Job job = new Job(Messages.getString("org.nightlabs.jfire.pbx.ui.call.CallHandlerRegistry.callJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				call(personID, monitor);
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.INTERACTIVE);
		job.setUser(true);
		job.schedule();
	}

	public void call(PropertySetID personID, ProgressMonitor monitor) throws PhoneSystemException
	{
		if (Display.getCurrent() != null)
			throw new IllegalStateException("Thread mismatch! This method must not be called on an SWT UI thread!"); //$NON-NLS-1$

		monitor.beginTask(Messages.getString("org.nightlabs.jfire.pbx.ui.call.CallHandlerRegistry.callJob.name"), 100); //$NON-NLS-1$
		try {
			final PhoneSystem phoneSystem = PhoneSystemDAO.sharedInstance().getPhoneSystem(
					(WorkstationID)null,
					new String[] {
							FetchPlan.DEFAULT, PhoneSystem.FETCH_GROUP_NAME, PhoneSystem.FETCH_GROUP_CALLABLE_STRUCT_FIELDS
					},
					1,
					new SubProgressMonitor(monitor, 50)
			);
			if (phoneSystem == null) {
				WorkstationID workstationID = null;

				Login login;
				try {
					login = Login.getLogin();
				} catch (LoginException e) {
					throw new RuntimeException(e);
				}

				if (login.getWorkstationID() != null)
					workstationID = WorkstationID.create(login.getOrganisationID(), login.getWorkstationID());

				throw new NoPhoneSystemAssignedException(workstationID);
			}

			if (phoneSystem.getCallableStructFields().isEmpty()) {
				Display.getDefault().syncExec(new Runnable() { // important to use syncExec because otherwise the shell might be closed before the dialog is opened.
					@Override
					public void run() {
						MessageDialog.openError(
								RCPUtil.getActiveShell(),
								Messages.getString("org.nightlabs.jfire.pbx.ui.call.CallHandlerRegistry.errorDialog[phoneSystemLacksCallableNumber].title"), //$NON-NLS-1$
								String.format(
										Messages.getString("org.nightlabs.jfire.pbx.ui.call.CallHandlerRegistry.errorDialog[phoneSystemLacksCallableNumber].text"), //$NON-NLS-1$
										phoneSystem.getName().getText()
								)
						);
					}
				});
				return;
			}

			// load person, collect phone numbers and (if more than one) ask the user which one to call.
			final PropertySet p = PropertySetDAO.sharedInstance().getPropertySet(
					personID, new String[] { FetchPlan.DEFAULT, PropertySet.FETCH_GROUP_FULL_DATA }, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					new SubProgressMonitor(monitor, 30)
			);

			final PropertySet person = Util.cloneSerializable(p);

			StructLocal structLocal = StructLocalDAO.sharedInstance().getStructLocal(person.getStructLocalObjectID(), new SubProgressMonitor(monitor, 10));
			person.inflate(structLocal);

			final List<PhoneNumberDataField> phoneNumberDataFields = new LinkedList<PhoneNumberDataField>();
			for (DataField dataField : person.getDataFields()) {
				if (dataField.isEmpty())
					continue;

				if (dataField instanceof PhoneNumberDataField && phoneSystem.getCallableStructFields().contains(dataField.getStructField()))
					phoneNumberDataFields.add((PhoneNumberDataField) dataField);
			}

			if (phoneNumberDataFields.isEmpty()) {
				Display.getDefault().syncExec(new Runnable() { // important to use syncExec because otherwise the shell might be closed before the dialog is opened.
					@Override
					public void run() {
						MessageDialog.openError(
								RCPUtil.getActiveShell(),
								Messages.getString("org.nightlabs.jfire.pbx.ui.call.CallHandlerRegistry.errorDialog[noPhoneNumber].title"), //$NON-NLS-1$
								String.format(Messages.getString("org.nightlabs.jfire.pbx.ui.call.CallHandlerRegistry.errorDialog[noPhoneNumber].text"), person.getDisplayName()) //$NON-NLS-1$
						);
					}
				});
				return;
			}

			Collections.sort(phoneNumberDataFields, new Comparator<PhoneNumberDataField>() {
				@Override
				public int compare(PhoneNumberDataField o1, PhoneNumberDataField o2) {
					return o1.getPropRelativePK().compareTo(o2.getPropRelativePK());
				}
			});

			PhoneNumberDataField phoneNumberDataField = null;
			if (phoneNumberDataFields.size() > 1) {
				// We show a nice dialog asking for the phone number to dial.
				// When the user selected a phone number, we assign the field phoneNumberDataField appropriately.
				final PhoneNumberDataField[] tmp = new PhoneNumberDataField[1];

				Display.getDefault().syncExec(new Runnable() { // important to use syncExec because otherwise the shell might be closed before the dialog is opened.
					@Override
					public void run() {
						SelectPhoneNumberDialog dialog = new SelectPhoneNumberDialog(RCPUtil.getActiveShell(), person, phoneNumberDataFields);
						dialog.open();
						tmp[0] = dialog.getSelectedPhoneNumberDataField();
					}
				});

				phoneNumberDataField = tmp[0];
			}
			else
				phoneNumberDataField = phoneNumberDataFields.get(0);

			if (phoneNumberDataField != null) {
				DataFieldID phoneNumberDataFieldID = (DataFieldID) JDOHelper.getObjectId(phoneNumberDataField);
				Call call = new PhoneNumberDataFieldCall(phoneNumberDataFieldID);
				call(phoneSystem, call, new SubProgressMonitor(monitor, 40));
			}

		} finally {
			monitor.done();
		}
	}

	public void call(final Call call)
	{
		Job job = new Job(Messages.getString("org.nightlabs.jfire.pbx.ui.call.CallHandlerRegistry.callJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				PhoneSystem phoneSystem = PhoneSystemDAO.sharedInstance().getPhoneSystem((WorkstationID)null, null, 1, new SubProgressMonitor(monitor, 50));
				if (phoneSystem == null) {
					WorkstationID workstationID = null;

					Login login;
					try {
						login = Login.getLogin();
					} catch (LoginException e) {
						throw new RuntimeException(e);
					}

					if (login.getWorkstationID() != null)
						workstationID = WorkstationID.create(login.getOrganisationID(), login.getWorkstationID());

					throw new NoPhoneSystemAssignedException(workstationID);
				}

				call(phoneSystem, call, monitor);
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.INTERACTIVE);
		job.setUser(true);
		job.schedule();
	}

	public void call(PhoneSystem phoneSystem, Call call, ProgressMonitor monitor) throws PhoneSystemException
	{
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.pbx.ui.call.CallHandlerRegistry.callJob.name"), 100); //$NON-NLS-1$
		try {
			IConfigurationElement configurationElement = null;
			Class<? extends PhoneSystem> clazz = phoneSystem.getClass();
			while (clazz != null) {
				configurationElement = phoneSystemClass2element.get(clazz.getName());
				if (configurationElement != null)
					break;

				if (PhoneSystem.class.isAssignableFrom(clazz.getSuperclass())) {
					@SuppressWarnings("unchecked")
					Class<? extends PhoneSystem> c = (Class<? extends PhoneSystem>) clazz.getSuperclass();
					clazz = c;
				}
				else
					clazz = null;
			}

			if (configurationElement == null)
				throw new NoCallHandlerException("There is no call handler registered for this phone system class: " + phoneSystem.getClass().getName()); //$NON-NLS-1$

			CallHandler callHandler;
			try {
				callHandler = (CallHandler) configurationElement.createExecutableExtension(CallHandler.ATTRIBUTE_CLASS);
			} catch (CoreException e) {
				throw new RuntimeException(e);
			}

			callHandler.call(call, new SubProgressMonitor(monitor, 50));
		} finally {
			monitor.done();
		}
	}
}
