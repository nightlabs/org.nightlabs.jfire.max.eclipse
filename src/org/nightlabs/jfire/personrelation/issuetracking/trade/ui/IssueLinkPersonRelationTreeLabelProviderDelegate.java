package org.nightlabs.jfire.personrelation.issuetracking.trade.ui;

import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.id.IssueLinkID;
import org.nightlabs.jfire.personrelation.ui.PersonRelationTreeLabelProviderDelegate;
import org.nightlabs.jfire.security.SecurityReflector;
import org.nightlabs.util.NLLocale;

public class IssueLinkPersonRelationTreeLabelProviderDelegate extends PersonRelationTreeLabelProviderDelegate
{
	private String languageID = NLLocale.getDefault().getLanguage();
	private String localOrganisationID = SecurityReflector.getUserDescriptor().getOrganisationID();

	@Override
	public Class<?> getJDOObjectClass() {
		return IssueLink.class;
	}

	@Override
	public Class<? extends ObjectID> getJDOObjectIDClass() {
		return IssueLinkID.class;
	}

	@Override
	public int[][] getJDOObjectColumnSpan(ObjectID jdoObjectID, Object jdoObject) {
		if (jdoObject == null)
			return null;

		return new int[][] { {0}, {1} };
	}

	@Override
	public String getJDOObjectText(ObjectID jdoObjectID, Object jdoObject, int spanColIndex) {
		if (jdoObject == null) {
			if (spanColIndex != 0)
				return null;

			IssueLinkID issueLinkID = (IssueLinkID) jdoObjectID;
			return issueLinkID.organisationID + '/' + issueLinkID.issueLinkID;
		}

		IssueLink issueLink = (IssueLink) jdoObject;
		switch (spanColIndex) {
			case 0:
				return issueLink.getIssueLinkType().getName().getText(languageID);
			case 1:
			{
				Issue issue = issueLink.getIssue();
				StringBuilder sb = new StringBuilder();
				if (!localOrganisationID.equals(issue.getOrganisationID())) {
					sb.append(issue.getOrganisationID());
					sb.append('/');
				}
				sb.append(issue.getIssueID());
				sb.append(": ");
				sb.append(issue.getSubject().getText(languageID));
				return sb.toString();
			}
		}
		return null;
	}

}
