package org.nightlabs.jfire.trade.ui.transfer.print;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.jfire.trade.ui.resource.Messages;

public class AutomaticPrintingOptionsGroup extends XComposite {

	Group group;
	Button doPrintCheckbox;
	Spinner printCountSpinner;
	Label infoLabel;
	int printCount = 0;
	boolean doPrint = true;
	String entityName;
	String infoText;

	public AutomaticPrintingOptionsGroup(Composite parent, String groupTitle, String entityName, String description)
	{
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL);
		this.entityName = entityName;

		group = new Group(this, SWT.NONE);
		group.setText(groupTitle);
		group.setLayout(new GridLayout(3, false));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.verticalIndent = 10;
		group.setLayoutData(gridData);

		if (description != null) {
			Label label = new Label(group, SWT.WRAP);
			label.setText(description);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 3;
			label.setLayoutData(gridData);
		}

		XComposite wrapper = new XComposite(group, SWT.NONE, LayoutMode.LEFT_RIGHT_WRAPPER, LayoutDataMode.NONE, 2);

		doPrintCheckbox = new Button(wrapper, SWT.CHECK);
		doPrintCheckbox.setText(String.format(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.print.AutomaticPrintingOptionsGroup.button.print.text"), entityName)); //$NON-NLS-1$
		wrapper = new XComposite(group, SWT.NONE, LayoutMode.LEFT_RIGHT_WRAPPER, LayoutDataMode.NONE, 2);
		Label label = new Label(wrapper, SWT.NONE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalIndent = 10;
		label.setLayoutData(gridData);
		label.setText("#"); //$NON-NLS-1$

		printCountSpinner = new Spinner(wrapper, getBorderStyle());
		printCountSpinner.setMinimum(0);
		printCountSpinner.setMaximum(-1);
		printCountSpinner.setDigits(0);
		printCountSpinner.setIncrement(1);
		infoLabel = new Label(group, SWT.RIGHT);
		infoLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		doPrintCheckbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean checkForMin = !doPrint;
				doPrint = doPrintCheckbox.getSelection();
				if (checkForMin) {
					printCountSpinner.setSelection(Math.max(1, printCountSpinner.getSelection()));
					printCount = printCountSpinner.getSelection();
				}
				updateInfoLabel();

				if (dirtyStateManager != null)
					dirtyStateManager.markDirty();
			}
		});
		printCountSpinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doPrintCheckbox.setSelection(printCountSpinner.getSelection() != 0);
				doPrint = doPrintCheckbox.getSelection();
				printCount = printCountSpinner.getSelection();
				updateInfoLabel();

				if (dirtyStateManager != null)
					dirtyStateManager.markDirty();
			}
		});
	}

	protected void updateInfoLabel() {
		if (getActualPrintCount() == 0)
			infoLabel.setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.print.AutomaticPrintingOptionsGroup.infoLabel.text")); //$NON-NLS-1$
		else {
			String copyText = printCount == 1 ? Messages.getString("org.nightlabs.jfire.trade.ui.transfer.print.AutomaticPrintingOptionsGroup.copy") : Messages.getString("org.nightlabs.jfire.trade.ui.transfer.print.AutomaticPrintingOptionsGroup.copies"); //$NON-NLS-1$ //$NON-NLS-2$
			infoLabel.setText(String.format(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.print.AutomaticPrintingOptionsGroup.willBePrinted"), printCount, copyText)); //$NON-NLS-1$
		}
	}

	public int getActualPrintCount() {
		return (doPrint ? printCount : 0);
	}

	public int getEnteredPrintCount() {
		return printCount;
	}

	public void setEnteredPrintCount(int printCount) {
		this.printCount = printCount;
		this.printCountSpinner.setSelection(printCount);
		if (printCount > 0)
			doPrintCheckbox.setSelection(true);

		updateInfoLabel();
	}

	public void setDoPrint(boolean value) {
		doPrint = value;
		this.doPrintCheckbox.setSelection(doPrint);

		updateInfoLabel();
	}

	public boolean getDoPrint() {
		return doPrint;
	}

	private IDirtyStateManager dirtyStateManager;
	public void setDirtyStateManager(IDirtyStateManager dirtyStateManager) {
		this.dirtyStateManager = dirtyStateManager;
	}
}
