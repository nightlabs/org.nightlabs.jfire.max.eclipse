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
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.toolkit.IToolkit;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.Price;
import org.nightlabs.jfire.accounting.PriceFragment;
import org.nightlabs.jfire.accounting.PriceFragmentType;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.base.ui.overview.search.JDOQuerySearchEntryViewer;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.PricedArticleContainer;
import org.nightlabs.jfire.trade.config.SummedPriceFragmentTypeConfigModule;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuery;
import org.nightlabs.l10n.NumberFormatter;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
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
	private XComposite footer;
	private Label selectionLabel;
	private Label totalLabel;
	private boolean hasContent;
	
	@Override
	public Composite createFooterComposite(Composite parent) {
		//Checks if the target type is PricedArticleContainer or not...
		if (PricedArticleContainer.class.isAssignableFrom(getTargetType())) {
			footer = new XComposite(parent, SWT.NONE, LayoutDataMode.GRID_DATA_HORIZONTAL);
			footer.getGridLayout().numColumns = 4;
			
			hasContent = false;
			
			updateSummaryJob.setPriority(Job.SHORT);
			updateSummaryJob.schedule();
			
			return footer;
		}

		return null;
	}

	private Job updateSummaryJob = new Job("Loading config module") {
		@Override
		protected IStatus run(ProgressMonitor monitor) throws Exception {
			monitor.beginTask("Loading config module....", 100);

			//Loads config module 
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

			JDOLifecycleManager.sharedInstance().addNotificationListener(SummedPriceFragmentTypeConfigModule.class, configModuleChangeListener);

			//Creates UIs
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					int lineHeight = 0;
					if (!hasContent) {
						IToolkit toolkit = XComposite.retrieveToolkit(footer);
						
						selectionLabel = toolkit.createLabel(footer, "", SWT.RIGHT);
						selectionLabel.setText("Selection: ");
						selectionLabel.setLayoutData(new GridData(GridData.FILL_BOTH));

						footerTextSelection = new StyledText(footer, SWT.WRAP | SWT.MULTI);
						footerTextSelection.setAlignment(SWT.RIGHT);
						GridData gridData = new GridData(GridData.FILL_BOTH);
						footerTextSelection.setLayoutData(gridData);
						
						totalLabel = toolkit.createLabel(footer, "", SWT.RIGHT);
						totalLabel.setText("Total: ");
						totalLabel.setLayoutData(new GridData(GridData.FILL_BOTH));

						footerTextTotal = new StyledText(footer, SWT.WRAP | SWT.MULTI);
						footerTextTotal.setAlignment(SWT.RIGHT);
						gridData = new GridData(GridData.FILL_BOTH);
						footerTextTotal.setLayoutData(gridData);

						displayTotals((Collection<R>) Collections.emptySet(), footerTextSelection);
						displayTotals((Collection<R>) Collections.emptySet(), footerTextTotal);
						
						lineHeight = 20 * summedPriceFragmentTypeConfigModule.getSummedPriceFragmentTypeList().size() + 10;		
						
						footer.getGridData().heightHint = lineHeight;
						footer.getParent().layout();
						((SashForm)getComposite()).setWeights(calculateSashWeights(null));
						
						hasContent = true;
					}
					else {
						displayTotals(getListComposite().getSelectedElements(), footerTextSelection);
						displayTotals(getListComposite().getElements(), footerTextTotal);
					}
				}
			});
			
			return Status.OK_STATUS;
		}
	};
	
	private NotificationListener configModuleChangeListener = new NotificationAdapterJob() {
		@Override
		public void notify(final NotificationEvent event) {
			updateSummaryJob.setPriority(Job.SHORT);
			updateSummaryJob.schedule();
		}
	};
	
	@Override
	protected void displaySearchResult(Object result) {
		super.displaySearchResult(result);
		if (footerTextTotal != null) {
			Collection<R> elements = getListComposite().getElements();
			displayTotals(elements, footerTextTotal);
		}
	}

	private Map<Currency, Map<PriceFragmentType, Long>> currency2SummedPriceMap = new HashMap<Currency, Map<PriceFragmentType,Long>>();
//	private Map<PriceFragmentType, Long> priceFragmentType2ValueMap = new HashMap<PriceFragmentType, Long>();
	private void displayTotals(Collection<R> articleContainers, StyledText styledText) 
	{
		//Configured price fragment types that need to be calculated
		List<PriceFragmentType> summedPriceFragmentTypes = summedPriceFragmentTypeConfigModule.getSummedPriceFragmentTypeList();
		
//		//Initialises the values in the map
//		for (PriceFragmentType summedPriceFragmentType : summedPriceFragmentTypes) {
//			priceFragmentType2ValueMap.put(summedPriceFragmentType, new Long(0));
//		}

//		for (PriceFragmentType summedPriceFragmentType : summedPriceFragmentTypes) {
//			priceFragmentType2ValueMap.put(summedPriceFragmentType, new Long(0));
//		}
		
		//For each article containers
		for (R articleContainer : articleContainers) {
			PricedArticleContainer pricedArticleContainer = (PricedArticleContainer) articleContainer;
			Price price = pricedArticleContainer.getPrice();
			if (price != null) 
			{
				Currency currency = price.getCurrency();
				if (currency2SummedPriceMap.get(currency) == null) 
				{
					//Initialises the map
					currency2SummedPriceMap.put(currency, new HashMap<PriceFragmentType, Long>());
				}
				else
				{
					//Clears the data
					Map<PriceFragmentType, Long> priceFragmentType2ValueMap = currency2SummedPriceMap.get(currency);
					priceFragmentType2ValueMap.clear();
				}
				
				//For each price fragment types
				for (PriceFragmentType summedPriceFragmentType : summedPriceFragmentTypes) 
				{
					Map<PriceFragmentType, Long> priceFragmentType2ValueMap = currency2SummedPriceMap.get(currency);
					PriceFragment priceFragment = price.getPriceFragment(summedPriceFragmentType.getPrimaryKey(), false);
					Long amount = priceFragmentType2ValueMap.get(summedPriceFragmentType);
					if (amount != null) {
						amount = amount + (priceFragment == null ? 0:priceFragment.getAmount());
						priceFragmentType2ValueMap.put(summedPriceFragmentType, amount);
					}
				}
			}
		}

		StringBuilder sumString = new StringBuilder("");
		for (Currency currency : currency2SummedPriceMap.keySet()) {
			Map<PriceFragmentType, Long> priceFragmentType2ValueMap = currency2SummedPriceMap.get(currency);
			for (PriceFragmentType priceFragmentType : summedPriceFragmentTypes) {
				sumString.append(priceFragmentType.getName().getText()); 
				sumString.append(" = ");
				
				Long value = priceFragmentType2ValueMap.get(priceFragmentType);;
				long longValue = (value == null?0:value);
				sumString.append(NumberFormatter.formatCurrency(longValue, currency));

				sumString.append("\n ");
			}	
		}
		

		styledText.setText(sumString.toString());
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
