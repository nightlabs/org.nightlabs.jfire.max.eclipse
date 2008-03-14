/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://opensource.org/licenses/lgpl-license.php                         *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.trade.ui.articlecontainer.header;

import java.util.Iterator;
import java.util.LinkedList;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.nightlabs.ModuleException;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.OrganisationLegalEntity;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.GeneralEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.GeneralEditorInput;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.deliverynote.GeneralEditorInputDeliveryNote;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice.GeneralEditorInputInvoice;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.GeneralEditorInputOffer;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.order.GeneralEditorInputOrder;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.notification.NotificationAdapterWorkerThreadAsync;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.util.Util;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class HeaderTreeComposite
extends XComposite
implements ISelectionProvider
{
	private IWorkbenchPartSite site;

	private TreeViewer headerTreeViewer;
	private HeaderTreeContentProvider headerTreeContentProvider;
	private HeaderTreeLabelProvider headerTreeLabelProvider;

	protected DrillDownAdapter drillDownAdapter;

	private OrganisationLegalEntity myOrganisationLegalEntity; // representing our own organisation
	private LegalEntity partner;

	private CreateOrderAction createOrderAction;
	private CreateOfferAction createOfferAction;

	private HeaderTreeNode selectedNode = null;

	/**
	 * @param parent
	 * @param style
	 * @param setLayoutData
	 */
	public HeaderTreeComposite(Composite parent, int style, IWorkbenchPartSite site)
	throws ModuleException
	{
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
		this.site = site;

		// TODO we should use our SharedImages framework here.
		imageOrderRootTreeNode = AbstractUIPlugin.imageDescriptorFromPlugin(TradePlugin.ID_PLUGIN, "icons/articlecontainer/header/OrderRootTreeNode.16x16.png").createImage(); //$NON-NLS-1$
		imageOrderTreeNode = AbstractUIPlugin.imageDescriptorFromPlugin(TradePlugin.ID_PLUGIN, "icons/articlecontainer/header/OrderTreeNode.16x16.png").createImage(); //$NON-NLS-1$
		imageOfferTreeNode = AbstractUIPlugin.imageDescriptorFromPlugin(TradePlugin.ID_PLUGIN, "icons/articlecontainer/header/OfferTreeNode.16x16.png").createImage(); //$NON-NLS-1$
		imageInvoiceRootTreeNode = AbstractUIPlugin.imageDescriptorFromPlugin(TradePlugin.ID_PLUGIN, "icons/articlecontainer/header/InvoiceRootTreeNode.16x16.png").createImage(); //$NON-NLS-1$
		imageInvoiceTreeNode = AbstractUIPlugin.imageDescriptorFromPlugin(TradePlugin.ID_PLUGIN, "icons/articlecontainer/header/InvoiceTreeNode.16x16.png").createImage(); //$NON-NLS-1$
		imageDeliveryNoteRootTreeNode = AbstractUIPlugin.imageDescriptorFromPlugin(TradePlugin.ID_PLUGIN, "icons/articlecontainer/header/DeliveryNoteRootTreeNode.16x16.png").createImage(); //$NON-NLS-1$
		imageDeliveryNoteTreeNode = AbstractUIPlugin.imageDescriptorFromPlugin(TradePlugin.ID_PLUGIN, "icons/articlecontainer/header/DeliveryNoteTreeNode.16x16.png").createImage(); //$NON-NLS-1$

		imageCustomerRootTreeNode = AbstractUIPlugin.imageDescriptorFromPlugin(TradePlugin.ID_PLUGIN, "icons/articlecontainer/header/PurchaseRootTreeNode.16x16.png").createImage(); //$NON-NLS-1$
		imageVendorRootTreeNode = AbstractUIPlugin.imageDescriptorFromPlugin(TradePlugin.ID_PLUGIN, "icons/articlecontainer/header/SaleRootTreeNode.16x16.png").createImage(); //$NON-NLS-1$

		try {
			TradeManager tm = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			myOrganisationLegalEntity = tm.getOrganisationLegalEntity(
					Login.getLogin().getOrganisationID(), true,
					new String[]{FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

			partner = tm.getAnonymousCustomer(null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT); // new String[] {FetchPlan.ALL});
		} catch (ModuleException x) {
			throw x;
		} catch (Exception x) {
			throw new ModuleException(x);
		}

		// set up the table tree for our order-/invoice-/delivery-headers
		headerTreeViewer = new TreeViewer(this, SWT.NONE);
		headerTreeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		headerTreeViewer.getTree().setLayout(new WeightedTableLayout(new int[] {1}));

		headerTreeContentProvider = new HeaderTreeContentProvider(this);
		headerTreeViewer.setContentProvider(headerTreeContentProvider);

		headerTreeLabelProvider = new HeaderTreeLabelProvider();
		headerTreeViewer.setLabelProvider(headerTreeLabelProvider);

		TreeColumn col = new TreeColumn(headerTreeViewer.getTree(), SWT.LEFT);
//		col.setText("Test");

		createOrderAction = new CreateOrderAction(this);
		createOfferAction = new CreateOfferAction(this);

		// Our content provider fetches the data itself, hence we need the following call
		// only to trigger the tree initialization
		headerTreeViewer.setInput(new Object());
		drillDownAdapter = new DrillDownAdapter(headerTreeViewer);

		headerTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				if (((IStructuredSelection)event.getSelection()).isEmpty())
					selectedNode = null;
				else
					selectedNode = (HeaderTreeNode) ((IStructuredSelection)event.getSelection()).getFirstElement();

				//PurchaseRootTreeNode
				//SaleRootTreeNode
				
				
				createOfferAction.setEnabled(selectedNode instanceof OrderTreeNode);

						
				
				if (!selectionChangedListeners.isEmpty()) {
					SelectionChangedEvent newEvent = new SelectionChangedEvent(
							HeaderTreeComposite.this, getSelection());
					for (Iterator<ISelectionChangedListener> it = selectionChangedListeners.iterator(); it.hasNext(); ) {
						ISelectionChangedListener listener = it.next();
						listener.selectionChanged(newEvent);
					}
				} // if (!selectionChangedListeners.isEmpty()) {
			}
		});

//		headerTreeViewer.getTableTree().addMouseListener(new MouseAdapter() {
		headerTreeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event)
			{
				IStructuredSelection structuredSelection = (IStructuredSelection)getSelection();
				Object selection = null;
				if (!structuredSelection.isEmpty())
					selection = structuredSelection.getFirstElement();

				GeneralEditorInput editorInput;
				if (selection instanceof OrderID)
					editorInput = new GeneralEditorInputOrder((OrderID)selection);
				else if (selection instanceof OfferID)
					editorInput = new GeneralEditorInputOffer((OfferID)selection);
				else if (selection instanceof InvoiceID)
					editorInput = new GeneralEditorInputInvoice((InvoiceID)selection);
				else if (selection instanceof DeliveryNoteID)
					editorInput = new GeneralEditorInputDeliveryNote((DeliveryNoteID)selection);
				else {
					// expand/collapse currently selected node... and that's all.
					if (selectedNode != null && selectedNode.hasChildren()) {
						if (selectedNode.isExpanded())
							selectedNode.collapseToLevel(1);
						else
							selectedNode.expandToLevel(1);
					}
					return;
				}
					// throw new IllegalStateException("selection \"" + (selection == null ? "null" : selection.getClass().getName()) + "\" type unknown!");

				openEditor(editorInput);
			}
		});

		hookContextMenu();

		// site.setSelectionProvider(this);
		SelectionManager.sharedInstance().addNotificationListener(
				TradePlugin.ZONE_SALE,
				LegalEntity.class, notificationListenerCustomerSelected);

		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
				SelectionManager.sharedInstance().removeNotificationListener(
						TradePlugin.ZONE_SALE,
						LegalEntity.class, notificationListenerCustomerSelected);

				imageOrderRootTreeNode.dispose(); imageOrderRootTreeNode = null;
				imageOrderTreeNode.dispose(); imageOrderTreeNode = null;
				imageOfferTreeNode.dispose(); imageOfferTreeNode = null;
				imageInvoiceRootTreeNode.dispose(); imageInvoiceRootTreeNode = null;
				imageInvoiceTreeNode.dispose(); imageInvoiceTreeNode = null;
				imageDeliveryNoteRootTreeNode.dispose(); imageDeliveryNoteRootTreeNode = null;
				imageDeliveryNoteTreeNode.dispose(); imageDeliveryNoteTreeNode = null;
				imageCustomerRootTreeNode.dispose(); imageCustomerRootTreeNode = null;
				imageVendorRootTreeNode.dispose(); imageVendorRootTreeNode = null;
			}
		});
	}

	protected Image imageOrderRootTreeNode = null;
	protected Image imageOrderTreeNode = null;
	protected Image imageOfferTreeNode = null;
	protected Image imageInvoiceRootTreeNode = null;
	protected Image imageInvoiceTreeNode = null;
	protected Image imageDeliveryNoteRootTreeNode = null;
	protected Image imageDeliveryNoteTreeNode = null;

	protected Image imageCustomerRootTreeNode = null;
	protected Image imageVendorRootTreeNode = null;

	public static void openEditor(GeneralEditorInput editorInput)
	{
		IWorkbench wb = PlatformUI.getWorkbench();
	  IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
	  IWorkbenchPage page = win.getActivePage();
		try {
			page.openEditor(editorInput, GeneralEditor.ID_EDITOR);
		} catch (PartInitException x) {
			throw new RuntimeException(x);
		}
	}

	private NotificationListener notificationListenerCustomerSelected = new NotificationAdapterWorkerThreadAsync() {
		public void notify(NotificationEvent event) {
			try {
				if (event.getSubjects().isEmpty())
					setPartnerID(null, true);
				else
					setPartnerID((AnchorID)event.getFirstSubject(), true);
			} catch (ModuleException x) {
				throw new RuntimeException(x);
			}
		}
	};

	private AnchorID setPartnerIDInvocationID = null;

	public void setPartnerID(final AnchorID partnerID, final boolean closeEditorsOfOtherPartners) // TODO needs a ProgressMonitor parameter!
	throws ModuleException
	{
		// TODO maybe we should prevent this method from being called on the UI thread (exception)?!
		setPartnerIDInvocationID = partnerID;
		try {
			TradeManager tm = partnerID == null ? null : TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			final LegalEntity partner = partnerID == null ? null : tm.getLegalEntity(partnerID, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

			if (!Util.equals(partnerID, setPartnerIDInvocationID))
				return;

			if (Thread.currentThread() == Display.getDefault().getThread())
				setPartner(partner, closeEditorsOfOtherPartners);
			else {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (!Util.equals(partnerID, setPartnerIDInvocationID))
							return;

						setPartner(partner, closeEditorsOfOtherPartners);
					}
				});
			}
		} catch (Exception x) {
			throw new ModuleException(x);
		}
	}

	public void setPartner(LegalEntity partner, boolean closeEditorsOfOtherPartners)
	{
		if (!Login.isLoggedIn())
			return;

		if (isDisposed())
			return;

		this.partner = partner;
		AnchorID partnerAnchorID = (AnchorID) JDOHelper.getObjectId(partner);
		headerTreeContentProvider.clear();
//		headerTreeViewer.setContentProvider(headerTreeContentProvider); // this is a workaround, because for some reason, the content provider seems not to be set in some situations (heisenbug)
		headerTreeViewer.setInput(new Object());
		headerTreeViewer.expandToLevel(3);

		if (! closeEditorsOfOtherPartners)
			return;
		
		IWorkbench wb = PlatformUI.getWorkbench();
	  IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
	  IWorkbenchPage page = win.getActivePage();
	  IEditorReference[] references = page.getEditorReferences();
	  
	  for (IEditorReference reference : references) {
			IEditorPart editor = reference.getEditor(false);
			if (editor instanceof GeneralEditor) {
				GeneralEditor ge = (GeneralEditor) editor;
				ArticleContainer ac = ge.getGeneralEditorComposite().getArticleContainer();
//				TODO This was a workaround to NOT close the editor in the QuickSaleView, but now
//							with the Editor<->Perspective Patch we only get editors for Trading Perspective.
//				if (ge.getGeneralEditorComposite().getArticleContainer() instanceof Order) {
//					Order order = (Order) ge.getGeneralEditorComposite().getArticleContainer();
//					if (order.isQuickSaleWorkOrder())
//						continue;
//				}
//				if (!partnerAnchorID.equals(ac.getVendorID()) && !partnerAnchorID.equals(ac.getCustomerID()))
				// partnerAnchorID may be null, hence use util class to cover null cases
				if (!Util.equals(partnerAnchorID, ac.getVendorID()) && !Util.equals(partnerAnchorID, ac.getCustomerID()))
						page.closeEditor(editor, true);
			}
//			else
//				page.closeEditor(editor, true);
		}
	}

	private void fillContextMenu(IMenuManager manager) {
		//	manager.add(action1);
		//	manager.add(action2);
		//	manager.add(new Separator());
		manager.add(createOrderAction);
		manager.add(createOfferAction);

//			manager.add(new TestAction());

		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute their actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(headerTreeViewer.getControl());
		headerTreeViewer.getControl().setMenu(menu);
		site.registerContextMenu(menuMgr, headerTreeViewer);
	}

	/**
	 * @return Returns the partner.
	 */
	public LegalEntity getPartner()
	{
		return partner;
	}
	public AnchorID getPartnerID()
	{
		return (AnchorID)JDOHelper.getObjectId(partner);
	}
	/**
	 * @return Returns the myOrganisationLegalEntity.
	 */
	public OrganisationLegalEntity getMyOrganisationLegalEntity()
	{
		return myOrganisationLegalEntity;
	}
	public AnchorID getMyOrganisationLegalEntityID()
	{
		return (AnchorID)JDOHelper.getObjectId(myOrganisationLegalEntity);
	}
	/**
	 * @return Returns the headerTreeContentProvider.
	 */
	public HeaderTreeContentProvider getHeaderTreeContentProvider()
	{
		return headerTreeContentProvider;
	}
	/**
	 * @return Returns the headerTreeViewer.
	 */
	public TreeViewer getHeaderTreeViewer()
	{
		return headerTreeViewer;
	}

	private java.util.List<ISelectionChangedListener> selectionChangedListeners = 
		new LinkedList<ISelectionChangedListener>();

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.add(listener);
	}

	/**
	 * @return an {@link IStructuredSelection} - either empty or with an instance
	 * of {@link org.nightlabs.jfire.trade.ui.id.OrderID},
	 * {@link org.nightlabs.jfire.trade.ui.id.OfferID},
	 * {@link org.nightlabs.jfire.accounting.id.InvoiceID},
	 * {@link org.nightlabs.jfire.store.id.DeliveryNoteID}.
	 *
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection()
	{
		ISelection selection = null;

		if (selectedNode instanceof OrderTreeNode) {
			OrderTreeNode node = (OrderTreeNode)selectedNode;
			Order order = node.getOrder();
			OrderID orderID = (OrderID)JDOHelper.getObjectId(order);
			selection = new StructuredSelection(orderID);
		}
		else if (selectedNode instanceof OfferTreeNode) {
			OfferTreeNode node = (OfferTreeNode)selectedNode;
			Offer offer = node.getOffer();
			OfferID offerID = (OfferID)JDOHelper.getObjectId(offer);
			selection = new StructuredSelection(offerID);
		}
		else if (selectedNode instanceof InvoiceTreeNode) {
			InvoiceTreeNode node = (InvoiceTreeNode)selectedNode;
			Invoice invoice = node.getInvoice();
			InvoiceID invoiceID = (InvoiceID)JDOHelper.getObjectId(invoice);
			selection = new StructuredSelection(invoiceID);
		}
		else if (selectedNode instanceof DeliveryNoteTreeNode) {
			DeliveryNoteTreeNode node = (DeliveryNoteTreeNode)selectedNode;
			DeliveryNote deliveryNote = node.getDeliveryNote();
			DeliveryNoteID deliveryNoteID = (DeliveryNoteID)JDOHelper.getObjectId(deliveryNote);
			selection = new StructuredSelection(deliveryNoteID);
		}

		if (selection == null)
			return StructuredSelection.EMPTY;

		return selection;
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.remove(listener);
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection)
	{
		throw new UnsupportedOperationException("NYI"); //$NON-NLS-1$
	}

//	public void addInvoice(InvoiceID invoiceID)
//	{
//		try {
//			InvoiceRootTreeNode invoiceRootTreeNode = getHeaderTreeContentProvider().getVendorInvoiceRootTreeNode();
//			AccountingManager accountingManager = AccountingManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//			Invoice invoice = accountingManager.getInvoice(invoiceID, InvoiceRootTreeNode.FETCH_GROUPS_INVOICE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//			InvoiceTreeNode invoiceTreeNode = new InvoiceTreeNode(invoiceRootTreeNode, InvoiceTreeNode.POSITION_FIRST_CHILD, invoice);
//			invoiceTreeNode.select();
//		} catch (Exception x) {
//			throw new RuntimeException(x);
//		}
//	}

//	public void addDeliveryNote(DeliveryNoteID deliveryNoteID)
//	{
//		try {
//			DeliveryNoteRootTreeNode deliveryNoteRootTreeNode = getHeaderTreeContentProvider().getVendorDeliveryNoteRootTreeNode();
//			StoreManager storeManager = StoreManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//			DeliveryNote deliveryNote = storeManager.getDeliveryNote(deliveryNoteID, DeliveryNoteRootTreeNode.FETCH_GROUPS_DELIVERY_NOTE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//			DeliveryNoteTreeNode deliveryNoteTreeNode = new DeliveryNoteTreeNode(deliveryNoteRootTreeNode, DeliveryNoteTreeNode.POSITION_FIRST_CHILD, deliveryNote);
//			deliveryNoteTreeNode.select();
//		} catch (Exception x) {
//			throw new RuntimeException(x);
//		}
//	}

	public CreateOrderAction getCreateOrderAction()
	{
		return createOrderAction;
	}

	public CreateOfferAction getCreateOfferAction()
	{
		return createOfferAction;
	}

	public HeaderTreeNode getSelectedNode()
	{
		return selectedNode;
	}
}
