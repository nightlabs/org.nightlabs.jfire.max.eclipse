package org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person;

import javax.jdo.FetchPlan;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueComment;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issuetracking.trade.ui.IssueTrackingTradePlugin;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Fitas Amine - fitas at nightlabs dot de
 *
 */
public class AddNewCommentViewAction  extends Action{


	private static String[] FETCH_GROUP_ISSUE = new String[]{
		FetchPlan.DEFAULT,
		Issue.FETCH_GROUP_ISSUE_COMMENTS
	};

	/**
	 *
	 */
	public AddNewCommentViewAction() {
		super();
	}

	private LegalEntityPersonIssueLinkTreeView view;
	/**
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(LegalEntityPersonIssueLinkTreeView view) {
		this.view = view;
	}


	@Override
	public void run() {

		InputDialog dlg = new InputDialog(RCPUtil.getActiveShell(),
				"Add new Comment", "Enter a New Comment", "", null);
		if (dlg.open() == Window.OK) {
			// User clicked OK; update the label with the input

			if(view.getSelectedIssueLink().getIssue() != null)
			{
				Issue issue = view.getSelectedIssueLink().getIssue();

				IssueComment comment = new IssueComment(issue.getOrganisationID(),
						IDGenerator.nextID(IssueComment.class),
						issue,
						dlg.getValue(),
						Login.sharedInstance().getUser(new String[]{FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new org.eclipse.core.runtime.NullProgressMonitor()));
				issue.getComments().add(comment);
				IssueDAO.sharedInstance().storeIssue(issue, true, FETCH_GROUP_ISSUE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());		
			}
		}
	}
	

	@Override
	public ImageDescriptor getImageDescriptor() {
		return SharedImages.getSharedImageDescriptor(IssueTrackingTradePlugin.getDefault(),
				this.getClass(),"Add");//$NON-NLS-1$
	}

	@Override
	public String getText() {
		return "Add a Comment";
	}

	@Override
	public String getToolTipText() {
		return "Add a Comment";	
	}
}
