package org.nightlabs.jfire.trade.account.transfer.manual;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.jfire.trade.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;

/**
 * @author Chairat Kongarayawetchakun - chairatk[at]nightlabs[dot]de
 */
class ManualMoneyTransferWizardPage extends WizardHopPage{
	private ManualMoneyTransferComposite manualMoneyTransferComposite;
	
	public ManualMoneyTransferWizardPage(){
		super(ManualMoneyTransferWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.trade.account.transfer.manual.ManualMoneyTransferWizardPage.title")); //$NON-NLS-1$
		setDescription("Transfer money from one account to another account with these information.");
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE);
		mainComposite.getGridLayout().numColumns = 1;

		manualMoneyTransferComposite = new ManualMoneyTransferComposite(mainComposite, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		manualMoneyTransferComposite.setLayoutData(gridData);

		manualMoneyTransferComposite.getCurrencyEdit().addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				updatePageComplete();
			}
		});
		
		manualMoneyTransferComposite.getReasonText().addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				updatePageComplete();
			}
		});

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
			ManualMoneyTransferWizard wizard = (ManualMoneyTransferWizard)getWizard();
			
			AnchorID fromAccount;
			if(wizard.getFromAccountChooserPage() != null){
				fromAccount = wizard.getFromAccountChooserPage().getSelectedAccount();
			}//if
			else{
				fromAccount = wizard.getSelectedAccountAnchorID();
			}
			AnchorID toAccount = wizard.getToAccountChooserPage().getSelectedAccount();
			manualMoneyTransferComposite.setFromAccount(fromAccount);
			manualMoneyTransferComposite.setToAccount(toAccount);
		}
		super.setVisible(visible);
	}
	
	private void updatePageComplete() {
		setPageComplete(false);

		long amount = manualMoneyTransferComposite.getCurrencyEdit().getValue();
		if(amount <= 0){
			setMessage("This money will transfer from \"TO-Account\" to \"FROM-Account\"");
		}//if
		
		if(getReason() == null){
			setMessage("Should enter the reason why transfer money manually.");
		}
		else{
			setMessage(null);
		}

		setPageComplete(true);
		setErrorMessage(null);
		
	}
	
	public long getAmount(){
		try{
			return manualMoneyTransferComposite.getCurrencyEdit().getValue();		
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public I18nText getReason(){
		I18nText reason = manualMoneyTransferComposite.getReasonText().getI18nText();
		return reason;
	}
	
	public AnchorID getToAccount(){
		return manualMoneyTransferComposite.getToAccount();
	}
	
	public AnchorID getFromAccount(){
		return manualMoneyTransferComposite.getFromAccount();
	}
}