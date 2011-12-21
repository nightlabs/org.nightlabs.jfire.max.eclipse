package org.nightlabs.jfire.trade.dashboard.ui.internal.invoice;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.language.I18nTextEditor;
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

public class ConfigureInvoiceGadgetPage extends AbstractDashbardGadgetConfigPage<Object> {
	
	protected static final String DashboardGadgetInvoice = null;

	private I18nTextEditor gadgetTitle;
	
	private Spinner amountOfInvoices;

	private ComboViewer choosenQuery;

	
	static class InvoiceQueryItem 
	{
		/** The <code>null</code> value marks the default invoice-query for vanilla "latest invoices". */
		QueryStoreID invoiceQueryItemId;
		String name;
		public InvoiceQueryItem(QueryStoreID invoiceQueryItemId, String name) {
			super();
			this.invoiceQueryItemId = invoiceQueryItemId;
			this.name = name;
		}
	}

	
	public ConfigureInvoiceGadgetPage() {
		super(ConfigureInvoiceGadgetPage.class.getName());
		setTitle(Messages.getString("org.nightlabs.jfire.base.dashboard.ui.internal.invoice.ConfigureInvoiceGadgetPage.title")); //$NON-NLS-1$
	}


	@Override
	public void configure(DashboardGadgetLayoutEntry layoutEntry) {
		if (gadgetTitle != null && gadgetTitle.getI18nText() != null)
			layoutEntry.getEntryName().copyFrom(gadgetTitle.getI18nText());
		DashboardGadgetInvoiceConfig newConfig = new DashboardGadgetInvoiceConfig();
		if (amountOfInvoices != null && amountOfInvoices.getSelection() > 0)
			newConfig.setAmountOfInvoices(amountOfInvoices.getSelection());
		if (getSelectedInvoiceQueryItem() != null)
			newConfig.setInvoiceQueryItemId(getSelectedInvoiceQueryItem().invoiceQueryItemId);
		layoutEntry.setConfig(newConfig);
	}


	@Override
	public Control createPageContents(Composite parent) 
	{
		XComposite wrapper = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		
		DashboardGadgetInvoiceConfig entryConfig = (DashboardGadgetInvoiceConfig) getLayoutEntry().getConfig();
		final DashboardGadgetInvoiceConfig config = entryConfig != null ? entryConfig : new DashboardGadgetInvoiceConfig(); 
		
		GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		gridData.verticalIndent = 15;
		gridData.widthHint = 500;
		
		Label gadgetDescription = new Label(wrapper, SWT.WRAP);
		gadgetDescription.setText(Messages.getString("org.nightlabs.jfire.trade.dashboard.ui.internal.invoice.ConfigureInvoiceGadgetPage.0")); //$NON-NLS-1$
		
		gadgetTitle = new I18nTextEditor(wrapper, Messages.getString("org.nightlabs.jfire.trade.dashboard.ui.internal.invoice.ConfigureInvoiceGadgetPage.1")); //$NON-NLS-1$
		if (!getLayoutEntry().getEntryName().isEmpty()) {
			gadgetTitle.setI18nText(getLayoutEntry().getEntryName());
		} else {
			gadgetTitle.setI18nText(createInitialName());
		}
		gadgetTitle.setLayoutData(gridData);

		gridData = new GridData();
		gridData.verticalIndent = 15;
		
		Label amountOfInvoicesLabel = new Label(wrapper, SWT.LEFT);
		amountOfInvoicesLabel.setText(Messages.getString("org.nightlabs.jfire.trade.dashboard.ui.internal.invoice.ConfigureInvoiceGadgetPage.2")); //$NON-NLS-1$
		amountOfInvoicesLabel.setLayoutData(gridData);
		amountOfInvoices = new Spinner(wrapper, SWT.BORDER);
		amountOfInvoices.setMinimum(1);
		amountOfInvoices.setMaximum(100);
		amountOfInvoices.setIncrement(5);
		amountOfInvoices.setSelection(5);
		
		gridData = new GridData();
		gridData.verticalIndent = 15;
		
		Label choosenQueryLabel = new Label(wrapper, SWT.LEFT);
		choosenQueryLabel.setText(Messages.getString("org.nightlabs.jfire.trade.dashboard.ui.internal.invoice.ConfigureInvoiceGadgetPage.3")); //$NON-NLS-1$
		choosenQueryLabel.setLayoutData(gridData);
		choosenQuery = new ComboViewer(wrapper);
//		choosenQuery.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		choosenQuery.setContentProvider(new ArrayContentProvider());
		choosenQuery.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((InvoiceQueryItem)element).name;
			}
		});
		
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
					input.add(new InvoiceQueryItem((QueryStoreID) JDOHelper.getObjectId(baseQueryStore), baseQueryStore.getName().getText()));
				}
				
				choosenQuery.getCombo().getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						choosenQuery.setInput(input);
						if (config != null && config.getInvoiceQueryItemId() != null) {
							for (InvoiceQueryItem invoiceQueryItem : input) {
								if (invoiceQueryItem.invoiceQueryItemId.equals(config.getInvoiceQueryItemId()))
									choosenQuery.setSelection(new StructuredSelection(invoiceQueryItem));
							}
						} else
							choosenQuery.setSelection(new StructuredSelection(createDefaultItem));
						
						choosenQuery.getControl().pack();
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
		QueryStoreID invoiceQueryItemId = null;
		InvoiceQueryItem defaultQueryItem = new InvoiceQueryItem(invoiceQueryItemId, Messages.getString("org.nightlabs.jfire.trade.dashboard.ui.internal.invoice.ConfigureInvoiceGadgetPage.5")); //$NON-NLS-1$
		return defaultQueryItem;
	}


	private I18nText createInitialName() {
		I18nTextBuffer text = new I18nTextBuffer();
		//TODO get localised initial message
		text.setText(Locale.ENGLISH, Messages.getString("org.nightlabs.jfire.trade.dashboard.ui.internal.invoice.ConfigureInvoiceGadgetPage.6")); //$NON-NLS-1$
		return text;
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
