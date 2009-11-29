package org.nightlabs.jfire.contact.ui;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.prop.edit.ValidationResultHandler;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.BlockBasedEditor;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.DataBlockEditorChangedEvent;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.DataBlockEditorChangedListener;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.FullDataBlockCoverageComposite;
import org.nightlabs.jfire.contact.ui.resource.Messages;
import org.nightlabs.jfire.jdo.notification.DirtyObjectID;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.StructLocal;
import org.nightlabs.jfire.prop.dao.PropertySetDAO;
import org.nightlabs.jfire.prop.dao.StructLocalDAO;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.jfire.prop.validation.ValidationResult;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;
import org.nightlabs.util.Util;

public class ContactDetailComposite extends XComposite
{
	private Display display;
	private PropertySet selectedPerson;
	private FullDataBlockCoverageComposite fullDataBlockCoverageComposite;

	private NotificationListener notificationListenerPersonChanged = new NotificationAdapterJob(Messages.getString("org.nightlabs.jfire.contact.ui.ContactDetailComposite.notificationListenerPersonChanged.jobName")) { //$NON-NLS-1$
		@Override
		public void notify(NotificationEvent notificationEvent) {
			PropertySetID selectedPersonID = (PropertySetID) JDOHelper.getObjectId(selectedPerson);
			if (selectedPersonID == null)
				return;

			DirtyObjectID dirtyObjectID = (DirtyObjectID) notificationEvent.getFirstSubject();
			Object objectID = dirtyObjectID.getObjectID();
			if (selectedPersonID.equals(objectID)) {
				setPersonID(selectedPersonID, getProgressMonitor());
			}
		}
	};

	public ContactDetailComposite(Composite parent) {
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		display = parent.getDisplay();

		GridLayout layout = new GridLayout(1, true);
		this.setLayout(layout);

		JDOLifecycleManager.sharedInstance().addNotificationListener(Person.class, notificationListenerPersonChanged);

		this.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				JDOLifecycleManager.sharedInstance().removeNotificationListener(Person.class, notificationListenerPersonChanged);
			}
		});
	}

	private void createOrUpdateFullDataBlockCoverageComposite()
	{
		if (fullDataBlockCoverageComposite == null) {
			ValidationResultHandler resultManager = new ValidationResultHandler() {
				@Override
				public void handleValidationResult(ValidationResult validationResult) {

				}
			};

			fullDataBlockCoverageComposite = new FullDataBlockCoverageComposite(
					this,
					SWT.NONE,
					selectedPerson,
					null,
					resultManager
			)
			{
				@Override
				protected BlockBasedEditor createBlockBasedEditor() {
					return new BlockBasedEditor(false);
				}
			};

			fullDataBlockCoverageComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

			// Check to see if contents of the view is modified.
			fullDataBlockCoverageComposite.addChangeListener(new DataBlockEditorChangedListener() {
				@Override
				public void dataBlockEditorChanged(DataBlockEditorChangedEvent dataBlockEditorChangedEvent) {
					for (Object l : dataBlockEditorChangedListeners.getListeners())
						((DataBlockEditorChangedListener)l).dataBlockEditorChanged(dataBlockEditorChangedEvent);
				}
			});
		}
		else {
			fullDataBlockCoverageComposite.refresh(selectedPerson);
		}

		this.layout(true);
	}

	private ListenerList dataBlockEditorChangedListeners = new ListenerList();

	public void addChangeListener(DataBlockEditorChangedListener listener) {
		dataBlockEditorChangedListeners.add(listener);
	}

	public void removeChangeListener(DataBlockEditorChangedListener listener) {
		dataBlockEditorChangedListeners.remove(listener);
	}

	public void setPersonID(final PropertySetID personID)
	{
		Job job = new Job(Messages.getString("org.nightlabs.jfire.contact.ui.ContactDetailComposite.loadSelectedPersonJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				setPersonID(personID, monitor);
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule();
	}

	public void setPersonID(PropertySetID personID, ProgressMonitor monitor)
	{
		if (Display.getCurrent() != null)
			throw new IllegalStateException("This method must be called in a Job!"); //$NON-NLS-1$

		monitor.beginTask(Messages.getString("org.nightlabs.jfire.contact.ui.ContactDetailComposite.loadSelectedPersonJob.name"), 100); //$NON-NLS-1$
		try {
			// step 1
			PropertySet p = personID == null ? null : PropertySetDAO.sharedInstance().getPropertySet(
					personID,
					new String[] {
							FetchPlan.DEFAULT, PropertySet.FETCH_GROUP_FULL_DATA
					},
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					new SubProgressMonitor(monitor, 70)
			);

			// step 2
			p = Util.cloneSerializable(p); // This cloning is essentially necessary, because we otherwise edit the object that is right now in the cache (and shared)!
			monitor.worked(5);

			// step 3
			StructLocal personStruct = p == null ? null : StructLocalDAO.sharedInstance().getStructLocal(
					p.getStructLocalObjectID(),
					new SubProgressMonitor(monitor, 20)
			);

			// step 4
			if (p != null)
				p.inflate(personStruct);

			monitor.worked(5);

			// update UI on SWT thread
			final PropertySet person = p;
			display.asyncExec(
					new Runnable() {
						@Override
						public void run() {
							selectedPerson = person;
							createOrUpdateFullDataBlockCoverageComposite();
						}
					}
			);
		} finally {
			monitor.done();
		}
	}

	public PropertySet getPerson() {
		if (Display.getCurrent() != display)
			throw new IllegalStateException("Thread mismatch! This method must be called on the SWT UI thread!"); //$NON-NLS-1$

		if (fullDataBlockCoverageComposite != null && !fullDataBlockCoverageComposite.isDisposed())
			fullDataBlockCoverageComposite.updatePropertySet();

		return selectedPerson;
	}

//	public void setPerson(PropertySet person) {
//		if (Display.getCurrent() != display)
//			throw new IllegalStateException("Thread mismatch! This method must be called on the SWT UI thread!");
//
//		this.selectedPerson = person;
//
//		if (fullDataBlockCoverageComposite != null && !fullDataBlockCoverageComposite.isDisposed())
//			fullDataBlockCoverageComposite.refresh(person);
//	}
}
