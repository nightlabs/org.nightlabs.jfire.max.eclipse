package org.nightlabs.jfire.issuetracking.ui.issue.editor.issueMarker;

import org.eclipse.jface.action.Action;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.issue.id.IssueID;
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
	private IssueID issueID;
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

	public void setIssueID(IssueID issueID) {
		this.issueID = issueID;
	}

	@Override
	public void run() {
		System.out.println("\n ------------------ BOO! -----------------\n");
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(new AddIssueMarkerWizard(issueID));
		dialog.open();
	}
}
