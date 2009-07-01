package org.nightlabs.jfire.trade.ui.articlecontainer.deliverydate;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.Form;
import org.nightlabs.base.ui.composite.DateTimeControl;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.notification.NotificationAdapterSWTThreadAsync;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.toolkit.IToolkit;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.dao.ArticleContainerDAO;
import org.nightlabs.jfire.trade.deliverydate.ArticleContainerDeliveryDateDTO;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuery;
import org.nightlabs.jfire.trade.query.DeliveryNoteQuery;
import org.nightlabs.jfire.trade.query.OfferQuery;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
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
	private AnchorID selectedLegalEntityID;
//	private ExpandableComposite advancedFilterComp;

	private static final String TYPE_OFFER = Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.deliverydate.DeliveryDateComposite.type.offer"); //$NON-NLS-1$
	private static final String TYPE_DELIVERY_NOTE = Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.deliverydate.DeliveryDateComposite.type.deliverynote"); //$NON-NLS-1$
	private static final String TYPE_BOTH = Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.deliverydate.DeliveryDateComposite.type.both"); //$NON-NLS-1$

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
//		final Composite filterCriteriaWrapper = new XComposite(parent, SWT.NONE, LayoutMode.ORDINARY_WRAPPER,
//				LayoutDataMode.GRID_DATA_HORIZONTAL, 3);
		IToolkit toolkit = getToolkit(true);
		Form form = toolkit.createForm(parent);
		form.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Composite filterCriteriaWrapper = form.getBody();
		filterCriteriaWrapper.setLayout(new GridLayout(3, false));
		filterCriteriaWrapper.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

//		Label label = new Label(filterCriteriaWrapper, SWT.NONE);
		Label label = toolkit.createLabel(filterCriteriaWrapper, "", SWT.NONE); //$NON-NLS-1$
		label.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.deliverydate.DeliveryDateComposite.label.deliverydate.text")); //$NON-NLS-1$
		label.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.deliverydate.DeliveryDateComposite.label.deliverydate.tooltip")); //$NON-NLS-1$
		dateTimeControl = new DateTimeControl(filterCriteriaWrapper, true, SWT.NONE, DateFormatter.FLAGS_DATE_SHORT);
		toolkit.adapt(dateTimeControl);
		dateTimeControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		dateTimeControl.addSelectionListener(new SelectionAdapter(){
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				search();
			}
		});
//		Button searchButton = new Button(filterCriteriaWrapper, SWT.FLAT);
		Button searchButton = toolkit.createButton(filterCriteriaWrapper, "", SWT.FLAT); //$NON-NLS-1$
		searchButton.setImage(SharedImages.SEARCH_16x16.createImage());
		searchButton.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.deliverydate.DeliveryDateComposite.button.search.tooltip")); //$NON-NLS-1$
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
//		onlyCurrentBuisnessPartnerButton = new Button(filterCriteriaWrapper, SWT.CHECK);
		onlyCurrentBuisnessPartnerButton = toolkit.createButton(filterCriteriaWrapper, "", SWT.CHECK); //$NON-NLS-1$
		onlyCurrentBuisnessPartnerButton.setSelection(onlyCurrentBusinessPartner);
		onlyCurrentBuisnessPartnerButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.deliverydate.DeliveryDateComposite.button.onlyCurrentBusinessPartner.text")); //$NON-NLS-1$
		onlyCurrentBuisnessPartnerButton.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.deliverydate.DeliveryDateComposite.button.onlyCurrentBusinessPartner.tooltip")); //$NON-NLS-1$
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
		toolkit.adapt(typeCombo);
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
					if (selectedObject instanceof ArticleContainerDeliveryDateDTO) {
						ArticleContainerDeliveryDateDTO dto = (ArticleContainerDeliveryDateDTO) selectedObject;
						ArticleContainerID articleContainerID = dto.getArticleContainerID();
						try {
							RCPUtil.openEditor(new ArticleContainerEditorInput(articleContainerID), ArticleContainerEditor.ID_EDITOR);
						} catch (PartInitException e) {
							throw new RuntimeException(e);
						}
					}
				}
			}
		});

		SelectionManager.sharedInstance().addNotificationListener(LegalEntity.class, selectionListener);
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				SelectionManager.sharedInstance().removeNotificationListener(LegalEntity.class, selectionListener);
			}
		});

		adaptToToolkit();
	}

	private NotificationListener selectionListener = new NotificationAdapterSWTThreadAsync() {
		@Override
		public void notify(NotificationEvent notificationEvent) {
			AnchorID legalEntityID = (AnchorID) notificationEvent.getFirstSubject();
			selectedLegalEntityID = legalEntityID;
			if (onlyCurrentBusinessPartner) {
				search();
			}
		}
	};

	private void search()
	{
		final Display display = Display.getCurrent();
		if (display == null)
			throw new IllegalStateException("Thread mismatch! This method must be called on the UI thread!"); //$NON-NLS-1$

		deliveryDateTable.setLoadingMessage(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.deliverydate.DeliveryDateComposite.loading.message")); //$NON-NLS-1$

		final Date deliveryDate = dateTimeControl.getDate();
		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.deliverydate.DeliveryDateComposite.job.search.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception
			{
				if (!isDisposed()){
					QueryCollection<AbstractArticleContainerQuery> articleContainerQueries =
						new QueryCollection<AbstractArticleContainerQuery>(ArticleContainer.class);
					for (Class<? extends ArticleContainer> acClass : articleContainerClasses) {
						AbstractArticleContainerQuery query = getQuery(acClass);
						query.setAllFieldsDisabled();
						query.setFieldEnabled(AbstractArticleContainerQuery.FieldName.articleDeliveryDate, true);
						query.setArticleDeliveryDate(deliveryDate);
						if (onlyCurrentBusinessPartner) {
							if (selectedLegalEntityID != null) {
								query.setFieldEnabled(AbstractArticleContainerQuery.FieldName.customerID, true);
								query.setCustomerID(selectedLegalEntityID);
							}
						}
						articleContainerQueries.add(query);
					}

//					final Collection<?> articleContainers = ArticleContainerDAO.sharedInstance().
//						getArticleContainersForQueries(articleContainerQueries, getFetchGroups(),
//							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
					final Collection<ArticleContainerDeliveryDateDTO> dtos = ArticleContainerDAO.sharedInstance().getArticleContainerDeliveryDateDTOs(
							articleContainerQueries, getFetchGroups(), NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
					if (!display.isDisposed()) {
						display.asyncExec(new Runnable() {
							public void run() {
								if (deliveryDateTable.isDisposed())
									return;

								deliveryDateTable.setInput(dtos);
							}
						});
					}
				}
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

	private AbstractArticleContainerQuery getQuery(Class<? extends ArticleContainer> articleContainerClass) {
		if (Offer.class.isAssignableFrom(articleContainerClass)) {
			return new OfferQuery();
		}
		else if (DeliveryNote.class.isAssignableFrom(articleContainerClass)) {
			return new DeliveryNoteQuery();
		}
		return null;
	}

}
