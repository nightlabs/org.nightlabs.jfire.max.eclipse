package org.nightlabs.jfire.auth.ui.ldap.editor;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPServer;

/**
 * Main page for editing {@link LDAPServer} object. 
 * 
 * Adds two sections: 
 * {@link LDAPServerGeneralConfigSection} for editing general properties like host, port and name; 
 * {@link LDAPServerAdvancedConfigSection} for editing advanced properties dealing JFire and LDAP interaction.
 * 
 * Page controller is {@link LDAPServerMainPageController}.  
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 * 
 */
public class LDAPServerEditorMainPage extends EntityEditorPageWithProgress{
	
	public static final String ID_PAGE = LDAPServerEditorMainPage.class.getName();
	
	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link LDAPServerEditorMainPage} and {@link LDAPServerMainPageController}.
	 */
	public static class Factory implements IEntityEditorPageFactory {

		public IFormPage createPage(FormEditor formEditor) {
			return new LDAPServerEditorMainPage(formEditor);
		}
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new LDAPServerMainPageController(editor);
		}
		
	}
	
	private LDAPServerGeneralConfigSection generalConfigSection;
	private LDAPServerAdvancedConfigSection advancedConfigSection;
	
	/**
	 * Create an instance of {@link LDAPServerEditorMainPage}.
	 * This constructor is used by the entity editor page extension system.
	 * 
	 * @param editor The editor for which to create this form page.
	 */
	public LDAPServerEditorMainPage(FormEditor editor){
		super(editor, ID_PAGE, "LDAP Server configuration"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addSections(Composite parent) {
		generalConfigSection = new LDAPServerGeneralConfigSection(this, parent);
		getManagedForm().addPart(generalConfigSection);
		advancedConfigSection = new LDAPServerAdvancedConfigSection(this, parent);
		getManagedForm().addPart(advancedConfigSection);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getPageFormTitle() {
		return "Edit LDAP server configuration"; //$NON-NLS-1$
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configurePageWrapper(Composite pageWrapper) {
		GridLayout layout = new GridLayout(2, true);
		layout.marginBottom = 10;
		layout.marginTop = 5;
		layout.marginLeft = 5;
		layout.marginRight = 5;
		layout.horizontalSpacing = 10;
		pageWrapper.setLayout(layout);
	}
}
