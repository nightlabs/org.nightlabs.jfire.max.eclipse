package org.nightlabs.jfire.reporting.trade.ui.articlecontainer.detail.action.print;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.jdo.FetchPlan;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.trade.config.TradeDocumentsLocaleConfigModule;
import org.nightlabs.jfire.reporting.trade.config.TradeDocumentsLocaleConfigModule.LocaleOption;
import org.nightlabs.jfire.reporting.trade.ui.resource.Messages;
import org.nightlabs.jfire.security.SecurityReflector;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.security.dao.UserDAO;
import org.nightlabs.jfire.security.id.UserID;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.ArticleContainerDAO;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;
import org.nightlabs.util.Util;

public class ArticleContainerReportActionHelper {

	/**
	 * Retrieves the given {@link ArticleContainer}s customer and its persons Locale.
	 * @param reportID The {@link ReportRegistryItemID} of the report to show.
	 * @param params The params of the report to show.
	 * @param monitor The monitor to report progress to.
	 * @return The locale of the given {@link ArticleContainer}s customer.
	 */
	public static Locale getArticleContainerReportLocale(
			ArticleContainerID articleContainerID, ReportRegistryItemID reportID, Map<String, Object> params, ProgressMonitor monitor) {
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.reporting.trade.ui.articlecontainer.detail.action.print.ArticleContainerReportActionHelper.getArticleContainerReportLocale.taskName"), 5); //$NON-NLS-1$
		TradeDocumentsLocaleConfigModule localeCfMod = ConfigUtil.getUserCfMod(
				TradeDocumentsLocaleConfigModule.class, new String[] {FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 3));
		
		Locale result = null;
		
		if (localeCfMod.getLocaleOption() == LocaleOption.customerLocale) {
			Locale customerLocale = getCustomerLocale(articleContainerID, new SubProgressMonitor(monitor, 2));
			if (customerLocale != null) {
				result = customerLocale;
			}
		} else if (localeCfMod.getLocaleOption() == LocaleOption.userLocale) {
			Locale userLocale = getUserLocale(new SubProgressMonitor(monitor, 2));
			if (userLocale != null) {
				result = userLocale;
			}
		} else if (localeCfMod.getLocaleOption() == LocaleOption.clientLocale) {
			result = Locale.getDefault();
		} else if (localeCfMod.getLocaleOption() == LocaleOption.askIfCustomerAndUserLocaleDiffer) {
			final Locale userLocale = getUserLocale(new SubProgressMonitor(monitor, 1));
			final Locale customerLocale = getCustomerLocale(articleContainerID, new SubProgressMonitor(monitor, 1));
			if (Util.equals(userLocale, customerLocale)) {
				return userLocale;
			} else {
				final Locale[] dlgResult = new Locale[1];
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						List<Locale> locales = new LinkedList<Locale>();
						if (customerLocale != null)
							locales.add(customerLocale);
						if (userLocale != null)
							locales.add(userLocale);
						if (!locales.contains(Locale.getDefault())) {
							locales.add(Locale.getDefault());
						}
						ArticleContainerReportLocaleDialog dlg = new ArticleContainerReportLocaleDialog(Display.getCurrent().getActiveShell(), locales);
						if (dlg.open() == Window.OK) {
							dlgResult[0] = dlg.getSelectedLocale();
						}
					}
				});
				if (dlgResult[0] != null)
					result = dlgResult[0];
			}
		}
		monitor.done();
		return result;
	}
	
	/**
	 * Used by {@link #getArticleContainerReportLocale(ArticleContainerID, ReportRegistryItemID, Map, ProgressMonitor)}.
	 */
	private static Locale getCustomerLocale(ArticleContainerID articleContainerID, ProgressMonitor monitor) {
		if (articleContainerID != null) { 
			ArticleContainer articleContainer = ArticleContainerDAO.sharedInstance().getArticleContainer(
					articleContainerID, 
					new String[] {FetchPlan.DEFAULT, ArticleContainer.FETCH_GROUP_CUSTOMER, LegalEntity.FETCH_GROUP_PERSON}, 
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
			if (articleContainer.getCustomer() != null) {
				monitor.done();
				return articleContainer.getCustomer().getPerson().getLocale();
			}
		}
		return null;
	}

	/**
	 * Used by {@link #getArticleContainerReportLocale(ArticleContainerID, ReportRegistryItemID, Map, ProgressMonitor)}.
	 */
	private static Locale getUserLocale(ProgressMonitor monitor) {
		UserID userID = SecurityReflector.getUserDescriptor().getUserObjectID();
		User user = UserDAO.sharedInstance().getUser(
				userID, new String[] {FetchPlan.DEFAULT, User.FETCH_GROUP_PERSON}, 
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 1));
		if (user.getPerson() != null) {
			monitor.done();
			return user.getPerson().getLocale();
		}
		return null;
	}

}
