/**
 * 
 */
package org.nightlabs.jfire.trade.ui.producttype.quicklist;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.jfire.store.ProductType;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public abstract class AbstractProductTypeTree<P extends ProductType> 
extends AbstractTreeComposite<P> 
{
	private AbstractProductTypeQuickListFilter filter;
	
	/**
	 * @param parent
	 */
	public AbstractProductTypeTree(Composite parent) {
		this(parent, AbstractTreeComposite.DEFAULT_STYLE_SINGLE);
	}

	/**
	 * @param parent
	 * @param init
	 */
	public AbstractProductTypeTree(Composite parent, int style) {
		this(parent, style, null);
	}

	/**
	 * @param parent
	 * @param init
	 */
	public AbstractProductTypeTree(Composite parent, int style, 
			AbstractProductTypeQuickListFilter filter) 
	{
		super(parent, style, true, true, true);
		this.filter = filter;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.tree.AbstractTreeComposite#createTreeColumns(org.eclipse.swt.widgets.Tree)
	 */
	@Override
	public void createTreeColumns(Tree tree) {

	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.tree.AbstractTreeComposite#setTreeProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	@Override
	public void setTreeProvider(TreeViewer treeViewer) {

	}

}
