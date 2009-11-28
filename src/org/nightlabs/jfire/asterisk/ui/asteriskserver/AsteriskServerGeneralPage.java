package org.nightlabs.jfire.asterisk.ui.asteriskserver;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class AsteriskServerGeneralPage
extends EntityEditorPageWithProgress
{
	/**
	 * The id of this page.
	 */
	public static final String ID_PAGE = AsteriskServerGeneralPage.class.getName();

	private AsteriskServerBasicSection asteriskServerBasicSection;
	private AsteriskServerCallFilePropertiesSection callFilePropertiesSection;

	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link AsteriskServerGeneralPage}.
	 */
	public static class Factory implements IEntityEditorPageFactory {

		public IFormPage createPage(FormEditor formEditor) {
			return new AsteriskServerGeneralPage(formEditor);
		}
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new AsteriskServerEditorPageController(editor);
		}
	}

	/**
	 * Create an instance of AsteriskServerPropertiesPage.
	 * <p>
	 * This constructor is used by the entity editor
	 * page extension system.
	 *
	 * @param editor The editor for which to create this
	 * 		form page.
	 */
	public AsteriskServerGeneralPage(FormEditor editor)
	{
		super(editor, ID_PAGE, "General");
	}

//	private ScrolledComposite scrolledComposite;
	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#addSections(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void addSections(Composite parent) {
//		scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
//		scrolledComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
//
//		final XComposite mainComposite = new XComposite(scrolledComposite, SWT.NONE);
//		GridLayout layout = (GridLayout)mainComposite.getLayout();
//		layout.numColumns = 1;
//		layout.makeColumnsEqualWidth = true;
//
//		scrolledComposite.setContent(mainComposite);
//		scrolledComposite.setExpandHorizontal(true);
//		scrolledComposite.setExpandVertical(true);

		asteriskServerBasicSection = new AsteriskServerBasicSection(this, parent);
		getManagedForm().addPart(asteriskServerBasicSection);

		callFilePropertiesSection = new AsteriskServerCallFilePropertiesSection(this, parent);
		getManagedForm().addPart(callFilePropertiesSection);
	}

// @Yo: If you override the correct methods (=> Eclipse API) in your sections, you don't need to override this method anymore.
//	/*
//	 * (non-Javadoc)
//	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#handleControllerObjectModified(org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent)
//	 */
//	@Override
//	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
//		Display.getDefault().asyncExec(new Runnable() {
//			public void run() {
//				AsteriskServer asteriskServer =
//					((AsteriskServerEditorPageController)getPageController()).getControllerObject();
//				callFilePropertiesSection.setAsteriskServer(asteriskServer);
//				switchToContent();
//			}
//		});
//	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#getPageFormTitle()
	 */
	@Override
	protected String getPageFormTitle() {
		return "General";
	}
}
