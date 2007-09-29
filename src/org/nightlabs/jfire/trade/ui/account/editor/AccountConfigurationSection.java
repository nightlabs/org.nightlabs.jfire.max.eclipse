/**
 * 
 */
package org.nightlabs.jfire.trade.ui.account.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.editor.MessageSectionPart;
import org.nightlabs.jfire.trade.ui.accounting.AccountConfigurationComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public class AccountConfigurationSection 
//extends ToolBarSectionPart 
extends MessageSectionPart
{
	private AccountConfigurationComposite accountConfigurationComposite;
	
	public AccountConfigurationSection(IFormPage page, Composite parent) 
	{
		super(page, parent, 
				ExpandableComposite.TITLE_BAR,
				Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.AccountConfigurationSection.title")); //$NON-NLS-1$
		accountConfigurationComposite = new AccountConfigurationComposite(
				getContainer(), SWT.NONE, this, true);
		
//		getContainer().setLayout(new TableWrapLayout());		
//		TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB);
//		data.grabVertical = true;
//		data.grabHorizontal = true;
//		data.valign = TableWrapData.FILL;
//		accountConfigurationComposite.setLayoutData(data);
		
//		AddSummaryAccountAction addAction = new AddSummaryAccountAction();
//		getToolBarManager().add(addAction);
//			
//		RemoveSummaryAccountAction removeAction = new RemoveSummaryAccountAction();
//		getToolBarManager().add(removeAction);
//		
//		updateToolBarManager();	
		
		getSection().layout(true, true);
		getSection().pack(true);
	}
	
	public AccountConfigurationComposite getAccountConfigurationComposite() {
		return accountConfigurationComposite;
	}
	
	private ControlListener resizeListener = new ControlListener(){
		public void controlResized(ControlEvent e) {
			getSection().layout(true, true);
			getSection().pack(true);
		}
		public void controlMoved(ControlEvent e) {
		}
	};
	
//	private class AddSummaryAccountAction
//	extends Action
//	{
//		public AddSummaryAccountAction() {
//			super();
//			setText("Add Summary Account");
//			setToolTipText("Add Summary Account");
//			setImageDescriptor(SharedImages.ADD_16x16);
//		}
//		
//		@Override
//		public void run() {
//			accountConfigurationComposite.getAccountContainer().addAccount();
//		}
//	}
//	
//	private class RemoveSummaryAccountAction
//	extends Action
//	{
//		public RemoveSummaryAccountAction() {
//			super();
//			setText("Remove Summary Account");
//			setToolTipText("Remove Summary Account");			
//			setImageDescriptor(SharedImages.DELETE_16x16);
//		}
//		
//		@Override
//		public void run() {
//			accountConfigurationComposite.getAccountContainer().removeAccount();
//		}
//	}	
}
