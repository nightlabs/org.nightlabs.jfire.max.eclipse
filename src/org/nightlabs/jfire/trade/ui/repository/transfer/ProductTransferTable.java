package org.nightlabs.jfire.trade.ui.repository.transfer;

import java.util.Collection;
import java.util.Map;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.store.ProductTransfer;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.Repository;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.Anchor;
import org.nightlabs.jfire.transfer.Transfer;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.util.Util;

public class ProductTransferTable
extends AbstractTableComposite<ProductTransfer>
{
	public static final String[] FETCH_GROUPS_PRODUCT_TRANSFER = {
		FetchPlan.DEFAULT, Transfer.FETCH_GROUP_THIS_TRANSFER,
		ProductTransfer.FETCH_GROUP_PRODUCT_TYPE_ID_2_PRODUCT_COUNT_MAP,
//		ProductTransfer.FETCH_GROUP_PRODUCT_COUNT,
		LegalEntity.FETCH_GROUP_PERSON, Repository.FETCH_GROUP_NAME, Repository.FETCH_GROUP_OWNER,
		ProductType.FETCH_GROUP_NAME
	};

	private AnchorID currentRepositoryID;

	public static enum Direction {
		incoming,
		outgoing
	}

	private class ProductTransferLabelProvider extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex)
		{
			if (!(element instanceof ProductTransfer)) {
				if (columnIndex == 0)
					return String.valueOf(element);

				return ""; //$NON-NLS-1$
			}

			ProductTransfer productTransfer = (ProductTransfer) element;
			Anchor other;
			Direction direction;
			if (Util.equals(currentRepositoryID, JDOHelper.getObjectId(productTransfer.getFrom()))) {
				direction = Direction.outgoing;
				other = productTransfer.getTo();
			}
			else if (Util.equals(currentRepositoryID, JDOHelper.getObjectId(productTransfer.getTo()))) {
				direction = Direction.incoming;
				other = productTransfer.getFrom();
			}
			else
				throw new IllegalStateException("Neither ProductTransfer.from nor ProductTransfer.to is the current repository! currentRepositoryID=\"" + currentRepositoryID + "\" productTransferPK=\"" + productTransfer.getPrimaryKey() + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			switch (columnIndex) {
				case 0:
					return DateFormatter.formatDateShortTimeHMS(productTransfer.getTimestamp(), false);
				case 1: {
					StringBuffer sb = new StringBuffer();
					for (Map.Entry<ProductType, Integer> me : productTransfer.getProductType2productCountMap().entrySet()) {
						if (sb.length() > 0)
							sb.append(", "); //$NON-NLS-1$

						sb.append(me.getKey().getName().getText());
						sb.append(" ["); //$NON-NLS-1$
						sb.append(me.getValue());
						sb.append(']');
					}
					return sb.toString();
				}
				case 2:
					return direction.name();
				case 3:
					String otherStr = null;

					if (other instanceof Repository)
						otherStr = ((Repository)other).getName().getText();
					if (other instanceof LegalEntity)
						otherStr = ((LegalEntity)other).getPerson().getDisplayName();

					if (otherStr == null || "".equals(otherStr)) //$NON-NLS-1$
						return String.valueOf(JDOHelper.getObjectId(other));

					return otherStr; //$NON-NLS-1$
				case 4:
					if (other instanceof Repository)
						return ((Repository)other).getOwner().getPerson().getDisplayName();
					break;
				case 5:
					return productTransfer.getInitiator().getName();
				case 6:
					return productTransfer.getClass().getName(); // TODO localise!
			}

			return ""; //$NON-NLS-1$
		}
	}

	public ProductTransferTable(Composite parent, int style)
	{
		super(parent, style);
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		TableColumn tc;
		TableLayout layout = new TableLayout();

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.repository.transfer.ProductTransferTable.timeStampColumn.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnPixelData(140));

		tc = new TableColumn(table, SWT.RIGHT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.repository.transfer.ProductTransferTable.productTypeAndQuantityColumn.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(30));

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.repository.transfer.ProductTransferTable.directionColumn.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnPixelData(80));

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.repository.transfer.ProductTransferTable.fromOrToColumn.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(30));

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.repository.transfer.ProductTransferTable.repositoryOwnerColumn.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(30));

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.repository.transfer.ProductTransferTable.userColumn.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(20));

		tc = new TableColumn(table, SWT.RIGHT); // TODO LEFT when localised!
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.repository.transfer.ProductTransferTable.transferTypeColumn.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(20));

		table.setLayout(layout);
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer)
	{
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new ProductTransferLabelProvider());
	}

	public void setProductTransfers(AnchorID currentRepositoryID, Collection<ProductTransfer> productTransfers)
	{
		if (currentRepositoryID == null)
			throw new IllegalArgumentException("currentRepositoryID == null"); //$NON-NLS-1$

		this.currentRepositoryID = currentRepositoryID;
		super.setInput(productTransfers);
	}

	public void setLoadingStatus()
	{
		this.currentRepositoryID = null;
		super.setInput(Messages.getString("org.nightlabs.jfire.trade.ui.repository.transfer.ProductTransferTable.loadingDataPlaceholder")); //$NON-NLS-1$
	}

	@Override
	public void setInput(Object input)
	{
		throw new UnsupportedOperationException("Use setProductTransfers(...) or setLoadingStatus(...) instead!"); //$NON-NLS-1$
	}
}
