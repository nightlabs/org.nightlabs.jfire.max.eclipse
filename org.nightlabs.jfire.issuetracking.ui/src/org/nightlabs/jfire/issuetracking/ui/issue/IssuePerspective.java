package org.nightlabs.jfire.issuetracking.ui.issue;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.overview.IssueOverviewView;

/**
 * The RCP perspective holding views and other things for managing {@link Issue}.
 *
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssuePerspective implements IPerspectiveFactory{

	public static final String ID_PERSPECTIVE = IssuePerspective.class.getName();

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		createLayout(layout);
	}

	private void createLayout(IPageLayout layout) {

		layout.setEditorAreaVisible(true);

		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.25f,	IPageLayout.ID_EDITOR_AREA); //$NON-NLS-1$
		left.addView(IssueOverviewView.VIEW_ID);

		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.75f,	IPageLayout.ID_EDITOR_AREA); //$NON-NLS-1$
		bottom.addView(IssueDescriptionView.VIEW_ID);
		bottom.addView(IssueLinkView.VIEW_ID);
		bottom.addView(IssueHistoryView.VIEW_ID);
		bottom.addView(IssueCommentView.VIEW_ID);

		layout.addPerspectiveShortcut(ID_PERSPECTIVE);
		layout.addShowViewShortcut(IssueOverviewView.VIEW_ID);
		RCPUtil.addAllPerspectiveShortcuts(layout);
	}
}