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

	RecurringTimingConfigSection recurringTimingConfigSection;
	
	public RecurringOfferConfigurationPage(FormEditor editor) {
		super(editor, RecurringOfferConfigurationPage.class.getName(), "Recurring Offer Configuration");
	}

	@Override
	protected void addSections(Composite parent) {
		
		final RecurringOfferConfigurationPageController controller = (RecurringOfferConfigurationPageController) getPageController();
		
		
		recurringTimingConfigSection = new RecurringTimingConfigSection(this, parent, controller);
		getManagedForm().addPart(recurringTimingConfigSection);
	}

	@Override
	protected String getPageFormTitle() {
		return null;
	}

}
