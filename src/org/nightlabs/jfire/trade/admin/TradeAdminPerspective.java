package org.nightlabs.jfire.trade.admin;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.trade.admin.overview.TradeAdminView;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class TradeAdminPerspective 
implements IPerspectiveFactory 
{
	public static final String ID_PERSPECTIVE = TradeAdminPerspective.class.getName();
	
	public void createInitialLayout(IPageLayout layout) 
	{
		layout.setEditorAreaVisible(true);
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.25f,	IPageLayout.ID_EDITOR_AREA); //$NON-NLS-1$
		left.addView(TradeAdminView.VIEW_ID);
		layout.addPerspectiveShortcut(ID_PERSPECTIVE);
		layout.addShowViewShortcut(TradeAdminView.VIEW_ID);
		RCPUtil.addAllPerspectiveShortcuts(layout);
	}

}
