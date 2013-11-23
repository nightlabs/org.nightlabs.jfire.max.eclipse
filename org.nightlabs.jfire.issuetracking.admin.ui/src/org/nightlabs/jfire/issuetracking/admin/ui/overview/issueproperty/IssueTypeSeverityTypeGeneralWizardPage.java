/**
 * 
 */
package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issuetracking.admin.ui.IssueTrackingAdminPlugin;
import org.nightlabs.jfire.issuetracking.admin.ui.resource.Messages;

/**
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 *
 */
public class IssueTypeSeverityTypeGeneralWizardPage 
extends WizardHopPage 
{
	private IssueSeverityType issueSeverityType;
	private IssueTypeSeverityTypeComposite severityTypeComposite;
	
	public IssueTypeSeverityTypeGeneralWizardPage(IssueSeverityType issueSeverityType) {
		super(	IssueTypeSeverityTypeGeneralWizardPage.class.getName(),
	    		Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypeSeverityTypeGeneralWizardPage.title"), //$NON-NLS-1$
	    		SharedImages.getWizardPageImageDescriptor(IssueTrackingAdminPlugin.getDefault(), IssueTypeSeverityTypeGeneralWizardPage.class)
	    	);
		this.issueSeverityType = issueSeverityType;
	    setDescription(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypeSeverityTypeGeneralWizardPage.description")); //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		severityTypeComposite = new IssueTypeSeverityTypeComposite(issueSeverityType, parent, SWT.NONE);
		severityTypeComposite.getSeverityTypeNameI18nTextEditor().addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				getContainer().updateButtons();
			}
		});
		return severityTypeComposite; 
	}

	public IssueTypeSeverityTypeComposite getSeverityTypeComposite() {
		return severityTypeComposite;
	}
	
	@Override
	public boolean isPageComplete() {
		return severityTypeComposite != null && (severityTypeComposite.isComplete() && !severityTypeComposite.getSeverityTypeNameI18nTextEditor().getI18nText().isEmpty());
	}
}
