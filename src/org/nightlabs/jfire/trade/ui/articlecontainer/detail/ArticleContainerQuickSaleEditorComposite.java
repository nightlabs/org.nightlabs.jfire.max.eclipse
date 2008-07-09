package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.security.auth.login.LoginException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPartSite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.config.TradeConfigModule;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.id.SegmentTypeID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.order.ArticleContainerEditorInputOrder;
import org.nightlabs.jfire.trade.ui.legalentity.view.LegalEntityEditorView;
import org.nightlabs.jfire.trade.ui.legalentity.view.SelectAnonymousViewAction;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.transfer.wizard.AbstractCombiTransferWizard;
import org.nightlabs.jfire.trade.ui.transfer.wizard.CombiTransferArticleContainerWizard;
import org.nightlabs.jfire.trade.ui.transfer.wizard.TransferWizard;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ArticleContainerQuickSaleEditorComposite
extends XComposite
{	
	private IWorkbenchPartSite site;
	private Composite buttonComp;
	private Button okButtonCustomer;
	private Button okButtonAnonymous;
	private Button deleteAllButton;
	private Button deleteSelectionButton;
	private Text customerSearchText;
	private ArticleContainerEditorComposite articleContainerEditorComposite;
	
	/**
	 * @param site
	 * @param parent
	 * @param input
	 */
	public ArticleContainerQuickSaleEditorComposite(IWorkbenchPartSite site,
			Composite parent, ArticleContainerEditorInput input)
	{
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		this.site = site;
		createComposite(this, input);
	}
	
	protected void createComposite(Composite parent, ArticleContainerEditorInput input)
	{
		articleContainerEditorComposite = new ArticleContainerEditorComposite(site, parent, input);
		
		buttonComp = new XComposite(parent, SWT.NONE);
		buttonComp.setLayout(new GridLayout(7, false));
		buttonComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		deleteAllButton = new Button(buttonComp, SWT.NONE);
		deleteAllButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerQuickSaleEditorComposite.deleteAllButton.text")); //$NON-NLS-1$
		deleteAllButton.setImage(SharedImages.DELETE_16x16.createImage());
		deleteAllButton.addSelectionListener(deleteAllListener);

		deleteSelectionButton = new Button(buttonComp, SWT.NONE);
		deleteSelectionButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerQuickSaleEditorComposite.button.deleteSelection.text")); //$NON-NLS-1$
		deleteSelectionButton.setImage(SharedImages.DELETE_16x16.createImage());
		deleteSelectionButton.addSelectionListener(deleteSelectionListener);
		deleteSelectionButton.setEnabled(false);
		
		// need to add listeners for activeSegmentEdit by this listener, because at this time activeSegementEdit is null
		articleContainerEditorComposite.addActiveSegmentEditSelectionListener(new ActiveSegmentEditSelectionListener(){
			@Override
			public void selected(ActiveSegmentEditSelectionEvent event) {
				// add listener to check for articleSelection to set enable state for deleteSelectionButton
				event.getActiveSegmentEdit().addSegmentEditArticleSelectionListener(segmentEditArticleSelectionListener);				
			}
		});
		
		Label spacerLabel = new Label(buttonComp, SWT.NONE);
		spacerLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label customerSearchLabel = new Label(buttonComp, SWT.NONE);
		customerSearchLabel.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerQuickSaleEditorComposite.customerSearchLabel.text")); //$NON-NLS-1$
		customerSearchText = new Text(buttonComp, SWT.BORDER);
		GridData textData = new GridData();
		textData.widthHint = 100;
		textData.heightHint = 15;
		textData.minimumWidth = 100;
		customerSearchText.setLayoutData(textData);
		customerSearchText.addSelectionListener(okListenerCustomer);
		
		okButtonCustomer = new Button(buttonComp, SWT.NONE);
		okButtonCustomer.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerQuickSaleEditorComposite.okButtonCustomer.text")); //$NON-NLS-1$
		okButtonCustomer.setImage(SharedImages.getSharedImage(TradePlugin.getDefault(), LegalEntityEditorView.class));
		okButtonCustomer.addSelectionListener(okListenerCustomer);
		
//		Label separator = new Label(buttonComp, SWT.SEPARATOR);
		
		okButtonAnonymous = new Button(buttonComp, SWT.NONE);
		okButtonAnonymous.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerQuickSaleEditorComposite.okButtonAnonymous.text")); //$NON-NLS-1$
		okButtonAnonymous.setImage(SharedImages.getSharedImage(TradePlugin.getDefault(), SelectAnonymousViewAction.class));
		okButtonAnonymous.addSelectionListener(okListenerAnonymous);

		articleContainerEditorComposite.addArticleChangeListener(articleChangeListener);
		articleContainerEditorComposite.addArticleCreateListener(articleCreateListener);

		buttonComp.setEnabled(false);
	}
	
	public ArticleContainerEditorComposite getArticleContainerEditorComposite() {
		return articleContainerEditorComposite;
	}

	/**
	 * Returns a Set of all {@link ArticleID} of those {@link Article}s which do not have the allocated or reversed status
	 * form the given Set of {@link ArticleSelection}s.
	 * 
	 * @param selections a Set of {@link ArticleSelection} which should be checked
	 * @return a Set of all {@link ArticleID} of those {@link Article}s which do not have the allocated or reversed status
	 */
	protected ArticleStatusCheckResult getArticleStatusCheckResult(Set<ArticleSelection> selections) {
		return new ArticleStatusCheckResult(ArticleSelection.getSelectedArticles(selections));
	}
	
	private SegmentEditArticleSelectionListener segmentEditArticleSelectionListener = new SegmentEditArticleSelectionListener() {
		public void selected(SegmentEditArticleSelectionEvent event) {
			if (!event.getArticleSelections().isEmpty()) {
				// we check if all articles are allocated or reversed, only then they can be removed
				ArticleStatusCheckResult articleStatusCheckResult = getArticleStatusCheckResult(event.getArticleSelections());
				deleteSelectionButton.setEnabled(articleStatusCheckResult.isAllArticlesAllocatedOrReversed());
			}
			else {
				deleteSelectionButton.setEnabled(false);
			}			
		}
	};	
	
	private SelectionListener okListenerCustomer = new SelectionListener(){
		public void widgetSelected(SelectionEvent e) {
			String text = customerSearchText.getText();
//			LegalEntity legalEntity = LegalEntitySearchCreateWizard.open(text, true);
//			if (legalEntity != null) {
//				assignToCustomer(legalEntity);
//				if (payAndDeliverAll()) {
//					createNewOrder();
//				}
//			}
			CustomerPaymentDeliveryWizard wiz = new CustomerPaymentDeliveryWizard(
					text,
					(OrderID) getArticleContainerEditorComposite().getArticleContainerID(),
					AbstractCombiTransferWizard.TRANSFER_MODE_BOTH,
					TransferWizard.Side.Vendor);
			if (new DynamicPathWizardDialog(wiz).open() == Dialog.OK)
				createNewOrder();
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};

	private SelectionListener okListenerAnonymous = new SelectionListener(){
		public void widgetSelected(SelectionEvent e) {
			LegalEntity legalEntity = LegalEntityDAO.sharedInstance().getAnonymousLegalEntity(
				new NullProgressMonitor()
			);
			assignToCustomer(legalEntity);
			if (payAndDeliverAll()) {
				createNewOrder();
			}
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};
	
	private SelectionListener deleteAllListener = new SelectionAdapter(){
		public void widgetSelected(SelectionEvent e) {
			deleteAll();
		}
	};

	private SelectionListener deleteSelectionListener = new SelectionAdapter(){
		public void widgetSelected(SelectionEvent e) {
			deleteSelection();
		}
	};
		
	public static ArticleContainerEditorInputOrder createEditorInput()
	{
		TradeManager tm;
		try {
			try {
				Login.getLogin();

				TradeConfigModule tradeConfigModule = ConfigUtil.getUserCfMod(
						TradeConfigModule.class,
						new String[] {
							FetchPlan.DEFAULT,
							TradeConfigModule.FETCH_GROUP_CURRENCY,
						},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new NullProgressMonitor()); // TODO async!

				tm = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
					// by default get the customerID of the anonymous customer
					AnchorID customerID = (AnchorID) JDOHelper.getObjectId(
						LegalEntityDAO.sharedInstance().getAnonymousLegalEntity(
							new NullProgressMonitor()
						)
					);
//			 FIXME IDPREFIX (next line) should be asked from user if necessary!
					OrderID orderID = tm.createQuickSaleWorkOrder(
							customerID,
							null,
							tradeConfigModule.getCurrencyID(),
							new SegmentTypeID[] {null}); // null here is a shortcut for default segment type
					return new ArticleContainerEditorInputOrder(orderID);
			} catch (LoginException le) {
				return null;
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

//	private ArticleCreateListener articleCreateListener = new ArticleCreateListener(){
//		public void articlesCreated(ArticleCreateEvent articleCreateEvent) { 
//			if (buttonComp != null && !buttonComp.isDisposed())
//				buttonComp.setEnabled(true);
//		}
//	};

	private Set<Article> articlesWithWrongState = new HashSet<Article>();
	
	private void checkAllArticlesAreAllocatedOrReversed() 
	{
		ArticleStatusCheckResult articleStatusCheckResult = new ArticleStatusCheckResult(articleContainerEditorComposite.getArticles());			
		
		if (!articleStatusCheckResult.getNotAllocatedNorReversedArticles().isEmpty())
			articlesWithWrongState.addAll(articleStatusCheckResult.getNotAllocatedNorReversedArticles());
		
		if (!articleStatusCheckResult.getAllocatedOrReversedArticles().isEmpty())
			articlesWithWrongState.removeAll(articleStatusCheckResult.getAllocatedOrReversedArticles());
		
		if (buttonComp != null && !buttonComp.isDisposed())
			buttonComp.setEnabled(articlesWithWrongState.isEmpty());		
	}
	
	private ArticleCreateListener articleCreateListener = new ArticleCreateListener(){
		public void articlesCreated(ArticleCreateEvent articleCreateEvent) {
			checkAllArticlesAreAllocatedOrReversed();
		}
	};
	
	private ArticleChangeListener articleChangeListener = new ArticleChangeListener(){
		public void articlesChanged(ArticleChangeEvent articleChangeEvent) {
			checkAllArticlesAreAllocatedOrReversed();
		}
	};
	
	protected void assignToCustomer(LegalEntity legalEntity)
	{
		TradeManager tm = TradePlugin.getDefault().getTradeManager();
		AnchorID customerID = (AnchorID) JDOHelper.getObjectId(legalEntity);
		OrderID orderID = (OrderID) getArticleContainerEditorComposite().getArticleContainerID();
		try {
			tm.assignCustomer(orderID, customerID, true, null, 1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected boolean payAndDeliverAll()
	{
		CombiTransferArticleContainerWizard wizard = new CombiTransferArticleContainerWizard(
				getArticleContainerEditorComposite().getArticleContainerID(),
				AbstractCombiTransferWizard.TRANSFER_MODE_BOTH);
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
		int returnCode = dialog.open();
		if (returnCode == Window.CANCEL)
			return false;
		return true;
	}
	
	protected void deleteAll()
	{
		try {
			TradeManager tradeManager = TradePlugin.getDefault().getTradeManager();
			Collection<Article> articles = tradeManager.releaseArticles(NLJDOHelper.getObjectIDSet(
					getArticleContainerEditorComposite().getArticles()), true, false, null,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
			Set<ObjectID> articleIDs = NLJDOHelper.getObjectIDSet(getArticleContainerEditorComposite().getArticles());
			tradeManager.deleteArticles(articleIDs, true);
			
			createNewOrder();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void deleteSelection()
	{
		// We remove the lines from the server - therefore find out first, what lines shall be handled here.
		SegmentEdit segmentEdit = articleContainerEditorComposite.getActiveSegmentEdit();
		Set<? extends ArticleSelection> articleSelections = segmentEdit.getArticleSelections();

		if (!articleSelections.isEmpty())
		{
			Set<ArticleID> articleIDs = new HashSet<ArticleID>();
			for (ArticleSelection articleSelection : articleSelections) {
				for (Article article : articleSelection.getSelectedArticles()) {
					articleIDs.add((ArticleID) JDOHelper.getObjectId(article));
				}
			}

			final Composite composite = articleContainerEditorComposite.getActiveSegmentEdit().getComposite();
//			Composite composite = articleContainerEditorComposite;
			try {
				if (!composite.isDisposed()) {
					composite.setEnabled(false);
					buttonComp.setEnabled(false);
				}
				ArticleChangeListener articleChangeListener = new ArticleChangeListener(){
					@Override
					public void articlesChanged(ArticleChangeEvent articleChangeEvent) {
						if (!composite.isDisposed()) {
							composite.setEnabled(true);
							buttonComp.setEnabled(true);
						}
						articleContainerEditorComposite.removeArticleChangeListener(this);
					}
				};
				articleContainerEditorComposite.addArticleChangeListener(articleChangeListener);

				TradeManager tradeManager = TradePlugin.getDefault().getTradeManager();
				Collection<Article> articles = tradeManager.releaseArticles(articleIDs, 
						true, false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
				tradeManager.deleteArticles(articleIDs, true);				
			}
			catch (Exception e) {
				if (!composite.isDisposed()) {
					composite.setEnabled(true);
					buttonComp.setEnabled(true);
				}
				throw new RuntimeException(e);
			}
		}
	}
		
	protected void createArticleContainerEditorComposite(ArticleContainerEditorInput input)
	{
		articleContainerEditorComposite.dispose();
		articleContainerEditorComposite = new ArticleContainerEditorComposite(site, this, input);
		layout(true, true);
	}
	
	protected void createNewOrder()
	{
		// only close, open will occur automatically because of partListener in ArticleContainerQuickSaleEditor
		RCPUtil.closeEditor(getArticleContainerEditorComposite().getInput(), false);
	}
}
