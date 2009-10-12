package org.nightlabs.jfire.trade.admin.ui.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;

/**
 *
 * @author vince
 *
 */

public class CurrencyEditorMetaPage extends EntityEditorPageWithProgress {



	public static class Factory implements IEntityEditorPageFactory{
		@Override
		public IFormPage createPage(FormEditor formEditor) {
			return new CurrencyEditorMetaPage(formEditor);
		}

		@Override
		public IEntityEditorPageController createPageController(
				EntityEditor editor) {
			return new CurrencyEditorPageController(editor);
		}

	}

	public CurrencyEditorMetaPage(FormEditor editor){
		super(editor,CurrencyEditorMetaPage.class.getName(),"Currency Data");
	}

	@Override
	protected void addSections(Composite parent) {


		CurrencyEditorPageController controller = (CurrencyEditorPageController)getPageController();
        CurrencySection currencySection = new CurrencySection(this, parent, controller);
        getManagedForm().addPart(currencySection);



	}

	@Override
	protected String getPageFormTitle() {

		return "Currency data";
	}



}
