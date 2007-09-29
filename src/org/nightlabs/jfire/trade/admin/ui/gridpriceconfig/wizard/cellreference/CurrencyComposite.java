package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.composite.ListComposite;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.trade.accounting.CurrencyDAO;
import org.nightlabs.jfire.trade.accounting.CurrencyLabelProvider;
import org.nightlabs.progress.ProgressMonitor;

public class CurrencyComposite extends AbstractCellReferenceComposite{

	private Currency selectedCurrency = null;

	private ListComposite<Currency> currencyListComposite;

	private CellReferencePage cellReferencePage;
	
	public CurrencyComposite(CellReferencePage cellReferencePage, Composite parent) {
		super(parent, SWT.None);
		this.cellReferencePage = cellReferencePage;

		currencyListComposite = new ListComposite<Currency>(this, SWT.SINGLE | SWT.BORDER);
		currencyListComposite.setLabelProvider(new CurrencyLabelProvider());
		Currency dummy = new Currency(
				"dummy", //$NON-NLS-1$
				"Loading data...",
				0);
		currencyListComposite.addElement(dummy);

		org.nightlabs.base.ui.job.Job job = new org.nightlabs.base.ui.job.Job("Loading Currencies") {
			@Override
			protected IStatus run(ProgressMonitor monitor)
			throws Exception
			{
				final List<Currency> currencies = CurrencyDAO.sharedInstance().getCurrencies(monitor);
				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						if (currencyListComposite.isDisposed())
							return;

						currencyListComposite.removeAll();
						currencyListComposite.addElements(currencies);
					}
				});
				return Status.OK_STATUS;
			}	
		};
		job.schedule();
				
		currencyListComposite.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent arg0) {
				Currency c = currencyListComposite.getSelectedElement();
				if(c != null){
					selectedCurrency = c;
					checked(true);
				}//if
			}
		});
	}
	
	protected void createScript(){
		StringBuffer scriptBuffer = new StringBuffer();
		
		scriptBuffer.append("CurrencyID.create") //$NON-NLS-1$
			.append(CellReferenceWizard.L_BRACKET)
				.append(CellReferenceWizard.DOUBLE_QUOTE).append(selectedCurrency.getCurrencyID()).append(CellReferenceWizard.DOUBLE_QUOTE)
			.append(CellReferenceWizard.R_BRACKET);
		
		cellReferencePage.addDimensionScript(this.getClass().getName(), scriptBuffer.toString());
	}
	
	@Override
	protected void doEnable() {
		if(selectedCurrency != null)
			createScript();	
	}

	@Override
	protected void doDisable() {
		cellReferencePage.removeDimensionScript(this.getClass().getName());
	}
}