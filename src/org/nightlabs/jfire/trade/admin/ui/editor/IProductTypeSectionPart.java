package org.nightlabs.jfire.trade.admin.ui.editor;

import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.Section;
import org.nightlabs.jfire.store.ProductType;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public interface IProductTypeSectionPart
extends IFormPart
{
	/**
	 * sets the productType to display
	 * @param productType the productType to set
	 */
	void setProductType(ProductType productType);
	
	/**
	 * returns the current displayed/edited productType
	 * @return the productType
	 */
	ProductType getProductType();
	
	/**
	 * returns the section
	 * @return the section
	 */
	Section getSection();	
}
