/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;
import org.nightlabs.jfire.reporting.admin.ui.resource.Messages;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class JFireReportingExpressionProviderRegistry extends
		AbstractEPProcessor {

	public static class ItemCarrier {
		private String itemId;
		private String parentId;
		private String displayText;
		private String insertText;
		private String tooltipText;
		
		public ItemCarrier(String itemId) {
			this.itemId = itemId;
		}
		
		public String getItemId() {
			return itemId;
		}
		
		public String getDisplayText() {
			return displayText;
		}
		
		public void setDisplayText(String displayText) {
			this.displayText = displayText;
		}
		
		public String getInsertText() {
			return insertText;
		}
		
		public void setInsertText(String insertText) {
			this.insertText = insertText;
		}
		
		public String getParentId() {
			return parentId;
		}
		
		public void setParentId(String parentId) {
			this.parentId = parentId;
		}
		
		public String getTooltipText() {
			return tooltipText;
		}
		
		public void setTooltipText(String tooltipText) {
			this.tooltipText = tooltipText;
		}
	}
	
	public static final String EXTENSION_POINT_ID = "org.nightlabs.jfire.reporting.admin.ui.expressionProviderEntry"; //$NON-NLS-1$
	
	private static final String ELEMENT_NAME_CATEGORY = "expressionProviderCategory"; //$NON-NLS-1$
	private static final String ELEMENT_NAME_ENTRY = "expressionProviderEntry"; //$NON-NLS-1$

	private Map<String, ItemCarrier> id2Carrier = new HashMap<String, ItemCarrier>();
	private Map<String, List<ItemCarrier>> parentId2Carrier = new HashMap<String, List<ItemCarrier>>();
	
	
	/**
	 * 
	 */
	public JFireReportingExpressionProviderRegistry() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#processElement(org.eclipse.core.runtime.IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	public void processElement(IExtension extension,
			IConfigurationElement element) throws Exception {
		if (element.getName().equals(ELEMENT_NAME_CATEGORY)) {
			ItemCarrier categoryCarrier = new ItemCarrier(element.getAttribute("id")); //$NON-NLS-1$
			categoryCarrier.setDisplayText(element.getAttribute("name")); //$NON-NLS-1$
			categoryCarrier.setParentId(element.getAttribute("parentCategoryId")); //$NON-NLS-1$
			categoryCarrier.setTooltipText(element.getAttribute("tooltip")); //$NON-NLS-1$
			id2Carrier.put(categoryCarrier.getItemId(), categoryCarrier);
		} else if (element.getName().equals(ELEMENT_NAME_ENTRY)) {
			ItemCarrier entryCarrier = new ItemCarrier(element.getAttribute("id")); //$NON-NLS-1$
			entryCarrier.setDisplayText(element.getAttribute("name")); //$NON-NLS-1$
			entryCarrier.setInsertText(element.getAttribute("insertText")); //$NON-NLS-1$
			entryCarrier.setParentId(element.getAttribute("categoryId")); //$NON-NLS-1$
			entryCarrier.setTooltipText(element.getAttribute("tooltip")); //$NON-NLS-1$
			id2Carrier.put(entryCarrier.getItemId(), entryCarrier);
		}
	}
	
	@Override
	public synchronized void process() {
		super.process();
		for (Map.Entry<String, ItemCarrier> entry : id2Carrier.entrySet()) {
			String parentId = entry.getValue().getParentId();
			List<ItemCarrier> subItems = parentId2Carrier.get(parentId);
			if (subItems == null) {
				subItems = new ArrayList<ItemCarrier>();
				parentId2Carrier.put(parentId, subItems);
			}
			subItems.add(entry.getValue());
		}
		for (List<ItemCarrier> subItems : parentId2Carrier.values()) {
			Collections.sort(subItems, new Comparator<ItemCarrier>() {
				@Override
				public int compare(ItemCarrier carrier1, ItemCarrier carrier2) {
					return carrier1.getDisplayText().compareTo(carrier2.getDisplayText());
				}
			});
		}
	}
	
	
	public List<ItemCarrier> getSubItemsForParentId(String parentId) {
		return Collections.unmodifiableList(parentId2Carrier.get(parentId));
	}
	
	private static JFireReportingExpressionProviderRegistry sharedInstance;
	
	public static JFireReportingExpressionProviderRegistry sharedInstance() {
		if (sharedInstance == null) {
			synchronized (JFireReportingExpressionProviderRegistry.class) {
				if (sharedInstance == null) {
					sharedInstance = new JFireReportingExpressionProviderRegistry();
					sharedInstance.process();
				}
			}
		}
		return sharedInstance;
	}

}
