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
import org.nightlabs.jfire.issue.IssueResolution;
import org.nightlabs.jfire.issuetracking.admin.ui.IssueTrackingAdminPlugin;

/**
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 *
 */
public class IssueTypeResolutionGeneralWizardPage 
extends WizardHopPage 
{
	private IssueResolution issueResolution;
	private IssueTypeResolutionComposite resolutionComposite;
	
	public IssueTypeResolutionGeneralWizardPage(IssueResolution issueResolution) {
		super(	IssueTypeResolutionGeneralWizardPage.class.getName(),
	    		"Title",
	    		SharedImages.getWizardPageImageDescriptor(IssueTrackingAdminPlugin.getDefault(), IssueTypeResolutionGeneralWizardPage.class)
	    	);
		this.issueResolution = issueResolution;
	    setDescription("Description");
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		resolutionComposite = new IssueTypeResolutionComposite(issueResolution, parent, SWT.NONE);
		resolutionComposite.getResolutionNameI18nTextEditor().addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				getContainer().updateButtons();
			}
		});
		return resolutionComposite; 
	}

	public IssueTypeResolutionComposite getResolutionComposite() {
		return resolutionComposite;
	}
	
	@Override
	public boolean isPageComplete() {
		return resolutionComposite != null && (resolutionComposite.isComplete() && !resolutionComposite.getResolutionNameI18nTextEditor().getI18nText().isEmpty());
	}
}
