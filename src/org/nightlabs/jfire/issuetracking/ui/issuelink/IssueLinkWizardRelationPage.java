package org.nightlabs.jfire.issuetracking.ui.issuelink;

import javax.jdo.FetchPlan;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issue.dao.IssueLinkTypeDAO;
import org.nightlabs.progress.NullProgressMonitor;

public class IssueLinkWizardRelationPage 
extends WizardHopPage
{
	private IssueLinkWizard iWizard;
	private IssueLinkAdder issueLinkAdder;
	
	private Text newRelationText;
	
	private Button createNewCheckBox;
	private Button selectFromCheckBox;
	
	private List predefinedRelationList;
	
	public IssueLinkWizardRelationPage(IssueLinkWizard iWizard, IssueLinkAdder issueLinkAdder) {
		super("Select/Create the relation for links", "Select/Create the relation for links.");
		setDescription("The relation for links");
		this.iWizard = iWizard;
		
		new WizardHop(this);
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE);
		mainComposite.getGridLayout().numColumns = 1;

		createNewCheckBox = new Button(mainComposite, SWT.RADIO);		
		createNewCheckBox.setText("Create a new relation");
		createNewCheckBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createNewCheckBox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setCreateNew(true, true);
			}
		});
		
		newRelationText = new Text(mainComposite, SWT.SINGLE | SWT.BORDER);
		newRelationText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		selectFromCheckBox = new Button(mainComposite, SWT.RADIO);
		selectFromCheckBox.setText("Select a relation from the list");
		selectFromCheckBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectFromCheckBox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setCreateNew(false, true);
			}
		});

		Group manageRelationGroup = new Group(mainComposite, SWT.NONE);
		manageRelationGroup.setLayout(new GridLayout(1, false));
		manageRelationGroup.setText("Predefined Relations");
		manageRelationGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		XComposite manageComposite = new XComposite(manageRelationGroup, SWT.NONE);
		manageComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		manageComposite.getGridLayout().numColumns = 1;
		
		predefinedRelationList = new List(manageComposite, SWT.NONE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

		Display.getCurrent().asyncExec(new Runnable() {
			@Override
			public void run() {
				final java.util.List<IssueLinkType> issueLinkTypes = IssueLinkTypeDAO.sharedInstance().getIssueLinkTypesByLinkClass(
						issueLinkAdder.getIssueLinkHandlerFactory().getLinkObjectClass(), 
						new String[] {IssueLinkType.FETCH_GROUP_THIS, FetchPlan.DEFAULT}, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
						new NullProgressMonitor());

				
				for (IssueLinkType issueLinkType : issueLinkTypes) {
					predefinedRelationList.add(issueLinkType.getName().getText());	
				}
				
				predefinedRelationList.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
//						IssueLinkType issueLinkType = 
//							iWizardissueLinkTypes.get(predefinedRelationList.getSelectionIndex());
					}
				});
			}
		});

		GridData gridData = new GridData(GridData.FILL_BOTH);
		predefinedRelationList.setLayoutData(gridData);
		
		
		setCreateNew(false, false);
		
		return mainComposite;
	}
	
	public void setCreateNew(boolean b, boolean updateButtons) {
		createNewCheckBox.setSelection(b);
		selectFromCheckBox.setSelection(!b);

		predefinedRelationList.setEnabled(selectFromCheckBox.getSelection());
		newRelationText.setEnabled(createNewCheckBox.getSelection());
		
		if (updateButtons)
			getContainer().updateButtons();
	}
	
	@Override
	public boolean isPageComplete() {
		return getWizardHop().getHopPages() != null && getWizardHop().getHopPages().size() != 0;
	}
}