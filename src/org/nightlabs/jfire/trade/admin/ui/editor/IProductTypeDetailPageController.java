/**
 * 
 */
package org.nightlabs.jfire.trade.admin.ui.editor;

/**
 * Extension for page controllers for ProductTypes that manage
 * the {@link ProductTypeSaleAccessStatus}. 
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface IProductTypeDetailPageController extends IProductTypePageController {
	
	/**
	 * @return The sale access status of the ProductType this controller currently manages.
	 */
	ProductTypeSaleAccessStatus getProductTypeSaleAccessStatus();
	
	/**
	 * Set the sale access status of the ProductType this controller currently manages.
	 * This will be stored when the controller is aked to save.
	 * 
	 * @param saleAccessStatus The new sale access satus.
	 */
	void setProductTypeSaleAccessStatus(ProductTypeSaleAccessStatus saleAccessStatus);

}
