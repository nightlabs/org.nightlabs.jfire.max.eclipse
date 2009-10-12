package org.nightlabs.jfire.trade.admin.ui.editor;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.dao.CurrencyDAO;

/**
 *
 * @author vince
 *
 */

public class CurrencyCreateWizard
extends DynamicPathWizard
implements INewWizard
{
	private Currency newCurrency;
	private CurrencyCreateWizardPage currencyCreateWizardPage;

	public CurrencyCreateWizard()
	{
		setWindowTitle("Create a new currency");
	}

	@Override
	public void addPages() {
		currencyCreateWizardPage= new CurrencyCreateWizardPage();
		addPage(currencyCreateWizardPage);
	}

	@Override
	public boolean performFinish() {
		final Currency currency = currencyCreateWizardPage.createCurrency();

		try {
			getContainer().run(true, false, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					newCurrency = CurrencyDAO.sharedInstance().storeCurrency(
							currency,
							true,
							null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							new ProgressMonitorWrapper(monitor)
					);
				}
			});
		} catch (Exception x) {
			ExceptionHandlerRegistry.asyncHandleException(x);
			return false;
		}
		return true;
	}

	public Currency getNewCurrency() {
		return newCurrency;
	}



	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// nothing to do
	}

}
