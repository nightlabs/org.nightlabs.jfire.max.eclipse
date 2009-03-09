package org.nightlabs.jfire.trade.admin.ui.overview;

import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.PShelfItem;
import org.eclipse.nebula.widgets.pshelf.RedmondShelfRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
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

	private PShelf shelf;
	protected void createComposite(Composite parent)
	{
		parent.setLayout(new FillLayout());

		shelf = new PShelf(parent, SWT.NONE);
		shelf.setRenderer(new RedmondShelfRenderer());
		shelf.setLayoutData(new GridData(GridData.FILL_BOTH));
//		SortedMap<Integer, TradeAdminCategoryFactory> index2Category =
//			TradeAdminOverviewRegistry.sharedInstance().getIndex2Catgeory();
//		for (Iterator<Integer> iterator = index2Category.keySet().iterator(); iterator.hasNext();) {
//			int index = iterator.next();
//			TradeAdminCategoryFactory categoryFactory = index2Category.get(index);
		for (TradeAdminCategoryFactory categoryFactory : TradeAdminOverviewRegistry.sharedInstance().getCategories()) {
			PShelfItem categoryItem = new PShelfItem(shelf, SWT.NONE);
			categoryItem.setText(categoryFactory.getName());
			categoryItem.setImage(categoryFactory.getImage());
			//	    categoryItem.getBody().setLayout(new GridLayout());
			categoryItem.getBody().setLayout(new FillLayout());
			// TODO: should use scrollable composite
			TradeAdminCategory category = categoryFactory.createTradeAdminCategory();
			categoryItem.setData(category);
			category.createComposite(categoryItem.getBody());
		}
	}
	
}
