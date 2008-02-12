package org.nightlabs.jfire.trade.admin.ui.editor;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * An abstract page controller for entity editor pages that holds a ProductType. 
 * 
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 * @param <ProductTypeType> The class of Producttype this controller is used for 
 */
public abstract class AbstractProductTypePageController<ProductTypeType extends ProductType> 
extends ActiveEntityEditorPageController<ProductTypeType>
implements IProductTypePageController<ProductTypeType>
{
	/**
	 * @param editor
	 */
	public AbstractProductTypePageController(EntityEditor editor) {
		super(editor);
		productTypeID = ((ProductTypeEditorInput)editor.getEditorInput()).getJDOObjectID();
	}

	/**
	 * @param editor
	 * @param startBackgroundLoading
	 */
	public AbstractProductTypePageController(EntityEditor editor,
			boolean startBackgroundLoading) 
	{
		super(editor, startBackgroundLoading);
		productTypeID = ((ProductTypeEditorInput)editor.getEditorInput()).getJDOObjectID();
	}

	private ProductTypeID productTypeID;
	/**
	 * returns the productTypeID
	 * @return the productTypeID
	 */
	public ProductTypeID getProductTypeID() {
		return productTypeID;
	}
	/**
	 * sets the productTypeID
	 * @param productTypeID the productTypeID to set
	 */
	public void setProductTypeID(ProductTypeID productTypeID) {
		this.productTypeID = productTypeID;
	}	
	
	private ProductTypeType productType;
	/**
	 * returns the productType
	 * @return the productType
	 */
	public ProductTypeType getProductType() {
		return getControllerObject();
	}
	
	protected void setProductType(ProductTypeType productType) {
		this.productType = productType;
	}
}
