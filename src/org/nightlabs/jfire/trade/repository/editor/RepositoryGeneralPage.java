package org.nightlabs.jfire.trade.repository.editor;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.store.Repository;
import org.nightlabs.jfire.trade.resource.Messages;

class RepositoryGeneralPage
extends EntityEditorPageWithProgress
{
	private static Logger logger = Logger.getLogger(RepositoryGeneralPage.class);

	public static class Factory implements IEntityEditorPageFactory {
		public IFormPage createPage(FormEditor formEditor) {
			return new RepositoryGeneralPage(formEditor);
		}
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new RepositoryGeneralPageController(editor);
		}
	}

	private RepositoryGeneralSection repositoryGeneralSection;

	public RepositoryGeneralPage(FormEditor editor) {
		super(editor, RepositoryGeneralPage.class.getName(), Messages.getString("org.nightlabs.jfire.trade.repository.editor.RepositoryGeneralPage.title")); //$NON-NLS-1$
	}

	@Override
	protected void addSections(Composite parent)
	{
		repositoryGeneralSection = new RepositoryGeneralSection(this, parent);
		getManagedForm().addPart(repositoryGeneralSection);
	}

	@Override
	protected void asyncCallback() // this method will soon be deprecated => handleControllerObjectModified does all the work
	{
	}

	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent)
	{
		switchToContent();

		if (!(modifyEvent.getNewObject() instanceof Repository)) {
			logger.warn("handleControllerObjectModified: EntityEditorPageControllerModifyEvent.getNewObject() returned instance of " + (modifyEvent.getNewObject() == null ? null : modifyEvent.getNewObject().getClass().getName()), new Exception("DEBUG STACKTRACE")); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		final Repository repository = (Repository) modifyEvent.getNewObject();

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				repositoryGeneralSection.setRepository(repository);
			}
		});
	}

	@Override
	protected String getPageFormTitle() {
		return Messages.getString("org.nightlabs.jfire.trade.repository.editor.RepositoryGeneralPage.title"); //$NON-NLS-1$
	}

}
