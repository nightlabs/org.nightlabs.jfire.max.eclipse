package org.nightlabs.jfire.trade.ui.modeofpayment;

import java.util.Locale;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.jfire.accounting.pay.ModeOfPayment;

public class ModeOfPaymentTable
		extends AbstractTableComposite<ModeOfPayment>
{
	
	/**
	 * The minimal fetch-groups needed for a {@link ModeOfPayment}
	 * to be displayed in this table.
	 */
	public static final String[] FETCH_GROUPS_MODE_OF_PAYMENT_FLAVOUR = new String[] {
		FetchPlan.DEFAULT, ModeOfPayment.FETCH_GROUP_NAME
	};
	
	private class LabelProvider extends org.eclipse.jface.viewers.LabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			return ((ModeOfPayment)element).getName().getText(Locale.getDefault().getLanguage());
		}
	}

	public ModeOfPaymentTable(Composite parent, int style, int viewerStyle) {
		super(parent, style, true, viewerStyle);
		table.setHeaderVisible(false); // if this is set to true, then table-columns need to be externalised
	}
	
	public ModeOfPaymentTable(Composite parent)
	{
		this(parent, SWT.NONE, DEFAULT_STYLE_SINGLE_BORDER);
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		// header is not visible => no externalisation needed
		new TableColumn(table, SWT.LEFT).setText("ModeOfPayment"); //$NON-NLS-1$
		table.setLayout(new WeightedTableLayout(new int[] {1}));
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer)
	{
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
	}
}
