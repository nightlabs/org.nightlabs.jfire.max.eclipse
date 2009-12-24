package org.nightlabs.jfire.trade.ui.overview;

import java.util.Collection;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.accounting.Price;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.base.ui.overview.search.JDOQuerySearchEntryViewer;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.PricedArticleContainer;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuery;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marco Schulze - Marco at NightLabs dot de
 */
public abstract class ArticleContainerEntryViewer<R extends ArticleContainer, Q extends AbstractArticleContainerQuery>
	extends JDOQuerySearchEntryViewer<R, Q>
{
	private Text footerTextTotal;
	private Text footerTextSelection;

	public ArticleContainerEntryViewer(Entry entry) {
		super(entry);
	}

	@Override
	protected void addResultTableListeners(
			final AbstractTableComposite<R> tableComposite) {
		super.addResultTableListeners(tableComposite);
		tableComposite.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				displayTotals(tableComposite.getSelectedElements(), "Selection total: ", footerTextSelection);
			}
		});
	}

	@Override
	public Composite createFooterComposite(Composite parent) {
		if (PricedArticleContainer.class.isAssignableFrom(getTargetType())) {
			XComposite footer = new XComposite(parent, SWT.NONE, XComposite.LayoutMode.TIGHT_WRAPPER);
			footer.getGridLayout().numColumns = 2;
			
			footerTextTotal = new Text(footer, SWT.MULTI | SWT.RIGHT);
			footerTextTotal.setEditable(false);
			footerTextTotal.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			footerTextSelection = new Text(footer, SWT.MULTI | SWT.RIGHT);
			footerTextSelection.setEditable(false);
			footerTextSelection.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			return footer;
		}
		return null;
	}

	@Override
	protected void displaySearchResult(Object result) {
		super.displaySearchResult(result);
		if (footerTextTotal != null) {
			Collection<R> elements = getListComposite().getElements();
			displayTotals(elements, "Total: ", footerTextTotal);
		}
	}

	private void displayTotals(Collection<R> articleContainers, String prefix, Text text) {
		// TODO: Sum per currency
		// TODO: Sum separate (configurable) price-fragments
		long totalSum = 0;
		Currency curr = null;
		for (R ac : articleContainers) {
			Price acPrice = getACPrice(ac);
			if (acPrice != null) {
				totalSum += acPrice.getAmount();
				curr = acPrice.getCurrency();
			}
		}
		if (totalSum != 0) {
			text.setText(prefix + curr.toDouble(totalSum) + " " + curr.getCurrencySymbol());
		} else {
			text.setText(prefix + "0");
		}
	}

	/**
	 * Returns Article containers' price
	 * @param ac
	 * @return
	 */
	private Price getACPrice(R ac) {
		if (ac instanceof Offer) {
			return ((Offer)ac).getPrice();
		} else if (ac instanceof Invoice) {
			return ((Invoice) ac).getPrice();
		}
		return null;
	}
	
//	private void show

//	@Override
//	protected void optimizeSearchResults(Object result)
//	{
//		if (result instanceof Collection) {
//			Collection articleContainers = (Collection) result;
//			Set<AnchorID> anchorIDs = new HashSet<AnchorID>(articleContainers.size() * 2);
//			for (Object object : articleContainers) {
//				if (object instanceof ArticleContainer) {
//					ArticleContainer articleContainer = (ArticleContainer) object;
//					anchorIDs.add(articleContainer.getVendorID());
//					anchorIDs.add(articleContainer.getCustomerID());
//				}
//			}
//			String[] FETCH_GROUP_ARTICLE_CONTAINER_ANCHORS = new String[] {
//					LegalEntity.FETCH_GROUP_PERSON,
//					FetchPlan.DEFAULT
//			};
//			// fetch the name of the customer and the vendor at once to add them to the cache
//			// what avoids afterwards multiple connections to the server in labelProvider
//			LegalEntityProvider.sharedInstance().getLegalEntities(
//					anchorIDs.toArray(new AnchorID[anchorIDs.size()]),
//					FETCH_GROUP_ARTICLE_CONTAINER_ANCHORS,
//					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//		}
//	}

}
