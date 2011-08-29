package org.nightlabs.jfire.personrelation.issuetracking.trade.ui.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueComment;
import org.nightlabs.jfire.issue.IssueDescription;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.dao.IssueCommentDAO;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.dao.IssueLinkDAO;
import org.nightlabs.jfire.issue.id.IssueCommentID;
import org.nightlabs.jfire.issue.id.IssueDescriptionID;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issue.id.IssueLinkID;
import org.nightlabs.jfire.jdo.notification.TreeNodeMultiParentResolver;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.personrelation.dao.PersonRelationDAO;
import org.nightlabs.jfire.personrelation.id.PersonRelationID;
import org.nightlabs.jfire.personrelation.issuetracking.PersonRelationIssueParentResolver;
import org.nightlabs.jfire.personrelation.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.jfire.personrelation.ui.tree.AbstractPersonRelationTreeControllerDelegate;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeNode;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

public class IssuePersonRelationTreeControllerDelegate extends AbstractPersonRelationTreeControllerDelegate
{
	private static final Logger logger = Logger.getLogger(IssuePersonRelationTreeControllerDelegate.class);

	public static final String[] FETCH_GROUPS_ISSUE_LINK = {
		FetchPlan.DEFAULT,
		IssueLink.FETCH_GROUP_LINKED_OBJECT_CLASS,
		IssueLink.FETCH_GROUP_ISSUE,
		Issue.FETCH_GROUP_SUBJECT,
	};

	public static final String[] FETCH_GROUPS_ISSUE_COMMENT = {
		FetchPlan.DEFAULT,
		IssueComment.FETCH_GROUP_USER,
		IssueComment.FETCH_GROUP_ISSUE_ID, // used for the TreeNodeMultiParentResolver
	};

	public static final String[] FETCH_GROUPS_ISSUE_DESCRIPTION_ISSUE = {
		FetchPlan.DEFAULT,
		Issue.FETCH_GROUP_DESCRIPTION,
	};

	@Override
	public Map<ObjectID, Long> retrieveChildCount(Set<? extends PersonRelationTreeNode> parentNodes, Set<ObjectID> parentIDs, ProgressMonitor monitor) {
		if (logger.isDebugEnabled()) {
			logger.debug("retrieveChildCount: entered with " + parentIDs.size() + " parentIDs."); //$NON-NLS-1$ //$NON-NLS-2$
			if (logger.isTraceEnabled()) {
				for (ObjectID parentID : parentIDs) {
					logger.trace("retrieveChildCount:   * " + parentID); //$NON-NLS-1$
				}
			}
		}

		monitor.beginTask(Messages.getString("org.nightlabs.jfire.personrelation.issuetracking.trade.ui.tree.IssuePersonRelationTreeControllerDelegate.task.loadingLinkedIssueCountsForPersons.name"), 150); //$NON-NLS-1$
		try {
			Set<PropertySetID> personIDs = null;
			Set<PersonRelationID> personRelationIDs = null;
			Set<IssueLinkID> issueLinkIDs = null;

			Map<ObjectID, Long> result = new HashMap<ObjectID, Long>(parentIDs.size());

			for (ObjectID objectID : parentIDs) {
				if (objectID instanceof PropertySetID) {
					if (personIDs == null)
						personIDs = new HashSet<PropertySetID>();

					personIDs.add((PropertySetID) objectID);
				}
				else if (objectID instanceof PersonRelationID) {
					if (personRelationIDs == null)
						personRelationIDs = new HashSet<PersonRelationID>();

					personRelationIDs.add((PersonRelationID) objectID);
				}
				else if (objectID instanceof IssueLinkID) {
					if (issueLinkIDs == null)
						issueLinkIDs = new HashSet<IssueLinkID>();

					issueLinkIDs.add((IssueLinkID) objectID);
				}
				else if (objectID instanceof IssueDescriptionID) {
					result.put(objectID, 0L);
				}
				else if (objectID instanceof IssueCommentID) {
					result.put(objectID, 0L);
				}
			}

			if (issueLinkIDs != null) {
				Map<IssueLinkID, Long> counts = IssueCommentDAO.sharedInstance().getIssueCommentCountsOfIssueOfIssueLinks(issueLinkIDs, new SubProgressMonitor(monitor, 50));
				for (Map.Entry<IssueLinkID, Long> me : counts.entrySet()) {
					result.put(me.getKey(), me.getValue() + 1); // add 1 for the description
				}
			}
			else
				monitor.worked(50);

			if (personIDs != null) {
				Map<ObjectID, Long> issueLinkCounts = IssueLinkDAO.sharedInstance().getIssueLinkCounts(personIDs, new SubProgressMonitor(monitor, 50));
				result.putAll(issueLinkCounts);
			}
			else
				monitor.worked(50);

			personIDs = null; // not used below => give the gc the possibility to collect it.

			if (personRelationIDs != null) {
				Set<PropertySetID> pIDs = new HashSet<PropertySetID>();

				// TODO maybe we should start a backend project to optimize this?! Marco.
				Collection<? extends PersonRelation> personRelations = PersonRelationDAO.sharedInstance().getPersonRelations(
						personRelationIDs,
						new String[] { FetchPlan.DEFAULT, PersonRelation.FETCH_GROUP_TO_ID },
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new SubProgressMonitor(monitor, 50)
				);

				Map<PropertySetID, List<PersonRelationID>> personID2personRelationIDs = new HashMap<PropertySetID, List<PersonRelationID>>();
				for (PersonRelation personRelation : personRelations) {
					PersonRelationID personRelationID = (PersonRelationID) JDOHelper.getObjectId(personRelation);
					PropertySetID personID = personRelation.getToID();
					pIDs.add(personID);

					List<PersonRelationID> l = personID2personRelationIDs.get(personID);
					if (l == null) {
						l = new ArrayList<PersonRelationID>();
						personID2personRelationIDs.put(personID, l);
					}
					l.add(personRelationID);
				}

				Map<ObjectID, Long> issueLinkCounts = IssueLinkDAO.sharedInstance().getIssueLinkCounts(pIDs, new SubProgressMonitor(monitor, 50));
				for (Map.Entry<ObjectID, Long> me : issueLinkCounts.entrySet()) {
					PropertySetID personID = (PropertySetID) me.getKey();
					List<PersonRelationID> prIDs = personID2personRelationIDs.get(personID);
					if (prIDs == null)
						throw new IllegalStateException("personID2personRelationIDs.get(personID) returned null! " + personID); //$NON-NLS-1$

					for (PersonRelationID personRelationID : prIDs) {
						result.put(personRelationID, me.getValue());
					}
				}
			}
			else
				monitor.worked(50);


			return result;
		} finally {
			monitor.done();
		}
	}

	@Override
	public Collection<? extends ObjectID> retrieveChildObjectIDs(PersonRelationTreeNode parentNode, ProgressMonitor monitor) {
		if (logger.isDebugEnabled())
			logger.debug("retrieveChildObjectIDs: entered for: " + parentNode != null ? parentNode.getJdoObjectID() : "null"); //$NON-NLS-1$

		monitor.beginTask(Messages.getString("org.nightlabs.jfire.personrelation.issuetracking.trade.ui.tree.IssuePersonRelationTreeControllerDelegate.task.loadingLinkedIssuesForPerson.name"), 100); //$NON-NLS-1$
		try {
			PropertySetID personID = null;
			if (parentID instanceof PropertySetID) {
				personID = (PropertySetID) parentID;
				monitor.worked(50);
			}
			else if (parentID instanceof PersonRelationID) {
				PersonRelationID personRelationID = (PersonRelationID) parentID;
				PersonRelation personRelation = PersonRelationDAO.sharedInstance().getPersonRelation(
						personRelationID,
						new String[] { FetchPlan.DEFAULT, PersonRelation.FETCH_GROUP_TO_ID },
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new SubProgressMonitor(monitor, 50)
				);
				personID = personRelation.getToID();
			}
			else if (parentID instanceof IssueLinkID) {
				IssueLinkID issueLinkID = (IssueLinkID) parentID;
				List<IssueCommentID> issueCommentIDs = IssueCommentDAO.sharedInstance().getIssueCommentIDsOfIssueOfIssueLink(issueLinkID, new SubProgressMonitor(monitor, 100));
				Collection<ObjectID> result = new ArrayList<ObjectID>(issueCommentIDs.size() + 1); // add one for the description

				IssueLink issueLink = IssueLinkDAO.sharedInstance().getIssueLink(issueLinkID, FETCH_GROUPS_ISSUE_LINK, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
				IssueDescriptionID issueDescriptionID = IssueDescriptionID.create(
						issueLink.getIssue().getOrganisationID(),
						issueLink.getIssue().getIssueID()
				);

				result.add(issueDescriptionID);
				result.addAll(issueCommentIDs);

				return result;
			}

			if (personID == null)
				return null;

			Collection<IssueLinkID> issueLinkIDs = IssueLinkDAO.sharedInstance().getIssueLinkIDs(personID, new SubProgressMonitor(monitor, 50));
			return issueLinkIDs;
		} finally {
			monitor.done();
		}
	}

	@Override
	public Collection<?> retrieveJDOObjects(Set<ObjectID> objectIDs, ProgressMonitor monitor) {
		if (logger.isDebugEnabled()) {
			logger.debug("retrieveJDOObjects: entered with " + objectIDs.size() + " objectIDs."); //$NON-NLS-1$ //$NON-NLS-2$
			if (logger.isTraceEnabled()) {
				for (ObjectID objectID : objectIDs) {
					logger.trace("retrieveJDOObjects:   * " + objectID); //$NON-NLS-1$
				}
			}
		}

		Collection<Object> result = null;
		Set<IssueID> issueDescriptionIssueIDs = null;
		Set<IssueCommentID> issueCommentIDs = null;
		Set<IssueLinkID> issueLinkIDs = null;
		for (ObjectID objectID : objectIDs) {
			if (objectID instanceof IssueLinkID) {
				IssueLinkID issueLinkID = (IssueLinkID) objectID;

				if (issueLinkIDs == null)
					issueLinkIDs = new HashSet<IssueLinkID>(objectIDs.size());

				issueLinkIDs.add(issueLinkID);
			}
			else if (objectID instanceof IssueCommentID) {
				IssueCommentID issueCommentID = (IssueCommentID) objectID;

				if (issueCommentIDs == null)
					issueCommentIDs = new HashSet<IssueCommentID>(objectIDs.size());

				issueCommentIDs.add(issueCommentID);
			}
			else if (objectID instanceof IssueDescriptionID) {
				IssueDescriptionID issueDescriptionID = (IssueDescriptionID) objectID;

				if (issueDescriptionIssueIDs == null)
					issueDescriptionIssueIDs = new HashSet<IssueID>();

				IssueID issueID = IssueID.create(issueDescriptionID.organisationID, issueDescriptionID.issueID);
				issueDescriptionIssueIDs.add(issueID);
			}
		}

		if (issueLinkIDs != null) {
			if (result == null)
				result = new LinkedList<Object>();

			result.addAll(
					IssueLinkDAO.sharedInstance().getIssueLinks(issueLinkIDs, FETCH_GROUPS_ISSUE_LINK, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor)
			);
		}

		if (issueCommentIDs != null) {
			if (result == null)
				result = new LinkedList<Object>();

			result.addAll(
					IssueCommentDAO.sharedInstance().getIssueComments(issueCommentIDs, FETCH_GROUPS_ISSUE_COMMENT, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor)
			);
		}

		if (issueDescriptionIssueIDs != null) {
			if (result == null)
				result = new LinkedList<Object>();

			List<Issue> issues = IssueDAO.sharedInstance().getIssues(issueDescriptionIssueIDs, FETCH_GROUPS_ISSUE_DESCRIPTION_ISSUE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
			for (Issue issue : issues) {
				result.add(issue.getDescription());
			}
		}

		return result;
	}

	@Override
	public Collection<Class<? extends Object>> getJDOObjectClasses() {
		Collection<Class<? extends Object>> result = new ArrayList<Class<? extends Object>>(3);
		result.add(IssueLink.class);
		result.add(IssueDescription.class);
		result.add(IssueComment.class);
		return result;
	}

	private PersonRelationIssueParentResolver personRelationIssueParentResolver;

	@Override
	public TreeNodeMultiParentResolver getPersonRelationParentResolverDelegate() {
		if (personRelationIssueParentResolver == null)
			personRelationIssueParentResolver = new PersonRelationIssueParentResolver();

		return personRelationIssueParentResolver;
	}

}
