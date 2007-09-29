/**
 * 
 */
package org.nightlabs.jfire.trade.account.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.dao.AccountDAO;

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

	public void doSave(IProgressMonitor monitor) 
	{		
		for (IFormPage page : getPages()) {
			if (page instanceof AccountConfigurationPage) {
				final AccountConfigurationPage acp = (AccountConfigurationPage) page;
				Account account = acp.getAccountConfigurationSection().getAccountConfigurationComposite().getAccount();
				this.account = AccountDAO.sharedInstance().storeAccount(account, 
						false, FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
						new ProgressMonitorWrapper(monitor));
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
