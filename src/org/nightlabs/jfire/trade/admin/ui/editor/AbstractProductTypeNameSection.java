package org.nightlabs.jfire.trade.admin.ui.editor;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.action.InheritanceAction;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorTable;
import org.nightlabs.base.ui.language.II18nTextEditor;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 */
public abstract class AbstractProductTypeNameSection
extends ToolBarSectionPart
implements IProductTypeSectionPart
{
	// TODO should be named FETCH_GROUPS_NAME (plural since it is an array) 
	public String[] FETCH_GROUP_NAME = new String[] {FetchPlan.DEFAULT, ProductType.FETCH_GROUP_NAME};

	public AbstractProductTypeNameSection(IFormPage page, Composite parent) {
		this(page, parent, ExpandableComposite.TITLE_BAR);
	}

	public AbstractProductTypeNameSection(IFormPage page, Composite parent, int style) {
		this(page, parent, style, Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeNameSection.text")); //$NON-NLS-1$
	}

	public AbstractProductTypeNameSection(IFormPage page, Composite parent, int style, String title) {
		super(page, parent, style, title);
		productTypeName = new I18nTextEditorTable(getContainer());
		productTypeName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (!ignoreProductTypeNameModify) {
					setInheritanceSelection(false);
					if (productType.getFieldMetaData(ProductType.FieldName.name) != null)
						productType.getFieldMetaData(ProductType.FieldName.name).setValueInherited(false);
				}
				// TODO: fix NullPointerException
				markDirty();
			}
		});

		inheritanceAction = new InheritanceAction(){
			@Override
			public void run() {
				inheritNamePressed();
//				setSelection(!isSelection());
			}
		};
		inheritanceAction.setEnabled(false);
		getToolBarManager().add(inheritanceAction);
		updateToolBarManager();

//		inheritProductTypeName = new InheritanceToggleButton(getSection());
//		inheritProductTypeName.addSelectionListener(new SelectionAdapter() {
//		public void widgetSelected(SelectionEvent e) {
//		inheritNamePressed();
//		}
//		});
//		getSection().setTextClient(inheritProductTypeName);
	}

	private static class ProductTypeHolder {
		public ProductType extendedProductType;
	}

//	private InheritanceToggleButton inheritProductTypeName = null;
	private InheritanceAction inheritanceAction = null;
	private boolean ignoreProductTypeNameModify = false;

	private ProductType productType = null;
	public ProductType getProductType() {
		return productType;
	}

//	public void setProductType(ProductType productType)
//	{
//		if (productType == null || getSection() == null || getSection().isDisposed())
//			return;
//
//		this.productType = productType;
//		if (productType == null) {
//			setInheritanceSelection(false);
//			productTypeName.setI18nText(null);
//		}
//		else {
//			productTypeName.setI18nText(productType.getName(), I18nTextEditor.EditMode.DIRECT);
//			if (productType.getFieldMetaData("name") != null) //$NON-NLS-1$
//				setInheritanceSelection(productType.getFieldMetaData("name").isValueInherited()); //$NON-NLS-1$
//		}
//	}

	//TODO I have to implement those two methods

	private AbstractProductTypePageController<ProductType> productTypePageController;

	public void setProductTypePageController(AbstractProductTypePageController<ProductType> pageController)
	{
		if (pageController == null || getSection() == null || getSection().isDisposed())
			return;

		productTypePageController = pageController; 

		this.productType = pageController.getProductType();
		
		if (productType == null) {
			setInheritanceSelection(false);
			productTypeName.setI18nText(null);
			inheritanceAction.setEnabled(false);
		}
		else {
			productTypeName.setI18nText(productType.getName(), I18nTextEditor.EditMode.DIRECT);
			if (productType.getFieldMetaData(ProductType.FieldName.name) != null)
				setInheritanceSelection(productType.getFieldMetaData(ProductType.FieldName.name).isValueInherited());

			inheritanceAction.setEnabled(productType.getExtendedProductTypeID() != null);
		}
	}

	public AbstractProductTypePageController<ProductType> getProductTypePageController()
	{
		return productTypePageController;
	}

	private II18nTextEditor productTypeName = null;
	protected abstract ProductType retrieveExtendedProductType(ProductType type, ProgressMonitor monitor);

	protected void inheritNamePressed()
	{
//		if (!getInheritanceSelection())
		if (getInheritanceSelection())
			productType.getFieldMetaData(ProductType.FieldName.name).setValueInherited(false); //$NON-NLS-1$
		else {
			if (productType != null) {
				final ProductTypeID productTypeID = (ProductTypeID) JDOHelper.getObjectId(productType);
				Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeNameSection.loadExtendedProductTypeJob.name")) { //$NON-NLS-1$
					@Override
					protected IStatus run(ProgressMonitor monitor) {
						monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeNameSection.loadExtendedProductTypeMonitor.task.name"), 10); //$NON-NLS-1$

						// TODO why do we need the retrieveExtendedProductType(...) method, if we fetch the extended product type here already, anyway???
						ProductType productTypeWithExtended = ProductTypeDAO.sharedInstance().getProductType(
								productTypeID, new String[] {FetchPlan.DEFAULT, ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE},
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								new SubProgressMonitor(monitor, 4)
						);
						final ProductTypeHolder typeHolder = new ProductTypeHolder();
						typeHolder.extendedProductType = productTypeWithExtended.getExtendedProductType();
						if (typeHolder.extendedProductType != null) {
							typeHolder.extendedProductType = retrieveExtendedProductType(typeHolder.extendedProductType, new SubProgressMonitor(monitor, 4));
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									ignoreProductTypeNameModify = true;
									try {
										productTypeName.setI18nText(null);
										productType.getName().copyFrom(typeHolder.extendedProductType.getName());
										productTypeName.setI18nText(productType.getName());
										if (productType.getFieldMetaData("name") != null) //$NON-NLS-1$
											productType.getFieldMetaData("name").setValueInherited(true); //$NON-NLS-1$
										getProgressMonitorWrapper().worked(2);
									} finally {
										ignoreProductTypeNameModify = false;
										markDirty();
										getProgressMonitorWrapper().done();
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
	}

	protected boolean getInheritanceSelection() {
//		return inheritProductTypeName.getSelection();
		return inheritanceAction.isChecked();
	}

	protected void setInheritanceSelection(boolean selection) {
//		inheritProductTypeName.setSelection(selection);
		inheritanceAction.setChecked(selection);
	}
}
