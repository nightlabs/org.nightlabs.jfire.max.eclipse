package org.nightlabs.jfire.trade.ui.tariff;

import java.util.Comparator;
import java.util.List;

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

public class TariffOrderCfModComposite extends XComposite {

	private TariffList tariffList;
	
	public TariffOrderCfModComposite(Composite parent, int style, final IDirtyStateManager dirtyStateManager) {
		super(parent, style, LayoutMode.ORDINARY_WRAPPER, LayoutDataMode.GRID_DATA, 2);
		getGridLayout().makeColumnsEqualWidth = false;
		
		tariffList = new TariffList(this, SWT.NONE, false, null);
		tariffList.getGridData().verticalSpan = 2;
		
		XComposite buttonComp = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		
		GridData gd = new GridData();
		gd.widthHint = 40;
		final Button upButton = new Button(buttonComp, SWT.PUSH);
		upButton.setLayoutData(gd);
		upButton.setText("Up");
		
		gd = new GridData();
		gd.widthHint = 40;
		final Button downButton = new Button(buttonComp, SWT.PUSH);
		downButton.setLayoutData(gd);
		downButton.setText("Down");
		
		SelectionListener buttonListener = new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				if (e.widget == upButton) {
					tariffList.moveSelectedTariffOneUp();
					dirtyStateManager.markDirty();
				} else {
					tariffList.moveSelectedTariffOneDown();
					dirtyStateManager.markDirty();
				}
			}
		};
		
		upButton.addSelectionListener(buttonListener);
		downButton.addSelectionListener(buttonListener);
		
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
}
