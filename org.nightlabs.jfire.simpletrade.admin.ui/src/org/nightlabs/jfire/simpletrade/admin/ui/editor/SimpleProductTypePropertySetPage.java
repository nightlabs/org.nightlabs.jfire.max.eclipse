package org.nightlabs.jfire.simpletrade.admin.ui.editor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.StructLocal;
import org.nightlabs.jfire.prop.dao.StructLocalDAO;
import org.nightlabs.jfire.simpletrade.admin.ui.resource.Messages;
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
//	private BlockBasedEditorSection propertiesSection = null;
	private SimpleProductTypePropertiesSection propertiesSection = null;


	/**
	 * @param editor
	 * @param id
	 * @param name
	 */
	public SimpleProductTypePropertySetPage(FormEditor editor) {
		super(editor, SimpleProductTypePropertySetPage.class.getName(), Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.editor.SimpleProductTypePropertySetPage.title"));  //$NON-NLS-1$
	}

//	public BlockBasedEditorSection getBlockBasedEditorSection() {
//		return propertiesSection;
//	}

	public SimpleProductTypePropertiesSection getPropertiesSection() {
		return propertiesSection;
	}

	private int sectionStyle = ExpandableComposite.TITLE_BAR;

	@Override
	protected void addSections(Composite parent)
	{
		structLocalScopeSection = new SimpleProductTypeStructLocalScopeSection(this, parent, sectionStyle);
		getManagedForm().addPart(structLocalScopeSection);

//		propertiesSection = new BlockBasedEditorSection(this, parent, sectionStyle, Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.editor.SimpleProductTypePropertySetPage.blockBasedEditorSection.title"));  //$NON-NLS-1$
		propertiesSection = new SimpleProductTypePropertiesSection(this, parent, sectionStyle, Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.editor.SimpleProductTypePropertySetPage.propertiesSection.title"));  //$NON-NLS-1$
		getManagedForm().addPart(propertiesSection);
	}

	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
		final SimpleProductTypePropertySetPageController controller = (SimpleProductTypePropertySetPageController) getPageController();
		final SimpleProductType simpleProductType = controller.getProductType();
		Job job = new Job(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.editor.SimpleProductTypePropertySetPage.loadStructLocalJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				final PropertySet propertySet = controller.getPropertySet();
				final StructLocal structLocal = propertySet == null ? null : StructLocalDAO.sharedInstance().getStructLocal(
						propertySet.getStructLocalObjectID(),
//						SimpleProductType.class,
//						simpleProductType.getPropertySet().getStructScope(),
//						simpleProductType.getPropertySet().getStructLocalScope(),
						monitor
				);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed())
							return; // Do nothing if UI is disposed
						structLocalScopeSection.setSimpleProductType(simpleProductType);
						propertiesSection.setSimpleProductType(simpleProductType);
						propertiesSection.setPropertySet(propertySet);
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
		return Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.editor.SimpleProductTypePropertySetPage.pageFormTitle"); //$NON-NLS-1$
	}

}
