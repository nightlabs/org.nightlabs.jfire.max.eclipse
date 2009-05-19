package org.nightlabs.jfire.issuetracking.ui.issue.editor.issueMarker;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.issueMarker.IssueMarker;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.AbstractIssueEditorGeneralSection;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditorGeneralPage;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditorPageController;

//      ,-_|\
//     /     \  ]Egoiste in
//     @_,-._/       ]N[ightLabs
//  ======= v =====================================================================================
//  "Science without religion is lame, religion without science is blind." -- A. E. (1879 - 1955).
/**
 * A section for the {@link IssueEditorGeneralPage} to handle the interface mechanisms for the
 * {@link IssueMarker}.
 *
 * @author Khaireel Mohamed - khaireel at nightlabs dot de
 */
public class IssueMarkerSection extends AbstractIssueEditorGeneralSection {
	private AddIssueMarkerAction addIssueMarkerAction;
	private IssueMarkerTable issueMarkerTable;

	public IssueMarkerSection(FormPage page, Composite parent, final IssueEditorPageController controller) {
		super(page, parent, controller);
		getSection().setText("Issue Markers");
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1;

		// The table displaying the IssueMarkers.
		IssueMarkerTableComposite imtComposite = new IssueMarkerTableComposite(client, SWT.NONE);
		issueMarkerTable = imtComposite.getIssueMarkerTable();	// <-- TODO Add ItemChangeListener, and maybe DoubleClickListener.


		getSection().setClient(client);

		// Top set of control buttons. For now: Just a '+' to add a new IssueMarker.
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
			// Revise this later for the general purpose?
			// Currently, this is set to work only on first load.
			issueMarkerTable.setInput(iMs);
		}

		getSection().setExpanded(isMarkersExist);
	}





	// --------------------------------------------------------------------------------------------------------------------------------
	/**
	 *  Setup more control for the TableComposite in this Section.
	 *  This seems enough, and we dont have to have anything more elaborate.
	 */
	private class IssueMarkerTableComposite extends XComposite {
		private IssueMarkerTable issueMarkerTable;

		public IssueMarkerTableComposite(Composite parent, int style) {
			super(parent, style, LayoutMode.TIGHT_WRAPPER);

			// Prepare the wrapper, and sew in the Table.
			getGridLayout().numColumns = 2;
			getGridLayout().makeColumnsEqualWidth = false;
			getGridData().grabExcessHorizontalSpace = true;

			GridData gridData = new GridData(GridData.FILL_BOTH);
			gridData.heightHint = 100;

			issueMarkerTable = new IssueMarkerTable(this, SWT.NONE);
			issueMarkerTable.setLayoutData(gridData);
		}

		public IssueMarkerTable getIssueMarkerTable() { return issueMarkerTable; }
	}
}
