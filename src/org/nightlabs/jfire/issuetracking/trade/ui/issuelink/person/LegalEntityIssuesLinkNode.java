package org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person;

import java.util.ArrayList;
import java.util.List;
import org.nightlabs.jdo.ObjectID;


/**
 * @author Fitas Amine - fitas at nightlabs dot de
 *
 */
public class LegalEntityIssuesLinkNode{
		
	protected String name;
	protected LegalEntityIssuesLinkNode parent;
	protected List<LegalEntityIssuesLinkNode> nodes;
	private String organisationID;
	private ObjectID personID;
	
	public String getName() {
		return name;
	}

	public List<LegalEntityIssuesLinkNode> getChildNodes() {
		return nodes;
	}

	public void addChildNode(LegalEntityIssuesLinkNode child) {		
		nodes.add(child);
	    child.parent = this;
	}

	public String getOrganisationID() {
		return organisationID;
	}

	public ObjectID getPersonID() {
		return personID;
	}

	public LegalEntityIssuesLinkNode getParent() {
		return parent;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	public LegalEntityIssuesLinkNode(
			String organisationID,
			ObjectID personID,
			String name) 
	{
		super();
		this.organisationID = organisationID;
		this.personID = personID;
		this.nodes = new ArrayList<LegalEntityIssuesLinkNode>();
		this.name = name;
	}
	
	
	
	
}
