package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ReverseProductWizard 
extends DynamicPathWizard 
{
	private ReverseProductPage reverseProductPage;
	
	public ReverseProductWizard() {
		setWindowTitle("Reverse Product");
	}

	@Override
	public void addPages() {
		reverseProductPage = new ReverseProductPage();
		addPage(reverseProductPage);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

}
