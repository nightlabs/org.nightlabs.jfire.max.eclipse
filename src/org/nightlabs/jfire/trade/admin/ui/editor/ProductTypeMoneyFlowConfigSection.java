package org.nightlabs.jfire.trade.admin.ui.editor;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.action.InheritanceAction;
import org.nightlabs.base.ui.action.SelectionAction;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.TradeAdminPlugin;
import org.nightlabs.jfire.trade.admin.ui.moneyflow.MoneyFlowConfigComposite;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ProductTypeMoneyFlowConfigSection
extends ToolBarSectionPart
{
	private AddDelegateAction addAction;
	private RemoveDelegateAction removeAction;
	private AssignAction assignAction;
	private InheritAction inheritAction;
	private ProductType productType;
	
	/**
	 * @param page
	 * @param parent
	 * @param style
	 * @param title
	 */
	public ProductTypeMoneyFlowConfigSection(IFormPage page,
			Composite parent, int style, String title)
	{
		super(page, parent, style, title);
		this.moneyFlowConfigComposite = new MoneyFlowConfigComposite(getContainer(),
				SWT.NONE, this, false);
		
		getSection().setBackgroundMode(SWT.INHERIT_FORCE);
		getToolBarManager().getControl().setBackgroundMode(SWT.INHERIT_FORCE);
		getToolBarManager().getControl().setBackground(getSection().getTitleBarGradientBackground());
		
		addAction = new AddDelegateAction();
		registerAction(addAction);
		removeAction = new RemoveDelegateAction();
		registerAction(removeAction);
		assignAction = new AssignAction();
		registerAction(assignAction);
		inheritAction = new InheritAction();
		registerAction(inheritAction);
		
		MenuManager menuManager = new MenuManager();
		menuManager.add(addAction);
		menuManager.add(removeAction);
		
		Menu menu = menuManager.createContextMenu(moneyFlowConfigComposite.getProductTypeMappingTree().getTree());
		moneyFlowConfigComposite.getProductTypeMappingTree().getTree().setMenu(menu);
		moneyFlowConfigComposite.getProductTypeMappingTree().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				updateToolBarManager();
			}
		});
		
		updateToolBarManager();
	}

	private MoneyFlowConfigComposite moneyFlowConfigComposite = null;
	public MoneyFlowConfigComposite getMoneyFlowConfigComposite() {
		return moneyFlowConfigComposite;
	}
	
	/**
	 * sets the {@link ProductType}
	 * @param productType the {@link ProductType} to set
	 */
	public void setProductType(ProductType productType)
	{
		this.productType = productType;
		getMoneyFlowConfigComposite().setProductType(productType);
		if (productType.getProductTypeLocal().getLocalAccountantDelegate() != null) {
			getSection().setText(productType.getProductTypeLocal().getLocalAccountantDelegate().getName().getText());
			getSection().layout();
			inheritAction.updateState(productType);
			updateToolBarManager();
		}
	}
	
	class AddDelegateAction
	extends SelectionAction
	{
		public AddDelegateAction() {
			super();
			setId(AddDelegateAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(TradeAdminPlugin.getDefault(),
					ProductTypeMoneyFlowConfigSection.class, "Create")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.ProductTypeMoneyFlowConfigSection.AddDelegateAction.toolTipText")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.ProductTypeMoneyFlowConfigSection.AddDelegateAction.text")); //$NON-NLS-1$
		}
		
		public boolean calculateEnabled() {
			return getMoneyFlowConfigComposite().getCurrDelegate() != null;
		}

		public boolean calculateVisible() {
			return true;
		}

		@Override
		public void run() {
			getMoneyFlowConfigComposite().addDelegate();
		}
	}
	
	class RemoveDelegateAction
	extends SelectionAction
	{
		public RemoveDelegateAction() {
			super();
			setId(RemoveDelegateAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(TradeAdminPlugin.getDefault(),
					ProductTypeMoneyFlowConfigSection.class, "Remove")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.ProductTypeMoneyFlowConfigSection.RemoveDelegateAction.toolTipText")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.ProductTypeMoneyFlowConfigSection.RemoveDelegateAction.text")); //$NON-NLS-1$
		}
		
		public boolean calculateEnabled() {
			return getMoneyFlowConfigComposite().getSelectedLocalAccountantDelegate() != null;
		}

		public boolean calculateVisible() {
			return true;
		}

		@Override
		public void run() {
			getMoneyFlowConfigComposite().removeDelegate();
		}
	}
	
	class AssignAction
	extends SelectionAction
	{
		public AssignAction() {
			super();
			setId(AssignAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(TradeAdminPlugin.getDefault(),
					ProductTypeMoneyFlowConfigSection.class, "AssignNew")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.ProductTypeMoneyFlowConfigSection.AssignAction.toolTipText")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.ProductTypeMoneyFlowConfigSection.AssignAction.text")); //$NON-NLS-1$
		}
		
		public boolean calculateEnabled() {
			return !inheritAction.isChecked();
		}

		public boolean calculateVisible() {
			return true;
		}

		@Override
		public void run() {
			getMoneyFlowConfigComposite().openSelectedAccountDelegateWizard();
			ProductType productType = getMoneyFlowConfigComposite().getProductType();
			if (productType != null && productType.getProductTypeLocal().getLocalAccountantDelegate() != null) {
				getSection().setText(productType.getProductTypeLocal().getLocalAccountantDelegate().getName().getText());
				getSection().layout();
			}
		}
	}

	class InheritAction
	extends InheritanceAction {
		@Override
		public void run() {
			if (productType == null)
				return;
//			boolean newValue = !isSelection();
//			setSelection(newValue);
			updateToolBarManager();
			markDirty();
		}
		
		public void updateState(ProductType productType) {
			setChecked(productType.getProductTypeLocal().getFieldMetaData("localAccountantDelegate").isValueInherited()); //$NON-NLS-1$
		}
	}

	@Override
	public void commit(boolean save) {
		productType.getProductTypeLocal().getFieldMetaData("localAccountantDelegate").setValueInherited(inheritAction.isChecked()); //$NON-NLS-1$
		// delegate itself was already set
		productType.getProductTypeLocal().setLocalAccountantDelegate(moneyFlowConfigComposite.getProductTypeMappingTree().getDelegate());
		super.commit(save);
	}
	
}
