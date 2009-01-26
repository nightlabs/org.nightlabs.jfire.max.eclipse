/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
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
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkItemChangeEvent;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * The composite that has a table for listing {@link IssueLink}.
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 *
 */
public class IssueLinkTable 
extends AbstractTableComposite<IssueLinkTableItem>
{
	private static final Logger logger = Logger.getLogger(IssueLinkTable.class);

	private class LabelProvider extends TableLabelProvider {
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex == 0) {
				if (element instanceof IssueLinkTableItem) {
					IssueLinkTableItem issueLinkTableItem = (IssueLinkTableItem) element;
					IssueLinkHandler<ObjectID, Object> handler = getIssueLinkHandler(issueLinkTableItem.getLinkedObjectID());
					IssueLink issueLink = issueLinkTableItem.getIssueLink();
					if (issueLink == null)
						return null; // TODO we should return an image symbolising that currently data is loaded. issueLinkTableItem.getIssueLink() is only null, if there is currently a Jab running in the background loading data for a newly created IssueLink.

					Object linkedObject = issueLink2LinkedObjectMap.get(issueLink);

					return handler.getLinkedObjectImage(issueLink, linkedObject);
				}
			}
			return null;
		}
		
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof IssueLinkTableItem) {
				IssueLinkTableItem issueLinkTableItem = (IssueLinkTableItem) element;
				IssueLinkHandler<ObjectID, Object> handler = getIssueLinkHandler(issueLinkTableItem.getLinkedObjectID());
				IssueLink issueLink = issueLinkTableItem.getIssueLink();
				Object linkedObject = issueLink2LinkedObjectMap.get(issueLink);

				if (columnIndex == 0) {
					if (issueLink == null)
						return Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTable.table.loading.text"); //$NON-NLS-1$

					return handler.getLinkedObjectName(issueLink, linkedObject);
				}

				if (issueLink == null)
					return ""; //$NON-NLS-1$

				if (columnIndex == 1) {
					return issueLink.getIssueLinkType().getName().getText();

//					IssueLinkType issueLinkType = IssueLinkTypeDAO.sharedInstance().getIssueLinkTypesByLinkClass(Object.class, 
//							new String[]{IssueLinkType.FETCH_GROUP_THIS_ISSUE_LINK_TYPE, FetchPlan.DEFAULT},
//							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//							new NullProgressMonitor()).get(0); 
//					return issueLinkTableItem.getIssueLinkType() == null ? issueLinkType.getName().getText() : issueLinkTableItem.getIssueLinkType().getName().getText();
				}
			}
			return ""; //$NON-NLS-1$
		}
	}
	
	public IssueLinkTable(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * Set the issue to work with. This method is used by editor(page)s in order to have the data-loading-work
	 * to be done by the editor's page-controller. If you don't have a page-controller, but are working in
	 * another situation, you better use {@link #setIssueID(IssueID)}.
	 *
	 * @param issue An instance of <code>Issue</code> which must have at least those fields detached that are referenced by the following fetch-groups:
	 * <ul>
	 *		<li>{@link FetchPlan#DEFAULT}</li>
	 *		<li>{@link Issue#FETCH_GROUP_ISSUE_LINKS}</li>
	 *		<li>{@link IssueLink#FETCH_GROUP_ISSUE_LINK_TYPE}</li>
	 *		<li>{@link IssueLinkType#FETCH_GROUP_NAME}</li>
	 *		<li>{@link IssueLink#FETCH_GROUP_LINKED_OBJECT_CLASS}</li>
	 * </ul>
	 */
	public void setIssue(final Issue issue)
	{
		setIssue(
				IssueID.create(issue.getOrganisationID(), issue.getIssueID()),
				issue
		);
	}

	/**
	 * Set the issue to work with. This method should normally used. It causes this table to load all its
	 * data itself, so you don't need to care about fetch-groups. The IssueID
	 *
	 * @param issueID - the issue's {@link IssueID} used in the table
	 */
	public void setIssueID(final IssueID issueID)
	{
		setIssue(issueID, null);
	}

	private void setIssue(final IssueID $issueID, final Issue $issue)
	{
		Job job = new Job(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTable.job.loadingIssueLinks.text")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						issue = null;
						issueLink2LinkedObjectMap = null;
						setLoadingMessage(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTable.job.loadingIssueLinks.loadingMessage.text")); //$NON-NLS-1$
					}
				});

				int monitorTicksLeft = 100;
				monitor.beginTask(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTable.monitor.loadingIssueLinks.text"), monitorTicksLeft); //$NON-NLS-1$

				// (1) load IssueLinks without linkedObjects but with linkedObjectClasses and linkedObjectIDs
				final Issue _issue;

				// When we use this table in an editor, the Issue instance is loaded by the page-controller
				// and we must not load it again. In this case, $issue will be initialised - otherwise, it's null.
				if ($issue != null) {
					_issue = $issue;
					monitor.worked(40);
				}
				else {
					_issue = IssueDAO.sharedInstance().getIssue(
						$issueID, 
						new String[] {
								FetchPlan.DEFAULT,
								Issue.FETCH_GROUP_ISSUE_LINKS,
								IssueLinkType.FETCH_GROUP_NAME,
								IssueLink.FETCH_GROUP_LINKED_OBJECT_CLASS
						}, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new SubProgressMonitor(monitor, 40));
				}
				monitorTicksLeft -= 40;

				// (2) resolve IssueLinkHandlers and group IssueLinks by IssueLinkHandler
				//     => Map<IssueLinkHandler, Set<IssueLink>>
				Map<IssueLinkHandler<?, ?>, Set<IssueLink>> handler2issueLinks = new HashMap<IssueLinkHandler<?, ?>, Set<IssueLink>>();

				for (IssueLink il : _issue.getIssueLinks()) {
					IssueLinkHandler<?, ?> handler = getIssueLinkHandler(il.getLinkedObjectClass());

					Set<IssueLink> issueLinks = handler2issueLinks.get(handler);
					if (issueLinks == null) {
						issueLinks = new HashSet<IssueLink>();
						handler2issueLinks.put(handler, issueLinks);
					}
					issueLinks.add(il);
				}

				// (3) obtain linked objects via their IssueLinkHandlers
				final Map<IssueLink, Object> _issueLink2LinkedObjectMap = new HashMap<IssueLink, Object>();

				if (handler2issueLinks.isEmpty())
					monitor.worked(monitorTicksLeft);
				else {
					int monitorTick = monitorTicksLeft / handler2issueLinks.keySet().size();

					for (Map.Entry<IssueLinkHandler<?, ?>, Set<IssueLink>> me : handler2issueLinks.entrySet()) {
						IssueLinkHandler<?, ?> handler = me.getKey();
						Set<IssueLink> issueLinks = me.getValue();
						Map<IssueLink, ?> il2loMap = handler.getLinkedObjects(issueLinks, new SubProgressMonitor(monitor, monitorTick));
						_issueLink2LinkedObjectMap.putAll(il2loMap);
					}
				}

				// display data
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						issue = _issue;
						issueLink2LinkedObjectMap = _issueLink2LinkedObjectMap;
						issueLinkTableItems.clear();
						for (Map.Entry<IssueLink, Object> me : _issueLink2LinkedObjectMap.entrySet()) {
							IssueLink issueLink = me.getKey();
							IssueLinkTableItem linkItem = new IssueLinkTableItem(issueLink.getLinkedObjectID(), issueLink.getIssueLinkType());
							linkItem.initIssueLink(issueLink);
							issueLinkTableItems.add(linkItem);
						}
						
						setInput(issueLinkTableItems);
					}
				});
				
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	// only modified on the SWT UI thread!
	private Issue issue;
	// only modified on the SWT UI thread!
	private Set<IssueLinkTableItem> issueLinkTableItems = new HashSet<IssueLinkTableItem>();
	// only modified on the SWT UI thread!
	private Map<IssueLink, Object> issueLink2LinkedObjectMap;

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setMoveable(true);
		tableColumn.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTable.tableColumn.linkObject.text")); //$NON-NLS-1$
		
		tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setMoveable(true);
		tableColumn.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTable.tableColumn.relation.text")); //$NON-NLS-1$

		WeightedTableLayout layout = new WeightedTableLayout(new int[]{30, 30});
		table.setLayout(layout);
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setLabelProvider(new LabelProvider());
		tableViewer.setContentProvider(new TableContentProvider());
	}
	
	public IssueLinkHandler<ObjectID, Object> getIssueLinkHandler(String idStr) {
		return getIssueLinkHandler(ObjectIDUtil.createObjectID(idStr));
	}
	
	public IssueLinkHandler<ObjectID, Object> getIssueLinkHandler(ObjectID objectID) {
		Class<?> pcClass = JDOObjectID2PCClassMap.sharedInstance().getPersistenceCapableClass(objectID);
		return getIssueLinkHandler(pcClass);
	}
	
	private Map<Class<?>, IssueLinkHandler<ObjectID, Object>> class2IssueLinkHandler = new HashMap<Class<?>, IssueLinkHandler<ObjectID, Object>>();	
	
	public synchronized IssueLinkHandler<ObjectID, Object> getIssueLinkHandler(Class<?> linkedObjectClass) {
		IssueLinkHandler<ObjectID, Object> handler = class2IssueLinkHandler.get(linkedObjectClass);
		if (handler == null) {
			IssueLinkHandlerFactory<ObjectID, Object> factory = null;
			try {
				factory = IssueLinkHandlerFactoryRegistry.sharedInstance().getIssueLinkHandlerFactory(linkedObjectClass);
			} catch (EPProcessorException e) {
				throw new RuntimeException(e);
			}
			handler = factory.createIssueLinkHandler();
			class2IssueLinkHandler.put(linkedObjectClass, handler);
		}
		return handler;
	}

	private ListenerList tableItemChangeListeners = new ListenerList();

	public void addIssueLinkTableItemChangeListener(IssueLinkTableItemChangeListener listener) {
		tableItemChangeListeners.add(listener);
	}

	public void removeIssueLinkTableItemChangeListener(IssueLinkTableItemChangeListener listener) {
		tableItemChangeListeners.remove(listener);
	}

	protected void notifyIssueLinkTableItemChangeListeners(IssueLinkItemChangeEvent.ChangeType changeType, Collection<IssueLinkTableItem> items) {
		Object[] listeners = tableItemChangeListeners.getListeners();
		IssueLinkItemChangeEvent evt = new IssueLinkItemChangeEvent(this, changeType, items);
		for (Object l : listeners) {
			if (l instanceof IssueLinkTableItemChangeListener) {
				((IssueLinkTableItemChangeListener) l)
						.issueLinkItemChanged(evt);
			}
		}
	}

	public void addIssueLinkTableItem(Issue issue, IssueLinkTableItem item) {
		this.issue = issue; 
		
		Set<IssueLinkTableItem> items = new HashSet<IssueLinkTableItem>();
		items.add(item);
		addIssueLinkTableItems(items);
	}
	
	/**
	 * Add new instances of {@link IssueLinkTableItem} representing new {@link IssueLink}s that must be created.
	 * Therefore, the {@link IssueLinkTableItem#getIssueLink()} property is not yet assigned (i.e. still returning <code>null</code>).
	 *
	 * @param items the new <code>IssueLinkTableItem</code>
	 */
	public void addIssueLinkTableItems(final Collection<IssueLinkTableItem> items)
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("This method must be called on the SWT UI thread!"); //$NON-NLS-1$

		if (issue == null)
			throw new IllegalStateException("Not yet initialised! Cannot add issue links before this table has loaded its data!"); //$NON-NLS-1$

		if (items.isEmpty())
			return;

		// check whether the new items are really new and have no IssueLink assigned yet
		for (IssueLinkTableItem issueLinkTableItem : items) {
			if (issueLinkTableItem.getIssueLink() != null)
				throw new IllegalArgumentException("issueLinkTableItem.getIssueLink() != null !!! The IssueLink instance must not yet exist!"); //$NON-NLS-1$
		}

		// add them to the table (the LabelProvider will show "Loading data..." as long as there is no IssueLink assigned)
		issueLinkTableItems.addAll(items);
		this.refresh();

		// create IssueLink instances - since the Issue is not thread-safe, we call this method on the UI thread before spawning a job.
		final Map<IssueLinkTableItem, IssueLink> issueLinkTableItem2issueLinkMap = new HashMap<IssueLinkTableItem, IssueLink>();
		for (IssueLinkTableItem issueLinkTableItem : items) {
			IssueLink issueLink = issue.createIssueLink(issueLinkTableItem.getIssueLinkType(), (ObjectID)issueLinkTableItem.getLinkedObjectID(), JDOObjectID2PCClassMap.sharedInstance().getPersistenceCapableClass(issueLinkTableItem.getLinkedObjectID()));
			issueLinkTableItem2issueLinkMap.put(issueLinkTableItem, issueLink);
		}

		Job job = new Job(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTable.job.loadingLinkedObject.text")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception
			{
				int monitorTicksLeft = 100;
				monitor.beginTask(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTable.monitor.loadingLinkedObjects.text"), monitorTicksLeft); //$NON-NLS-1$

				// resolve IssueLinkHandlers and group IssueLinks by IssueLinkHandler
				//     => Map<IssueLinkHandler, Set<IssueLink>>
				Map<IssueLinkHandler<?, ?>, Set<IssueLink>> handler2issueLinks = new HashMap<IssueLinkHandler<?, ?>, Set<IssueLink>>();

				for (Map.Entry<IssueLinkTableItem, IssueLink> me : issueLinkTableItem2issueLinkMap.entrySet()) {
					IssueLinkHandler<?, ?> handler = getIssueLinkHandler(me.getKey().getLinkedObjectID());

					Set<IssueLink> issueLinks = handler2issueLinks.get(handler);
					if (issueLinks == null) {
						issueLinks = new HashSet<IssueLink>();
						handler2issueLinks.put(handler, issueLinks);
					}
					issueLinks.add(me.getValue());
				}

				monitor.worked(5);
				monitorTicksLeft -= 5;

				// obtain linked objects via their IssueLinkHandlers
				final Map<IssueLink, Object> _issueLink2LinkedObjectMap = new HashMap<IssueLink, Object>();
				int monitorTick = monitorTicksLeft / handler2issueLinks.keySet().size();

				for (Map.Entry<IssueLinkHandler<?, ?>, Set<IssueLink>> me : handler2issueLinks.entrySet()) {
					IssueLinkHandler<?, ?> handler = me.getKey();
					Set<IssueLink> issueLinks = me.getValue();

					Map<IssueLink, ?> il2loMap = handler.getLinkedObjects(issueLinks, new SubProgressMonitor(monitor, monitorTick));
					_issueLink2LinkedObjectMap.putAll(il2loMap);
				}

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (issue == null)
							return; // silently return, if setIssueID(...) has been called in the meantime

						issueLink2LinkedObjectMap.putAll(_issueLink2LinkedObjectMap);

						for (Map.Entry<IssueLinkTableItem, IssueLink> me : issueLinkTableItem2issueLinkMap.entrySet()) {
							me.getKey().initIssueLink(me.getValue());
						}

						refresh();

						notifyIssueLinkTableItemChangeListeners(IssueLinkItemChangeEvent.ChangeType.add, items);
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
	}

	public void removeIssueLinkTableItems(Collection<IssueLinkTableItem> items) {
		if (Display.getCurrent() == null)
			throw new IllegalStateException("This method must be called on the SWT UI thread!"); //$NON-NLS-1$

		if (issue == null)
			throw new IllegalStateException("Not yet initialised! Cannot add issue links before this table has loaded its data!"); //$NON-NLS-1$

		Collection<IssueLinkTableItem> removedItems = new HashSet<IssueLinkTableItem>(items.size());
		for (IssueLinkTableItem issueLinkTableItem : items) {
			IssueLink issueLink = issueLinkTableItem.getIssueLink();
			if (issueLink != null) {
				if (issueLinkTableItems.remove(issueLinkTableItem)) {
					issue.removeIssueLink(issueLink);
					issueLink2LinkedObjectMap.remove(issueLink);
					removedItems.add(issueLinkTableItem);
				}
			}
		}

		this.refresh();
		notifyIssueLinkTableItemChangeListeners(IssueLinkItemChangeEvent.ChangeType.remove, removedItems);
	}
	
	public Set<IssueLinkTableItem> getIssueLinkTableItems() {
		return issueLinkTableItems;
	}
}