package org.nightlabs.jfire.issuetracking.ui.issue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.wizard.WizardHopPage;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class IssueNewWizardPage extends WizardHopPage{
	private IssueCreateComposite issueCreateComposite;
	
	public IssueNewWizardPage(){
		super(IssueNewWizardPage.class.getName(), "Wizard Page Title");
		setDescription("Create a new issue.");
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE);
		mainComposite.getGridLayout().numColumns = 1;

		issueCreateComposite = new IssueCreateComposite(mainComposite, SWT.NONE);
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
		}
		super.setVisible(visible);
	}
	
	private void updatePageComplete() {
		setPageComplete(false);
		//.................
		setPageComplete(true);
		setErrorMessage(null);
		
	}
}
