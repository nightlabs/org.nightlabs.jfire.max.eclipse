/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.base.jdo.JDOObjectID2PCClassMap;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandler;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandlerFactory;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandlerFactoryRegistry;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 *
 */
public class IssueLinkTable 
extends AbstractTableComposite<Object>{

	private class LabelProvider extends TableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex == 0) {
				if (element instanceof IssueLink) {
					IssueLink issueLink = (IssueLink)element;
					IssueLinkHandler handler = getIssueLinkHandler(issueLink.getLinkedObjectID());
					return handler.getLinkedObjectImage();
				}
				if (element instanceof Entry) {
					Entry entry = (Entry)element;
					if (entry.getKey() instanceof ObjectID) {
						IssueLinkHandler handler = getIssueLinkHandler((ObjectID)entry.getKey());
						return handler.getLinkedObjectImage();
					}
				}
			}
			return null;
		}
		
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof IssueLink) {
				IssueLink issueLink = (IssueLink)element;
				if (columnIndex == 0) {
					IssueLinkHandler handler = getIssueLinkHandler(issueLink.getLinkedObjectID());
					return handler.getLinkedObjectName(issueLink.getLinkedObjectID());
				}
				
				if (columnIndex == 1) {
					return issueLink.getIssueLinkType().getName().getText();
				}
			}
			
			if (element instanceof Entry) {
				Entry entry = (Entry)element;
				if (columnIndex == 0) {
					IssueLinkHandler handler = getIssueLinkHandler((ObjectID)entry.getKey());
					return handler.getLinkedObjectName((ObjectID)entry.getKey());
				}
				
				if (columnIndex == 1) {
					IssueLinkType issueLinkType = (IssueLinkType)entry.getValue();
					return issueLinkType == null ? "" : issueLinkType.getName().getText();
				}
			}
			return "";
		}
	}
	
	public IssueLinkTable(Composite parent, int style) {
		super(parent, style);
	}
	
//	public void addIssueLink(IssueLink issueLink)
//	{
//		
//	}

	public void setIssueID(final IssueID issueID)
	{
		Job job = new Job("Loading issue links") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				int monitorTicksLeft = 100;
				monitor.beginTask("Loading issue links", monitorTicksLeft);

				// (1) load IssueLinks without linkedObjects but with linkedObjectClasses and linkedObjectIDs
				Issue issue = IssueDAO.sharedInstance().getIssue(
						issueID, 
						new String[] {
								FetchPlan.DEFAULT,
								Issue.FETCH_GROUP_ISSUE_LINKS,
								IssueLink.FETCH_GROUP_LINKED_OBJECT_CLASS
						}, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new SubProgressMonitor(monitor, 20));
				monitorTicksLeft -= 20;


				// (2) resolve IssueLinkHandlers and group IssueLinks by IssueLinkHandler
				//     => Map<IssueLinkHandlerFactory, Set<IssueLink>>
				Map<IssueLinkHandlerFactory, Set<IssueLink>> handlerFactory2issueLink = 
					new HashMap<IssueLinkHandlerFactory, Set<IssueLink>>();
				
				for (IssueLink il : issue.getIssueLinks()) {
					IssueLinkHandlerFactory factory = IssueLinkHandlerFactoryRegistry.sharedInstance().getIssueLinkHandlerFactory(il.getLinkedObjectClass());

					Set<IssueLink> issueLinks = handlerFactory2issueLink.get(factory);
					if (issueLinks == null) {
						issueLinks = new HashSet<IssueLink>();
						handlerFactory2issueLink.put(factory, issueLinks);
					}
					issueLinks.add(il);
				}

				// (3) obtain linked objects via their IssueLinkHandlers
				final Collection<IssueLink> issueLinks = new HashSet<IssueLink>();
				int monitorTick = monitorTicksLeft / handlerFactory2issueLink.keySet().size();
				for (IssueLinkHandlerFactory factory : handlerFactory2issueLink.keySet()) {
					issueLinks.addAll(factory.createIssueLinkHandler().getLinkedObjects(
							issue.getIssueLinks(), 
							new SubProgressMonitor(monitor, monitorTick)).values());
				}

				// display data
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						setInput(issueLinks);
					}
				});
				
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	public void addIssueLink(IssueLink issueLink)
	{
		
	}

	public void removeIssueLink(IssueLink issueLink)
	{
		
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
