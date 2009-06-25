package org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person;

import javax.jdo.JDOHelper;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issuetracking.trade.ui.IssueTrackingTradePlugin;
import org.nightlabs.jfire.issuetracking.ui.issue.create.CreateIssueWizard;



public class CreateNewIssueViewAction extends Action{
	
	/**
	 *
	 */
	public CreateNewIssueViewAction () {
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
		if(view.getPartner() != null)
		{		
			CreateIssueWizard createIssueWizard = new CreateIssueWizard();
			createIssueWizard.addLinkedObject((ObjectID)JDOHelper.getObjectId(view.getPartner().getPerson()),
					IssueLinkType.ISSUE_LINK_TYPE_ID_RELATED);
			DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(createIssueWizard);
			dialog.open();
		}
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return SharedImages.getSharedImageDescriptor(IssueTrackingTradePlugin.getDefault(),
				this.getClass(),"Create");//$NON-NLS-1$
	}

	@Override
	public String getText() {
		return "Create New Linked Issue";
	}

	@Override
	public String getToolTipText() {
		return "Create New Linked Issue";	
		}

	

}
