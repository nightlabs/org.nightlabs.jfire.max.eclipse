package org.nightlabs.jfire.trade.ui.tariff;

import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.jfire.accounting.Tariff;
import org.nightlabs.jfire.accounting.TariffOrderConfigModule;
import org.nightlabs.jfire.trade.ui.resource.Messages;

public class TariffOrderCfModComposite extends XComposite {

	private TariffList tariffList;
	private Button upButton;
	private Button downButton;
	
	public TariffOrderCfModComposite(Composite parent, int style, final IDirtyStateManager dirtyStateManager) {
		super(parent, style, LayoutMode.ORDINARY_WRAPPER, LayoutDataMode.GRID_DATA, 2);
		getGridLayout().makeColumnsEqualWidth = false;
		
		tariffList = new TariffList(this, SWT.NONE, false, null);
		
		XComposite buttonComp = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		buttonComp.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = GridData.BEGINNING;
		upButton = new Button(buttonComp, SWT.PUSH);
		upButton.setLayoutData(gd);
		upButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.tariff.TariffOrderCfModComposite.button.text.up")); //$NON-NLS-1$
		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = GridData.BEGINNING;
		downButton = new Button(buttonComp, SWT.PUSH);
		downButton.setLayoutData(gd);
		downButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.tariff.TariffOrderCfModComposite.button.text.down")); //$NON-NLS-1$
		
		SelectionListener buttonListener = new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				if (e.widget == upButton) 
				{
					tariffList.moveSelectedTariffOneUp();
					dirtyStateManager.markDirty();
				} else {
					tariffList.moveSelectedTariffOneDown();
					dirtyStateManager.markDirty();
				}
				checkButtonStates();
			}
		};
		
		upButton.addSelectionListener(buttonListener);
		downButton.addSelectionListener(buttonListener);

		tariffList.addSelectionChangedListener(new ISelectionChangedListener(){
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				checkButtonStates();
			}
		});
		layout();
	}
	
	public List<Tariff> getOrderedTariffs() {
		return tariffList.getOrderedTariffs();
	}

	public void loadTariffs(final TariffOrderConfigModule cfMod) {
		tariffList.loadTariffs(new Comparator<Tariff>() {
			public int compare(Tariff o1, Tariff o2) {
				return cfMod.getTariffComparator().compare(o1, o2);
			}
		});
	}
	
	private void checkButtonStates() 
	{
		if (tariffList.getSelectionIndex() == 0) {
			upButton.setEnabled(false);
		}
		else if (tariffList.getSelectionIndex() == tariffList.getItemCount()-1) {
			downButton.setEnabled(false);					
		}
		else if (tariffList.getSelectionIndex() == -1) {
			upButton.setEnabled(false);
			downButton.setEnabled(false);
		}
		else {
			upButton.setEnabled(true);
			downButton.setEnabled(true);
		}
	}
}
