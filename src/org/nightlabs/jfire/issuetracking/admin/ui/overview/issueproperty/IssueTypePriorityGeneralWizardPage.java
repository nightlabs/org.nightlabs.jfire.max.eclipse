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
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issuetracking.admin.ui.IssueTrackingAdminPlugin;

/**
 * @author Chairat Kongarayawetchakun 
 *
 */
public class IssueTypePriorityGeneralWizardPage 
extends WizardHopPage {
	
	private IssuePriority issuePriority;
	private IssueTypePriorityComposite priorityComposite;
	
	public IssueTypePriorityGeneralWizardPage(IssuePriority issuePriority) {
		super(	IssueTypePriorityGeneralWizardPage.class.getName(),
	    		"Title",
	    		SharedImages.getWizardPageImageDescriptor(IssueTrackingAdminPlugin.getDefault(), IssueTypePriorityGeneralWizardPage.class)
	    	);
		this.issuePriority = issuePriority;
	    setDescription("Description");
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		priorityComposite = new IssueTypePriorityComposite(issuePriority, parent, SWT.NONE);
		priorityComposite.getPriorityNameI18nTextEditor().addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				getContainer().updateButtons();
			}
		});
		return priorityComposite; 
	}

	public IssueTypePriorityComposite getPriorityComposite() {
		return priorityComposite;
	}
	
	@Override
	public boolean isPageComplete() {
		return priorityComposite != null && (priorityComposite.isComplete() && !priorityComposite.getPriorityNameI18nTextEditor().getI18nText().isEmpty());
	}
}
