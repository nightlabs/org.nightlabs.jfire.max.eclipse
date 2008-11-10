package org.nightlabs.jfire.scripting.print.ui.transfer.delivery;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.jfire.store.id.ProductID;
import org.nightlabs.jfire.trade.ILayout;
import org.nightlabs.jfire.trade.LayoutMapForArticleIDSet;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.util.CacheDirTag;
import org.nightlabs.util.IOUtil;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractScriptDataProviderThread
extends Thread
{
	private static final Logger logger = Logger.getLogger(AbstractScriptDataProviderThread.class);

	private AbstractClientDeliveryProcessorPrint clientDeliveryProcessor;
//	private AbstractClientDeliveryProcessorFactory clientDeliveryProcessorFactoryPrint;
	private volatile Throwable error;
	private Map<ProductID, Map<ScriptRegistryItemID, Object>> scriptingResult;
	private Object scriptingResultMutex = new Object();
	/**
	 * It's essential, that an item is always added to {@link #articleIDsReady} AND
	 * removed from this list within the same synchronized block (on {@link #articleIDsMutex}).
	 * That means, it must be kept in this list, until it is processed
	 * completely.
	 */
	private LinkedList<ArticleID> articleIDsToProcess = new LinkedList<ArticleID>();
	private LinkedList<ArticleID> articleIDsReady = new LinkedList<ArticleID>();
	private Object articleIDsMutex = new Object();

	public AbstractScriptDataProviderThread(AbstractClientDeliveryProcessorPrint clientDeliveryProcessor)
	{
		this.clientDeliveryProcessor = clientDeliveryProcessor;
//		this.clientDeliveryProcessorFactoryPrint = (AbstractClientDeliveryProcessorFactory) clientDeliveryProcessor.getClientDeliveryProcessorFactory();
		this.articleIDsToProcess.addAll(this.clientDeliveryProcessor.getDelivery().getArticleIDs());
		this.bulkProcessSize = clientDeliveryProcessor.getPreferredDocumentCount();
		if (this.bulkProcessSize < 1)
			this.bulkProcessSize = 1;
		
		if (logger.isDebugEnabled())
			logger.debug("New instance of TicketDataProviderThread created."); //$NON-NLS-1$
	}

//	/**
//	 * returns the Layout File of the ProductID
//	 *
//	 * @param productID the ProductID
//	 * @return the Layout File of the ProductID
//	 */
//	public abstract File getLayoutFile(ProductID productID);

	/**
	 * @return Returns <code>true</code>, if the method {@link #fetchReadyArticleID()}
	 *		would immediately return (either with an object result or <code>null</code>).
	 *		Returns <code>false</code>, if a call to {@link #fetchReadyArticleID()}
	 *		would block.
	 */
	public boolean canInvokeFetchReadyArticleIDWithoutBlocking()
	{
		synchronized (articleIDsMutex) {
			return articleIDsToProcess.isEmpty() || !articleIDsReady.isEmpty();
		}
	}

	/**
	 * If there is currently no articleID ready to be printed, but still articleIDs to be processed,
	 * this method will block until one becomes ready. If there is nothing ready and nothing left
	 * to be processed, this method returns <code>null</code>.
	 * @see #canInvokeFetchReadyArticleIDWithoutBlocking()
	 */
	public ArticleID fetchReadyArticleID() throws Throwable
	{
		if (logger.isDebugEnabled())
			logger.debug("fetchReadyArticleID: enter"); //$NON-NLS-1$

		synchronized (articleIDsMutex) {
			if (articleIDsToProcess.isEmpty() && articleIDsReady.isEmpty()) {
				if (logger.isDebugEnabled())
					logger.debug("fetchReadyArticleID: returning null"); //$NON-NLS-1$

				return null;
			}

			if (logger.isDebugEnabled())
				logger.debug("fetchReadyArticleID: beginning to wait"); //$NON-NLS-1$

			while (error == null && articleIDsReady.isEmpty()) {
				try {
					articleIDsMutex.wait(10000);
				} catch (InterruptedException e) {
					// ignore
				}
				if (logger.isDebugEnabled() && articleIDsReady.isEmpty())
					logger.debug("fetchReadyArticleID: still no articleID ready => continue waiting"); //$NON-NLS-1$
			}
			if (error != null)
				throw error;

			if (logger.isDebugEnabled())
				logger.debug("fetchReadyArticleID: removing and returning first ready article. articleIDsReady.size()=" + articleIDsReady.size()); //$NON-NLS-1$

			return articleIDsReady.removeFirst();
		}
	}

	public Throwable getError() {
		return error;
	}

	private volatile boolean interrupted = false;

	@Override
	public void interrupt() {
		interrupted = true;
		super.interrupt();
	}

	@Override
	public boolean isInterrupted() {
		return interrupted || super.isInterrupted();
	}

	protected boolean isArticleIDsToProcessEmpty()
	{
		synchronized (articleIDsMutex) {
			return articleIDsToProcess.isEmpty();
		}
	}

	protected Map<ScriptRegistryItemID, Object> getScriptResultMap(ProductID ticketID, boolean throwExceptionIfNotFound)
	{
		Map<ScriptRegistryItemID, Object> scriptResultMap;
		synchronized (scriptingResultMutex) {
			scriptResultMap = scriptingResult.get(ticketID);
		}
		if (throwExceptionIfNotFound && scriptResultMap == null)
			throw new IllegalArgumentException("No scriptResult found for " + ticketID); //$NON-NLS-1$

		return scriptResultMap;
	}

//	public abstract Map<ProductID, Map<ScriptRegistryItemID, Object>> getScriptingResult();
	protected Map<ProductID, Map<ScriptRegistryItemID, Object>> getScriptingResult()
	{
		return scriptingResult;
	}

//	/**
//	 * returns the corresponding {@link ProductID} for the given {@link ArticleID}
//	 *
//	 * @param articleID the {@link ArticleID}
//	 * @param throwExceptionIfNotFound determines if a Exception should be thrown if no
//	 * product could be found for the given articleID
//	 * @return the corresponding {@link ProductID} for the given {@link ArticleID}
//	 */
//	public abstract ProductID getProductID(ArticleID articleID, boolean throwExceptionIfNotFound);
	protected ProductID getProductID(ArticleID articleID, boolean throwExceptionIfNotFound)
	{
		ProductID productID;
		synchronized (layoutMapForArticleIDSetMutex) {
			productID = layoutMapForArticleIDSet == null ? null : layoutMapForArticleIDSet.getArticleID2ProductIDMap().get(articleID);
		}

		if (throwExceptionIfNotFound && productID == null)
			throw new IllegalArgumentException("No artcileID found for " + articleID); //$NON-NLS-1$

		return productID;
	}

	private Object layoutMapForArticleIDSetMutex = new Object();
	private LayoutMapForArticleIDSet layoutMapForArticleIDSet;

	private File cacheDir;

	/**
	 * If you inherit this class and override this method, you <b>must</b> call
	 * <code>super.init();</code> in your implementation as first statement!!!
	 */
	public void init()
	{
		cacheDir = getCacheDir();
	}

	public File getCacheDir()
	{
		if (cacheDir == null) {
			try {
				cacheDir = IOUtil.createUserTempDir("JFirePrintCache.", null); //$NON-NLS-1$
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			CacheDirTag cacheDirTag = new CacheDirTag(cacheDir);
			try {
				cacheDirTag.tag("JFire delivery print cache", true, false); //$NON-NLS-1$
			} catch (Exception e) {
				Logger.getLogger(AbstractScriptDataProviderThread.class).error("Tagging cache dir failed!", e); //$NON-NLS-1$
			}
		}
		return cacheDir;
	}

	/**
	 * This is initialized in the constructor to be the value returned by
	 * {@link AbstractClientDeliveryProcessorPrint#getPreferredDocumentCount()}.
	 * It will be double with each loop the thread runs until it reaches a
	 * maximum of 12.
	 */
	private int bulkProcessSize;

	@Override
	public void run()
	{
		long runStart = System.currentTimeMillis();
		if (logger.isDebugEnabled())
			logger.debug("run: enter"); //$NON-NLS-1$

		try {
			try {
				while (!isArticleIDsToProcessEmpty()) {
					// as long as there's sth. to do, we take the bulkProcessSize number of articleIDs and process them.
					if (logger.isDebugEnabled())
						logger.debug("run: getting a bulk of max " + bulkProcessSize + " articleIDs to process:"); //$NON-NLS-1$ //$NON-NLS-2$

					LinkedList<ArticleID> articleIDs = new LinkedList<ArticleID>();
					synchronized (articleIDsMutex) {
						int i = 0;
						for (ArticleID articleID : articleIDsToProcess) {
							if (++i > bulkProcessSize)
								break;

							articleIDs.add(articleID);
							if (logger.isDebugEnabled())
								logger.debug("run:   " + articleID); //$NON-NLS-1$
						}
					}

					if (logger.isDebugEnabled())
						logger.debug("run: getLayoutMapForArticleIDSet from server..."); //$NON-NLS-1$
					
					long localTimeMeasure = System.currentTimeMillis();
					
					LayoutMapForArticleIDSet tlm = getLayoutMapForArticleIDSet(articleIDs);
					if (tlm == null)
						throw new IllegalStateException("getLayoutMapForArticleIDSet(articleIDs) returned null! Check your implementation in class: " + this.getClass().getName()); //$NON-NLS-1$

					synchronized (layoutMapForArticleIDSetMutex) {
						if (layoutMapForArticleIDSet == null)
							layoutMapForArticleIDSet = tlm;
						else {
							layoutMapForArticleIDSet.append(tlm);
						}
					}
					DeliveryProcessorPrintDebugInfo.addTime(
							DeliveryProcessorPrintDebugInfo.CAT_FETCH_DATA_FETCH_LAYOUTS_MAP, System.currentTimeMillis() - localTimeMeasure);

					if (isInterrupted())
						throw new InterruptedException("interrupted!"); //$NON-NLS-1$

					// check which layout is not yet here or not up-to-date
					Set<ObjectID> layoutIDsToDownload = new HashSet<ObjectID>();
					Collection<ILayout> layouts = tlm.getProductID2LayoutMap().values();
					iterateTicketLayout:
					for (ILayout layout : layouts) {
						ObjectID layoutID = (ObjectID) JDOHelper.getObjectId(layout);
						File layoutFile = getLayoutFileByLayoutID(layoutID);
						if (!layoutFile.exists()) {
							if (logger.isDebugEnabled())
								logger.debug("run: layoutFile does not exist: " + layoutFile); //$NON-NLS-1$

							layoutIDsToDownload.add(layoutID);
							continue iterateTicketLayout;
						}

						if (!layoutFile.isFile())
							throw new IllegalStateException("layoutFile exists, but is not a normal file: " + layoutFile); //$NON-NLS-1$

						// the file exists, but is it up-to-date?
						if (Math.abs(layoutFile.lastModified() - layout.getFileTimestamp().getTime()) > 5000) {
							// file has different timestamp
							if (logger.isDebugEnabled())
								logger.debug("run: layoutFile has different timestamp: " + layoutFile); //$NON-NLS-1$

							layoutIDsToDownload.add(layoutID);
							continue iterateTicketLayout;
						}

						if (logger.isDebugEnabled())
							logger.debug("run: layoutFile already exists with correct timestamp: " + layoutFile); //$NON-NLS-1$
					}

					if (isInterrupted())
						throw new InterruptedException("interrupted!"); //$NON-NLS-1$

					// fetch all the necessary TicketLayouts WITH their files from the server
					if (!layoutIDsToDownload.isEmpty()) {
						localTimeMeasure = System.currentTimeMillis();
						if (logger.isDebugEnabled())
							logger.debug("run: loading complete Layouts (with files) from server..."); //$NON-NLS-1$

						List<ILayout> ilayouts = getLayouts(layoutIDsToDownload);

						// store the files
						for (ILayout layout : ilayouts) {
							ObjectID layoutID = (ObjectID) JDOHelper.getObjectId(layout);
							File layoutFile = getLayoutFileByLayoutID(layoutID);

							if (logger.isDebugEnabled())
								logger.debug("run: storing layoutFile: " + layoutFile); //$NON-NLS-1$

							File dir = layoutFile.getParentFile();
							if (!dir.exists()) {
								if (!dir.mkdirs())
									throw new IOException("Creating directory failed: " + dir); //$NON-NLS-1$
							}

							FileOutputStream out = new FileOutputStream(layoutFile);
							try {
								ByteArrayInputStream in = new ByteArrayInputStream(layout.getFileData());
								IOUtil.transferStreamData(in, out);
							} finally {
								out.close();
							}
							layoutFile.setLastModified(layout.getFileTimestamp().getTime());
						} // for (TicketLayout ticketLayout : ticketLayouts) {
						
						DeliveryProcessorPrintDebugInfo.addTime(
								DeliveryProcessorPrintDebugInfo.CAT_FETCH_DATA_FETCH_LAYOUTS, System.currentTimeMillis() - localTimeMeasure);
					} // if (!ticketLayoutIDsToDownload.isEmpty()) {
					else {
						// nothing to download, only register debug info
						DeliveryProcessorPrintDebugInfo.addTime(
								DeliveryProcessorPrintDebugInfo.CAT_FETCH_DATA_FETCH_LAYOUTS, 0);
					}
					if (isInterrupted())
						throw new InterruptedException("interrupted!"); //$NON-NLS-1$

					if (logger.isDebugEnabled())
						logger.debug("run: get ScriptingResult from server..."); //$NON-NLS-1$
					localTimeMeasure = System.currentTimeMillis();
					
					LinkedList<ProductID> productIDs = new LinkedList<ProductID>();
					for (ArticleID articleID : articleIDs) {
						ProductID productID = layoutMapForArticleIDSet.getArticleID2ProductIDMap().get(articleID);
						if (productID == null)
							throw new IllegalStateException("No productID found for " + articleID); //$NON-NLS-1$

						productIDs.add(productID);
					}

					Map<ProductID, Map<ScriptRegistryItemID, Object>> tsr = getScriptingResults(productIDs);
					synchronized (scriptingResultMutex) {
						if (scriptingResult == null)
							scriptingResult = tsr;
						else
							scriptingResult.putAll(tsr); // imho it's safe to do that, because no productID should ever occur twice
					}
					
					DeliveryProcessorPrintDebugInfo.addTime(
							DeliveryProcessorPrintDebugInfo.CAT_FETCH_DATA_SCRIPT_RESULTS, System.currentTimeMillis() - localTimeMeasure);
					
					if (isInterrupted())
						throw new InterruptedException("interrupted!"); //$NON-NLS-1$

					if (logger.isDebugEnabled())
						logger.debug("run: transferring processed articles to ready list: "); //$NON-NLS-1$

					// move the processed articleIDs from one list to the other
					synchronized (articleIDsMutex) {
						for (ArticleID articleID : articleIDs) {
							articleIDsReady.add(articleID);
							articleIDsToProcess.remove(articleID); // should be fast, because that should always be the first

							if (logger.isDebugEnabled())
								logger.debug("run:   " + articleID); //$NON-NLS-1$
						}
						articleIDsMutex.notifyAll();
					}
					
					bulkProcessSize *= 2;
					if (bulkProcessSize > 12)
						bulkProcessSize = 12;
				} // while (!isArticleIDsToProcessEmpty()) {
			} catch (Throwable t) {
				logger.error("Retrieving data failed!", t); //$NON-NLS-1$
				error = t;
			}
		} finally {
			synchronized (articleIDsMutex) {
				articleIDsMutex.notifyAll();
			}
			if (logger.isDebugEnabled()) {
				logger.debug("run: exit"); //$NON-NLS-1$
				long duration = System.currentTimeMillis() - runStart;
				DeliveryProcessorPrintDebugInfo.addTime(
						DeliveryProcessorPrintDebugInfo.CAT_FETCH_DATA_TOTAL_TIME, duration);
				logger.debug("Retrieving data took "+duration);
			}
		}
	}

	protected abstract LayoutMapForArticleIDSet getLayoutMapForArticleIDSet(List<ArticleID> articleIDs);

	protected abstract Map<ProductID, Map<ScriptRegistryItemID, Object>> getScriptingResults(
			List<ProductID> productIDs);

	protected abstract File getLayoutFileByLayoutID(ObjectID layoutID);

	protected ObjectID getLayoutByProductID(ProductID productID) {
		ILayout layout;
		synchronized (layoutMapForArticleIDSetMutex) {
			layout = layoutMapForArticleIDSet.getProductID2LayoutMap().get(productID);
		}
		if (layout == null)
			throw new IllegalArgumentException("productID unknown: " + productID); //$NON-NLS-1$

		return (ObjectID) JDOHelper.getObjectId(layout);
	}
	
	protected File getLayoutFileByProductID(ProductID productID)
	{
		ILayout layout;
		synchronized (layoutMapForArticleIDSetMutex) {
			layout = layoutMapForArticleIDSet.getProductID2LayoutMap().get(productID);
		}
		if (layout == null)
			throw new IllegalArgumentException("productID unknown: " + productID); //$NON-NLS-1$

		ObjectID objectID = (ObjectID) JDOHelper.getObjectId(layout);
		return getLayoutFileByLayoutID(objectID);
	}

	protected abstract List<ILayout> getLayouts(Set<ObjectID> layoutIDs);
}
