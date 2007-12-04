package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import java.util.Collection;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleEvent;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleListener;
import org.nightlabs.jfire.base.ui.jdo.notification.JDOLifecycleAdapterJob;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueDescription;
import org.nightlabs.jfire.issue.IssueFileAttachment;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueSubject;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueTypeDAO;
import org.nightlabs.jfire.issue.id.IssueTypeID;
import org.nightlabs.jfire.jdo.notification.IJDOLifecycleListenerFilter;
import org.nightlabs.jfire.jdo.notification.JDOLifecycleState;
import org.nightlabs.jfire.jdo.notification.SimpleLifecycleListenerFilter;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * This composite lists all {@link IssueType}s of an issue type in a table.
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class IssueTypeTable 
extends AbstractTableComposite<IssueType>{

	/**
	 * The fetch groups of issue data.
	 */
	public static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT, 
		Issue.FETCH_GROUP_THIS,
		IssueType.FETCH_GROUP_THIS,
		IssueDescription.FETCH_GROUP_THIS, 
		IssueSubject.FETCH_GROUP_THIS,
		IssueFileAttachment.FETCH_GROUP_THIS,
		IssueSeverityType.FETCH_GROUP_THIS,
		IssuePriority.FETCH_GROUP_THIS};
	
	public IssueTypeTable(Composite parent, int style)
	{
		super(parent, style);
		
		getTableViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				StructuredSelection s = (StructuredSelection)e.getSelection();
				if (s.isEmpty())
					return;

				IssueType issueType = (IssueType)s.getFirstElement();
				try {
					RCPUtil.openEditor(new IssueTypeEditorInput((IssueTypeID)JDOHelper.getObjectId(issueType)),
							IssueTypeEditor.EDITOR_ID);
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}
		});
		
//		JDOLifecycleManager.sharedInstance().addLifecycleListener(myLifecycleListener);
//	    addDisposeListener(new DisposeListener() {
//	      public void widgetDisposed(DisposeEvent event)
//	      {
//	        JDOLifecycleManager.sharedInstance().removeLifecycleListener(myLifecycleListener);
//	      }
//	    });
	    
	    
	    loadIssueTypes();
	}
	
	private JDOLifecycleListener myLifecycleListener = new JDOLifecycleAdapterJob("Loading Xyz") {
	    private IJDOLifecycleListenerFilter filter = new SimpleLifecycleListenerFilter(
	      Issue.class,
	      true,
	      JDOLifecycleState.NEW);

	    public IJDOLifecycleListenerFilter getJDOLifecycleListenerFilter()
	    {
	      return filter;
	    }

	    public void notify(JDOLifecycleEvent event)
	    {
	      loadIssueTypes();
	    }
	};

	private void loadIssueTypes(){
		final Collection<IssueType> issueTypes = IssueTypeDAO.sharedInstance().getIssueTypes(IssueTypeTable.FETCH_GROUPS,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new NullProgressMonitor());
		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {					
				setIssueTypes(null, issueTypes);
			}
		});
	}
	
	public void setIssueTypes(IssueTypeID currentIssueTypeID, Collection<IssueType> issueTypes)
	{
		super.setInput(issueTypes);
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
		tableViewer.setLabelProvider(new IssueTypeLabelProvider());
	}

	class IssueTypeLabelProvider
	extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex) 
		{
			if (element instanceof IssueType) {
				IssueType issueType = (IssueType) element;
				switch (columnIndex) 
				{
				case(0):
					return issueType.getName().getText();
				default:
					return ""; //$NON-NLS-1$
				}
			}
			return null;
		}		
	}
}
