/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui;

import java.util.List;

import org.eclipse.birt.report.designer.ui.dialogs.IExpressionProvider;
import org.eclipse.birt.report.designer.ui.dialogs.Operator;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.swt.graphics.Image;
import org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.JFireReportingExpressionProviderRegistry.ItemCarrier;

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
	
	/**
	 * 
	 */
	public JFireReportingExpressionProvider() {
	}

	public Object[] getCategory() {
		List<ItemCarrier> topLevelCarriers = JFireReportingExpressionProviderRegistry.sharedInstance().getSubItemsForParentId(null);
		if (topLevelCarriers != null)
			return topLevelCarriers.toArray();
		else 
			return new Object[0];
	}

	public Object[] getChildren(Object parent) {
		if (parent instanceof ItemCarrier) {
			String parentId = ((ItemCarrier) parent).getItemId();
			List<ItemCarrier> children = JFireReportingExpressionProviderRegistry.sharedInstance().getSubItemsForParentId(parentId);
			if (children != null)
				return children.toArray();
		}
		return new Object[0];
	}
	
	public String getDisplayText(Object element) {		
		if (element instanceof ItemCarrier)
			return ((ItemCarrier) element).getDisplayText();
		return null;
	}
	
	public String getInsertText(Object element) {
		if (element instanceof ItemCarrier)
			return ((ItemCarrier) element).getInsertText();
		return null;
	}

	public Image getImage(Object element) {
		return null;
	}

	public Operator[] getOperators() {
		return null;
	}

	public String getTooltipText(Object element) {
		if (element instanceof ItemCarrier) {
			return ((ItemCarrier) element).getTooltipText();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		return true;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}
	
}
