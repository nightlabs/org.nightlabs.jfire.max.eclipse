package org.nightlabs.jfire.personrelation.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.ActiveJDOObjectLazyTreeController;
import org.nightlabs.jfire.jdo.notification.TreeNodeMultiParentResolver;
import org.nightlabs.jfire.jdo.notification.TreeNodeParentResolver;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.personrelation.PersonRelationParentResolver;
import org.nightlabs.jfire.personrelation.PersonRelationType;
import org.nightlabs.jfire.personrelation.dao.PersonRelationDAO;
import org.nightlabs.jfire.personrelation.id.PersonRelationID;
import org.nightlabs.jfire.prop.dao.PropertySetDAO;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;
import org.nightlabs.util.CollectionUtil;

public class ActivePersonRelationTreeController
extends ActiveJDOObjectLazyTreeController<ObjectID, Object, PersonRelationTreeNode>
{
	public static final String[] FETCH_GROUPS_PERSON = {
		FetchPlan.DEFAULT, // we only need the display-name, hence this should be sufficient
	};

	public static final String[] FETCH_GROUPS_PERSON_RELATION = {
		FetchPlan.DEFAULT,
		PersonRelation.FETCH_GROUP_PERSON_RELATION_TYPE,
		PersonRelationType.FETCH_GROUP_NAME,
		PersonRelation.FETCH_GROUP_FROM_ID,
		PersonRelation.FETCH_GROUP_TO,
		PersonRelation.FETCH_GROUP_FROM_PERSON_RELATION_IDS,
	};

	private volatile Collection<PropertySetID> rootPersonIDs;

	public Collection<PropertySetID> getRootPersonIDs() {
		return rootPersonIDs;
	}
	public void setRootPersonIDs(Collection<PropertySetID> rootPersonIDs) {
		this.rootPersonIDs = rootPersonIDs;
		clear();
	}

	@Override
	protected PersonRelationTreeNode createNode() {
		return new PersonRelationTreeNode();
	}

	@Override
	protected TreeNodeParentResolver createTreeNodeParentResolver() {
		throw new UnsupportedOperationException("This method should not be called because we're using a TreeNode*Multi*ParentResolver!");
	}

	@Override
	protected TreeNodeMultiParentResolver createTreeNodeMultiParentResolver() {
		return new PersonRelationParentResolver();
	}

	@Override
	protected Class<?> getJDOObjectClass() {
		return PersonRelation.class;
//		throw new UnsupportedOperationException("Due to other methods being overwritten, this method should never be called!");
	}

	@Override
	protected Collection<Object> retrieveJDOObjects(Set<ObjectID> objectIDs, ProgressMonitor monitor)
	{
		monitor.beginTask("Retrieving objects", 110);
		try {
			Set<PropertySetID> personIDs = null;
			Set<PersonRelationID> personRelationIDs = null;

			for (ObjectID objectID : objectIDs) {
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
			}

			Collection<Object> result = new ArrayList<Object>(objectIDs.size());

			int tixPerson = personIDs != null && !personIDs.isEmpty() ? 100 : 0;
			int tixRelation = personRelationIDs != null && !personRelationIDs.isEmpty() ? 100 : 0;

			tixPerson = 100 * 100 / Math.max(1, tixPerson + tixRelation);
			tixRelation = 100 * 100 / Math.max(1, tixPerson + tixRelation);

			monitor.worked(10);

			if (personIDs != null && !personIDs.isEmpty()) {
				result.addAll(
						PropertySetDAO.sharedInstance().getPropertySets(
								personIDs,
								FETCH_GROUPS_PERSON, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								new SubProgressMonitor(monitor, tixPerson)
						)
				);
			}
			else
				monitor.worked(tixPerson);

			if (personRelationIDs != null && !personRelationIDs.isEmpty()) {
				result.addAll(
						PersonRelationDAO.sharedInstance().getPersonRelations(
								personRelationIDs,
								FETCH_GROUPS_PERSON_RELATION, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								new SubProgressMonitor(monitor, tixRelation)
						)
				);
			}
			else
				monitor.worked(tixRelation);

			return result;
		} finally {
			monitor.done();
		}
	}

	@Override
	protected Map<ObjectID, Long> retrieveChildCount(Set<ObjectID> parentIDs, ProgressMonitor monitor) {
		Collection<PropertySetID> rootPersonIDs = this.rootPersonIDs;
		if (rootPersonIDs == null)
			return Collections.emptyMap();

		monitor.beginTask("Retrieving child counts", 110);
		try {
			Set<PropertySetID> personIDs = null;
			Set<PersonRelationID> personRelationIDs = null;

			Map<ObjectID, Long> result = new HashMap<ObjectID, Long>(parentIDs.size());

			for (ObjectID objectID : parentIDs) {
				if (objectID == null) {
					result.put(objectID, new Long(rootPersonIDs.size()));
				}
				else if (objectID instanceof PropertySetID) {
					if (personIDs == null)
						personIDs = new HashSet<PropertySetID>();

					personIDs.add((PropertySetID) objectID);
				}
				else if (objectID instanceof PersonRelationID) {
					if (personRelationIDs == null)
						personRelationIDs = new HashSet<PersonRelationID>();

					personRelationIDs.add((PersonRelationID) objectID);
				}
			}

			int tixPerson = personIDs != null && !personIDs.isEmpty() ? 100 : 1;
			int tixRelation = personRelationIDs != null && !personRelationIDs.isEmpty() ? 100 : 1;

			tixPerson = 100 * 100 / Math.max(1, tixPerson + tixRelation);
			tixRelation = 100 * 100 / Math.max(1, tixPerson + tixRelation);

			monitor.worked(10);

			if (personIDs != null && !personIDs.isEmpty()) {
				ProgressMonitor subMonitor = new SubProgressMonitor(monitor, tixPerson);
				subMonitor.beginTask("Retrieving child counts", personIDs.size());
				for (PropertySetID personID : personIDs) {
					long personRelationCount = PersonRelationDAO.sharedInstance().getPersonRelationCount(
							null, personID, null, new NullProgressMonitor()
					);
					result.put(personID, personRelationCount);
					subMonitor.worked(1);
				}
				subMonitor.done();
			}
			else
				monitor.worked(1);

			if (personRelationIDs != null && !personRelationIDs.isEmpty()) {
				List<PersonRelation> personRelations = PersonRelationDAO.sharedInstance().getPersonRelations(
						personRelationIDs,
						new String[] {
								FetchPlan.DEFAULT,
								PersonRelation.FETCH_GROUP_TO_ID,
						},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new SubProgressMonitor(monitor, tixRelation / 2)
				);

				ProgressMonitor subMonitor = new SubProgressMonitor(monitor, tixRelation / 2);
				subMonitor.beginTask("Retrieving child counts", personRelations.size());
				for (PersonRelation personRelation : personRelations) {
					PersonRelationID personRelationID = (PersonRelationID) JDOHelper.getObjectId(personRelation);
					long personRelationCount = PersonRelationDAO.sharedInstance().getPersonRelationCount(
							null, personRelation.getToID(), null, new NullProgressMonitor()
					);
					result.put(personRelationID, personRelationCount);
					subMonitor.worked(1);
				}
				subMonitor.done();
			}
			else
				monitor.worked(1);

			return result;
		} finally {
			monitor.done();
		}
	}

	@Override
	protected Collection<ObjectID> retrieveChildObjectIDs(ObjectID parentID, ProgressMonitor monitor) {
		Collection<PropertySetID> rootPersonIDs = this.rootPersonIDs;
		if (rootPersonIDs == null)
			return Collections.emptyList();

		monitor.beginTask("Retrieving child IDs", 100);
		try {
			if (parentID == null) {
				return CollectionUtil.castCollection(rootPersonIDs);
			}
			else if (parentID instanceof PropertySetID) {
				Collection<PersonRelationID> childPersonRelationIDs = PersonRelationDAO.sharedInstance().getPersonRelationIDs(
						null, (PropertySetID)parentID, null,
						new SubProgressMonitor(monitor, 80)
				);

				return CollectionUtil.castCollection(childPersonRelationIDs);
			}
			else if (parentID instanceof PersonRelationID) {
				Collection<PersonRelationID> personRelationIDs = Collections.singleton((PersonRelationID)parentID);

				List<PersonRelation> personRelations = PersonRelationDAO.sharedInstance().getPersonRelations(
						personRelationIDs,
						new String[] {
								FetchPlan.DEFAULT,
								PersonRelation.FETCH_GROUP_TO_ID,
						},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new SubProgressMonitor(monitor, 20)
				);

				if (personRelations.isEmpty())
					return Collections.emptyList();

				PersonRelation personRelation = personRelations.iterator().next();

				Collection<PersonRelationID> childPersonRelationIDs = PersonRelationDAO.sharedInstance().getPersonRelationIDs(
						null, personRelation.getToID(), null,
						new SubProgressMonitor(monitor, 80)
				);

				return CollectionUtil.castCollection(childPersonRelationIDs);
			}
			else
				return Collections.emptyList(); // TODO delegate!
		} finally {
			monitor.done();
		}
	}

}
