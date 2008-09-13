package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.base.ui.form.CompositeFormPage;
import org.nightlabs.jfire.base.jdo.JDOObjectID2PCClassMap;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.progress.ProgressMonitor;

/**
 * This is the basic editor page {@link ArticleContainerEditor} 
 * works with. The editor expects the page to be added with the
 * id {@link #PAGE_ID} and will use it to serve the {@link ArticleContainerEdit}.
 * <p>
 * This page obtains the {@link ArticleContainerEdit} from {@link ArticleContainerEditFactoryRegistry}
 * based on the class-name of the loaded {@link ArticleContainer}, 
 * this is done in {@link #getArticleContainerEdit()}. 
 * </p>
 * <p>
 * Note, that this page assumes to be added to an editor that was opened
 * with an instance of {@link ArticleContainerEditorInput} as input.
 * </p>
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ArticleContainerEditorPage
//extends FormPage 
extends CompositeFormPage
{

	public static final String PAGE_ID = ArticleContainerEditorPage.class.getName(); 
	
	/**
	 * The factory that creates instances of {@link ArticleContainerEditorPage}. 
	 */
	public static class Factory implements IEntityEditorPageFactory {
		@Override
		public IFormPage createPage(FormEditor formEditor) {
			return new ArticleContainerEditorPage(formEditor);
		}
		@Override
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new EntityEditorPageController(editor) {
				@Override
				public void doLoad(ProgressMonitor monitor) {
				}
				@Override
				public boolean doSave(ProgressMonitor monitor) {
					return true;
				}
			};
		}
	}
	
	/**
	 * Create a new {@link ArticleContainerEditorPage} for the given editor.
	 * The id of the page will be {@link ArticleContainerEditorPage#PAGE_ID}. 
	 * 
	 * @param editor The editor the new page should be shown in.
	 */
	public ArticleContainerEditorPage(FormEditor editor) {
		super(editor, ArticleContainerEditorPage.PAGE_ID, "Articles");
	}
	
	/**
	 * Create a new {@link ArticleContainerEditorPage} for the given editor.
	 * The id of the page will be {@link ArticleContainerEditorPage#PAGE_ID}. 
	 * @param editor The editor the new page should be shown in.
	 * @param title The title of the new page.
	 */
	protected ArticleContainerEditorPage(FormEditor editor, String title) {
		super(editor, ArticleContainerEditorPage.PAGE_ID, title);
	}
	
	@Override
	protected Composite createComposite(Composite parent) {
		return getArticleContainerEdit().createComposite(parent);
	}
	
//	@Override
//	protected void createFormContent(IManagedForm managedForm) {
//		Composite body = managedForm.getForm().getBody();
//		managedForm.getForm().setDelayedReflow(true);
//		body.setLayout(new GridLayout());
//		XComposite.configureLayout(LayoutMode.TOTAL_WRAPPER, (GridLayout) body.getLayout());
//		NightlabsFormsToolkit toolkit = new NightlabsFormsToolkit(managedForm.getForm().getDisplay());
//		XComposite comp = new XComposite(managedForm.getForm().getBody(), SWT.NONE, LayoutMode.TOTAL_WRAPPER);
//		comp.setToolkit(toolkit);
//		getArticleContainerEdit().createComposite(comp);
//		comp.adaptToToolkit();
//		managedForm.getForm().getBody().layout(true, true);
////		super.createFormContent(managedForm);
//	}
	
	protected ArticleContainerEditorInput getArticleContainerEditorInput() {
		return (ArticleContainerEditorInput) getEditor().getEditorInput();
	}
	
	private ArticleContainerEdit articleContainerEdit;
	
	/**
	 * Returns the {@link ArticleContainerEdit} for this page.
	 * It is obtained from the {@link ArticleContainerEditFactoryRegistry} using the 
	 * class-name of the {@link ArticleContainer} this page should edit (according to
	 * the editor-input of the editor).
	 * 
	 * @return
	 */
	public ArticleContainerEdit getArticleContainerEdit() {
		if (articleContainerEdit == null) {
			Class<?> articleEditorClass = JDOObjectID2PCClassMap.sharedInstance().getPersistenceCapableClass(getArticleContainerEditorInput().getArticleContainerID());
			ArticleContainerEditFactory factory = null;
			try {
				factory = ArticleContainerEditFactoryRegistry.sharedInstance().getArticleContainerEditFactory(articleEditorClass.getName());
			} catch (EPProcessorException e) {
				throw new RuntimeException(e);
			}
			if (factory == null) 
				throw new IllegalStateException("Could not find a ArticleContainerEdit for the articleContainerClass: " + articleEditorClass);
			articleContainerEdit = factory.createArticleContainerEdit();
			articleContainerEdit.init(getArticleContainerEditorInput().getArticleContainerID());
		}
		return articleContainerEdit;
	}
	
}
