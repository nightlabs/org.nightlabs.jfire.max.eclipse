package org.nightlabs.jfire.simpletrade.admin.ui.producttype;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.ModuleException;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.composite.InheritanceToggleButton;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.simpletrade.SimpleTradeManager;
import org.nightlabs.jfire.simpletrade.SimpleTradeManagerUtil;
import org.nightlabs.jfire.simpletrade.admin.ui.resource.Messages;
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;
import org.nightlabs.jfire.store.NestedProductType;
import org.nightlabs.jfire.store.NestedProductTypeMapInheriter;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.producttype.NestedProductTypeTable;
import org.nightlabs.jfire.trade.ui.store.ProductTypeDAO;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.Utils;

public class ProductTypeDetailComposite
extends XComposite
implements ISelectionProvider
{
	private InheritanceToggleButton inheritProductTypeName;
//	private Button inheritProductTypeName;
	private I18nTextEditor productTypeName;
	private ProductType productType = null;

	private InheritanceToggleButton inheritNestedProductTypes;
	private NestedProductTypeTable nestedProductTypeTable;
//	private Button createNestedProductType;
//	private Button removeNestedProductType;
//	private Button editNestedProductType;

	private boolean ignoreProductTypeNameModify = false;

	private static class ProductTypeHolder {
		public ProductType extendedProductType;
	}

	private IDirtyStateManager dirtyStateManager = null;
	public IDirtyStateManager getDirtyStateManager() {
		return dirtyStateManager;
	}
	
	public ProductTypeDetailComposite(Composite parent, int style) {
		this(parent, style, null);
	}
	
	public ProductTypeDetailComposite(Composite parent, int style, IDirtyStateManager dirtyStateManager)
	{
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
		
		this.dirtyStateManager = dirtyStateManager;
		XComposite compositeName = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		compositeName.getGridLayout().numColumns = 2;
		compositeName.getGridData().grabExcessVerticalSpace = false;
		
		Label nameLabel = new Label(compositeName, SWT.NONE);
		nameLabel.setText(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.ProductTypeDetailComposite.nameLabel.text")); //$NON-NLS-1$
		GridData labelData = new GridData(GridData.FILL_HORIZONTAL);
		labelData.horizontalSpan = 2;
		nameLabel.setLayoutData(labelData);		
		
		inheritProductTypeName = new InheritanceToggleButton(compositeName);
		GridData buttonData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING, 
				GridData.VERTICAL_ALIGN_END, true, true);
		inheritProductTypeName.setLayoutData(buttonData);
		
		final IDirtyStateManager dsm = dirtyStateManager;
		productTypeName = new I18nTextEditor(compositeName);
//		productTypeName = new I18nTextEditor(compositeName, "Name");
		productTypeName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e)
			{
				if (!ignoreProductTypeNameModify)
					inheritProductTypeName.setSelection(false);
				
				if (dsm != null)
					dsm.markDirty();
			}
		});

//		inheritProductTypeName = new InheritanceToggleButton(compositeName);
//		inheritProductTypeName = new Button(compositeName, SWT.CHECK);
//		inheritProductTypeName.setText("inherit");
		inheritProductTypeName.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		inheritProductTypeName.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				if (inheritProductTypeName.getSelection()) {
					if (productType != null) {
						final ProductTypeID productTypeID = (ProductTypeID) JDOHelper.getObjectId(productType);
						Job job = new Job(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.ProductTypeDetailComposite.loadExtendedProductTypeJob.name")) { //$NON-NLS-1$
							@Override
							protected IStatus run(ProgressMonitor monitor) throws Exception {
								ProductType productTypeWithExtended = ProductTypeDAO.sharedInstance().getProductType(									
										productTypeID, new String[] {FetchPlan.DEFAULT, ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE}, 
										NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
										monitor);
								final ProductTypeHolder typeHolder = new ProductTypeHolder();
								typeHolder.extendedProductType = productTypeWithExtended.getExtendedProductType();
								if (typeHolder.extendedProductType != null) {
									typeHolder.extendedProductType = (SimpleProductType) ProductTypeDAO.sharedInstance().getProductType(
											(ProductTypeID)JDOHelper.getObjectId(typeHolder.extendedProductType), 
											new String[] {FetchPlan.DEFAULT, ProductType.FETCH_GROUP_NAME}, 
											NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
											monitor);
									Display.getDefault().asyncExec(new Runnable() {
										public void run() {
											ignoreProductTypeNameModify = true;
											try {
												productTypeName.setI18nText(null);
												productType.getName().copyFrom(typeHolder.extendedProductType.getName());
												productTypeName.setI18nText(productType.getName());
												if (productType.getFieldMetaData("name") != null) //$NON-NLS-1$
													productType.getFieldMetaData("name").setValueInherited(true); //$NON-NLS-1$
											} finally {
												ignoreProductTypeNameModify = false;
											}
										}
									});
								}
								return Status.OK_STATUS;
							}
							
						};
						job.schedule();
					}
				}
				else
					productType.getFieldMetaData("name").setValueInherited(false); //$NON-NLS-1$
			}
		});

		XComposite compNestHeader = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		compNestHeader.getGridLayout().numColumns = 2;
		compNestHeader.getGridData().grabExcessVerticalSpace = false;
		compNestHeader.getGridData().verticalAlignment = GridData.VERTICAL_ALIGN_END;
		inheritNestedProductTypes = new InheritanceToggleButton(compNestHeader);
		inheritNestedProductTypes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				inheritNestedProductTypesClicked();
			}
		});
		new Label(compNestHeader, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.ProductTypeDetailComposite.nestedProductTypesLabel.text")); //$NON-NLS-1$
		nestedProductTypeTable = new NestedProductTypeTable(this);
		nestedProductTypeTable.setInput(null);

//		XComposite tableButtonComp = new XComposite(page, SWT.NONE, XComposite.LAYOUT_MODE_ORDINARY_WRAPPER);
//		tableButtonComp.getGridLayout().numColumns = 3;
//		createNestedProductType = new Button(tableButtonComp, SWT.PUSH);
//		createNestedProductType.setText("Add");
//		removeNestedProductType = new Button(tableButtonComp, SWT.PUSH);
//		removeNestedProductType.setText("Remove");
//		editNestedProductType = new Button(tableButtonComp, SWT.PUSH);
//		editNestedProductType.setText("Edit");
	}

	private void inheritNestedProductTypesClicked()
	{		
		if (productType == null)
			return;

		boolean inherited = inheritNestedProductTypes.getSelection();
		if (productType.getFieldMetaData("nestedProductTypes") != null) //$NON-NLS-1$
			productType.getFieldMetaData("nestedProductTypes").setValueInherited(inherited); //$NON-NLS-1$
		if (inherited) {
			Job job = new Job(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.ProductTypeDetailComposite.inheritNestedProductTypesJob.name")) { //$NON-NLS-1$
				@Implement
				protected IStatus run(ProgressMonitor monitor) throws Exception
				{
					try {
						ProductType pt = ProductTypeDAO.sharedInstance().getProductType(
								(ProductTypeID)JDOHelper.getObjectId(productType), 
								new String[] { ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE },
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
								monitor
								);

						ProductType mother = ProductTypeDAO.sharedInstance().getProductType((
								ProductTypeID)JDOHelper.getObjectId(pt.getExtendedProductType()),
								FETCH_GROUPS_SIMPLE_PRODUCT_TYPE,
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								monitor);

						new NestedProductTypeMapInheriter().copyFieldValue(
								mother, productType,
								mother.getClass(), productType.getClass(),
								ProductType.class.getDeclaredField("nestedProductTypes"), //$NON-NLS-1$
								mother.getFieldMetaData("nestedProductTypes"), //$NON-NLS-1$
								productType.getFieldMetaData("nestedProductTypes")); //$NON-NLS-1$
					} catch (Exception x) {
						throw new RuntimeException(x);
					} finally {
						Display.getDefault().asyncExec(new Runnable()
						{
							public void run()
							{
								nestedProductTypeTable.setEnabled(true);
								refreshNestedProductTypes();
							}
						});
					}
					return Status.OK_STATUS;
				}
			};
			job.setPriority(Job.SHORT);
			nestedProductTypeTable.setEnabled(false);
			job.schedule();
		} // if (inherited) {
	}

	protected void setProductType(ProductType productType)
	{
		if (productType != null && !(productType instanceof SimpleProductType))
			return;

		this.productType = productType;

//		publishedCheckBox.setEnabled(productType != null);
//		saleableCheckBox.setEnabled(productType != null);
		if (productType == null) {
			inheritProductTypeName.setSelection(false);
			inheritNestedProductTypes.setSelection(false);
			productTypeName.setI18nText(null);
		}
		else {
			SimpleProductType spt = (SimpleProductType)productType;
			productTypeName.setI18nText(spt.getName());
			if (productType.getFieldMetaData("name") != null) //$NON-NLS-1$
				inheritProductTypeName.setSelection(productType.getFieldMetaData("name").isValueInherited()); //$NON-NLS-1$
			if (productType.getFieldMetaData("nestedProductTypes") != null) //$NON-NLS-1$
				inheritNestedProductTypes.setSelection(productType.getFieldMetaData("nestedProductTypes").isValueInherited()); //$NON-NLS-1$
			// TODO sort nestedProductTypes alphabetically

//			publishedCheckBox.setSelection(productType.isPublished());
//			saleableCheckBox.setSelection(productType.isSaleable());
		}

		nestedProductTypeTable.setInput(productType);
	}

	public ProductType getProductType()
	{
		return productType;
	}

	public void submit()
	throws ModuleException
	{
		try {
			ProductType productType = getProductType();

			if (productType == null)
				return;

			productTypeName.copyToOriginal();
			SimpleProductType spt = (SimpleProductType) productType;
			if (spt.getFieldMetaData("name") != null) //$NON-NLS-1$
				spt.getFieldMetaData("name").setValueInherited(inheritProductTypeName.getSelection()); //$NON-NLS-1$
			SimpleTradeManager stm = SimpleTradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			spt = stm.storeProductType(spt, true, FETCH_GROUPS_SIMPLE_PRODUCT_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
			this.setProductType(spt);
//			ChangeManager.sharedInstance().notify(
//			new NotificationEvent(this, JDOHelper.getObjectId(spt)));
		} catch (RuntimeException x) {
			throw x;
		} catch (ModuleException x) {
			throw x;
		} catch (Exception x) {
			throw new ModuleException(x);
		}
	}

	public static final String[] FETCH_GROUPS_SIMPLE_PRODUCT_TYPE = new String[]{
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_FIELD_METADATA_MAP,
		ProductType.FETCH_GROUP_NAME,
		ProductType.FETCH_GROUP_NESTED_PRODUCT_TYPES,
		NestedProductType.FETCH_GROUP_INNER_PRODUCT_TYPE};

	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		nestedProductTypeTable.addSelectionChangedListener(listener);
	}

	public ISelection getSelection()
	{
		return nestedProductTypeTable.getSelection();
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		nestedProductTypeTable.removeSelectionChangedListener(listener);
	}

	public void setSelection(ISelection selection)
	{
		nestedProductTypeTable.setSelection(selection);
	}

	public void refreshNestedProductTypes()
	{
		nestedProductTypeTable.refresh();
		if (productType != null && productType.getFieldMetaData("nestedProductTypes") != null) //$NON-NLS-1$
			inheritNestedProductTypes.setSelection(productType.getFieldMetaData("nestedProductTypes").isValueInherited()); //$NON-NLS-1$
	}

	public void setProductTypeID(final ProductTypeID productTypeID)
	{
		Job loadJob = new Job(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.ProductTypeDetailComposite.loadProductTypeJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						setProductType(null);
					}
				});
				if (productTypeID == null)
					return Status.OK_STATUS;
				try {
					final ProductType productType = Utils.cloneSerializable(
						ProductTypeDAO.sharedInstance().getProductType(
								productTypeID, FETCH_GROUPS_SIMPLE_PRODUCT_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								monitor));

					if (Thread.currentThread() == Display.getDefault().getThread())
						setProductType(productType);
					else {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								setProductType(productType);
							}
						});
					}
				} catch (Exception x) {
					throw new RuntimeException(x);
				}
				return Status.OK_STATUS;
			}
		};
		loadJob.schedule();
	}

	public void removeSelectedNestedProductTypes()
	{
		if (productType == null)
			return;

		if (productType.getFieldMetaData("nestedProductTypes") != null) //$NON-NLS-1$
			productType.getFieldMetaData("nestedProductTypes").setValueInherited(false); //$NON-NLS-1$
		
		for (NestedProductType nestedProductType : nestedProductTypeTable.getSelectedElements()) {
			productType.removeNestedProductType(
					nestedProductType.getInnerProductTypeOrganisationID(),
					nestedProductType.getInnerProductTypeProductTypeID());
		}

		refreshNestedProductTypes();
	}
}
