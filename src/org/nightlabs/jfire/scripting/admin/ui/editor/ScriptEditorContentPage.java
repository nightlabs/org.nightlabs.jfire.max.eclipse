package org.nightlabs.jfire.scripting.admin.ui.editor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.scripting.Script;
import org.nightlabs.jfire.scripting.admin.ui.editor.scriptedit.ScriptEdit;
import org.nightlabs.jfire.scripting.admin.ui.editor.scriptedit.ScriptEditFactory;
import org.nightlabs.jfire.scripting.admin.ui.editor.scriptedit.ScriptEditFactoryRegistry;

/**
 *
 * @author vince
 *
 */
public class ScriptEditorContentPage
extends EntityEditorPageWithProgress
{
	private Text contentText;
	public static class Factory implements IEntityEditorPageFactory{
		@Override
		public IFormPage createPage(FormEditor formEditor) {
			return new ScriptEditorContentPage(formEditor);
		}

		@Override
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			ScriptEditorPageController controller = editor.getController().getSinglePageController(ScriptEditorPageController.class);
			if (controller != null)
				return controller;
			return new ScriptEditorPageController(editor);
		}
	}

	public ScriptEditorContentPage(FormEditor editor) {
		super(editor, ScriptEditorContentPage.class.getName(), "Content");
	}


//	@Override
//	protected void createFormContent(IManagedForm managedForm) {
//		ScrolledForm form = managedForm.getForm();
//
//		configureForm(form);
//		configureBody(form.getBody());
//		form.getBody().setLayoutData(new GridData(GridData.FILL_BOTH));
//		int style = SWT.MULTI  |SWT.BORDER| SWT.V_SCROLL |SWT.H_SCROLL;
//		contentText = new Text(form.getBody(), style);
//		contentText.setLayoutData(new GridData(GridData.FILL_BOTH));
//
//		addSections(form.getBody());
//		// this will notify immediately, in case there was already an event.
//		getPageController().addModifyListener(new IEntityEditorPageControllerModifyListener() {
//			public void controllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
//				switchToContent();
//				handleControllerObjectModified(modifyEvent);
//			}
//		});
//	}

//	@Override
//	protected void handleControllerObjectModified(
//			EntityEditorPageControllerModifyEvent modifyEvent) {
//		switchToContent(); // multiple calls don't hurt
//		Display.getDefault().asyncExec(new Runnable() {
//			public void run() {
//				contentText.setText(getController().getScript().getText());
//			}
//		});
//	}

	private Composite container;
	private Map<String, ScriptEdit> language2ScriptEdit = new HashMap<String, ScriptEdit>();
	private Script script;
	private ScriptEdit scriptEdit;

	@Override
	protected void handleControllerObjectModified(final EntityEditorPageControllerModifyEvent modifyEvent)
	{
		super.handleControllerObjectModified(modifyEvent);

		getManagedForm().getForm().getDisplay().asyncExec(new Runnable() {
			public void run() {
				// Dispose old UI.
				for (Control child : container.getChildren())
					child.dispose();

				scriptEdit = null;

				// Get the new current Script instance.
				script = (Script) modifyEvent.getNewObject();
				if (script == null)
					return;

				// Find the factory for the current language or re-use a previously used ScriptEdit.
				scriptEdit = language2ScriptEdit.get(script.getLanguage());
				if (scriptEdit == null) {
					ScriptEditFactory factory = ScriptEditFactoryRegistry.sharedInstance().getScriptEditFactoryForLanguage(script.getLanguage(), true);
					scriptEdit = factory.createScriptEdit();
					language2ScriptEdit.put(script.getLanguage(), scriptEdit);
				}

				// Assign current script to be edited.
				scriptEdit.setScript(script);

				// Create new UI.
				scriptEdit.createControl(container);
				getManagedForm().reflow(true);
				container.layout();
			}
		});
	}

	@Override
	protected void addSections(Composite parent) {
		container = parent;


//		asyncLoadJob.schedule();
//		//Do nothing!!!!!!!!!!!!
//		if (getController().isLoaded()) {
//			contentText.setText(getController().getScript().getText());
//		}
	}

	@Override
	protected String getPageFormTitle() {
		return "Content";
	}

	protected ScriptEditorPageController getController() {
		return (ScriptEditorPageController)getPageController();
	}

//	private Job asyncLoadJob = new Job(Messages.getString("org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress.loadJob.name")) { //$NON-NLS-1$
//		@Override
//		protected IStatus run(ProgressMonitor monitor) {
//			final IEntityEditorPageController controller = getPageController();
//			if (controller != null) {
//
////				CompoundProgressMonitor compoundMonitor = new CompoundProgressMonitor(new ProgressMonitorWrapper(progressMonitorPart), monitor);
//				if (controller instanceof EntityEditorPageController) {
//					((EntityEditorPageController)controller).load(new NullProgressMonitor());
//				} // (controller instanceof EntityEditorPageController)
//				else
//					controller.doLoad(new NullProgressMonitor());
//			}
//			return Status.OK_STATUS;
//		}
//	};
}