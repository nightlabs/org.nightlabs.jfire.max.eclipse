package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse;

import org.eclipse.jface.action.Action;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.trade.ui.TradePlugin;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ReverseProductAction 
extends Action 
{
	public ReverseProductAction() {
		super("Reverse Product", 
				SharedImages.getSharedImageDescriptor(TradePlugin.getDefault(), ReverseProductAction.class));
	}

	/**
	 * @param text
	 * @param style
	 */
	public ReverseProductAction(String text, int style) {
		super(text, style);
	}

	@Override
	public void run() {
		ReverseProductWizard wizard = new ReverseProductWizard();
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
		dialog.open();
	}

}
