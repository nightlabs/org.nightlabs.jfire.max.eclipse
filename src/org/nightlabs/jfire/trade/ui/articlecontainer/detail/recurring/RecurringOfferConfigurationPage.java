package org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;


/**
 * @author Fitas Amine <!-- fitas [AT] nightlabs [DOT] de -->
 *
 */
public class RecurringOfferConfigurationPage extends EntityEditorPageWithProgress{

	public static class Factory implements IEntityEditorPageFactory {

		@Override
		public IFormPage createPage(FormEditor formEditor) {
			return new RecurringOfferConfigurationPage(formEditor);
		}

		@Override
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new RecurringOfferConfigurationPageController(editor);
		}

	}


	public RecurringOfferConfigurationPage(FormEditor editor) {
		super(editor, RecurringOfferConfigurationPage.class.getName(), "Recurring Offer Configuration");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void addSections(Composite parent) {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getPageFormTitle() {
		// TODO Auto-generated method stub
		return null;
	}

}
