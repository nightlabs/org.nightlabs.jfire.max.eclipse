package org.nightlabs.jfire.trade.ui.modeofdelivery;

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
import org.nightlabs.jfire.store.deliver.ModeOfDeliveryFlavour;
import org.nightlabs.jfire.trade.ui.resource.Messages;

public class ModeOfDeliveryFlavourTable
extends AbstractTableComposite<ModeOfDeliveryFlavour>
{
	private static class ContentProvider implements IStructuredContentProvider {

		@SuppressWarnings("unchecked") //$NON-NLS-1$
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Collection) {
				return ((Collection)inputElement).toArray();
			}
			else
				throw new IllegalArgumentException("ModeOfDeliveryFlavourTable.ContentProvider expects a Collection as inputElement. Recieved "+inputElement.getClass().getName()); //$NON-NLS-1$
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
	
	private static class LabelProvider extends org.eclipse.jface.viewers.LabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			byte[] iconData = ((ModeOfDeliveryFlavour)element).getIcon16x16Data();
			if (iconData == null)
				return null;

			ByteArrayInputStream in = new ByteArrayInputStream(iconData);
			Image image = new Image(SWTUtil.getStandardDisplay(), in);
			return image;
		}

		public String getColumnText(Object element, int columnIndex) {
			return ((ModeOfDeliveryFlavour)element).getName().getText(Locale.getDefault().getLanguage());
		}
	}

	public ModeOfDeliveryFlavourTable(Composite parent)
	{
		super(parent, SWT.NONE, true, DEFAULT_STYLE_SINGLE_BORDER);
		setHeaderVisible(false); // if this is changed to true, we need to localize the tableColumns
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		// the header is not visible => no need to externalize
		new TableColumn(table, SWT.LEFT).setText("ModeOfDeliveryFlavour"); //$NON-NLS-1$
		table.setLayout(new WeightedTableLayout(new int[] {1}));
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer)
	{
		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
	}

	/**
	 * @return Returns <code>null</code>, if no {@link ModeOfDeliveryFlavour} is selected,
	 *		otherwise the one selected.
	 */
	public ModeOfDeliveryFlavour getSelectedModeOfDeliveryFlavour()
	{
		IStructuredSelection sel = (IStructuredSelection) getTableViewer().getSelection();
		if (sel.isEmpty())
			return null;
		else
			return (ModeOfDeliveryFlavour) sel.getFirstElement();
	}
}
