/**
 * 
 */
package org.nightlabs.jfire.trade.ui.account.editor;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.SummaryAccount;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public class AccountConfigurationPage
extends EntityEditorPageWithProgress
{
	private static final Logger logger = Logger.getLogger(AccountConfigurationPage.class);
	
	/**
	 * The id of this page.
	 */
	public static final String ID_PAGE = AccountConfigurationPage.class.getName();
	
	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link PersonPreferencesPage}.
	 */
	public static class Factory implements IEntityEditorPageFactory {

		public IFormPage createPage(FormEditor formEditor) {
			return new AccountConfigurationPage(formEditor);
		}
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new AccountConfigurationPageController(editor);
		}
	}
	
	public AccountConfigurationPage(FormEditor editor) {
		super(editor, ID_PAGE, Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.AccountConfigurationPage.title")); //$NON-NLS-1$
	}

	private AccountConfigurationSection accountConfigurationSection = null;
	public AccountConfigurationSection getAccountConfigurationSection() {
		return accountConfigurationSection;
	}
	
	@Override
	protected void addSections(Composite parent) {
		accountConfigurationSection = new AccountConfigurationSection(this, parent);
		getManagedForm().addPart(accountConfigurationSection);
	}

	@Override
	protected void asyncCallback() {

	}

	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent)
	{
		switchToContent();

		if (!(modifyEvent.getNewObject() instanceof Account)) {
			logger.warn("handleControllerObjectModified: EntityEditorPageControllerModifyEvent.getNewObject() returned instance of " + (modifyEvent.getNewObject() == null ? null : modifyEvent.getNewObject().getClass().getName()), new Exception("DEBUG STACKTRACE")); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		final Account account = (Account) modifyEvent.getNewObject();

		Display.getDefault().asyncExec(new Runnable() {
//		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				accountConfigurationSection.getAccountConfigurationComposite().setAccount(
						account, account instanceof SummaryAccount);
			}
		});
	}
	
	@Override
	protected String getPageFormTitle() {
		return Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.AccountConfigurationPage.title"); //$NON-NLS-1$
	}
}
