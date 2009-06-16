package org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.jdo.ObjectID;


/**
 * @author Fitas Amine - fitas at nightlabs dot de
 *
 */
public class LegalEntityIssuesLinkNode{
		
	protected String name;
	protected Image icon; 
	protected LegalEntityIssuesLinkNode parent;
	private String organisationID;
	private ObjectID personID;
	
	public String getName() {
		return name;
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

	public Image getIcon() {
		return icon;
	}

	public void setIcon(Image icon) {
		this.icon = icon;
	}
	
	public LegalEntityIssuesLinkNode(
			String organisationID,
			ObjectID personID,
			String name) 
	{
		this(organisationID,personID,name,null);
	}

	public LegalEntityIssuesLinkNode(
			String organisationID,
			ObjectID personID,
			String name,
			Image icon) 
	{
		super();
		this.organisationID = organisationID;
		this.personID = personID;
		this.name = name;
		this.icon = icon;
	}	
	
	
	
	public Object[] getChildNodes() {
		return new ArrayList<LegalEntityIssuesLinkNode>().toArray();	
	}
	
	
}
