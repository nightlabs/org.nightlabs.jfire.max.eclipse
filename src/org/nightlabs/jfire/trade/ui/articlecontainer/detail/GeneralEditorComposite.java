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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.EditLockTypeInvoice;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.accounting.InvoiceLocal;
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.editlock.EditLockCallback;
import org.nightlabs.jfire.base.ui.editlock.EditLockCarrier;
import org.nightlabs.jfire.base.ui.editlock.EditLockHandle;
import org.nightlabs.jfire.base.ui.editlock.EditLockMan;
import org.nightlabs.jfire.base.ui.editlock.InactivityAction;
import org.nightlabs.jfire.editlock.id.EditLockTypeID;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jdo.notification.DirtyObjectID;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.store.DeliveryNoteLocal;
import org.nightlabs.jfire.store.EditLockTypeDeliveryNote;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleCarrier;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ArticleSegmentGroup;
import org.nightlabs.jfire.trade.EditLockTypeOffer;
import org.nightlabs.jfire.trade.EditLockTypeOrder;
import org.nightlabs.jfire.trade.FetchGroupsTrade;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.OfferLocal;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.Segment;
import org.nightlabs.jfire.trade.SegmentContainer;
import org.nightlabs.jfire.trade.SegmentType;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.articlecontainer.DeliveryNoteDAO;
import org.nightlabs.jfire.trade.ui.articlecontainer.InvoiceProvider;
import org.nightlabs.jfire.trade.ui.articlecontainer.OfferDAO;
import org.nightlabs.jfire.trade.ui.articlecontainer.OrderDAO;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.GeneralEditorActionBarContributor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.deliverynote.DeliveryNoteFooterComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.deliverynote.DeliveryNoteHeaderComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.deliverynote.GeneralEditorInputDeliveryNote;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice.GeneralEditorInputInvoice;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice.InvoiceFooterComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice.InvoiceHeaderComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.GeneralEditorInputOffer;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.OfferFooterComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.OfferHeaderComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.order.GeneralEditorInputOrder;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.order.OrderFooterComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.order.OrderHeaderComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

/**
 * This <tt>Composite</tt> is used to edit
 * {@link org.nightlabs.jfire.trade.ui.Order}s,
 * {@link org.nightlabs.jfire.trade.ui.Offer}s,
 * {@link org.nightlabs.jfire.accounting.Invoice}s and
 * {@link org.nightlabs.jfire.store.Delivery}s. It asks the
 * {@link org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEditFactoryRegistry}
 * for the right factories for all the {@link org.nightlabs.jfire.trade.ui.Segment}s
 * and displays the edits which are delivered from the factory.
 * <p>
 * This <tt>Composite</tt> contains the main logic while
 * {@link org.nightlabs.jfire.trade.ui.articlecontainer.detail.GeneralEditor} is
 * just a tiny wrapper. This architecture allows to display and edit an
 * order/offer/invoice/deliveryNote within another carrier (e.g. a view).
 * 
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class GeneralEditorComposite
extends XComposite
{
	private static final Logger logger = Logger.getLogger(GeneralEditorComposite.class);

	private GeneralEditorInput input = null;

	/**
	 * This is initialized by
	 * {@link GeneralEditorActionBarContributor#setActiveEditor(IEditorPart)} as
	 * soon as the Editor became active the first time.
	 */
	private GeneralEditorActionBarContributor generalEditorActionBarContributor = null;

	private Order order = null;

	private Offer offer = null;

	private Invoice invoice = null;

	private DeliveryNote deliveryNote = null;

	private ArticleContainer articleContainer = null;

	private HeaderComposite headerComposite;

	private FooterComposite footerComposite;

	// private List segmentCompositeScrollContainers = new ArrayList();
	// private List segmentEditComposites = new ArrayList();
	
	private Map<TabItem, SegmentEdit> segmentEditsByTabItem = new HashMap<TabItem, SegmentEdit>();
//	// only used if no tabFolder, means only 1 or less segmentTypes are used
//	private Map<Composite, SegmentEdit> segmentEditByComposite = new HashMap<Composite, SegmentEdit>();
//	private Composite singleSegmentComposite;
	private SegmentEdit singleSegmentSegmentEdit;

	private Map<String, SegmentEdit> segmentPK2segmentEditMap = new HashMap<String, SegmentEdit>();

	// /**
	// * This composite is the scrolling carrier of the {@link
	// #segmentCompositeContainer}.
	// */
	// private ScrolledComposite segmentCompositeScrollContainer;

//	private TabFolder segmentCompositeFolder;
	private Composite segmentCompositeFolder;

	// /**
	// * This composite holds all those composites which render the segments
	// * (vertically stacked). It is the content Composite of the
	// * {@link #segmentCompositeScrollContainer}.
	// *
	// * TODO We will change the GUI not to stack the segments vertically, but to
	// display tabs. Alternatively, they could be stacked, but
	// "expandable+collapsable".
	// */
	// private XComposite segmentCompositeContainer;

	private IWorkbenchPartSite site;

	private Label loadingDataLabel;

	/**
	 * @param parent
	 * @param style
	 */
	public GeneralEditorComposite(IWorkbenchPartSite site, Composite parent, GeneralEditorInput generalEditorInput) {
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		if (site == null)
			throw new IllegalArgumentException("site must not be null!"); //$NON-NLS-1$

		this.site = site;

		loadingDataLabel = new Label(this, SWT.NONE);
		loadingDataLabel.setText("Loading data...");

		loadInitialArticleContainerJob = new LoadInitialArticleContainerJob(generalEditorInput);
		loadInitialArticleContainerJob.schedule();
	}

	private LoadInitialArticleContainerJob loadInitialArticleContainerJob;

	private class LoadInitialArticleContainerJob extends Job
	{
		private GeneralEditorInput generalEditorInput;

		public LoadInitialArticleContainerJob(GeneralEditorInput generalEditorInput)
		{
			super("Loading article container");
			this.generalEditorInput = generalEditorInput;
			assert generalEditorInput != null : "generalEditorInput != null";
		}
		
		@Override
		protected org.eclipse.core.runtime.IStatus run(ProgressMonitor monitor) throws Exception
		{
			loadInitialArticleContainerJob = null; // release memory

			initGeneralEditorInput(generalEditorInput, monitor);

			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					if (GeneralEditorComposite.this.isDisposed())
						return;

					loadingDataLabel.dispose();
					loadingDataLabel = null;

					if (order != null)
						headerComposite = new OrderHeaderComposite(GeneralEditorComposite.this, order);

					if (offer != null)
						headerComposite = new OfferHeaderComposite(GeneralEditorComposite.this, offer);

					if (invoice != null)
						headerComposite = new InvoiceHeaderComposite(GeneralEditorComposite.this, invoice);

					if (deliveryNote != null)
						headerComposite = new DeliveryNoteHeaderComposite(GeneralEditorComposite.this, deliveryNote);

					headerComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

					// TODO: segments can be potentially added on the fly, therefore GeneralEditorComposite.this behaviour must be supported
					if (hasDifferentSegments()) {
						segmentCompositeFolder = new TabFolder(GeneralEditorComposite.this, SWT.NONE);
						segmentCompositeFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
						((TabFolder)segmentCompositeFolder).addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								updateActiveSegmentEdit();
							}
						});
					}
					else {
						segmentCompositeFolder = new XComposite(GeneralEditorComposite.this, SWT.NONE);
					}

					// segmentCompositeScrollContainer = new ScrolledComposite(GeneralEditorComposite.this,
					// SWT.V_SCROLL); // TODO do we really not want horizontal scrolling?
					// segmentCompositeScrollContainer.setExpandHorizontal(true);
					// segmentCompositeScrollContainer.setExpandVertical(true);
					// segmentCompositeScrollContainer.setLayoutData(new
					// GridData(GridData.FILL_BOTH));
					// segmentCompositeScrollContainer.setAlwaysShowScrollBars(true); // TODO do
					// we really want to ALWAYS display scroll bars?
					//
					// segmentCompositeContainer = new
					// XComposite(segmentCompositeScrollContainer, SWT.NONE,
					// XComposite.LAYOUT_MODE_TIGHT_WRAPPER);
					// segmentCompositeScrollContainer.setContent(segmentCompositeContainer);

					// segmentCompositeContainer.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));

					EditLockTypeID editLockTypeID;
					if (order != null) {
						footerComposite = new OrderFooterComposite(GeneralEditorComposite.this, GeneralEditorComposite.this);
						editLockTypeID = EditLockTypeOrder.EDIT_LOCK_TYPE_ID;
					}
					else if (offer != null) {
						footerComposite = new OfferFooterComposite(GeneralEditorComposite.this, GeneralEditorComposite.this);
						editLockTypeID = EditLockTypeOffer.EDIT_LOCK_TYPE_ID;
					}
					else if (invoice != null) {
						footerComposite = new InvoiceFooterComposite(GeneralEditorComposite.this, GeneralEditorComposite.this);
						editLockTypeID = EditLockTypeInvoice.EDIT_LOCK_TYPE_ID;
					}
					else if (deliveryNote != null) {
						footerComposite = new DeliveryNoteFooterComposite(GeneralEditorComposite.this, GeneralEditorComposite.this);
						editLockTypeID = EditLockTypeDeliveryNote.EDIT_LOCK_TYPE_ID;
					}
					else
						throw new IllegalStateException("All null!"); //$NON-NLS-1$

					footerComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					footerComposite.refresh();

					// try {
					// createSegmentEditComposites();
					// } catch (EPProcessorException e) {
					// throw new RuntimeException(e);
					// }

					final EditLockHandle editLockHandle = EditLockMan.sharedInstance().acquireEditLock(editLockTypeID, getArticleContainerID(), "TODO", // TODO description //$NON-NLS-1$
//							null,
							new EditLockCallback() {
						@Override
						public InactivityAction getEditLockAction(EditLockCarrier editLockCarrier) {
							return InactivityAction.REFRESH_LOCK;
						}
					},
					getShell(), new NullProgressMonitor()); // TODO async!
					// TODO whenever we change sth., we should refresh the lock by calling editLockHandle.refresh()!

					final Class<? extends ArticleContainer> articleContainerClass = articleContainer.getClass();
					JDOLifecycleManager.sharedInstance().addNotificationListener(articleContainerClass, articleContainerChangedListener);

					addDisposeListener(new DisposeListener() {
						public void widgetDisposed(DisposeEvent e) {
							JDOLifecycleManager.sharedInstance().removeNotificationListener(articleContainerClass, articleContainerChangedListener);

//							ArticleContainerID articleContainerID = getArticleContainerID();
//							// TODO WORKAROUND JPOX bug begin
//							if (articleContainerID instanceof OrderID) {
//								OrderID orderID = (OrderID) articleContainerID;
//								if (orderID.organisationID == null) {
//									logger.warn("orderID.organisationID == null", new NullPointerException("orderID.organisationID == null")); //$NON-NLS-1$ //$NON-NLS-2$
//									orderID.organisationID = order.getOrganisationID();
//								}
//								if (orderID.orderIDPrefix == null) {
//									logger.warn("orderID.orderIDPrefix == null", new NullPointerException("orderID.orderIDPrefix == null")); //$NON-NLS-1$ //$NON-NLS-2$
//									orderID.orderIDPrefix = order.getOrderIDPrefix();
//								}
//							}
//							// TODO WORKAROUND JPOX bug end

							editLockHandle.release();
							if (articleSegmentGroups != null)
								articleSegmentGroups.onDispose();
							// removeDisposeListener(this); // nötig? Marco.
						}
					});

					// it is likely that the action-bar-contributor has been set too early for creating the UI, hence, we call it now.
					if (generalEditorActionBarContributor != null)
						setGeneralEditorActionBarContributor(generalEditorActionBarContributor);
				} // void run()
			});

			return Status.OK_STATUS;
		}
	};

	private NotificationListener articleContainerChangedListener = new NotificationAdapterJob() {
		@Override
		public void notify(NotificationEvent notificationEvent)
		{
			if (isDisposed())
				return;

			DirtyObjectID notifiedDirtyObjectID = (DirtyObjectID) notificationEvent.getFirstSubject();
			if (!getArticleContainerID().equals(notifiedDirtyObjectID.getObjectID())) {
//				if (logger.isDebugEnabled()) {
//					logger.debug("");
//				}
				return;
			}

			// reload the new ArticleContainer from the server
			initGeneralEditorInput(input, new ProgressMonitorWrapper(getProgressMonitor()));

			// update header+footer on the UI thread
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					if (isDisposed())
						return;

					updateHeaderAndFooter();
				}
			});
		}
	};

	public GeneralEditorInput getInput() {
		return input;
	}
	
	protected boolean hasDifferentSegments()
	{
		// TODO After initial creation of the GeneralEditorComposite, it is a bad idea
		// to access the ArticleContainer! During initialisation, all the relevant data
		// (i.e. all the Articles) are loaded into an instance of ClientArticleSegmentGroups
		// which is manipulated afterwards. That means, while the result of
		// this.articleContainer.getArticles() does NOT change until the editor is closed and
		// reopened, the method this.articleSegmentGroups.getArticles() will return up-to-date
		// information. It even provides this info structured
		// (see e.g. ArticleSegmentGroups.getArticleSegmentGroups()).

		Segment segment = null;

		if (articleContainer instanceof SegmentContainer) {
			SegmentContainer segmentContainer = (SegmentContainer) articleContainer;
			Collection<? extends Segment> segments = segmentContainer.getSegments();

			if (segments.size() > 1)
				return true;
			else if (segments.size() == 1)
				segment = segments.iterator().next();
		}

		for (Iterator<Article> it = articleContainer.getArticles().iterator(); it.hasNext(); ) {
			Article article = it.next();
			if (segment == null)
				segment = article.getSegment();
			if (!segment.equals(article.getSegment()))
				return true;
		}

		return false;
	}
	
	protected void updateHeaderAndFooter() {
		headerComposite.refresh();
		footerComposite.refresh();
		if (logger.isDebugEnabled())
			logger.debug("updateHeaderAndFooter"); //$NON-NLS-1$
	}

	/**
	 * @return Returns <code>null</code> before the editor has been active the
	 *         first time. Afterwards, it returns the contributor responsible for
	 *         the editor.
	 */
	public GeneralEditorActionBarContributor getGeneralEditorActionBarContributor() {
		return generalEditorActionBarContributor;
	}

	/**
	 * This method is called by
	 * {@link GeneralEditorActionBarContributor#setActiveEditor(IEditorPart)}.
	 */
	public void setGeneralEditorActionBarContributor(
			GeneralEditorActionBarContributor generalEditorActionBarContributor) {
		this.generalEditorActionBarContributor = generalEditorActionBarContributor;

		assert Display.getCurrent() != null : "*NOT* called on UI thread! This method must be called on the UI thread!";

		// not yet initialised - will initialise in the callback
		if (loadingDataLabel != null)
			return;

		try {
			if (!segmentEditCompositesCreated)
				createSegmentEditComposites();
		} catch (EPProcessorException e) {
			throw new RuntimeException(e);
		}
	}

	// protected void recreateSegmentEditComposites() throws EPProcessorException
	// {
	// removeSegmentEditComposites();
	// createSegmentEditComposites();
	// }

	private boolean segmentEditCompositesCreated = false;

	protected void removeSegmentEditComposites()
	{
		segmentEditCompositesCreated = false;
		
		if (hasDifferentSegments()) {
			TabItem[] tabItems = ((TabFolder)segmentCompositeFolder).getItems();
			for (int i = 0; i < tabItems.length; ++i) {
				tabItems[i].dispose();
			}
			segmentEditsByTabItem.clear();
		}
		else {
			segmentCompositeFolder.dispose();
		}
	}

	private CompositeContentChangeListener segmentCompositeContentChangeListener = new CompositeContentChangeListener() {
		/**
		 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.CompositeContentChangeListener#changed(org.nightlabs.jfire.trade.ui.articlecontainer.CompositeContentChangeEvent)
		 */
		public void changed(CompositeContentChangeEvent event) {
			calculateScrollContentSize();
//			updateHeaderAndFooter(); // I think that's not necessary here. There are already other listeners.
		}
	};

	protected void createSegmentEditComposite(ArticleSegmentGroup asg, SegmentEdit segmentEdit)
	{
		Segment segment = segmentEdit.getArticleSegmentGroup().getSegment();

		// TODO Segments can be added while this GeneralEditorComposite is visible. Unfortunately,
		// this was not taken into account when Daniel refactored this class (he removed the TabFolder
		// when there's only one Segment). After his refactoring, it means that we would need to
		// rebuild the UI if a Segment is added! Marco.

		TabItem tabItem = null;
		if (hasDifferentSegments()) {
			tabItem = new TabItem((TabFolder)segmentCompositeFolder, SWT.NONE);
			tabItem.setText(
					String.format(
							Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.GeneralEditorComposite.segmentTabItem.text"), //$NON-NLS-1$
							segment.getSegmentType().getName().getText(),
							segment.getSegmentIDAsString()));
		}

		ScrolledComposite segmentCompositeScrollContainer;
		// TODO do we really not want horizontal scrolling?
		segmentCompositeScrollContainer = new ScrolledComposite(
				segmentCompositeFolder, SWT.V_SCROLL);
		segmentCompositeScrollContainer.setExpandHorizontal(true);
		segmentCompositeScrollContainer.setExpandVertical(true);
		segmentCompositeScrollContainer.setLayoutData(new GridData(
				GridData.FILL_BOTH));
		// segmentCompositeScrollContainer.setAlwaysShowScrollBars(true);
		// TODO do we really want to ALWAYS display scroll bars?
		
		if (tabItem != null)
			tabItem.setControl(segmentCompositeScrollContainer);

		// segmentCompositeScrollContainers.add(segmentCompositeScrollContainer);

		// segmentCompositeContainer = new
		// XComposite(segmentCompositeScrollContainer, SWT.NONE,
		// XComposite.LAYOUT_MODE_TIGHT_WRAPPER);
		// segmentCompositeScrollContainer.setContent(segmentCompositeContainer);

		Composite composite = segmentEdit
				.createComposite(segmentCompositeScrollContainer); // segmentCompositeContainer);
		segmentCompositeScrollContainer.setContent(composite);
		// segmentEditComposites.add(composite);

		// TODO Segments can be added while this GeneralEditorComposite is visible. See comment at the beginning of this method! Marco.
		if (hasDifferentSegments())
			segmentEditsByTabItem.put(tabItem, segmentEdit);
		else {
//			singleSegmentComposite = segmentCompositeFolder;
			singleSegmentSegmentEdit = segmentEdit;
//			segmentEditByComposite.put(segmentCompositeFolder, segmentEdit);
		}
		
		segmentPK2segmentEditMap.put(segment.getPrimaryKey(), segmentEdit);
		layout(true, true);
	}

	private ClientArticleSegmentGroups articleSegmentGroups = null;

	protected void createArticleSegmentGroups()
	throws EPProcessorException
	{
		ArticleCreateListener[] articleCreateListenerArray = null;
		ArticleChangeListener[] articleChangeListenerArray = null;

		if (!earlyArticleCreateListeners.isEmpty()) {
			Object[] listeners = earlyArticleCreateListeners.getListeners();
			articleCreateListenerArray = new ArticleCreateListener[listeners.length + 1];
			System.arraycopy(listeners, 0, articleCreateListenerArray, 0, listeners.length);
//			for (int i=0; i<earlyArticleCreateListeners.size(); i++) {
//				articleCreateListenerArray[i] = earlyArticleCreateListeners.get(i);
//			}
			articleCreateListenerArray[articleCreateListenerArray.length-1] = articleCreateListener;
		}
		else {
			articleCreateListenerArray = new ArticleCreateListener[] { articleCreateListener };
		}

		if (!earlyArticleChangeListeners.isEmpty()) {
			Object[] listeners = earlyArticleChangeListeners.getListeners();
			articleChangeListenerArray = new ArticleChangeListener[listeners.length + 1];
			System.arraycopy(listeners, 0, articleChangeListenerArray, 0, listeners.length);
//			for (int i=0; i<earlyArticleChangeListeners.size(); i++) {
//				articleChangeListenerArray[i] = earlyArticleChangeListeners.get(i);
//			}
			articleChangeListenerArray[articleChangeListenerArray.length-1] = articleChangeListener;
		}
		else {
			articleChangeListenerArray = new ArticleChangeListener[] { articleChangeListener };
		}

		articleSegmentGroups = new ClientArticleSegmentGroups(articleContainer,
				articleCreateListenerArray,
				articleChangeListenerArray);
	}
	
	protected void createSegmentEditComposites()
	throws EPProcessorException
	{
		segmentEditCompositesCreated = true;

		// ArticleSegmentGroups asgs = new ArticleSegmentGroups(articleContainer);
		for (ArticleSegmentGroup articleSegmentGroup : articleSegmentGroups.getArticleSegmentGroups())
			createSegmentEditAndComposite(articleSegmentGroup);

		updateActiveSegmentEdit();

		// The following line is important! Otherwise, it wouldn't scroll correctly!
		calculateScrollContentSize();
	}

	protected void createSegmentEditAndComposite(ArticleSegmentGroup asg) {
		Segment segment = asg.getSegment();
		SegmentType segmentType = segment.getSegmentType();

		SegmentEditFactory sef = SegmentEditFactoryRegistry.sharedInstance()
				.getSegmentEditFactory(input.getSegmentContext(), segmentType.getClass(), true);

		SegmentEdit segmentEdit = sef.createSegmentEdit(this, input.getSegmentContext(), asg);
		segmentEdit.addCompositeContentChangeListener(segmentCompositeContentChangeListener);

		createSegmentEditComposite(asg, segmentEdit);

		if (activeSegmentEdit == null) {
			activeSegmentEdit = segmentEdit;
			segmentEdit.getComposite().setFocus();
			updateActiveSegmentEdit();
		}
	}

	private ArticleCreateListener articleCreateListener = new ArticleCreateListener() {
		public void articlesCreated(ArticleCreateEvent articleCreateEvent) {
			Map<SegmentEdit, List<ArticleCarrier>> segmentEdit2ArticleCarriers = new HashMap<SegmentEdit, List<ArticleCarrier>>();
			// Map<Segment, List<Article>> segment2ArticlesWithoutSegmentEdit = new
			// HashMap<Segment, List<Article>>();
			Set<ArticleSegmentGroup> articleSegmentGroupsWithoutSegmentEdit = new HashSet<ArticleSegmentGroup>();

			for (ArticleCarrier articleCarrier : articleCreateEvent.getArticleCarriers()) {
				Article article = articleCarrier.getArticle();
				SegmentEdit segmentEdit = segmentPK2segmentEditMap.get(article.getSegment().getPrimaryKey());
				if (segmentEdit == null) {
					// we do not yet have a SegmentEdit for article
					articleSegmentGroupsWithoutSegmentEdit.add(articleCarrier.getArticleSegmentGroup());

					// List<Article> articles =
					// segment2ArticlesWithoutSegmentEdit.get(article.getSegment());
					// if (articles == null) {
					// articles = new ArrayList<Article>();
					// segment2ArticlesWithoutSegmentEdit.put(article.getSegment(),
					// articles);
					// }
					// articles.add(article);
				} else {
					// we do already have a SegmentEdit for article
					List<ArticleCarrier> articleCarriers = segmentEdit2ArticleCarriers.get(segmentEdit);
					if (articleCarriers == null) {
						articleCarriers = new ArrayList<ArticleCarrier>();
						segmentEdit2ArticleCarriers.put(segmentEdit, articleCarriers);
					}
					articleCarriers.add(articleCarrier);
				}
			}

			for (Map.Entry<SegmentEdit, List<ArticleCarrier>> me : segmentEdit2ArticleCarriers.entrySet())
				me.getKey().addArticles(me.getValue());

			for (ArticleSegmentGroup articleSegmentGroup : articleSegmentGroupsWithoutSegmentEdit)
				createSegmentEditAndComposite(articleSegmentGroup);

			// for (Map.Entry<Segment, List<Article>> me :
			// segment2ArticlesWithoutSegmentEdit.entrySet()) {
			// Segment segment = me.getKey();
			// // List<Article> articles = me.getValue(); // currently not needed
			//
			// ArticleSegmentGroup asg =
			// articleSegmentGroups.getArticleSegmentGroupForSegmentPK(segment.getPrimaryKey(),
			// true);
			// // TODO can we be sure that the articles we have here are identical
			// with those Articles that are managed by asg???
			// // imho, it should be the case...
			// createSegmentEditAndComposite(asg);
			// }
			updateHeaderAndFooter();
		}
	};

	private ArticleChangeListener articleChangeListener = new ArticleChangeListener() {
		public void articlesChanged(ArticleChangeEvent articleChangeEvent) {
			if (articleChangeEvent.getDeletedArticles().isEmpty())
				return;

//			for (Article article : articleChangeEvent.getDeletedArticles()) {
//				SegmentEdit segmentEdit = segmentPK2segmentEditMap.get(article
//						.getSegment().getPrimaryKey());
//				 if (segmentEdit != null)
//					 segmentEdit.r
//			}

			Map<SegmentEdit, Collection<ArticleCarrier>> segmentEdit2DeletedArticleCarriers = new HashMap<SegmentEdit, Collection<ArticleCarrier>>();
			for (ArticleCarrier articleCarrier : articleChangeEvent.getDeletedArticleCarriers()) {
				String segmentPK = articleCarrier.getArticle().getSegment().getPrimaryKey();
				SegmentEdit segmentEdit = segmentPK2segmentEditMap.get(segmentPK);
				Collection<ArticleCarrier> deletedArticleCarriers = segmentEdit2DeletedArticleCarriers.get(segmentEdit);
				if (deletedArticleCarriers == null) {
					deletedArticleCarriers = new ArrayList<ArticleCarrier>();
					segmentEdit2DeletedArticleCarriers.put(segmentEdit, deletedArticleCarriers);
				}
				deletedArticleCarriers.add(articleCarrier);
			}

			for (Map.Entry<SegmentEdit, Collection<ArticleCarrier>> me : segmentEdit2DeletedArticleCarriers.entrySet())
				me.getKey().removeArticles(me.getValue());

			updateHeaderAndFooter();
		}
	};

	protected void updateActiveSegmentEdit()
	{
		if (hasDifferentSegments()) {
			TabFolder tabFolder = ((TabFolder)segmentCompositeFolder);
			TabItem item = tabFolder.getItemCount() < 1 ? null
					: tabFolder.getItem(tabFolder.getSelectionIndex());
			activeSegmentEdit = segmentEditsByTabItem.get(item);
			if (activeSegmentEdit == null && item != null && item.getControl() != null)
				throw new IllegalStateException(
						"TabItem is not registered in Map segmentEditsByTabItem!!!");			 //$NON-NLS-1$
		}
		else {
//			activeSegmentEdit = (SegmentEdit) segmentEditByComposite.get(segmentCompositeFolder);
			activeSegmentEdit = singleSegmentSegmentEdit;
		}
		
		ActiveSegmentEditSelectionEvent event = null;
		Object[] listeners = activeSegmentEditSelectionListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			ActiveSegmentEditSelectionListener listener = (ActiveSegmentEditSelectionListener) listeners[i];
			if (event == null)
				event = new ActiveSegmentEditSelectionEvent(this);
			listener.selected(event);
		}
	}

	private ListenerList activeSegmentEditSelectionListeners = new ListenerList();

	public void addActiveSegmentEditSelectionListener(
			ActiveSegmentEditSelectionListener listener) {
		activeSegmentEditSelectionListeners.add(listener);
	}

	public void removeActiveSegmentEditSelectionListener(
			ActiveSegmentEditSelectionListener listener) {
		activeSegmentEditSelectionListeners.remove(listener);
	}

	// private List segmentEdits = new ArrayList();

	/**
	 * This method must be called, whenever the segments add or remove controls to
	 * their composites and hence the size changes. Otherwise, the scrolling
	 * doesn't work correctly.
	 */
	public void calculateScrollContentSize() {
		for (Iterator it = segmentEditsByTabItem.values().iterator(); it.hasNext();) {
			SegmentEdit segmentEdit = (SegmentEdit) it.next();
			calculateScrollContentSize(segmentEdit);
//			Composite segmentEditComposite = segmentEdit.getComposite();
//			ScrolledComposite segmentCompositeScrollContainer = (ScrolledComposite) segmentEditComposite
//					.getParent();
//
//			// ScrolledComposite segmentCompositeScrollContainer = (ScrolledComposite)
//			// it.next();
//			// Control[] children = segmentCompositeScrollContainer.getChildren();
//			// if (children.length != 1)
//			// throw new IllegalStateException("segmentCompositeScrollContainer has "
//			// + children.length + " child controls instead of 1!");
//			//
//			// Composite segmentEditComposite = (Composite) children[0];
//
//			Rectangle bounds = segmentEditComposite.getBounds();
//			bounds.width = segmentCompositeScrollContainer.getClientArea().width
//					- segmentCompositeScrollContainer.getVerticalBar().getSize().x;
//			// segmentCompositeContainer.setBounds(bounds);
//
//			segmentCompositeScrollContainer.setMinSize(segmentEditComposite
//					.computeSize(bounds.width, SWT.DEFAULT));
//			segmentEditComposite.layout();
		}

		if (singleSegmentSegmentEdit != null)
			calculateScrollContentSize(singleSegmentSegmentEdit);
	}

	private void calculateScrollContentSize(SegmentEdit segmentEdit)
	{
		Composite segmentEditComposite = segmentEdit.getComposite();
		ScrolledComposite segmentCompositeScrollContainer = (ScrolledComposite) segmentEditComposite
				.getParent();

		// ScrolledComposite segmentCompositeScrollContainer = (ScrolledComposite)
		// it.next();
		// Control[] children = segmentCompositeScrollContainer.getChildren();
		// if (children.length != 1)
		// throw new IllegalStateException("segmentCompositeScrollContainer has "
		// + children.length + " child controls instead of 1!");
		//
		// Composite segmentEditComposite = (Composite) children[0];

		Rectangle bounds = segmentEditComposite.getBounds();
		bounds.width = segmentCompositeScrollContainer.getClientArea().width
				- segmentCompositeScrollContainer.getVerticalBar().getSize().x;
		// segmentCompositeContainer.setBounds(bounds);

		segmentCompositeScrollContainer.setMinSize(segmentEditComposite
				.computeSize(bounds.width, SWT.DEFAULT));
		segmentEditComposite.layout();
	}

	// protected TradeManager getTradeManager()
	// throws RemoteException, LoginException, CreateException, NamingException
	// {
	// return
	// TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
	// }
	//
	// protected StoreManager getStoreManager()
	// throws RemoteException, LoginException, CreateException, NamingException
	// {
	// return
	// StoreManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
	// }

	private SegmentEdit activeSegmentEdit = null;

	/**
	 * @return Returns either <code>null</code> if there is no SegmentEdit
	 *         active or the one that is.
	 */
	public SegmentEdit getActiveSegmentEdit() {
		return activeSegmentEdit;
	}

	public Collection<SegmentEdit> getSegmentEdits() {
		return segmentEditsByTabItem.values();
	}

	// public List getSegmentEdits()
	// {
	// return segmentEdits;
	// }

	public static final String[] FETCH_GROUPS_ARTICLE_CONTAINER_WITHOUT_ARTICLES = {
		FetchPlan.DEFAULT,
		FetchGroupsTrade.FETCH_GROUP_ARTICLE_CONTAINER_IN_EDITOR,
		Segment.FETCH_GROUP_THIS_SEGMENT,
		SegmentType.FETCH_GROUP_THIS_SEGMENT_TYPE,
		StatableLocal.FETCH_GROUP_STATE
	};

	public static final String[] FETCH_GROUPS_ORDER_WITH_ARTCILES = {
			Order.FETCH_GROUP_THIS_ORDER, Segment.FETCH_GROUP_THIS_SEGMENT,
			SegmentType.FETCH_GROUP_THIS_SEGMENT_TYPE,
			FetchGroupsTrade.FETCH_GROUP_ARTICLE_IN_ORDER_EDITOR, FetchPlan.DEFAULT };

	public static final String[] FETCH_GROUPS_OFFER_WITH_ARTICLES = {
			Offer.FETCH_GROUP_THIS_OFFER, OfferLocal.FETCH_GROUP_THIS_OFFER_LOCAL,
			StatableLocal.FETCH_GROUP_STATE, Order.FETCH_GROUP_CUSTOMER_GROUP,
			Segment.FETCH_GROUP_THIS_SEGMENT,
			SegmentType.FETCH_GROUP_THIS_SEGMENT_TYPE,
			FetchGroupsTrade.FETCH_GROUP_ARTICLE_IN_OFFER_EDITOR, FetchPlan.DEFAULT };

	public static final String[] FETCH_GROUPS_INVOICE_WITH_ARTICLES = {
			Invoice.FETCH_GROUP_THIS_INVOICE,
			InvoiceLocal.FETCH_GROUP_THIS_INVOICE_LOCAL,
			StatableLocal.FETCH_GROUP_STATE, Segment.FETCH_GROUP_THIS_SEGMENT,
			SegmentType.FETCH_GROUP_THIS_SEGMENT_TYPE,
			FetchGroupsTrade.FETCH_GROUP_ARTICLE_IN_INVOICE_EDITOR, FetchPlan.DEFAULT };

	public static final String[] FETCH_GROUPS_DELIVERY_NOTE_WITH_ARTICLES = {
			DeliveryNote.FETCH_GROUP_THIS_DELIVERY_NOTE,
			DeliveryNoteLocal.FETCH_GROUP_THIS_DELIVERY_NOTE_LOCAL,
			StatableLocal.FETCH_GROUP_STATE, Segment.FETCH_GROUP_THIS_SEGMENT,
			SegmentType.FETCH_GROUP_THIS_SEGMENT_TYPE,
			FetchGroupsTrade.FETCH_GROUP_ARTICLE_IN_DELIVERY_NOTE_EDITOR,
			FetchPlan.DEFAULT };

	/**
	 * Initialise this instance of <code>GeneralEditorComposite</code> or reload the {@link ArticleContainer} referenced
	 * by the <code>generalEditorInput</code> parameter. In case of reloading, the articles are not fetched again
	 * (they're managed separately by the {@link ClientArticleSegmentGroups} in {@link #articleSegmentGroups})
	 *
	 * @param generalEditorInput the input to be set.
	 * @param monitor the monitor to provide feedback.
	 */
	protected synchronized void initGeneralEditorInput(GeneralEditorInput generalEditorInput, ProgressMonitor monitor) {
		boolean reloadArticleContainerWithoutArticles = this.input != null;

		if (input == null && generalEditorInput == null)
				throw new IllegalStateException("input not yet initialized and no input parameter given!"); //$NON-NLS-1$

		if (generalEditorInput == null)
			generalEditorInput = input;
		else if (input != null && !generalEditorInput.equals(input))
			throw new IllegalStateException("this.input != GeneralEditorInput generalEditorInput !!!"); //$NON-NLS-1$

//		monitor.beginTask("Loading article container", 100); // the monitor is directly passed to the DAOs - no need to use a sub-monitor
		try {
			this.input = generalEditorInput;
			this.order = null;
			this.offer = null;
			this.invoice = null;
			this.deliveryNote = null;

			if (input instanceof GeneralEditorInputOrder) {
				OrderID orderID = ((GeneralEditorInputOrder) input).getOrderID();
				if (reloadArticleContainerWithoutArticles)
					articleContainer = order = OrderDAO.sharedInstance().getOrder(orderID, FETCH_GROUPS_ARTICLE_CONTAINER_WITHOUT_ARTICLES, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
				else
					articleContainer = order = OrderDAO.sharedInstance().getOrder(orderID, FETCH_GROUPS_ORDER_WITH_ARTCILES, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
			} else if (input instanceof GeneralEditorInputOffer) {
				OfferID offerID = ((GeneralEditorInputOffer) input).getOfferID();
				if (reloadArticleContainerWithoutArticles)
					articleContainer = offer = OfferDAO.sharedInstance().getOffer(offerID, FETCH_GROUPS_ARTICLE_CONTAINER_WITHOUT_ARTICLES, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
				else
					articleContainer = offer = OfferDAO.sharedInstance().getOffer(offerID, FETCH_GROUPS_OFFER_WITH_ARTICLES, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
			} else if (input instanceof GeneralEditorInputInvoice) {
				InvoiceID invoiceID = ((GeneralEditorInputInvoice) input).getInvoiceID();
				if (reloadArticleContainerWithoutArticles)
					articleContainer = invoice = InvoiceProvider.sharedInstance().getInvoice(invoiceID, FETCH_GROUPS_ARTICLE_CONTAINER_WITHOUT_ARTICLES, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
				else
				articleContainer = invoice = InvoiceProvider.sharedInstance().getInvoice(invoiceID, FETCH_GROUPS_INVOICE_WITH_ARTICLES, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
			} else if (input instanceof GeneralEditorInputDeliveryNote) {
				DeliveryNoteID deliveryNoteID = ((GeneralEditorInputDeliveryNote) input).getDeliveryNoteID();
				if (reloadArticleContainerWithoutArticles)
					articleContainer = deliveryNote = DeliveryNoteDAO.sharedInstance().getDeliveryNote(deliveryNoteID, FETCH_GROUPS_ARTICLE_CONTAINER_WITHOUT_ARTICLES, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
				else
				articleContainer = deliveryNote = DeliveryNoteDAO.sharedInstance().getDeliveryNote(deliveryNoteID, FETCH_GROUPS_DELIVERY_NOTE_WITH_ARTICLES, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
			} else
				throw new IllegalArgumentException("input type \"" + input.getClass().getName() + "\" unknown"); //$NON-NLS-1$ //$NON-NLS-2$

			if (logger.isDebugEnabled())
				logger.debug("initGeneralEditorInput: loaded version " + JDOHelper.getVersion(articleContainer) + " of " + JDOHelper.getObjectId(articleContainer));

			if (!reloadArticleContainerWithoutArticles)
				createArticleSegmentGroups();
		} catch (Exception e) {
			throw new RuntimeException(e);
//		} finally {
//			monitor.done();
		}
	}

	public Menu createArticleContainerContextMenu(Control parent) {
		if (generalEditorActionBarContributor != null)
			return generalEditorActionBarContributor
					.createArticleContainerContextMenu(parent);

		return null;
	}

	public Menu createArticleEditContextMenu(Control parent) {
		return generalEditorActionBarContributor
				.createArticleEditContextMenu(parent);
	}

	/**
	 * @return Returns the ArticleContainer (either
	 *         {@link org.nightlabs.jfire.trade.ui.Order},
	 *         {@link org.nightlabs.jfire.trade.ui.Offer},
	 *         {@link org.nightlabs.jfire.accounting.Invoice},
	 *         {@link org.nightlabs.jfire.store.DeliveryNote} or
	 *         {@link org.nightlabs.jfire.store.ReceptionNote}) <b>WITHOUT</b>
	 *         articles. Hence, you <b>must not</b> call
	 *         {@link ArticleContainer#getArticles()}! Use {@link #getArticles()}
	 *         instead!
	 */
	public ArticleContainer getArticleContainer() {
		return articleContainer;
	}

	public ArticleContainerID getArticleContainerID() {
		return (ArticleContainerID) JDOHelper.getObjectId(articleContainer);
	}

	public Collection<Article> getArticles() {
		if (articleSegmentGroups == null)
			return Collections.emptyList();
		return articleSegmentGroups.getArticles();
	}

	public IWorkbenchPartSite getSite() {
		return site;
	}

//	private List<ArticleChangeListener> earlyArticleChangeListeners = new ArrayList<ArticleChangeListener>();
	private ListenerList earlyArticleChangeListeners = new ListenerList();
	public void addArticleChangeListener(ArticleChangeListener articleChangeListener) {
		if (articleSegmentGroups != null) {
			articleSegmentGroups.addArticleChangeListener(articleChangeListener);
		}
		else {
			earlyArticleChangeListeners.add(articleChangeListener);;
		}
	}
	
	public void removeArticleChangeListener(ArticleChangeListener articleChangeListener) {
		if (articleSegmentGroups != null) {
			articleSegmentGroups.removeArticleChangeListener(articleChangeListener);
		} else {
			logger.warn("ArticleChangeListener not removed because articleSegmentGroups == null!"); //$NON-NLS-1$
		}
	}
	
//	private List<ArticleCreateListener> earlyArticleCreateListeners = new ArrayList<ArticleCreateListener>();
	private ListenerList earlyArticleCreateListeners = new ListenerList();
	public void addArticleCreateListener(ArticleCreateListener articleCreateListener) {
		if (articleSegmentGroups != null) {
			articleSegmentGroups.addArticleCreateListener(articleCreateListener);
		} else {
			earlyArticleCreateListeners.add(articleCreateListener);
		}
	}
	
	public void removeArticleCreateListener(ArticleCreateListener articleCreateListener) {
		if (articleSegmentGroups != null) {
			articleSegmentGroups.removeArticleCreateListener(articleCreateListener);
		} else {
			logger.warn("ArticleCreateListener not removed because articleSegmentGroups == null!"); //$NON-NLS-1$
		}
	}

	public ClientArticleSegmentGroups getArticleSegmentGroups()
	{
		return articleSegmentGroups;
	}
}
