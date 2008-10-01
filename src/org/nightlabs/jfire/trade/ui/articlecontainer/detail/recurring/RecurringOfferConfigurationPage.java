package org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.trade.ui.account.editor.AccountGeneralSection;


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

	
	/**
	 * The id of this page.
	 */
	public static final String ID_PAGE = RecurringOfferConfigurationPage.class.getName();
	
	RecurringTimingConfigSection recurringTimingConfigSection;
	RecurringOfferConfigSection recurringOfferConfigSection;



	public RecurringOfferConfigurationPage(FormEditor editor) {
		super(editor,ID_PAGE, "Recurring Offer Configuration");
	}

	@Override
	protected void addSections(Composite parent) {

		final RecurringOfferConfigurationPageController controller = (RecurringOfferConfigurationPageController) getPageController();
		
		recurringOfferConfigSection = new RecurringOfferConfigSection(this, parent, controller);
		getManagedForm().addPart(recurringOfferConfigSection);

		recurringTimingConfigSection = new RecurringTimingConfigSection(this, parent, controller);
		getManagedForm().addPart(recurringTimingConfigSection);
	}

	@Override
	protected void asyncCallback() {

	}
	
	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent)
	{
		switchToContent();
	}
	
	@Override
	protected String getPageFormTitle() {
		return "Recurring Offer Configuration";
	}
	
}
