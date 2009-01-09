package org.nightlabs.jfire.trade.ui.store.search;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ProductTypeTableComposite
extends AbstractTableComposite<ProductType>
{
	/**
	 * @param parent
	 * @param style
	 */
	public ProductTypeTableComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		TableLayout layout = new TableLayout();

		TableColumn name = new TableColumn(table, SWT.LEFT);
		name.setText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeTableComposite.nameTableColumn.text")); //$NON-NLS-1$
		name.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeTableComposite.nameTableColumn.toolTipText")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(10));

		TableColumn published = new TableColumn(table, SWT.LEFT);
		published.setText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeTableComposite.publishedTableColumn.text")); //$NON-NLS-1$
		published.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeTableComposite.publishedTableColumn.toolTipText")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(1));

		TableColumn confirmed = new TableColumn(table, SWT.LEFT);
		confirmed.setText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeTableComposite.confirmedTableColumn.text")); //$NON-NLS-1$
		confirmed.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeTableComposite.confirmedTableColumn.toolTipText")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(1));

		TableColumn saleable = new TableColumn(table, SWT.LEFT);
		saleable.setText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeTableComposite.saleableTableColumn.text")); //$NON-NLS-1$
		saleable.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeTableComposite.saleableTableColumn.toolTipText")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(1));

		TableColumn closed = new TableColumn(table, SWT.LEFT);
		closed.setText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeTableComposite.closedTableColumn.text")); //$NON-NLS-1$
		closed.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeTableComposite.closedTableColumn.toolTipText")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(1));

		TableColumn deliveryConf = new TableColumn(table, SWT.LEFT);
		deliveryConf.setText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeTableComposite.deliveryConfigurationTableColumn.text")); //$NON-NLS-1$
		deliveryConf.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeTableComposite.deliveryConfigurationTableColumn.toolTipText")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(10));

		TableColumn innerPriceConf = new TableColumn(table, SWT.LEFT);
		innerPriceConf.setText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeTableComposite.priceConfigTableColumn.text")); //$NON-NLS-1$
		innerPriceConf.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeTableComposite.priceConfigTableColumn.toolTipText")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(10));

//		TableColumn localAccountantDelegate = new TableColumn(table, SWT.LEFT);
//		localAccountantDelegate.setText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeTableComposite.localAccountantDelegateTableColumn.text")); //$NON-NLS-1$
//		localAccountantDelegate.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeTableComposite.localAccountantDelegateTableColumn.toolTipText")); //$NON-NLS-1$
//		layout.addColumnData(new ColumnWeightData(10));

		TableColumn owner = new TableColumn(table, SWT.LEFT);
		owner.setText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeTableComposite.ownerTableColumn.text")); //$NON-NLS-1$
		owner.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeTableComposite.ownerTableColumn.toolTipText")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(10));

		TableColumn nestedProductTypeCount = new TableColumn(table, SWT.LEFT);
		nestedProductTypeCount.setText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeTableComposite.nestedProductTypeCountTableColumn.text")); //$NON-NLS-1$
		nestedProductTypeCount.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.store.search.ProductTypeTableComposite.nestedProductTypeCountTableColumn.toolTipText")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(10));

		table.setLayout(layout);
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ProductTypeContentProvider());
		tableViewer.setLabelProvider(new ProductTypeLabelProvider());
		tableViewer.setSorter(new ViewerSorter());
	}

	class ProductTypeLabelProvider
	extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex)
		{
			if (element instanceof String) {
				if (columnIndex == 0)
					return (String)element;
				return ""; //$NON-NLS-1$
			}
			ProductType pt = (ProductType) element;
			switch (columnIndex) {
				case(0):
					if (pt.getName() != null)
						return pt.getName().getText();
				case(1):
					return ""; //$NON-NLS-1$
				case(2):
					return ""; //$NON-NLS-1$
				case(3):
					return ""; //$NON-NLS-1$
				case(4):
					return ""; //$NON-NLS-1$
				case(5):
					if (pt.getDeliveryConfiguration() != null && pt.getDeliveryConfiguration().getName() != null)
						return pt.getDeliveryConfiguration().getName().getText();
				case(6):
					if (pt.getInnerPriceConfig() != null && pt.getInnerPriceConfig().getName() != null)
						return pt.getInnerPriceConfig().getName().getText();
//				case(7):
//					if (pt.getLocalAccountantDelegate() != null && pt.getLocalAccountantDelegate().getName() != null)
//						return pt.getLocalAccountantDelegate().getName().getText();
				case(7):
					if (pt.getOwner() != null && pt.getOwner().getPerson() != null)
						return pt.getOwner().getPerson().getDisplayName();
				case(8):
					if (pt.getProductTypeLocal().getNestedProductTypeLocals() != null)
						return ""+pt.getProductTypeLocal().getNestedProductTypeLocals().size();				 //$NON-NLS-1$
			}
			return ""; //$NON-NLS-1$
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex)
		{
			if (element instanceof String)
				return null;

			ProductType pt = (ProductType) element;
			switch (columnIndex) {
				case(1):
					if (pt.isPublished())
						return SharedImages.getSharedImage(TradePlugin.getDefault(),
							ProductTypeTableComposite.class, "StatusTrue"); //$NON-NLS-1$
					else
						return SharedImages.getSharedImage(TradePlugin.getDefault(),
								ProductTypeTableComposite.class, "StatusFalse"); //$NON-NLS-1$
				case(2):
					if (pt.isConfirmed())
						return SharedImages.getSharedImage(TradePlugin.getDefault(),
							ProductTypeTableComposite.class, "StatusTrue"); //$NON-NLS-1$
					else
						return SharedImages.getSharedImage(TradePlugin.getDefault(),
								ProductTypeTableComposite.class, "StatusFalse"); //$NON-NLS-1$
				case(3):
					if (pt.isPublished())
						return SharedImages.getSharedImage(TradePlugin.getDefault(),
							ProductTypeTableComposite.class, "StatusTrue"); //$NON-NLS-1$
					else
						return SharedImages.getSharedImage(TradePlugin.getDefault(),
								ProductTypeTableComposite.class, "StatusFalse"); //$NON-NLS-1$
				case(4):
					if (pt.isClosed())
						return SharedImages.getSharedImage(TradePlugin.getDefault(),
							ProductTypeTableComposite.class, "StatusTrue"); //$NON-NLS-1$
					else
						return SharedImages.getSharedImage(TradePlugin.getDefault(),
								ProductTypeTableComposite.class, "StatusFalse");						 //$NON-NLS-1$
			}
			return super.getColumnImage(element, columnIndex);
		}
	}

	class ProductTypeContentProvider
	extends TableContentProvider
	{

	}
}
