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

package org.nightlabs.jfire.trade.ui.legalentity.config;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.jfire.base.ui.config.AbstractUserConfigModulePreferencePage;
import org.nightlabs.jfire.base.ui.config.IConfigModuleController;
import org.nightlabs.jfire.base.ui.prop.structedit.StructFieldNode;
import org.nightlabs.jfire.base.ui.prop.structedit.StructTreeComposite;
import org.nightlabs.jfire.organisation.Organisation;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.prop.StructField;
import org.nightlabs.jfire.prop.dao.StructLocalDAO;
import org.nightlabs.jfire.prop.id.StructLocalID;
import org.nightlabs.jfire.trade.config.LegalEntityViewConfigModule;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class LegalEntityViewConfigPreferencePage
extends AbstractUserConfigModulePreferencePage
{
	private XComposite wrapper;
	private LEViewPersonStructFieldTable structFieldTable;

	private XComposite buttonWrapper;
	private Button addButton;
	private Button removeButton;
	private Button upButton;
	private Button downButton;


	public LegalEntityViewConfigPreferencePage() {
		super();
	}

	/**
	 * @param title
	 */
	public LegalEntityViewConfigPreferencePage(String title) {
		super(title);
	}

	/**
	 * @param title
	 * @param image
	 */
	public LegalEntityViewConfigPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	/**
	 * @see org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage#createPreferencePage(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createPreferencePage(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		wrapper.getGridLayout().numColumns = 2;
		wrapper.getGridLayout().makeColumnsEqualWidth = false;

		structFieldTable = new LEViewPersonStructFieldTable(wrapper, SWT.NONE	);
		structFieldTable.setLayoutData(new GridData(GridData.FILL_BOTH));

		buttonWrapper = new XComposite(wrapper, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		buttonWrapper.setLayoutData(new GridData(GridData.FILL_VERTICAL));

		addButton = new Button(buttonWrapper, SWT.PUSH);
		addButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.legalentity.config.LegalEntityViewConfigPreferencePage.addButton.text")); //$NON-NLS-1$
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StructDialog dialog = new StructDialog(addButton.getShell());
				if (dialog.open() == Dialog.OK) {
					structFieldTable.addStructField(dialog.getSelectedStructField().getStructFieldIDObj().toString());
					structFieldTable.refresh();
					setConfigChanged(true);
				}
			}
		});

		removeButton = new Button(buttonWrapper, SWT.PUSH);
		removeButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.legalentity.config.LegalEntityViewConfigPreferencePage.removeButton.text")); //$NON-NLS-1$
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				structFieldTable.removeSelected();
				structFieldTable.refresh();
				setConfigChanged(true);
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		upButton = new Button(buttonWrapper, SWT.PUSH);
		upButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.legalentity.config.LegalEntityViewConfigPreferencePage.upButton.text")); //$NON-NLS-1$
		upButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		upButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				structFieldTable.moveSelectedUp();
				structFieldTable.refresh();
				setConfigChanged(true);
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

		});

		downButton = new Button(buttonWrapper, SWT.PUSH);
		downButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.legalentity.config.LegalEntityViewConfigPreferencePage.downButton.text")); //$NON-NLS-1$
		downButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		downButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				structFieldTable.moveSelectedDown();
				structFieldTable.refresh();
				setConfigChanged(true);
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

		});

		structFieldTable.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				updateButtonsEnabled();
			}
		});
		updateButtonsEnabled();
	}

	protected void updateButtonsEnabled() {
		removeButton.setEnabled(structFieldTable.getFirstSelectedElement() != null);
		upButton.setEnabled((!structFieldTable.isSelectedFirst()) && structFieldTable.getFirstSelectedElement() != null);
		downButton.setEnabled((!structFieldTable.isSelectedLast()) && structFieldTable.getFirstSelectedElement() != null);
	}

	@Override
	protected void updatePreferencePage() {
		if (!structFieldTable.isDisposed()) {
			structFieldTable.setInput(getConfigModuleController().getConfigModule());
		}
	}

	protected void discardPreferencePageWidgets() {
		wrapper = null;
		structFieldTable = null;

		buttonWrapper = null;
		addButton = null;
		removeButton = null;
		upButton = null;
		downButton = null;
	}

	@Override
	protected void setBodyContentEditable(boolean editable)
	{
		addButton.setEnabled(editable);
		if (editable)
		{
			updateButtonsEnabled();
		}
		else
		{
			removeButton.setEnabled(false);
			upButton.setEnabled(false);
			downButton.setEnabled(false);
		}
		structFieldTable.setEditable(editable);
	}

	@Override
	public void updateConfigModule()
	{
		LegalEntityViewConfigModule configModule = (LegalEntityViewConfigModule) getConfigModuleController().getConfigModule();
		configModule.getStructFields().clear();
		configModule.getStructFields().addAll(structFieldTable.getStructFields());
	}

	@Override
	protected IConfigModuleController createConfigModuleController() {
		return new LegalEntityViewConfigController(this);
	}

	class StructDialog extends ResizableTitleAreaDialog {

		private StructField<?> selectedStructField;
		private StructTreeComposite treeComposite;


		public StructDialog(Shell shell) {
			super(shell, null);
		}

		@Override
		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			newShell.setText(Messages.getString("org.nightlabs.jfire.trade.ui.legalentity.config.LegalEntityViewConfigPreferencePage.windows.title")); //$NON-NLS-1$
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			setTitle(Messages.getString("org.nightlabs.jfire.trade.ui.legalentity.config.LegalEntityViewConfigPreferencePage.title"));			 //$NON-NLS-1$
			treeComposite = new StructTreeComposite(parent, true, null);
			treeComposite.setInput(
					StructLocalDAO.sharedInstance().getStructLocal(
							StructLocalID.create(
									Organisation.DEV_ORGANISATION_ID,
									Person.class, Person.STRUCT_SCOPE, Person.STRUCT_LOCAL_SCOPE
							),
							new NullProgressMonitor()
					)
			);
			treeComposite.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(SelectionChangedEvent arg0) {
					StructFieldNode node = treeComposite.getStructFieldNode();
					selectedStructField = node != null ? node.getField() : null;
					setOKButtonEnabled(
							selectedStructField != null &&
							!(structFieldTable.getStructFields().contains(selectedStructField.getStructFieldIDObj().toString()))
						);
				}
			});
			return super.createDialogArea(parent);
		}

		@Override
		protected void createButtonsForButtonBar(Composite parent) {
			super.createButtonsForButtonBar(parent);
			setOKButtonEnabled(false);
		}

		protected void setOKButtonEnabled(boolean value) {
			getButton(IDialogConstants.OK_ID).setEnabled(value);
		}

		public StructField<?> getSelectedStructField() {
			return selectedStructField;
		}
	}
}
