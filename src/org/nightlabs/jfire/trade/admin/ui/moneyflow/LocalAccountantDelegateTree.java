/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://opensource.org/licenses/lgpl-license.php                         *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.trade.admin.ui.moneyflow;

import java.util.Locale;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.base.ui.tree.TreeContentProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.book.LocalAccountantDelegate;
import org.nightlabs.jfire.accounting.book.id.LocalAccountantDelegateID;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class LocalAccountantDelegateTree extends AbstractTreeComposite {

	public static final String[] DEFAULT_DELEGATE_FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		LocalAccountantDelegate.FETCH_GROUP_NAME
	};
	
	private static class ContentProvider extends TreeContentProvider {

		private Class delegateClass;
		
		private static final Object[] EMPTY_DATA = new Object[]{};
		
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Class)
				delegateClass = (Class)inputElement;
			if (delegateClass == null)
				return EMPTY_DATA;
			return 
				LocalAccountantDelegateProvider.sharedInstance()
				.getTopLevelDelegates(delegateClass, DEFAULT_DELEGATE_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT).toArray();
		}

		/**
		 * @see org.nightlabs.base.ui.tree.TreeContentProvider#getChildren(java.lang.Object)
		 */		
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof LocalAccountantDelegate)
				return 
					LocalAccountantDelegateProvider.sharedInstance()
					.getChildDelegates(
						(LocalAccountantDelegateID)JDOHelper.getObjectId(parentElement),
						DEFAULT_DELEGATE_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT
					).toArray();
			return EMPTY_DATA;
		}

		/**
		 * @see org.nightlabs.base.ui.tree.TreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object element) {
			return true;
		}

		public void dispose() {
		}
		
		public void setDelegateClass(Class delegateClass) {
			this.delegateClass = delegateClass;
		}
	}
	
	

	
	private static class LabelProvider extends TableLabelProvider {
		public String getColumnText(Object element, int columnIndex) {
			return getText(element);
		}

		/**
		 * @see org.nightlabs.base.ui.table.TableLabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object element) {
			if (element instanceof LocalAccountantDelegate)
				return ((LocalAccountantDelegate)element).getName().getText(Locale.getDefault().getLanguage());
			return ""; //$NON-NLS-1$
		}
	}
	
	
	
	
	private Class delegateClass;
	
	/**
	 * @param parent
	 */
	public LocalAccountantDelegateTree(Composite parent, Class delegateClass) {
		super(parent, DEFAULT_STYLE_SINGLE | SWT.BORDER, true, false, false);
		this.delegateClass = delegateClass;
		init();
	}

	/**
	 * @see org.nightlabs.base.ui.tree.AbstractTreeComposite#setTreeProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	public void setTreeProvider(TreeViewer treeViewer) {
		treeViewer.setContentProvider(new ContentProvider());
		treeViewer.setLabelProvider(new LabelProvider());
		if (delegateClass != null) {
			treeViewer.setInput(delegateClass);
		}
	}

	/**
	 * @see org.nightlabs.base.ui.tree.AbstractTreeComposite#createTreeColumns(org.eclipse.swt.widgets.Tree)
	 */
	public void createTreeColumns(Tree tree) {
		// no columns for this tree
	}
	
	public void setDelegateClass(Class delegateClass) {
		this.delegateClass = delegateClass;
		if (delegateClass != null)
			getTreeViewer().setInput(delegateClass);
		else {
			getContentProvider().setDelegateClass(null);
			getTreeViewer().setInput(getContentProvider());
		}
			
	}
	
	public Class getDelegateClass() {
		return delegateClass;
	}
	
	protected ContentProvider getContentProvider() {
		return (ContentProvider)getTreeViewer().getContentProvider();
	}
	
	/**
	 */
	public LocalAccountantDelegate getSelectedDelegate() {
		if (getTree().getSelectionCount() == 1) {
			return (LocalAccountantDelegate)getTree().getSelection()[0].getData();
		}
		return null;
	}
}
