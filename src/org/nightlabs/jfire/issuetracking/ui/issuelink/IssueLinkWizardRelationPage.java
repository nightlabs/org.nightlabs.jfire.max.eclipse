package org.nightlabs.jfire.issuetracking.ui.issuelink;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.base.ui.wizard.WizardHopPage;

public class IssueLinkWizardRelationPage 
extends WizardHopPage
{
	private IssueLinkWizard iWizard;
	
	private Text relationText;
	
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

		SashForm sashForm = new SashForm(mainComposite, SWT.VERTICAL);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		sashForm.setLayoutData(gridData);
		
		XComposite generalComposite = new XComposite(sashForm, SWT.NONE);
		generalComposite.getGridLayout().numColumns = 1;
		
		new Label(generalComposite, SWT.NONE).setText("Relation:");
		relationText = new Text(generalComposite, SWT.SINGLE | SWT.BORDER);
		relationText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(generalComposite, SWT.NONE).setText("Predefined Relations:");
		List predefinedRelationList = new List(generalComposite, SWT.NONE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		predefinedRelationList.add("No Relations");
		predefinedRelationList.add("Relation 1");
		predefinedRelationList.add("Relation 2");
		predefinedRelationList.add("Relation 3");
		predefinedRelationList.add("Relation 4");
		predefinedRelationList.add("Relation 5");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 50;
		predefinedRelationList.setLayoutData(gridData);
		
		
		Group manageRelationGroup = new Group(sashForm, SWT.NONE);
		manageRelationGroup.setText("Relations");
		manageRelationGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		new Label(manageRelationGroup, SWT.NONE).setText("HALLLLLLLLLLLLO!!");
		
		return mainComposite;
	}
	
	@Override
	public boolean isPageComplete() {
		return getWizardHop().getHopPages() != null && getWizardHop().getHopPages().size() != 0;
	}
}
