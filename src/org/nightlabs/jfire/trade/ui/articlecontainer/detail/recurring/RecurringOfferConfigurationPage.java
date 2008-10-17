package org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring;


import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.trade.ui.resource.Messages;

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
		super(editor,ID_PAGE, Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.RecurringOfferConfigurationPage.title")); //$NON-NLS-1$
	}

	@Override
	protected void addSections(Composite parent) {

		final RecurringOfferConfigurationPageController controller = (RecurringOfferConfigurationPageController) getPageController();

		recurringOfferConfigSection = new RecurringOfferConfigSection(this, parent, controller);
		getManagedForm().addPart(recurringOfferConfigSection);
		recurringOfferConfigSection.getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		recurringTimingConfigSection = new RecurringTimingConfigSection(this, parent, controller);
		getManagedForm().addPart(recurringTimingConfigSection);


		if (controller.isLoaded()) {
			recurringOfferConfigSection.setRecurringOfferConfiguration(controller.getControllerObject());
			recurringTimingConfigSection.setRecurringOfferConfiguration(controller.getControllerObject());

		}


	}

	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent)
	{
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (recurringOfferConfigSection != null ) 
					recurringOfferConfigSection.setRecurringOfferConfiguration(getController().getControllerObject());

				if (recurringTimingConfigSection != null ) 
					recurringTimingConfigSection.setRecurringOfferConfiguration(getController().getControllerObject());

				switchToContent();
				getManagedForm().getForm().reflow(true);
			}
		});



	}


	protected RecurringOfferConfigurationPageController getController() {
		return (RecurringOfferConfigurationPageController)getPageController();
	}

	@Override
	protected String getPageFormTitle() {
		return Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.RecurringOfferConfigurationPage.formTitle"); //$NON-NLS-1$
	}

}
