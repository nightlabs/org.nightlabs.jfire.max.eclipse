/**
 *
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.BlockBasedEditorSection;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 *
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ArticleContainerPropertySetPage
extends EntityEditorPageWithProgress
{
	public static class Factory implements IEntityEditorPageFactory {
		@Override
		public IFormPage createPage(FormEditor formEditor) {
			return new ArticleContainerPropertySetPage(formEditor);
		}
		@Override
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new ArticleContainerPropertySetPageController(editor);
		}
	}

	public static final String PAGE_ID = ArticleContainerPropertySetPage.class.getName();

	private BlockBasedEditorSection blockBasedEditorSection;

	/**
	 * @param editor
	 */
	public ArticleContainerPropertySetPage(FormEditor editor) {
		super(editor, PAGE_ID, Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerPropertySetPage.page.name")); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#addSections(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void addSections(Composite parent) {
		blockBasedEditorSection = new BlockBasedEditorSection(this, parent, Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerPropertySetPage.section.name")); //$NON-NLS-1$
		getManagedForm().addPart(blockBasedEditorSection);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#getPageFormTitle()
	 */
	@Override
	protected String getPageFormTitle() {
		return Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerPropertySetPage.page.title"); //$NON-NLS-1$
	}

	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
		ArticleContainer articleContainer = (ArticleContainer) modifyEvent.getNewObject();
		blockBasedEditorSection.setPropertySet(articleContainer.getPropertySet());
	}
}
