/**
 * 
 */
package org.nightlabs.jfire.trade.admin.ui.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.registry.EditorRegistry;
import org.nightlabs.base.ui.search.AbstractSearchResultActionHandler;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.editor.ProductTypeEditorInput;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class GenericProductTypeSearchAdminActionHandler 
extends AbstractSearchResultActionHandler 
{
	private static final Logger logger = Logger.getLogger(GenericProductTypeSearchAdminActionHandler.class);

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.search.ISearchResultActionHandler#run()
	 */
	@Override
	public void run() {
		Collection<ProductType> selectedObjects = getSearchResultProvider().getSelectedObjects();
		if (selectedObjects != null) 
		{
			Collection<ProductTypeID> objectIDs = new ArrayList<ProductTypeID>(selectedObjects.size());
			for (ProductType productType : selectedObjects) {
				objectIDs.add((ProductTypeID) JDOHelper.getObjectId(productType));
 			}
			
			Map<ProductTypeID, String> productTypeID2EditorID = new HashMap<ProductTypeID, String>(selectedObjects.size());
			IEditorRegistry editorRegistry = PlatformUI.getWorkbench().getEditorRegistry();
			if (editorRegistry instanceof EditorRegistry) {
				EditorRegistry registry = (EditorRegistry) editorRegistry;
				IEditorDescriptor[] descriptors = registry.getSortedEditorsFromPlugins();
				for (IEditorDescriptor descriptor : descriptors) {
					IEditorMatchingStrategy strategy = descriptor.getEditorMatchingStrategy();
					if (strategy != null) {
						for (ProductTypeID productTypeID : objectIDs) {
							if (strategy.matches(null, new ProductTypeEditorInput(productTypeID))) {
								productTypeID2EditorID.put(productTypeID, descriptor.getId());
							}
						}
					}
//					if (descriptor instanceof EditorDescriptor) {
//						EditorDescriptor desc = (EditorDescriptor) descriptor;
//						try {
//							IEditorPart editorPart = desc.createEditor();
//							if (editorPart instanceof AbstractProductTypeAdminEditor) {
//								AbstractProductTypeAdminEditor adminEditor = (AbstractProductTypeAdminEditor) editorPart;
//								Class<? extends ProductType> productTypeClass = adminEditor.getProductTypeClass();								
//								for (ProductTypeID productTypeID : objectIDs) {
//									Class<?> clazz = JDOObjectID2PCClassMap.sharedInstance().getPersistenceCapableClass(productTypeID);
//									if (clazz.equals(productTypeClass)) {
//										productTypeID2EditorID.put(productTypeID, descriptor.getId());
//									}
//								}
//							}
//						} catch (CoreException e) {
//							logger.error("Could not create editorPart with id "+descriptor.getId(), e);
//						}
//					}
				}
			}

			for (Map.Entry<ProductTypeID, String> entry : productTypeID2EditorID.entrySet()) {
				try {					
					RCPUtil.openEditor(new ProductTypeEditorInput(entry.getKey()), entry.getValue());
				} catch (PartInitException e) {
					throw new RuntimeException(e);
				}				
			}
		}
	}

}
