package org.nightlabs.jfire.trade.ui.overview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.search.JDOQueryComposite;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuickSearchQuery;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * 
 */
public abstract class AbstractArticleContainerFilterComposite<R extends ArticleContainer, Q extends AbstractArticleContainerQuickSearchQuery<R>>
	extends AbstractQueryFilterComposite<R, Q>
{
	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 * @param queryProvider
	 */
	public AbstractArticleContainerFilterComposite(Composite parent, int style,
		LayoutMode layoutMode, LayoutDataMode layoutDataMode,
		QueryProvider<R, ? super Q> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
	}

	public AbstractArticleContainerFilterComposite(Composite parent, int style,
		QueryProvider<R, ? super Q> queryProvider)
	{
		super(parent, style, queryProvider);
	}

//	protected StatableFilterComposite statableFilterComposite = null;
//	public StatableFilterComposite getStatableFilterComposite() {AbstractArticleContainerQuickSearchQuery<R>
//		return statableFilterComposite;
//	}

	protected ArticleContainerFilterComposite<R, Q> articleContainerFilterComposite = null;
	public ArticleContainerFilterComposite<R, Q> getArticleContainerFilterComposite() {
		return articleContainerFilterComposite;
	}

	@Override
	protected void createContents()
	{
//		createStatableComposite(parent);
		articleContainerFilterComposite = createArticleContainerComposite();
	}
	
	// FIXME: Move StatableFilterComposite to a new Section! (marius)
//	protected Composite createStatableComposite(Composite parent)
//	{
//		statableFilterComposite = new StatableFilterComposite(this,
//				SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL);
//		statableFilterComposite.setStatableClass(getQueryClass());
//		statableFilterComposite.setToolkit(getToolkit());
//		statableFilterComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		return statableFilterComposite;
//	}
	
	protected ArticleContainerFilterComposite<R, Q> createArticleContainerComposite()
	{
		articleContainerFilterComposite = new ArticleContainerFilterComposite<R, Q>(this,
				SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL);
//		articleContainerFilterComposite.setArticleContainerClass(getQueryClass());
//		articleContainerFilterComposite.setArticleContainerQuery(createArticleContainerQuery());
		articleContainerFilterComposite.setToolkit(getToolkit());
		articleContainerFilterComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return articleContainerFilterComposite;
	}

	@Override
	protected List<JDOQueryComposite<R, Q>> registerJDOQueryComposites()
	{
		List<JDOQueryComposite<R, Q>> queryComps = new ArrayList<JDOQueryComposite<R, Q>>(1);
		
//		if (isArticleContainerStatable())
//		{
//			queryComps.add(statableFilterComposite);			
//		}
		queryComps.add(articleContainerFilterComposite);
		return queryComps;
	}

//	protected AbstractArticleContainerQuickSearchQuery<?> createArticleContainerQuery()
//	{
//		return new ArticleContainerQuery(getQueryClass());
//	}

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
