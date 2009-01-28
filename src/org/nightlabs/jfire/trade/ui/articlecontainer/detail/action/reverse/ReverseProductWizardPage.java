/**
 *
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.jfire.store.id.ProductID;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author daniel[at]nightlabs[dot]de
 *
 */
public class ReverseProductWizardPage extends DynamicPathWizardPage
{
	public static final String PAGE_NAME = ReverseProductWizardPage.class.getName();

	private ReverseProductComposite reverseProductComposite;

	/**
	 * Creates a ReverseProductWizardPage.
	 */
	public ReverseProductWizardPage()
	{
		super(PAGE_NAME, "Reverse Product");
		setDescription("Enter an product id / number to find the reversing offer for");
		setPageComplete(false);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent)
	{
		Composite wrapper = new XComposite(parent, SWT.NONE);

		reverseProductComposite = new ReverseProductComposite(wrapper, SWT.NONE);
		reverseProductComposite.getProductIDText().addDelayedModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e)
			{
				Display.getDefault().asyncExec(new Runnable(){
					@Override
					public void run() {
						String errorMessage = null;
						ProductID productID = reverseProductComposite.getProductID(getProgressMonitor());
						if (productID == null) {
							errorMessage = Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.ReverseProductDialog.errorMessage"); //$NON-NLS-1$
						}
						setErrorMessage(errorMessage);
						setPageComplete(errorMessage == null);
						getContainer().updateButtons();
					}
				});
			}
		});
		GridData gd = new GridData(GridData.CENTER, GridData.BEGINNING, true, false);
		reverseProductComposite.setLayoutData(gd);

		return wrapper;
	}

	public ReverseProductComposite getReverseProductComposite() {
		return reverseProductComposite;
	}

	protected ProgressMonitor getProgressMonitor() {
		if (getContainer() instanceof DynamicPathWizardDialog) {
			DynamicPathWizardDialog dlg = (DynamicPathWizardDialog) getContainer();
			return new ProgressMonitorWrapper(dlg.getProgressMonitor());
		}
		return new NullProgressMonitor();
	}
}
