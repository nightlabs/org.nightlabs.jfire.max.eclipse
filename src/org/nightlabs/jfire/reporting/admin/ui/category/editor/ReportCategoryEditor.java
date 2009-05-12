package org.nightlabs.jfire.reporting.admin.ui.category.editor;

import javax.jdo.FetchPlan;

import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditor;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.IReportRegistryItemEditorInput;
import org.nightlabs.jfire.reporting.dao.ReportRegistryItemDAO;
import org.nightlabs.jfire.reporting.layout.ReportCategory;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.progress.ProgressMonitor;

/**
 * Container for pages to edit {@link ReportCategory}.
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ReportCategoryEditor extends ActiveEntityEditor {

	public static final String ID_EDITOR = ReportCategoryEditor.class.getName();

	public ReportCategoryEditor() {
	}


	// :: --- [ ~~ ActiveEntiyEditor ] -------------------------------------------------------------------------->>---|
	@Override
	protected String getEditorTitleFromEntity(Object entity) {
		return entity instanceof ReportRegistryItem ? ((ReportRegistryItem)entity).getName().getText() : null;
	}

	@Override
	protected Object retrieveEntityForEditorTitle(ProgressMonitor monitor) {
		IReportRegistryItemEditorInput input = (IReportRegistryItemEditorInput) getEditorInput();
		assert input != null;
		return ReportRegistryItemDAO.sharedInstance().getReportRegistryItem(input.getReportRegistryItemID(), new String[] {FetchPlan.DEFAULT, ReportRegistryItem.FETCH_GROUP_NAME}, monitor);
	}
	// :: --- [ ~~ ActiveEntiyEditor ] --------------------------------------------------------------------------<<---|



//	@Override
//	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
//		super.init(site, input);
//		loadPartName(site.getShell().getDisplay());
//	}
//
//	private void loadPartName(final Display display) {
//		Job loadNameJob = new Job("Loading editor name") {
//			@Override
//			protected IStatus run(ProgressMonitor monitor) throws Exception {
//				monitor.beginTask("Loading editor name", 100);
//				IReportRegistryItemEditorInput input = (IReportRegistryItemEditorInput) getEditorInput();
//				if (input != null) {
//					final ReportRegistryItem item = ReportRegistryItemDAO.sharedInstance().getReportRegistryItem(
//							input.getReportRegistryItemID(),
//							new String[] {FetchPlan.DEFAULT, ReportRegistryItem.FETCH_GROUP_NAME}, new SubProgressMonitor(monitor, 80));
//
//					display.asyncExec(new Runnable() {
//						public void run() {
//							setPartName(item.getName().getText());
//						}
//					});
//				}
//				monitor.done();
//				return Status.OK_STATUS;
//			}
//		};
//		loadNameJob.schedule();
//	}
//
//	@Override
//	public void doSave(IProgressMonitor monitor) {
//		super.doSave(monitor);
////		loadPartName(getSite().getShell().getDisplay());
//		// can't reload name as the value is stored asynchronously in a job.
//	}
}
