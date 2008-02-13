package org.nightlabs.jfire.trade.ui.overview;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.nightlabs.base.ui.util.RCPUtil;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class TradeOverviewPerspective
implements IPerspectiveFactory
{
	public static final String ID_PERSPECTIVE = TradeOverviewPerspective.class.getName();
	
	public void createInitialLayout(IPageLayout layout)
	{
		layout.setEditorAreaVisible(true);
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.25f,	IPageLayout.ID_EDITOR_AREA); //$NON-NLS-1$
		left.addView(TradeOverviewView.VIEW_ID);
		RCPUtil.addAllPerspectiveShortcuts(layout);
		layout.addShowViewShortcut(TradeOverviewView.VIEW_ID);
	}
	
}
