package org.nightlabs.jfire.contact.ui;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.base.ui.person.search.PersonSearchComposite;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.notification.NotificationEvent;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class ContactView
extends LSDViewPart
{
	public static final String VIEW_ID = ContactView.class.getName();

	private IMemento initMemento = null;
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		this.initMemento = memento;
	}

	private PersonSearchComposite searchComposite;
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.part.ControllablePart#createPartContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartContents(Composite parent)
	{
		searchComposite = new PersonSearchComposite(parent, SWT.NONE, "");
		Composite buttonBar = searchComposite.getButtonBar();
		final Display display = searchComposite.getDisplay();
		GridLayout gl = new GridLayout();
		XComposite.configureLayout(LayoutMode.LEFT_RIGHT_WRAPPER, gl);
		gl.numColumns = 2;

		buttonBar.setLayout(gl);
		new XComposite(buttonBar, SWT.NONE, LayoutDataMode.GRID_DATA_HORIZONTAL);

		Button searchButton = searchComposite.createSearchButton(buttonBar);

		searchComposite.getResultTable().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Person person = searchComposite.getResultTable().getFirstSelectedElement();

				//				IssueDescriptionView issuePropertyView = (IssueDescriptionView)RCPUtil.findView(IssueDescriptionView.VIEW_ID);
				//				IssueLinkView issueLinkView = (IssueLinkView)RCPUtil.findView(IssueLinkView.VIEW_ID);
				//				IssueHistoryView issueHistoryView = (IssueHistoryView)RCPUtil.findView(IssueHistoryView.VIEW_ID);
				//
				//				if (issuePropertyView != null) issuePropertyView.setIssue(issue);
				//				if (issueLinkView != null) issueLinkView.setIssue(issue);
				//				if (issueHistoryView != null) issueHistoryView.setIssue(issue);

				PropertySetID personID = (PropertySetID)JDOHelper.getObjectId(person);
				SelectionManager.sharedInstance().notify(
						new NotificationEvent(this, ContactPlugin.ZONE_PROPERTY, personID, Person.class)
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
}