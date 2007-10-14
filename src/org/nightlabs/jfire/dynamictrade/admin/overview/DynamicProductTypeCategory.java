package org.nightlabs.jfire.dynamictrade.admin.overview;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.dynamictrade.admin.createproducttype.CreateDynamicProductTypeAction;
import org.nightlabs.jfire.dynamictrade.admin.editor.DynamicProductTypeEditor;
import org.nightlabs.jfire.dynamictrade.admin.editor.DynamicProductTypeEditorInput;
import org.nightlabs.jfire.dynamictrade.admin.tree.DynamicProductTypeTree;
import org.nightlabs.jfire.dynamictrade.admin.tree.DynamicProductTypeTreeNode;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.overview.AbstractTradeAdminCategory;
import org.nightlabs.jfire.trade.admin.ui.overview.TradeAdminCategoryFactory;

public class DynamicProductTypeCategory
		extends AbstractTradeAdminCategory
{

	public DynamicProductTypeCategory(
			TradeAdminCategoryFactory tradeAdminCategoryFactory)
	{
		super(tradeAdminCategoryFactory);
	}

	@Implement
	protected Composite _createComposite(Composite parent)
	{
		DynamicProductTypeTree dynamicProductTypeTree = new DynamicProductTypeTree(parent);
		dynamicProductTypeTree.getTreeViewer().expandToLevel(3);
		CreateDynamicProductTypeAction createDynamicProductTypeAction = new CreateDynamicProductTypeAction(dynamicProductTypeTree);
		dynamicProductTypeTree.addContextMenuContribution(createDynamicProductTypeAction);
		dynamicProductTypeTree.getTreeViewer().addDoubleClickListener(doubleClickListener);
		return dynamicProductTypeTree;
	}

	private IDoubleClickListener doubleClickListener = new IDoubleClickListener(){	
		public void doubleClick(DoubleClickEvent event) {
			if (!event.getSelection().isEmpty()) {
				StructuredSelection sel = (StructuredSelection) event.getSelection();
				Object firstElement = sel.getFirstElement();
				if (firstElement instanceof DynamicProductTypeTreeNode) {
					DynamicProductTypeTreeNode treeNode = (DynamicProductTypeTreeNode) firstElement;
					DynamicProductType dynamicProductType = treeNode.getJdoObject();
					ProductTypeID dynamicProductTypeID = (ProductTypeID) JDOHelper.getObjectId(dynamicProductType);
					try {
						RCPUtil.openEditor(new DynamicProductTypeEditorInput(dynamicProductTypeID), 
								DynamicProductTypeEditor.EDITOR_ID);
					} catch (PartInitException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}	
	};
}
