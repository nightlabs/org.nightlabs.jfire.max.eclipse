package org.nightlabs.jfire.voucher.admin.ui.localaccountantdelegate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;

public class VoucherLocalAccountantDelegateComposite
extends XComposite
implements ISelectionProvider
{
	private static final Logger logger = Logger.getLogger(VoucherLocalAccountantDelegateComposite.class);

	private Map<Currency, Account> map;

	private static class VoucherLocalAccountantDelegateLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			if (element instanceof Map.Entry) {
				Map.Entry<Currency, Account> me = (Map.Entry<Currency, Account>)element;
				switch (columnIndex) {
					case 0:
						return me.getKey().getCurrencySymbol();
					case 1:
						return me.getValue() == null ? Messages.getString("org.nightlabs.jfire.voucher.admin.ui.localaccountantdelegate.VoucherLocalAccountantDelegateComposite.VoucherLocalAccountantDelegateLabelProvider.accountName_noneAssigned") : me.getValue().getName().getText(); //$NON-NLS-1$
					default:
						return ""; //$NON-NLS-1$
				}
			}

			if (columnIndex == 0)
				return String.valueOf(element);
			else
				return ""; //$NON-NLS-1$
		}
	}

	private ICellModifier cellModifier = new ICellModifier() {
		public boolean canModify(Object element, String property)
		{
			return COLUMN_ACCOUNT.equals(property);
		}
		public Object getValue(Object element, String property)
		{
			if (COLUMN_ACCOUNT.equals(property)) {
				Map.Entry<Currency, Account> me = (Map.Entry<Currency, Account>)element;
				return me; // me.getValue() == null ? "test" : me.getValue().getName().getText();
			}
			return null;
		}
		public void modify(Object element, String property, Object value)
		{
			TableItem tableItem = (TableItem)element;
//			Map.Entry<Currency, Account> me = (Map.Entry<Currency, Account>)tableItem.getData();

			if (COLUMN_ACCOUNT.equals(property)) {
				logger.info("ICellModifier.modify: value=" + value); //$NON-NLS-1$
				Map.Entry<Currency, Account> me = (Map.Entry<Currency, Account>) value;

//				Account account = (Account) value;
//				me.setValue(account);
				tableItem.setText(1, ((ITableLabelProvider)tableViewer.getLabelProvider()).getColumnText(me, 1));

//				String txt = (String) value;
//				Account v = null;
//				try {
//					v = new Account(txt);
//				} catch (NumberFormatException x) {
//					MessageDialog.openError(getShell(), "Invalid Number", "The text you entered is not a valid number!");
//				}
//				if (v != null) {
//					me.setValue(new Account(txt));
//					tableItem.setText(1, ((ITableLabelProvider)tableViewer.getLabelProvider()).getColumnText(me, 1));
//				}
			}
		}
	};

	private DialogCellEditor cellEditor;

	private DialogCellEditor createDialogCellEditor(Table table)
	{
		return new DialogCellEditor(table) {
			@Override
			protected void updateContents(Object value)
			{
				Object v = getValue();
				Map.Entry<Currency, Account> me = (Map.Entry<Currency, Account>)v;
				getDefaultLabel().setText(((ITableLabelProvider)tableViewer.getLabelProvider()).getColumnText(me, 1));
			}

			@Override
			@Implement
			protected Object openDialogBox(Control cellEditorWindow)
			{
				Object v = getValue();
				Map.Entry<Currency, Account> me = (Map.Entry<Currency, Account>)v;

				SelectAccountWizard selectAccountWizard = new SelectAccountWizard(me.getKey(), me.getValue());
				DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(selectAccountWizard);
				if (dialog.open() != Window.OK)
					return null;

				me.setValue(selectAccountWizard.getSelectedAccount());
				return me;
			}
		};
	}

	private TableViewer tableViewer;

	private Button addCurrencyButton;
	private Button removeCurrencyButton;

	protected static final String COLUMN_CURRENCY = "currency"; //$NON-NLS-1$
	protected static final String COLUMN_ACCOUNT = "account"; //$NON-NLS-1$

	public VoucherLocalAccountantDelegateComposite(Composite parent, boolean showButtons)
	{
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		tableViewer = new TableViewer(this, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);

		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new VoucherLocalAccountantDelegateLabelProvider());
		tableViewer.setCellModifier(cellModifier);
		tableViewer.setCellEditors(new CellEditor[] {null, createDialogCellEditor(table)});
		tableViewer.setColumnProperties(new String[] { COLUMN_CURRENCY, COLUMN_ACCOUNT });
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				fireSelectionChangedEvent();
			}
		});

		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setLayout(new WeightedTableLayout(new int[] {1, 1}));
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.localaccountantdelegate.VoucherLocalAccountantDelegateComposite.currencyTableColumn.text")); //$NON-NLS-1$
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.localaccountantdelegate.VoucherLocalAccountantDelegateComposite.accountTableColumn.text")); //$NON-NLS-1$

		if (showButtons) {
			getGridLayout().numColumns = 2;
			XComposite buttonComp = new XComposite(this, SWT.NONE, LayoutDataMode.NONE);
			buttonComp.setLayoutData(new GridData());
			addCurrencyButton = new Button(buttonComp, SWT.PUSH);
			addCurrencyButton.setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.localaccountantdelegate.VoucherLocalAccountantDelegateComposite.addCurrencyButton.text")); //$NON-NLS-1$
			addCurrencyButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			addCurrencyButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					addCurrency();
				}
			});
			removeCurrencyButton = new Button(buttonComp, SWT.PUSH);
			removeCurrencyButton.setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.localaccountantdelegate.VoucherLocalAccountantDelegateComposite.removeCurrencyButton.text")); //$NON-NLS-1$
			removeCurrencyButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			removeCurrencyButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					removeCurrency();
				}
			});
		}
	}

	/**
	 * Opens a dialog and adds the selected currencies.
	 */
	public void addCurrency()
	{
		AddCurrencyWizard wizard = new AddCurrencyWizard(this);
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
		dialog.open();
	}

	/**
	 * Adds the given <code>Currency</code>. If it's already in the map, this is a noop.
	 *
	 * @param currency The <code>Currency</code> to add.
	 */
	public void addCurrency(Currency currency)
	{
		if (map == null || map.containsKey(currency))
			return;

		map.put(currency, null);
		setMap(map);
	}

	/**
	 * Removes the currently selected <code>Currency</code> from the table.
	 */
	public void removeCurrency()
	{
		Currency c = getSelectedCurrency();
		if (c != null)
			removeCurrency(c);
	}

	/**
	 * Removes the given <code>Currency</code> from the table.
	 *
	 * @param currency The <code>Currency</code> to remove.
	 */
	public void removeCurrency(Currency currency)
	{
		if (map == null)
			return;

		map.remove(currency);
		setMap(map);
	}

	public void setMap(Map<Currency, Account> map)
	{
		this.map = map;

		if (map == null)
			tableViewer.setInput(null);
		else {
			List<Map.Entry<Currency, Account>> l = new ArrayList<Map.Entry<Currency, Account>>(map.size());
			l.addAll(map.entrySet());
			Collections.sort(l, new Comparator<Map.Entry<Currency, Account>>() {
				public int compare(Map.Entry<Currency, Account> me1, Map.Entry<Currency, Account> me2)
				{
					return me1.getKey().getCurrencySymbol().compareTo(me2.getKey().getCurrencySymbol());
				}
			});
			tableViewer.setInput(l);
		}
	}

	public Map<Currency, Account> getMap()
	{
		return map;
	}

	private Currency selectedCurrency;

	public Currency getSelectedCurrency()
	{
		return selectedCurrency;
	}

	protected void fireSelectionChangedEvent()
	{
		IStructuredSelection sel = (IStructuredSelection) getSelection();
		if (sel.isEmpty())
			selectedCurrency = null;
		else
			selectedCurrency = ((Map.Entry<Currency, Account>)sel.getFirstElement()).getKey();

		if (removeCurrencyButton != null)
			removeCurrencyButton.setEnabled(selectedCurrency != null);

		Object[] listeners = selectionChangedListeners.getListeners();
		if (listeners.length < 1)
			return;

		SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());
		for (Object l : listeners) {
			ISelectionChangedListener listener = (ISelectionChangedListener) l;
			listener.selectionChanged(event);
		}
	}

	private ListenerList selectionChangedListeners = new ListenerList();

	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.add(listener);
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.remove(listener);
	}

	public ISelection getSelection()
	{
		return tableViewer.getSelection();
	}

	public void setSelection(ISelection selection)
	{
		tableViewer.setSelection(selection);
	}
}
