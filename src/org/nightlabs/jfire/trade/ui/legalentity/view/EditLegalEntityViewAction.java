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

package org.nightlabs.jfire.trade.ui.legalentity.view;

import javax.jdo.JDOHelper;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntityEditorWizard;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class EditLegalEntityViewAction extends Action {

	public EditLegalEntityViewAction() {
		super();
	}

	private LegalEntityEditorView view;
	
	/**
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(LegalEntityEditorView view) {
		this.view = view;
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run() {
		LegalEntity legalEntity = LegalEntityEditorWizard.open(view.getSelectedLegalEntity());
		if (legalEntity != null) {
			view.setSelectedLegalEntityID((AnchorID) JDOHelper.getObjectId(legalEntity));
		}
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return SharedImages.getSharedImageDescriptor(TradePlugin.getDefault(), this.getClass());
	}
	
	@Override
	public String getText() {
		return Messages.getString("org.nightlabs.jfire.trade.ui.legalentity.view.EditLegalEntityViewAction.text"); //$NON-NLS-1$
	}
	
	@Override
	public String getToolTipText() {
		return Messages.getString("org.nightlabs.jfire.trade.ui.legalentity.view.EditLegalEntityViewAction.tooltip"); //$NON-NLS-1$
	}
}
