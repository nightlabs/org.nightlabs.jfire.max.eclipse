package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;
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
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ReverseProductAction
extends Action
{
	private static final Logger logger = Logger.getLogger(ReverseProductAction.class);

	private Shell shell = null;

	public ReverseProductAction(Shell shell) {
		super(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.ReverseProductAction.text"),  //$NON-NLS-1$
				SharedImages.getSharedImageDescriptor(TradePlugin.getDefault(), ReverseProductAction.class));
		if (shell == null) {
			throw new IllegalArgumentException("Param shell must not be null!");
		}
		this.shell = shell;
	}

	@Override
	public void run()
	{
		ReverseProductDialog dialog = new ReverseProductDialog(getShell(), null);
		int returnCode = dialog.open();
		if (returnCode == Window.OK) {
			ProductID productID = dialog.getProductID();
			createReversingOffer(productID, dialog.isReverseAll());
		}
	}

	private void createReversingOffer(final ProductID productID, final boolean completeOffer)
	{
		Job searchJob = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.ReverseProductAction.job.name")){ //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				TradeManager tm = JFireEjbFactory.getBean(TradeManager.class, Login.getLogin().getInitialContextProperties());
				try {
					final Offer reversingOffer = tm.createReverseOfferForProduct(productID, completeOffer, true,
							new String[] {FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
					if (reversingOffer == null) {
						showNothingFound();
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
					}
				} catch (Exception e) {
					if (e instanceof ReverseProductException) {
						final ReverseProductException exception = (ReverseProductException) e;
						final IReverseProductError error = exception.getReverseProductError();
						if (error != null) {
							if (error instanceof AlreadyReversedArticleReverseProductError) {
								getDisplay().syncExec(new Runnable(){
									@Override
									public void run() {
										AlreadyReversedArticleReverseProductError alreadyReversedError = (AlreadyReversedArticleReverseProductError) error;
										Dialog dialog = new OpenAlreadyReversedOfferDialog(getShell(), alreadyReversedError);
										dialog.open();
									}
								});
							}
						}
						else {
							getDisplay().syncExec(new Runnable(){
								@Override
								public void run() {
									MessageDialog.openError(getShell(),
											Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.ReverseProductAction.dialog.title"),  //$NON-NLS-1$
											exception.getDescription());
								}
							});
						}
					}
					else {
						logger.warn("Problem occured when trying to create a reversing offer for productID "+productID, e); //$NON-NLS-1$
					}
				}
				return Status.OK_STATUS;
			}
		};
		searchJob.schedule();
	}

	private Shell getShell() {
		return shell;
	}

	private Display getDisplay() {
		return shell.getDisplay();
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
}
