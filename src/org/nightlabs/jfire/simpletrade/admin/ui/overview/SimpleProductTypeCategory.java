package org.nightlabs.jfire.simpletrade.admin.ui.overview;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.simpletrade.admin.ui.editor.SimpleProductTypeEditor;
import org.nightlabs.jfire.simpletrade.admin.ui.producttype.ProductTypeTree;
import org.nightlabs.jfire.simpletrade.admin.ui.producttype.ProductTypeTreeNode;
import org.nightlabs.jfire.simpletrade.admin.ui.producttype.create.CreateProductTypeAction;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.editor.ProductTypeEditorInput;
import org.nightlabs.jfire.trade.admin.ui.overview.AbstractTradeAdminCategory;
import org.nightlabs.jfire.trade.admin.ui.overview.TradeAdminCategoryFactory;

/**
 * @author Marco Schulze - Marco at NightLabs dot de
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 */
public class SimpleProductTypeCategory
		extends AbstractTradeAdminCategory
{

	public SimpleProductTypeCategory(
			TradeAdminCategoryFactory tradeAdminCategoryFactory)
	{
		super(tradeAdminCategoryFactory);
	}

	@Override
	@Implement
	protected Composite _createComposite(Composite parent)
	{
		ProductTypeTree productTypeTree = new ProductTypeTree(parent, SWT.NONE); // , null, SimpletradePlugin.ZONE_ADMIN);
		productTypeTree.getTreeViewer().expandToLevel(3);
		productTypeTree.getTreeViewer().addDoubleClickListener(doubleClickListener);
		createProductAction = new CreateProductTypeAction(productTypeTree);
		productTypeTree.addContextMenuContribution(createProductAction);
		return productTypeTree;
	}

	private CreateProductTypeAction createProductAction = null;

	private IDoubleClickListener doubleClickListener = new IDoubleClickListener(){
		public void doubleClick(DoubleClickEvent event) {
			if (!event.getSelection().isEmpty() && event.getSelection() instanceof StructuredSelection) {
				StructuredSelection sel = (StructuredSelection) event.getSelection();
				if (sel.getFirstElement() instanceof ProductTypeTreeNode) {
					ProductTypeTreeNode productTypeTreeNode = (ProductTypeTreeNode) sel.getFirstElement();
					ProductTypeID productTypeID = productTypeTreeNode.getJdoObject().getObjectId();
					try {
						RCPUtil.openEditor(new ProductTypeEditorInput(productTypeID), SimpleProductTypeEditor.ID_EDITOR);
					} catch (PartInitException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	};
}
