package org.nightlabs.jfire.voucher.admin.ui.editor.accountpriceconfig;

import java.util.HashMap;
import java.util.Map;

import javax.jdo.FetchPlan;

import org.eclipse.jface.action.Action;
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
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.book.LocalAccountantDelegate;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeLocal;
import org.nightlabs.jfire.voucher.accounting.VoucherLocalAccountantDelegate;
import org.nightlabs.jfire.voucher.admin.ui.VoucherAdminPlugin;
import org.nightlabs.jfire.voucher.admin.ui.localaccountantdelegate.VoucherLocalAccountantDelegateComposite;
import org.nightlabs.jfire.voucher.dao.VoucherTypeDAO;
import org.nightlabs.jfire.voucher.store.VoucherType;
import org.nightlabs.math.Base36Coder;
import org.nightlabs.progress.NullProgressMonitor;


/**
 * @author fitas [at] NightLabs [dot] de
 *
 */
public class VoucherAccountConfigSection extends ToolBarSectionPart{

	private InheritanceAction inheritanceAction;
	private VoucherType voucherType;
	private VoucherLocalAccountantDelegate voucherLocalAccountantDelegate;
	private HashMap<Currency, Account> accountsDelegateMap;
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
		
		VoucherLocalAccountantDelegate delegate1 = getVoucherLocalAccountantDelegate();
		
		if (delegate1!= null)
		{				
		
			VoucherLocalAccountantDelegate delegate = new VoucherLocalAccountantDelegate(
					IDGenerator.getOrganisationID(),
					Base36Coder.sharedInstance(false).encode(IDGenerator.nextID(LocalAccountantDelegate.class), 1));
			delegate.getName().copyFrom(getVoucherLocalAccountantDelegate().getName());
			

			for (Map.Entry<Currency, Account> me : accountantDelegateComposite.getMap().entrySet()) {
				delegate.setAccount(me.getKey().getCurrencyID(), me.getValue()); 		
			}	
			
		
			voucherType.getProductTypeLocal().setLocalAccountantDelegate(delegate);
		
			voucherType.getProductTypeLocal().getFieldMetaData(
					ProductTypeLocal.FieldName.localAccountantDelegate).setValueInherited(
							false);		
		
		
		}
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
		voucherLocalAccountantDelegate = (VoucherLocalAccountantDelegate) this.voucherType.getProductTypeLocal().getLocalAccountantDelegate();
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
		VoucherLocalAccountantDelegate localAccountantDelegate = (VoucherLocalAccountantDelegate) this.voucherType.getProductTypeLocal().getLocalAccountantDelegate();

		for (Map.Entry<String, Account> me : localAccountantDelegate.getAccounts().entrySet()) {		
			accountsDelegateMap.put(new Currency(me.getKey(),me.getKey(),2), me.getValue());
		}		

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
			voucherType.getProductTypeLocal().setLocalAccountantDelegate(
					parentVoucherType.getProductTypeLocal().getLocalAccountantDelegate());
		}
		else
			voucherType.getProductTypeLocal().setLocalAccountantDelegate(voucherLocalAccountantDelegate);

		voucherType.getProductTypeLocal().getFieldMetaData(
				ProductTypeLocal.FieldName.localAccountantDelegate).setValueInherited(
						inheritanceAction.isChecked());		

		updateContents();
		markDirty();
	}

	
	
	protected void assignAccountConfigClicked()
	{
		AccountVoucherTypeWizard priceVoucherTypeWizard = new AccountVoucherTypeWizard(voucherType.getExtendedProductTypeID(),this.voucherType);
		DynamicPathWizardDialog wizardDialog = new DynamicPathWizardDialog(priceVoucherTypeWizard);
		if( wizardDialog.open() == Window.OK)
		{
			
			inheritanceAction.setChecked(voucherType.getProductTypeLocal().getFieldMetaData(ProductTypeLocal.FieldName.localAccountantDelegate).isValueInherited());
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
