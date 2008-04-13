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

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.part.ControllablePart;
import org.nightlabs.base.ui.part.PartVisibilityListener;
import org.nightlabs.base.ui.part.PartVisibilityTracker;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.login.part.LSDPartController;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.ProgressMonitor;

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
//	/**
//	 * List of direct listeners of the selection of the legal entity in this view
//	 * not over the Notification framework
//	 */
//	private ListenerList legalEntitySelectionListeners = new ListenerList();
	
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
		contributeToActionBars();
		setSelectedLegalEntityID(null);
	}
	
	public String getQuickSearchText() {
		return selectionComposite.getQuickSearchText();
	}

	public LegalEntity getSelectedLegalEntity() {
		return selectionComposite.getSelectedLegalEntity();
	}

//	public void addLegalEntitySelectionListener(ILegalEntitySelectionListener listener) {
//		legalEntitySelectionListeners.add(listener);
//	}
//	public void removeLegalEntitySelectionListener(ILegalEntitySelectionListener listener) {
//		legalEntitySelectionListeners.remove(listener);
//	}
//	private void notifyLegalEntitySelectionListeners(AnchorID legalEntityID) {
//		Object[] listeners = legalEntitySelectionListeners.getListeners();
//		for (Object listener : listeners) {
//			if (listener instanceof ILegalEntitySelectionListener) {
//				((ILegalEntitySelectionListener) listener).legalEntitySelected(legalEntityID);
//			}
//		}
//	}
	
	public void setSelectedLegalEntityID(final AnchorID legalEntityID) {
		selectionComposite.setSelectedLegalEntityID(legalEntityID);
		editLegalEntityAction.setEnabled(legalEntityID != null);
		if (legalEntityID != null) {
			Job job = new Job("Check LegalEntity") {
				@Override
				protected IStatus run(ProgressMonitor monitor) throws Exception {
					LegalEntity le = LegalEntityDAO.sharedInstance().getLegalEntity(legalEntityID, new String[] {FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
					editLegalEntityAction.setEnabled(!le.isAnonymous());
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
//		notifyLegalEntitySelectionListeners(legalEntityID);
	}

	private SelectAnonymousViewAction selectAnonymousAction = new SelectAnonymousViewAction();
	private EditLegalEntityViewAction editLegalEntityAction = new EditLegalEntityViewAction();
	private SearchLegalEntityViewAction searchLegalEntityAction = new SearchLegalEntityViewAction();
	
	private void contributeToActionBars() {
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		selectAnonymousAction.init(this);
		toolBarManager.add(selectAnonymousAction);
		editLegalEntityAction.init(this);
		toolBarManager.add(editLegalEntityAction);
		searchLegalEntityAction.init(this);
		toolBarManager.add(searchLegalEntityAction);		
	}
	
//	public void setSelectedLegalEntity(LegalEntity legalEntity) {
//		selectionComposite.setSelectedLegalEntity(legalEntity);
//	}
}
