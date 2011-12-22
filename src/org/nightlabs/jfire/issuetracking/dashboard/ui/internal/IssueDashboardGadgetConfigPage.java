package org.nightlabs.jfire.issuetracking.dashboard.ui.internal;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
import org.nightlabs.jfire.base.dashboard.ui.AbstractDashbardGadgetConfigPage;
import org.nightlabs.jfire.dashboard.DashboardGadgetLayoutEntry;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.dashboard.IssueDashboardGadgetConfig;
import org.nightlabs.jfire.query.store.BaseQueryStore;
import org.nightlabs.jfire.query.store.dao.QueryStoreDAO;
import org.nightlabs.jfire.query.store.id.QueryStoreID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.NLLocale;
import org.nightlabs.util.Util;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class IssueDashboardGadgetConfigPage 
extends AbstractDashbardGadgetConfigPage<Object> 
{
	private I18nTextEditor gadgetTitle;
	private boolean autoAdjustGadgetTitle = true;
	private Spinner amountOfIssues;
	private ComboViewer choosenQuery;
	
	static class IssueQueryItem 
	{
		/** The <code>null</code> value marks the default issue-query for vanilla "issues". */
		BaseQueryStore queryStore;
		I18nText name;
		
		public IssueQueryItem(BaseQueryStore queryStore) {
			this.queryStore = queryStore;
			this.name = queryStore.getName();
		}
		
		public IssueQueryItem(I18nText name) {
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
	
	public IssueDashboardGadgetConfigPage() {
		super(IssueDashboardGadgetConfigPage.class.getName());
		setTitle("Issue Dashboard Configuration");
	}

	@Override
	public void configure(DashboardGadgetLayoutEntry layoutEntry) {
		if (gadgetTitle != null && gadgetTitle.getI18nText() != null)	
			layoutEntry.getEntryName().copyFrom(gadgetTitle.getI18nText());
		IssueDashboardGadgetConfig newConfig = new IssueDashboardGadgetConfig();
		if (amountOfIssues != null && amountOfIssues.getSelection() > 0)
			newConfig.setAmountOfIssues(amountOfIssues.getSelection());
		if (getSelectedQueryItem() != null)
			newConfig.setIssueQueryItemId(getSelectedQueryItem().getQueryStoreID());
		layoutEntry.setConfig(newConfig);
	}

	@Override
	public Control createPageContents(Composite parent) 
	{
		XComposite wrapper = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, 2);
		
		IssueDashboardGadgetConfig entryConfig = (IssueDashboardGadgetConfig) getLayoutEntry().getConfig();
		final IssueDashboardGadgetConfig config = entryConfig != null ? entryConfig : new IssueDashboardGadgetConfig(); 
		
		Label gadgetDescription = new Label(wrapper, SWT.WRAP);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gadgetDescription.setLayoutData(gd);
		gadgetDescription.setText("Issues");

		Label titleLabel = new Label(wrapper, SWT.NONE);
		titleLabel.setText("Title");
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
		choosenQueryLabel.setText("Choosen Query");
		choosenQueryLabel.setLayoutData(new GridData());
		choosenQuery = new ComboViewer(wrapper);
		choosenQuery.setContentProvider(new ArrayContentProvider());
		choosenQuery.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((IssueQueryItem)element).getName();
			}
		});
		choosenQuery.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (autoAdjustGadgetTitle) {
					IssueQueryItem selectedQueryItem = getSelectedQueryItem();
					if (selectedQueryItem != null)
						gadgetTitle.setI18nText(selectedQueryItem.getNameI18nText(), EditMode.BUFFERED);
				}
			}
		});
		choosenQuery.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label amountLabel = new Label(wrapper, SWT.NONE);
		amountLabel.setText("Amount");
		amountLabel.setLayoutData(new GridData());
		amountOfIssues = new Spinner(wrapper, SWT.BORDER);
		amountOfIssues.setMinimum(1);
		amountOfIssues.setMaximum(100);
		amountOfIssues.setIncrement(5);
		amountOfIssues.setSelection(config.getAmountOfIssues());
		
		Job loadQueriesJob = new Job("Loading Issues")
		{
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception 
			{
				//get all stored queries
				Collection<BaseQueryStore> queries = QueryStoreDAO.sharedInstance().getQueryStoresByReturnType(
						Issue.class, 
						true, 
						new String[]{FetchPlan.DEFAULT, BaseQueryStore.FETCH_GROUP_NAME}, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
						monitor
				);
				
				final List<IssueQueryItem> input = new LinkedList<IssueQueryItem>();
				final IssueQueryItem createDefaultItem = createDefaultItem();
				input.add(createDefaultItem());
				for (BaseQueryStore baseQueryStore : queries) {
					input.add(new IssueQueryItem(baseQueryStore));
				}
				
				choosenQuery.getCombo().getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						choosenQuery.setInput(input);
						if (config != null) {
							for (IssueQueryItem invoiceQueryItem : input) {
								if (Util.equals(invoiceQueryItem.getQueryStoreID(), config.getIssueQueryItemId())) {
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

	private IssueQueryItem createDefaultItem() {
		//non-persistent query: 
		I18nTextBuffer defaultItemName = new I18nTextBuffer();
//		readI18nTextFromProps(defaultItemName, "Issue");
		defaultItemName.setText(NLLocale.getDefault(), "Issue");
		IssueQueryItem defaultQueryItem = new IssueQueryItem(defaultItemName);
		return defaultQueryItem;
	}

	private I18nText createInitialName() {
		I18nTextBuffer text = new I18nTextBuffer();
//		readI18nTextFromProps(text, "Issues");
		text.setText(NLLocale.getDefault(), "Issue");
		return text;
	}
	
//	private void readI18nTextFromProps(I18nTextBuffer defaultItemName, String key) {
//		defaultItemName.readFromProperties(Messages.BUNDLE_NAME, getClass().getClassLoader(), key);
//		// readFromProperties does not include the default-props, add them explicitely
//		defaultItemName.setText(Locale.ENGLISH, ResourceBundle.getBundle(Messages.BUNDLE_NAME, Locale.ENGLISH).getString(key));
//	}

	private IssueQueryItem getSelectedQueryItem() {
		if (choosenQuery != null) {
			ISelection selection = choosenQuery.getSelection();
			if (selection instanceof IStructuredSelection) {
				return (IssueQueryItem) ((IStructuredSelection) selection).getFirstElement();
			}
		}
		return null;
	}

}
