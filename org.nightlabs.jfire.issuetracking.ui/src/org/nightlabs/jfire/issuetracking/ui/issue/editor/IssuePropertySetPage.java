package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.BlockBasedEditorSection;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssuePropertySetPage
extends EntityEditorPageWithProgress
{
	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link EventDetailPage}.
	 */
	public static class Factory implements IEntityEditorPageFactory {
		public IFormPage createPage(FormEditor formEditor) {
			return new IssuePropertySetPage(formEditor);
		}

		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new IssueEditorPageController(editor);
		}
	}

	private IssueStructLocalScopeSection structLocalScopeSection = null;
	private BlockBasedEditorSection blockBasedEditorSection = null;


	/**
	 * @param editor
	 * @param id
	 * @param name
	 */
	public IssuePropertySetPage(FormEditor editor) {
		super(editor, IssuePropertySetPage.class.getName(), Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssuePropertySetPage.title")); //$NON-NLS-1$
	}

	public BlockBasedEditorSection getBlockBasedEditorSection() {
		return blockBasedEditorSection;
	}

	private int sectionStyle = ExpandableComposite.TITLE_BAR;

	@Override
	protected void addSections(Composite parent)
	{
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		toolkit.decorateFormHeading(getManagedForm().getForm().getForm());

		structLocalScopeSection = new IssueStructLocalScopeSection(this, parent, sectionStyle);
		getManagedForm().addPart(structLocalScopeSection);

		blockBasedEditorSection = new BlockBasedEditorSection(this, parent, sectionStyle, Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssuePropertySetPage.section.properties.text")); //$NON-NLS-1$
		getManagedForm().addPart(blockBasedEditorSection);
	}

	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
		final IssueEditorPageController controller = (IssueEditorPageController) getPageController();
		final Issue issue = controller.getIssue();
		Job job = new Job(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssuePropertySetPage.job.loadingData.text")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
//				final StructLocal structLocal = StructLocalDAO.sharedInstance().getStructLocal(
//						issue.getPropertySet().getStructLocalObjectID(),
////						Issue.class,
////						issue.getPropertySet().getStructScope(),
////						issue.getPropertySet().getStructLocalScope(),
//						monitor
//				);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed())
							return; // Do nothing if UI is disposed
						structLocalScopeSection.setIssue(issue);
						blockBasedEditorSection.setPropertySet(controller.getIssue().getPropertySet());
						switchToContent();
					}
				});
				return Status.OK_STATUS;
			}

		};
		job.schedule();
	}

	@Override
	protected String getPageFormTitle() {
		return Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssuePropertySetPage.pageFormTitle.text"); //$NON-NLS-1$
	}

}
