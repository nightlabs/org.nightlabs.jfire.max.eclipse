package org.nightlabs.jfire.trade.admin.ui.editor;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.action.InheritanceAction;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.priceconfig.IInnerPriceConfig;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.TradeAdminPlugin;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.PriceConfigComposite;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigPage;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigWizard;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractGridPriceConfigSection
extends ToolBarSectionPart
{
	private String orgTitle;

	public AbstractGridPriceConfigSection(IFormPage page, Composite parent, int style) {
		this(page, parent, style, Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractGridPriceConfigSection.title")); //$NON-NLS-1$
	}

	private InheritanceAction inheritanceAction;

	/**
	 * @param page
	 * @param parent
	 * @param style
	 * @param title
	 */
	public AbstractGridPriceConfigSection(IFormPage page, Composite parent,
			int style, String title) {
		super(page, parent, style, title);
		this.orgTitle = title;
		priceConfigComposite = createPriceConfigComposite(getContainer());
		priceConfigComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		AssignNewPriceConfigAction assignNewPriceConfigAction = new AssignNewPriceConfigAction();
		getToolBarManager().add(assignNewPriceConfigAction);
		
		inheritanceAction = new InheritanceAction(){
			@Override
			public void run() {
				inheritPressed();
//				setSelection(!isSelection());
			}
		};
		inheritanceAction.setEnabled(false);
		getToolBarManager().add(inheritanceAction);

		updateToolBarManager();

//		Composite buttonWrapper = new XComposite(getSection(), SWT.NONE,
//				LayoutMode.TOTAL_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL, 2);
//		inheritButton = new InheritanceToggleButton(buttonWrapper);
//		inheritButton.addSelectionListener(new SelectionListener(){
//			public void widgetSelected(SelectionEvent e) {
//				inheritPressed();
//			}
//			public void widgetDefaultSelected(SelectionEvent e) {
//				widgetSelected(e);
//			}
//		});
//
//		Button assignNewPriceConfigButton = new Button(buttonWrapper, SWT.NONE);
//		assignNewPriceConfigButton.setImage(SharedImages.getSharedImage(
//				TradeAdminPlugin.getDefault(), AbstractGridPriceConfigSection.class, "AssignPriceConfig"));
//		assignNewPriceConfigButton.setToolTipText("Assign new Price Configuration");
//		assignNewPriceConfigButton.addSelectionListener(new SelectionListener(){
//			public void widgetSelected(SelectionEvent e) {
//				assignNewPressed();
//			}
//			public void widgetDefaultSelected(SelectionEvent e) {
//				widgetSelected(e);
//			}
//		});
//
//		getSection().setTextClient(buttonWrapper);
	}

//	private InheritanceToggleButton inheritButton = null;
	
	protected abstract PriceConfigComposite createPriceConfigComposite(Composite parent);
	
	private PriceConfigComposite priceConfigComposite = null;
	public PriceConfigComposite getPriceConfigComposite() {
		return priceConfigComposite;
	}

	private volatile Job inheritPressedLoadJob = null;
		
	protected void inheritPressed() {
		if (inheritanceAction.isChecked()) {
			Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractGridPriceConfigSection.job.loadExtendendProductType.name")) { //$NON-NLS-1$
				@Override
				protected IStatus run(ProgressMonitor monitor) throws Exception {
					monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractGridPriceConfigSection.job.loadExtendendProductType.name"), 100); //$NON-NLS-1$
					try {
						ProductType pt = packageProductType;
						if (pt == null)
							return Status.OK_STATUS;

						ProductType parentPT = ProductTypeDAO.sharedInstance().getProductType(
								pt.getExtendedProductTypeID(),
								new String[] {
									javax.jdo.FetchPlan.DEFAULT,
									ProductType.FETCH_GROUP_INNER_PRICE_CONFIG
								},
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								new SubProgressMonitor(monitor, 40)
						);

						if (this != inheritPressedLoadJob)
							return Status.OK_STATUS;

						priceConfigComposite.assignNewPriceConfig(
								parentPT.getInnerPriceConfig(),
								true,
								new SubProgressMonitor(monitor, 60)
						);

						return Status.OK_STATUS;
					} finally {
						monitor.done();
					}
				}
			};
			inheritPressedLoadJob = job;
			job.setPriority(Job.SHORT);
			job.schedule();
		}
		else {
			packageProductType.getFieldMetaData(ProductType.FieldName.innerPriceConfig).setValueInherited(false);
			priceConfigComposite.getDirtyStateManager().markDirty();
		}
	}

	private ProductType packageProductType;

	public void setPackageProductType(ProductType productType)
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("This method must be called on the UI thread!"); //$NON-NLS-1$

		if (productType == null) {
			inheritanceAction.setEnabled(false);
		}
		else {
			inheritanceAction.setChecked(productType.getFieldMetaData(ProductType.FieldName.innerPriceConfig).isValueInherited());
			inheritanceAction.setEnabled(productType.getExtendedProductTypeID() != null);
		}

		priceConfigComposite._setPackageProductType(productType);
		this.packageProductType = productType;
	}

	protected void assignNewPressed()
	{
		AbstractChooseGridPriceConfigWizard wizard = getPriceConfigComposite().createChoosePriceConfigWizard(
				(ProductTypeID) JDOHelper.getObjectId(getPriceConfigComposite().getPackageProductType().getExtendedProductType()));
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
//		dialog.setTitle("Choose Price Configuration");
		int returnType = dialog.open();
		if (returnType == Window.OK) {
			getPriceConfigComposite().assignNewPriceConfig(wizard);
			inheritanceAction.setChecked(wizard.getAbstractChooseGridPriceConfigPage().getAction() == AbstractChooseGridPriceConfigPage.Action.inherit);
			IInnerPriceConfig ipc = getPriceConfigComposite().getPackageProductType().getInnerPriceConfig();
			if (ipc == null)
				getSection().setText(orgTitle);
			else
				getSection().setText(ipc.getName().getText());
			getSection().layout();
			markDirty();
		}
	}
	
	class AssignNewPriceConfigAction
	extends Action
	{
		public AssignNewPriceConfigAction() {
			super();
			setId(AssignNewPriceConfigAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					TradeAdminPlugin.getDefault(), AbstractGridPriceConfigSection.class, "AssignPriceConfig")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractGridPriceConfigSection.AssignNewPriceConfigAction.toolTipText")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractGridPriceConfigSection.AssignNewPriceConfigAction.text")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			assignNewPressed();
		}
	}
}
