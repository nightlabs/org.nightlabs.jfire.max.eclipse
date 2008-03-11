package org.nightlabs.jfire.issuetracking.ui.issuelink;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jdo.JDOHelper;

import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

public abstract class AbstractIssueLinkHandler <LinkedObjectID extends ObjectID, LinkedObject>
implements IssueLinkHandler<LinkedObjectID, LinkedObject>
{
	@SuppressWarnings("unchecked")
	@Override
	public Map<IssueLink, LinkedObject> getLinkedObjects(Set<IssueLink> issueLinks, ProgressMonitor monitor)
	{
		monitor.beginTask("Loading delivery notes", 100);

		Set<LinkedObjectID> linkedObjectIDs = new HashSet<LinkedObjectID>(issueLinks.size());
		for (IssueLink issueLink : issueLinks)
			linkedObjectIDs.add((LinkedObjectID) issueLink.getLinkedObjectID());

		monitor.worked(10);

		Collection<? extends LinkedObject> linkedObjects = _getLinkedObjects(
				issueLinks,
				linkedObjectIDs,
				new SubProgressMonitor(monitor, 80));

		Map<ObjectID, LinkedObject> objectID2objectMap = new HashMap<ObjectID, LinkedObject>(linkedObjects.size());
		for (LinkedObject lo : linkedObjects)
			objectID2objectMap.put((ObjectID) JDOHelper.getObjectId(lo), lo);

		Map<IssueLink, LinkedObject> linkedObjectMap = new HashMap<IssueLink, LinkedObject>(issueLinks.size());
		for (IssueLink issueLink : issueLinks) {
			LinkedObject lo = objectID2objectMap.get(issueLink.getLinkedObjectID());
			if (lo == null)
				throw new IllegalStateException("Object missing in result set! " + issueLink.getLinkedObjectID());
			
			linkedObjectMap.put(issueLink, lo);
		}

		monitor.worked(10);
		monitor.done();
		return linkedObjectMap;
	}

	protected abstract Collection<LinkedObject> _getLinkedObjects(
			Set<IssueLink> issueLinks,
			Set<LinkedObjectID> linkedObjectIDs,
			ProgressMonitor monitor); 
}
