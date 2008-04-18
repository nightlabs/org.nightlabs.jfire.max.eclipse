/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issuelink;

import java.util.Map;
import java.util.Set;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.progress.ProgressMonitor;

/**
 * Handle retrieving and displaying of objects linked to an {@link Issue}.
 * <p>
 * It is possible to link arbitrary objects to an {@link Issue} via instances
 * of {@link IssueLink}. The only requirement these objects need to fulfill is to be
 * JDO objects having an object-id assigned which implements {@link ObjectID}.
 * </p>
 * <p>
 * Implementors should not directly implement this interface, but instead extend
 * the abstract class {@link AbstractIssueLinkHandler}.
 * </p>
 *
 * @author chairatk
 */
public interface IssueLinkHandler<LinkedObjectID extends ObjectID, LinkedObject>
{
	/**
	 * Query the linked objects with appropriate fetch-groups from the server.
	 * <p>
	 * In order to display {@link IssueLink}s, the
	 * {@link IssueLink#getLinkedObject() linked objects}
	 * need to be queried from the server with the appropriate fetch-groups. Therefore,
	 * the framework first queries the <code>IssueLink</code>s without the linked objects resolved
	 * (but with the properties {@link IssueLink#getLinkedObjectID() linkedObjectID}
	 * and {@link IssueLink#getLinkedObjectClass() linkedObjectClass} available) and then
	 * calls this method to obtain the corresponding object.
	 * </p>
	 * <p>
	 * Which fetch-groups are required depends on your implementation of
	 * {@link #getLinkedObjectImage(IssueLink, Object)}
	 * and {@link #getLinkedObjectName(IssueLink, Object)}.
	 * </p>
	 * <p>
	 * If you don't require any data from the linked object (e.g. because you only display
	 * the primary key fields), then you can put <code>null</code> values into the returned
	 * map.
	 * </p>
	 * <p>
	 * This method is called asynchronously in a {@link Job}.
	 * </p>
	 *
	 * @param issueLinks the {@link IssueLink} instances which have no {@link IssueLink#getLinkedObject()} resolved yet.
	 * @param monitor the monitor for feedback.
	 * @return the linked objects mapped by the corresponding IssueLinks (the ones that were passed as <code>issueLinks</code>).
	 *		If the linked objects are not required (because their IDs are sufficient), <code>null</code> values can be put into this map
	 *		(the keys must not contain <code>null</code>!). 
	 */
	Map<IssueLink, LinkedObject> getLinkedObjects(Set<IssueLink> issueLinks, ProgressMonitor monitor);

	Object getLinkedObject(LinkedObjectID linkedObjectID, ProgressMonitor monitor);
	
	/**
	 * Get the image of the object linked to the given <code>issueLink</code>
	 * (passed as <code>linkedObject</code>).
	 * <p>
	 * After retrieving all data, {@link IssueLink} instances together with their
	 * {@link IssueLink#getLinkedObject() linked objects} are displayed in the UI.
	 * Since you might need special fetch-groups, the linked object is first
	 * obtained separately by {@link #getLinkedObjects(Set, ProgressMonitor)} and
	 * then passed separately as <code>linkedObject</code>.
	 * </p>
	 * <p>
	 * This method is called on the SWT GUI thread.
	 * </p>
	 *
	 * param issueLink the instance to be displayed in the UI.
	 * param linkedObject the object linked to the given <code>issueLink</code> or <code>null</code> iff {@link #getLinkedObjects(Set, ProgressMonitor)} didn't retrieve it.
	 * @return <code>null</code> (to not show any image) or an image symbolising the given <code>linkedObject</code>.
	 * @see #getLinkedObjectName(IssueLink, Object)
	 */
	Image getLinkedObjectImage();

	/**
	 * Get the name of the object linked to the given <code>issueLink</code>
	 * (passed as <code>linkedObject</code>).
	 * <p>
	 * After retrieving all data, {@link IssueLink} instances together with their
	 * {@link IssueLink#getLinkedObject() linked objects} are displayed in the UI.
	 * Since you might need special fetch-groups, the linked object is first
	 * obtained separately by {@link #getLinkedObjects(Set, ProgressMonitor)} and
	 * then passed separately as <code>linkedObject</code>.
	 * </p>
	 * <p>
	 * This method is called on the SWT GUI thread.
	 * </p> 
	 * 
	 * @param linkedObject the object linked to the given <code>issueLink</code> or <code>null</code> iff {@link #getLinkedObjects(Set, ProgressMonitor)} didn't retrieve it.
	 * @return the text to be shown as name of the linked object. Must never be <code>null</code>.
	 * @see #getLinkedObjectImage(IssueLink, Object)
	 */
	String getLinkedObjectName(LinkedObjectID linkedObjectID);

	/**
	 * Open an editor for the linked object of the given <code>issueLink</code>.
	 * <p>
	 * This method is called on the SWT GUI thread.
	 * </p>
	 *
	 * @param issueLink the {@link IssueLink} whose linked object shall be opened.
	 * @param linkedObjectID the linked object - i.e. the same as {@link IssueLink#getLinkedObjectID() issueLink.getLinkedObjectID()} (convenience parameter).
	 */
	void openLinkedObject(LinkedObjectID linkedObjectID);
}
