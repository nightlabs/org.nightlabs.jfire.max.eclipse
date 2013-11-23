package org.nightlabs.jfire.dynamictrade.admin.ui.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.jfire.dynamictrade.admin.ui.priceconfig.PriceConfigComposite;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractGridPriceConfigSection;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marco Schulze - Marco at NightLabs dot de
 */
public class DynamicProductTypePriceConfigSection
extends AbstractGridPriceConfigSection
{
	/**
	 * @param page
	 * @param parent
	 * @param style
	 * @param title
	 */
	public DynamicProductTypePriceConfigSection(IFormPage page, Composite parent, int style) {
		super(page, parent, style);
	}

	@Override
	protected PriceConfigComposite createPriceConfigComposite(Composite parent) {
		return new PriceConfigComposite(getContainer(), this);
	}
}
