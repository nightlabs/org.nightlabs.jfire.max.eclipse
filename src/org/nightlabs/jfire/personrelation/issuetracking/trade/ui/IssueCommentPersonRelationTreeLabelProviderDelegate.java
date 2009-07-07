package org.nightlabs.jfire.personrelation.issuetracking.trade.ui;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.IssueComment;
import org.nightlabs.jfire.issue.id.IssueCommentID;
import org.nightlabs.jfire.personrelation.ui.PersonRelationTreeLabelProviderDelegate;

public class IssueCommentPersonRelationTreeLabelProviderDelegate extends PersonRelationTreeLabelProviderDelegate
{
	@Override
	public Class<?> getJDOObjectClass() {
		return IssueComment.class;
	}

	@Override
	public Class<? extends ObjectID> getJDOObjectIDClass() {
		return IssueCommentID.class;
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

			IssueCommentID issueCommentID = (IssueCommentID) jdoObjectID;
			return issueCommentID.organisationID + '/' + issueCommentID.commentID;
		}

		IssueComment issueComment = (IssueComment) jdoObject;
		switch (spanColIndex) {
			case 0:
				return issueComment.getText();
		}
		return null;
	}

	@Override
	public Image getJDOObjectImage(ObjectID jdoObjectID, Object jdoObject, int spanColIndex) {
		if (spanColIndex == 0) {
			return SharedImages.getSharedImage(Activator.getDefault(), IssueCommentPersonRelationTreeLabelProviderDelegate.class);
		}

		return null;
	}

}
