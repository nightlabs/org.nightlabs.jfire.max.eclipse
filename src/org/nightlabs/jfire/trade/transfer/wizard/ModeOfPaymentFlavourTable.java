package org.nightlabs.jfire.trade.transfer.wizard;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Locale;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.internal.forms.widgets.SWTUtil;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jfire.accounting.pay.ModeOfPaymentFlavour;

public class ModeOfPaymentFlavourTable
		extends AbstractTableComposite
{
	private static class ContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Collection) {
				return ((Collection)inputElement).toArray();
			}
			else
				throw new IllegalArgumentException("ModeOfPaymentFlavourTable.ContentProvider expects a Collection as inputElement. Recieved "+inputElement.getClass().getName()); //$NON-NLS-1$
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
	
	private static class LabelProvider extends org.eclipse.jface.viewers.LabelProvider implements ITableLabelProvider {

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

	public ModeOfPaymentFlavourTable(Composite parent)
	{
		super(parent, SWT.NONE, true, DEFAULT_STYLE_SINGLE_BORDER);
		table.setHeaderVisible(false); // if this is set to true, then table-columns need to be externalised
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
		tableViewer.setContentProvider(new ContentProvider());
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
