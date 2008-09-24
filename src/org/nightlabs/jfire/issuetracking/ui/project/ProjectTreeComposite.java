package org.nightlabs.jfire.issuetracking.ui.project;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.jdo.FetchPlan;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.DrillDownAdapter;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOObjectTreeContentProvider;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOObjectTreeLabelProvider;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOTreeNodesChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOTreeNodesChangedEventHandler;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectDAO;
import org.nightlabs.jfire.issue.project.id.ProjectID;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.issue.ActiveProjectTreeController;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 *
 */
public class ProjectTreeComposite 
extends AbstractTreeComposite<Project> 
{
	private static String[] FETCH_GROUPS = new String[]{
		FetchPlan.DEFAULT, Project.FETCH_GROUP_NAME, Project.FETCH_GROUP_SUBPROJECTS, Project.FETCH_GROUP_PARENT_PROJECT
	};

	protected static class ProjectTreeContentProvider
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

	public ProjectTreeComposite(Composite parent, int treeStyle)
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

		addContextMenuContribution(new CreateProjectAction());
		addContextMenuContribution(new CreateSubProjectAction());
		addContextMenuContribution(new RenameProjectAction());
		addContextMenuContribution(new DeleteProjectAction());

		drillDownAdapter = new DrillDownAdapter(getTreeViewer());
		hookContextMenu();
	}

	public ProjectTreeComposite(Composite parent)
	{
		this(parent, DEFAULT_STYLE_SINGLE);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ProjectTreeComposite.this.fillContextMenu(manager);
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
		protected String getJDOObjectText(Project jdoObject, int columnIndex) {
			return jdoObject.getName().getText();
		}

		@Override
		protected Image getJDOObjectImage(Project project, int columnIndex) {
			if (columnIndex == 0)
				return SharedImages.getSharedImage(IssueTrackingPlugin.getDefault(),
						ProjectTreeComposite.class, "project");

			return super.getJDOObjectImage(project, columnIndex);
		}
	}

	@Implement
	@Override
	public void createTreeColumns(Tree tree)
	{
	}

	@Implement
	@Override
	public void setTreeProvider(TreeViewer treeViewer)
	{
		treeViewer.setContentProvider(new ProjectTreeContentProvider());
		treeViewer.setLabelProvider(new ProjectTreeLabelProvider());
	}

	@Override
	protected Project getSelectionObject(Object obj)
	{
		if (obj instanceof ProjectTreeNode)
			return ((ProjectTreeNode)obj).getJdoObject();

		return null;
	}

	public class CreateProjectAction extends Action {
		private InputDialog dialog;
		public CreateProjectAction() {
			setId(CreateProjectAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingPlugin.getDefault(), 
					ProjectTreeComposite.class, 
			"Create"));
			setToolTipText("Create Project");
			setText("Create Project");
		}

		@Override
		public void run() {
			dialog = new InputDialog(RCPUtil.getActiveShell(), "Create Project", "Enter project's name", "Name", null) {
				@Override
				protected void okPressed() {
					try {
						Project project = new Project(Login.getLogin().getOrganisationID(), IDGenerator.nextID(Project.class));
						project.getName().setText(Locale.ENGLISH.getLanguage(), getValue());

						ProjectDAO.sharedInstance().storeProject(project, false, new String[]{FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
						dialog.close();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				};

				@Override
				protected Control createDialogArea(Composite parent) {
					Control dialogArea = super.createDialogArea(parent);
					return dialogArea;
				}
			};

			if (dialog.open() != Window.OK)
				return;
		}		
	}

	public class CreateSubProjectAction extends Action {
		private InputDialog dialog;
		public CreateSubProjectAction() {
			setId(CreateSubProjectAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingPlugin.getDefault(), 
					ProjectTreeComposite.class, 
			"Create"));
			setToolTipText("Create Sub Project");
			setText("Create Sub Project");
		}

		@Override
		public void run() {
			dialog = new InputDialog(RCPUtil.getActiveShell(), "Create Sub Project", "Enter project's name", "Name", null) {
				@Override
				protected void okPressed() {
					try {
						TreeSelection selection = (TreeSelection)getTreeViewer().getSelection();
						Project projectToStore = ((ProjectTreeNode)(selection.getFirstElement())).getJdoObject();
						Project project = new Project(Login.getLogin().getOrganisationID(), IDGenerator.nextID(Project.class));
						project.getName().setText(Locale.ENGLISH.getLanguage(), getValue());

						projectToStore = ProjectDAO.sharedInstance().getProject(
								projectToStore.getObjectId(), new String[]{Project.FETCH_GROUP_NAME, Project.FETCH_GROUP_SUBPROJECTS}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
						projectToStore.addSubProject(project);
						ProjectDAO.sharedInstance().storeProject(projectToStore, false, new String[]{FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
						dialog.close();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				};

				@Override
				protected Control createDialogArea(Composite parent) {
					Control dialogArea = super.createDialogArea(parent);
					return dialogArea;
				}
			};

			if (dialog.open() != Window.OK)
				return;
		}		
	}

	public class RenameProjectAction extends Action {
		private InputDialog dialog;
		public RenameProjectAction() {
			setId(CreateProjectAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingPlugin.getDefault(), 
					ProjectTreeComposite.class, 
			"Rename"));
			setToolTipText("Rename Project");
			setText("Rename Project");
		}

		@Override
		public void run() {
			TreeSelection selection = (TreeSelection)getTreeViewer().getSelection();
			final Project projectToStore = ((ProjectTreeNode)(selection.getFirstElement())).getJdoObject();
			dialog = new InputDialog(RCPUtil.getActiveShell(), "Rename Project", "Enter project's name", projectToStore.getName().getText(), null) {
				@Override
				protected void okPressed() {
					try {
						projectToStore.getName().setText(Locale.ENGLISH.getLanguage(), getValue());
						ProjectDAO.sharedInstance().storeProject(projectToStore, false, new String[]{FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
						dialog.close();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				};

				@Override
				protected Control createDialogArea(Composite parent) {
					Control dialogArea = super.createDialogArea(parent);
					return dialogArea;
				}
			};

			if (dialog.open() != Window.OK)
				return;
		}		
	}
	
	class DeleteProjectAction 
	extends Action {		
		public DeleteProjectAction() {
			super();
			setId(DeleteProjectAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingPlugin.getDefault(), 
					ProjectTreeComposite.class, 
					"Delete"));
			setToolTipText("Delete the selected project");
			setText("Delete");
		}
		
		@Override
		public void run() {
			boolean confirm = MessageDialog.openConfirm(getShell(), "Confirm Delete", "Delete this item(s)?");
			if(confirm) {
				TreeSelection selection = (TreeSelection)getTreeViewer().getSelection();
				Project project = ((ProjectTreeNode)(selection.getFirstElement())).getJdoObject();
				ProjectDAO.sharedInstance().deleteProject(project.getObjectId(), new NullProgressMonitor());
				refresh();
			}
		}		
	}
}