/**
 * 
 */
package org.nightlabs.jfire.trade.ui.account.editor;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.AccountType;
import org.nightlabs.jfire.accounting.SummaryAccount;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public class AccountConfigurationPageController
extends AbstractAccountPageController
{
	public static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		AccountType.FETCH_GROUP_NAME,
		LegalEntity.FETCH_GROUP_PERSON,
		PropertySet.FETCH_GROUP_FULL_DATA,
		Account.FETCH_GROUP_ACCOUNT_TYPE,
		Account.FETCH_GROUP_CURRENCY,
		Account.FETCH_GROUP_NAME,
		Account.FETCH_GROUP_OWNER,
		Account.FETCH_GROUP_SUMMARY_ACCOUNTS,
		Account.FETCH_GROUP_DESCRIPTION,
		SummaryAccount.FETCH_GROUP_SUMMED_ACCOUNTS
	};

	public AccountConfigurationPageController(EntityEditor editor) {
		super(editor);
	}

	@Override
	protected String[] getEntityFetchGroups() {
		return FETCH_GROUPS;
	}
		
	@Override
	protected Account storeEntity(Account account, ProgressMonitor monitor) 
	{
//		AnchorID accountId = (AnchorID) JDOHelper.getObjectId(account);
//		AccountingManagerRemote acm = JFireEjb3Factory.getRemoteBean(AccountingManagerRemote.class, SecurityReflector.getInitialContextProperties());
//		if (account instanceof SummaryAccount) 
//		{
//			SummaryAccount summaryAccount = (SummaryAccount) account;
//			Set<AnchorID> summedAccountIds = new HashSet<AnchorID>();
//			for (Account summedAcount : summaryAccount.getSummedAccounts()) {
//				summedAccountIds.add((AnchorID) JDOHelper.getObjectId(summedAcount));
//			}
//			acm.setSummaryAccountSummedAccounts(accountId, summedAccountIds);
//		}
//		Set<AnchorID> summaryAccountIds = new HashSet<AnchorID>();
//		for (Account summaryAcount : account.getSummaryAccounts()) {
//			summaryAccountIds.add((AnchorID) JDOHelper.getObjectId(summaryAcount));
//		}
//		acm.setAccountSummaryAccounts(accountId, summaryAccountIds);			
//		return AccountDAO.sharedInstance().getAccount(accountId, getEntityFetchGroups(), NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		
		return super.storeEntity(account, monitor);
	}
	
//	@Override
//	public boolean doSave(ProgressMonitor monitor)
//	{
//		for (IFormPage page : getPages()) {
//			if (page instanceof AccountConfigurationPage) {
//				final AccountConfigurationPage acp = (AccountConfigurationPage) page;
//				Account account = acp.getAccountConfigurationSection().getAccountConfigurationComposite().getAccount();
//				this.account = AccountDAO.sharedInstance().storeAccount(account,
//						false, FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//						monitor);
//				return true;
//			}
//		}
//		return false;
//	}
	
}
