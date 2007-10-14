package org.nightlabs.jfire.voucher.admin.ui.priceconfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;
import org.nightlabs.l10n.NumberFormatter;

public class CurrencyAmountTable
extends XComposite
implements ISelectionProvider
{
	private static final Logger logger = Logger.getLogger(CurrencyAmountTable.class);

	private Map<Currency, Long> map;

	private static class CurrencyAmountLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			if (element instanceof Map.Entry) {
				Map.Entry<Currency, Long> me = (Map.Entry<Currency, Long>)element;
				switch (columnIndex) {
					case 0:
						return me.getKey().getCurrencySymbol();
					case 1:
						return NumberFormatter.formatCurrency(me.getValue(), 1, me.getKey(), false);
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
			return COLUMN_AMOUNT.equals(property);
		}
		public Object getValue(Object element, String property)
		{
			if (COLUMN_AMOUNT.equals(property)) {
				Map.Entry<Currency, Long> me = (Map.Entry<Currency, Long>)element;
				return String.valueOf(me.getValue());
			}
			return null;
		}
		public void modify(Object element, String property, Object value)
		{
			TableItem tableItem = (TableItem)element;
			Map.Entry<Currency, Long> me = (Map.Entry<Currency, Long>)tableItem.getData();

			if (COLUMN_AMOUNT.equals(property)) {
				logger.info("ICellModifier.modify: value=" + value); //$NON-NLS-1$
				String txt = (String) value;
				Long v = null;
				try {
					v = new Long(txt);
				} catch (NumberFormatException x) {
					MessageDialog.openError(getShell(), Messages.getString("org.nightlabs.jfire.voucher.admin.ui.priceconfig.CurrencyAmountTable.errorMessageInvalidNumber.title"), Messages.getString("org.nightlabs.jfire.voucher.admin.ui.priceconfig.CurrencyAmountTable.errorMessageInvalidNumber.message")); //$NON-NLS-1$ //$NON-NLS-2$
				}
				if (v != null) {
					me.setValue(v);
					tableItem.setText(1, ((ITableLabelProvider)tableViewer.getLabelProvider()).getColumnText(me, 1));
				}
			}
		}
	};

	private TableViewer tableViewer;

	private Button addCurrencyButton;
	private Button removeCurrencyButton;

	protected static final String COLUMN_CURRENCY = "currency"; //$NON-NLS-1$
	protected static final String COLUMN_AMOUNT = "amount"; //$NON-NLS-1$

	public CurrencyAmountTable(Composite parent, boolean showButtons)
	{
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		tableViewer = new TableViewer(this, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();

		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new CurrencyAmountLabelProvider());
		tableViewer.setCellModifier(cellModifier);
		TextCellEditor tce = new TextCellEditor(table);
//		tce.setValidator(new ICellEditorValidator() {
//			public String isValid(Object value)
//			{
//				logger.info("ICellEditorValidator.isValid: value=" + value);
//				String txt = (String) value;
//				try {
//					Long.parseLong(txt);
//					return null;
//				} catch (NumberFormatException x) {
//					return "The text you entered is not a valid number: " + txt;
//				}
//			}
//		});
		tableViewer.setCellEditors(new CellEditor[] {null, tce});
		tableViewer.setColumnProperties(new String[] { COLUMN_CURRENCY, COLUMN_AMOUNT });
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				fireSelectionChangedEvent();
			}
		});

		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setLayout(new WeightedTableLayout(new int[] {1, 1}));
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.priceconfig.CurrencyAmountTable.currencyTableColumn.text")); //$NON-NLS-1$
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.priceconfig.CurrencyAmountTable.amountTableColumn.text")); //$NON-NLS-1$

		if (showButtons) {
			getGridLayout().numColumns = 2;
			XComposite buttonComp = new XComposite(this, SWT.NONE, LayoutDataMode.NONE);
			buttonComp.setLayoutData(new GridData());
			addCurrencyButton = new Button(buttonComp, SWT.PUSH);
			addCurrencyButton.setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.priceconfig.CurrencyAmountTable.addCurrencyButton.text")); //$NON-NLS-1$
			addCurrencyButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			addCurrencyButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					addCurrency();
				}
			});
			removeCurrencyButton = new Button(buttonComp, SWT.PUSH);
			removeCurrencyButton.setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.priceconfig.CurrencyAmountTable.removeCurrencyButton.text")); //$NON-NLS-1$
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

		map.put(currency, new Long(0));
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

	public void setMap(Map<Currency, Long> map)
	{
		this.map = map;

		if (map == null)
			tableViewer.setInput(null);
		else {
			List<Map.Entry<Currency, Long>> l = new ArrayList<Map.Entry<Currency,Long>>(map.size());
			l.addAll(map.entrySet());
			Collections.sort(l, new Comparator<Map.Entry<Currency, Long>>() {
				public int compare(Map.Entry<Currency, Long> me1, Map.Entry<Currency, Long> me2)
				{
					return me1.getKey().getCurrencySymbol().compareTo(me2.getKey().getCurrencySymbol());
				}
			});
			tableViewer.setInput(l);
		}
	}

	public Map<Currency, Long> getMap()
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
			selectedCurrency = ((Map.Entry<Currency, Long>)sel.getFirstElement()).getKey();

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
