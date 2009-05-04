package org.nightlabs.jfire.voucher.admin.ui.editor.price;

import java.util.HashMap;
import java.util.Map;

import javax.jdo.FetchPlan;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.action.InheritanceAction;
import org.nightlabs.base.ui.composite.ListComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.book.LocalAccountantDelegate;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeLocal;
import org.nightlabs.jfire.voucher.accounting.VoucherLocalAccountantDelegate;
import org.nightlabs.jfire.voucher.admin.ui.VoucherAdminPlugin;
import org.nightlabs.jfire.voucher.admin.ui.localaccountantdelegate.VoucherLocalAccountantDelegateComposite;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;
import org.nightlabs.jfire.voucher.dao.VoucherTypeDAO;
import org.nightlabs.jfire.voucher.store.VoucherType;
import org.nightlabs.progress.NullProgressMonitor;


/**
 * @author fitas [at] NightLabs [dot] de
 *
 */
public class VoucherAccountConfigSection extends ToolBarSectionPart{


	private InheritanceAction inheritanceAction;

	private VoucherLocalAccountantDelegate selectedLocalAccountantDelegate;

	private ListComposite<VoucherLocalAccountantDelegate> accountantDelegateList;
	private VoucherLocalAccountantDelegateComposite accountantDelegateComposite;
	

	public VoucherAccountConfigSection(IFormPage page, Composite parent, int style) {
		super(page, parent, style, "Account Configuration");

		AddAccountConfigAction addAccountConfigAction = new AddAccountConfigAction();
		getToolBarManager().add(addAccountConfigAction);

		RemoveAccountConfigAction removeAccountConfigAction = new RemoveAccountConfigAction();
		getToolBarManager().add(removeAccountConfigAction);

		AssignAccountConfigAction assignAccountConfigAction = new AssignAccountConfigAction();
		getToolBarManager().add(assignAccountConfigAction);

		inheritanceAction = new InheritanceAction(){
			@Override
			public void run() {
				inheritPressed();
			}
		};
		inheritanceAction.setEnabled(false);
		
		XComposite comp = new XComposite(getContainer(), SWT.NONE);
		StackLayout stackLayout = new StackLayout();
		comp.setLayout(stackLayout);
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		accountantDelegateComposite = new VoucherLocalAccountantDelegateComposite(comp, true);
		//accountantDelegateComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		stackLayout.topControl = accountantDelegateComposite;
		
		updateToolBarManager();

	}


	public void setVoucherType(VoucherType voucherType)
	{
		final VoucherType parentvoucherType  = VoucherTypeDAO.sharedInstance().getVoucherType(
				voucherType.getObjectId(),
				new String[] { FetchPlan.DEFAULT,  ProductType.FETCH_GROUP_PRODUCT_TYPE_LOCAL, ProductTypeLocal.FETCH_GROUP_LOCAL_ACCOUNTANT_DELEGATE,VoucherLocalAccountantDelegate.FETCH_GROUP_VOUCHER_LOCAL_ACCOUNTS, VoucherLocalAccountantDelegate.FETCH_GROUP_NAME,Account.FETCH_GROUP_NAME},
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());

		
		selectedLocalAccountantDelegate = (VoucherLocalAccountantDelegate) parentvoucherType.getProductTypeLocal().getLocalAccountantDelegate();
		updateAccounts();

	}


	protected void updateAccounts()
	{
		HashMap<Currency, Account>map =  new HashMap<Currency, Account>();

		for (Map.Entry<String, Account> me : selectedLocalAccountantDelegate.getAccounts().entrySet()) {		
			map.put(new Currency(me.getKey(),me.getKey(),2), me.getValue());

		}		
		accountantDelegateComposite.setMap(map);
	}



	protected void inheritPressed() {

		markDirty();
	}





	protected void addAccountConfigClicked()
	{
		markDirty();
	}

	protected void removeAccountConfigClicked()
	{
		markDirty();
	}

	protected void assignAccountConfigClicked()
	{

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
			setToolTipText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.editor.price.VoucherPriceConfigSection.AssignPriceConfigActionText"));  //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.editor.price.VoucherPriceConfigSection.AssignPriceConfigActionTooltip")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			assignAccountConfigClicked();
		}
	}

	class AddAccountConfigAction
	extends Action
	{
		public AddAccountConfigAction() {
			super();
			setId(AddAccountConfigAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					VoucherAdminPlugin.getDefault(),
					VoucherPriceConfigSection.class,
			"Add")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.editor.price.VoucherPriceConfigSection.AddCurrencyConfigActionTooltip"));  //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.editor.price.VoucherPriceConfigSection.AddCurrencyConfigActionText")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			addAccountConfigClicked();
		}
	}

	class RemoveAccountConfigAction
	extends Action
	{
		public RemoveAccountConfigAction() {
			super();
			setId(RemoveAccountConfigAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					VoucherAdminPlugin.getDefault(),
					VoucherPriceConfigSection.class,
			"Remove")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.editor.price.VoucherPriceConfigSection.RemoveCurrencyConfigActionTooltip"));  //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.editor.price.VoucherPriceConfigSection.RemoveCurrencyConfigActionText")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			removeAccountConfigClicked();
		}
	}







}
