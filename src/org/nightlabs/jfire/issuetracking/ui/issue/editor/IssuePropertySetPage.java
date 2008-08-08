package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.BlockBasedEditorSection;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.prop.StructLocal;
import org.nightlabs.jfire.prop.dao.StructLocalDAO;
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
		super(editor, IssuePropertySetPage.class.getName(), "Properties");
	}

	public BlockBasedEditorSection getBlockBasedEditorSection() {
		return blockBasedEditorSection;
	}
	
	private int sectionStyle = ExpandableComposite.TITLE_BAR;
	
	@Override
	protected void addSections(Composite parent)
	{
		structLocalScopeSection = new IssueStructLocalScopeSection(this, parent, sectionStyle);
		getManagedForm().addPart(structLocalScopeSection);
		
		blockBasedEditorSection = new BlockBasedEditorSection(this, parent, sectionStyle, "Properties");
		getManagedForm().addPart(blockBasedEditorSection);
	}

	@Override
	protected void asyncCallback()
	{
		final IssueEditorPageController controller = (IssueEditorPageController) getPageController();
		final Issue issue = controller.getIssue();
		Job job = new Job("Loading....") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				final StructLocal structLocal = StructLocalDAO.sharedInstance().getStructLocal(
						Issue.class,
						issue.getPropertySet().getStructScope(),
						issue.getPropertySet().getStructLocalScope(),
						monitor
				);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed())
							return; // Do nothing if UI is disposed
						structLocalScopeSection.setIssue(issue);
						blockBasedEditorSection.setPropertySet(controller.getIssue().getPropertySet(), structLocal);
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
		return "Properties";
	}

}
