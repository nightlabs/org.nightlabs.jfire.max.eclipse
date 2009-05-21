package org.nightlabs.jfire.issuetracking.ui.issuelink.person;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.person.search.PersonSearchComposite;
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
		personSearchComposite = new PersonSearchComposite(parent, SWT.NONE, "");
		return personSearchComposite;
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