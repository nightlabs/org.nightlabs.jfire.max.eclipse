package org.nightlabs.jfire.pbx.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.pbx.ui.resource.Messages;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class PhoneSystemConfigPage
extends EntityEditorPageWithProgress
{
	/**
	 * The id of this page.
	 */
	public static final String ID_PAGE = PhoneSystemConfigPage.class.getName();


	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link PhoneSystemConfigPage}.
	 */
	public static class Factory implements IEntityEditorPageFactory {

		public IFormPage createPage(FormEditor formEditor) {
			return new PhoneSystemConfigPage(formEditor);
		}
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new PhoneSystemEditorPageController(editor);
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
	public PhoneSystemConfigPage(FormEditor editor)
	{
		super(editor, ID_PAGE, Messages.getString("org.nightlabs.jfire.pbx.ui.PhoneSystemConfigPage.name")); //$NON-NLS-1$
	}

	private PhoneSystemCallableFieldSection phoneSystemCallableFieldSection;
	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#addSections(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void addSections(Composite parent) {
		phoneSystemCallableFieldSection = new PhoneSystemCallableFieldSection(this, parent);
		getManagedForm().addPart(phoneSystemCallableFieldSection);
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#getPageFormTitle()
	 */
	@Override
	protected String getPageFormTitle() {
		return Messages.getString("org.nightlabs.jfire.pbx.ui.PhoneSystemConfigPage.pageFormTitle"); //$NON-NLS-1$
	}
}
