package org.nightlabs.jfire.trade.ui.modeofdelivery;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.HashSet;

import javax.jdo.FetchPlan;

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
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.store.deliver.ModeOfDeliveryFlavour;
import org.nightlabs.jfire.store.deliver.dao.ModeOfDeliveryFlavourDAO;
import org.nightlabs.jfire.store.deliver.id.ModeOfDeliveryFlavourID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.NLLocale;

public class ModeOfDeliveryFlavourTable
extends AbstractTableComposite<ModeOfDeliveryFlavour>
{
	/**
	 * The minimal fetch-groups needed for a {@link ModeOfDeliveryFlavour}
	 * to be displayed in this table.
	 */
	public static final String[] FETCH_GROUPS_MODE_OF_DELIVERY_FLAVOUR = new String[] {
		FetchPlan.DEFAULT, ModeOfDeliveryFlavour.FETCH_GROUP_NAME,
		ModeOfDeliveryFlavour.FETCH_GROUP_ICON_16X16_DATA
	};

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
			return ((ModeOfDeliveryFlavour)element).getName().getText(NLLocale.getDefault().getLanguage());
		}
	}

	public ModeOfDeliveryFlavourTable(Composite parent)
	{
		this(parent, SWT.NONE, DEFAULT_STYLE_SINGLE_BORDER);
	}

	public ModeOfDeliveryFlavourTable(Composite parent, int style, int viewerStyle)
	{
		super(parent, style, true, viewerStyle);
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

	/**
	 * Obtains the referenced {@link ModeOfDeliveryFlavour}s using the {@link ModeOfDeliveryFlavourDAO}
	 * and sets them as input for the table.
	 * <p>
	 * This might be called from a non-UI thread.
	 * </p>
	 *
	 * @param ModeOfDeliveryFlavourIDs The ids of the {@link ModeOfDeliveryFlavour}s to set.
	 * @param monitor The monitor to report progress to.
	 */
	public void setModeOfDeliveryFlavourIDs(Collection<ModeOfDeliveryFlavourID> modeOfDeliveryFlavourIDs, ProgressMonitor monitor) {
		final Collection<ModeOfDeliveryFlavour> flavours = ModeOfDeliveryFlavourDAO.sharedInstance().getModeOfDeliveryFlavours(
				new HashSet<ModeOfDeliveryFlavourID>(modeOfDeliveryFlavourIDs),
				FETCH_GROUPS_MODE_OF_DELIVERY_FLAVOUR,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		if (!getTable().isDisposed()) {
			getTable().getDisplay().asyncExec(new Runnable() {
				public void run() {
					setInput(flavours);
				}
			});
		}
	}
}
