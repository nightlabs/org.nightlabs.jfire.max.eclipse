package org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.issuetracking.trade.ui.IssueTrackingTradePlugin;


/**
 * @author Fitas Amine - fitas at nightlabs dot de
 *
 */
public class AddNewCommentViewAction  extends Action{

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
