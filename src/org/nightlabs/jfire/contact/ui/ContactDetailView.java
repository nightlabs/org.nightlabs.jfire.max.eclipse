package org.nightlabs.jfire.contact.ui;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.notification.NotificationAdapterSWTThreadAsync;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.DataBlockEditorChangedEvent;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.DataBlockEditorChangedListener;
import org.nightlabs.jfire.contact.ui.resource.Messages;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.dao.PropertySetDAO;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;
import org.nightlabs.util.Util;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class ContactDetailView
extends LSDViewPart
{
	public static final String VIEW_ID = ContactDetailView.class.getName();

	private SaveAction saveAction;

	private Display display;
	private ContactDetailComposite contactDetailComposite;
	private NotificationListener contactSelectionListener;

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.part.ControllablePart#createPartContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartContents(Composite parent)
	{
		display = parent.getDisplay();
		contactDetailComposite = new ContactDetailComposite(parent);
		contributeToActionBars();
		contactDetailComposite.addChangeListener(new DataBlockEditorChangedListener() {
			@Override
			public void dataBlockEditorChanged(DataBlockEditorChangedEvent dataBlockEditorChangedEvent) {
				if (saveAction != null && !saveAction.isEnabled())
					saveAction.setEnabled(true);
			}
		});

		contactSelectionListener  = new NotificationAdapterSWTThreadAsync() {
			@Override
			public void notify(NotificationEvent notificationEvent) {
				final PropertySet selectedPerson = contactDetailComposite.getPerson();
				final Object firstSelection = notificationEvent.getFirstSubject();
				if (firstSelection instanceof PropertySetID) {
					if (firstSelection != null) {
						// Check to see if the Person record has been modified before allowing uset to navigate to another
						// record. If so prompt to save changes.
						if (selectedPerson != null && saveAction != null && saveAction.isEnabled()) {
							boolean isSaveChanges = MessageDialog.openQuestion(
									RCPUtil.getActiveShell(),
									Messages.getString("org.nightlabs.jfire.contact.ui.ContactDetailView.askUserSaveChangesDialog.title"), //$NON-NLS-1$
									String.format(Messages.getString("org.nightlabs.jfire.contact.ui.ContactDetailView.askUserSaveChangesDialog.text"), selectedPerson.getDisplayName()) //$NON-NLS-1$
							);

							if (isSaveChanges) {
								final PropertySet person = Util.cloneSerializable(selectedPerson);
								Job job = new Job(Messages.getString("org.nightlabs.jfire.contact.ui.ContactDetailView.savePersonJob.name")) { //$NON-NLS-1$
									@Override
									protected IStatus run(ProgressMonitor monitor) throws Exception {
										person.deflate();
										PropertySetDAO.sharedInstance().storeJDOObject(
												person, false, null, 1,
												monitor
										);
										return Status.OK_STATUS;
									}
								};
								job.setPriority(Job.LONG);
								job.schedule();
							}
							// @Kai: The problem you described in the following is solved by Util.cloneSerializable(...)
							// Something is still not correct here.
							// Problem symptom: (1) User decides NOT to save Record_A. (2) Navigates to other records.
							//                  (3) Some time later comes back to (unsaved) Record_A.
							//                  ==> The modified-unsaved Record_A is displayed, instead of the original Record_A.
							// So: Restore previous (clean) data? Clear changes from cache? Or what??
							// @Kai: No! We need to clone the object! Marco.
							// @Marco: Ok. Kapish.
							saveAction.setEnabled(false);
						}

						contactDetailComposite.setPersonID((PropertySetID)firstSelection);
					}
				}
			}
		};

		SelectionManager.sharedInstance().addNotificationListener(ContactPlugin.ZONE_PROPERTY, Person.class, contactSelectionListener);

		contactDetailComposite.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				SelectionManager.sharedInstance().removeNotificationListener(ContactPlugin.ZONE_PROPERTY, Person.class, contactSelectionListener);
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.login.part.LSDViewPart#setFocus()
	 */
	@Override
	public void setFocus() {
		if (contactDetailComposite != null)
			contactDetailComposite.setFocus();
	}

	/**
	 * Prepares the ActionBar.
	 */
	private void contributeToActionBars() {
		if (saveAction != null)
			return;

		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		saveAction = new SaveAction();
		toolBarManager.add(saveAction);
		toolBarManager.update(true);
	}

	/**
	 * Allows to click on Action to save changes.
	 */
	private class SaveAction extends Action {
		public SaveAction() {
			setId(SaveAction.class.getName());
			setImageDescriptor(SharedImages.SAVE_16x16);
			setToolTipText(Messages.getString("org.nightlabs.jfire.contact.ui.ContactDetailView.SaveAction.toolTipText")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.contact.ui.ContactDetailView.SaveAction.text")); //$NON-NLS-1$

			// By default, this button should always be disabled.
			setEnabled(false);
		}

		@Override
		public void run() {
			final PropertySet selectedPerson = contactDetailComposite.getPerson();
			if (selectedPerson == null)
				return;

			final PropertySet person = Util.cloneSerializable(selectedPerson);
			final PropertySetID personID = (PropertySetID) JDOHelper.getObjectId(person);

			Job job = new Job(Messages.getString("org.nightlabs.jfire.contact.ui.ContactDetailView.savePersonJob.name")) { //$NON-NLS-1$
				@Override
				protected IStatus run(ProgressMonitor _monitor) throws Exception {
					_monitor.beginTask(Messages.getString("org.nightlabs.jfire.contact.ui.ContactDetailView.savePersonJob.name"), 100); //$NON-NLS-1$
					try {
						person.deflate();
						PropertySetDAO.sharedInstance().storeJDOObject(
								person, false, null, 1,
								new SubProgressMonitor(_monitor, 50)
						);
						contactDetailComposite.setPersonID(personID, new SubProgressMonitor(_monitor, 50));
					} finally {
						_monitor.done();
					}
					return Status.OK_STATUS;
				}
			};
			job.setPriority(Job.SHORT);
			job.setUser(true);
			setEnabled(false);
			job.schedule();
		}
	}

	public PropertySet getPerson() {
		return contactDetailComposite.getPerson();
	}
}