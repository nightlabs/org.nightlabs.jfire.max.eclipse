package org.nightlabs.jfire.issuetracking.ui.issue.editor.issueMarker;

import org.eclipse.jface.action.Action;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueLinkListSection;

//      ,-_|\
//     /     \  ]Egoiste in
//     @_,-._/       ]N[ightLabs
//  ======= v =====================================================================================
//  "Science without religion is lame, religion without science is blind." -- A. E. (1879 - 1955).
/**
 * This accompanies the {@link IssueMarkerSection} in handling the interfacing process.
 * TODO Complete the codes man!
 *
 * @author Khaireel Mohamed - khaireel at nightlabs dot de
 */
public class AddIssueMarkerAction extends Action {
	/**
	 * Creates a new instance of the AddIssueMarkerAction.
	 */
	public AddIssueMarkerAction() {
		setId(AddIssueMarkerAction.class.getName());
		setImageDescriptor(SharedImages.getSharedImageDescriptor(
				IssueTrackingPlugin.getDefault(),
				IssueLinkListSection.class,
				"Add")); //$NON-NLS-1$
		setToolTipText("Add issue marker(s)");
		setText("Add issue marker(s)");
	}

	@Override
	public void run() {
		// TODO Provide interface through a dialog to get user to add a new IssueMarker)
	}
}
