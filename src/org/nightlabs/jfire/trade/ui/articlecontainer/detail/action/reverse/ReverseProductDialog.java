package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse;

import java.util.ResourceBundle;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.message.IErrorMessageDisplayer;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.jfire.store.id.ProductID;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 */
public class ReverseProductDialog
extends ResizableTitleAreaDialog
implements IErrorMessageDisplayer
{
//	private static final Logger logger = Logger.getLogger(ReverseProductDialog.class);

	private ReverseProductComposite reverseProductComposite;
	private ProductID productID;
	private ProgressMonitorPart progressMonitorPart;

	/**
	 * @param shell
	 * @param resourceBundletest
	 */
	public ReverseProductDialog(Shell shell, ResourceBundle resourceBundle) {
		super(shell, resourceBundle);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		getShell().setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.ReverseProductDialog.windows.title")); //$NON-NLS-1$
		setTitle(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.ReverseProductDialog.title")); //$NON-NLS-1$
		setMessage(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.ReverseProductDialog.message")); //$NON-NLS-1$
		Composite wrapper = new XComposite(parent, SWT.NONE);
		reverseProductComposite = new ReverseProductComposite(wrapper, SWT.NONE);
		reverseProductComposite.getProductIDText().addDelayedModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e)
			{
				Display.getDefault().asyncExec(new Runnable(){
					@Override
					public void run() {
						Button okButton = getButton(Window.OK);
						if (okButton != null) {
							String errorMessage = null;
							ProductID productID = reverseProductComposite.getProductID(getProgressMonitor());
							if (productID == null) {
								errorMessage = Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.ReverseProductDialog.errorMessage"); //$NON-NLS-1$
							}
							setErrorMessage(errorMessage);
							okButton.setEnabled(productID != null);
						}
					}
				});
			}
		});
		progressMonitorPart = new ProgressMonitorPart(wrapper, new GridLayout());
		progressMonitorPart.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		return wrapper;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		productID = reverseProductComposite.getProductID(getProgressMonitor());
		super.okPressed();
	}

	public ProductID getProductID() {
		return productID;
	}

	public boolean isReverseAll() {
		return reverseProductComposite.isReverseAll();
	}

	public boolean isReverseArticle() {
		return reverseProductComposite.isReverseArticle();
	}

	private ProgressMonitor getProgressMonitor() {
		return new ProgressMonitorWrapper(progressMonitorPart);
	}
}
;