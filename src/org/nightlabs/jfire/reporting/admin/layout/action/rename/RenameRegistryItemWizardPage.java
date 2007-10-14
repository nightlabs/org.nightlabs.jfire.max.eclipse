/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.layout.action.rename;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.language.I18nTextEditorWizardPage;
import org.nightlabs.jfire.reporting.admin.resource.Messages;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[ÐOT]de>
 *
 */
public class RenameRegistryItemWizardPage extends I18nTextEditorWizardPage {

	private ReportRegistryItem registryItem;
	
	/**
	 * @param pageName
	 * @param title
	 * @param editorCaption
	 */
	public RenameRegistryItemWizardPage(ReportRegistryItem registryItem) {
		super(RenameRegistryItemWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.reporting.admin.layout.action.rename.RenameRegistryItemWizardPage.title"), Messages.getString("org.nightlabs.jfire.reporting.admin.layout.action.rename.RenameRegistryItemWizardPage.editorCaption")); //$NON-NLS-1$ //$NON-NLS-2$
		this.registryItem = registryItem;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.language.I18nTextEditorWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		Control result = super.createPageContents(parent);
//		getI18nText().copyFrom(registryItem.getName());
		setI18nText(registryItem.getName());
//		get
		return result;
	}
	
	

}
