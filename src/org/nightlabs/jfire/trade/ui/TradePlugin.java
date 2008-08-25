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

package org.nightlabs.jfire.trade.ui;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.nightlabs.jfire.accounting.AccountingManager;
import org.nightlabs.jfire.accounting.AccountingManagerUtil;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.store.StoreManager;
import org.nightlabs.jfire.store.StoreManagerUtil;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class TradePlugin
extends AbstractUIPlugin
{
	public static final String ZONE_SALE = TradePlugin.class.getName() + "#ZONE_SALE"; //$NON-NLS-1$
	public static final String ZONE_ADMIN = TradePlugin.class.getName() + "#ZONE_ADMIN"; //$NON-NLS-1$

	//The shared instance.
	private static TradePlugin plugin;
//	//Resource bundle.
//	private static ResourceBundle resourceBundle;

	public static final String ID_PLUGIN = TradePlugin.class.getPackage().getName();

	/**
	 * The constructor.
	 */
	public TradePlugin() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
//		try {
//			resourceBundle = Platform.getResourceBundle(getBundle());
//		} catch (MissingResourceException x) {
//			resourceBundle = null;
//		}

		// TODO: find some way listen to creation of the WorkbenchWindow (WorkbenchWindowAdvisor can do this)
//		PlatformUI.getWorkbench().addWindowListener(new IWindowListener() {
//			public void windowActivated(IWorkbenchWindow window) {
//				QuickSalePerspective.checkPerspectiveListenerAdded();
//			}
//			public void windowClosed(IWorkbenchWindow window) {
//			}
//			public void windowDeactivated(IWorkbenchWindow window) {
//			}
//			public void windowOpened(IWorkbenchWindow window) {
//				Display.getDefault().asyncExec(new Runnable() {
//					public void run() {
//						QuickSalePerspective.checkPerspectiveListenerAdded();
//					}
//				});
//			}
//		});
	}

//	public static final String OVERVIEW_EDITORS_ACTIVITY_ID = "org.nightlabs.jfire.trade.ui.overviewEditorHidding";
//	private IPerspectiveListener4 activityPerspectiveListener = new PerspectiveAdapter()
//	{
//		public void perspectiveActivated(IWorkbenchPage page,
//				IPerspectiveDescriptor perspective)
//		{
//			IWorkbenchActivitySupport workbenchActivitySupport = PlatformUI.getWorkbench().getActivitySupport();
//			IActivityManager activityManager = workbenchActivitySupport.getActivityManager();
//			Set<String> enabledActivityIds = new HashSet<String>(activityManager.getEnabledActivityIds());
//			if (perspective.getId().equals(TradeOverviewPerspective.ID_PERSPECTIVE)) {
//				enabledActivityIds.add(OVERVIEW_EDITORS_ACTIVITY_ID);
//				logger.info(OVERVIEW_EDITORS_ACTIVITY_ID+" activity enabled");
//			}
//			else {
//				enabledActivityIds.remove(OVERVIEW_EDITORS_ACTIVITY_ID);
//				logger.info(OVERVIEW_EDITORS_ACTIVITY_ID+" activity disabled");
//			}
//			workbenchActivitySupport.setEnabledActivityIds(enabledActivityIds);
//
//			Set<String> definedActivityIDs = activityManager.getDefinedActivityIds();
//			for (String string : definedActivityIDs) {
//				logger.info("defined activity id = "+string);
//			}
//
//			Set<String> enabledActivityIDs = activityManager.getEnabledActivityIds();
//			for (String string : enabledActivityIDs) {
//				logger.info("enabled activity id = "+string);
//			}
//
//			IActivity activity = activityManager.getActivity(OVERVIEW_EDITORS_ACTIVITY_ID);
//			if (activity != null) {
//				logger.info("activity "+OVERVIEW_EDITORS_ACTIVITY_ID+".isEnabled() = "+activity.isEnabled());
//			} else {
//				logger.info("activity "+OVERVIEW_EDITORS_ACTIVITY_ID+" is null!");
//			}
//		}
//		public void perspectiveOpened(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
//			IWorkbenchActivitySupport workbenchActivitySupport = PlatformUI.getWorkbench().getActivitySupport();
//			IActivityManager activityManager = workbenchActivitySupport.getActivityManager();
//			Set<String> enabledActivityIds = new HashSet<String>(activityManager.getEnabledActivityIds());
//			if (perspective.getId().equals(TradeOverviewPerspective.ID_PERSPECTIVE)) {
//				enabledActivityIds.add(OVERVIEW_EDITORS_ACTIVITY_ID);
//				logger.info(OVERVIEW_EDITORS_ACTIVITY_ID+" activity enabled");
//			}
//			else {
//				enabledActivityIds.remove(OVERVIEW_EDITORS_ACTIVITY_ID);
//				logger.info(OVERVIEW_EDITORS_ACTIVITY_ID+" activity disabled");
//			}
//			workbenchActivitySupport.setEnabledActivityIds(enabledActivityIds);
//
//			Set<String> definedActivityIDs = activityManager.getDefinedActivityIds();
//			for (String string : definedActivityIDs) {
//				logger.info("defined activity id = "+string);
//			}
//
//			Set<String> enabledActivityIDs = activityManager.getEnabledActivityIds();
//			for (String string : enabledActivityIDs) {
//				logger.info("enabled activity id = "+string);
//			}
//
//			IActivity activity = activityManager.getActivity(OVERVIEW_EDITORS_ACTIVITY_ID);
//			if (activity != null) {
//				logger.info("activity "+OVERVIEW_EDITORS_ACTIVITY_ID+".isEnabled() = "+activity.isEnabled());
//			} else {
//				logger.info("activity "+OVERVIEW_EDITORS_ACTIVITY_ID+" is null!");
//			}
//		}
//
//	};

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
//		resourceBundle = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static TradePlugin getDefault() {
		return plugin;
	}

//	/**
//	 * Returns the string from the plugin's resource bundle,
//	 * or 'key' if not found.
//	 */
//	public static String getResourceString(String key) {
//		ResourceBundle bundle = TradePlugin.getDefault().getResourceBundle();
//		try {
//			return (bundle != null) ? bundle.getString(key) : key;
//		} catch (MissingResourceException e) {
//			return key;
//		}
//	}

//	/**
//	 * Returns the plugin's resource bundle,
//	 */
//	public ResourceBundle getResourceBundle() {
//		return resourceBundle;
//	}

// TODO these constants are not necessary => use the SharedImages instead!
	public static final String IMAGE_ORDER_16x16 = "icons/TradePlugin-Order.16x16.png"; //$NON-NLS-1$
	public static final String IMAGE_OFFER_16x16 = "icons/TradePlugin-Offer.16x16.png"; //$NON-NLS-1$
	public static final String IMAGE_INVOICE_16x16 = "icons/TradePlugin-Invoice.16x16.png"; //$NON-NLS-1$

	/**
	 * Indicates that the {@link Invoice} of the article has been paid completely. Note, that an article
	 * cannot be paid individually - only the corresponding invoice.
	 */
	public static final String IMAGE_ARTICLE_PAID_16x16 = "icons/TradePlugin-Article-paid.16x16.png"; //$NON-NLS-1$
	public static final String IMAGE_DELIVERY_NOTE_16x16 = "icons/TradePlugin-DeliveryNote.16x16.png"; //$NON-NLS-1$
	public static final String IMAGE_ARTICLE_DELIVERED_16x16 = "icons/TradePlugin-Article-delivered.16x16.png"; //$NON-NLS-1$

	@Override
	protected void initializeImageRegistry(ImageRegistry reg)
	{
		super.initializeImageRegistry(reg);
		reg.put(IMAGE_ORDER_16x16, imageDescriptorFromPlugin(TradePlugin.ID_PLUGIN, IMAGE_ORDER_16x16));
		reg.put(IMAGE_OFFER_16x16, imageDescriptorFromPlugin(TradePlugin.ID_PLUGIN, IMAGE_OFFER_16x16));
		reg.put(IMAGE_INVOICE_16x16, imageDescriptorFromPlugin(TradePlugin.ID_PLUGIN, IMAGE_INVOICE_16x16));
		reg.put(IMAGE_ARTICLE_PAID_16x16, imageDescriptorFromPlugin(TradePlugin.ID_PLUGIN, IMAGE_ARTICLE_PAID_16x16));
		reg.put(IMAGE_DELIVERY_NOTE_16x16, imageDescriptorFromPlugin(TradePlugin.ID_PLUGIN, IMAGE_DELIVERY_NOTE_16x16));
		reg.put(IMAGE_ARTICLE_DELIVERED_16x16, imageDescriptorFromPlugin(TradePlugin.ID_PLUGIN, IMAGE_ARTICLE_DELIVERED_16x16));
	}

	/**
	 * Returns a localized string that represents the type of {@link ArticleContainer} passed.
	 * 
	 * @param articleContainer The {@link ArticleContainer} to get the type string for.
	 * @param capitalize If <code>true</code> the first letter of the type string will be upper case.
	 * @return A localized string that represents the type of {@link ArticleContainer} passed.
	 */
	public static String getArticleContainerTypeString(ArticleContainer articleContainer, boolean capitalize) {
		String prefix = "org.nightlabs.jfire.trade.ui.TradePlugin.articleContainerTypeString.";
		Class<?> acClass = articleContainer.getClass();
		String acTypeString = getMessageKey(prefix + acClass.getSimpleName());
		while (acTypeString == null && !(acClass == Object.class)) {
			acClass = acClass.getSuperclass();
			acTypeString = getMessageKey(prefix + acClass.getSimpleName());
		}
		if (acTypeString == null) {
			acTypeString = articleContainer.getClass().getSimpleName();
		}
		if (capitalize && acTypeString.length() > 0) {
			acTypeString = acTypeString.substring(0, 1).toUpperCase() + acTypeString.substring(1); 
		}
		return acTypeString;
	}
	
	private static String getMessageKey(String key) {
		if (Messages.RESOURCE_BUNDLE.containsKey(key))
			return Messages.RESOURCE_BUNDLE.getString(key);
		return null;
	}
	
	public TradeManager getTradeManager()
	{
		try {
			return TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public StoreManager getStoreManager()
	{
		try {
			return StoreManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public AccountingManager getAccountingManager()
	{
		try {
			return AccountingManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
