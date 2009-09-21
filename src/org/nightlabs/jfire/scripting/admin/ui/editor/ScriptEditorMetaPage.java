package org.nightlabs.jfire.scripting.admin.ui.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;

public class ScriptEditorMetaPage
extends EntityEditorPageWithProgress
{

	public static class Factory implements IEntityEditorPageFactory{
		@Override
		public IFormPage createPage(FormEditor formEditor) {
			return new ScriptEditorMetaPage(formEditor);
		}

		@Override
		public IEntityEditorPageController createPageController(
				EntityEditor editor) {
			return new ScriptEditorPageController(editor);
		}

	}

	public ScriptEditorMetaPage(FormEditor editor) {
		super(editor,ScriptEditorMetaPage.class.getName() ," Meta data");
	}

	@Override
	protected void addSections(Composite parent) {
		ScriptEditorPageController controller = (ScriptEditorPageController)getPageController();

		ScriptMetaSection metaSection = new ScriptMetaSection(this,parent,controller);
		ScriptTableSection tableSection = new ScriptTableSection(this,parent,controller);

		getManagedForm().addPart(metaSection);
		getManagedForm().addPart(tableSection);

//		if (controller.isLoaded()) {
//			Script script = controller.getControllerObject();
//			metaSection.setScript(script);
//			tableSection.setScript(script);
//		}
//		else
//			System.out.println("NOT YET LOADED!");
	}

	@Override
	protected String getPageFormTitle() {
		return "Meta data";
	}

}
