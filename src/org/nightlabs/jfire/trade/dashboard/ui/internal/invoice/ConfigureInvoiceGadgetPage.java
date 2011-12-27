package org.nightlabs.jfire.trade.dashboard.ui.internal.invoice;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.base.dashboard.ui.AbstractDashbardGadgetConfigPage;
import org.nightlabs.jfire.dashboard.DashboardGadgetLayoutEntry;
import org.nightlabs.jfire.query.store.BaseQueryStore;
import org.nightlabs.jfire.query.store.dao.QueryStoreDAO;
import org.nightlabs.jfire.query.store.id.QueryStoreID;
import org.nightlabs.jfire.trade.dashboard.DashboardGadgetInvoiceConfig;
import org.nightlabs.jfire.trade.dashboard.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.Util;

public class ConfigureInvoiceGadgetPage extends AbstractDashbardGadgetConfigPage<Object> {
	
	protected static final String DashboardGadgetInvoice = null;

	private I18nTextEditor gadgetTitle;
	private boolean autoAdjustGadgetTitle = true;
	
	private Spinner amountOfInvoices;

	private ComboViewer choosenQuery;

	
	static class InvoiceQueryItem 
	{
		/** The <code>null</code> value marks the default invoice-query for vanilla "latest invoices". */
		BaseQueryStore queryStore;
		I18nText name;
		public InvoiceQueryItem(BaseQueryStore queryStore) {
			super();
			this.queryStore = queryStore;
			this.name = queryStore.getName();
		}
		
		public InvoiceQueryItem(I18nText name) {
			super();
			this.name = name;
		}
		
		private String getName() {
			return name.getText();
		}
		
		private I18nText getNameI18nText() {
			return name;
		}
		
		private QueryStoreID getQueryStoreID() {
			if (queryStore == null)
				return null;
			return (QueryStoreID) JDOHelper.getObjectId(queryStore);
		}
	}

	
	public ConfigureInvoiceGadgetPage() {
		super(ConfigureInvoiceGadgetPage.class.getName());
		setTitle(Messages.getString("org.nightlabs.jfire.trade.dashboard.ui.internal.invoice.ConfigureInvoiceGadgetPage.title")); //$NON-NLS-1$
	}

	@Override
	public void initialize(DashboardGadgetLayoutEntry<?> layoutEntry) {
		super.initialize(layoutEntry);
	}

	@Override
	public void configure(DashboardGadgetLayoutEntry layoutEntry) {
		if (gadgetTitle != null && gadgetTitle.getI18nText() != null)	
			layoutEntry.getEntryName().copyFrom(gadgetTitle.getI18nText());
		DashboardGadgetInvoiceConfig newConfig = new DashboardGadgetInvoiceConfig();
		if (amountOfInvoices != null && amountOfInvoices.getSelection() > 0)
			newConfig.setAmountOfInvoices(amountOfInvoices.getSelection());
		if (getSelectedInvoiceQueryItem() != null)
			newConfig.setInvoiceQueryItemId(getSelectedInvoiceQueryItem().getQueryStoreID());
		layoutEntry.setConfig(newConfig);
	}


	@Override
	public Control createPageContents(Composite parent) 
	{
		XComposite wrapper = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, 2);
		
		
		DashboardGadgetInvoiceConfig entryConfig = (DashboardGadgetInvoiceConfig) getLayoutEntry().getConfig();
		final DashboardGadgetInvoiceConfig config = entryConfig != null ? entryConfig : new DashboardGadgetInvoiceConfig(); 
		
		Label gadgetDescription = new Label(wrapper, SWT.WRAP);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gadgetDescription.setLayoutData(gd);
		gadgetDescription.setText(Messages.getString("org.nightlabs.jfire.trade.dashboard.ui.internal.invoice.ConfigureInvoiceGadgetPage.0")); //$NON-NLS-1$

		Label titleLabel = new Label(wrapper, SWT.NONE);
		titleLabel.setText(Messages.getString("org.nightlabs.jfire.trade.dashboard.ui.internal.invoice.ConfigureInvoiceGadgetPage.1")); //$NON-NLS-1$
		gadgetTitle = new I18nTextEditor(wrapper); 
		if (!getLayoutEntry().getEntryName().isEmpty()) {
			gadgetTitle.setI18nText(getLayoutEntry().getEntryName());
		} else {
			gadgetTitle.setI18nText(createInitialName());
		}
		gadgetTitle.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		gadgetTitle.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				autoAdjustGadgetTitle = false;
			}
		});
		Label choosenQueryLabel = new Label(wrapper, SWT.NONE);
		choosenQueryLabel.setText(Messages.getString("org.nightlabs.jfire.trade.dashboard.ui.internal.invoice.ConfigureInvoiceGadgetPage.3")); //$NON-NLS-1$
		choosenQueryLabel.setLayoutData(new GridData());
		choosenQuery = new ComboViewer(wrapper);
//		choosenQuery.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		choosenQuery.setContentProvider(new ArrayContentProvider());
		choosenQuery.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((InvoiceQueryItem)element).getName();
			}
		});
		choosenQuery.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (autoAdjustGadgetTitle) {
					InvoiceQueryItem selectedInvoiceQueryItem = getSelectedInvoiceQueryItem();
					if (selectedInvoiceQueryItem != null)
						gadgetTitle.setI18nText(selectedInvoiceQueryItem.getNameI18nText(), EditMode.BUFFERED);
				}
			}
		});
		choosenQuery.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		

		Label amountOfInvoicesLabel = new Label(wrapper, SWT.NONE);
		amountOfInvoicesLabel.setText(Messages.getString("org.nightlabs.jfire.trade.dashboard.ui.internal.invoice.ConfigureInvoiceGadgetPage.2")); //$NON-NLS-1$
		amountOfInvoicesLabel.setLayoutData(new GridData());
		int max = DashboardGadgetInvoiceConfig.maxAmountOfInvoicesInDashboard;
		amountOfInvoices = new Spinner(wrapper, SWT.BORDER);
		amountOfInvoices.setMinimum(1);
		amountOfInvoices.setMaximum(max);
		amountOfInvoices.setIncrement(5);
		amountOfInvoices.setPageIncrement(5);
		
		int amount = DashboardGadgetInvoiceConfig.initialAmountOfInvoicesInDashboard;	// initial selection if none could be read out
		if (config.getAmountOfInvoices() > 0)
			amount = config.getAmountOfInvoices();
		
		amountOfInvoices.setSelection(amount < max + 1 ? amount : max);
		
		Job loadQueriesJob = new Job(Messages.getString("org.nightlabs.jfire.trade.dashboard.ui.internal.invoice.ConfigureInvoiceGadgetPage.4"))  //$NON-NLS-1$
		{
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception 
			{
				//get all stored queries
				Collection<BaseQueryStore> queries = QueryStoreDAO.sharedInstance().getQueryStoresByReturnType(
						Invoice.class, 
						true, 
						new String[]{FetchPlan.DEFAULT, BaseQueryStore.FETCH_GROUP_NAME}, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
						monitor
				);
				
				final List<InvoiceQueryItem> input = new LinkedList<InvoiceQueryItem>();
				final InvoiceQueryItem createDefaultItem = createDefaultItem();
				input.add(createDefaultItem());
				for (BaseQueryStore baseQueryStore : queries) {
					input.add(new InvoiceQueryItem(baseQueryStore));
				}
				
				choosenQuery.getCombo().getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						choosenQuery.setInput(input);
						if (config != null) {
							for (InvoiceQueryItem invoiceQueryItem : input) {
								if (Util.equals(invoiceQueryItem.getQueryStoreID(), config.getInvoiceQueryItemId())) {
									boolean oldVal = autoAdjustGadgetTitle;
									try {
										autoAdjustGadgetTitle = false;
										choosenQuery.setSelection(new StructuredSelection(invoiceQueryItem));
									} finally {
										autoAdjustGadgetTitle = oldVal;
									}
								}
							}
						} else
							choosenQuery.setSelection(new StructuredSelection(createDefaultItem));
					}
				});
				
				return Status.OK_STATUS;
			}
		};
		loadQueriesJob.schedule();
		return wrapper;
	}

	private InvoiceQueryItem createDefaultItem() {
		//non-persistent query: 
		I18nTextBuffer defaultItemName = new I18nTextBuffer();
		readI18nTextFromProps(defaultItemName, "org.nightlabs.jfire.trade.dashboard.ui.internal.invoice.ConfigureInvoiceGadgetPage.5"); //$NON-NLS-1$
		InvoiceQueryItem defaultQueryItem = new InvoiceQueryItem(defaultItemName);
		return defaultQueryItem;
	}

	private I18nText createInitialName() {
		I18nTextBuffer text = new I18nTextBuffer();
		readI18nTextFromProps(text,"org.nightlabs.jfire.trade.dashboard.ui.internal.invoice.ConfigureInvoiceGadgetPage.6"); //$NON-NLS-1$
		return text;
	}
	

	private void readI18nTextFromProps(I18nTextBuffer defaultItemName, String key) {
		defaultItemName.readFromProperties(Messages.BUNDLE_NAME, getClass().getClassLoader(), key);
		// readFromProperties does not include the default-props, add them explicitely
		defaultItemName.setText(Locale.ENGLISH, ResourceBundle.getBundle(Messages.BUNDLE_NAME, Locale.ENGLISH).getString(key));
	}

	
	private InvoiceQueryItem getSelectedInvoiceQueryItem() {
		if (choosenQuery != null) {
			ISelection selection = choosenQuery.getSelection();
			if (selection instanceof IStructuredSelection) {
				return (InvoiceQueryItem) ((IStructuredSelection) selection).getFirstElement();
			}
		}
		return null;
	}
}
