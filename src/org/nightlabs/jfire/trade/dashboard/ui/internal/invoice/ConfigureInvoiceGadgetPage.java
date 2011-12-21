package org.nightlabs.jfire.trade.dashboard.ui.internal.invoice;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

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
	
	private static final String QueryStoreID = null;

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
		
		Label l = new Label(wrapper, SWT.WRAP);
		l.setText("This gadget will help you to see the invoices of your most interest.");
		
		gadgetTitle = new I18nTextEditor(wrapper, "Set the title of this gadget:");
		if (!getLayoutEntry().getEntryName().isEmpty()) {
			gadgetTitle.setI18nText(getLayoutEntry().getEntryName());
		} else {
			gadgetTitle.setI18nText(createInitialName());
		}

		//TODO set label left of spinner
		Label amountOfInvoicesLabel = new Label(wrapper, SWT.LEFT);
		amountOfInvoicesLabel.setText("Amount of invoices to show:");
		amountOfInvoices = new Spinner(wrapper, SWT.BORDER);
		amountOfInvoices.setMinimum(1);
		amountOfInvoices.setSelection(5);
		
		//TODO add a label left of the ComboViewer
		choosenQuery = new ComboViewer(wrapper);
		choosenQuery.setContentProvider(new ArrayContentProvider());
		choosenQuery.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((InvoiceQueryItem)element).name;
			}
		});
		
		Job loadQueriesJob = new Job("Loading queries...") 
		{
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception 
			{
				Collection<BaseQueryStore> queries = QueryStoreDAO.sharedInstance().getQueryStoresByReturnType(
						Invoice.class, 
						true, 
						new String[]{BaseQueryStore.FETCH_GROUP_NAME, BaseQueryStore.FETCH_GROUP_DESCRIPTION}, 
						1, 
						monitor
				);
				final List<InvoiceQueryItem> input = new LinkedList<ConfigureInvoiceGadgetPage.InvoiceQueryItem>();
				input.add(createDefaultItem()); //TODO could this be set in italic style?
				for (BaseQueryStore baseQueryStore : queries) {
					input.add(new InvoiceQueryItem((QueryStoreID) JDOHelper.getObjectId(baseQueryStore), baseQueryStore.getName().getText()));
				}
				
				choosenQuery.getCombo().getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						choosenQuery.setInput(input);
						choosenQuery.setSelection(new StructuredSelection(config.getInvoiceQueryItemId()));
					}
				});
				
				return Status.OK_STATUS;
			}
		};
		
		return wrapper;
	}

	private InvoiceQueryItem createDefaultItem() {
		//non-persistent query: 
		QueryStoreID invoiceQueryItemId = null;
		InvoiceQueryItem defaultQueryItem = new InvoiceQueryItem(invoiceQueryItemId, "Latest invoices");
		return defaultQueryItem;
	}


	private I18nText createInitialName() {
		I18nTextBuffer text = new I18nTextBuffer();
		//TODO get localised initial message
		text.setText(Locale.ENGLISH, "My last invoices:");
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
