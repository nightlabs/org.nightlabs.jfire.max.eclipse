package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.progress.ProgressMonitor;

/**
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ArticleContainerEditorDummyPage extends EntityEditorPageWithProgress {

	public static class Factory implements IEntityEditorPageFactory {

		@Override
		public IFormPage createPage(FormEditor formEditor) {
			return new ArticleContainerEditorDummyPage(formEditor);
		}

		@Override
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new EntityEditorPageController(editor) {
				@Override
				public void doLoad(ProgressMonitor monitor) {
					fireModifyEvent(null, null);
				}
				@Override
				public boolean doSave(ProgressMonitor monitor) {
					fireModifyEvent(null, null);
					return true;
				}
			};
		}
		
	}
	
	public ArticleContainerEditorDummyPage(FormEditor editor) {
		super(editor, ArticleContainerEditorDummyPage.class.getName(), "Dummy page");
	}

	@Override
	protected void addSections(Composite parent) {
		final ToolBarSectionPart sectionPart = new ToolBarSectionPart(
				getEditor().getToolkit(), parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE, "Test Section");
		getManagedForm().addPart(sectionPart);
		Button button = new Button(sectionPart.getContainer(), SWT.PUSH);
		button.setText("Mark dirty");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sectionPart.markDirty();
			}
		});
		
	}

	@Override
	protected String getPageFormTitle() {
		return "This is my dummy page";
	}
	
}
