package org.nightlabs.jfire.contact.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.base.ui.prop.edit.ValidationResultHandler;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.BlockBasedEditor;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.DataBlockEditorChangedEvent;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.DataBlockEditorChangedListener;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.FullDataBlockCoverageComposite;
import org.nightlabs.jfire.prop.StructLocal;
import org.nightlabs.jfire.prop.dao.StructLocalDAO;
import org.nightlabs.jfire.prop.validation.ValidationResult;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class ContactDetailEditorPage
extends EntityEditorPageWithProgress
{
	private FullDataBlockCoverageComposite fullDataBlockCoverageComposite;

	public ContactDetailEditorPage(FormEditor editor) {
		super(editor, ContactDetailEditorPage.class.getName(), null);
	}

	public ContactDetailEditorPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link EventDetailPage}.
	 */
	public static class Factory implements IEntityEditorPageFactory {
		public IFormPage createPage(FormEditor formEditor) {
			return new ContactDetailEditorPage(formEditor);
		}

		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new ContactEditorPageController(editor);
		}
	}

	@Override
	protected void addSections(Composite parent) {
		final ContactEditorPageController controller = (ContactEditorPageController)getPageController();

		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		toolkit.decorateFormHeading(getManagedForm().getForm().getForm());

		ScrolledForm scrolledForm = toolkit.createScrolledForm(parent);
		scrolledForm.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite body = scrolledForm.getBody();
		GridLayout layout = new GridLayout(1, true);
		body.setLayout(layout);

		if (controller.isLoaded()) {
			ValidationResultHandler resultManager = new ValidationResultHandler() {
				@Override
				public void handleValidationResult(ValidationResult validationResult) {

				}
			};
			StructLocal structLocal = StructLocalDAO.sharedInstance().getStructLocal(
					controller.getControllerObject().getStructLocalObjectID(),
					new NullProgressMonitor()
			);
			controller.getControllerObject().inflate(structLocal);
			fullDataBlockCoverageComposite = new FullDataBlockCoverageComposite(
					body,
					SWT.NONE,
					controller.getControllerObject(),
					null,
					resultManager) {
				@Override
				protected BlockBasedEditor createBlockBasedEditor() {
					return new BlockBasedEditor(false);
				}
			};
			fullDataBlockCoverageComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
			fullDataBlockCoverageComposite.addChangeListener(new DataBlockEditorChangedListener() {

				@Override
				public void dataBlockEditorChanged(
						DataBlockEditorChangedEvent dataBlockEditorChangedEvent) {
					controller.markDirty();
					getManagedForm().dirtyStateChanged();
				}
			});

		}
	}

	@Override
	protected String getPageFormTitle() {
		return "Contact Detail";
	}

}
