package org.nightlabs.jfire.scripting.admin.ui.editor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IFormPart;
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
 * @author vince - vince at guinaree dot com
 *
 */
public class ScriptEditorContentPage
extends EntityEditorPageWithProgress
implements IScriptEditorContentPage
{

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

	private static final String DEFAULT_PAGE_FORM_TITLE = "Content";

	public ScriptEditorContentPage(FormEditor editor) {
		super(editor, ScriptEditorContentPage.class.getName(), DEFAULT_PAGE_FORM_TITLE);
	}

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
				if (scriptEdit != null)
					// Dispose old UI.
					for (IFormPart part : getManagedForm().getParts())
						getManagedForm().removePart(part);

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

				// tell the ScriptEdit in which IFormPage it is (to allow it creating sections).
				getManagedForm().getForm().setText(getPageFormTitle());
				setPartName(getPageFormTitle());
				scriptEdit.setFormPage(ScriptEditorContentPage.this);

				// Assign current script to be edited.
				scriptEdit.setScript(script);

				//scriptEdit.setDirtyStateManager()
				scriptEdit.setController((ScriptEditorPageController)getPageController());


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
	}

	@Override
	protected String getPageFormTitle() {
		return DEFAULT_PAGE_FORM_TITLE;
	}

	@Override
	public void setPartName(String partName) {
		super.setPartName(partName);
	}
}