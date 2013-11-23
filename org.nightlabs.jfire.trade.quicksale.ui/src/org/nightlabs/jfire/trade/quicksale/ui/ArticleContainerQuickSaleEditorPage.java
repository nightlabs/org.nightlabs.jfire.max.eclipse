package org.nightlabs.jfire.trade.quicksale.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.TradeManagerRemote;
import org.nightlabs.jfire.trade.config.TradeConfigModule;
import org.nightlabs.jfire.trade.dao.ArticleDAO;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.id.SegmentTypeID;
import org.nightlabs.jfire.trade.quicksale.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ActiveSegmentEditSelectionEvent;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ActiveSegmentEditSelectionListener;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleChangeEvent;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleChangeListener;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorPage;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleCreateEvent;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleCreateListener;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleSelection;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleStatusCheckResult;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ClientArticleSegmentGroupSet;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEdit;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEditArticleSelectionEvent;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEditArticleSelectionListener;
import org.nightlabs.jfire.trade.ui.legalentity.view.LegalEntityEditorView;
import org.nightlabs.jfire.trade.ui.legalentity.view.SelectAnonymousViewAction;
import org.nightlabs.jfire.trade.ui.reserve.ReservationPaymentDeliveryWizard;
import org.nightlabs.jfire.trade.ui.reserve.ReservationWizardDialog;
import org.nightlabs.jfire.trade.ui.transfer.wizard.AbstractCombiTransferWizard;
import org.nightlabs.jfire.trade.ui.transfer.wizard.CombiTransferArticleContainerWizard;
import org.nightlabs.jfire.trade.ui.transfer.wizard.QuickSaleErrorHandler;
import org.nightlabs.jfire.trade.ui.transfer.wizard.TransferWizard;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ArticleContainerQuickSaleEditorPage
extends ArticleContainerEditorPage
{
	private static final Logger logger = LoggerFactory.getLogger(ArticleContainerQuickSaleEditorPage.class);

	public static class Factory implements IEntityEditorPageFactory {
		@Override
		public IFormPage createPage(FormEditor formEditor) {
			return new ArticleContainerQuickSaleEditorPage(formEditor);
		}
		@Override
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new EntityEditorPageController(editor) {
				@Override
				public void doLoad(ProgressMonitor monitor) {
				}
				@Override
				public boolean doSave(ProgressMonitor monitor) {
					return true;
				}
			};
		}
	}

	private XComposite wrapper;
	private Composite buttonComp;
	private Button okButtonCustomer;
	private Button okButtonAnonymous;
	private Button deleteSelectionButton;
	private Text customerSearchText;
	private Button deleteAllButton;
//	private Button reverseButton;

	/**
	 */
	public ArticleContainerQuickSaleEditorPage(FormEditor editor) {
		super(editor);
	}

	@Override
	protected Composite createComposite(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		createComposite(wrapper, ((ArticleContainerEditorInput)getEditorInput()).getArticleContainerID());
		return wrapper;
	}

	protected void createComposite(Composite parent, ArticleContainerID articleContainerID) {
//		getArticleContainerEdit().setShowHeader(false);
		getArticleContainerEdit().createComposite(parent);
		getArticleContainerEdit().setShowHeader(false);
		XComposite wrapper = new XComposite(parent, SWT.NONE, LayoutDataMode.GRID_DATA_HORIZONTAL);

		buttonComp = new XComposite(wrapper, SWT.NONE);
		buttonComp.setLayout(new GridLayout(8, false));
		buttonComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		deleteAllButton = new Button(buttonComp, SWT.FLAT);
		deleteAllButton.setText(Messages.getString("org.nightlabs.jfire.trade.quicksale.ui.ArticleContainerQuickSaleEditorPage.deleteAllButton.text")); //$NON-NLS-1$
		deleteAllButton.setImage(SharedImages.DELETE_16x16.createImage());
		deleteAllButton.addSelectionListener(deleteAllListener);

		deleteSelectionButton = new Button(buttonComp, SWT.FLAT);
		deleteSelectionButton.setText(Messages.getString("org.nightlabs.jfire.trade.quicksale.ui.ArticleContainerQuickSaleEditorPage.button.deleteSelection.text")); //$NON-NLS-1$
		deleteSelectionButton.setImage(SharedImages.DELETE_16x16.createImage());
		deleteSelectionButton.addSelectionListener(deleteSelectionListener);
		deleteSelectionButton.setEnabled(false);

		// need to add listeners for activeSegmentEdit by this listener, because at this time activeSegementEdit is null
		getArticleContainerEdit().addActiveSegmentEditSelectionListener(new ActiveSegmentEditSelectionListener(){
			@Override
			public void selected(ActiveSegmentEditSelectionEvent event) {
				// add listener to check for articleSelection to set enable state for deleteSelectionButton
				event.getActiveSegmentEdit().addSegmentEditArticleSelectionListener(segmentEditArticleSelectionListener);
			}
		});

		Label spacerLabel = new Label(buttonComp, SWT.NONE);
		spacerLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label customerSearchLabel = new Label(buttonComp, SWT.NONE);
		customerSearchLabel.setText(Messages.getString("org.nightlabs.jfire.trade.quicksale.ui.ArticleContainerQuickSaleEditorPage.customerSearchLabel.text")); //$NON-NLS-1$
		customerSearchText = new Text(buttonComp, wrapper.getBorderStyle());
		GridData textData = new GridData();
		textData.widthHint = 100;
		textData.heightHint = 15;
		textData.minimumWidth = 100;
		customerSearchText.setLayoutData(textData);
		customerSearchText.addSelectionListener(okListenerCustomer);

		okButtonCustomer = new Button(buttonComp, SWT.FLAT);
		okButtonCustomer.setText(Messages.getString("org.nightlabs.jfire.trade.quicksale.ui.ArticleContainerQuickSaleEditorPage.okButtonCustomer.text")); //$NON-NLS-1$
		okButtonCustomer.setImage(SharedImages.getSharedImage(TradePlugin.getDefault(), LegalEntityEditorView.class));
		okButtonCustomer.addSelectionListener(okListenerCustomer);

//		Label separator = new Label(buttonComp, SWT.SEPARATOR);

		okButtonAnonymous = new Button(buttonComp, SWT.FLAT);
		okButtonAnonymous.setText(Messages.getString("org.nightlabs.jfire.trade.quicksale.ui.ArticleContainerQuickSaleEditorPage.okButtonAnonymous.text")); //$NON-NLS-1$
		okButtonAnonymous.setImage(SharedImages.getSharedImage(TradePlugin.getDefault(), SelectAnonymousViewAction.class));
		okButtonAnonymous.addSelectionListener(okListenerAnonymous);

		getArticleContainerEdit().addArticleChangeListener(articleChangeListener);
		getArticleContainerEdit().addArticleCreateListener(articleCreateListener);

		buttonComp.setEnabled(false);
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
				// we check if all articles are non-allocated, allocated or reversed, only then they can be removed
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
			OrderID orderID = (OrderID) getArticleContainerEdit().getArticleContainerID();
//			CustomerPaymentDeliveryWizard wiz = new CustomerPaymentDeliveryWizard(
			ReservationPaymentDeliveryWizard wiz = new ReservationPaymentDeliveryWizard(
					text,
					orderID,
					AbstractCombiTransferWizard.TRANSFER_MODE_BOTH,
					TransferWizard.Side.Vendor);
//			DynamicPathWizardDialog dlg = new DynamicPathWizardDialog(wiz);
			DynamicPathWizardDialog dlg = new ReservationWizardDialog(wiz);
			int returnCode = dlg.open();
			boolean transferSuccessful = wiz.isTransfersSuccessful();
			if (logger.isDebugEnabled()) {
				logger.debug("ReservationWizardDialog OrderID = "+orderID); //$NON-NLS-1$
				logger.debug("ReservationWizardDialog returnCode = "+returnCode); //$NON-NLS-1$
				logger.debug("ReservationWizardDialog.isTransfersSuccessful() = "+transferSuccessful); //$NON-NLS-1$
			}
			if (returnCode == Dialog.OK && transferSuccessful) {
				createNewOrder();
			}
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
		@Override
		public void widgetSelected(SelectionEvent e) {
			deleteAll();
		}
	};

	private SelectionListener deleteSelectionListener = new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent e) {
			deleteSelection();
		}
	};

	public static ArticleContainerEditorInput createEditorInput()
	{
		TradeManagerRemote tm;
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

				tm = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
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
					return new ArticleContainerEditorInput(orderID);
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

	private void checkAllArticlesAreAllocatedOrReversed(Collection<? extends Article> deletedArticles)
	{
		ArticleStatusCheckResult articleStatusCheckResult = new ArticleStatusCheckResult(getArticleContainerEdit().getArticles());

		articlesWithWrongState.addAll(articleStatusCheckResult.getNotAllocatedNorReversedArticles());
		articlesWithWrongState.removeAll(articleStatusCheckResult.getAllocatedOrReversedArticles());

		if (deletedArticles != null)
			articlesWithWrongState.removeAll(deletedArticles);

		if (buttonComp != null && !buttonComp.isDisposed())
			buttonComp.setEnabled(articlesWithWrongState.isEmpty());
	}

	private ArticleCreateListener articleCreateListener = new ArticleCreateListener(){
		public void articlesCreated(ArticleCreateEvent articleCreateEvent) {
			checkAllArticlesAreAllocatedOrReversed(null);
		}
	};

	private ArticleChangeListener articleChangeListener = new ArticleChangeListener(){
		public void articlesChanged(ArticleChangeEvent articleChangeEvent) {
			checkAllArticlesAreAllocatedOrReversed(articleChangeEvent.getDeletedArticles());
		}
	};

	protected void assignToCustomer(LegalEntity legalEntity)
	{
		TradeManagerRemote tm = TradePlugin.getDefault().getTradeManager();
		AnchorID customerID = (AnchorID) JDOHelper.getObjectId(legalEntity);
		OrderID orderID = (OrderID) getArticleContainerEdit().getArticleContainerID();
		try {
			tm.assignCustomer(orderID, customerID, true, null, 1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected boolean payAndDeliverAll()
	{
		CombiTransferArticleContainerWizard wizard = new CombiTransferArticleContainerWizard(
				getArticleContainerEdit().getArticleContainerID(),
				AbstractCombiTransferWizard.TRANSFER_MODE_BOTH);
		wizard.setErrorHandler(new QuickSaleErrorHandler());
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
		int returnCode = dialog.open();
		if (returnCode == Window.CANCEL)
			return false;
		return wizard.isTransfersSuccessful();
	}

	protected void deleteAll()
	{
		try {
//			TradeManager tradeManager = TradePlugin.getDefault().getTradeManager();
//			Collection<Article> articles = tradeManager.releaseArticles(NLJDOHelper.getObjectIDSet(
//					getArticleContainerEdit().getArticles()), true, false, null,
//					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//			Set<ObjectID> articleIDs = NLJDOHelper.getObjectIDSet(getArticleContainerEdit().getArticles());
//			tradeManager.deleteArticles(articleIDs, true);
			// I think this is not necessary, because the articles are deleted automatically via the EditLock-release-hook

			createNewOrder();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void deleteSelection()
	{
		// We remove the lines from the server - therefore find out first, what lines shall be handled here.
		SegmentEdit segmentEdit = getArticleContainerEdit().getActiveSegmentEdit();
		Set<? extends ArticleSelection> articleSelections = segmentEdit.getArticleSelections();

		if (!articleSelections.isEmpty())
		{
			Set<ArticleID> articleIDs = new HashSet<ArticleID>();
			for (ArticleSelection articleSelection : articleSelections) {
				for (Article article : articleSelection.getSelectedArticles()) {
					articleIDs.add((ArticleID) JDOHelper.getObjectId(article));
				}
			}

			final Composite composite = getArticleContainerEdit().getActiveSegmentEdit().getComposite();
//			Composite composite = articleContainerEdit;
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
						getArticleContainerEdit().removeArticleChangeListener(this);
					}
				};
				getArticleContainerEdit().addArticleChangeListener(articleChangeListener);

//				TradeManager tradeManager = TradePlugin.getDefault().getTradeManager();
//				Collection<Article> articles = tradeManager.releaseArticles(articleIDs,
//						true, false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//				tradeManager.deleteArticles(articleIDs, true);

				ClientArticleSegmentGroupSet clientArticleSegmentGroupSet = getArticleContainerEdit().getArticleSegmentGroupSet();

				List<Article> articles = ArticleDAO.sharedInstance().deleteArticles(
						articleIDs, true, true, clientArticleSegmentGroupSet.getFetchGroupsArticle(), clientArticleSegmentGroupSet.getMaxFetchDepthArticle(),
						new NullProgressMonitor()
				);
				Set<ArticleID> deletedArticleIDs = new HashSet<ArticleID>(articleIDs);
				for (Article article : articles) {
					deletedArticleIDs.remove(JDOHelper.getObjectId(article));
				}

				clientArticleSegmentGroupSet.updateArticles(deletedArticleIDs, articles);
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

	protected void createNewOrder()
	{
		// only close, open will occur automatically because of partListener in ArticleContainerQuickSaleEditor
		RCPUtil.closeEditor(getEditorInput(), false);
	}
}
