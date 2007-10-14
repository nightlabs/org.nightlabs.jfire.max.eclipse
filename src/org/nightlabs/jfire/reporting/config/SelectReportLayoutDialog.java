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

package org.nightlabs.jfire.reporting.config;

import java.util.Collection;

import javax.jdo.FetchPlan;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.dialog.CenteredDialog;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.reporting.layout.ReportLayout;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItemTable;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.resource.Messages;

/**
 * A dialog that lets the use choose a {@link ReportLayout} 
 * from a Table of {@link ReportRegistryItem}s.
 * The dialog can be instantiated with a list of items the user
 * should choose from and one default item that will be pre-selected.  
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class SelectReportLayoutDialog extends CenteredDialog {

	private XComposite wrapper;
	private SashForm sashForm;
	private ReportRegistryItemTable itemTable;
	private ReportRegistryItem selectedRegistryItem;
	private Collection<ReportRegistryItemID> availableItemIDs;
	private ReportRegistryItemID defaultItemID;
	private Text descriptionText;
	
	/**
	 * Create a new {@link SelectReportLayoutDialog}.
	 * 
	 * @param parentShell The parent shell
	 * @param availableItemIDs The list of items the user should choose from.
	 * @param defaultItemID The item that will be pre-selected (can be <code>null</code>).
	 */
	public SelectReportLayoutDialog(
			Shell parentShell,
			Collection<ReportRegistryItemID> availableItemIDs,
			ReportRegistryItemID defaultItemID
	) {
		super(parentShell);
		this.availableItemIDs = availableItemIDs;
		this.defaultItemID = defaultItemID;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Sets default size.
	 * </p>
	 * @see org.nightlabs.base.ui.dialog.CenteredDialog#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.getString("org.nightlabs.jfire.reporting.config.SelectReportLayoutDialog.newShell.text")); //$NON-NLS-1$
		setToCenteredLocationPreferredSize(newShell, 300, 400);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE);
		sashForm = new SashForm(wrapper, SWT.NONE | SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		itemTable = new ReportRegistryItemTable(sashForm, SWT.NONE);
		itemTable.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				Button okButton = getButton(IDialogConstants.OK_ID);
				selectedRegistryItem = itemTable.getFirstSelectedElement();
				okButton.setEnabled(selectedRegistryItem != null && selectedRegistryItem instanceof ReportLayout);
			}
		});
		itemTable.getTableViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				selectedRegistryItem = itemTable.getFirstSelectedElement();
				if (selectedRegistryItem != null && selectedRegistryItem instanceof ReportLayout) {
					close();
				}
			}
		});
		itemTable.setReportRegistryItemIDs(
				availableItemIDs, defaultItemID,
				new String[] {
						FetchPlan.DEFAULT, 
						ReportRegistryItem.FETCH_GROUP_NAME, ReportRegistryItem.FETCH_GROUP_DESCRIPTION
					}
			);
		descriptionText = new Text(sashForm, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
		descriptionText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		sashForm.setWeights(new int[] {1, 1});
		itemTable.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ReportRegistryItem item = itemTable.getFirstSelectedElement();
				if (item != null) {
					if (item.getDescription().isEmpty()) {
						descriptionText.setText(Messages.getString("org.nightlabs.jfire.reporting.config.SelectReportLayoutDialog.descriptionText.text")); //$NON-NLS-1$
					} else {
						descriptionText.setText(item.getDescription().getText());
					}
				}
			}
		});
		return wrapper;
	}
	
	/**
	 * Open a {@link SelectReportLayoutDialog} and let the user choose a ReportLayout.
	 * 
	 * @param availableItemIDs The list of items the user should choose from.
	 * @param defaultItemID The item that will be pre-selected (can be <code>null</code>)
	 * @return The {@link ReportRegistryItem} the user selected or <code>null</code>,
	 * 		if the user canceled the dialog.
	 */
	public static ReportRegistryItem openDialog(
			Collection<ReportRegistryItemID> availableItemIDs,
			ReportRegistryItemID defaultItemID
	) {
		SelectReportLayoutDialog dlg = new SelectReportLayoutDialog(
				RCPUtil.getActiveWorkbenchShell(),
				availableItemIDs, defaultItemID
			);
		if (dlg.open() == Window.OK) {
			return dlg.selectedRegistryItem;
		}
		return null;
	}
	
}
