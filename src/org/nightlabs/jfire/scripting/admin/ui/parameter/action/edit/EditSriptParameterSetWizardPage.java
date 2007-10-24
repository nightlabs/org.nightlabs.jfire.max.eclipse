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
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.scripting.admin.ui.parameter.action.edit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.language.I18nTextEditorWizardPage;
import org.nightlabs.jfire.scripting.ScriptParameterSet;
import org.nightlabs.jfire.scripting.admin.ui.resource.Messages;
import org.nightlabs.jfire.scripting.ui.ScriptParameterTable;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class EditSriptParameterSetWizardPage extends I18nTextEditorWizardPage {

	private XComposite paramWrapper;
	private ScriptParameterTable parameterTable;	
	private XComposite buttonsWrapper;
	private ScriptParameterSet parameterSet;
	private Button newParamButton;
	private Button delParamButton;
	
	
	/**
	 * @param pageName
	 * @param editorCaption
	 */
	public EditSriptParameterSetWizardPage(ScriptParameterSet parameterSet) {
		super(EditSriptParameterSetWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.scripting.admin.ui.parameter.action.edit.EditSriptParameterSetWizardPage.title"), Messages.getString("org.nightlabs.jfire.scripting.admin.ui.parameter.action.edit.EditSriptParameterSetWizardPage.editorCaption")); //$NON-NLS-1$ //$NON-NLS-2$
		this.parameterSet = parameterSet;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.nightlabs.base.ui.language.I18nTextEditorWizardPage#createAdditionalContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createAdditionalContents(Composite wrapper) {
		paramWrapper = new XComposite(wrapper, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		paramWrapper.getGridLayout().numColumns = 2;
		parameterTable = new ScriptParameterTable(paramWrapper, SWT.BORDER);
		if (parameterSet != null)
			parameterTable.setInput(parameterSet);
		buttonsWrapper = new XComposite(paramWrapper, SWT.NONE, LayoutMode.ORDINARY_WRAPPER);
		newParamButton = new Button(buttonsWrapper, SWT.PUSH);
		newParamButton.setText(Messages.getString("org.nightlabs.jfire.scripting.admin.ui.parameter.action.edit.EditSriptParameterSetWizardPage.newParamButton.text")); //$NON-NLS-1$
		newParamButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		delParamButton = new Button(buttonsWrapper, SWT.PUSH);
		delParamButton.setText(Messages.getString("org.nightlabs.jfire.scripting.admin.ui.parameter.action.edit.EditSriptParameterSetWizardPage.delParamButton.text")); //$NON-NLS-1$
		delParamButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		// TODO: implement
		return super.isPageComplete();
	}
	
}
