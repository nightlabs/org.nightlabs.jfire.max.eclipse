package org.nightlabs.jfire.personrelation.ui;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.personrelation.PersonRelationType;
import org.nightlabs.jfire.personrelation.dao.PersonRelationDAO;
import org.nightlabs.jfire.personrelation.id.PersonRelationID;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * Handles the more specialised {@link LabelProvider}s for displaying nodes containing {@link PropertySet}s.
 *
 * @author khaireel (at) nightlabs (dot) de
 */
public class PropertySetTreeLabelProviderDelegate extends AbstractPersonRelationTreeLabelProviderDelegate {
	@Override
	public Class<?> getJDOObjectClass() { return Person.class; }

	@Override
	public Class<? extends ObjectID> getJDOObjectIDClass() { return PropertySetID.class; }

	@Override
	public int[][] getJDOObjectColumnSpan(ObjectID jdoObjectID, Object jdoObject) { return null; }

	@Override
	public String getJDOObjectText(ObjectID jdoObjectID, Object jdoObject, int spanColIndex) {
		return null; // Let the default LabelProvider in the PersonRelationTree handle this.
	}

	@Override
	public Image getJDOObjectImage(ObjectID jdoObjectID, Object jdoObject, int spanColIndex) {
		// Here's the tricky bit: Handling the PropertySet means we are handling the cases when we are at the
		// root node(s) of the PersonRelationTree, where the forward relations to its children is not necessarily straight-forward.
		//
		// For example, (ignoring all children that do not contain a PersonRelation type):
		//    Case I [straight-forward]: If ALL of the root's children are of the same PersonRelationType,
		//                               then this root's relation to ALL it's children is simply the reversed PersonRelationType.
		//
		//    Case II [non-straight-forward]: Otherwise, we cannot be sure what the root's relation to its children is.
		//                                    In this case, do we revert to the default icon?
		//
		// Now, in order to check the children, we need access to the PersonRelationTreeController, where the map between
		// the JDOObjectID and the actual PersonRelationTreeNode is held.

		// This should expedite stuffs; since this method is called several times for the exact same jdoObjectID!
		Image image = objectID2Image.get(jdoObjectID);
		if (image != null)
			return image;

		// Cautiously work to access the data of the LAZY tree.
		// See notes on progress-steps.

		// Lazy-progress: Step 1.
		List<PersonRelationTreeNode> treeNodeList = personRelationTreeController.getTreeNodeList(jdoObjectID);
		if (treeNodeList == null || treeNodeList.isEmpty())
			return null;

		// Lazy progress: Step 2.
		PersonRelationTreeNode rootNode = treeNodeList.get(0); // We can simply pick the first because we are examining only the children -- and all children of a given node is the same!
		long rootNodeChildCount = personRelationTreeController.getNodeCount(rootNode);
		if (rootNodeChildCount < 0)
			return null;

		// Lazy progress: Step 3. Now we should be ready.
		if (rootNodeChildCount == 0)
			return null; // <-- This means that rootNode has no children. Fall-back and let the default procedure take over.


		// We examine all the children for [Case I].
		// i.e. We should be able to access the children's ObjectIDs, without loading the rest of the Objects.
		List<ObjectID> childrenJDOObjectIDs = rootNode.getChildrenJDOObjectIDs();
		PersonRelationType relationTypeToRoot = null;
		for (ObjectID childObjectID : childrenJDOObjectIDs) {
			if (childObjectID instanceof PersonRelationID) {
				PersonRelation personRelation = getPersonRelation((PersonRelationID) childObjectID);
				PersonRelationType personRelationType = personRelation.getPersonRelationType();

				if (relationTypeToRoot == null)
					relationTypeToRoot = personRelationType;
				else {
					if (!relationTypeToRoot.equals(personRelationType)) {
						// [Case II] Stop and no need to figure out the relations anymore. This root doesnt have a unique relationship with its children.
						Image nodeIcon = SharedImages.getSharedImage(PersonRelationPlugin.getDefault(), PersonRelationTreeLabelProviderDelegate.class, "");
						objectID2Image.put((PropertySetID) jdoObjectID, nodeIcon);
						return nodeIcon;
					}
				}
			}
		}


		// One last check:
		if (relationTypeToRoot == null)
			return null;


		// If we have survived up till here, then we have found [Case I] to be true.
		String personRelationTypeID = relationTypeToRoot.getReversePersonRelationTypeID().personRelationTypeID;
		Image nodeIcon = SharedImages.getSharedImage(PersonRelationPlugin.getDefault(), PersonRelationTreeLabelProviderDelegate.class, personRelationTypeID);
		objectID2Image.put((PropertySetID) jdoObjectID, nodeIcon);

		return nodeIcon;
	}


	private PersonRelationTreeController personRelationTreeController;
	private Map<PropertySetID, Image> objectID2Image = new WeakHashMap<PropertySetID, Image>();

	/**
	 * Creates a new instance of the PropertySetTreeLabelProviderDelegate, with a reference to a valid {@link PersonRelationTreeController}.
	 */
	public PropertySetTreeLabelProviderDelegate(PersonRelationTreeController personRelationTreeController) {
		this.personRelationTreeController = personRelationTreeController;
	}

	// TODO Think: Is this a good way? We just need the PersonRelation reference to the ID. Should do this in a Job/SWT thread?
	private PersonRelation getPersonRelation(final PersonRelationID personRelationID) {
		return PersonRelationDAO.sharedInstance().getPersonRelation(
				personRelationID, new String[] {FetchPlan.DEFAULT, PersonRelation.FETCH_GROUP_PERSON_RELATION_TYPE},
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
	}

}
