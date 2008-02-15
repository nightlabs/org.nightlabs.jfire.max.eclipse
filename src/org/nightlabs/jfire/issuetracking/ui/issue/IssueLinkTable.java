/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.base.jdo.JDOObjectID2PCClassMap;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandler;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandlerFactory;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandlerFactoryRegistry;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 *
 */
public class IssueLinkTable 
extends AbstractTableComposite<ObjectID>{

	private class LabelProvider extends TableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex == 0) {
				if (element instanceof String) {
					ObjectID objectID = ObjectIDUtil.createObjectID((String) element);
					IssueLinkHandler handler = getIssueLinkHandler(objectID);
					return handler.getLinkObjectImage(objectID);
				}
				
				if (element instanceof ObjectID) {
					ObjectID objectID = (ObjectID)element;
					IssueLinkHandler handler = getIssueLinkHandler(objectID);
					return handler.getLinkObjectImage(objectID);
				}
			}
			return null;
		}
		
		public String getColumnText(Object element, int columnIndex) {
			if (columnIndex == 0) {
				if (element instanceof String) {
					ObjectID objectID = ObjectIDUtil.createObjectID((String) element);
					IssueLinkHandler handler = getIssueLinkHandler(objectID);
					return handler.getLinkObjectDescription(objectID);
				}
				
				if (element instanceof ObjectID) {
					ObjectID objectID = (ObjectID)element;
					IssueLinkHandler handler = getIssueLinkHandler(objectID);
					return handler.getLinkObjectDescription(objectID);
				}
			}
			
			if (columnIndex == 1) {
				return "No Relation";
			}
			
			return "";
		}
	}
	
	public IssueLinkTable(Composite parent, int style) {
		super(parent, style);
	}
	

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setMoveable(true);
		tableColumn.setText("Link object");
		
		tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setMoveable(true);
		tableColumn.setText("Relation");

		WeightedTableLayout layout = new WeightedTableLayout(new int[]{30, 30});
		table.setLayout(layout);
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setLabelProvider(new LabelProvider());
		tableViewer.setContentProvider(new TableContentProvider());
	}
	
	public IssueLinkHandler getIssueLinkHandler(String idStr) {
		return getIssueLinkHandler(ObjectIDUtil.createObjectID(idStr));
	}
	
	public IssueLinkHandler getIssueLinkHandler(ObjectID objectID) {
		Class<?> pcClass = JDOObjectID2PCClassMap.sharedInstance().getPersistenceCapableClass(objectID);
		return getIssueLinkHandler(pcClass);
	}
	
	private Map<Class<?>, IssueLinkHandler> class2IssueLinkHandler = new HashMap<Class<?>, IssueLinkHandler>();	
	
	protected IssueLinkHandler getIssueLinkHandler(Class<?> linkObjectClass) {
		IssueLinkHandler handler = class2IssueLinkHandler.get(linkObjectClass);
		if (handler == null) {
			IssueLinkHandlerFactory factory = null;
			try {
				factory = IssueLinkHandlerFactoryRegistry.sharedInstance().getIssueLinkHandlerFactory(linkObjectClass);
			} catch (EPProcessorException e) {
				throw new RuntimeException(e);
			}
			handler = factory.createIssueLinkHandler();
			class2IssueLinkHandler.put(linkObjectClass, handler);
		}
		return handler;
	}
}
