/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.birt.report.designer.ui.dialogs.IExpressionProvider;
import org.eclipse.birt.report.designer.ui.dialogs.Operator;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.swt.graphics.Image;
import org.nightlabs.jfire.reporting.JFireReportingHelper;
import org.nightlabs.jfire.reporting.admin.ui.resource.Messages;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class JFireReportingExpressionProvider implements IExpressionProvider {

	public static class Factory implements IAdapterFactory {

		public Factory() {
			System.err.println(Factory.class.getName() + " instantiated"); //$NON-NLS-1$
		}
		
		public Object getAdapter(Object adaptableObject, Class adapterType) {
			if (IExpressionProvider.class.isAssignableFrom(adapterType)) {
				return new JFireReportingExpressionProvider();
			}
			return null;
		}

		public Class[] getAdapterList() {
			return null;
		}
		
	}
	
	public static class ItemCarrier {
		private String itemKey;

		public ItemCarrier(String itemKey) {
			this.itemKey = itemKey;
		}
		
		public String getItemKey() {
			return itemKey;
		}
		
		public String getDisplayText() {
			return identifier2Name.get(itemKey);
		}
		
		public String getInsertText() {
			return identifier2InsertText.get(itemKey);
		}
		
	}
	
	private static Map<String, String> identifier2Name = new HashMap<String, String>();
	private static Map<String, List<ItemCarrier>> category2SubCategories = new HashMap<String, List<ItemCarrier>>();
	private static Map<String, List<ItemCarrier>> subCategory2Elements = new HashMap<String, List<ItemCarrier>>();
	private static Map<String, String> identifier2InsertText = new HashMap<String, String>();
	
	private static final String IDENTIFIER_JFIRE_REPORTING = "JFireReporting"; //$NON-NLS-1$
	private static final String IDENTIFIER_CATEGORY_IMPORTS = "JFireReporting.Imports"; //$NON-NLS-1$
	private static final String IDENTIFIER_CATEGORY_IMPORTS_HELPER = "JFireReporting.Imports.JFireReportingHelper"; //$NON-NLS-1$
	private static final String IDENTIFIER_CATEGORY_HELPER_METHODS = "JFireReporting.HelperMethods"; //$NON-NLS-1$
	private static final String IDENTIFIER_CATEGORY_HELPER_METHODS_DATASET_PARAM = "JFireReporting.HelperMethods.createDataSetParam"; //$NON-NLS-1$
	private static final String IDENTIFIER_CATEGORY_HELPER_METHODS_PERSISTENCE_MANAGER = "JFireReporting.HelperMethods.getPersistenceManager"; //$NON-NLS-1$
	private static final String IDENTIFIER_CATEGORY_HELPER_METHODS_VAR = "JFireReporting.HelperMethods.getVar"; //$NON-NLS-1$
	private static final String IDENTIFIER_CATEGORY_HELPER_METHODS_GET_JDO_OBJECT = "JFireReporting.HelperMethods.getJDOObject"; //$NON-NLS-1$
	private static final String IDENTIFIER_CATEGORY_HELPER_METHODS_LOGGER = "JFireReporting.HelperMethods.getLogger"; //$NON-NLS-1$
	
	public static final ItemCarrier ITEM_JFIRE_REPORTING = new ItemCarrier(IDENTIFIER_JFIRE_REPORTING);
	public static final ItemCarrier CATEGORY_IMPORTS = new ItemCarrier(IDENTIFIER_CATEGORY_IMPORTS);
	public static final ItemCarrier CATEGORY_IMPORTS_HELPER = new ItemCarrier(IDENTIFIER_CATEGORY_IMPORTS_HELPER);
	public static final ItemCarrier CATEGORY_HELPER_METHODS = new ItemCarrier(IDENTIFIER_CATEGORY_HELPER_METHODS);
	public static final ItemCarrier CATEGORY_HELPER_METHODS_DATASET_PARAM = new ItemCarrier(IDENTIFIER_CATEGORY_HELPER_METHODS_DATASET_PARAM);
	public static final ItemCarrier CATEGORY_HELPER_METHODS_PERSISTENCE_MANAGER = new ItemCarrier(IDENTIFIER_CATEGORY_HELPER_METHODS_PERSISTENCE_MANAGER);
	public static final ItemCarrier CATEGORY_HELPER_METHODS_VAR = new ItemCarrier(IDENTIFIER_CATEGORY_HELPER_METHODS_VAR);
	public static final ItemCarrier CATEGORY_HELPER_METHODS_LOGGER = new ItemCarrier(IDENTIFIER_CATEGORY_HELPER_METHODS_LOGGER);
	public static final ItemCarrier CATEGORY_HELPER_METHODS_GET_JDO_OBJECT = new ItemCarrier(IDENTIFIER_CATEGORY_HELPER_METHODS_GET_JDO_OBJECT);
	
	private static boolean indexCreated = false;
	
	private synchronized static void createIndex() {
		if (indexCreated)
			return;
		identifier2Name.put(IDENTIFIER_JFIRE_REPORTING, "JFire Reporting"); //$NON-NLS-1$
		
		addSubCategories(IDENTIFIER_JFIRE_REPORTING, new ItemCarrier[] {
				CATEGORY_IMPORTS, CATEGORY_HELPER_METHODS
			});

		addSubCategoryElements(CATEGORY_IMPORTS, new ItemCarrier[] {
				CATEGORY_IMPORTS_HELPER
			});
		
		addSubCategoryElements(CATEGORY_HELPER_METHODS, new ItemCarrier[] {
				CATEGORY_HELPER_METHODS_DATASET_PARAM,
				CATEGORY_HELPER_METHODS_GET_JDO_OBJECT,
				CATEGORY_HELPER_METHODS_PERSISTENCE_MANAGER, 
				CATEGORY_HELPER_METHODS_VAR,
				CATEGORY_HELPER_METHODS_LOGGER
			});
		
		identifier2InsertText.put(CATEGORY_IMPORTS_HELPER.getItemKey(), "importClass(Packages."+JFireReportingHelper.class.getName()+");"); //$NON-NLS-1$ //$NON-NLS-2$
		
		identifier2InsertText.put(CATEGORY_HELPER_METHODS_DATASET_PARAM.getItemKey(), "JFireReportingHelper.createDataSetParam()"); //$NON-NLS-1$
		identifier2InsertText.put(CATEGORY_HELPER_METHODS_PERSISTENCE_MANAGER.getItemKey(), "JFireReportingHelper.getPersistenceManager()"); //$NON-NLS-1$
		identifier2InsertText.put(CATEGORY_HELPER_METHODS_VAR.getItemKey(), "JFireReportingHelper.getVar()"); //$NON-NLS-1$
		identifier2InsertText.put(CATEGORY_HELPER_METHODS_GET_JDO_OBJECT.getItemKey(), "JFireReportingHelper.getJDOObject()"); //$NON-NLS-1$
		identifier2InsertText.put(CATEGORY_HELPER_METHODS_LOGGER.getItemKey(), "JFireReportingHelper.getLogger()"); //$NON-NLS-1$
		
		identifier2Name.put(CATEGORY_IMPORTS.getItemKey(), Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.JFireReportingExpressionProvider.identifier.import")); //$NON-NLS-1$
		identifier2Name.put(CATEGORY_IMPORTS_HELPER.getItemKey(), Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.JFireReportingExpressionProvider.identifier.reportingHelper")); //$NON-NLS-1$
		identifier2Name.put(CATEGORY_HELPER_METHODS.getItemKey(), Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.JFireReportingExpressionProvider.identifier.helperMethods")); //$NON-NLS-1$
		identifier2Name.put(CATEGORY_HELPER_METHODS_DATASET_PARAM.getItemKey(), Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.JFireReportingExpressionProvider.identifier.createDatasetParameters")); //$NON-NLS-1$
		identifier2Name.put(CATEGORY_HELPER_METHODS_GET_JDO_OBJECT.getItemKey(), Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.JFireReportingExpressionProvider.identifier.resolveJDOObjects")); //$NON-NLS-1$
		identifier2Name.put(CATEGORY_HELPER_METHODS_PERSISTENCE_MANAGER.getItemKey(), Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.JFireReportingExpressionProvider.identifier.getPersistenceManager")); //$NON-NLS-1$
		identifier2Name.put(CATEGORY_HELPER_METHODS_VAR.getItemKey(), Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.JFireReportingExpressionProvider.identifier.getSharedVariables")); //$NON-NLS-1$
		identifier2Name.put(CATEGORY_HELPER_METHODS_LOGGER.getItemKey(), Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.JFireReportingExpressionProvider.identifier.loggerToUse")); //$NON-NLS-1$
		
		indexCreated = true;
	}
	
	private static void addSubCategories(String identifier, ItemCarrier[] subCats) {
		List<ItemCarrier> cats = category2SubCategories.get(identifier);
		if (cats == null) {
			cats = new ArrayList<ItemCarrier>(subCats.length);
			category2SubCategories.put(identifier, cats);
		}
		for (ItemCarrier subCat : subCats) {
			cats.add(subCat);
			identifier2Name.put(subCat.getItemKey(), null);
		}
	}
	
	private static void addSubCategoryElements(ItemCarrier category, ItemCarrier[] elements) {
		List<ItemCarrier> elems = category2SubCategories.get(category);
		if (elems == null) {
			elems = new ArrayList<ItemCarrier>(elements.length);
			subCategory2Elements.put(category.getItemKey(), elems);
		}
		for (ItemCarrier element : elements) {
			elems.add(element);
			identifier2Name.put(element.getItemKey(), null);
		}
	}
	
	private static String getIdentifierName(ItemCarrier identifier) {
		String name = identifier2Name.get(identifier.getItemKey());
		return name != null ? name : null;
	}
	
	private static String getIdentifierInsertText(ItemCarrier identifier) {
		String name = identifier2InsertText.get(identifier.getItemKey());
		return name != null ? name : null;
	}
	
	/**
	 * 
	 */
	public JFireReportingExpressionProvider() {
		createIndex();
	}

	public Object[] getCategory() {
		return new Object[] { ITEM_JFIRE_REPORTING };
	}

	public Object[] getChildren(Object parent) {
		if (parent.equals(ITEM_JFIRE_REPORTING)) {
			return new ArrayList<ItemCarrier>(category2SubCategories.get(IDENTIFIER_JFIRE_REPORTING)).toArray();
		}
		if (parent instanceof ItemCarrier) {
			String parentKey = ((ItemCarrier) parent).getItemKey();
			for (Map.Entry<String, List<ItemCarrier>> subCatEntry : category2SubCategories.entrySet()) {
				if (parentKey.equals(subCatEntry.getKey()))
					return subCatEntry.getValue().toArray();
			}
			for (Map.Entry<String, List<ItemCarrier>> subCatElementEntry : subCategory2Elements.entrySet()) {
				if (parentKey.equals(subCatElementEntry.getKey()))
					return subCatElementEntry.getValue().toArray();
			}
		}
		return new Object[] {};
	}
	
	public String getDisplayText(Object element) {		
		if (element instanceof ItemCarrier)
			return getIdentifierName((ItemCarrier) element);
		return null;
	}
	
	public String getInsertText(Object element) {
		if (element instanceof ItemCarrier)
			return getIdentifierInsertText((ItemCarrier) element);
		return null;
	}

	public Image getImage(Object element) {
		return null;
	}

	public Operator[] getOperators() {
		return null;
	}

	public String getTooltipText(Object element) {
		return getDisplayText(element);
	}

	public boolean hasChildren(Object element) {
		return true;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}
	
}
