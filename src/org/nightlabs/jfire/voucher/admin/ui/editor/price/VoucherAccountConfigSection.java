package org.nightlabs.jfire.voucher.admin.ui.editor.price;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.action.InheritanceAction;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.voucher.admin.ui.VoucherAdminPlugin;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;


/**
 * @author fitas [at] NightLabs [dot] de
 *
 */
public class VoucherAccountConfigSection extends ToolBarSectionPart{

	
	private InheritanceAction inheritanceAction;
	
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

		
//		MenuManager menuManager = new MenuManager();
//		menuManager.add(addAccountConfigAction);
//		menuManager.add(removeAccountConfigAction);
//		getContainer().setMenu(menu);
		updateToolBarManager();
	
	
	
	
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
					VoucherAccountConfigSection.class,
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
					VoucherAccountConfigSection.class,
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
