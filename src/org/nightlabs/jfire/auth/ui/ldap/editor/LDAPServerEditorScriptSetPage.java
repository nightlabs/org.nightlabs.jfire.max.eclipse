package org.nightlabs.jfire.auth.ui.ldap.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPScriptSet;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPServer;

/**
 * Page for editing {@link LDAPScriptSet} object of {@link LDAPServer}. 
 * 
 * Adds one section: 
 * {@link LDAPServerScriptSetSection} for editing scripts of corresponding {@link LDAPScriptSet} object; 
 * 
 * Page controller is {@link LDAPServerScriptSetPageController}.  
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 * 
 */
public class LDAPServerEditorScriptSetPage extends EntityEditorPageWithProgress{
	
	public static final String ID_PAGE = LDAPServerEditorScriptSetPage.class.getName();
	
	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link LDAPServerEditorScriptSetPage} and {@link LDAPServerScriptSetPageController}.
	 */
	public static class Factory implements IEntityEditorPageFactory {

		public IFormPage createPage(FormEditor formEditor) {
			return new LDAPServerEditorScriptSetPage(formEditor);
		}
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new LDAPServerScriptSetPageController(editor);
		}
		
	}
	
	private LDAPServerScriptSetSection scriptsSection;
	
	/**
	 * Create an instance of {@link LDAPServerEditorScriptSetPage}.
	 * This constructor is used by the entity editor page extension system.
	 * 
	 * @param editor The editor for which to create this form page.
	 */
	public LDAPServerEditorScriptSetPage(FormEditor editor){
		super(editor, ID_PAGE, "JFire-LDAP interaction scripts"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addSections(Composite parent) {
		scriptsSection = new LDAPServerScriptSetSection(this, parent);
		getManagedForm().addPart(scriptsSection);
	}
	
	/**
	 * Get {@link LDAPServerScriptSetSection} of this page.
	 * 
	 * @return scriptsSection
	 */
	public LDAPServerScriptSetSection getScriptsSection() {
		return scriptsSection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getPageFormTitle() {
		return "Edit JFire-LDAP interaction scripts (Do NOT edit these scripts unless you know exactly what you are doing!)"; //$NON-NLS-1$
	}
	
}
