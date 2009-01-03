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
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.EditLockTypeInvoice;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.accounting.InvoiceLocal;
import org.nightlabs.jfire.accounting.dao.InvoiceDAO;
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.base.jdo.JDOObjectID2PCClassMap;
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
import org.nightlabs.jfire.store.dao.DeliveryNoteDAO;
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
import org.nightlabs.jfire.trade.SegmentType;
import org.nightlabs.jfire.trade.dao.OfferDAO;
import org.nightlabs.jfire.trade.dao.OrderDAO;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.recurring.RecurringOffer;
import org.nightlabs.jfire.trade.recurring.RecurringOrder;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleContainerEditorActionBarContributor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.IArticleContainerEditActionContributor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.deliverynote.DeliveryNoteFooterComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.deliverynote.DeliveryNoteHeaderComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice.InvoiceFooterComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice.InvoiceHeaderComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.OfferFooterComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.OfferHeaderComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.order.OrderFooterComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.order.OrderHeaderComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.RecurringOfferFooterComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.RecurringOrderFooterComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

/**
 * This composite might be used to implement registered {@link ArticleContainerEdit} or directly in ui-code.
 * One of its known uses is the implementation of {@link DefaultArticleContainerEdit}.
 * It might also be sub-classed in order to configure the header and footer composites.
 * <p>
 * For its main part it loads the {@link ArticleContainer} from the articleContainerID it is instantiated with
 * and creates a {@link ClientArticleSegmentGroupSet} to manage the articles within the container.
 * It also asks the {@link org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEditFactoryRegistry}
 * for the right factories for all the {@link org.nightlabs.jfire.trade.ui.Segment}s
 * and displays the edits which are delivered from the factory.
 * </p>
 *
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ArticleContainerEditComposite
extends XComposite
implements ArticleContainerEdit
{
	private static final Logger logger = Logger.getLogger(ArticleContainerEditComposite.class);

	private ArticleContainerID articleContainerID = null;

	/**
	 * This is initialized by
	 * {@link ArticleContainerEditorActionBarContributor#setActiveEditor(IEditorPart)} as
	 * soon as the Editor became active the first time.
	 */
	private IArticleContainerEditActionContributor articleContainerEditActionContributor = null;

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

	private Label loadingDataLabel;

	/**
	 * @param parent
	 * @param style
	 */
	public ArticleContainerEditComposite(Composite parent, ArticleContainerID _articleContainerID) {
		super(parent, SWT.NONE, LayoutMode.ORDINARY_WRAPPER);

		loadingDataLabel = new Label(this, SWT.NONE);
		loadingDataLabel.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditComposite.label.loadingData")); //$NON-NLS-1$

		loadInitialArticleContainerJob = new LoadInitialArticleContainerJob(_articleContainerID);
		loadInitialArticleContainerJob.schedule();
	}

	private LoadInitialArticleContainerJob loadInitialArticleContainerJob;

	private class LoadInitialArticleContainerJob extends Job
	{
		private ArticleContainerID loadArticleContainerID;

		public LoadInitialArticleContainerJob(ArticleContainerID articleContainerID)
		{
			super(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditComposite.job.loadingArticleContainer")); //$NON-NLS-1$
			this.loadArticleContainerID = articleContainerID;
			assert articleContainerID != null : "loadArticleContainerID != null"; //$NON-NLS-1$
		}

		@Override
		protected org.eclipse.core.runtime.IStatus run(ProgressMonitor monitor) throws Exception
		{
			loadInitialArticleContainerJob = null; // release memory

			initArticleContainer(loadArticleContainerID, monitor);

			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					if (ArticleContainerEditComposite.this.isDisposed())
						return;

					loadingDataLabel.dispose();
					loadingDataLabel = null;

					headerComposite = createHeaderComposite(ArticleContainerEditComposite.this);
					headerComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

					new Label(ArticleContainerEditComposite.this, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

					// TODO: segments can be potentially added on the fly, therefore ArticleContainerEditComposite.this behaviour must be supported
					if (hasDifferentSegments()) {
						segmentCompositeFolder = new TabFolder(ArticleContainerEditComposite.this, SWT.NONE);
						segmentCompositeFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
						((TabFolder)segmentCompositeFolder).addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								updateActiveSegmentEdit();
							}
						});
					}
					else {
						segmentCompositeFolder = new XComposite(ArticleContainerEditComposite.this, SWT.NONE, LayoutMode.TOTAL_WRAPPER);
					}

					new Label(ArticleContainerEditComposite.this, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

					footerComposite = createFooterComposite(ArticleContainerEditComposite.this);

					footerComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					footerComposite.refresh();

					EditLockTypeID editLockTypeID = getEditLockTypeID();

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
//							OrderID orderID = (OrderID) articleContainerID;
//							if (orderID.organisationID == null) {
//							logger.warn("orderID.organisationID == null", new NullPointerException("orderID.organisationID == null")); //$NON-NLS-1$ //$NON-NLS-2$
//							orderID.organisationID = order.getOrganisationID();
//							}
//							if (orderID.orderIDPrefix == null) {
//							logger.warn("orderID.orderIDPrefix == null", new NullPointerException("orderID.orderIDPrefix == null")); //$NON-NLS-1$ //$NON-NLS-2$
//							orderID.orderIDPrefix = order.getOrderIDPrefix();
//							}
//							}
//							// TODO WORKAROUND JPOX bug end

							editLockHandle.release();
							if (articleSegmentGroupSet != null)
								articleSegmentGroupSet.onDispose();
							// removeDisposeListener(this); // nötig? Marco.
						}
					});

					// it is likely that the action-bar-contributor has been set too early for creating the UI, hence, we call it now.
					if (articleContainerEditActionContributor != null)
						setArticleContainerEditActionContributor(articleContainerEditActionContributor);
					try {
						if (!segmentEditCompositesCreated)
							createSegmentEditComposites();
					} catch (EPProcessorException e) {
						throw new RuntimeException(e);
					}
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
//				logger.debug("");
//				}
				return;
			}

			// reload the new ArticleContainer from the server
			initArticleContainer(articleContainerID, new ProgressMonitorWrapper(getProgressMonitor()));

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

	public ArticleContainerID getArticleContainerID() {
		return articleContainerID;
	}

	/**
	 * This method is called to create the {@link HeaderComposite} of this composite.
	 * It might be overridden to create custom headers.
	 * <p>
	 * This implementations will check if one of the supported implementations of
	 * {@link ArticleContainer} was loaded and then create one of the following:
	 * {@link OrderHeaderComposite}, {@link OfferHeaderComposite}, {@link InvoiceHeaderComposite}
	 * or {@link DeliveryNoteHeaderComposite}.
	 * </p>
	 * @param parent The parent to use for the new composite.
	 * @return A newly created {@link HeaderComposite}.
	 */
	protected HeaderComposite createHeaderComposite(Composite parent) {

		if (articleContainer instanceof Order)
			return new OrderHeaderComposite(this, (Order) articleContainer);
		if(articleContainer instanceof Offer)
			return new OfferHeaderComposite(this, (Offer) articleContainer);
		if (articleContainer instanceof Invoice)
			return new InvoiceHeaderComposite(this, (Invoice) articleContainer);
		if (articleContainer instanceof DeliveryNote)
			return new DeliveryNoteHeaderComposite(this, (DeliveryNote) articleContainer);

		throw new IllegalStateException("The current ArticleContainer is of an unsupported type: " + //$NON-NLS-1$
				(getArticleContainer() != null ? getArticleContainer().getClass().getName() : "null") + "."); //$NON-NLS-1$ //$NON-NLS-2$
	}


	/**
	 * This method is called to create the {@link FooterComposite} of this composite.
	 * It might be overridden to create custom footers.
	 * <p>
	 * This implementations will check if one of the supported implementations of
	 * {@link ArticleContainer} was loaded and then create one of the following:
	 * {@link OrderFooterComposite}, {@link OfferFooterComposite}, {@link InvoiceFooterComposite}
	 * or {@link DeliveryNoteFooterComposite}.
	 * </p>
	 * @param parent The parent to use for the new composite.
	 * @return A newly created {@link FooterComposite}.
	 */
	protected FooterComposite createFooterComposite(Composite parent) {

		if (articleContainer instanceof Order)
			return new OrderFooterComposite(parent, this);
		if (articleContainer instanceof RecurringOrder)
			return new RecurringOrderFooterComposite(parent, this);
		if (articleContainer instanceof Offer)
			return new OfferFooterComposite(parent, this);
		if (articleContainer instanceof RecurringOffer)
			return new RecurringOfferFooterComposite(parent, this);
		if (articleContainer instanceof Invoice)
			return new InvoiceFooterComposite(parent, this);
		if (articleContainer instanceof DeliveryNote)
			return new DeliveryNoteFooterComposite(parent, this);

		throw new IllegalStateException("The current ArticleContainer is of an unsupported type: " + //$NON-NLS-1$
				(getArticleContainer() != null ? getArticleContainer().getClass().getName() : "null") + "."); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * This method is called to determine the {@link EditLockTypeID}
	 * that should be used to acquire an edit lock for the edited ArticleContainer.
	 * This implementation supports Orders, Offers, Invoices and DeliveryNotes
	 * for all other implementations of {@link ArticleContainer} this method
	 * should be overridden.
	 *
	 * @return The {@link EditLockTypeID} that should be used to acquire an edit lock.
	 */
	protected EditLockTypeID getEditLockTypeID() {

		if (articleContainer instanceof RecurringOrder)
			return EditLockTypeOrder.EDIT_LOCK_TYPE_ID;
		if (articleContainer instanceof RecurringOffer)
			return EditLockTypeOffer.EDIT_LOCK_TYPE_ID;
		if (articleContainer instanceof Order)
			return EditLockTypeOrder.EDIT_LOCK_TYPE_ID;
		if (articleContainer instanceof Offer)
			return EditLockTypeOffer.EDIT_LOCK_TYPE_ID;
		if (articleContainer instanceof Invoice)
			return EditLockTypeInvoice.EDIT_LOCK_TYPE_ID;
		if (articleContainer instanceof DeliveryNote)
			return EditLockTypeDeliveryNote.EDIT_LOCK_TYPE_ID;

		throw new IllegalStateException("The current ArticleContainer is of an unsupported type: " + //$NON-NLS-1$
				(getArticleContainer() != null ? getArticleContainer().getClass().getName() : "null") + "."); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected boolean hasDifferentSegments()
	{
		return articleSegmentGroupSet.getArticleSegmentGroups().size() > 1;
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
	public IArticleContainerEditActionContributor getArticleContainerEditActionContributor() {
		return articleContainerEditActionContributor;
	}

	/**
	 * This method is called by
	 * {@link ArticleContainerEditorActionBarContributor#setActiveEditor(IEditorPart)}.
	 */
	@Override
	public void setArticleContainerEditActionContributor(IArticleContainerEditActionContributor articleContainerEditActionContributor) {
		this.articleContainerEditActionContributor = articleContainerEditActionContributor;
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

		// TODO Segments can be added while this ArticleContainerEditComposite is visible. Unfortunately,
		// this was not taken into account when Daniel refactored this class (he removed the TabFolder
		// when there's only one Segment). After his refactoring, it means that we would need to
		// rebuild the UI if a Segment is added! Marco.

		TabItem tabItem = null;
		if (hasDifferentSegments()) {
			tabItem = new TabItem((TabFolder)segmentCompositeFolder, SWT.NONE);
			tabItem.setText(
					String.format(
							Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditComposite.segmentTabItem.text"), //$NON-NLS-1$
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

		// TODO Segments can be added while this ArticleContainerEditComposite is visible. See comment at the beginning of this method! Marco.
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

	private ClientArticleSegmentGroupSet articleSegmentGroupSet = null;

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
//			articleCreateListenerArray[i] = earlyArticleCreateListeners.get(i);
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
//			articleChangeListenerArray[i] = earlyArticleChangeListeners.get(i);
//			}
			articleChangeListenerArray[articleChangeListenerArray.length-1] = articleChangeListener;
		}
		else {
			articleChangeListenerArray = new ArticleChangeListener[] { articleChangeListener };
		}

		articleSegmentGroupSet = new ClientArticleSegmentGroupSet(articleContainer,
				articleCreateListenerArray,
				articleChangeListenerArray);
	}

	protected void createSegmentEditComposites()
	throws EPProcessorException
	{
		segmentEditCompositesCreated = true;

		// ArticleSegmentGroupSet asgs = new ArticleSegmentGroupSet(articleContainer);
		for (ArticleSegmentGroup articleSegmentGroup : articleSegmentGroupSet.getArticleSegmentGroups())
			createSegmentEditAndComposite(articleSegmentGroup);

		updateActiveSegmentEdit();

		// The following line is important! Otherwise, it wouldn't scroll correctly!
		calculateScrollContentSize();
	}

	protected void createSegmentEditAndComposite(ArticleSegmentGroup asg) {
		Segment segment = asg.getSegment();
		SegmentType segmentType = segment.getSegmentType();
		Class<?> articleContainerClass = JDOObjectID2PCClassMap.sharedInstance().getPersistenceCapableClass(articleContainerID);
		SegmentEditFactory sef = SegmentEditFactoryRegistry.sharedInstance().getSegmentEditFactory(articleContainerClass, segmentType.getClass(), true);

		SegmentEdit segmentEdit = sef.createSegmentEdit(this, articleContainerClass, asg);
		segmentEdit.addCompositeContentChangeListener(segmentCompositeContentChangeListener);

		createSegmentEditComposite(asg, segmentEdit);

		if (activeSegmentEdit == null) {
			activeSegmentEdit = segmentEdit;
			segmentEdit.getComposite().setFocus();
			updateActiveSegmentEdit();
		}
	}

	private ArticleCreateListener articleCreateListener = new ArticleCreateListener() {
		public void articlesCreated(final ArticleCreateEvent articleCreateEvent) {
			Runnable runnable = new Runnable() {
				public void run()
				{
					Map<SegmentEdit, List<ArticleCarrier>> segmentEdit2ArticleCarriers = new HashMap<SegmentEdit, List<ArticleCarrier>>();
					Set<ArticleSegmentGroup> articleSegmentGroupsWithoutSegmentEdit = new HashSet<ArticleSegmentGroup>();

					for (ArticleCarrier articleCarrier : articleCreateEvent.getArticleCarriers()) {
						Article article = articleCarrier.getArticle();
						SegmentEdit segmentEdit = segmentPK2segmentEditMap.get(article.getSegment().getPrimaryKey());
						if (segmentEdit == null) {
							// we do not yet have a SegmentEdit for article
							articleSegmentGroupsWithoutSegmentEdit.add(articleCarrier.getArticleSegmentGroup());

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


					updateHeaderAndFooter();
				}
			};

			if (segmentCompositeFolder == null) // too early - try again later
				Display.getDefault().asyncExec(runnable);
			else {
				if (!segmentCompositeFolder.isDisposed()) // if we're not too late, do it now
					runnable.run();
			}
		}
	};

	private ArticleChangeListener articleChangeListener = new ArticleChangeListener() {
		public void articlesChanged(final ArticleChangeEvent articleChangeEvent) {
			Runnable runnable = new Runnable() {
				public void run()
				{
					if (!articleChangeEvent.getDeletedArticles().isEmpty()) {
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
					}

					updateHeaderAndFooter(); // the header + footer might iterate articles instead of using summary information => need to update whenever an article changes.
				}
			};

			if (segmentCompositeFolder == null) // too early - try again later
				Display.getDefault().asyncExec(runnable);
			else {
				if (!segmentCompositeFolder.isDisposed()) // if we're not too late, do it now
					runnable.run();
			}
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

	@Override
	public void addActiveSegmentEditSelectionListener(
			ActiveSegmentEditSelectionListener listener) {
		activeSegmentEditSelectionListeners.add(listener);
	}

	@Override
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
		for (SegmentEdit segmentEdit : segmentEditsByTabItem.values()) {
			calculateScrollContentSize(segmentEdit);
//			Composite segmentEditComposite = segmentEdit.getComposite();
//			ScrolledComposite segmentCompositeScrollContainer = (ScrolledComposite) segmentEditComposite
//			.getParent();

//			// ScrolledComposite segmentCompositeScrollContainer = (ScrolledComposite)
//			// it.next();
//			// Control[] children = segmentCompositeScrollContainer.getChildren();
//			// if (children.length != 1)
//			// throw new IllegalStateException("segmentCompositeScrollContainer has "
//			// + children.length + " child controls instead of 1!");
//			//
//			// Composite segmentEditComposite = (Composite) children[0];

//			Rectangle bounds = segmentEditComposite.getBounds();
//			bounds.width = segmentCompositeScrollContainer.getClientArea().width
//			- segmentCompositeScrollContainer.getVerticalBar().getSize().x;
//			// segmentCompositeContainer.setBounds(bounds);

//			segmentCompositeScrollContainer.setMinSize(segmentEditComposite
//			.computeSize(bounds.width, SWT.DEFAULT));
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
	// JFireEjbFactory.getBean(TradeManager.class, Login.getLogin().getInitialContextProperties());
	// }
	//
	// protected StoreManager getStoreManager()
	// throws RemoteException, LoginException, CreateException, NamingException
	// {
	// return
	// JFireEjbFactory.getBean(StoreManager.class, Login.getLogin().getInitialContextProperties());
	// }

	private SegmentEdit activeSegmentEdit = null;

	/**
	 * @return Returns either <code>null</code> if there is no SegmentEdit
	 *         active or the one that is.
	 */
	@Override
	public SegmentEdit getActiveSegmentEdit() {
		return activeSegmentEdit;
	}

	@Override
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

	public static final String[] FETCH_GROUPS_ORDER_WITH_ARTICLES = {
		FetchGroupsTrade.FETCH_GROUP_ARTICLE_CONTAINER_IN_EDITOR,
		Order.FETCH_GROUP_THIS_ORDER, Segment.FETCH_GROUP_THIS_SEGMENT,
		SegmentType.FETCH_GROUP_THIS_SEGMENT_TYPE,
		FetchGroupsTrade.FETCH_GROUP_ARTICLE_IN_ORDER_EDITOR, FetchPlan.DEFAULT };

	public static final String[] FETCH_GROUPS_OFFER_WITH_ARTICLES = {
		FetchGroupsTrade.FETCH_GROUP_ARTICLE_CONTAINER_IN_EDITOR,
		Offer.FETCH_GROUP_ARTICLES,
		OfferLocal.FETCH_GROUP_THIS_OFFER_LOCAL,
		StatableLocal.FETCH_GROUP_STATE, Order.FETCH_GROUP_CUSTOMER_GROUP,
		Segment.FETCH_GROUP_THIS_SEGMENT,
		SegmentType.FETCH_GROUP_THIS_SEGMENT_TYPE,
		FetchGroupsTrade.FETCH_GROUP_ARTICLE_IN_OFFER_EDITOR, FetchPlan.DEFAULT };

	public static final String[] FETCH_GROUPS_INVOICE_WITH_ARTICLES = {
		FetchGroupsTrade.FETCH_GROUP_ARTICLE_CONTAINER_IN_EDITOR,
		Invoice.FETCH_GROUP_THIS_INVOICE,
		InvoiceLocal.FETCH_GROUP_THIS_INVOICE_LOCAL,
		StatableLocal.FETCH_GROUP_STATE, Segment.FETCH_GROUP_THIS_SEGMENT,
		SegmentType.FETCH_GROUP_THIS_SEGMENT_TYPE,
		FetchGroupsTrade.FETCH_GROUP_ARTICLE_IN_INVOICE_EDITOR, FetchPlan.DEFAULT };

	public static final String[] FETCH_GROUPS_DELIVERY_NOTE_WITH_ARTICLES = {
		FetchGroupsTrade.FETCH_GROUP_ARTICLE_CONTAINER_IN_EDITOR,
		DeliveryNote.FETCH_GROUP_THIS_DELIVERY_NOTE,
		DeliveryNoteLocal.FETCH_GROUP_THIS_DELIVERY_NOTE_LOCAL,
		StatableLocal.FETCH_GROUP_STATE, Segment.FETCH_GROUP_THIS_SEGMENT,
		SegmentType.FETCH_GROUP_THIS_SEGMENT_TYPE,
		FetchGroupsTrade.FETCH_GROUP_ARTICLE_IN_DELIVERY_NOTE_EDITOR,
		FetchPlan.DEFAULT };

	/**
	 * Initialise this instance of <code>ArticleContainerEditComposite</code> or reload the {@link ArticleContainer} referenced
	 * by the <code>loadArticleContainerID</code> parameter. In case of reloading, the articles are not fetched again
	 * (they're managed separately by the {@link ClientArticleSegmentGroupSet} in {@link #articleSegmentGroupSet})
	 *
	 * @param _articleContainerID the articleContainerID to be set.
	 * @param monitor the monitor to provide feedback.
	 */
	protected synchronized void initArticleContainer(ArticleContainerID _articleContainerID, ProgressMonitor monitor)
	{
		// When reloading an ArticleContainer (because it changed), we do *NOT* load the articles again, because
		// they are independently updated.
		boolean reloadArticleContainerWithoutArticles = this.articleContainerID != null;

		if (this.articleContainerID == null && _articleContainerID == null)
			throw new IllegalStateException("articleContainerID not yet initialized and no articleContainerID parameter given!"); //$NON-NLS-1$

		if (_articleContainerID == null)
			_articleContainerID = articleContainerID;
		else if (articleContainerID != null && !_articleContainerID.equals(articleContainerID))
			throw new IllegalStateException("this.aritcleContainerID != ArticleContainerID articleContainerID !!!"); //$NON-NLS-1$

//		monitor.beginTask("Loading article container", 100); // the monitor is directly passed to the DAOs - no need to use a sub-monitor
		try {
			this.articleContainerID = _articleContainerID;
			this.articleContainer = retrieveArticleContainer(articleContainerID, !reloadArticleContainerWithoutArticles, monitor);

			if (logger.isDebugEnabled())
				logger.debug("initArticleContainerEditorInput: loaded version " + JDOHelper.getVersion(articleContainer) + " of " + JDOHelper.getObjectId(articleContainer)); //$NON-NLS-1$ //$NON-NLS-2$

			if (!reloadArticleContainerWithoutArticles)
				createArticleSegmentGroups();
		} catch (Exception e) {
			throw new RuntimeException(e);
//			} finally {
//			monitor.done();
		}
	}

	/**
	 * Get an {@link ArticleContainer} from the server (or the cache, if present there) using a DAO.
	 * Override this method, if you need to retrieve a custom <code>ArticleContainer</code> implementation
	 * with specialized fetch-groups.
	 *
	 * @param articleContainerID the object-id of the {@link ArticleContainer} to be loaded.
	 * @param withArticles <code>true</code>, if the {@link ArticleContainer} should be loaded with its {@link Article}s;
	 *		<code>false</code>, if the <code>Article</code>s should <b>not</b> be detached. You must take this into account
	 *		when choosing your fetch-groups. This is necessary, because it makes no sense to detach a huge <code>ArticleContainer</code>
	 *		with all its articles again (when it changed), even though most of the articles didn't change. That's why,
	 *		the articles are independently updated ({@link ClientArticleSegmentGroupSet} takes care about that).
	 * @param monitor the monitor for progress feedback.
	 * @return the {@link ArticleContainer} for the specified {@link ArticleContainerID}.
	 */
	protected ArticleContainer retrieveArticleContainer(ArticleContainerID articleContainerID, boolean withArticles, ProgressMonitor monitor)
	{
		if (articleContainerID instanceof OrderID)
			return OrderDAO.sharedInstance().getOrder(
					(OrderID) articleContainerID,
					withArticles ? FETCH_GROUPS_ORDER_WITH_ARTICLES : FETCH_GROUPS_ARTICLE_CONTAINER_WITHOUT_ARTICLES,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					monitor
			);

		if (articleContainerID instanceof OfferID)
			return OfferDAO.sharedInstance().getOffer(
					(OfferID) articleContainerID,
					withArticles ? FETCH_GROUPS_OFFER_WITH_ARTICLES : FETCH_GROUPS_ARTICLE_CONTAINER_WITHOUT_ARTICLES,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					monitor
			);

		if (articleContainerID instanceof InvoiceID)
			return InvoiceDAO.sharedInstance().getInvoice(
					(InvoiceID) articleContainerID,
					withArticles ? FETCH_GROUPS_INVOICE_WITH_ARTICLES : FETCH_GROUPS_ARTICLE_CONTAINER_WITHOUT_ARTICLES,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					monitor
			);

		if (articleContainerID instanceof DeliveryNoteID)
			return DeliveryNoteDAO.sharedInstance().getDeliveryNote(
					(DeliveryNoteID) articleContainerID,
					withArticles ? FETCH_GROUPS_DELIVERY_NOTE_WITH_ARTICLES : FETCH_GROUPS_ARTICLE_CONTAINER_WITHOUT_ARTICLES,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					monitor
			);

//		if (articleContainerID instanceof ReceptionNoteID)
//			return ReceptionNoteDAO.sharedInstance().getReceptionNote(
//					(ReceptionNoteID) articleContainerID,
//					fetchGroups,
//					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//					monitor
//			);

		throw new IllegalArgumentException("articleContainerID type \"" + articleContainerID.getClass().getName() + "\" unknown"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public Menu createArticleContainerContextMenu(Control parent) {
		if (articleContainerEditActionContributor != null)
			return articleContainerEditActionContributor.createArticleContainerContextMenu(parent);

		return null;
	}

	public Menu createArticleEditContextMenu(Control parent) {
		return articleContainerEditActionContributor.createArticleEditContextMenu(parent);
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
	@Override
	public ArticleContainer getArticleContainer() {
		return articleContainer;
	}

	@Override
	public Collection<Article> getArticles() {
		if (articleSegmentGroupSet == null)
			return Collections.emptyList();
		return articleSegmentGroupSet.getArticles();
	}

//	private List<ArticleChangeListener> earlyArticleChangeListeners = new ArrayList<ArticleChangeListener>();
	private ListenerList earlyArticleChangeListeners = new ListenerList();
	@Override
	public void addArticleChangeListener(ArticleChangeListener articleChangeListener) {
		if (articleSegmentGroupSet != null) {
			articleSegmentGroupSet.addArticleChangeListener(articleChangeListener);
		}
		else {
			earlyArticleChangeListeners.add(articleChangeListener);;
		}
	}

	@Override
	public void removeArticleChangeListener(ArticleChangeListener articleChangeListener) {
		if (articleSegmentGroupSet != null) {
			articleSegmentGroupSet.removeArticleChangeListener(articleChangeListener);
		} else {
			logger.warn("ArticleChangeListener not removed because articleSegmentGroupSet == null!"); //$NON-NLS-1$
		}
	}

//	private List<ArticleCreateListener> earlyArticleCreateListeners = new ArrayList<ArticleCreateListener>();
	private ListenerList earlyArticleCreateListeners = new ListenerList();
	@Override
	public void addArticleCreateListener(ArticleCreateListener articleCreateListener) {
		if (articleSegmentGroupSet != null) {
			articleSegmentGroupSet.addArticleCreateListener(articleCreateListener);
		} else {
			earlyArticleCreateListeners.add(articleCreateListener);
		}
	}

	@Override
	public void removeArticleCreateListener(ArticleCreateListener articleCreateListener) {
		if (articleSegmentGroupSet != null) {
			articleSegmentGroupSet.removeArticleCreateListener(articleCreateListener);
		} else {
			logger.warn("ArticleCreateListener not removed because articleSegmentGroupSet == null!"); //$NON-NLS-1$
		}
	}

	@Override
	public ClientArticleSegmentGroupSet getArticleSegmentGroupSet()
	{
		return articleSegmentGroupSet;
	}

	@Override
	public Composite createComposite(Composite parent) {
		return this;
	}

	@Override
	public Composite getComposite() {
		return this;
	}

	@Override
	public void init(ArticleContainerID articleContainerID) {
		// Noop, initialized in constructor.
	}
}
