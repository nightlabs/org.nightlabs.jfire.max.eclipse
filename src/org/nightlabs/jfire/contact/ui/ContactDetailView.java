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

	private SaveDetailsChangesAction saveDetailsChangesAction;

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
				if (saveDetailsChangesAction != null && !saveDetailsChangesAction.isEnabled())
					saveDetailsChangesAction.setEnabled(true);
			}
		});

		contactSelectionListener  = new NotificationAdapterSWTThreadAsync() {
			@Override
			public void notify(NotificationEvent notificationEvent) {
				final PropertySet selectedPerson = contactDetailComposite.getPerson();
				final Object firstSelection = notificationEvent.getFirstSubject();
				if (firstSelection instanceof PropertySetID) {
					if (firstSelection != null) {
						// [2009-11-17 Kai]:
						// Check to see if the Person record has been modified before allowing uset to navigate to another
						// record. If so prompt to save changes.
						if (selectedPerson != null && saveDetailsChangesAction != null && saveDetailsChangesAction.isEnabled()) {
							boolean isSaveChanges = MessageDialog.openQuestion(
									RCPUtil.getActiveShell(),
									"Record modified",
									String.format("Changes have been made to person '%s'. Save changes?", selectedPerson.getDisplayName())
							);

							if (isSaveChanges) {
								final PropertySet person = Util.cloneSerializable(selectedPerson);
								Job job = new Job("Saving person") {
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
							saveDetailsChangesAction.setEnabled(false);
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
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		saveDetailsChangesAction = new SaveDetailsChangesAction();
		toolBarManager.add(saveDetailsChangesAction);
		toolBarManager.update(true);
	}

	/**
	 * Allows to click on Action to save changes.
	 */
	protected class SaveDetailsChangesAction extends Action {
		public SaveDetailsChangesAction() {
			setId(SaveDetailsChangesAction.class.getName());
			setImageDescriptor(SharedImages.SAVE_16x16);
			setToolTipText("Save changes...");
			setText("Save changes...");

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

			Job job = new Job("Saving contact") {
				@Override
				protected IStatus run(ProgressMonitor _monitor) throws Exception {
					_monitor.beginTask("Saving contact", 100);
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