package org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.DrillDownAdapter;
import org.nightlabs.base.ui.editor.Editor2PerspectiveRegistry;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.base.ui.tree.TreeContentProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueComment;
import org.nightlabs.jfire.issue.IssueDescription;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.IssueManagerRemote;
import org.nightlabs.jfire.issue.dao.IssueLinkDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issue.id.IssueMarkerID;
import org.nightlabs.jfire.issue.issuemarker.IssueMarker;
import org.nightlabs.jfire.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditor;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditorInput;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Fitas Amine - fitas at nightlabs dot de
 *
 */
public class PersonIssueLinkTreeComposite
extends AbstractTreeComposite
{

	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(LegalEntityPersonIssueLinkTreeView .class);
	private IssueLinkTreeNode rootlegalEntityIssuesLinkNode = null;
	private static final Object[] EMPTY_DATA = new Object[]{};
	private LegalEntity partner = null;
	private Object selectedNode = null;
	private DrillDownAdapter drillDownAdapter;
	private IWorkbenchPartSite site;
	private Collection<IssueMarker> issueMarkers;
	private Collection<Image> iconImages = new ArrayList<Image>();


	private static final String[] FETCH_GROUPS_ISSUESLINK = new String[] {
		FetchPlan.DEFAULT,
		IssueLink.FETCH_GROUP_ISSUE,
		Issue.FETCH_GROUP_ISSUE_TYPE,
		Issue.FETCH_GROUP_SUBJECT,
		Issue.FETCH_GROUP_DESCRIPTION,
		Issue.FETCH_GROUP_ISSUE_COMMENTS,
		Issue.FETCH_GROUP_ISSUE_MARKERS,
		Issue.FETCH_GROUP_ISSUE_LINKS,
		Issue.FETCH_GROUP_DESCRIPTION,
		IssueMarker.FETCH_GROUP_NAME,
		IssueMarker.FETCH_GROUP_ICON_16X16_DATA,
		IssueComment.FETCH_GROUP_TEXT};


	public PersonIssueLinkTreeComposite(Composite parent, int style, IWorkbenchPartSite site)
	{
		super(parent, style);
		this.site = site;
		init();
		JDOLifecycleManager.sharedInstance().addNotificationListener(Issue.class, issueChangeListener);

		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				JDOLifecycleManager.sharedInstance().removeNotificationListener(
						Issue.class, issueChangeListener);
			}
		});

		
		getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			   public void selectionChanged(SelectionChangedEvent event) {
			       // if the selection is empty clear the label
			       if(event.getSelection().isEmpty())
			           return;
					StructuredSelection s = (StructuredSelection)event.getSelection();
					selectedNode = s.getFirstElement();
			   }
			});

		// open up the Issue in the Issue Editor.
		getTreeViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				StructuredSelection s = (StructuredSelection)e.getSelection();

				if (s.isEmpty())
					return;

				Object o = s.getFirstElement();
				if (o instanceof IssueLink)
				{
					Issue issue = ((IssueLink)o).getIssue();
					IssueEditorInput issueEditorInput = new IssueEditorInput(IssueID.create(issue.getOrganisationID(), issue.getIssueID()));
					try {
						Editor2PerspectiveRegistry.sharedInstance().openEditor(issueEditorInput, IssueEditor.EDITOR_ID);
					} catch (Exception e1) {
						throw new RuntimeException(e1);
					}
				}
			}

		}
		);
		
		drillDownAdapter = new DrillDownAdapter(getTreeViewer());
		getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{

		}});
		
		// Load the IssueMarkers references.
		Job job = new Job(Messages.getString("Load the Issue Markers")) {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				IssueManagerRemote imr = null;
				try                 { imr = JFireEjb3Factory.getRemoteBean(IssueManagerRemote.class, Login.getLogin().getInitialContextProperties()); }
				catch (Exception e) { throw new RuntimeException(e); }

				Set<IssueMarkerID> issueMarkerIDs = imr.getIssueMarkerIDs();
				issueMarkers = imr.getIssueMarkers(
						issueMarkerIDs,
						new String[] {FetchPlan.DEFAULT, IssueMarker.FETCH_GROUP_NAME, IssueMarker.FETCH_GROUP_DESCRIPTION, IssueMarker.FETCH_GROUP_ICON_16X16_DATA},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT
				);

				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						createContextMenu();
					}
				});
				
				return Status.OK_STATUS;	
			}
			};
			
			job.setPriority(Job.SHORT);
			job.schedule();			
	}
	
	public Object getSelectedNode() {
		return selectedNode;
	}
	
	private void fillContextMenu(IMenuManager manager) {
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute their actions here
		Set<IssueMarker>selectedIssueMarkers = null;
		// get the already selected Issue Markers
		if(selectedNode!= null && selectedNode instanceof IssueLink)
		{
			Issue issue = ((IssueLink)selectedNode).getIssue();
			selectedIssueMarkers =  issue.getIssueMarkers();	
		}

		for(IssueMarker issueMarker:issueMarkers)
		{

			AddIssueMarkerMenuAction addIssueMarkerMenuAction = new AddIssueMarkerMenuAction(); 
			addIssueMarkerMenuAction.init(this, issueMarker);
			manager.add(addIssueMarkerMenuAction);
			if(selectedIssueMarkers!= null)
				addIssueMarkerMenuAction.setChecked(selectedIssueMarkers.contains(issueMarker));	

		}
	}

	private void createContextMenu() {
		MenuManager menuMgr = new MenuManager("Markers");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(getTreeViewer().getControl());
		
	    
		getTreeViewer().getControl().setMenu(menu);
		site.registerContextMenu(menuMgr, getTreeViewer());
	}
	

	private NotificationListener issueChangeListener = new NotificationAdapterJob() {
		public void notify(NotificationEvent evt) {
			logger.info("changeListener got notified with event "+evt); //$NON-NLS-1$
			ProgressMonitor monitor = getProgressMonitor();
			monitor.beginTask(Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person.PersonIssueLinkTreeComposite.task.refreshNodes"), 100); //$NON-NLS-1$
			setRootNode(partner, new SubProgressMonitor(monitor, 100));}		
	};

	private class ContentProvider extends TreeContentProvider {

		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		/**
		 * @see org.nightlabs.base.ui.tree.TreeContentProvider#getChildren(java.lang.Object)
		 */
		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof IssueLinkTreeNode)
				return ((IssueLinkTreeNode)parentElement).getChildNodes();
			if (parentElement instanceof IssueLink)
			{
				final List<IssueComment> commentsList = ((IssueLink)parentElement).getIssue().getComments();
				Collections.sort(commentsList, new Comparator<IssueComment>() {
					public int compare(IssueComment o1, IssueComment o2) {
						// chronological Order
						return o1.getCreateTimestamp().compareTo(o2.getCreateTimestamp());
					}
				});

				// for empty string description no need to add the desc node
				if(((IssueLink)parentElement).getIssue().getDescription().getText().isEmpty())
					return	commentsList.toArray();
				// concatnate the Comments nodes with the Desc
				Object[] arrayDesc = new Object[] {((IssueLink)parentElement).getIssue().getDescription()};
				return concat(arrayDesc,commentsList.toArray());
			}
			return EMPTY_DATA;
		}

		protected Object[] concat(Object[] object, Object[] more) {
			Object[] both = new Object[object.length + more.length];
			System.arraycopy(object, 0, both, 0, object.length);
			System.arraycopy(more, 0, both, object.length, more.length);
			return both;
		}


		/**
		 * @see org.nightlabs.base.ui.tree.TreeContentProvider#hasChildren(java.lang.Object)
		 */
		@Override
		public boolean hasChildren(Object element) {

			if (element instanceof IssueLinkTreeNode)
				return ((IssueLinkTreeNode)element).hasChildren();
			if (element instanceof IssueLink)
				return ((IssueLink)element).getIssue().getComments().size() > 0||
				!((IssueLink)element).getIssue().getDescription().getText().isEmpty();
				if (element instanceof IssueComment||element instanceof IssueDescription)
					return false;
				else
					return true;
		}

		@Override
		public void dispose() {
			for (Image image : iconImages)
				image.dispose();
			iconImages.clear();
		}

	}

	private static Dimension ISSUE_MARKER_IMAGE_DIMENSION = new Dimension(16, 16);

	private class LabelProvider extends TableLabelProvider {
		public String getColumnText(Object element, int columnIndex) {

			if(columnIndex==0)
				return getText(element);
			else
				return ""; //$NON-NLS-1$
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if (element instanceof IssueLinkTreeNode&&columnIndex==0)
				return ((IssueLinkTreeNode)element).getIcon();
			if (element instanceof IssueLink&&columnIndex==1)
				return getCombiIssueMarkerImage(((IssueLink)element).getIssue());
			return null;
		}

		protected Image getCombiIssueMarkerImage(Issue issue)
		{
			int maxIssueMarkerCountPerIssue;
			if (issue.getIssueMarkers().size() > 0)
				maxIssueMarkerCountPerIssue = issue.getIssueMarkers().size();
			else
				return null;
			Image combinedIcon = new Image(getDisplay(),
					ISSUE_MARKER_IMAGE_DIMENSION.width * maxIssueMarkerCountPerIssue + maxIssueMarkerCountPerIssue - 1,
					ISSUE_MARKER_IMAGE_DIMENSION.height);
			GC gc = new GC(combinedIcon);
			Image icon = null;
			int i = 0;
			try {
				for(IssueMarker issueMarker:issue.getIssueMarkers()) {
					if (issueMarker.getIcon16x16Data() != null) {
						ByteArrayInputStream in = new ByteArrayInputStream(issueMarker.getIcon16x16Data());
						icon = new Image(getDisplay(), in);
						gc.drawImage(icon, ISSUE_MARKER_IMAGE_DIMENSION.width * i + i, 0);
						iconImages.add(icon);
					}
					i++;
				}
			} finally {
				gc.dispose();
			}
			iconImages.add(combinedIcon);
			return combinedIcon;
		}


		/**
		 * @see org.nightlabs.base.ui.table.TableLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if (element instanceof IssueLinkTreeNode)
				return ((IssueLinkTreeNode)element).getName();
			if (element instanceof IssueLink)
				return String.format(Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person.PersonIssueLinkTreeComposite.2"),((IssueLink)element).getIssue().getIssueIDAsString(), //$NON-NLS-1$
						((IssueLink)element).getIssue().getSubject().getText());
			if (element instanceof IssueComment)
				return ((IssueComment)element).getText();
			if (element instanceof IssueDescription)
				return ((IssueDescription)element).getText();

			return ""; //$NON-NLS-1$
		}
	}


	public void setRootNode(LegalEntity partner, ProgressMonitor monitor)
	{
		this.partner = partner;

		final ObjectID personID = (ObjectID) JDOHelper.getObjectId(partner.getPerson()); // <-- FIXME Problem here triggered from the IssueTracking perspective...

		final Collection<IssueLink> links = IssueLinkDAO.sharedInstance().getIssueLinksByOrganisationIDAndLinkedObjectID(partner.getOrganisationID(),
				personID,
				FETCH_GROUPS_ISSUESLINK,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 80)
		);

		this.rootlegalEntityIssuesLinkNode = new IssueLinkTreeNode(Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person.PersonIssueLinkTreeComposite.node.name"), null) //$NON-NLS-1$
		{
			@Override
			public Object[] getChildNodes() {
				return links.toArray();
			}
			@Override
			public boolean hasChildren() {
				return !links.isEmpty();
			}

		};

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				getTreeViewer().setInput(rootlegalEntityIssuesLinkNode);
				if(selectedNode!=null)
					getTreeViewer().expandToLevel(selectedNode,1);
				else
					getTreeViewer().expandToLevel(1);
			}
		});
		monitor.worked(20);
	}

	/**
	 * @see org.nightlabs.base.ui.tree.AbstractTreeComposite#setTreeProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	@Override
	public void setTreeProvider(TreeViewer treeViewer) {
		treeViewer.setContentProvider(new ContentProvider());
		treeViewer.setLabelProvider(new LabelProvider());
		if (rootlegalEntityIssuesLinkNode != null) {
			treeViewer.setInput(rootlegalEntityIssuesLinkNode);
		}
	}

	@Override
	public void createTreeColumns(Tree tree) {
		TreeColumn column = new TreeColumn(tree, SWT.RIGHT);
		column.setAlignment(SWT.RIGHT);
		column.setResizable(false);
		TableLayout l = new TableLayout();
		l.addColumnData(new ColumnWeightData(3));
		l.addColumnData(new ColumnWeightData(1));
		tree.setLayout(l);
		getTree().setHeaderVisible(false);
		getTree().getColumn(0).setResizable(false);
	}

}


