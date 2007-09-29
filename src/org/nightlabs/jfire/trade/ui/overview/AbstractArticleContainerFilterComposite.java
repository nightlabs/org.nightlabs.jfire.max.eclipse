package org.nightlabs.jfire.trade.ui.overview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.ui.JDOQueryComposite;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * 
 */
public abstract class AbstractArticleContainerFilterComposite
extends AbstractQueryFilterComposite
{
	public AbstractArticleContainerFilterComposite(Composite parent, int style) {
		super(parent, style);
	}

	protected StatableFilterComposite statableFilterComposite = null;
	public StatableFilterComposite getStatableFilterComposite() {
		return statableFilterComposite;
	}

	protected ArticleContainerFilterComposite articleContainerFilterComposite = null;
	public ArticleContainerFilterComposite getArticleContainerFilterComposite() {
		return articleContainerFilterComposite;
	}

	@Override
	protected void createContents(Composite parent) 
	{
		createStatableComposite(parent);
		createArticleContainerComposite(parent);		
	}
	
	protected Composite createStatableComposite(Composite parent) 
	{
		statableFilterComposite = new StatableFilterComposite(this,		
				SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL);
		statableFilterComposite.setStatableClass(getQueryClass());
		statableFilterComposite.setToolkit(getToolkit());
		statableFilterComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return statableFilterComposite;
	}
	
	protected Composite createArticleContainerComposite(Composite parent) 
	{
		articleContainerFilterComposite = new ArticleContainerFilterComposite(this,		
				SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL);
		articleContainerFilterComposite
				.setArticleContainerClass(getQueryClass());
		articleContainerFilterComposite.setToolkit(getToolkit());
		articleContainerFilterComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return articleContainerFilterComposite;
	}
	
	@Override
	protected List<JDOQueryComposite> registerJDOQueryComposites() 
	{
		List<JDOQueryComposite> queryComps = new ArrayList<JDOQueryComposite>(2);
		queryComps.add(statableFilterComposite);
		queryComps.add(articleContainerFilterComposite);
		return queryComps;
	}

//	@Override
//	protected List<QuickSearchEntry> registerQuickSearchEntryTypes() 
//	{
//		List<QuickSearchEntry> quickSearchEntries = new ArrayList<QuickSearchEntry>();
//		quickSearchEntries.add(new DeliveryNoteIDQuickSearchEntry());		
//		quickSearchEntries.add(new DeliveryNoteCustomerNameQuickSearchEntry());		
//		quickSearchEntries.add(new DeliveryNoteVendorNameQuickSearchEntry());
//		return quickSearchEntries;
//	}

}
