package org.nightlabs.jfire.trade.ui.overview;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.toolkit.IToolkit;
import org.nightlabs.base.ui.util.RCPUtil;
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
			public void selectionChanged(SelectionChangedEvent event) {
				if (PricedArticleContainer.class.isAssignableFrom(getTargetType()))
					if (footerTextSelection != null && !footerTextSelection.isDisposed())
						displayTotals(tableComposite.getSelectedElements(), footerTextSelection);
			}
		});
	}

	private SummedPriceFragmentTypeConfigModule summedPriceFragmentTypeConfigModule; 
	private XComposite footerComposite;
	private Label selectionLabel;
	private Label totalLabel;

	@Override
	public Composite createFooterComposite(final Composite parent) {
		//Checks if the target type is PricedArticleContainer or not...
		if (PricedArticleContainer.class.isAssignableFrom(getTargetType())) {
			loadConfigModuleJob.setPriority(Job.SHORT);
			loadConfigModuleJob.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					//Creates UIs
					getComposite().getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							if (footerComposite != null && !footerComposite.isDisposed()) {
								for (Control child : footerComposite.getChildren())
									child.dispose();
								footerComposite.dispose();
								footerComposite = null;
							}
							
							if (!summedPriceFragmentTypeConfigModule.getSummedPriceFragmentTypeList().isEmpty()) {
								footerComposite = generateFooterComposite(parent);
							}
							
							setFooter(footerComposite);
							relayoutFooter();
						}
					}); //Display
				}//done()
			});

			loadConfigModuleJob.schedule();
		}
		
		return footerComposite;
	}

	private XComposite generateFooterComposite(Composite parent) {
		XComposite footer = new XComposite(parent, SWT.NONE, LayoutMode.TOTAL_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL);
		footer.getGridLayout().numColumns = 4;
		
		IToolkit toolkit = XComposite.retrieveToolkit(footer);
		toolkit.adapt(footer);
		
		selectionLabel = toolkit.createLabel(footer, "Selection: ", SWT.RIGHT);
		GridData gridData = new GridData(GridData.FILL_VERTICAL);
		gridData.widthHint = 150;
		selectionLabel.setLayoutData(gridData);

		footerTextSelection = new Text(footer, SWT.RIGHT | SWT.MULTI);
		footerTextSelection.setEditable(false);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.widthHint = 450;
		footerTextSelection.setLayoutData(gridData);

		totalLabel = toolkit.createLabel(footer, "Total: ", SWT.RIGHT);
		gridData = new GridData(GridData.FILL_VERTICAL);
		gridData.widthHint = 150;
		totalLabel.setLayoutData(gridData);

		footerTextTotal = new Text(footer, SWT.RIGHT | SWT.MULTI);
		footerTextTotal.setEditable(false);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.widthHint = 450;
		footerTextTotal.setLayoutData(gridData);

		if (getListComposite().getElements().isEmpty()) {
			displayTotals((Collection<R>) Collections.emptySet(), footerTextSelection);
			displayTotals((Collection<R>) Collections.emptySet(), footerTextTotal);
		}
		else {
			displayTotals(getListComposite().getSelectedElements(), footerTextSelection);
			displayTotals(getListComposite().getElements(), footerTextTotal);
		}

		XComposite.retrieveToolkit(parent).adapt(footer);
		return footer;
	}
	
	private void relayoutFooter() {
		int numCurrency = currency2PriceFragmentTypeSumMap.keySet().size();
		int numPriceFragmentType = summedPriceFragmentTypeConfigModule.getSummedPriceFragmentTypeList().size();
		
		if (footerComposite != null && !footerComposite.isDisposed())
			footerComposite.getGridData().heightHint = (RCPUtil.getFontHeight(footerTextSelection) * numPriceFragmentType * numCurrency);
		
		getComposite().getParent().layout();
		((SashForm)getComposite()).setWeights(calculateSashWeights(null));
	}
	
	private Job loadConfigModuleJob = new Job("Loading config module") {
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

			return Status.OK_STATUS;
		}
	};

	private NotificationListener configModuleChangeListener = new NotificationAdapterJob() {
		@Override
		public void notify(final NotificationEvent event) {
			loadConfigModuleJob.setPriority(Job.SHORT);
			loadConfigModuleJob.schedule();
		}
	};

	@Override
	protected void displaySearchResult(Object result) {
		super.displaySearchResult(result);
		if (footerTextTotal != null && !footerTextTotal.isDisposed()) {
			Collection<R> elements = getListComposite().getElements();
			displayTotals(elements, footerTextTotal);
			relayoutFooter();
		}
	}

	private Map<Currency, Map<PriceFragmentType, Long>> currency2PriceFragmentTypeSumMap = new HashMap<Currency, Map<PriceFragmentType,Long>>();
	private void displayTotals(Collection<R> articleContainers, Text text) 
	{
		List<PriceFragmentType> summedPriceFragmentTypes = summedPriceFragmentTypeConfigModule.getSummedPriceFragmentTypeList();
		
		for (Map<PriceFragmentType, Long> priceFragmentTypeSumMap : currency2PriceFragmentTypeSumMap.values()) {
			priceFragmentTypeSumMap.clear();
		}
		
		//For each article containers
		for (R articleContainer : articleContainers) {
			PricedArticleContainer pricedArticleContainer = (PricedArticleContainer) articleContainer;
			Price price = pricedArticleContainer.getPrice();
			if (price != null) 
			{
				Currency currency = price.getCurrency();
				if (currency2PriceFragmentTypeSumMap.get(currency) == null) 
				{
					//Initialises the map
					currency2PriceFragmentTypeSumMap.put(currency, new HashMap<PriceFragmentType, Long>());
				}

				//For each summed price fragment types
				for (PriceFragmentType summedPriceFragmentType : summedPriceFragmentTypes) 
				{
					PriceFragment priceFragment = price.getPriceFragment(summedPriceFragmentType.getPrimaryKey(), false);
					long amount = priceFragment == null ? 0 : priceFragment.getAmount();
					
					Map<PriceFragmentType, Long> priceFragmentTypeSumMap = currency2PriceFragmentTypeSumMap.get(currency);
					Long totalAmount = priceFragmentTypeSumMap.get(summedPriceFragmentType);
					
					if (totalAmount == null) {
						totalAmount = 0L;
					}
					
					totalAmount += amount;
					priceFragmentTypeSumMap.put(summedPriceFragmentType, totalAmount);
				}
			}
		}

		StringBuilder sumString = new StringBuilder("");
		for (Currency currency : currency2PriceFragmentTypeSumMap.keySet()) {
			Map<PriceFragmentType, Long> priceFragmentTypeSumMap = currency2PriceFragmentTypeSumMap.get(currency);
			for (PriceFragmentType priceFragmentType : summedPriceFragmentTypes) {
				sumString.append(priceFragmentType.getName().getText()); 
				sumString.append(" = ");

				Long sum = priceFragmentTypeSumMap.get(priceFragmentType);;
				long longSum = (sum == null?0:sum);
				sumString.append(NumberFormatter.formatCurrency(longSum, currency));

				sumString.append("\n ");
			}	
		}

		text.setText(sumString.toString());
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
