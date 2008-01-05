package org.nightlabs.jfire.voucher.print.transfer.deliver;

import java.io.File;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.apache.log4j.Logger;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.jfire.scripting.print.ui.transfer.delivery.AbstractScriptDataProviderThread;
import org.nightlabs.jfire.store.id.ProductID;
import org.nightlabs.jfire.trade.ILayout;
import org.nightlabs.jfire.trade.LayoutMapForArticleIDSet;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.ui.transfer.deliver.AbstractClientDeliveryProcessor;
import org.nightlabs.jfire.voucher.VoucherManager;
import org.nightlabs.jfire.voucher.VoucherManagerUtil;
import org.nightlabs.jfire.voucher.scripting.VoucherLayout;
import org.nightlabs.jfire.voucher.scripting.id.VoucherLayoutID;
import org.nightlabs.util.CollectionUtil;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherDataProviderThread 
extends AbstractScriptDataProviderThread 
{
	private static final Logger logger = Logger.getLogger(VoucherDataProviderThread.class);
	
	public VoucherDataProviderThread(AbstractClientDeliveryProcessor clientDeliveryProcessor) {
		super(clientDeliveryProcessor);
	}

	private static final String[] FETCH_GROUPS_VOUCHER_LAYOUT_STAGE_1 = { FetchPlan.DEFAULT };
	private static final String[] FETCH_GROUPS_VOUCHER_LAYOUT_STAGE_2 = { FetchPlan.DEFAULT, VoucherLayout.FETCH_GROUP_FILE };

//	@Override
//	public File getLayoutFile(ProductID productID) 
//	{
//		VoucherLayout voucherLayout;
//		synchronized (layoutMapForArticleIDSetMutex) {
//			voucherLayout = (VoucherLayout) layoutMapForArticleIDSet.getProductID2LayoutMap().get(productID);
//		}
//		if (voucherLayout == null)
//			throw new IllegalArgumentException("productID unknown: " + productID);
//
//		VoucherLayoutID voucherLayoutID = (VoucherLayoutID) JDOHelper.getObjectId(voucherLayout);
//		return getVoucherLayoutFile(voucherLayoutID);
//	}

	private VoucherManager getVoucherManager() 
	{
		try {
			return VoucherManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
		} catch (Exception e) {
			throw new RuntimeException(e); 
		}
	}
	
	@Override
	protected LayoutMapForArticleIDSet getLayoutMapForArticleIDSet(List<ArticleID> articleIDs) 
	{
		VoucherManager voucherManager = getVoucherManager();
		try {
			return voucherManager.getVoucherLayoutMapForArticleIDSet(
				articleIDs, 
				FETCH_GROUPS_VOUCHER_LAYOUT_STAGE_1, 
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}		
	}

	@Override
	protected List<ILayout> getLayouts(Set<ObjectID> layoutIDs) 
	{
		VoucherManager voucherManager = getVoucherManager();
		Set<VoucherLayoutID> voucherLayoutIDs = CollectionUtil.castSet(layoutIDs);
		try {
			return voucherManager.getVoucherLayouts(
				voucherLayoutIDs, 
				FETCH_GROUPS_VOUCHER_LAYOUT_STAGE_2, 
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}		
	}

	@Override
	protected Map<ProductID, Map<ScriptRegistryItemID, Object>> getScriptingResults(List<ProductID> productIDs) 
	{
		VoucherManager voucherManager = getVoucherManager();
		try {
			return voucherManager.getVoucherScriptingResults(productIDs, false);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected File getLayoutFileByLayoutID(ObjectID layoutID) {
		if (layoutID == null)
			throw new IllegalArgumentException("layoutID must not be null!");

		if (!(layoutID instanceof VoucherLayoutID))
			throw new IllegalArgumentException("Param layoutID "+layoutID+" is not a VoucherLayoutID!");

		VoucherLayoutID voucherLayoutID = (VoucherLayoutID) layoutID;
		return new File( 
				getCacheDir(),
				voucherLayoutID.organisationID + File.separatorChar + voucherLayoutID.voucherLayoutID);			
	}	
}
