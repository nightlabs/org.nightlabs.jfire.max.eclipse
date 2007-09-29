package org.nightlabs.jfire.trade.admin.tariff;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.trade.admin.tariffmapping.TariffMappingView;

public class TariffPerspective
implements IPerspectiveFactory
{

	public void createInitialLayout(IPageLayout layout)
	{
		layout.setEditorAreaVisible(false);
		layout.addView(TariffEditView.ID_VIEW, IPageLayout.LEFT, 1f, IPageLayout.ID_EDITOR_AREA);
		layout.addView(TariffMappingView.ID_VIEW, IPageLayout.RIGHT, 0.5f, TariffEditView.ID_VIEW);
		
  	layout.addShowViewShortcut(TariffEditView.ID_VIEW);
  	layout.addShowViewShortcut(TariffMappingView.ID_VIEW);
  	
		RCPUtil.addAllPerspectiveShortcuts(layout);
	}

}
