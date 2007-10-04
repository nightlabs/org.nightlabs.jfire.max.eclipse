package org.nightlabs.jfire.simpletrade.admin.editor;

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
import org.nightlabs.jfire.prop.StructLocal;
import org.nightlabs.jfire.prop.dao.StructLocalDAO;
import org.nightlabs.jfire.simpletrade.admin.resource.Messages;
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SimpleProductTypePropertySetPage 
extends EntityEditorPageWithProgress 
{
	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link EventDetailPage}. 
	 */
	public static class Factory implements IEntityEditorPageFactory {
		public IFormPage createPage(FormEditor formEditor) {
			return new SimpleProductTypePropertySetPage(formEditor);
		}

		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new SimpleProductTypePropertySetPageController(editor);
		}
	}
	
	private SimpleProductTypeStructLocalScopeSection structLocalScopeSection = null;
	private BlockBasedEditorSection blockBaseEditorSection = null;
	
	
	/**
	 * @param editor
	 * @param id
	 * @param name
	 */
	public SimpleProductTypePropertySetPage(FormEditor editor) {
		super(editor, SimpleProductTypePropertySetPage.class.getName(), Messages.getString("org.nightlabs.jfire.simpletrade.admin.editor.SimpleProductTypePropertySetPage.title"));  //$NON-NLS-1$
	}

	public BlockBasedEditorSection getBlockBaseEditorSection() {
		return blockBaseEditorSection;
	}
	
	private int sectionStyle = ExpandableComposite.TITLE_BAR;
	
	@Override
	protected void addSections(Composite parent) 
	{
		structLocalScopeSection = new SimpleProductTypeStructLocalScopeSection(this, parent, sectionStyle); 
		getManagedForm().addPart(structLocalScopeSection);
		
		blockBaseEditorSection = new BlockBasedEditorSection(this, parent, sectionStyle, Messages.getString("org.nightlabs.jfire.simpletrade.admin.editor.SimpleProductTypePropertySetPage.blockBaseEditorSection.title"));  //$NON-NLS-1$
		getManagedForm().addPart(blockBaseEditorSection);	
	}

	@Override
	protected void asyncCallback() 
	{
		final SimpleProductTypePropertySetPageController controller = (SimpleProductTypePropertySetPageController) getPageController();
		final SimpleProductType simpleProductType = (SimpleProductType) controller.getProductType();
		Job job = new Job(Messages.getString("org.nightlabs.jfire.simpletrade.admin.editor.SimpleProductTypePropertySetPage.loadStructLocalJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				final StructLocal structLocal = StructLocalDAO.sharedInstance().getStructLocal(
						SimpleProductType.class, simpleProductType.getStructLocalScope(), monitor);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						structLocalScopeSection.setSimpleProductType(simpleProductType);
						blockBaseEditorSection.setProperty(controller.getPropertySet(), structLocal);
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
		return Messages.getString("org.nightlabs.jfire.simpletrade.admin.editor.SimpleProductTypePropertySetPage.pageFormTitle"); //$NON-NLS-1$
	}

}
