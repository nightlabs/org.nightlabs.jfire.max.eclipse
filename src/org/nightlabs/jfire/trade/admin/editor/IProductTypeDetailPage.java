/**
 * 
 */
package org.nightlabs.jfire.trade.admin.editor;

import org.eclipse.ui.forms.editor.IFormPage;

/**
 * Interface for entity form pages that edit the details of a ProductType. 
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public interface IProductTypeDetailPage extends IFormPage {
	
	/**
	 * Return the controller associated with this page, 
	 * if the page has a controller associated with that
	 * implements {@link IProductTypePageController}.
	 * If the controller is not approriate, an {@link IllegalStateException} 
	 * will be thrown.  
	 *  
	 * @return The {@link IProductTypeDetailPageController} associated with this page.
	 */
	IProductTypeDetailPageController getProductTypeDetailPageController();
}
