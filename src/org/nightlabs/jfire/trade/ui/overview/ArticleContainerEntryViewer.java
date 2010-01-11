package org.nightlabs.jfire.trade.ui.overview;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.Price;
import org.nightlabs.jfire.accounting.PriceFragment;
import org.nightlabs.jfire.accounting.PriceFragmentType;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.base.ui.overview.search.JDOQuerySearchEntryViewer;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.PricedArticleContainer;
import org.nightlabs.jfire.trade.config.SummedPriceFragmentTypeConfigModule;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuery;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marco Schulze - Marco at NightLabs dot de
 */
public abstract class ArticleContainerEntryViewer<R extends ArticleContainer, Q extends AbstractArticleContainerQuery>
	extends JDOQuerySearchEntryViewer<R, Q>
{
	private StyledText footerTextTotal;
	private StyledText footerTextSelection;

	public ArticleContainerEntryViewer(Entry entry) {
		super(entry);
	}

	@Override
	protected void addResultTableListeners(
			final AbstractTableComposite<R> tableComposite) {
		super.addResultTableListeners(tableComposite);
		tableComposite.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (PricedArticleContainer.class.isAssignableFrom(getTargetType()))
					displayTotals(tableComposite.getSelectedElements(), footerTextSelection);
			}
		});
	}

	private SummedPriceFragmentTypeConfigModule summedPriceFragmentTypeConfigModule; 
	@Override
	public Composite createFooterComposite(Composite parent) {
		if (PricedArticleContainer.class.isAssignableFrom(getTargetType())) {
			final XComposite footer = new XComposite(parent, SWT.NONE, LayoutMode.ORDINARY_WRAPPER);
			footer.getGridLayout().numColumns = 2;
			
			Job job = new Job("Loading config module") {
				@Override
				protected IStatus run(ProgressMonitor monitor) throws Exception {
					monitor.beginTask("Loading config module....", 100);

					summedPriceFragmentTypeConfigModule = ConfigUtil.getUserCfMod(
							SummedPriceFragmentTypeConfigModule.class,
							new String[] {
								FetchPlan.DEFAULT,
								SummedPriceFragmentTypeConfigModule.FETCH_GROUP_SUMMED_PRICE_FRAGMENT_TYPE_LIST,
								PriceFragmentType.FETCH_GROUP_NAME,
							},
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							new SubProgressMonitor(monitor, 10)
					);		
					
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							Label selectionLabel = new Label(footer, SWT.RIGHT);
							selectionLabel.setText("Selection: ");
							selectionLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

							footerTextSelection = new StyledText(footer, SWT.WRAP);
							footerTextSelection.setAlignment(SWT.RIGHT);
							GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
							footerTextSelection.setLayoutData(gridData);
							footerTextSelection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
							displayTotals((Collection<R>) Collections.emptySet(), footerTextSelection);

							Label totalLabel = new Label(footer, SWT.RIGHT);
							totalLabel.setText("Total: ");
							totalLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

							footerTextTotal = new StyledText(footer, SWT.WRAP);
							footerTextTotal.setAlignment(SWT.RIGHT);
							gridData = new GridData(GridData.FILL_HORIZONTAL);
							footerTextTotal.setLayoutData(gridData);
							displayTotals((Collection<R>) Collections.emptySet(), footerTextTotal);

							footer.layout();
						}
					});
					return Status.OK_STATUS;
				}
			};
			job.setPriority(Job.SHORT);
			job.schedule();
			
			return footer;
		}
		
		return null;
	}

	@Override
	protected void displaySearchResult(Object result) {
		super.displaySearchResult(result);
		if (footerTextTotal != null) {
			Collection<R> elements = getListComposite().getElements();
			displayTotals(elements, footerTextTotal);
		}
	}

	private void displayTotals(Collection<R> articleContainers, StyledText styledText) {
		List<PriceFragmentType> priceFragmentTypes = summedPriceFragmentTypeConfigModule.getSummedPriceFragmentTypeList();
		
		Map<PriceFragmentType, Long> priceFragmentType2ValueMap = new HashMap<PriceFragmentType, Long>();
		Currency currency = null;
		for (R articleContainer : articleContainers) {
			PricedArticleContainer pricedArticleContainer = (PricedArticleContainer) articleContainer;
			Price price = pricedArticleContainer.getPrice();
			if (price != null) {
				long totalSum = 0;
				for (PriceFragmentType priceFragmentType : priceFragmentTypes) {
					PriceFragment priceFragment = price.getPriceFragment(priceFragmentType.getPrimaryKey(), false);
					if (priceFragmentType2ValueMap.get(priceFragmentType) != null)
						totalSum = priceFragmentType2ValueMap.get(priceFragmentType) + priceFragment.getAmount();
					
					priceFragmentType2ValueMap.put(priceFragmentType, totalSum);
				}
				totalSum += price.getAmount();
				currency = price.getCurrency();
			}
		}
		
		StringBuilder sumString = new StringBuilder("");
		for (PriceFragmentType priceFragmentType : priceFragmentType2ValueMap.keySet()) {
			sumString.append(priceFragmentType.getName().getText() + " = " + currency.toDouble(priceFragmentType2ValueMap.get(priceFragmentType)) + ", ");
		}
		
		styledText.setText(sumString + " " + currency.getCurrencySymbol());
//		if (totalSum != 0) {
//			styledText.setText(currency.toDouble(totalSum) + " " + currency.getCurrencySymbol());
//		} else {
//			styledText.setText("0");
//		}
	}

//	/**
//	 * Returns Article containers' price
//	 * @param ac
//	 * @return
//	 */
//	private Price getACPrice(R ac) {
//		if (ac instanceof Offer) {
//			return ((Offer)ac).getPrice();
//		} else if (ac instanceof Invoice) {
//			return ((Invoice) ac).getPrice();
//		}
//		return null;
//	}
	
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
