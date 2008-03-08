package org.nightlabs.jfire.trade.ui.modeofpayment;

import java.io.ByteArrayInputStream;
import java.util.Locale;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.internal.forms.widgets.SWTUtil;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.jfire.accounting.pay.ModeOfPaymentFlavour;

public class ModeOfPaymentFlavourTable
		extends AbstractTableComposite<ModeOfPaymentFlavour>
{
	
	/**
	 * The minimal fetch-groups needed for a {@link ModeOfPaymentFlavour}
	 * to be displayed in this table.
	 */
	public static final String[] FETCH_GROUPS_MODE_OF_PAYMENT_FLAVOUR = new String[] {
		FetchPlan.DEFAULT, ModeOfPaymentFlavour.FETCH_GROUP_ICON_16X16_DATA,
		ModeOfPaymentFlavour.FETCH_GROUP_NAME
	};
	
	private class LabelProvider extends org.eclipse.jface.viewers.LabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			byte[] iconData = ((ModeOfPaymentFlavour)element).getIcon16x16Data();
			if (iconData == null)
				return null;

			ByteArrayInputStream in = new ByteArrayInputStream(iconData);
			Image image = new Image(SWTUtil.getStandardDisplay(), in);
			return image;
		}

		public String getColumnText(Object element, int columnIndex) {
			return ((ModeOfPaymentFlavour)element).getName().getText(Locale.getDefault().getLanguage());
		}
	}

	public ModeOfPaymentFlavourTable(Composite parent, int style, int viewerStyle) {
		super(parent, style, true, viewerStyle);
		setHeaderVisible(false); // if this is set to true, then table-columns need to be externalised
	}
	
	public ModeOfPaymentFlavourTable(Composite parent)
	{
		this(parent, SWT.NONE, DEFAULT_STYLE_SINGLE_BORDER);
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		// header is not visible => no externalisation needed
		new TableColumn(table, SWT.LEFT).setText("ModeOfPaymentFlavour"); //$NON-NLS-1$
		table.setLayout(new WeightedTableLayout(new int[] {1}));
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer)
	{
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
	}

	/**
	 * @return Returns <code>null</code>, if no {@link ModeOfPaymentFlavour} is selected,
	 *		otherwise the one selected.
	 */
	public ModeOfPaymentFlavour getSelectedModeOfPaymentFlavour()
	{
		IStructuredSelection sel = (IStructuredSelection) getTableViewer().getSelection();
		if (sel.isEmpty())
			return null;
		else
			return (ModeOfPaymentFlavour) sel.getFirstElement();
	}

}
