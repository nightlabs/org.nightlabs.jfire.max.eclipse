/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.layout.action.rename;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditorWizardPage;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.jfire.reporting.admin.ui.resource.Messages;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[ÐOT]de>
 *
 */
public class RenameRegistryItemWizardPage extends I18nTextEditorWizardPage {

	private ReportRegistryItem registryItem;
	private I18nTextEditorMultiLine descriptionEditor;
	
	/**
	 * @param pageName
	 * @param title
	 * @param editorCaption
	 */
	public RenameRegistryItemWizardPage(ReportRegistryItem registryItem) {
		super(RenameRegistryItemWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.action.rename.RenameRegistryItemWizardPage.title"), Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.action.rename.RenameRegistryItemWizardPage.editorCaption")); //$NON-NLS-1$ //$NON-NLS-2$
		this.registryItem = registryItem;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.language.I18nTextEditorWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		Control result = super.createPageContents(parent);
		setI18nText(registryItem.getName());
		return result;
	}
	
	@Override
	protected void createAdditionalContents(Composite wrapper) {
		super.createAdditionalContents(wrapper);
		descriptionEditor = new I18nTextEditorMultiLine(wrapper, getLanguageChooser());
		descriptionEditor.setI18nText(registryItem.getDescription());
		descriptionEditor.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	protected I18nText getNameBuffer() {
		return getI18nText();
	}
	protected I18nText getDescriptionBuffer() {
		return descriptionEditor.getI18nText();
	}
}
