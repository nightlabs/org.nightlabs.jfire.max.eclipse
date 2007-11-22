/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2006 NightLabs - http://NightLabs.org                    *
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
 ******************************************************************************/
package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueNewWizard;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueTable;

/**
 * An editor page for issue tracking list.
 * 
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class IssueListPage extends EntityEditorPageWithProgress
{
	/**
	 * The id of this page.
	 */
	public static final String ID_PAGE = IssueListPage.class.getName();

	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link IssueListPage}. 
	 */
	public static class Factory implements IEntityEditorPageFactory {

		public IFormPage createPage(FormEditor formEditor) {
			return new IssueListPage(formEditor);
		}
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new IssuePageController(editor);
		}
	}

	/**
	 * <p>
	 * This constructor is used by the entity editor
	 * page extension system.
	 * 
	 * @param editor The editor for which to create this
	 * 		form page. 
	 */
	public IssueListPage(FormEditor editor)
	{
		super(editor, ID_PAGE, "Issue List Page Title");
	}

	@Override
	protected void addSections(Composite parent) {
		final IssuePageController controller = (IssuePageController)getPageController();
		
		Button createButton = new Button(parent, SWT.PUSH);
		createButton.setText("New Issue");
		createButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				IssueNewWizard wizard = new IssueNewWizard(null);
				//Instantiates the wizard container with the wizard and opens it
				DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
				dialog.open();
			}
		});
		
		XComposite tableComposite = new XComposite(parent, SWT.NONE);
		tableComposite.getGridLayout().numColumns = 2;
		
		Label l1 = new Label(tableComposite, SWT.NONE);
		l1.setAlignment(SWT.CENTER);
		l1.setText("Most recently issues");
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		l1.setLayoutData(gridData);
		
		Label l2 = new Label(tableComposite, SWT.NONE);
		l2.setAlignment(SWT.CENTER);
		l2.setText("Summary");
		l2.setLayoutData(gridData);
		
		IssueTable issueTable = new IssueTable(tableComposite, SWT.NONE);
		gridData = new GridData(GridData.FILL_BOTH);
		issueTable.setLayoutData(gridData);
		
		IssueTable issueTable2 = new IssueTable(tableComposite, SWT.NONE);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.verticalSpan = 3;
		gridData.verticalAlignment = GridData.FILL;
		issueTable2.setLayoutData(gridData);
		
		Label l3 = new Label(tableComposite, SWT.NONE);
		l3.setAlignment(SWT.CENTER);
		l3.setText("Resolved issues");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		l3.setLayoutData(gridData);
		
		IssueTable issueTable3 = new IssueTable(tableComposite, SWT.NONE);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.verticalSpan = 1;
		issueTable3.setLayoutData(gridData);
	}

	@Override
	protected void asyncCallback() {
	}
	
	@Override
	protected void handleControllerObjectModified(
			EntityEditorPageControllerModifyEvent modifyEvent) {
		switchToContent(); // multiple calls don't hurt
	}

	@Override
	protected String getPageFormTitle() {
		return "Issue List Page Form Title";
	}
}
