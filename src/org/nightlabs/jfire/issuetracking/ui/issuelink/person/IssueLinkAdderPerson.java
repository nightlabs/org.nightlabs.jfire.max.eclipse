package org.nightlabs.jfire.issuetracking.ui.issuelink.person;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.person.search.PersonSearchComposite;
import org.nightlabs.jfire.base.ui.resource.Messages;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkAdder;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.progress.ProgressMonitor;

/**
 * 
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueLinkAdderPerson 
extends AbstractIssueLinkAdder 
{
	private PersonSearchComposite personSearchComposite;
	/**
	 * Constructs an issue-issue link adder.
	 */
	public IssueLinkAdderPerson() {
	}
	
	@Override
	protected Composite doCreateComposite(Composite parent) {
		personSearchComposite = new PersonSearchComposite(parent, SWT.NONE, ""); //$NON-NLS-1$
		personSearchComposite.getResultTable().addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent evt) {
				notifyIssueLinkDoubleClickListeners();
			}
		});
		personSearchComposite.getResultTable().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				fireSelectionChangedEvent();
			}
		});
		Composite buttonBar = personSearchComposite.getButtonBar();
		createSearchButton(buttonBar);
		return personSearchComposite;
	}

	public Button createSearchButton(Composite parent) {
		Button searchButton = new Button(parent, SWT.PUSH);
		searchButton.setText(Messages.getString("org.nightlabs.jfire.base.ui.prop.PropertySetSearchComposite.searchButton.text")); //$NON-NLS-1$
		searchButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		searchButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doSearch();
			}
		});
		return searchButton;
	}

	@Override
	protected void doSearch() {
		personSearchComposite.performSearch();
	}
	
	/**
	 * 
	 */
	public Set<ObjectID> getLinkedObjectIDs() {
		Collection<Person> elements = personSearchComposite.getResultTable().getSelectedElements();
		return NLJDOHelper.getObjectIDSet(elements);
	}

	/**
	 * 
	 * @return
	 */
	public boolean isComplete() {
		if (personSearchComposite == null)
			return false;
		return !personSearchComposite.getResultTable().getSelectedElements().isEmpty();
	}

	@Override
	public Set<IssueLink> createIssueLinks(
			Issue issue,
			IssueLinkType issueLinkType,
			ProgressMonitor monitor)
	{
		Set<IssueLink> issueLinks = new HashSet<IssueLink>();
		for (Person linkedPerson : personSearchComposite.getResultTable().getSelectedElements()) {
			issueLinks.add(
					issue.createIssueLink(issueLinkType, linkedPerson));
		}
		return issueLinks;
	}
}