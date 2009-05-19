package org.nightlabs.jfire.voucher.admin.ui.editor.accountpriceconfig;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.action.InheritanceAction;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeLocal;
import org.nightlabs.jfire.voucher.accounting.VoucherLocalAccountantDelegate;
import org.nightlabs.jfire.voucher.admin.ui.VoucherAdminPlugin;
import org.nightlabs.jfire.voucher.admin.ui.localaccountantdelegate.VoucherLocalAccountantDelegateComposite;
import org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.VoucherTypeTableDialog;
import org.nightlabs.jfire.voucher.dao.VoucherTypeDAO;
import org.nightlabs.jfire.voucher.store.VoucherType;
import org.nightlabs.jfire.voucher.ui.quicklist.VoucherTypeQuickListFilter;
import org.nightlabs.progress.NullProgressMonitor;


/**
 * @author fitas [at] NightLabs [dot] de
 *
 */
public class VoucherAccountConfigSection extends ToolBarSectionPart{

	private InheritanceAction inheritanceAction;
	private VoucherType voucherType;
	private VoucherLocalAccountantDelegate voucherLocalAccountantDelegate;
	private VoucherLocalAccountantDelegate orginalVoucherLocalAccountantDelegate;
	private HashMap<Currency, Account> accountsDelegateMap;
	private Boolean localAccountinheritance = false;
	private VoucherLocalAccountantDelegateComposite accountantDelegateComposite;

	public static final String[] FETCH_GROUPS_VOUCHER_ACCOUNT = {
		FetchPlan.DEFAULT, 
		ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE_ID, 
		ProductType.FETCH_GROUP_PRODUCT_TYPE_LOCAL,
		ProductTypeLocal.FETCH_GROUP_LOCAL_ACCOUNTANT_DELEGATE,
		ProductTypeLocal.FETCH_GROUP_FIELD_METADATA_MAP,
		VoucherLocalAccountantDelegate.FETCH_GROUP_VOUCHER_LOCAL_ACCOUNTS,
		VoucherLocalAccountantDelegate.FETCH_GROUP_NAME,Account.FETCH_GROUP_NAME
	};


	public VoucherAccountConfigSection(IFormPage page, Composite parent, int style) {
		super(page, parent, style, "Account Configuration");

		
		AssignAccountConfigAction assignAccountConfigAction = new AssignAccountConfigAction();
		getToolBarManager().add(assignAccountConfigAction);
		
		inheritanceAction = new InheritanceAction(){
			@Override
			public void run() {
				inheritPressed();
			}
		};
		inheritanceAction.setEnabled(false);
		getToolBarManager().add(inheritanceAction);

		XComposite comp = new XComposite(getContainer(), SWT.NONE);
		StackLayout stackLayout = new StackLayout();
		comp.setLayout(stackLayout);
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		accountantDelegateComposite = new VoucherLocalAccountantDelegateComposite(comp, true);
		accountantDelegateComposite.setMap(new HashMap<Currency, Account>());
		accountantDelegateComposite.addSelectionChangedListener(
				new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						// if value has changed
						if(!accountsDelegateMap.equals(accountantDelegateComposite.getMap()))
						{
							markDirty();
						}		
					}
				});

		stackLayout.topControl = accountantDelegateComposite;
		updateToolBarManager();

	}

	@Override
	public void commit(boolean onSave) {
		super.commit(onSave);
		
		
		if (voucherLocalAccountantDelegate== null)
			return;
			
		Collection<VoucherType> vouchers = VoucherTypeDAO.sharedInstance().getVoucherTypesByLocalAccountantDelegateId((ObjectID) JDOHelper.getObjectId(voucherLocalAccountantDelegate),
				VoucherTypeQuickListFilter.FETCH_GROUPS_VOUCHER_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
				
		if (!vouchers.isEmpty()) 
		{
			String title = "Affected voucher types";
			String message1 = "Storing the account configurations to the server will change the account configuration of the following voucher types.";
			String message2 = "Do you really want to proceed?";
			VoucherTypeTableDialog dlg = new VoucherTypeTableDialog(RCPUtil.getActiveShell(), vouchers, title, message1, message2) {
				@Override
				protected void createButtonsForButtonBar(Composite parent) {
					createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
					createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
				}
				
			};
			
			if(dlg.open() == Window.CANCEL)
				return;
		}
		
			// copy the values from the local account widget
			for (Map.Entry<Currency, Account> me : accountantDelegateComposite.getMap().entrySet()) {
				voucherLocalAccountantDelegate.setAccount(me.getKey().getCurrencyID(), me.getValue()); 		
			}	
			
			voucherType.getProductTypeLocal().getFieldMetaData(
					ProductTypeLocal.FieldName.localAccountantDelegate).setValueInherited(
							localAccountinheritance);		
			voucherType.getProductTypeLocal().setLocalAccountantDelegate(voucherLocalAccountantDelegate);
				
		
	}


	VoucherLocalAccountantDelegate getVoucherLocalAccountantDelegate()	
	{
		if (this.voucherType.getProductTypeLocal().getLocalAccountantDelegate() == null)
			return null;

		if(this.voucherType.getProductTypeLocal().getLocalAccountantDelegate() instanceof VoucherLocalAccountantDelegate)
			return (VoucherLocalAccountantDelegate) this.voucherType.getProductTypeLocal().getLocalAccountantDelegate();
		else
			throw new IllegalStateException("LocalAccountantDelegate is not an instance of VoucherLocalAccountantDelegate"); //$NON-NLS-1$
	}


	public void setVoucherType(VoucherType voucherType)
	{
		this.voucherType = voucherType; 
		voucherLocalAccountantDelegate = getVoucherLocalAccountantDelegate();
		orginalVoucherLocalAccountantDelegate = voucherLocalAccountantDelegate;
		inheritanceAction.setChecked(
				voucherType.getProductTypeLocal().getFieldMetaData(
						ProductTypeLocal.FieldName.localAccountantDelegate
				).isValueInherited()
		);
		inheritanceAction.setEnabled(voucherType.getExtendedProductTypeID() != null);

		updateContents();
	}


	protected void updateContents()
	{
		accountsDelegateMap =  new HashMap<Currency, Account>();

		for (Map.Entry<String, Account> me : voucherLocalAccountantDelegate.getAccounts().entrySet()) {		
			accountsDelegateMap.put(new Currency(me.getKey(),me.getKey(),2), me.getValue());
		}		
		localAccountinheritance = inheritanceAction.isChecked();
		Map<Currency, Account> copyMap = accountantDelegateComposite.getMap();
		// puts the accounts inside the widget
		copyMap.putAll(accountsDelegateMap);
		accountantDelegateComposite.setMap(copyMap);
	}



	protected void inheritPressed() {
		if( inheritanceAction.isChecked() )
		{
			VoucherType parentVoucherType =  VoucherTypeDAO.sharedInstance().getVoucherType(
					voucherType.getExtendedProductTypeID(),
					FETCH_GROUPS_VOUCHER_ACCOUNT,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
			
					voucherLocalAccountantDelegate = (VoucherLocalAccountantDelegate) parentVoucherType.getProductTypeLocal().getLocalAccountantDelegate();
		}
		else
			voucherLocalAccountantDelegate = orginalVoucherLocalAccountantDelegate;
		
		updateContents();
		markDirty();
	}

	
	
	protected void assignAccountConfigClicked()
	{
		AccountVoucherTypeWizard accountVoucherTypeWizard = new AccountVoucherTypeWizard(voucherType.getExtendedProductTypeID());
		DynamicPathWizardDialog wizardDialog = new DynamicPathWizardDialog(accountVoucherTypeWizard);
		if( wizardDialog.open() == Window.OK)
		{
			voucherLocalAccountantDelegate = accountVoucherTypeWizard.selectedVoucherLocalAccountantDelegate();
			inheritanceAction.setChecked(accountVoucherTypeWizard.isInherited());
			updateContents();
			markDirty();
		}
	}


	class AssignAccountConfigAction
	extends Action
	{
		public AssignAccountConfigAction() {
			super();
			setId(AssignAccountConfigAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					VoucherAdminPlugin.getDefault(),
					VoucherPriceConfigSection.class,
			"AssignAccountConfig")); //$NON-NLS-1$
			setToolTipText("assigns a new account configuration"); 
			setText("assigns a new account configuration");
		}

		@Override
		public void run() {
			assignAccountConfigClicked();
		}
	}

	
	
}
