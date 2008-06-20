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

import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;

/**
 * An editor page for issue tracking overview.
 * 
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class IssueEditorGeneralPage extends EntityEditorPageWithProgress
{
	/**
	 * The id of this page.
	 */
	public static final String ID_PAGE = IssueEditorGeneralPage.class.getName();

	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link IssueEditorGeneralPage}. 
	 */
	public static class Factory implements IEntityEditorPageFactory {

		public IFormPage createPage(FormEditor formEditor) {
			return new IssueEditorGeneralPage(formEditor);
		}
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new IssueEditorPageController(editor);
		}
	}

	// TODO: Somehow have a LanguageChooser for the whole page, not for every I18nEditor.
	
	private IssueLinkListSection issueLinkListSection;
	private IssueDetailSection issueDetailSection;
	private IssueTypeAndStateSection issueTypeAndStateSection;
	private IssueSubjectAndDescriptionSection issueSubjectAndDescriptionSection;
	private IssuePropertySection issuePropertySection;
	private IssueFileAttachmentSection issueFileAttachmentSection;
	private IssueCommentListSection issueCommentListSection;
	private IssueCommentCreateSection issueCommentCreateSection;
	
	/**
	 * <p>
	 * This constructor is used by the entity editor
	 * page extension system.
	 * 
	 * @param editor The editor for which to create this
	 * 		form page. 
	 */
	public IssueEditorGeneralPage(FormEditor editor)
	{
		super(editor, ID_PAGE, "General");
	}

	@Override
	protected void addSections(Composite parent) {
		final IssueEditorPageController controller = (IssueEditorPageController)getPageController();
		
		GridLayout layout = (GridLayout)parent.getLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		
		issueDetailSection = new IssueDetailSection(this, parent, controller);
		GridData gd = (GridData)issueDetailSection.getSection().getLayoutData();
		gd.verticalAlignment = GridData.BEGINNING;
		issueDetailSection.getSection().setLayoutData(gd);
		getManagedForm().addPart(issueDetailSection);
		
		issueTypeAndStateSection = new IssueTypeAndStateSection(this, parent, controller);
		gd = (GridData)issueTypeAndStateSection.getSection().getLayoutData();
		gd.verticalAlignment = GridData.BEGINNING;
		issueTypeAndStateSection.getSection().setLayoutData(gd);
		getManagedForm().addPart(issueTypeAndStateSection);
		
		issueSubjectAndDescriptionSection = new IssueSubjectAndDescriptionSection(this, parent, controller);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		issueSubjectAndDescriptionSection.getSection().setLayoutData(gridData);
		getManagedForm().addPart(issueSubjectAndDescriptionSection);
		
		issuePropertySection = new IssuePropertySection(this, parent, controller);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		issuePropertySection.getSection().setLayoutData(gridData);
		getManagedForm().addPart(issuePropertySection);
		
		issueCommentListSection = new IssueCommentListSection(this, parent, controller);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		issueCommentListSection.getSection().setLayoutData(gridData);
		getManagedForm().addPart(issueCommentListSection);
		
		issueCommentCreateSection = new IssueCommentCreateSection(this, parent, controller);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		issueCommentCreateSection.getSection().setLayoutData(gridData);
		getManagedForm().addPart(issueCommentCreateSection);
		
		issueLinkListSection = new IssueLinkListSection(this, parent, controller);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		issueLinkListSection.getSection().setLayoutData(gridData);
		getManagedForm().addPart(issueLinkListSection);
		
		issueFileAttachmentSection = new IssueFileAttachmentSection(this, parent, controller);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		issueFileAttachmentSection.getSection().setLayoutData(gridData);
		getManagedForm().addPart(issueFileAttachmentSection);
		
		if (controller.isLoaded()) {
			issueLinkListSection.setIssue(controller.getIssue());
			issueDetailSection.setIssue(controller.getIssue());
			issueTypeAndStateSection.setIssue(controller.getIssue());
			issueSubjectAndDescriptionSection.setIssue(controller.getIssue());
			issuePropertySection.setIssue(controller.getIssue());
			issueFileAttachmentSection.setIssue(controller.getIssue());
			issueCommentListSection.setIssue(controller.getIssue());
			issueCommentCreateSection.setIssue(controller.getIssue());
		}
	}

	@Override
	protected void asyncCallback() {
	}
	
	@Override
	protected void handleControllerObjectModified(
			EntityEditorPageControllerModifyEvent modifyEvent) {
		switchToContent(); // multiple calls don't hurt
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (issueLinkListSection != null && !issueLinkListSection.getSection().isDisposed()) {
					issueLinkListSection.setIssue(getController().getIssue());
					if (getController().getIssue().getIssueLinks().size() == 0) {
						issueLinkListSection.getSection().setExpanded(false);
					}
				}
				if (issueDetailSection != null && !issueDetailSection.getSection().isDisposed())
					issueDetailSection.setIssue(getController().getIssue());
				if (issueTypeAndStateSection != null && !issueTypeAndStateSection.getSection().isDisposed())
					issueTypeAndStateSection.setIssue(getController().getIssue());
				if (issueSubjectAndDescriptionSection != null && !issueSubjectAndDescriptionSection.getSection().isDisposed())
					issueSubjectAndDescriptionSection.setIssue(getController().getIssue());
				if (issuePropertySection != null && !issuePropertySection.getSection().isDisposed())
					issuePropertySection.setIssue(getController().getIssue());
				if (issueFileAttachmentSection != null && !issueFileAttachmentSection.getSection().isDisposed()) {
					issueFileAttachmentSection.setIssue(getController().getIssue());
					if (getController().getIssue().getIssueFileAttachments().size() == 0) {
						issueFileAttachmentSection.getSection().setExpanded(false);
					}
				}
				if (issueCommentListSection != null && !issueCommentListSection.getSection().isDisposed()) {
					issueCommentListSection.setIssue(getController().getIssue());
					if (getController().getIssue().getComments().size() == 0) {
						issueCommentListSection.getSection().setExpanded(false);
					}
				}
				if (issueCommentCreateSection != null && !issueCommentCreateSection.getSection().isDisposed())
					issueCommentCreateSection.setIssue(getController().getIssue());
			}
		});
	}

	@Override
	protected String getPageFormTitle() {
		return "Issue Details";
	}
	
	protected IssueEditorPageController getController() {
		return (IssueEditorPageController)getPageController();
	}

	public IssueLinkListSection getIssueLinkListSection() {
		return issueLinkListSection;
	}

	public IssueDetailSection getIssueDetailSection() {
		return issueDetailSection;
	}

	public IssueTypeAndStateSection getIssueTypeAndStateSection() {
		return issueTypeAndStateSection;
	}

	public IssueSubjectAndDescriptionSection getIssueSubjectAndDescriptionSection() {
		return issueSubjectAndDescriptionSection;
	}

	public IssuePropertySection getIssuePropertySection() {
		return issuePropertySection;
	}

	public IssueFileAttachmentSection getIssueFileAttachmentSection() {
		return issueFileAttachmentSection;
	}

	public IssueCommentListSection getIssueCommentListSection() {
		return issueCommentListSection;
	}

	public IssueCommentCreateSection getIssueCommentCreateSection() {
		return issueCommentCreateSection;
	}
}
