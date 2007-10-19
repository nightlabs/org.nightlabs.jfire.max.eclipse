package org.nightlabs.jfire.issuetracking.ui.issue;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.issuetracking.ui.overview.IssueOverviewView;

public class IssuePerspective implements IPerspectiveFactory{
	
	public static final String ID_PERSPECTIVE = IssuePerspective.class.getName();
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		createLayout(layout);
		addShortCuts(layout);
	}
	
	private void createLayout(IPageLayout layout) {
//		layout.setEditorAreaVisible(true);
//		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.25f,	IPageLayout.ID_EDITOR_AREA); //$NON-NLS-1$
//		left.addView(TradeOverviewView.VIEW_ID);
//		RCPUtil.addAllPerspectiveShortcuts(layout);
//		layout.addShowViewShortcut(TradeOverviewView.VIEW_ID);		
		
		layout.setEditorAreaVisible(true);
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.25f,	IPageLayout.ID_EDITOR_AREA); //$NON-NLS-1$
		left.addView(IssueOverviewView.VIEW_ID);
		layout.addPerspectiveShortcut(ID_PERSPECTIVE);
		layout.addShowViewShortcut(IssueOverviewView.VIEW_ID);
		RCPUtil.addAllPerspectiveShortcuts(layout);
		
//		// Editors are placed for free.
//		String editorArea = layout.getEditorArea();
//
//		// left
//		IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.LEFT, (float) 0.26, editorArea);		 //$NON-NLS-1$
//		bottomLeft.addView(IssueView.ID_VIEW);
	}
	
	private void addShortCuts(IPageLayout layout) {
		layout.addShowViewShortcut(IssueOverviewView.VIEW_ID);
		RCPUtil.addAllPerspectiveShortcuts(layout);
	}
}
