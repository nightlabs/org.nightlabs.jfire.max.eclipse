package org.nightlabs.jfire.voucher.admin.ui.editor.price;


import java.util.HashMap;
import java.util.Map;
import javax.jdo.FetchPlan;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.action.InheritanceAction;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.priceconfig.FetchGroupsPriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.PriceConfig;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.voucher.accounting.VoucherPriceConfig;
import org.nightlabs.jfire.voucher.admin.ui.VoucherAdminPlugin;
import org.nightlabs.jfire.voucher.admin.ui.priceconfig.CurrencyAmountTable;
import org.nightlabs.jfire.voucher.admin.ui.priceconfig.IPriceConfigValueChangedListener;
import org.nightlabs.jfire.voucher.dao.VoucherTypeDAO;
import org.nightlabs.jfire.voucher.store.VoucherType;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author fitas [at] NightLabs [dot] de
 *
 */

public class VoucherPriceConfigSection 
extends ToolBarSectionPart 
{
	private CurrencyAmountTable currencyAmountTableWrapper;


	private VoucherPriceConfig originalVoucherConfig;
	private VoucherType voucherType;
	private VoucherType parentVoucherType;
	private InheritanceAction inheritanceAction;

	private Composite stackWrapper;
	private StackLayout stackLayout;
	private Composite assignNewPriceConfigWrapper = null;


	/**
	 * @param page
	 * @param parent
	 * @param style
	 */
	public VoucherPriceConfigSection(IFormPage page, Composite parent, int style) {
		super(page, parent, style, "Price Config");




		AddCurrencyConfigAction addCurrencyConfigAction = new AddCurrencyConfigAction();
		getToolBarManager().add(addCurrencyConfigAction);


		RemoveCurrencyConfigAction removeCurrencyConfigAction = new RemoveCurrencyConfigAction();
		getToolBarManager().add(removeCurrencyConfigAction);

		AssignPriceConfigAction assignPriceConfigAction = new AssignPriceConfigAction();
		getToolBarManager().add(assignPriceConfigAction);



		inheritanceAction = new InheritanceAction(){
			@Override
			public void run() {
				inheritPressed();
			}		
		};


		getToolBarManager().add(inheritanceAction);



		stackWrapper = new XComposite(getContainer(), SWT.NONE);
		stackLayout = new StackLayout();
		stackWrapper.setLayout(stackLayout);
		stackWrapper.setLayoutData(new GridData(GridData.FILL_BOTH));		


		currencyAmountTableWrapper = new CurrencyAmountTable(stackWrapper,false);


		assignNewPriceConfigWrapper = new XComposite(stackWrapper, SWT.NONE, 
				LayoutMode.TOTAL_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL, 2);


		Button assignNewPriceConfigButton = new Button(assignNewPriceConfigWrapper, SWT.NONE);
		//assignNewPriceConfigButton.setImage(SharedImages.getSharedImage(
		//		TradeAdminPlugin.getDefault(), AbstractGridPriceConfigSection.class, "AssignPriceConfig"));
		assignNewPriceConfigButton.setToolTipText("Assign new Price Configuration");		
		assignNewPriceConfigButton.setText("Assign new Price Configuration");
		assignNewPriceConfigButton.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				assignPriceConfigPressed();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}		
		});


		currencyAmountTableWrapper.addPriceConfigValueChangedListener(new IPriceConfigValueChangedListener() {
			public void priceValueChanged()
			{
				//MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "test", "test");
				markDirty();
			}
		});



		stackLayout.topControl = currencyAmountTableWrapper;


		MenuManager menuManager = new MenuManager();
		menuManager.add(addCurrencyConfigAction);
		menuManager.add(removeCurrencyConfigAction);


		Menu menu = menuManager.createContextMenu(currencyAmountTableWrapper.getTable());


		getContainer().setMenu(menu);

		updateToolBarManager();



	}

	@Override
	public void commit(boolean onSave) {
		super.commit(onSave);


		if (getVoucherPriceConfig() != null)
		{

			Map<Currency, Long> map = currencyAmountTableWrapper.getMap();


			VoucherPriceConfig actualVoucherConfig = getVoucherPriceConfig();


			for (Map.Entry<Currency, Long> me : map.entrySet()) {
				actualVoucherConfig.setPrice(me.getKey(), me.getValue());
			}

		}

	}

	protected void switchtoNewAssignPriceConfigPage()
	{

		stackLayout.topControl = assignNewPriceConfigWrapper;
		stackWrapper.layout(true, true);

	}

	protected void switchtoEditPriceConfigPage()
	{

		stackLayout.topControl = currencyAmountTableWrapper;
		stackWrapper.layout(true, true);
	}



	protected void inheritPressed() {

		if( inheritanceAction.isChecked() )
		{


			parentVoucherType =  VoucherTypeDAO.sharedInstance().getVoucherType(
					voucherType.getExtendedProductTypeID(),
					new String[] { FetchPlan.DEFAULT,  ProductType.FETCH_GROUP_PACKAGE_PRICE_CONFIG,FetchGroupsPriceConfig.FETCH_GROUP_EDIT, PriceConfig.FETCH_GROUP_NAME},
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());


			voucherType.setPackagePriceConfig(parentVoucherType.getPackagePriceConfig());


		}
		else
			voucherType.setPackagePriceConfig(originalVoucherConfig);


		voucherType.getFieldMetaData("packagePriceConfig").setValueInherited( !voucherType.getFieldMetaData("packagePriceConfig").isValueInherited());

		updatePricesTable();

		markDirty();


	}



	protected VoucherPriceConfig getVoucherPriceConfig()
	{
		if (voucherType.getPackagePriceConfig() == null)
			return null;

		if(voucherType.getPackagePriceConfig() instanceof VoucherPriceConfig)
		{			
			VoucherPriceConfig	voucherConfigPrice = (VoucherPriceConfig) voucherType.getPackagePriceConfig();	
			return voucherConfigPrice;
		}
		else
			throw new IllegalStateException("PriceConfig is not an instance of VoucherPriceConfig");

	}


	public void setVoucherType(VoucherType voucher)
	{
		voucherType = voucher;

		originalVoucherConfig = (VoucherPriceConfig) voucher.getPackagePriceConfig();

		updatePricesTable();

		inheritanceAction.setChecked(voucherType.getFieldMetaData("packagePriceConfig").isValueInherited());

	}





	protected void updatePricesTable()
	{
		// check fro null
		// if null show the new assign config page 

		if(getVoucherPriceConfig()== null)	
		{
			switchtoNewAssignPriceConfigPage();
			return;
		}
		else
			switchtoEditPriceConfigPage();


		Map<Currency, Long> map = new HashMap<Currency, Long>(getVoucherPriceConfig().getPrices());

		currencyAmountTableWrapper.setMap(map);


	}


	protected void addCurrencyPressed() 
	{

		currencyAmountTableWrapper.addCurrency();
		markDirty();
	}



	protected void removeCurrencyPressed() 
	{

		currencyAmountTableWrapper.removeCurrency();
		markDirty();
	}






	protected void assignPriceConfigPressed() 
	{


		PriceVoucherTypeWizard priceVoucherTypeWizard = new PriceVoucherTypeWizard(voucherType.getExtendedProductTypeID() , voucherType);


		DynamicPathWizardDialog wizardDialog = new DynamicPathWizardDialog(
				Display.getDefault().getActiveShell(), 
				priceVoucherTypeWizard);

		if( wizardDialog.open() == Window.OK) 
		{		

			inheritanceAction.setChecked(voucherType.getFieldMetaData("packagePriceConfig").isValueInherited());

			updatePricesTable();

			markDirty();
		}

	}

	class AssignPriceConfigAction
	extends Action 
	{
		public AssignPriceConfigAction() {
			super();
			setId(AssignPriceConfigAction.class.getName());

			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					VoucherAdminPlugin.getDefault(),
					VoucherPriceConfigSection.class,
			"AssignPriceConfig")); //$NON-NLS-1$


			setToolTipText("Assign Price Config"); 
			setText("Assign Price Config");
		}

		public void run() {

			assignPriceConfigPressed();

		}		
	}







	class AddCurrencyConfigAction
	extends Action 
	{
		public AddCurrencyConfigAction() {
			super();
			setId(AddCurrencyConfigAction.class.getName());

			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					VoucherAdminPlugin.getDefault(),
					VoucherPriceConfigSection.class,
			"Add")); //$NON-NLS-1$

			setToolTipText("Add new Currency to the List"); 
			setText("Add Currency");
		}

		public void run() {

			addCurrencyPressed(); 

		}		
	}



	class RemoveCurrencyConfigAction
	extends Action 
	{
		public RemoveCurrencyConfigAction() {
			super();
			setId(RemoveCurrencyConfigAction.class.getName());

			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					VoucherAdminPlugin.getDefault(),
					VoucherPriceConfigSection.class,
			"Remove")); //$NON-NLS-1$

			setToolTipText("remove the Currency from the List"); 
			setText("Remove Currency");
		}

		@Override
		public void run() {

			removeCurrencyPressed(); 

		}		
	}







}

