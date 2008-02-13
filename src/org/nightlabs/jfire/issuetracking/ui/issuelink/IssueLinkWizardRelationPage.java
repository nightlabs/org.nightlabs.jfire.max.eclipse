package org.nightlabs.jfire.issuetracking.ui.issuelink;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.base.ui.wizard.WizardHopPage;

public class IssueLinkWizardRelationPage 
extends WizardHopPage
{
	private IssueLinkWizard iWizard;
	
	public IssueLinkWizardRelationPage(IssueLinkWizard iWizard) {
		super("Select/Create the relation for links", "Select/Create the relation for links.");
		setDescription("The relation for links");
		this.iWizard = iWizard;
		
		new WizardHop(this);
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE);
		mainComposite.getGridLayout().numColumns = 1;

		return mainComposite;
	}
	
	@Override
	public boolean isPageComplete() {
		return getWizardHop().getHopPages() != null && getWizardHop().getHopPages().size() != 0;
	}
}
