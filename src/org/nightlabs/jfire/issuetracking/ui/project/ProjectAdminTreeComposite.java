package org.nightlabs.jfire.issuetracking.ui.project;

import java.util.LinkedList;
import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.DrillDownAdapter;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.base.ui.jdo.tree.ActiveJDOObjectTreeComposite;
import org.nightlabs.jfire.base.ui.jdo.tree.ActiveJDOObjectTreeController;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOObjectTreeContentProvider;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOObjectTreeLabelProvider;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOTreeNodesChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOTreeNodesChangedEventHandler;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.id.ProjectID;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.issue.ActiveProjectTreeController;
import org.nightlabs.jfire.issuetracking.ui.project.create.CreateProjectAction;
import org.nightlabs.jfire.issuetracking.ui.project.create.CreateSubProjectAction;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 *
 */
public class ProjectAdminTreeComposite
extends ActiveJDOObjectTreeComposite<ProjectID, Project, ProjectTreeNode>
{
	private static String[] FETCH_GROUPS = new String[]{
		FetchPlan.DEFAULT, Project.FETCH_GROUP_NAME, Project.FETCH_GROUP_SUBPROJECTS, Project.FETCH_GROUP_PARENT_PROJECT
	};

	protected static class ProjectAdminTreeContentProvider
	extends JDOObjectTreeContentProvider<ProjectID, Project, ProjectTreeNode>
	{
		@Override
		public boolean hasJDOObjectChildren(Project project) {
			return true;
//			Project p = ProjectDAO.sharedInstance().getProject(project.getObjectId(), FETCH_GROUPS, 2, new NullProgressMonitor());
//			return p.getSubProjects().size() > 0;
		}
	}

	private ActiveProjectTreeController activeProjectTreeController;

	public ProjectAdminTreeComposite(Composite parent, int treeStyle, boolean needActions)
	{
		super(parent, treeStyle, true, true, false);
		activeProjectTreeController = new ActiveProjectTreeController()
		{
			@Override
			protected void onJDOObjectsChanged(JDOTreeNodesChangedEvent<ProjectID, ProjectTreeNode> changedEvent)
			{
				JDOTreeNodesChangedEventHandler.handle(getTreeViewer(), changedEvent);
			}
		};

		setInput(activeProjectTreeController);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
				activeProjectTreeController.close();
				activeProjectTreeController = null;
			}
		});

		if (needActions) {
			addContextMenuContribution(new CreateProjectAction());
			addContextMenuContribution(new CreateSubProjectAction(getTreeViewer()));
			addContextMenuContribution(new RenameProjectAction(getTreeViewer()));
			addContextMenuContribution(new DeleteProjectAction(getTreeViewer()));
		}

		drillDownAdapter = new DrillDownAdapter(getTreeViewer());
		hookContextMenu();
	}

	public ProjectAdminTreeComposite(Composite parent, boolean needActions)
	{
		this(parent, DEFAULT_STYLE_SINGLE, needActions);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ProjectAdminTreeComposite.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(getTreeViewer().getControl());
		getTreeViewer().getControl().setMenu(menu);
	}

	/**
	 * Contains instances of both, {@link IContributionItem} and {@link IAction}
	 */
	private List<Object> contextMenuContributions;

	public void addContextMenuContribution(IContributionItem contributionItem)
	{
		if (contextMenuContributions == null)
			contextMenuContributions = new LinkedList<Object>();

		contextMenuContributions.add(contributionItem);
	}

	public void addContextMenuContribution(IAction action)
	{
		if (contextMenuContributions == null)
			contextMenuContributions = new LinkedList<Object>();

		contextMenuContributions.add(action);
	}

	private void fillContextMenu(IMenuManager manager) {
		if (contextMenuContributions != null) {
			for (Object contextMenuContribution : contextMenuContributions) {
				if (contextMenuContribution instanceof IContributionItem)
					manager.add((IContributionItem)contextMenuContribution);
				else if (contextMenuContribution instanceof IAction)
					manager.add((IAction)contextMenuContribution);
				else
					throw new IllegalStateException("How the hell got an instance of " + (contextMenuContribution == null ? "null" : contextMenuContribution.getClass()) + " in the contextMenuContributions list?!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}

		drillDownAdapter.addNavigationActions(manager);

		// Other plug-ins can contribute their actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	protected DrillDownAdapter drillDownAdapter;

	protected static class ProjectTreeLabelProvider extends JDOObjectTreeLabelProvider<ProjectID, Project, ProjectTreeNode>
	{
		@Override
		protected String getJDOObjectText(Project jdoObject, int columnIndex)
		{
			if (jdoObject != null)
				return jdoObject.getName().getText();

			return ""; //$NON-NLS-1$
		}

		@Override
		protected Image getJDOObjectImage(Project project, int columnIndex) {
			if (columnIndex == 0)
				return SharedImages.getSharedImage(IssueTrackingPlugin.getDefault(),
						ProjectAdminTreeComposite.class, "project"); //$NON-NLS-1$

			return super.getJDOObjectImage(project, columnIndex);
		}
	}

	@Override
	public void createTreeColumns(Tree tree)
	{
	}

	@Override
	public void setTreeProvider(TreeViewer treeViewer)
	{
		treeViewer.setContentProvider(new ProjectAdminTreeContentProvider());
		treeViewer.setLabelProvider(new ProjectTreeLabelProvider());
	}

	@Override
	protected Project getSelectionObject(Object obj)
	{
		if (obj instanceof ProjectTreeNode)
			return ((ProjectTreeNode)obj).getJdoObject();

		return null;
	}

	@Override
	protected ActiveJDOObjectTreeController<ProjectID, Project, ProjectTreeNode> getJDOObjectTreeController() {
		return activeProjectTreeController;
	}
}