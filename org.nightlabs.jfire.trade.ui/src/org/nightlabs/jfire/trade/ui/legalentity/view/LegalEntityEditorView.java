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
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.notification.NotificationAdapterSWTThreadAsync;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.login.ui.part.LSDViewPart;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class LegalEntityEditorView
extends LSDViewPart
{
	public static final String ID_VIEW = LegalEntityEditorView.class.getName();

	private LegalEntitySelectionComposite selectionComposite;
	private SelectAnonymousViewAction selectAnonymousAction = new SelectAnonymousViewAction();
	private EditLegalEntityViewAction editLegalEntityAction = new EditLegalEntityViewAction();
	private SearchLegalEntityViewAction searchLegalEntityAction = new SearchLegalEntityViewAction();

	@Override
	public void setFocus() {
	}

	public void createPartContents(Composite parent) {
		selectionComposite = new LegalEntitySelectionComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		selectionComposite.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
				toolBarManager.removeAll();
			}
		});
		contributeToActionBars();
		selectionComposite.setSearchAction(searchLegalEntityAction);

		SelectionManager.sharedInstance().addNotificationListener(LegalEntity.class, selectionListener);
		selectionComposite.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				SelectionManager.sharedInstance().removeNotificationListener(LegalEntity.class, selectionListener);
			}
		});

		setSelectedLegalEntityID(null, true);
	}

	private NotificationListener selectionListener = new NotificationAdapterSWTThreadAsync() {
		@Override
		public void notify(NotificationEvent notificationEvent) {
			AnchorID legalEntityID = (AnchorID) notificationEvent.getFirstSubject();
			setSelectedLegalEntityID(legalEntityID, false);
		}
	};

	public String getQuickSearchText() {
		return selectionComposite.getQuickSearchText();
	}

	public LegalEntity getSelectedLegalEntity() {
		return selectionComposite.getSelectedLegalEntity();
	}

	public void setSelectedLegalEntityID(final AnchorID legalEntityID, boolean isPropagateNotification)
	{
		if (selectionComposite == null || selectionComposite.isDisposed())
			return;

		selectionComposite.setSelectedLegalEntityID(legalEntityID, isPropagateNotification);
		editLegalEntityAction.setEnabled(legalEntityID != null);
		if (legalEntityID != null) {
			Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.legalentity.view.LegalEntityEditorView.job.checkLegalEntity")) { //$NON-NLS-1$
				@Override
				protected IStatus run(ProgressMonitor monitor) throws Exception {
					LegalEntity le = LegalEntityDAO.sharedInstance().getLegalEntity(legalEntityID, new String[] {FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
					editLegalEntityAction.setEnabled(!le.isAnonymous());
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
	}

	private void contributeToActionBars() {
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		selectAnonymousAction.init(this);
		toolBarManager.add(selectAnonymousAction);
		editLegalEntityAction.init(this);
		toolBarManager.add(editLegalEntityAction);
		searchLegalEntityAction.init(this);
		toolBarManager.add(searchLegalEntityAction);
	}

}
