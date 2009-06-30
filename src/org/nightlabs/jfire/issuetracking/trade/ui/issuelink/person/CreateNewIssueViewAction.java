package org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Point;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.issuetracking.trade.ui.IssueTrackingTradePlugin;
import org.nightlabs.jfire.issuetracking.ui.issuelink.attach.AttachIssueToObjectWizard;


/**
 * @author Fitas Amine - fitas at nightlabs dot de
 *
 */
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
			AttachIssueToObjectWizard attachIssueToObjectWizard = new AttachIssueToObjectWizard(view.getPartner().getPerson());
			DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(attachIssueToObjectWizard)
			{
				@Override
				protected Point getInitialSize()
				{
					return new Point(convertHorizontalDLUsToPixels(600), convertVerticalDLUsToPixels(450));
				}
			};
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
