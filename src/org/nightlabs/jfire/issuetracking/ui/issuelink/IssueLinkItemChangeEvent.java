package org.nightlabs.jfire.issuetracking.ui.issuelink;

import java.util.Collection;
import java.util.EventObject;

import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTableItem;

/**
 * 
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 */
public class IssueLinkItemChangeEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * An Enum defining values used for specifying the event type.
	 */
	public static enum ChangeType {
		add,
		remove
	}

	private ChangeType changeType;
	private Collection<IssueLinkTableItem> issueLinkTableItems;

	/**
	 * 
	 * @param source
	 * @param changeType
	 * @param issueLinkTableItems
	 */
	public IssueLinkItemChangeEvent(Object source, ChangeType changeType, Collection<IssueLinkTableItem> issueLinkTableItems) {
		super(source);
		assert changeType != null : "changeType != null"; //$NON-NLS-1$
		assert issueLinkTableItems != null : "issueLinkTableItems != null"; //$NON-NLS-1$
		this.changeType = changeType;
		this.issueLinkTableItems = issueLinkTableItems;
	}

	/**
	 * 
	 * @return
	 */
	public ChangeType getChangeType() {
		return changeType;
	}

	/**
	 * 
	 * @return
	 */
	public Collection<IssueLinkTableItem> getIssueLinkTableItems() {
		return issueLinkTableItems;
	}
}
