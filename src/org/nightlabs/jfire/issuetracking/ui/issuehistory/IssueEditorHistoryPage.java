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
package org.nightlabs.jfire.issuetracking.ui.issuehistory;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditorPageController;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueHistoryListSection;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

/**
 * An editor page for issue tracking overview.
 * 
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class IssueEditorHistoryPage extends EntityEditorPageWithProgress
{
	/**
	 * The id of this page.
	 */
	public static final String ID_PAGE = IssueEditorHistoryPage.class.getName();

	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link IssueEditorHistoryPage}. 
	 */
	public static class Factory implements IEntityEditorPageFactory {

		public IFormPage createPage(FormEditor formEditor) {
			return new IssueEditorHistoryPage(formEditor);
		}
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			IssueEditorPageController controller = editor.getController().getSinglePageController(IssueEditorPageController.class);
			if (controller != null)
				return controller;
			return new IssueEditorPageController(editor);
		}
	}

	// TODO: Somehow have a LanguageChooser for the whole page, not for every I18nEditor.
	
	private IssueHistoryListSection issueHistorySection;
	
	/**
	 * <p>
	 * This constructor is used by the entity editor
	 * page extension system.
	 * 
	 * @param editor The editor for which to create this
	 * 		form page. 
	 */
	public IssueEditorHistoryPage(FormEditor editor)
	{
		super(editor, ID_PAGE, Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuehistory.IssueEditorHistoryPage.title")); //$NON-NLS-1$
	}

	@Override
	protected void addSections(Composite parent) {
		final IssueEditorPageController controller = (IssueEditorPageController)getPageController();
		
		issueHistorySection = new IssueHistoryListSection(this, parent, controller);
		getManagedForm().addPart(issueHistorySection);
		
		if (controller.isLoaded()) {
			issueHistorySection.setIssue(controller.getIssue());
		}
	}

	@Override
	protected void handleControllerObjectModified(
			EntityEditorPageControllerModifyEvent modifyEvent) {
		switchToContent(); // multiple calls don't hurt
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (issueHistorySection != null && !issueHistorySection.getSection().isDisposed())
					issueHistorySection.setIssue(getController().getIssue());
			}
		});
	}

	@Override
	protected String getPageFormTitle() {
		return Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuehistory.IssueEditorHistoryPage.pageFormTitle.text"); //$NON-NLS-1$
	}
	
	protected IssueEditorPageController getController() {
		return (IssueEditorPageController)getPageController();
	}
}