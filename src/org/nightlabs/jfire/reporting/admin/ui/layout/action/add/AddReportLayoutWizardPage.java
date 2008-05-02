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

package org.nightlabs.jfire.reporting.admin.ui.layout.action.add;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.nightlabs.base.ui.composite.FileSelectionComposite;
import org.nightlabs.base.ui.composite.LabeledText;
import org.nightlabs.base.ui.language.I18nTextEditorWizardPage;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.reporting.admin.ui.resource.Messages;
import org.nightlabs.util.NLLocale;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class AddReportLayoutWizardPage extends I18nTextEditorWizardPage {

	
	private LabeledText registryItemID;
	private Group choice;
	private Button reportFromFile;
	private Button reportFromTemplate;
	private FileSelectionComposite fileSelectComposite;
	
	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent arg0) {
			String editLang = getTextEditor().getLanguageChooser().getLanguage().getLanguageID();
			if (editLang.equals(NLLocale.getDefault().getLanguage()))
				registryItemID.getTextControl().setText(ObjectIDUtil.makeValidIDString(getTextEditor().getEditText()));
			getWizard().getContainer().updateButtons();
		}
	};
	
	/**
	 * @param pageName
	 * @param editorCaption
	 */
	public AddReportLayoutWizardPage() {
		super(AddReportLayoutWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.action.add.AddReportLayoutWizardPage.title"), Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.action.add.AddReportLayoutWizardPage.editorCaption")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/*
	 *  (non-Javadoc)
	 * @see org.nightlabs.base.ui.language.I18nTextEditorWizardPage#createAdditionalContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createAdditionalContents(Composite wrapper) {
		registryItemID = new LabeledText(wrapper, Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.action.add.AddReportLayoutWizardPage.registryItemIDLabel.text")); //$NON-NLS-1$
		choice = new Group(wrapper, SWT.NONE);
		choice.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.action.add.AddReportLayoutWizardPage.creationStyleGroup")); //$NON-NLS-1$
		choice.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		choice.setLayout(new GridLayout());
		reportFromFile = new Button(choice, SWT.RADIO);
		reportFromFile.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.action.add.AddReportLayoutWizardPage.reportFromFileButton.text")); //$NON-NLS-1$
		reportFromFile.setLayoutData(new GridData());
		reportFromTemplate = new Button(choice, SWT.RADIO);
		reportFromTemplate.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.action.add.AddReportLayoutWizardPage.reportFromTemplateButton.text")); //$NON-NLS-1$
		reportFromTemplate.setLayoutData(new GridData());
		SelectionListener listener = new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				getContainer().updateButtons();
			}
		};
		reportFromFile.addSelectionListener(listener);
		reportFromTemplate.addSelectionListener(listener);
		reportFromFile.setSelection(true);
		fileSelectComposite = new FileSelectionComposite(wrapper, SWT.NONE,
				FileSelectionComposite.OPEN_FILE,
				Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.action.add.AddReportLayoutWizardPage.fileSelectComposite.caption"),  //$NON-NLS-1$
				null)
		{
			@Override
			protected void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
			}
		};
		getTextEditor().addModifyListener(modifyListener);
	}
	
	public FileSelectionComposite getFileSelectComposite() {
		return fileSelectComposite;
	}

	public String getRegistryItemID() {
		return registryItemID.getTextControl().getText();
	}
	
	@Override
	public boolean isPageComplete() {
		boolean nameDefined = !getI18nText().isEmpty();
		if (!nameDefined)
			return false;
		if (reportFromTemplate.getSelection())
			return true;
		return fileSelectComposite.getFileText() != null && !"".equals(fileSelectComposite.getFileText());  //$NON-NLS-1$
	}
	
	public boolean isCreateFromFile() {
		return reportFromFile.getSelection();
	}
	
	@Override
	public IWizardPage getNextPage() {
		if (reportFromFile.getSelection())
			return null;
		return super.getNextPage();
	}
}
