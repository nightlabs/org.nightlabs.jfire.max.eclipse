package org.nightlabs.jfire.trade.ui.transfer.error;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.base.ui.tree.TreeContentProvider;
import org.nightlabs.jfire.accounting.pay.PaymentData;
import org.nightlabs.jfire.store.deliver.DeliveryData;
import org.nightlabs.jfire.trade.ui.resource.Messages;

public class TransferTreeComposite
extends AbstractTreeComposite<TransferTreeNode>
{
	private static class TransferLabelProvider
	extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex)
		{
			if (element instanceof TransferTreeNode)
				return ((TransferTreeNode)element).getColumnText(columnIndex);

			if (columnIndex == 0)
				return String.valueOf(element);

			return ""; //$NON-NLS-1$
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex)
		{
			if (element instanceof TransferTreeNode)
				return ((TransferTreeNode)element).getColumnImage(columnIndex);

			return super.getColumnImage(element, columnIndex);
		}
	}

	private static class TransferContentProvider
	extends TreeContentProvider
	{
		@SuppressWarnings("unchecked") //$NON-NLS-1$
		public Object[] getElements(Object inputElement)
		{
			if (inputElement instanceof TransferTreeNode)
				return ((TransferTreeNode)inputElement).getChildren();

			if (inputElement instanceof Object[]) {
				List<TransferTreeNode> result = new ArrayList<TransferTreeNode>();
				Object[] oa = (Object[]) inputElement;
				for (Object object : oa) {
					if (object instanceof Collection) {
						for (Iterator it = ((Collection)object).iterator(); it.hasNext(); ) {
							Object o = it.next();
							if (o instanceof PaymentData)
								result.add(new PaymentTreeNode(((PaymentData)o).getPayment()));
							else if (o instanceof DeliveryData)
								result.add(new DeliveryTreeNode(((DeliveryData)o).getDelivery()));
							else
								throw new IllegalStateException("invalid input element (PaymentData or DeliveryData expected): " + inputElement); //$NON-NLS-1$
						}
					}
					else
						throw new IllegalStateException("invalid input element (Collection expected): " + object + " inputElement: " + inputElement); //$NON-NLS-1$ //$NON-NLS-2$
				}
				return result.toArray();
			}
			throw new IllegalStateException("invalid input element (Object[] expected): " + inputElement); //$NON-NLS-1$
		}

		@Override
		public Object[] getChildren(Object parentElement)
		{
			if (parentElement instanceof TransferTreeNode)
				return ((TransferTreeNode)parentElement).getChildren();

			return super.getChildren(parentElement);
		}

		@Override
		public boolean hasChildren(Object element)
		{
			if (element instanceof TransferTreeNode)
				return ((TransferTreeNode)element).hasChildren();

			return super.hasChildren(element);
		}
	}

	public TransferTreeComposite(Composite parent)
	{
		super(parent);
	}

	@Override
	@Implement
	public void createTreeColumns(Tree tree)
	{
		tree.setHeaderVisible(true);

		TreeColumn tc;

		tc = new TreeColumn(tree, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.TransferTreeComposite.typeOrPhaseTableColumn.text")); //$NON-NLS-1$

		tc = new TreeColumn(tree, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.TransferTreeComposite.amountOrResultStateTableColumn.text")); //$NON-NLS-1$

		tc = new TreeColumn(tree, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.TransferTreeComposite.totalStateTableColumn.text")); //$NON-NLS-1$

		tc = new TreeColumn(tree, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.error.TransferTreeComposite.rollbackOrErrorMessageTableColumn.text")); //$NON-NLS-1$
		
		tree.setLayout(new WeightedTableLayout(
				new int[] {  -1, -1,  -1, 10 },
				new int[] { 150, 150, 100, -1 }));
	}

	@Override
	@Implement
	public void setTreeProvider(TreeViewer treeViewer)
	{
		treeViewer.setContentProvider(new TransferContentProvider());
		treeViewer.setLabelProvider(new TransferLabelProvider());
	}

	@Override
	public void setInput(Object input)
	{
		throw new UnsupportedOperationException("use setInput(List<PaymentData> paymentDatas, List<DeliveryData> deliveryDatas) instead!"); //$NON-NLS-1$
	}

	public void setInput(List<PaymentData> paymentDatas, List<DeliveryData> deliveryDatas)
	{
		if (paymentDatas == null && deliveryDatas != null)
			super.setInput(new Object[] { deliveryDatas });
		else if (paymentDatas != null && deliveryDatas == null)
			super.setInput(new Object[] { paymentDatas });
		else if (paymentDatas != null && deliveryDatas != null)
			super.setInput(new Object[] { paymentDatas, deliveryDatas });
		else
			throw new IllegalArgumentException("paymentDatas and deliveryDatas must not both be null!"); //$NON-NLS-1$
	}
}
