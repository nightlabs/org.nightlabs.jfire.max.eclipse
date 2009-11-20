package org.nightlabs.jfire.contact.ui;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.base.ui.prop.edit.ValidationResultHandler;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.BlockBasedEditor;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.DataBlockEditorChangedEvent;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.DataBlockEditorChangedListener;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.FullDataBlockCoverageComposite;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.StructLocal;
import org.nightlabs.jfire.prop.dao.PropertySetDAO;
import org.nightlabs.jfire.prop.dao.StructLocalDAO;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.jfire.prop.validation.ValidationResult;
import org.nightlabs.notification.NotificationAdapterCallerThread;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class ContactDetailView
extends LSDViewPart
{
	public static final String VIEW_ID = ContactDetailView.class.getName();

	private IMemento initMemento = null;
	private PropertySet selectedPerson;
	private FullDataBlockCoverageComposite fullDataBlockCoverageComposite;
	private NotificationListener contactSelectionListener;

	private SaveDetailsChangesAction saveDetailsChangesAction;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		this.initMemento = memento;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.part.ControllablePart#createPartContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartContents(Composite parent)
	{
		final XComposite mainComposite = new XComposite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		mainComposite.setLayout(layout);

		try {
			contactSelectionListener  = new NotificationAdapterCallerThread(){
				public void notify(NotificationEvent notificationEvent) {
					Object firstSelection = notificationEvent.getFirstSubject();
					if (firstSelection instanceof PropertySetID) {
						ValidationResultHandler resultManager = new ValidationResultHandler() {
							@Override
							public void handleValidationResult(ValidationResult validationResult) {

							}
						};


						if (firstSelection != null) {
							// [2009-11-17 Kai]:
							// Check to see if the Person record has been modified before allowing uset to navigate to another
							// record. If so prompt to save changes.
							if (saveDetailsChangesAction != null && saveDetailsChangesAction.isEnabled()) {
								boolean isSaveChanges = MessageDialog.openQuestion(mainComposite.getShell(), "Record modified",
										"Changes have been made to Person record '" + selectedPerson.getDisplayName()
										+ "'. Save changes?");

								if (isSaveChanges) {
									selectedPerson.deflate();
									selectedPerson = PropertySetDAO.sharedInstance().storeJDOObject(
											selectedPerson, true, new String[] { FetchPlan.DEFAULT, PropertySet.FETCH_GROUP_FULL_DATA}, 1,
											new NullProgressMonitor()
									);
								}
//								else TODO: Discard all changes; i.e. keep the original.
								// Something is still not correct here.
								// Problem symptom: (1) User decides NOT to save Record_A. (2) Navigates to other records.
								//                  (3) Some time later comes back to (unsaved) Record_A.
								//                  ==> The modified-unsaved Record_A is displayed, instead of the original Record_A.
								// So: Restore previous (clean) data? Clear changes from cache? Or what??

								// Done.
								saveDetailsChangesAction.setEnabled(false);
							}


							selectedPerson = PropertySetDAO.sharedInstance().getPropertySet((PropertySetID)firstSelection, new String[] { FetchPlan.DEFAULT, PropertySet.FETCH_GROUP_FULL_DATA}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
							StructLocal personStruct = StructLocalDAO.sharedInstance().getStructLocal(
									selectedPerson.getStructLocalObjectID(),
									new NullProgressMonitor()
							);
							selectedPerson.inflate(personStruct);
						}

						if (fullDataBlockCoverageComposite == null) {
							fullDataBlockCoverageComposite = new FullDataBlockCoverageComposite(
									mainComposite,
									SWT.NONE,
									selectedPerson,
									null,
									resultManager) {
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
									if (saveDetailsChangesAction != null && !saveDetailsChangesAction.isEnabled())
										saveDetailsChangesAction.setEnabled(true);
								}
							});
						}
						else {
							fullDataBlockCoverageComposite.refresh(selectedPerson);
						}
					}
				}
			};

			// Kai: [10 Nov 2009]
			contributeToActionBars();

			SelectionManager.sharedInstance().addNotificationListener(ContactPlugin.ZONE_PROPERTY, Person.class, contactSelectionListener);

			mainComposite.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					SelectionManager.sharedInstance().removeNotificationListener(ContactPlugin.ZONE_PROPERTY, Person.class, contactSelectionListener);
				}
			});
		}
		catch (Exception x) {
			ExceptionHandlerRegistry.asyncHandleException(x);
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.login.part.LSDViewPart#setFocus()
	 */
	@Override
	public void setFocus() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
	}


	/**
	 * Prepares the ActionBar.
	 */
	private void contributeToActionBars() {
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		saveDetailsChangesAction = new SaveDetailsChangesAction();
		toolBarManager.add(saveDetailsChangesAction);
		saveDetailsChangesAction.setEnabled(false);
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
			Job job = new Job("Saving........") {
				@Override
				protected IStatus run(ProgressMonitor _monitor) throws Exception {
					_monitor.beginTask("Begin...", 100);
					try {
						selectedPerson.deflate();
						selectedPerson = PropertySetDAO.sharedInstance().storeJDOObject(
								selectedPerson, true, new String[] { FetchPlan.DEFAULT, PropertySet.FETCH_GROUP_FULL_DATA}, 1,
								new SubProgressMonitor(_monitor, 2)
						);

						StructLocal personStruct = StructLocalDAO.sharedInstance().getStructLocal(
								selectedPerson.getStructLocalObjectID(),
								new NullProgressMonitor()
						);
						selectedPerson.inflate(personStruct);
						fullDataBlockCoverageComposite.refresh(selectedPerson);
					} finally {
						_monitor.done();
					}
					return Status.OK_STATUS;
				}
			};
			job.setPriority(Job.SHORT);
			job.schedule();

			setEnabled(false);
		}
	}
}