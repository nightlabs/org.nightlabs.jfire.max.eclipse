package org.nightlabs.jfire.trade.transfer.wizard;

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
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.store.deliver.Delivery;
import org.nightlabs.jfire.store.deliver.id.DeliveryID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.id.CustomerGroupID;
import org.nightlabs.jfire.trade.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;

/**
 * @author Tobias Langner <!-- tobias[dot]langner[at]nightlabs[dot]de -->
 */
public class DeliveryQueueDeliveryWizard extends CombiTransferArticlesWizard {

	private Collection<Delivery> oldDeliveries;

	private static Collection<ArticleID> getArticleIDs(Collection<Delivery> deliveries) {
		Collection<ArticleID> articleIDs = new LinkedList<ArticleID>();
		for (Delivery delivery : deliveries) {
			articleIDs.addAll(delivery.getArticleIDs());
		}
		
		return articleIDs;
	}

	public DeliveryQueueDeliveryWizard(Collection<Delivery> deliveries) {
		super(getArticleIDs(deliveries), AbstractCombiTransferWizard.TRANSFER_MODE_DELIVERY, TransferWizard.Side.Vendor);
		this.oldDeliveries = deliveries;
	}

	@Override
	protected void loadData() {
		try {
			getArticlesToTransfer().clear();

			TradeManager tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			int alreadyDeliveredArticles = 0;
			for (Iterator<?> it = tradeManager.getArticles(getArticleIDs(), FETCH_GROUPS_ARTICLES, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT).iterator(); it.hasNext();) {
				Article article = (Article) it.next();
				
				if (article.getArticleLocal().isDelivered()) {
					alreadyDeliveredArticles++;
					continue;
				}
				
				addCustomerGroupID((CustomerGroupID) JDOHelper.getObjectId(article.getOrder().getCustomerGroup()));
				getArticlesToTransfer().add(article);
				
				setCustomerID((AnchorID) JDOHelper.getObjectId(article.getOrder().getCustomer()));
			}
			if (alreadyDeliveredArticles > 0) {
				String message = String.format(Messages.getString("org.nightlabs.jfire.trade.transfer.wizard.DeliveryQueueDeliveryWizard.articlesAlreadyDeliveredDialogMessage"), alreadyDeliveredArticles); //$NON-NLS-1$
				MessageDialog.openWarning(RCPUtil.getActiveWorkbenchShell(), Messages.getString("org.nightlabs.jfire.trade.transfer.wizard.DeliveryQueueDeliveryWizard.articlesAlreadyDeliveredDialogTitle"), message); //$NON-NLS-1$
			}
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
						monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.transfer.deliver.DeliveryQueueDeliveryWizard.performFinish.monitor.task.name"), 4); //$NON-NLS-1$

						Map<ArticleID, Delivery> article2DeliveryMap = new HashMap<ArticleID, Delivery>();
						for (Delivery oldDelivery : oldDeliveries) {
							for (ArticleID articleID : oldDelivery.getArticleIDs()) {
								article2DeliveryMap.put(articleID, oldDelivery);
							}
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
						
						if (!TransferWizardUtil.deliver(getShell(), DeliveryQueueDeliveryWizard.this)) {
							// the TransferWizardUtil already shows a specialised ErrorDialog
						}
						monitor.worked(2);
						monitor.done();
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