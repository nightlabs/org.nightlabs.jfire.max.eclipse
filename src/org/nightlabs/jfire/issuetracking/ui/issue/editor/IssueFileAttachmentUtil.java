package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import java.text.DecimalFormat;

import org.nightlabs.jfire.issue.IssueFileAttachment;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class IssueFileAttachmentUtil
{
	public static String getFileSizeString(IssueFileAttachment issueFileAttachment) {
		DecimalFormat df = new DecimalFormat("#.##");
		if (issueFileAttachment.getFileSize() < 1024) {
			return df.format((double)issueFileAttachment.getFileSize()) + " bytes";
		}

		else if (issueFileAttachment.getFileSize() < 1024 * 1024) {
			return df.format((double)issueFileAttachment.getFileSize() / (double)1024) + " KB";
		}
		else
			return df.format((double)issueFileAttachment.getFileSize() / (double)(1024 * 1024)) + " MB";
	}
}
