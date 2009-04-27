package org.nightlabs.jfire.trade.ui.transfer.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.security.SecurityReflector;
import org.nightlabs.jfire.store.deliver.Delivery;
import org.nightlabs.jfire.store.deliver.DeliveryQueue;
import org.nightlabs.jfire.store.deliver.DeliveryQueueDAO;
import org.nightlabs.jfire.store.deliver.id.DeliveryID;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.OrganisationLegalEntity;
import org.nightlabs.jfire.trade.TradeManagerRemote;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.id.CustomerGroupID;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Tobias Langner <!-- tobias[dot]langner[at]nightlabs[dot]de -->
 */
public class DeliveryQueueDeliveryWizard extends CombiTransferArticlesWizard {

	private Collection<Delivery> oldDeliveries;
	private DeliveryQueue deliveryQueue;

	private static Collection<ArticleID> getArticleIDs(Collection<Delivery> deliveries) {
		Collection<ArticleID> articleIDs = new LinkedList<ArticleID>();
		for (Delivery delivery : deliveries) {
			articleIDs.addAll(delivery.getArticleIDs());
		}

		return articleIDs;
	}

	public DeliveryQueueDeliveryWizard(Collection<Delivery> deliveries, DeliveryQueue deliveryQueue) {
		super(getArticleIDs(deliveries), AbstractCombiTransferWizard.TRANSFER_MODE_DELIVERY);
		this.oldDeliveries = deliveries;
		this.deliveryQueue = deliveryQueue;
	}

	@Override
	protected void loadData() {
		try {
			getArticlesToTransfer().clear();
			AnchorID customerID = null;

			TradeManagerRemote tradeManager = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
			int alreadyDeliveredArticles = 0;
			Set<DeliveryNoteID> _deliveryNoteIDs = new HashSet<DeliveryNoteID>();
			for (Iterator<?> it = tradeManager.getArticles(getArticleIDs(), FETCH_GROUPS_ARTICLES, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT).iterator(); it.hasNext();) {
				Article article = (Article) it.next();

				if (article.getArticleLocal().isDelivered()) {
					alreadyDeliveredArticles++;
					continue;
				}

				if (isDeliveryEnabled()) {
					_deliveryNoteIDs.add(article.getDeliveryNoteID());
				}

				if (customerID == null)
					customerID = (AnchorID) JDOHelper.getObjectId(article.getOrder().getCustomer());
				else if (!customerID.equals(JDOHelper.getObjectId(article.getOrder().getCustomer())))
					throw new IllegalArgumentException("The passed Articles have differing customers!"); //$NON-NLS-1$

				addCustomerGroupID((CustomerGroupID) JDOHelper.getObjectId(article.getOrder().getCustomerGroup()));
				getArticlesToTransfer().add(article);
				setCustomerID((AnchorID) JDOHelper.getObjectId(article.getOrder().getCustomer()));
			}
			if (alreadyDeliveredArticles > 0) {
				String message = String.format(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.DeliveryQueueDeliveryWizard.articlesAlreadyDeliveredDialogMessage"), alreadyDeliveredArticles); //$NON-NLS-1$
				MessageDialog.openWarning(RCPUtil.getActiveShell(), Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.DeliveryQueueDeliveryWizard.articlesAlreadyDeliveredDialogTitle"), message); //$NON-NLS-1$
			}

			setDeliveryNoteIDs(_deliveryNoteIDs);

			// The LegalEntityID of the local organisation
			AnchorID mandatorID = AnchorID.create(
					SecurityReflector.getUserDescriptor().getOrganisationID(),
					LegalEntity.ANCHOR_TYPE_ID_LEGAL_ENTITY, OrganisationLegalEntity.class.getName());
			setCustomerID(customerID);
			if (mandatorID.equals(customerID))
				setSide(Side.Customer);
			else
				setSide(Side.Vendor);

		} catch (RuntimeException x) {
			throw x;
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}

	@Override
	public boolean performFinish() {
		try {
			getContainer().run(false, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.deliver.DeliveryQueueDeliveryWizard.performFinish.monitor.task.name"), 5); //$NON-NLS-1$
						try {
							Map<ArticleID, Delivery> article2DeliveryMap = new HashMap<ArticleID, Delivery>();
							Map<DeliveryID, Delivery> oldDeliveryMap = new HashMap<DeliveryID, Delivery>(oldDeliveries.size());
							for (Delivery oldDelivery : oldDeliveries) {
								for (ArticleID articleID : oldDelivery.getArticleIDs()) {
									article2DeliveryMap.put(articleID, oldDelivery);
								}
								oldDeliveryMap.put((DeliveryID) JDOHelper.getObjectId(oldDelivery), oldDelivery);
							}
							monitor.worked(1);

							// Assign for each new delivery the original deliveries from the delivery queue as precursor
							for (DeliveryEntryPage page : getDeliveryEntryPages()) {
								for (Delivery delivery : page.getDeliveryWizardHop().getDeliveryList()) {
									Set<DeliveryID> precursorIDs = new HashSet<DeliveryID>();
									for (ArticleID articleID : delivery.getArticleIDs()) {
										precursorIDs.add((DeliveryID) JDOHelper.getObjectId(article2DeliveryMap.get(articleID)));
									}
									delivery.setPrecursorIDSet(precursorIDs);
								}
							}
							monitor.worked(1);

							TransferWizardUtil.deliver(getShell(), DeliveryQueueDeliveryWizard.this);

							// now reload the deliveryQueue object because it may have been altered by the
							// new delivery (if the target and the source delivery queue are the same)
							deliveryQueue = DeliveryQueueDAO.sharedInstance().getDeliveryQueue(deliveryQueue.getObjectID(),
									new String[] { DeliveryQueue.FETCH_GROUP_PENDING_DELIVERY_SET }, -1, new NullProgressMonitor());

							for (DeliveryEntryPage page : getDeliveryEntryPages()) {
								for (Delivery delivery : page.getDeliveryWizardHop().getDeliveryList()) {
									// If the delivery has not failed, we mark all original (old) deliveries that contributed to this one as processed
									if (!delivery.isFailed()) {
										for (DeliveryID deliveryID : delivery.getPrecursorIDSet())
											deliveryQueue.markProcessed(oldDeliveryMap.get(deliveryID));
									}
								}
							}
							monitor.worked(2);
							DeliveryQueueDAO.sharedInstance().storeDeliveryQueue(deliveryQueue, false, null, -1, new NullProgressMonitor());

							monitor.worked(1);
						} finally {
							monitor.done();
						}

					} catch (RuntimeException x) {
						throw x;
					} catch (Exception x) {
						throw new RuntimeException(x);
					}
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}
}