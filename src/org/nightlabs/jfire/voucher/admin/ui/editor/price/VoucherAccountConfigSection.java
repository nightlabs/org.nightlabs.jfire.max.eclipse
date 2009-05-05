package org.nightlabs.jfire.voucher.admin.ui.editor.price;

import java.util.HashMap;
import java.util.Map;

import javax.jdo.FetchPlan;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
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
import org.nightlabs.jfire.accounting.priceconfig.FetchGroupsPriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.PriceConfig;
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
	private VoucherType parentVoucherType;
	private VoucherLocalAccountantDelegate voucherLocalAccountantDelegate;
	private HashMap<Currency, Account> accountsDelegateMap;
	private VoucherLocalAccountantDelegateComposite accountantDelegateComposite;


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
		//accountantDelegateComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
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

		MenuManager menuManager = new MenuManager();
		menuManager.add(assignAccountConfigAction);
		Menu menu = menuManager.createContextMenu(accountantDelegateComposite);
		getContainer().setMenu(menu);
		updateToolBarManager();

	}

	@Override
	public void commit(boolean onSave) {
		super.commit(onSave);
		if (this.voucherType != null)
		{		
			VoucherLocalAccountantDelegate delegate = new VoucherLocalAccountantDelegate(
					IDGenerator.getOrganisationID(),
					Base36Coder.sharedInstance(false).encode(IDGenerator.nextID(LocalAccountantDelegate.class), 1));
			delegate.getName().copyFrom(this.voucherType.getProductTypeLocal().getLocalAccountantDelegate().getName());
			for (Map.Entry<Currency, Account> me : accountantDelegateComposite.getMap().entrySet()) {
				delegate.setAccount(me.getKey().getCurrencyID(), me.getValue());
			}
			
			this.voucherType.getProductTypeLocal().setLocalAccountantDelegate(delegate);
		}
	}
	

	
	
	
	public void setVoucherType(VoucherType voucherType)
	{
		this.voucherType  = VoucherTypeDAO.sharedInstance().getVoucherType(
				voucherType.getObjectId(),
				new String[] { FetchPlan.DEFAULT,  ProductType.FETCH_GROUP_PRODUCT_TYPE_LOCAL, ProductTypeLocal.FETCH_GROUP_LOCAL_ACCOUNTANT_DELEGATE,VoucherLocalAccountantDelegate.FETCH_GROUP_VOUCHER_LOCAL_ACCOUNTS, VoucherLocalAccountantDelegate.FETCH_GROUP_NAME,Account.FETCH_GROUP_NAME},
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());

		voucherLocalAccountantDelegate = (VoucherLocalAccountantDelegate) this.voucherType.getProductTypeLocal().getLocalAccountantDelegate();
		updateAccounts();
	}


	protected void updateAccounts()
	{
		accountsDelegateMap =  new HashMap<Currency, Account>();
		VoucherLocalAccountantDelegate localAccountantDelegate = (VoucherLocalAccountantDelegate) this.voucherType.getProductTypeLocal().getLocalAccountantDelegate();

		for (Map.Entry<String, Account> me : localAccountantDelegate.getAccounts().entrySet()) {		
			accountsDelegateMap.put(new Currency(me.getKey(),me.getKey(),2), me.getValue());

		}		

		accountantDelegateComposite.setMap(accountsDelegateMap);
	}



	protected void inheritPressed() {
		if( inheritanceAction.isChecked() )
		{
			parentVoucherType =  VoucherTypeDAO.sharedInstance().getVoucherType(
					voucherType.getExtendedProductTypeID(),
					new String[] { FetchPlan.DEFAULT,  ProductType.FETCH_GROUP_PACKAGE_PRICE_CONFIG,FetchGroupsPriceConfig.FETCH_GROUP_EDIT, PriceConfig.FETCH_GROUP_NAME},
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
			voucherType.getProductTypeLocal().setLocalAccountantDelegate(
					parentVoucherType.getProductTypeLocal().getLocalAccountantDelegate());
		}
		else
			voucherType.getProductTypeLocal().setLocalAccountantDelegate(voucherLocalAccountantDelegate);

		voucherType.getFieldMetaData(
				ProductTypeLocal.FieldName.localAccountantDelegate).setValueInherited( 
						!voucherType.getFieldMetaData(ProductTypeLocal.FieldName.localAccountantDelegate).isValueInherited());
		updateAccounts();
		markDirty();
	}



	protected void assignAccountConfigClicked()
	{
		AccountVoucherTypeWizard priceVoucherTypeWizard = new AccountVoucherTypeWizard(voucherType.getExtendedProductTypeID(),this.voucherType);
		DynamicPathWizardDialog wizardDialog = new DynamicPathWizardDialog(priceVoucherTypeWizard);
		if( wizardDialog.open() == Window.OK)
		{
			inheritanceAction.setChecked(voucherType.getFieldMetaData(ProductTypeLocal.FieldName.localAccountantDelegate).isValueInherited());
			updateAccounts();
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
			"AssignPriceConfig")); //$NON-NLS-1$
			setToolTipText("assigns a new account configuration"); 
			setText("assigns a new account configuration");
		}

		@Override
		public void run() {
			assignAccountConfigClicked();
		}
	}




}
