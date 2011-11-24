package org.nightlabs.jfire.dynamictrade.admin.ui.editor;

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
import org.nightlabs.jfire.dynamictrade.admin.ui.resource.Messages;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.organisation.Organisation;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.Struct;
import org.nightlabs.jfire.prop.StructLocal;
import org.nightlabs.jfire.prop.dao.StructLocalDAO;
import org.nightlabs.jfire.prop.id.StructLocalID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DynamicProductTypePropertySetPage
extends EntityEditorPageWithProgress
{
	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link EventDetailPage}.
	 */
	public static class Factory implements IEntityEditorPageFactory {
		public IFormPage createPage(FormEditor formEditor) {
			return new DynamicProductTypePropertySetPage(formEditor);
		}

		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new DynamicProductTypePropertySetPageController(editor);
		}
	}

	private DynamicProductTypeStructLocalScopeSection structLocalScopeSection = null;
//	private BlockBasedEditorSection propertiesSection = null;
	private DynamicProductTypePropertiesSection propertiesSection = null;


	/**
	 * @param editor
	 * @param id
	 * @param name
	 */
	public DynamicProductTypePropertySetPage(FormEditor editor) {
		super(editor, DynamicProductTypePropertySetPage.class.getName(), Messages.getString("org.nightlabs.jfire.dynamictrade.admin.ui.editor.DynamicProductTypePropertySetPage.title"));  //$NON-NLS-1$
	}

//	public BlockBasedEditorSection getBlockBasedEditorSection() {
//		return propertiesSection;
//	}

	public DynamicProductTypePropertiesSection getPropertiesSection() {
		return propertiesSection;
	}

	private int sectionStyle = ExpandableComposite.TITLE_BAR;

	@Override
	protected void addSections(Composite parent)
	{
		structLocalScopeSection = new DynamicProductTypeStructLocalScopeSection(this, parent, sectionStyle);
		getManagedForm().addPart(structLocalScopeSection);

		propertiesSection = new DynamicProductTypePropertiesSection(this, parent, sectionStyle, Messages.getString("org.nightlabs.jfire.dynamictrade.admin.ui.editor.DynamicProductTypePropertySetPage.propertiesSection.title"));  //$NON-NLS-1$
		getManagedForm().addPart(propertiesSection);
	}

	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
		final DynamicProductTypePropertySetPageController controller = (DynamicProductTypePropertySetPageController) getPageController();
		final DynamicProductType productType = controller.getProductType();
		Job job = new Job(Messages.getString("org.nightlabs.jfire.dynamictrade.admin.ui.editor.DynamicProductTypePropertySetPage.loadStructLocalJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				PropertySet propertySet = controller.getPropertySet();
				if (propertySet == null) {
					StructLocal structLocal = 
						StructLocalDAO.sharedInstance().getStructLocal(StructLocalID.create(Organisation.DEV_ORGANISATION_ID, DynamicProductType.class, Struct.DEFAULT_SCOPE, StructLocal.DEFAULT_SCOPE), monitor);
					propertySet = new PropertySet(productType.getOrganisationID(), IDGenerator.nextID(PropertySet.class), structLocal);
				}
				final PropertySet finalProp = propertySet;
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed())
							return; // Do nothing if UI is disposed
						structLocalScopeSection.setPropertySet(finalProp);
						propertiesSection.setSimpleProductType(productType);
						propertiesSection.setPropertySet(finalProp);
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
		return Messages.getString("org.nightlabs.jfire.dynaictrade.admin.ui.editor.DynamicProductTypePropertySetPage.pageFormTitle"); //$NON-NLS-1$
	}

}
