package org.nightlabs.jfire.personrelation.issuetracking.trade.ui.view;

import javax.jdo.JDOHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.nightlabs.base.ui.notification.NotificationAdapterSWTThreadAsync;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.personrelation.issuetracking.trade.ui.PersonRelationDetailsComposite;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntityPersonEditor;
import org.nightlabs.jfire.trade.ui.legalentity.view.AnonymousLegalEntityComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;

/**
 * @author khaireel <!-- khaireel [AT] nightlabs [DOT] de -->
 */
public class PersonRelationDetailsView extends LSDViewPart
{
	public static final String VIEW_ID = PersonRelationDetailsView.class.getName();

	// Quick refs.
	private PersonRelationDetailsComposite personRelationDetailsComposite;
	private NotificationListener personSelectionListener;

	// The configurable(?) editor section. Similar to the one found in LegalEntitySelectionComposite.
	private TabFolder detailsFolder;
	private Section editorSection;

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.part.ControllablePart#createPartContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartContents(Composite parent) {
		detailsFolder = new TabFolder(parent, SWT.NONE);
		detailsFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		// TabItem 1: Quick-details: The expected configurable customer-view section.
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		editorSection = toolkit.createSection(detailsFolder, ExpandableComposite.TITLE_BAR);
//		editorSection.setText("RESERVED the configurable quick-details customer-view/section");
		editorSection.setText(org.nightlabs.jfire.personrelation.issuetracking.trade.ui.resource.Messages.getString("org.nightlabs.jfire.personrelation.issuetracking.trade.ui.PersonRelationDetailsView.editorSection.text")); //$NON-NLS-1$
		editorSection.setLayoutData(new GridData(GridData.FILL_BOTH));
		editorSection.setLayout(new GridLayout());

		TabItem quickDetails = new TabItem(detailsFolder, SWT.NONE);
		quickDetails.setText(org.nightlabs.jfire.personrelation.issuetracking.trade.ui.resource.Messages.getString("org.nightlabs.jfire.personrelation.issuetracking.trade.ui.PersonRelationDetailsView.tab.quickDetails.text")); //$NON-NLS-1$
		quickDetails.setControl(editorSection);


		// TabItem 2: Full details.
		TabItem fullDetails = new TabItem(detailsFolder, SWT.NONE);
		fullDetails.setText(org.nightlabs.jfire.personrelation.issuetracking.trade.ui.resource.Messages.getString("org.nightlabs.jfire.personrelation.issuetracking.trade.ui.PersonRelationDetailsView.tab.fullDetails.text")); //$NON-NLS-1$
		fullDetails.setControl(new PersonRelationDetailsComposite(detailsFolder));

		wrapper = new Wrapper();

		// Listeners baby, listenersssss.....
		personRelationDetailsComposite = (PersonRelationDetailsComposite)fullDetails.getControl();
		personSelectionListener = new NotificationAdapterSWTThreadAsync() {
			@Override
			public void notify(NotificationEvent notificationEvent) {
				final Object subject = notificationEvent.getFirstSubject();
				if (subject != null) {
					if (subject instanceof AnchorID)
						personRelationDetailsComposite.setLegalEntityID((AnchorID)subject);

					else if (subject instanceof PropertySetID)
						personRelationDetailsComposite.setPersonID((PropertySetID)subject);

					else if (subject instanceof Person) {
						Person person = (Person)subject;
						if (person.getDisplayName().equals("Anonymous")) //$NON-NLS-1$
							wrapper.setAnonymousVisualisation();
						else
							wrapper.setLegalEntityVisualisation(person);
					}
				}
			}
		};

		SelectionManager.sharedInstance().addNotificationListener(TradePlugin.ZONE_SALE, LegalEntity.class, personSelectionListener);
		SelectionManager.sharedInstance().addNotificationListener(TradePlugin.ZONE_SALE, Person.class, personSelectionListener); // Act as filter when selecting from the PersonsRelationTree.


		// ... and finally, the disposers.
		personRelationDetailsComposite.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				SelectionManager.sharedInstance().removeNotificationListener(TradePlugin.ZONE_SALE, LegalEntity.class, personSelectionListener);
				SelectionManager.sharedInstance().removeNotificationListener(TradePlugin.ZONE_SALE, Person.class, personSelectionListener);
			}
		});
	}

	public PropertySetID getPersonID()
	{
		return (PropertySetID) JDOHelper.getObjectId(personRelationDetailsComposite.getPerson());
	}

	private Wrapper wrapper;

	private class Wrapper {
		private Control leEditorControl;
		private LegalEntityPersonEditor leEditor;
		private AnonymousLegalEntityComposite anonymousLegalEntityComposite;

		protected void setAnonymousVisualisation() {
			if (leEditorControl != null) {
				leEditor.disposeControl();
				//			leEditorControl.dispose();
				leEditorControl = null;
			}
			if (anonymousLegalEntityComposite == null)
				anonymousLegalEntityComposite = new AnonymousLegalEntityComposite(editorSection, SWT.NONE);
			editorSection.setClient(anonymousLegalEntityComposite);
			editorSection.setText(Messages.getString("org.nightlabs.jfire.trade.ui.legalentity.view.LegalEntitySelectionComposite.editorSection.anonymous")); //$NON-NLS-1$
			detailsFolder.layout(true, true);
			detailsFolder.redraw();
		}

		protected void setLegalEntityVisualisation(Person person) {
			// Minor change from the original codes:
			// Instead of using the LegalEntity, we directly use the Person reference.
			if (anonymousLegalEntityComposite != null) {
				anonymousLegalEntityComposite.dispose();
				anonymousLegalEntityComposite = null;
			}
			if (leEditor == null)
				leEditor = new LegalEntityPersonEditor();
			if (leEditorControl == null)
				leEditorControl = leEditor.createControl(editorSection, false);
			if (person != null) {
				leEditor.setPropertySet(person);
				leEditor.refreshControl();
			}
			editorSection.setClient(leEditorControl);
			String displayName = person.getDisplayName();
			if (displayName == null)
				displayName = ""; //$NON-NLS-1$
			editorSection.setText(displayName);
			detailsFolder.layout(true, true);
			detailsFolder.redraw();
		}

	}
}
