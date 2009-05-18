package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.issueMarker.IssueMarker;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;

//      ,-_|\
//     /     \  ]Egoiste in
//     @_,-._/       ]N[ightLabs
//  ======= v =====================================================================================
//  "Science without religion is lame, religion without science is blind." -- A. E. (1879 - 1955).
/**
 * Comments.
 *
 * @author Khaireel Mohamed - khaireel at nightlabs dot de
 */
public class IssueMarkerSection extends AbstractIssueEditorGeneralSection {
//	private Issue issue;

	private final AddIssueMarkerAction addIssueMarkerAction;
	private final Text commentText;	// <-- TODO PLACE HOLDER: Properly rename and rework on this later.

	public IssueMarkerSection(FormPage page, Composite parent, final IssueEditorPageController controller) {
		super(page, parent, controller);
		getSection().setText("Issue Markers");
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1;

		commentText = new Text(client, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		commentText.setFont(new Font(getSection().getDisplay(), new FontData("Courier", 8, SWT.NORMAL))); //$NON-NLS-1$
		commentText.setText("[Place holder] Under construction...");

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 60;
		commentText.setLayoutData(gridData);
//		commentText.addModifyListener(new ModifyListener() {
//			@Override
//			public void modifyText(ModifyEvent e) {
//				if(!commentText.getText().equals("")) //$NON-NLS-1$
//					markDirty();
//				else
//					markUndirty();
//			}
//		});
		getSection().setClient(client);

		addIssueMarkerAction = new AddIssueMarkerAction();
		getToolBarManager().add(addIssueMarkerAction);

		updateToolBarManager();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.issuetracking.ui.issue.editor.AbstractIssueEditorGeneralSection#doSetIssue(org.nightlabs.jfire.issue.Issue)
	 */
	@Override
	protected void doSetIssue(Issue issue) {
		Set<IssueMarker> iMs = issue.getIssueMarkers();
		boolean isMarkersExist = iMs != null && !iMs.isEmpty();
		if (isMarkersExist) {
			String meTexts = "";
			for(IssueMarker iM : iMs)
				meTexts += iM.getName().getText() + ": " + iM.getDescription().getText() + "\n";

			commentText.setText(meTexts);
		}

		getSection().setExpanded(isMarkersExist);
	}




	// -------------> Helper bits. Later on. To be improved.
	public class AddIssueMarkerAction extends Action {
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

}
