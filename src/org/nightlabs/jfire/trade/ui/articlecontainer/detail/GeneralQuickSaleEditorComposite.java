package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import java.util.Collection;
import java.util.Set;

import javax.jdo.JDOHelper;
import javax.security.auth.login.LoginException;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
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
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.id.SegmentTypeID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.order.GeneralEditorInputOrder;
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
public class GeneralQuickSaleEditorComposite
extends XComposite
{
	private IWorkbenchPartSite site;
	
	/**
	 * @param site
	 * @param parent
	 * @param input
	 */
	public GeneralQuickSaleEditorComposite(IWorkbenchPartSite site,
			Composite parent, GeneralEditorInput input)
	{
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		this.site = site;
		createComposite(this, input);
	}
	
	private Composite buttonComp;
	protected void createComposite(Composite parent, GeneralEditorInput input)
	{
		generalEditorComposite = new GeneralEditorComposite(site, parent, input);
		
		buttonComp = new XComposite(parent, SWT.NONE);
		buttonComp.setLayout(new GridLayout(6, false));
		buttonComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		deleteButton = new Button(buttonComp, SWT.NONE);
		deleteButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.GeneralQuickSaleEditorComposite.deleteButton.text")); //$NON-NLS-1$
		deleteButton.setImage(SharedImages.DELETE_16x16.createImage());
		deleteButton.addSelectionListener(deleteListener);
		
		Label spacerLabel = new Label(buttonComp, SWT.NONE);
		spacerLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label customerSearchLabel = new Label(buttonComp, SWT.NONE);
		customerSearchLabel.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.GeneralQuickSaleEditorComposite.customerSearchLabel.text")); //$NON-NLS-1$
		customerSearchText = new Text(buttonComp, SWT.BORDER);
		GridData textData = new GridData();
		textData.widthHint = 100;
		textData.heightHint = 15;
		textData.minimumWidth = 100;
		customerSearchText.setLayoutData(textData);
		customerSearchText.addSelectionListener(okListenerCustomer);
		
		okButtonCustomer = new Button(buttonComp, SWT.NONE);
		okButtonCustomer.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.GeneralQuickSaleEditorComposite.okButtonCustomer.text")); //$NON-NLS-1$
		okButtonCustomer.setImage(SharedImages.getSharedImage(TradePlugin.getDefault(),
				LegalEntityEditorView.class));
		
//		Label separator = new Label(buttonComp, SWT.SEPARATOR);
		
		okButtonCustomer.addSelectionListener(okListenerCustomer);
		okButtonAnonymous = new Button(buttonComp, SWT.NONE);
		okButtonAnonymous.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.GeneralQuickSaleEditorComposite.okButtonAnonymous.text")); //$NON-NLS-1$
		okButtonAnonymous.setImage(SharedImages.getSharedImage(TradePlugin.getDefault(),
				SelectAnonymousViewAction.class));
		okButtonAnonymous.addSelectionListener(okListenerAnonymous);
				
		generalEditorComposite.addArticleChangeListener(articleChangeListener);
		generalEditorComposite.addArticleCreateListener(articleCreateListener);
		
		buttonComp.setEnabled(false);
	}
	
	private GeneralEditorComposite generalEditorComposite;
	public GeneralEditorComposite getGeneralEditorComposite() {
		return generalEditorComposite;
	}
	
	private Button okButtonCustomer;
	private Button okButtonAnonymous;
	private Button deleteButton;
	private Text customerSearchText;
	
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
					(OrderID) getGeneralEditorComposite().getArticleContainerID(),
					AbstractCombiTransferWizard.TRANSFER_MODE_BOTH,
					TransferWizard.Side.Vendor);
			new DynamicPathWizardDialog(wiz).open();
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
	
	private SelectionListener deleteListener = new SelectionListener(){
		public void widgetSelected(SelectionEvent e) {
			deleteAll();
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};
	
	public static GeneralEditorInputOrder createEditorInput()
	{
		// FIXME: add method in server which holds an order
		TradeManager tm;
		try {
			try {
				Login.getLogin();
				tm = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//			 TODO where do we get the currency from? User prefs?
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
							"EUR", //$NON-NLS-1$
							new SegmentTypeID[] {null}); // null here is a shortcut for default segment type
					return new GeneralEditorInputOrder(orderID);
			} catch (LoginException le) {
				return null;
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	private ArticleCreateListener articleCreateListener = new ArticleCreateListener(){
		public void articlesCreated(ArticleCreateEvent articleCreateEvent) {
			if (buttonComp != null && !buttonComp.isDisposed())
				buttonComp.setEnabled(true);
		}
	};
	
	private ArticleChangeListener articleChangeListener = new ArticleChangeListener(){
		public void articlesChanged(ArticleChangeEvent articleChangeEvent) {
			// TODO: check if order is empty
		}
	};
	
	protected void assignToCustomer(LegalEntity legalEntity)
	{
		TradeManager tm = TradePlugin.getDefault().getTradeManager();
		AnchorID customerID = (AnchorID) JDOHelper.getObjectId(legalEntity);
		OrderID orderID = (OrderID) getGeneralEditorComposite().getArticleContainerID();
		try {
			tm.assignCustomer(orderID, customerID, true, null, 1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected boolean payAndDeliverAll()
	{
		CombiTransferArticleContainerWizard wizard = new CombiTransferArticleContainerWizard(
				getGeneralEditorComposite().getArticleContainerID(),
				AbstractCombiTransferWizard.TRANSFER_MODE_BOTH,
				TransferWizard.Side.Vendor); // TODO it's not always vendor!
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
					getGeneralEditorComposite().getArticles()), true, false, null,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
			Set<ObjectID> articleIDs = NLJDOHelper.getObjectIDSet(getGeneralEditorComposite().getArticles());
			tradeManager.deleteArticles(articleIDs, true);
			
			createNewOrder();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected void createGeneralEditorComposite(GeneralEditorInput input)
	{
		generalEditorComposite.dispose();
		generalEditorComposite = new GeneralEditorComposite(site, this, input);
		layout(true, true);
	}
	
	protected void createNewOrder()
	{
		// only close open will occur automaticly because of partListener in GeneralQuickSaleEditor
		RCPUtil.closeEditor(getGeneralEditorComposite().getInput(), false);
	}
}
