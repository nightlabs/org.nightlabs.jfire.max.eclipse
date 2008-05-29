/**
 * 
 */
package org.nightlabs.jfire.trade.ui.account.editor;

import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.dao.AccountDAO;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public class AccountConfigurationPageController
extends AbstractAccountPageController
{
	/**
	 * @param editor
	 */
	public AccountConfigurationPageController(EntityEditor editor) {
		super(editor);
	}

	@Override
	public void doSave(ProgressMonitor monitor)
	{
		for (IFormPage page : getPages()) {
			if (page instanceof AccountConfigurationPage) {
				final AccountConfigurationPage acp = (AccountConfigurationPage) page;
				Account account = acp.getAccountConfigurationSection().getAccountConfigurationComposite().getAccount();
				this.account = AccountDAO.sharedInstance().storeAccount(account,
						false, FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						monitor);
//				Display.getDefault().syncExec(new Runnable(){
//					public void run() {
//						try {
//							acp.getAccountConfigurationSection().getAccountConfigurationComposite().save();
//						} catch (Exception e) {
//							throw new RuntimeException(e);
//						}
//					}
//				});
			}
		}
	}
	
}
