package org.nightlabs.jfire.trade.admin.ui.editor;


/**
 * An interface for page controllers for entity editor pages that hold a ProductType.
 *   
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 * @param <ProductTypeType> The class of ProductType this controller is used for.
 */
public interface IProductTypePageController<ProductTypeType> {

	ProductTypeType getProductType();
}
