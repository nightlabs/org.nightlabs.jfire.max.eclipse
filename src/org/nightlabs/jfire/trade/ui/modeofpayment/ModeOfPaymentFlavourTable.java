package org.nightlabs.jfire.trade.ui.modeofpayment;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.HashSet;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.internal.forms.widgets.SWTUtil;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.dao.ModeOfPaymentFlavourDAO;
import org.nightlabs.jfire.accounting.pay.ModeOfPaymentFlavour;
import org.nightlabs.jfire.accounting.pay.id.ModeOfPaymentFlavourID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.NLLocale;

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
			return ((ModeOfPaymentFlavour)element).getName().getText(NLLocale.getDefault().getLanguage());
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
		tableViewer.setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (!(e1 instanceof ModeOfPaymentFlavour && e2 instanceof ModeOfPaymentFlavour))
					return super.compare(viewer, e1, e2);
				String name1 = ((ModeOfPaymentFlavour) e1).getName().getText(NLLocale.getDefault().getLanguage());;
				String name2 = ((ModeOfPaymentFlavour) e2).getName().getText(NLLocale.getDefault().getLanguage());;
				return name1.compareTo(name2);
			}
		});
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer)
	{
		tableViewer.setContentProvider(new ArrayContentProvider());
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
	
	/**
	 * Obtains the referenced {@link ModeOfPaymentFlavour}s using the {@link ModeOfPaymentFlavourDAO}
	 * and sets them as input for the table.
	 * <p>
	 * This might be called from a non-UI thread.
	 * </p>
	 * 
	 * @param modeOfPaymentFlavourIDs The ids of the {@link ModeOfPaymentFlavour}s to set.
	 * @param monitor The monitor to report progress to.
	 */
	public void setModeOfPaymentFlavourIDs(Collection<ModeOfPaymentFlavourID> modeOfPaymentFlavourIDs, ProgressMonitor monitor) {
		final Collection<ModeOfPaymentFlavour> flavours = ModeOfPaymentFlavourDAO.sharedInstance().getModeOfPaymentFlavours(
				new HashSet<ModeOfPaymentFlavourID>(modeOfPaymentFlavourIDs), 
				FETCH_GROUPS_MODE_OF_PAYMENT_FLAVOUR, 
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		getTable().getDisplay().asyncExec(new Runnable() {
			public void run() {
				setInput(flavours);
			}
		});
	}
}
