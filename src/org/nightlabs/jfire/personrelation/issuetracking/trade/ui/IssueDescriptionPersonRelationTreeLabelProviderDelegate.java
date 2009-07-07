package org.nightlabs.jfire.personrelation.issuetracking.trade.ui;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.IssueDescription;
import org.nightlabs.jfire.issue.id.IssueDescriptionID;
import org.nightlabs.jfire.personrelation.ui.PersonRelationTreeLabelProviderDelegate;
import org.nightlabs.util.NLLocale;

public class IssueDescriptionPersonRelationTreeLabelProviderDelegate extends PersonRelationTreeLabelProviderDelegate
{
	private String languageID = NLLocale.getDefault().getLanguage();

	@Override
	public Class<?> getJDOObjectClass() {
		return IssueDescription.class;
	}

	@Override
	public Class<? extends ObjectID> getJDOObjectIDClass() {
		return IssueDescriptionID.class;
	}

	@Override
	public int[][] getJDOObjectColumnSpan(ObjectID jdoObjectID, Object jdoObject) {
		if (jdoObject == null)
			return null;

		return new int[][] { {0, 1} };
	}

	@Override
	public String getJDOObjectText(ObjectID jdoObjectID, Object jdoObject, int spanColIndex) {
		if (jdoObject == null) {
			if (spanColIndex != 0)
				return null;

			IssueDescriptionID issueDescriptionID = (IssueDescriptionID) jdoObjectID;
			return issueDescriptionID.organisationID + '/' + issueDescriptionID.issueID;
		}

		IssueDescription issueDescription = (IssueDescription) jdoObject;
		switch (spanColIndex) {
			case 0:
				return issueDescription.getText(languageID);
		}
		return null;
	}

	@Override
	public Image getJDOObjectImage(ObjectID jdoObjectID, Object jdoObject, int spanColIndex) {
		if (spanColIndex == 0) {
			return SharedImages.getSharedImage(Activator.getDefault(), IssueDescriptionPersonRelationTreeLabelProviderDelegate.class);
		}

		return null;
	}

}
