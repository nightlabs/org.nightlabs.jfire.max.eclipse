package org.nightlabs.jfire.contact.ui;

import javax.jdo.FetchPlan;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.base.ui.person.edit.blockbased.PersonBlockBasedEditor;
import org.nightlabs.jfire.base.ui.prop.edit.ValidationResultHandler;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.BlockBasedEditor;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.FullDataBlockCoverageComposite;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.person.PersonStruct;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.StructLocal;
import org.nightlabs.jfire.prop.dao.PropertySetDAO;
import org.nightlabs.jfire.prop.dao.StructLocalDAO;
import org.nightlabs.jfire.prop.datafield.TextDataField;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.jfire.prop.validation.ValidationResult;
import org.nightlabs.notification.NotificationAdapterCallerThread;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class ContactDetailView
extends LSDViewPart
{
	public static final String VIEW_ID = ContactDetailView.class.getName();

	private IMemento initMemento = null;
	private FormToolkit toolkit;
	private ScrolledForm scrolledForm;
	private Composite parent;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		this.initMemento = memento;
	}

	private Composite body;
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.part.ControllablePart#createPartContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartContents(Composite parent)
	{
		this.parent = parent;
		toolkit = new FormToolkit(parent.getDisplay());
		scrolledForm = toolkit.createScrolledForm(parent);
		scrolledForm.setText("No contact selected");

		body = scrolledForm.getBody();
		GridLayout layout = new GridLayout(1, true);
		body.setLayout(layout);

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
							person = PropertySetDAO.sharedInstance().getPropertySet((PropertySetID)firstSelection, new String[] { FetchPlan.DEFAULT, PropertySet.FETCH_GROUP_FULL_DATA}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
							StructLocal personStruct = StructLocalDAO.sharedInstance().getStructLocal(
									person.getStructLocalObjectID(),
									new NullProgressMonitor()
							);
							person.inflate(personStruct);
						}

						try {
							scrolledForm.setText(((TextDataField)person.getDataField(PersonStruct.PERSONALDATA_NAME)).getText());
						} catch (Exception e) {
							throw new RuntimeException(e);
						}

						if (fullDataBlockCoverageComposite == null) {
							fullDataBlockCoverageComposite = new FullDataBlockCoverageComposite(
									body,
									SWT.NONE,
									person,
									null,
									resultManager) {
								@Override
								protected BlockBasedEditor createBlockBasedEditor() {
									return new PersonBlockBasedEditor();
								}
							};

							fullDataBlockCoverageComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
						}
						else {
							fullDataBlockCoverageComposite.refresh(person);
						}
						scrolledForm.reflow(true);
					}
				}
			};

			SelectionManager.sharedInstance().addNotificationListener(ContactPlugin.ZONE_PROPERTY, Person.class, contactSelectionListener);

			scrolledForm.addDisposeListener(new DisposeListener() {
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

	private PropertySet person;
	private FullDataBlockCoverageComposite fullDataBlockCoverageComposite;
	private NotificationListener contactSelectionListener;
}