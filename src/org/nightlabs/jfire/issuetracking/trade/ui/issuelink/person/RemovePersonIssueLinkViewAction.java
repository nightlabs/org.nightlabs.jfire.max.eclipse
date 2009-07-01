package org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.issuetracking.trade.ui.IssueTrackingTradePlugin;

/**
 * @author Fitas Amine - fitas at nightlabs dot de
 *
 */
public class RemovePersonIssueLinkViewAction extends Action{
	

	public RemovePersonIssueLinkViewAction() {
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
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return SharedImages.getSharedImageDescriptor(IssueTrackingTradePlugin.getDefault(),
				this.getClass(),"Delete");//$NON-NLS-1$
	}

	@Override
	public String getText() {
		return "Removes the Link Person/Issue";
	}

	@Override
	public String getToolTipText() {
		return "Removes the Link between Person/Issue";
	}


}
