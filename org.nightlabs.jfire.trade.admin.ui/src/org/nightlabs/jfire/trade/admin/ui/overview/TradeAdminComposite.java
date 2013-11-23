package org.nightlabs.jfire.trade.admin.ui.overview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.nightlabs.base.ui.composite.ExclusiveExpandBar;
import org.nightlabs.base.ui.composite.XComposite;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Khaireel Mohamed - khaireel at nightlabs dot de
 */
public class TradeAdminComposite
extends XComposite
{
	public TradeAdminComposite(Composite parent, int style) {
		super(parent, style);
		createComposite(this);
	}

	public TradeAdminComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
		createComposite(this);
	}

	private ExpandBar shelf;
	protected void createComposite(Composite parent)
	{
		parent.setLayout(new FillLayout());

		shelf = new ExpandBar(parent, SWT.NONE);
		ExclusiveExpandBar.enableFor(shelf);
		boolean firstItem = true;
		for (TradeAdminCategoryFactory categoryFactory : TradeAdminOverviewRegistry.sharedInstance().getCategories()) {
			ExpandItem categoryItem = new ExpandItem(shelf, SWT.NONE);
			Composite body = new Composite(categoryItem.getParent(), SWT.NONE);
			categoryItem.setText(categoryFactory.getName());
			categoryItem.setImage(categoryFactory.getImage());
			//	    categoryItem.getBody().setLayout(new GridLayout());
			body.setLayout(new FillLayout());
			categoryItem.setControl(body);
			// TODO: should use scrollable composite
			TradeAdminCategory category = categoryFactory.createTradeAdminCategory();
			categoryItem.setData(category);
			Composite createdComposite = category.createComposite(body);
			if (createdComposite.getLayoutData() != null) {
				createdComposite.setLayoutData(null);
			}
			if (firstItem) {
				categoryItem.setExpanded(true);
				firstItem = false;
			}
		}
	}
	
}
