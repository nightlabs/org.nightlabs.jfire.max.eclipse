/**
 * 
 */
package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;

/**
 * @author Chairat Kongarayawetchakun 
 *
 */
public class IssueSeverityTypeTable 
extends AbstractTableComposite<IssueSeverityType>
{
	public IssueSeverityTypeTable(Composite parent, int style)
	{
		super(parent, style);
		
		getTableViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
//				StructuredSelection s = (StructuredSelection)e.getSelection();
//				if (s.isEmpty())
//					return;
//
//				IssueType issueType = (IssueType)s.getFirstElement();
//				try {
//					RCPUtil.openEditor(new IssueTypeEditorInput((IssueTypeID)JDOHelper.getObjectId(issueType)),
//							IssueTypeEditor.EDITOR_ID);
//				} catch (Exception ex) {
//					throw new RuntimeException(ex);
//				}
			}
		});
		
//		JDOLifecycleManager.sharedInstance().addLifecycleListener(myLifecycleListener);
//	    addDisposeListener(new DisposeListener() {
//	      public void widgetDisposed(DisposeEvent event)
//	      {
//	        JDOLifecycleManager.sharedInstance().removeLifecycleListener(myLifecycleListener);
//	      }
//	    });
	    
	    
//	    loadIssueTypes();
	}
	
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn tc;
		TableLayout layout = new TableLayout();

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText("Name");
		layout.addColumnData(new ColumnWeightData(30));
		
		table.setLayout(layout);
		
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new IssueTypeSeverityTypeLabelProvider());
	}
	
	class IssueTypeSeverityTypeLabelProvider
	extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex) 
		{
			if (element instanceof IssueSeverityType) {
				IssueSeverityType issueSeverityType = (IssueSeverityType) element;
				switch (columnIndex) 
				{
				case(0):
					return issueSeverityType.getIssueSeverityTypeText().getText();
				default:
					return ""; //$NON-NLS-1$
				}
			}
			return null;
		}		
	}
	
}
