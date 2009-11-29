package org.nightlabs.jfire.contact.ui;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.Editor2PerspectiveRegistry;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.base.ui.person.create.CreatePersonWizard;
import org.nightlabs.jfire.base.ui.person.search.PersonResultTable;
import org.nightlabs.jfire.base.ui.person.search.PersonSearchComposite;
import org.nightlabs.jfire.base.ui.prop.PropertySetTable;
import org.nightlabs.jfire.contact.ui.resource.Messages;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class ContactView
extends LSDViewPart
{
	public static final String VIEW_ID = ContactView.class.getName();

//	private IMemento initMemento = null;

	private AddNewContactAction addNewContactAction;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
//		this.initMemento = memento;
	}

	private PersonSearchComposite searchComposite;
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.part.ControllablePart#createPartContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartContents(Composite parent) {
		//searchComposite = new PersonSearchComposite(parent, SWT.NONE, ""); //$NON-NLS-1$

		// Kai: [10 Nov 2009]
		// Attempting to make resultTable to allow for only single selection.
		// Need to access the style and change it, i.e.
		// searchComposite.getResultTable().getStyle() <-- Can only get the style but cannot change after instantiation.
		//
		// It seems that the only way to change this behaviour is during the construction of the PropertySetTable
		// and ensure that it invokes AbstractTableComposite(Composite parent, int style, boolean initTable, int viewerStyle),
		// so that we can specify the viewerStyle as AbstractTableComposite.DEFAULT_STYLE_SINGLE.
		//
		// Notes:
		// 1. To get to: AbstractTableComposite(Composite parent, int style, boolean initTable, int viewerStyle)
		// -- [noted]
		//
		// 2. PropertySetTable<ProperySetType> extends AbstractTableComposite<ProperySetType>
		//    >> Abstract class has only a single constrcutor: PropertySetTable(Composite parent, int style)
		// -- [added constructor]: PropertySetTable(Composite parent, int style, int viewerStyle)
		//
		// 3. PersonResultTable extends PropertySetTable<Person>
		//    >> Has only a single constructor: PersonResultTable(Composite parent, int style)
		// -- [added constructor]: PersonResultTable(Composite parent, int style, int viewerStyle)
		//
		// 4. To modify: PersonSearchComposite.createResultTable(Composite parent)
		//               >> returns: PropertySetTable<Person> <-- new PersonResultTable(parent, SWT.NONE)
		// -- [Overrode]:
		searchComposite = new PersonSearchComposite(parent, SWT.NONE, "") { //$NON-NLS-1$
			@Override
			protected PropertySetTable<Person> createResultTable(Composite parent) {
				return new PersonResultTable(parent, SWT.NONE, AbstractTableComposite.DEFAULT_STYLE_SINGLE_BORDER);
			}
		};

		Composite buttonBar = searchComposite.getButtonBar();
		final Display display = searchComposite.getDisplay();
		GridLayout gl = new GridLayout();
		XComposite.configureLayout(LayoutMode.LEFT_RIGHT_WRAPPER, gl);
		gl.numColumns = 2;

		// Kai: [10 Nov 2009]
		// Allow to create new Person.
		gl.numColumns++;
		Button createNewButton = new Button(buttonBar, SWT.PUSH);
		createNewButton.setText(Messages.getString("org.nightlabs.jfire.contact.ui.ContactView.createNewButton.text")); //$NON-NLS-1$
		createNewButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		createNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleCreateNewContact();
			}
		});

		buttonBar.setLayout(gl);
		new XComposite(buttonBar, SWT.NONE, LayoutDataMode.GRID_DATA_HORIZONTAL);

		Button searchButton = searchComposite.createSearchButton(buttonBar);

		// Kai: [12 Nov 2009]
		contributeToActionBars();

		searchComposite.getResultTable().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Person person = searchComposite.getResultTable().getFirstSelectedElement();

				PropertySetID personID = (PropertySetID)JDOHelper.getObjectId(person);
				SelectionManager.sharedInstance().notify(
						new NotificationEvent(this, ContactPlugin.ZONE_PROPERTY, personID, Person.class)
				);
			}
		});
		searchComposite.getResultTable().addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				Person person = searchComposite.getResultTable().getFirstSelectedElement();
				PropertySetID personID = (PropertySetID)JDOHelper.getObjectId(person);
				try {
					RCPUtil.openEditor(
							new ContactEditorInput(personID),
							ContactEditor.EDITOR_ID);
				} catch (PartInitException e) {
					throw new RuntimeException(e);
				}
			}
		});

		searchComposite.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				SelectionManager.sharedInstance().notify(
						new NotificationEvent(this, ContactPlugin.ZONE_PROPERTY, null, Person.class)
				);
			}
		});
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
	 * Handles the creation of a new contact.
	 */
	private void handleCreateNewContact() {
		final Shell shell = getSite().getShell();
		final Display display = shell.getDisplay();

		Job job = new Job(Messages.getString("org.nightlabs.jfire.contact.ui.ContactView.openWizardJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				monitor.beginTask(Messages.getString("org.nightlabs.jfire.contact.ui.ContactView.openWizardJob.name"), 100); //$NON-NLS-1$
				try {
					final CreatePersonWizard wizard = new CreatePersonWizard();

					display.asyncExec(new Runnable() {
						public void run() {
							DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(shell, wizard);
							if (dialog.open() == Dialog.OK) {
								//TODO update the result table and select the correct one.
								searchComposite.performSearch();

								PropertySetID personID = (PropertySetID)JDOHelper.getObjectId(wizard.getPerson());

								ContactEditorInput contactEditorInput = new ContactEditorInput(personID);
								try {
									Editor2PerspectiveRegistry.sharedInstance().openEditor(contactEditorInput, ContactEditor.EDITOR_ID);
								} catch (Exception ex) {
									throw new RuntimeException(ex);
								}

								SelectionManager.sharedInstance().notify(
										new NotificationEvent(this, ContactPlugin.ZONE_PROPERTY, personID, Person.class)
								);
							}
						}
					});

					return Status.OK_STATUS;
				} finally {
					monitor.done();
				}
			}
		};
		job.setUser(true);
		job.setPriority(Job.INTERACTIVE);
		job.schedule();
	}


	/**
	 * Prepares the ActionBar.
	 */
	private void contributeToActionBars() {
		if (addNewContactAction != null)
			return; // this method might be called multiple times - e.g. when doing: login, logout & re-login.

		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		addNewContactAction = new AddNewContactAction();
		toolBarManager.add(addNewContactAction);
	}

	/**
	 * Allows to click on Action to add/create a new Contact.
	 */
	protected class AddNewContactAction extends Action {
		public AddNewContactAction() {
			setId(AddNewContactAction.class.getName());
			setImageDescriptor(SharedImages.ADD_16x16);
			setToolTipText(Messages.getString("org.nightlabs.jfire.contact.ui.ContactView.AddNewContactAction.toolTipText")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.contact.ui.ContactView.AddNewContactAction.text")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			handleCreateNewContact();
		}
	}
}