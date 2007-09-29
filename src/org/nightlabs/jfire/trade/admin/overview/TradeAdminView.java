package org.nightlabs.jfire.trade.admin.overview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 */
public class TradeAdminView 
extends LSDViewPart 
{
	public static String VIEW_ID = TradeAdminView.class.getName();
	
	private TradeAdminComposite tradeAdminComposite = null;
	public void createPartContents(Composite parent) 
	{
		tradeAdminComposite = new TradeAdminComposite(parent, SWT.NONE);
		if (parent.getLayout() instanceof FillLayout) {			
			tradeAdminComposite.setLayoutData(new Object());
		}
		if (parent.getLayout() instanceof GridLayout) {
			tradeAdminComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		}
	}
}
