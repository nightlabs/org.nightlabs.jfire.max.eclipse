package org.nightlabs.jfire.trade.admin.editor;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.action.InheritanceAction;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.store.NestedProductType;
import org.nightlabs.jfire.store.NestedProductTypeMapInheriter;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.TradeAdminPlugin;
import org.nightlabs.jfire.trade.admin.producttype.NestedProductTypeTable;
import org.nightlabs.jfire.trade.admin.resource.Messages;
import org.nightlabs.jfire.trade.store.ProductTypeDAO;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractNestedProductTypeSection 
extends ToolBarSectionPart 
implements IProductTypeSectionPart
{
	/**
	 * @param page
	 * @param parent
	 * @param style
	 * @param title
	 */
	public AbstractNestedProductTypeSection(IFormPage page,
			Composite parent, int style, String title) 
	{
		super(page, parent, style, title);
		XComposite compNestHeader = new XComposite(getContainer(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		compNestHeader.getGridLayout().numColumns = 2;
		compNestHeader.getGridData().grabExcessVerticalSpace = false;
		compNestHeader.getGridData().verticalAlignment = GridData.VERTICAL_ALIGN_END;

		new Label(compNestHeader, SWT.NONE).setText(
				Messages.getString("org.nightlabs.jfire.trade.admin.editor.AbstractNestedProductTypeSection.containedProductTypesLabel.text")); //$NON-NLS-1$
		nestedProductTypeTable = new NestedProductTypeTable(getContainer());
		nestedProductTypeTable.setInput(null);
		nestedProductTypeTable.addSelectionChangedListener(nestesProductTypeTabelListener);

		AddNestedProductTypeAction addNestedProductTypeAction = new AddNestedProductTypeAction();
		getToolBarManager().add(addNestedProductTypeAction);
		
		RemoveNestedProductTypeAction removeNestedProductTypeAction = new RemoveNestedProductTypeAction();
		getToolBarManager().add(removeNestedProductTypeAction);
		
		inheritNestedProductTypesAction = new InheritanceAction(){
			@Override
			public void run() {
				inheritNestedProductTypesClicked();
//				setSelection(!isSelection());
			}
		};
		getToolBarManager().add(inheritNestedProductTypesAction);
		
		MenuManager menuManager = new MenuManager();
		menuManager.add(addNestedProductTypeAction);
		menuManager.add(removeNestedProductTypeAction);
		
		Menu menu = menuManager.createContextMenu(nestedProductTypeTable.getTable());
		nestedProductTypeTable.getTable().setMenu(menu);
		
		updateToolBarManager();
	}

	private ProductType productType = null;
	public ProductType getProductType() {
		return productType;
	}
	public void setProductType(ProductType productType)
	{
		if (productType == null)
			return;

		this.productType = productType;
		if (productType == null) {
			setInheritanceSelection(false);
		}
		else {
			if (productType.getFieldMetaData("nestedProductTypes") != null) //$NON-NLS-1$
				setInheritanceSelection(productType.getFieldMetaData("nestedProductTypes").isValueInherited()); //$NON-NLS-1$
//			 TODO sort nestedProductTypes alphabetically
		}
		nestedProductTypeTable.setInput(productType);
	}

	private InheritanceAction inheritNestedProductTypesAction = null;
	private NestedProductTypeTable nestedProductTypeTable = null;

	public static final String[] FETCH_GROUPS_SIMPLE_PRODUCT_TYPE = new String[]{
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_FIELD_METADATA_MAP,
		ProductType.FETCH_GROUP_NAME,
		ProductType.FETCH_GROUP_NESTED_PRODUCT_TYPES,
		NestedProductType.FETCH_GROUP_INNER_PRODUCT_TYPE};

	private ISelectionChangedListener nestesProductTypeTabelListener = new ISelectionChangedListener(){
		public void selectionChanged(SelectionChangedEvent event) {
//			removeNestedProductTypeButton.setEnabled(!event.getSelection().isEmpty());				
		}
	};

	protected void inheritNestedProductTypesClicked()
	{		
		if (productType == null)
			return;

		boolean inherited = getInheritanceSelection();		
		productType.getFieldMetaData("nestedProductTypes").setValueInherited(inherited); //$NON-NLS-1$
		if (inherited) {
			Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.admin.editor.AbstractNestedProductTypeSection.loadInheritanceDataJob.name")) { //$NON-NLS-1$
				@Implement
				protected IStatus run(ProgressMonitor monitor)
				{
					monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.admin.editor.AbstractNestedProductTypeSection.loadInheritanceDataMonitor.task.name"), 3); //$NON-NLS-1$
					try {
						ProductType pt = ProductTypeDAO.sharedInstance().getProductType(
								(ProductTypeID)JDOHelper.getObjectId(productType), 
								new String[] { ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE },
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
								new SubProgressMonitor(monitor, 1)
						);

						ProductType mother = ProductTypeDAO.sharedInstance().getProductType((
								ProductTypeID)JDOHelper.getObjectId(pt.getExtendedProductType()),
								FETCH_GROUPS_SIMPLE_PRODUCT_TYPE,
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								new SubProgressMonitor(monitor, 1)
						);

						new NestedProductTypeMapInheriter().copyFieldValue(
								mother, productType,
								mother.getClass(), productType.getClass(),
								ProductType.class.getDeclaredField("nestedProductTypes"), //$NON-NLS-1$
								mother.getFieldMetaData("nestedProductTypes"), //$NON-NLS-1$
								productType.getFieldMetaData("nestedProductTypes")); //$NON-NLS-1$
						monitor.worked(1);
						monitor.done();
					} catch (Exception x) {
						monitor.setCanceled(true);
						throw new RuntimeException(x);
					} finally {
						Display.getDefault().asyncExec(new Runnable()
						{
							public void run()
							{
								nestedProductTypeTable.setEnabled(true);
								refreshNestedProductTypes();
								markDirty();
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

	public void refreshNestedProductTypes()
	{
		nestedProductTypeTable.refresh();
		if (productType != null && productType.getFieldMetaData("nestedProductTypes") != null) //$NON-NLS-1$
			setInheritanceSelection(productType.getFieldMetaData("nestedProductTypes").isValueInherited()); //$NON-NLS-1$
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

		if (!nestedProductTypeTable.getSelectedElements().isEmpty())
			markDirty();

		refreshNestedProductTypes();
	}	

	protected abstract void createNestedProductTypeClicked(); 	
	
	class AddNestedProductTypeAction 
	extends Action 
	{		
		public AddNestedProductTypeAction() {
			super();
			setId(AddNestedProductTypeAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					TradeAdminPlugin.getDefault(), 
					AbstractNestedProductTypeSection.class, 
					"Create")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.trade.admin.editor.AbstractNestedProductTypeSection.AddNestedProductTypeAction.toolTipText")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.trade.admin.editor.AbstractNestedProductTypeSection.AddNestedProductTypeAction.text"));	 //$NON-NLS-1$
		}
		
		@Override
		public void run() {
			createNestedProductTypeClicked();
		}		
	}
	
	class RemoveNestedProductTypeAction
	extends Action
	{
		public RemoveNestedProductTypeAction() {
			super();
			setId(RemoveNestedProductTypeAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					TradeAdminPlugin.getDefault(),
					AbstractNestedProductTypeSection.class,
					"Remove")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.trade.admin.editor.AbstractNestedProductTypeSection.RemoveNestedProductTypeAction.toolTipText")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.trade.admin.editor.AbstractNestedProductTypeSection.RemoveNestedProductTypeAction.text")); //$NON-NLS-1$
		}
		
		@Override
		public void run() {
			removeSelectedNestedProductTypes();
		}	
	}

	protected void setInheritanceSelection(boolean inherit) {
		inheritNestedProductTypesAction.setChecked(inherit);
	}

	protected boolean getInheritanceSelection() {
		return inheritNestedProductTypesAction.isChecked();
	}
}
