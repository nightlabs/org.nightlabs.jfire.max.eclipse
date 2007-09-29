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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.part.ControllablePart;
import org.nightlabs.base.ui.part.PartVisibilityListener;
import org.nightlabs.base.ui.part.PartVisibilityTracker;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.login.part.LSDPartController;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.transfer.id.AnchorID;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class LegalEntityEditorView
extends ViewPart
implements
	PartVisibilityListener,
	ControllablePart
{
	public static String ID_VIEW = LegalEntityEditorView.class.getName();
	private LegalEntitySelectionComposite selectionComposite;

	public LegalEntityEditorView() {
		LSDPartController.sharedInstance().registerPart(this);
	}
	
	@Override
	public void createPartControl(Composite parent) {
        LSDPartController.sharedInstance().createPartControl(this, parent);
        PartVisibilityTracker.sharedInstance().addVisibilityListener(this, this);
	}

	@Override
	public void setFocus() {
	}

	public void partHidden(IWorkbenchPartReference partRef) {
	}

	public void partVisible(IWorkbenchPartReference partRef) {
	}

	public boolean canDisplayPart() {
		return Login.isLoggedIn();
	}

	public void createPartContents(Composite parent) {
		selectionComposite = new LegalEntitySelectionComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
	}
	
	public String getQuickSearchText() {
		return selectionComposite.getQuickSearchText();
	}

	public LegalEntity getSelectedLegalEntity() {
		return selectionComposite.getSelectedLegalEntity();
	}
	public void setSelectedLegalEntityID(AnchorID legalEntityID) {
		selectionComposite.setSelectedLegalEntityID(legalEntityID);
	}

//	public void setSelectedLegalEntity(LegalEntity legalEntity) {
//		selectionComposite.setSelectedLegalEntity(legalEntity);
//	}
}
