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

package org.nightlabs.jfire.simpletrade.admin.producttype.create;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.simpletrade.admin.producttype.ProductTypeTree;
import org.nightlabs.jfire.simpletrade.admin.producttype.ProductTypeTreeNode;
import org.nightlabs.jfire.simpletrade.admin.resource.Messages;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class CreateProductTypeAction extends Action
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(CreateProductTypeAction.class);
	
	protected ProductTypeTree tree;
	protected ProductTypeTreeNode selectedNode = null;

	public CreateProductTypeAction(ProductTypeTree tree)
	{
		super(Messages.getString("org.nightlabs.jfire.simpletrade.admin.producttype.create.CreateProductTypeAction.text")); //$NON-NLS-1$
		setEnabled(false);
		this.tree = tree;
		tree.addSelectionChangedListener(
			new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event)
				{
					logger.debug("selection changed! selection: "+event.getSelection().getClass().getName()+", "+event.getSelection()); //$NON-NLS-1$ //$NON-NLS-2$
					StructuredSelection selection = (StructuredSelection) event.getSelection();
					if (selection.isEmpty())
						selectedNode = null;
					else
						selectedNode = (ProductTypeTreeNode) selection.getFirstElement();

					setEnabled(selectedNode != null && selectedNode.getJdoObject().isInheritanceBranch());
				}
			});
	}

	public void run()
	{
		try {
			logger.debug("run()! selectedNode="+selectedNode); //$NON-NLS-1$
			if (selectedNode == null)
				throw new IllegalStateException("No node selected!"); //$NON-NLS-1$

			CreateProductTypeWizard createProductWizard = new CreateProductTypeWizard(selectedNode.getJdoObject().getObjectId());
			DynamicPathWizardDialog wizardDialog = new DynamicPathWizardDialog(RCPUtil.getActiveWorkbenchShell(), createProductWizard);
			wizardDialog.open();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
