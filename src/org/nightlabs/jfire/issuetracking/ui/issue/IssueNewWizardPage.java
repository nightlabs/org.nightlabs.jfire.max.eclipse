package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jdo.ObjectID;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class IssueNewWizardPage extends WizardHopPage{
	private IssueCreateComposite issueCreateComposite;
	private Set<ObjectID> objectIDs;
	
	public IssueNewWizardPage(Set<ObjectID> objectIDs){
		super(IssueNewWizardPage.class.getName(), "New Issue");
		this.objectIDs = objectIDs;
		setDescription("Create a new issue.");
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE);
		mainComposite.getGridLayout().numColumns = 1;

		issueCreateComposite = new IssueCreateComposite(mainComposite, SWT.NONE);
		issueCreateComposite.setAttachedObjectIDs(objectIDs);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		issueCreateComposite.setLayoutData(gridData);

//		issueCreateComposite.addModifyListener(new ModifyListener(){
//			public void modifyText(ModifyEvent e) {
//				updatePageComplete();
//			}
//		});

		return mainComposite;
	}
	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		super.createControl(parent);
	}	

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			updatePageComplete();
		}
		super.setVisible(visible);
	}
	
	private void updatePageComplete() {
		setPageComplete(false);
		
		if(issueCreateComposite.getSubjectText().getEditText() == null && issueCreateComposite.getSubjectText().getEditText().equals("")){
			setMessage("The subject should not be empty.");
		}//if
		
		if(issueCreateComposite.getSelectedReporter() == null){
			setMessage("The reporter should not be null");
		}
		else{
			setMessage(null);
		}

		//.................
		setPageComplete(true);
		setErrorMessage(null);
		
	}
	
	public IssueCreateComposite getIssueCreateComposite(){
		return issueCreateComposite;
	}
}
