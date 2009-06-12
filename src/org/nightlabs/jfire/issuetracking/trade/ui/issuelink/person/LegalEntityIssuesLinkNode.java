package org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person;

import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.prop.id.PropertySetID;


/**
 * @author Fitas Amine - fitas at nightlabs dot de
 *
 */
public class LegalEntityIssuesLinkNode {

	private String organisationID;
	private ObjectID personID;

	public String getOrganisationID() {
		return organisationID;
	}

	public ObjectID getPersonID() {
		return personID;
	}

	public LegalEntityIssuesLinkNode(
			String organisationID,
			ObjectID personID) 
	{
		super();
		this.organisationID = organisationID;
		this.personID = personID;
	}

}
