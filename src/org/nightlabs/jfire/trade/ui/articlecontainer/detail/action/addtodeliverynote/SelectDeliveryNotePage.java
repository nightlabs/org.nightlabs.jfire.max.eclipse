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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.addtodeliverynote;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageDimension;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.store.StoreManager;
import org.nightlabs.jfire.store.StoreManagerUtil;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.DateFormatter;

public class SelectDeliveryNotePage extends DynamicPathWizardPage
{
	public static final int ACTION_CREATE = 1;
	public static final int ACTION_SELECT = 2;

	private Button createDeliveryNoteRadio;
	private Button selectDeliveryNoteRadio;

	private AbstractTableComposite deliveryNoteTable;
	private List deliveryNotes = new ArrayList(0);
	private DeliveryNote selectedDeliveryNote = null;

	public SelectDeliveryNotePage()
	{
		super(SelectDeliveryNotePage.class.getName(), Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.addtodeliverynote.SelectDeliveryNotePage.title")); //$NON-NLS-1$
		setImageDescriptor(SharedImages.getSharedImageDescriptor(TradePlugin.getDefault(), 
				SelectDeliveryNotePage.class, "", ImageDimension._75x70, ImageFormat.png)); //$NON-NLS-1$
	}

	public Control createPageContents(Composite parent)
	{
		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.ORDINARY_WRAPPER);

		createDeliveryNoteRadio = new Button(page, SWT.RADIO);
		createDeliveryNoteRadio.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.addtodeliverynote.SelectDeliveryNotePage.createDeliveryNoteRadio.text")); //$NON-NLS-1$
		createDeliveryNoteRadio.setSelection(true);
		selectDeliveryNoteRadio = new Button(page, SWT.RADIO);
		selectDeliveryNoteRadio.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.addtodeliverynote.SelectDeliveryNotePage.selectDeliveryNoteRadio.text")); //$NON-NLS-1$
		selectDeliveryNoteRadio.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				deliveryNoteTable.setEnabled(selectDeliveryNoteRadio.getSelection());
				((DynamicPathWizard)getWizard()).updateDialog();
			}
		});

		deliveryNoteTable = new AbstractTableComposite(page, SWT.NONE, true, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE) {
			protected void createTableColumns(TableViewer tableViewer, Table table)
			{
				new TableColumn(table, SWT.RIGHT).setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.addtodeliverynote.SelectDeliveryNotePage.deliveryNoteIDTableColumn.text")); //$NON-NLS-1$
				new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.addtodeliverynote.SelectDeliveryNotePage.createDateTableColumn.text")); //$NON-NLS-1$
				new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.addtodeliverynote.SelectDeliveryNotePage.createUserTableColumn.text")); //$NON-NLS-1$
				table.setLayout(new WeightedTableLayout(new int[] {33, 33, 33}));
			}
			protected void setTableProvider(TableViewer tableViewer)
			{
				tableViewer.setContentProvider(new TableContentProvider());
				tableViewer.setLabelProvider(new TableLabelProvider() {
					public String getColumnText(Object element, int columnIndex)
					{
						if (element instanceof String) {
							if (columnIndex == 0)
								return (String)element;
							else
								return ""; //$NON-NLS-1$
						}

						DeliveryNote deliveryNote = (DeliveryNote) element;
						switch (columnIndex) {
							case 0:
								return deliveryNote.getDeliveryNoteIDPrefix() + '/' + ObjectIDUtil.longObjectIDFieldToString(deliveryNote.getDeliveryNoteID());
							case 1:
								return DateFormatter.formatDateShortTimeHMS(deliveryNote.getCreateDT(), true);
							case 2:
								return deliveryNote.getCreateUser().getName() + " (" + deliveryNote.getCreateUser().getLogin() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
							default:
								return ""; //$NON-NLS-1$
						}
					}
				});
			}
		};
		deliveryNoteTable.getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				if (sel.isEmpty())
					selectedDeliveryNote = null;
				else {
					Object selectedObject = sel.getFirstElement();
					if (selectedObject instanceof DeliveryNote)
						selectedDeliveryNote = (DeliveryNote) selectedObject;
					else
						selectedDeliveryNote = null;
				}
				((DynamicPathWizard)getWizard()).updateDialog();
			}
		});
		deliveryNotes.add(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.addtodeliverynote.SelectDeliveryNotePage.loadDeliveryNotesPseudoEntry")); //$NON-NLS-1$
		deliveryNoteTable.setInput(deliveryNotes);
		deliveryNoteTable.setEnabled(selectDeliveryNoteRadio.getSelection());

		new Job(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.addtodeliverynote.SelectDeliveryNotePage.loadDeliveryNotesJob.name")) { //$NON-NLS-1$
			protected IStatus run(IProgressMonitor monitor)
			{
				loadDataInJob();
				return Status.OK_STATUS;
			}
		}.schedule();

		return page;
	}

	protected AddToDeliveryNoteWizard getAddToDeliveryNoteWizard()
	{
		return (AddToDeliveryNoteWizard) getWizard();
	}

	private void loadDataInJob()
	{
		try {
			StoreManager storeManager = StoreManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			final List l = storeManager.getNonFinalizedDeliveryNotes(
					getAddToDeliveryNoteWizard().getVendorID(),
					getAddToDeliveryNoteWizard().getCustomerID(),
					FETCH_GROUPS_DELIVERY_NOTES, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

			Display.getDefault().asyncExec(new Runnable() {
				public void run()
				{
					deliveryNotes.clear();
					deliveryNotes.addAll(l);
					deliveryNoteTable.refresh();
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static final String[] FETCH_GROUPS_DELIVERY_NOTES = new String[] {
		FetchPlan.DEFAULT,
		DeliveryNote.FETCH_GROUP_CREATE_USER
	};

	public int getAction()
	{
		int action = 0;

		if (createDeliveryNoteRadio.getSelection())
			action = ACTION_CREATE;
		else if (selectDeliveryNoteRadio.getSelection())
			action = ACTION_SELECT;

		if (action == 0)
			throw new IllegalStateException("action == 0!!!"); //$NON-NLS-1$

		return action;
	}

	public DeliveryNote getSelectedDeliveryNote()
	{
		return selectedDeliveryNote;
	}

	public DeliveryNoteID getSelectedDeliveryNoteID()
	{
		if (selectedDeliveryNote == null)
			return null;

		return (DeliveryNoteID) JDOHelper.getObjectId(selectedDeliveryNote);
	}

	public boolean isPageComplete()
	{
		return ACTION_CREATE == getAction() || (ACTION_SELECT == getAction() && getSelectedDeliveryNote() != null);
	}
}
