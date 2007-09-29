package org.nightlabs.jfire.trade.admin.editor;

import javax.jdo.JDOHelper;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.action.InheritanceAction;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.accounting.priceconfig.IInnerPriceConfig;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.TradeAdminPlugin;
import org.nightlabs.jfire.trade.admin.gridpriceconfig.PriceConfigComposite;
import org.nightlabs.jfire.trade.admin.gridpriceconfig.wizard.AbstractChooseGridPriceConfigWizard;
import org.nightlabs.jfire.trade.admin.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractGridPriceConfigSection 
extends ToolBarSectionPart 
{
	private String orgTitle;

	public AbstractGridPriceConfigSection(IFormPage page, Composite parent, int style) {
		this(page, parent, style, Messages.getString("org.nightlabs.jfire.trade.admin.editor.AbstractGridPriceConfigSection.title")); //$NON-NLS-1$
	}

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
		
		InheritanceAction inheritanceAction = new InheritanceAction(){
			@Override
			public void run() {
				inheritPressed();
//				setSelection(!isSelection());
			}		
		};
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
		
	protected void inheritPressed() {
		// TODO: implement this
		throw new UnsupportedOperationException("NYI"); //$NON-NLS-1$
	}

	protected void assignNewPressed() 
	{
		AbstractChooseGridPriceConfigWizard wizard = getPriceConfigComposite().createChoosePriceConfigWizard(
				(ProductTypeID) JDOHelper.getObjectId(getPriceConfigComposite().getPackageProductType().getExtendedProductType()));
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
//		dialog.setTitle("Choose Price Configuration");
		int returnType = dialog.open();
		if (returnType == Dialog.OK) {
			getPriceConfigComposite().assignNewPriceConfig(wizard);
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
			setToolTipText(Messages.getString("org.nightlabs.jfire.trade.admin.editor.AbstractGridPriceConfigSection.AssignNewPriceConfigAction.toolTipText")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.trade.admin.editor.AbstractGridPriceConfigSection.AssignNewPriceConfigAction.text")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			assignNewPressed();
		}		
	}
}
