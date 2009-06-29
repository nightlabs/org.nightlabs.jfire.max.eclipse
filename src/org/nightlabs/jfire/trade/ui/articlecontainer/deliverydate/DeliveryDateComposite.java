package org.nightlabs.jfire.trade.ui.articlecontainer.deliverydate;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.composite.DateTimeControl;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.dao.ArticleContainerDAO;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuery;
import org.nightlabs.jfire.trade.query.DeliveryNoteQuery;
import org.nightlabs.jfire.trade.query.OfferQuery;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.jfire.trade.ui.legalentity.view.LegalEntityEditorView;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class DeliveryDateComposite extends XComposite
{
	private DateTimeControl dateTimeControl;
	private DeliveryDateTable deliveryDateTable;
	private Button onlyCurrentBuisnessPartnerButton;
	private boolean onlyCurrentBusinessPartner = true;
	private Set<Class<? extends ArticleContainer>> articleContainerClasses;
	private Combo typeCombo;
//	private ExpandableComposite advancedFilterComp;

	private static final String TYPE_OFFER = "Offer";
	private static final String TYPE_DELIVERY_NOTE = "DeliveryNote";
	private static final String TYPE_BOTH = "Both";

	/**
	 * @param parent
	 * @param style
	 */
	public DeliveryDateComposite(Composite parent, int style) {
		super(parent, style);
		createComposite(this);
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public DeliveryDateComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
		createComposite(this);
	}

	protected void createComposite(Composite parent)
	{
		final Composite filterCriteriaWrapper = new XComposite(parent, SWT.NONE, LayoutMode.ORDINARY_WRAPPER,
				LayoutDataMode.GRID_DATA_HORIZONTAL, 3);
		Label label = new Label(filterCriteriaWrapper, SWT.NONE);
		label.setText("Delivery Date");
		label.setToolTipText("Search offers or deliverynotes which contain articles where the delivery date is higher than the specified one");
		dateTimeControl = new DateTimeControl(filterCriteriaWrapper, true, SWT.NONE, DateFormatter.FLAGS_DATE_SHORT);
		dateTimeControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button searchButton = new Button(filterCriteriaWrapper, SWT.PUSH);
		searchButton.setImage(SharedImages.SEARCH_16x16.createImage());
		searchButton.setToolTipText("To search click here or press Enter.");
		searchButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				search();
			}
		});

//		advancedFilterComp = new ExpandableComposite(filterCriteriaWrapper, ExpandableComposite.TWISTIE);
//		advancedFilterComp.setText("Filter Options");
//		advancedFilterComp.addExpansionListener(new IExpansionListener(){
//			@Override
//			public void expansionStateChanging(ExpansionEvent e) {
//
//			}
//			@Override
//			public void expansionStateChanged(ExpansionEvent e) {
//				filterCriteriaWrapper.layout(true);
//			}
//		});
//		Composite wrapper = new XComposite(advancedFilterComp, SWT.NONE);
		onlyCurrentBuisnessPartnerButton = new Button(filterCriteriaWrapper, SWT.CHECK);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		onlyCurrentBuisnessPartnerButton.setLayoutData(gd);
		onlyCurrentBuisnessPartnerButton.setText("Only current business partner");
		onlyCurrentBuisnessPartnerButton.setToolTipText("show only offers or delivery notes of the current selected business partner");
		onlyCurrentBuisnessPartnerButton.addSelectionListener(new SelectionAdapter(){
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				onlyCurrentBusinessPartner = onlyCurrentBuisnessPartnerButton.getSelection();
			}
		});

		typeCombo = new Combo(filterCriteriaWrapper, SWT.READ_ONLY);
		GridData comboData = new GridData(GridData.FILL_HORIZONTAL);
		comboData.horizontalSpan = 2;
		typeCombo.setLayoutData(comboData);
		typeCombo.setItems(new String[] {TYPE_OFFER, TYPE_DELIVERY_NOTE, TYPE_BOTH});
		typeCombo.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				String text = typeCombo.getText();
				articleContainerClasses.clear();
				if (text.equals(TYPE_OFFER)) {
					articleContainerClasses.add(Offer.class);
				}
				else if (text.equals(TYPE_DELIVERY_NOTE)) {
					articleContainerClasses.add(DeliveryNote.class);
				}
				else if (text.equals(TYPE_BOTH)) {
					articleContainerClasses.add(Offer.class);
					articleContainerClasses.add(DeliveryNote.class);
				}
			}
		});
		articleContainerClasses = new HashSet<Class<? extends ArticleContainer>>();
		articleContainerClasses.add(Offer.class);
		typeCombo.select(0);

		deliveryDateTable = new DeliveryDateTable(parent, SWT.NONE);
		deliveryDateTable.addDoubleClickListener(new IDoubleClickListener(){
			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (deliveryDateTable.getSelectedElements().size() == 1) {
					Object selectedObject = deliveryDateTable.getSelectedElements().iterator().next();
					if (selectedObject instanceof ArticleContainer) {
						ArticleContainer ac = (ArticleContainer) selectedObject;
						ArticleContainerID articleContainerID = (ArticleContainerID) JDOHelper.getObjectId(ac);
						try {
							RCPUtil.openEditor(new ArticleContainerEditorInput(articleContainerID), ArticleContainerEditor.ID_EDITOR);
						} catch (PartInitException e) {
							throw new RuntimeException(e);
						}
					}
				}
			}
		});
	}

	private void search()
	{
		final Display display = Display.getCurrent();
		if (display == null)
			throw new IllegalStateException("Thread mismatch! This method must be called on the UI thread!");

		final Date deliveryDate = dateTimeControl.getDate();
		Job job = new Job("Search") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception
			{
				// TODO use new DTO to optimize fetching of data
				QueryCollection<AbstractArticleContainerQuery> articleContainerQueries =
					new QueryCollection<AbstractArticleContainerQuery>(ArticleContainer.class);
				for (Class<? extends ArticleContainer> acClass : articleContainerClasses) {
					AbstractArticleContainerQuery query = getQuery(acClass);
					query.setAllFieldsDisabled();
					query.setFieldEnabled(OfferQuery.FieldName.articleDeliveryDate, true);
					query.setArticleDeliveryDate(deliveryDate);
					if (onlyCurrentBusinessPartner) {
						AnchorID selectedLegalEntityID = getSelectedLegalEntity();
						if (selectedLegalEntityID != null) {
							query.setCustomerID(selectedLegalEntityID);
						}
					}
					articleContainerQueries.add(query);
				}

				// In case both result is empty because only matching result of both criteria is returned (which is always empty)
				final Collection<?> articleContainers = ArticleContainerDAO.sharedInstance().
					getArticleContainersForQueries(articleContainerQueries, getFetchGroups(),
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
				display.asyncExec(new Runnable() {
					public void run() {
						if (deliveryDateTable.isDisposed())
							return;

						deliveryDateTable.setInput(articleContainers);
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	private String[] getFetchGroups()
	{
		return new String[] {FetchPlan.DEFAULT, ArticleContainer.FETCH_GROUP_CUSTOMER, LegalEntity.FETCH_GROUP_PERSON,
				ArticleContainer.FETCH_GROUP_ARTICLES, Article.FETCH_GROUP_END_CUSTOMER};
	}

	protected AbstractArticleContainerQuery getQuery(Class<? extends ArticleContainer> articleContainerClass) {
		if (Offer.class.isAssignableFrom(articleContainerClass)) {
			return new OfferQuery();
		}
		else if (DeliveryNote.class.isAssignableFrom(articleContainerClass)) {
			return new DeliveryNoteQuery();
		}
		return null;
	}

	protected AnchorID getSelectedLegalEntity() {
		// TODO is always null because called on non UI-thread
		IWorkbenchPage page = RCPUtil.getActiveWorkbenchPage();
		if (page != null) {
			IViewPart viewPart = page.findView(LegalEntityEditorView.ID_VIEW);
			if (viewPart != null && viewPart instanceof LegalEntityEditorView) {
				LegalEntityEditorView legalEntityEditorView = (LegalEntityEditorView) viewPart;
				LegalEntity selectedLegalEntity = legalEntityEditorView.getSelectedLegalEntity();
				if (selectedLegalEntity != null) {
					return (AnchorID) JDOHelper.getObjectId(selectedLegalEntity);
				}
			}
		}
		return null;
	}
}
