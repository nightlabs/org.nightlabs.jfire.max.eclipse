/**
 *
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse;

import java.lang.reflect.InvocationTargetException;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.JFireEjbFactory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.store.id.ProductID;
import org.nightlabs.jfire.store.reverse.AlreadyReversedArticleReverseProductError;
import org.nightlabs.jfire.store.reverse.IReverseProductError;
import org.nightlabs.jfire.store.reverse.ReverseProductException;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author daniel[at]nightlabs[dot]de
 *
 */
public class ReverseProductWizard
extends DynamicPathWizard
//extends CombiTransferArticleContainerWizard
{
	private static final Logger logger = Logger.getLogger(ReverseProductWizard.class);

	private ReverseProductWizardPage reverseProductWizardPage;

	public ReverseProductWizard() {
		super();
	}

	@Override
	public void addPages()
	{
		reverseProductWizardPage = new ReverseProductWizardPage();
		addPage(reverseProductWizardPage);
//		reverseProductWizardPage.getReverseProductComposite().addReverseProductListener(new IReverseProductListener(){
//			@Override
//			public void reverseProductChanged(ReverseProductEvent event) {
//				if (event.isReversePaymentAndDelivery()) {
//
//				}
//			}
//		});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish()
	{
		try {
			getContainer().run(false, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						createReverseOffer(new ProgressMonitorWrapper(monitor));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	private Display getDisplay() {
		return getShell().getDisplay();
	}

	private Offer createReverseOffer(ProgressMonitor monitor)
	throws Exception
	{
		ProductID productID = reverseProductWizardPage.getReverseProductComposite().getProductID(
				new SubProgressMonitor(monitor, 100));
		boolean completeOffer = reverseProductWizardPage.getReverseProductComposite().isReverseAll();
		try {
			return createReversingOffer(productID, completeOffer, new SubProgressMonitor(monitor, 100));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Offer createReversingOffer(final ProductID productID, final boolean completeOffer, ProgressMonitor monitor)
	throws Exception
	{
		TradeManager tm = JFireEjbFactory.getBean(TradeManager.class, Login.getLogin().getInitialContextProperties());
		try {
			final Offer reversingOffer = tm.createReverseOfferForProduct(productID, completeOffer, true,
					new String[] {FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
			if (reversingOffer == null) {
				showNothingFound();
				return null;
			}
			else {
				getDisplay().syncExec(new Runnable(){
					@Override
					public void run() {
						try {
							OfferID offerID = (OfferID) JDOHelper.getObjectId(reversingOffer);
							RCPUtil.openEditor(
									new ArticleContainerEditorInput(offerID),
									ArticleContainerEditor.ID_EDITOR);
						} catch (PartInitException e) {
							throw new RuntimeException(e);
						}
					}
				});
				return reversingOffer;
			}
		}
		catch (Exception e) {
			if (e instanceof ReverseProductException) {
				final ReverseProductException exception = (ReverseProductException) e;
				final IReverseProductError error = exception.getReverseProductError();
				if (error != null) {
					if (error instanceof AlreadyReversedArticleReverseProductError) {
						showAlreadyReversed((AlreadyReversedArticleReverseProductError) error);
					}
				}
				else {
					showError(exception);
				}
			}
			else {
				logger.warn("Problem occured when trying to create a reversing offer for productID "+productID, e); //$NON-NLS-1$
			}
			return null;
		}

	}

	private void showNothingFound() {
		getDisplay().syncExec(new Runnable(){
			@Override
			public void run() {
				MessageDialog.openError(getShell(),
						Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.ReverseProductAction.nothingFound.dialog.title"),  //$NON-NLS-1$
						Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.ReverseProductAction.nothingFound.dialog.message")); //$NON-NLS-1$
			}
		});
	}

	private void showAlreadyReversed(final AlreadyReversedArticleReverseProductError error) {
		getDisplay().syncExec(new Runnable(){
			@Override
			public void run() {
				AlreadyReversedArticleReverseProductError alreadyReversedError = error;
				Dialog dialog = new OpenAlreadyReversedOfferDialog(getShell(), alreadyReversedError);
				dialog.open();
			}
		});
	}

	private void showError(final ReverseProductException exception) {
		getDisplay().syncExec(new Runnable(){
			@Override
			public void run() {
				MessageDialog.openError(getShell(),
						Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.ReverseProductAction.dialog.title"),  //$NON-NLS-1$
						exception.getDescription());
			}
		});
	}

//	protected ProgressMonitor getProgressMonitor()
//	{
//		if (getContainer() instanceof DynamicPathWizardDialog) {
//			DynamicPathWizardDialog dlg = (DynamicPathWizardDialog) getContainer();
//			return new ProgressMonitorWrapper(dlg.getProgressMonitor());
//		}
//		return new NullProgressMonitor();
//	}

	@Override
	public void setContainer(IWizardContainer wizardContainer) {
		super.setContainer(wizardContainer);
		setWindowTitle("Reverse Product");
	}
}
