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
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

/**
 * An editor page for issue tracking overview.
 *
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class IssueEditorGeneralPage
extends EntityEditorPageWithProgress
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

	//Sections (in order)
	private IssueDetailSection issueDetailSection;
	private IssueTypeAndStateSection issueTypeAndStateSection;
	private IssueSubjectAndDescriptionSection issueSubjectAndDescriptionSection;
	private IssuePropertySection issuePropertySection;
	private IssueCommentListSection issueCommentListSection;
	private IssueCommentCreateSection issueCommentCreateSection;
	private IssueLinkListSection issueLinkListSection;
	private IssueFileAttachmentSection issueFileAttachmentSection;
	private IssueWorkTimeSection issueWorkTimeSection;

	// --[ In preparation for an IssueMarker ]--
	private IssueMarkerSection issueMarkerSection;
	// --[ In preparation for an IssueMarker ]--

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
		super(editor, ID_PAGE, Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditorGeneralPage.title")); //$NON-NLS-1$
	}

	private ScrolledComposite sc;
	@Override
	protected void addSections(Composite parent) {
		final IssueEditorPageController controller = (IssueEditorPageController)getPageController();

		sc = new ScrolledComposite(parent, SWT.H_SCROLL |
				  SWT.V_SCROLL);
		sc.setLayoutData(new GridData(GridData.FILL_BOTH));

		final XComposite c = new XComposite(sc, SWT.NONE);
		GridLayout layout = (GridLayout)c.getLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;

		sc.setContent(c);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		toolkit.decorateFormHeading(getManagedForm().getForm().getForm());

		issueDetailSection = new IssueDetailSection(this, c, controller);
		GridData gd = (GridData)issueDetailSection.getSection().getLayoutData();
		gd.verticalAlignment = GridData.BEGINNING;
		issueDetailSection.getSection().setLayoutData(gd);
		getManagedForm().addPart(issueDetailSection);

		issueTypeAndStateSection = new IssueTypeAndStateSection(this, c, controller);
		gd = (GridData)issueTypeAndStateSection.getSection().getLayoutData();
		gd.verticalAlignment = GridData.BEGINNING;
		issueTypeAndStateSection.getSection().setLayoutData(gd);
		getManagedForm().addPart(issueTypeAndStateSection);

		issueTypeAndStateSection.getSection().descriptionVerticalSpacing = issueDetailSection.getSection().getTextClientHeightDifference();

		issueSubjectAndDescriptionSection = new IssueSubjectAndDescriptionSection(this, c, controller);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		issueSubjectAndDescriptionSection.getSection().setLayoutData(gridData);
		getManagedForm().addPart(issueSubjectAndDescriptionSection);

		// --[ In preparation for an IssueMarker ]--
		issueMarkerSection = new IssueMarkerSection(this, c, controller);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		issueMarkerSection.getSection().setLayoutData(gridData);
		getManagedForm().addPart(issueMarkerSection);
		// --[ In preparation for an IssueMarker ]--

		issuePropertySection = new IssuePropertySection(this, c, controller);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		issuePropertySection.getSection().setLayoutData(gridData);
		getManagedForm().addPart(issuePropertySection);

		issueCommentListSection = new IssueCommentListSection(this, c, controller);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		issueCommentListSection.getSection().setLayoutData(gridData);
		getManagedForm().addPart(issueCommentListSection);

		issueCommentCreateSection = new IssueCommentCreateSection(this, c, controller);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		issueCommentCreateSection.getSection().setLayoutData(gridData);
		getManagedForm().addPart(issueCommentCreateSection);

		issueLinkListSection = new IssueLinkListSection(this, c, controller);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		issueLinkListSection.getSection().setLayoutData(gridData);
		getManagedForm().addPart(issueLinkListSection);

		issueFileAttachmentSection = new IssueFileAttachmentSection(this, c, controller);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.verticalAlignment = GridData.BEGINNING;
//		gridData.horizontalSpan = 2;
		issueFileAttachmentSection.getSection().setLayoutData(gridData);
		getManagedForm().addPart(issueFileAttachmentSection);

		issueWorkTimeSection = new IssueWorkTimeSection(this, c, controller);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.verticalAlignment = GridData.BEGINNING;
//		gridData.horizontalSpan = 2;
		issueWorkTimeSection.getSection().setLayoutData(gridData);
		getManagedForm().addPart(issueWorkTimeSection);

		if (controller.isLoaded()) {
			Issue issue = controller.getIssue();

			issueLinkListSection.setIssue(issue);
			issueDetailSection.setIssue(issue);
			issueTypeAndStateSection.setIssue(issue);
			issueSubjectAndDescriptionSection.setIssue(issue);
			issuePropertySection.setIssue(issue);
			issueFileAttachmentSection.setIssue(issue);
			issueCommentListSection.setIssue(issue);
			issueCommentCreateSection.setIssue(issue);
			issueWorkTimeSection.setIssue(issue);

			// --[ In preparation for an IssueMarker ]--
			issueMarkerSection.setIssue(issue);
			// --[ In preparation for an IssueMarker ]--
		}
	}

	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
		switchToContent(); // multiple calls don't hurt
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				Issue issue = getController().getIssue();

				if (issueLinkListSection != null && !issueLinkListSection.getSection().isDisposed()) {
					issueLinkListSection.setIssue(issue);
					issueLinkListSection.getSection().setExpanded(issue.getIssueLinks().size() != 0);
				}

				if (issueDetailSection != null && !issueDetailSection.getSection().isDisposed())
					issueDetailSection.setIssue(issue);

				if (issueTypeAndStateSection != null && !issueTypeAndStateSection.getSection().isDisposed())
					issueTypeAndStateSection.setIssue(issue);

				if (issueSubjectAndDescriptionSection != null && !issueSubjectAndDescriptionSection.getSection().isDisposed())
					issueSubjectAndDescriptionSection.setIssue(issue);

				if (issuePropertySection != null && !issuePropertySection.getSection().isDisposed())
					issuePropertySection.setIssue(issue);

				if (issueCommentListSection != null && !issueCommentListSection.getSection().isDisposed()) {
					issueCommentListSection.setIssue(issue);
					issueCommentListSection.getSection().setExpanded(issue.getComments().size() != 0);
				}

				if (issueCommentCreateSection != null && !issueCommentCreateSection.getSection().isDisposed())
					issueCommentCreateSection.setIssue(issue);

				if (issueWorkTimeSection != null && !issueWorkTimeSection.getSection().isDisposed())
					issueWorkTimeSection.setIssue(issue);


				// --- 8< --- KaiExperiments: since 15.05.2009 ------------------
				if (issueMarkerSection != null && !issueMarkerSection.getSection().isDisposed())
					issueMarkerSection.setIssue(issue);

				if (issueFileAttachmentSection != null && !issueFileAttachmentSection.getSection().isDisposed())
					issueFileAttachmentSection.setIssue(issue);
				// ------ KaiExperiments ----- >8 -------------------------------

			}
		});
	}

	@Override
	protected String getPageFormTitle() {
		return Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditorGeneralPage.pageFormtitle.text"); //$NON-NLS-1$
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

	public IssueWorkTimeSection getIssueWorkTimeSection() {
		return issueWorkTimeSection;
	}

	@Override
	protected boolean includeFixForVerticalScrolling() {
		return false;
	}
}
