package org.nightlabs.jfire.simpletrade.admin.ui.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.jfire.simpletrade.admin.ui.gridpriceconfig.PriceConfigComposite;
import org.nightlabs.jfire.simpletrade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractGridPriceConfigSection;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SimpleProductTypePriceConfigSection  
extends AbstractGridPriceConfigSection
{
	/**
	 * @param page
	 * @param parent
	 * @param style
	 * @param title
	 */
	public SimpleProductTypePriceConfigSection(IFormPage page, Composite parent, int style) {
		super(page, parent, style, Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.editor.SimpleProductTypePriceConfigSection.title")); //$NON-NLS-1$
	}

	@Override
	protected PriceConfigComposite createPriceConfigComposite(Composite parent) {
		return new PriceConfigComposite(parent, this);
	}
	
}
