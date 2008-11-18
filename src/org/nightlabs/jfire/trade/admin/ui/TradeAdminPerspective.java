package org.nightlabs.jfire.trade.admin.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.trade.admin.ui.customergroupmapping.CustomerGroupMappingView;
import org.nightlabs.jfire.trade.admin.ui.overview.TradeAdminView;
import org.nightlabs.jfire.trade.admin.ui.tariff.TariffEditView;
import org.nightlabs.jfire.trade.admin.ui.tariffmapping.TariffMappingView;

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
		
		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.7f, IPageLayout.ID_EDITOR_AREA);
		bottom.addView(TariffEditView.ID_VIEW);
		bottom.addView(TariffMappingView.ID_VIEW);
		bottom.addView(CustomerGroupMappingView.ID_VIEW);
		
		layout.addShowViewShortcut(TradeAdminView.VIEW_ID);
		layout.addShowViewShortcut(TariffEditView.ID_VIEW);
		layout.addShowViewShortcut(TariffMappingView.ID_VIEW);
		layout.addShowViewShortcut(CustomerGroupMappingView.ID_VIEW);
		
		RCPUtil.addAllPerspectiveShortcuts(layout);
	}

}
