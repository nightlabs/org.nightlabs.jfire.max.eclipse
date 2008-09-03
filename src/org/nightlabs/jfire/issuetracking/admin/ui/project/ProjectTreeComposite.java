package org.nightlabs.jfire.issuetracking.admin.ui.project;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.DrillDownAdapter;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issuetracking.admin.ui.IssueTrackingAdminPlugin;
import org.nightlabs.util.CollectionUtil;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 *
 */
public class ProjectTreeComposite
extends XComposite
{
	private TreeViewer treeViewer;
	
	public ProjectTreeComposite(Composite parent, int style)
	{
		super(parent, style);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
			}
		});

		createPartContents(this);
		
		addContextMenuContribution(new CreateProjectAction());
		addContextMenuContribution(new RenameProjectAction());
		
		drillDownAdapter = new DrillDownAdapter(treeViewer);
		hookContextMenu();
	}
	
	public ProjectTreeComposite(Composite parent)
	{
		this(parent, 0);
	}
	
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ProjectTreeComposite.this.fillContextMenu(manager);
			}
		});
		
		Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
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

	private ProjectTreeNode projectTreeNode = null;
	
	public void createPartContents(Composite parent)
	{
		treeViewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.setContentProvider(new ProjectTreeContentProvider(treeViewer));
		treeViewer.setLabelProvider(new ProjectLabelProvider());
		projectTreeNode = new ProjectTreeNode(treeViewer, null);
		treeViewer.setInput(projectTreeNode);
		treeViewer.setSorter(new ViewerSorter());
	}
	
	public class CreateProjectAction extends Action {
		private InputDialog dialog;
		public CreateProjectAction() {
			setId(CreateProjectAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingAdminPlugin.getDefault(), 
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
//						Project projectToStore = treeViewer..getFirstSelectedElement();
//						Project project = new Project(Login.getLogin().getOrganisationID(), IDGenerator.nextID(Project.class));
//						project.getName().setText(Locale.ENGLISH.getLanguage(), getValue());
//						projectToStore.addSubProject(project);
//						ProjectDAO.sharedInstance().storeProject(projectToStore, false, new String[]{FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
//						dialog.close();
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
					IssueTrackingAdminPlugin.getDefault(), 
					ProjectTreeComposite.class, 
			"Rename"));
			setToolTipText("Rename Project");
			setText("Rename Project");
		}

		@Override
		public void run() {
//			dialog = new InputDialog(RCPUtil.getActiveShell(), "Rename Project", "Enter project's name", getFirstSelectedElement().getName().getText(), null) {
//				@Override
//				protected void okPressed() {
//					try {
//						Project projectToStore = getFirstSelectedElement();
//						projectToStore.getName().setText(Locale.ENGLISH.getLanguage(), getValue());
//						ProjectDAO.sharedInstance().storeProject(projectToStore, false, new String[]{FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
//						dialog.close();
//					} catch (Exception e) {
//						throw new RuntimeException(e);
//					}
//				};
//				
//				@Override
//				protected Control createDialogArea(Composite parent) {
//					Control dialogArea = super.createDialogArea(parent);
//					return dialogArea;
//				}
//			};
			
			if (dialog.open() != Window.OK)
				return;
		}		
	}

	class ProjectTreeContentProvider
	implements ITreeContentProvider
	{
		private TreeViewer treeViewer;

		public ProjectTreeContentProvider(TreeViewer treeViewer) {
			this.treeViewer = treeViewer;
		}

		public Object[] getChildren(final Object parentElement) 
		{
			Project project = (Project) parentElement;
			return CollectionUtil.collection2TypedArray(project.getSubProjects(), Project.class);
		}

		public Object getParent(Object childElement) {
			Project project = (Project) childElement;
			return project.getParentProject();
		}

		public boolean hasChildren(Object element) {
			return true;
		}

		public Object[] getElements(final Object inputElement) {
			if (inputElement instanceof ProjectTreeNode) 
			{
				ProjectTreeNode projectTreeNode = (ProjectTreeNode) inputElement;
				treeViewer.getTree().setEnabled(false);
				projectTreeNode.loadChildren();
				final ProjectTreeNode[] nodes = projectTreeNode.getChildren();
				treeViewer.getTree().setEnabled(true);
				return nodes;
			}//if
			return new Object[]{};
		}

		public void dispose() {}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
	}
	
	class ProjectLabelProvider extends LabelProvider {
		public HashMap<ImageDescriptor, Image>imageCache = new HashMap<ImageDescriptor, Image>();
		@Override
		public Image getImage(Object element) {
			Image image = null;
			if(element instanceof ProjectTreeNode){
				ProjectTreeNode node = (ProjectTreeNode)element;
				ImageDescriptor descriptor = node.getImageDescriptor();

				//obtain the cached image corresponding to the descriptor
				image = imageCache.get(descriptor);
				if (image == null && descriptor != null) {
					image = descriptor.createImage();
					imageCache.put(descriptor, image);
				}
			}//if
			return image;
		}

		@Override
		public String getText(Object element) {
			String result = null;
			if(element instanceof ProjectTreeNode){
				ProjectTreeNode node = (ProjectTreeNode)element;
				result = node.getLabel();
			}//if
			else if (element instanceof String)
				result = element.toString();
			
			return result==null?"":result; //$NON-NLS-1$
		}
	}
}