package org.nightlabs.jfire.trade.admin.ui.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;

public class UnitEditorPage 
extends EntityEditorPageWithProgress
{
	public static class Factory implements IEntityEditorPageFactory{
		@Override
		public IFormPage createPage(FormEditor formEditor) {
			return new UnitEditorPage(formEditor);
		}

		@Override
		public IEntityEditorPageController createPageController(
				EntityEditor editor) {
			return new UnitEditorPageController(editor);
		}
	}
	
	public UnitEditorPage(FormEditor editor){
		super(editor,UnitEditorPage.class.getName(), "General");
	}
	
	@Override
	protected void addSections(Composite parent) {
		UnitEditorPageController controller = (UnitEditorPageController)getPageController();
        UnitSection unitSection = new UnitSection(this, parent, controller);
        getManagedForm().addPart(unitSection);
	}

	@Override
	protected String getPageFormTitle() {
		return "Unit";
	}
}