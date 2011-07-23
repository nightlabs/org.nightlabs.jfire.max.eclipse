/**
 *
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.config;

import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.eclipse.ui.dialog.ResizableTrayDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.PriceFragmentType;
import org.nightlabs.jfire.accounting.dao.PriceFragmentTypeDAO;
import org.nightlabs.jfire.trade.ui.accounting.PriceFragmentTypeTable;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class PriceFragmentTypeChooserDialog
extends ResizableTrayDialog
{
	private PriceFragmentTypeTable priceFragmentTypeTable;
	private Collection<PriceFragmentType> priceFragmentTypes;
	
	public PriceFragmentTypeChooserDialog(Shell parentShell) {
		super(parentShell, null);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Price Fragment Type Chooser");
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite mainComposite = (Composite) super.createDialogArea(parent);

		new Label(mainComposite, SWT.NONE).setText("Price Fragment Types: ");
	
		priceFragmentTypeTable = new PriceFragmentTypeTable(mainComposite, SWT.NONE);
//		priceFragmentTypeTable.loadPriceFragmentTypes();
		
		Job job = new Job("Loading Price Fragment Types.....") {
			@Override
			protected IStatus run(ProgressMonitor monitor) {
				monitor.beginTask("Loading Price Fragment Types", 100);
				
				priceFragmentTypes = PriceFragmentTypeDAO.sharedInstance().getPriceFragmentTypes(
						PriceFragmentTypeTable.DEFAULT_FETCH_GROUPS, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
						new NullProgressMonitor());
				priceFragmentTypes.removeAll(excludedPriceFragmentTypes);
				
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						priceFragmentTypeTable.setInput(priceFragmentTypes);
					}
				});
				
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule();
		

		priceFragmentTypeTable.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				selectedPriceFragmentTypes = priceFragmentTypeTable.getSelectedElements();
				if (priceFragmentTypeTable.getSelectedElements().size() != 0) {
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				}
			}
		});
		
		return mainComposite;
	}

	@Override
	protected void okPressed() {
		super.okPressed();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	
	private Collection<PriceFragmentType> selectedPriceFragmentTypes;
	public Collection<PriceFragmentType> getSelectedPriceFragmentTypes() {
		return selectedPriceFragmentTypes;
	}
	
	private Collection<PriceFragmentType> excludedPriceFragmentTypes;
	public void setExcludedPriceFragmentTypes(Collection<PriceFragmentType> priceFragmentTypes) {
		this.excludedPriceFragmentTypes = priceFragmentTypes;
	}
}