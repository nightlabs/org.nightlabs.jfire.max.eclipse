package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeItem;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOTreeNodesChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOTreeNodesChangedListener;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.issue.project.id.ProjectID;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectAdminTreeComposite;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectTreeNode;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueFilterCompositeProjectRelated
extends AbstractQueryFilterComposite<IssueQuery>
{
	private static final Logger logger = Logger.getLogger(IssueFilterCompositeProjectRelated.class);

//	private ProjectComboComposite projectCombo;
	private ProjectAdminTreeComposite projectTreeComposite;
	private CheckboxTreeViewer checkboxTreeViewer;
	
	private volatile Set<ProjectID> selectedProjectIDs = new HashSet<ProjectID>();

	/**
	 * @param parent
	 *          The parent to instantiate this filter into.
	 * @param style
	 *          The style to apply.
	 * @param layoutMode
	 *          The layout mode to use. See {@link XComposite.LayoutMode}.
	 * @param layoutDataMode
	 *          The layout data mode to use. See {@link XComposite.LayoutDataMode}.
	 * @param queryProvider
	 *          The queryProvider to use. It may be <code>null</code>, but the caller has to
	 *          ensure, that it is set before {@link #getQuery()} is called!
	 */
	public IssueFilterCompositeProjectRelated(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode,
			QueryProvider<? super IssueQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
		createComposite();
	}

	/**
	 * @param parent
	 *          The this to instantiate this filter into.
	 * @param style
	 *          The style to apply.
	 * @param queryProvider
	 *          The queryProvider to use. It may be <code>null</code>, but the caller has to
	 *          ensure, that it is set before {@link #getQuery()} is called!
	 */
	public IssueFilterCompositeProjectRelated(Composite parent, int style,
			QueryProvider<? super IssueQuery> queryProvider)
	{
		super(parent, style, queryProvider);
//		prepareIssueProperties();
		createComposite();
	}

	@Override
	public Class<IssueQuery> getQueryClass() {
		return IssueQuery.class;
	}

	@Override
	protected void createComposite()
	{
		this.setLayout(new GridLayout(3, false));

		XComposite projectComposite = new XComposite(this, SWT.NONE,
				LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		projectComposite.getGridLayout().numColumns = 2;

		new Label(projectComposite, SWT.NONE).setText("Project: ");
		projectTreeComposite = new ProjectAdminTreeComposite(projectComposite, SWT.CHECK);
		checkboxTreeViewer = new CheckboxTreeViewer(projectTreeComposite.getTree());
		
		checkboxTreeViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				selectedProjectIDs.clear();
				
				final boolean isChecked = event.getChecked();
				final ProjectTreeNode node = (ProjectTreeNode)event.getElement();
				
				projectTreeComposite.getTreeViewer().expandToLevel(node, AbstractTreeViewer.ALL_LEVELS);
				node.getActiveJDOObjectTreeController().addJDOTreeNodesChangedListener(new JDOTreeNodesChangedListener() {
					@Override
					public void onJDOObjectsChanged(
							JDOTreeNodesChangedEvent changedEvent) {
						for (Object o : changedEvent.getLoadedTreeNodes())
							checkboxTreeViewer.setChecked(o, isChecked);
					}
				});
				
				
				for (Object object : checkboxTreeViewer.getCheckedElements()) {
					if (object instanceof ProjectTreeNode) {
						ProjectTreeNode projectTreeNode = (ProjectTreeNode)object;
						selectedProjectIDs.add(projectTreeNode.getJdoObject().getObjectId());
					}
				}
				
				getQuery().setProjectIDs(selectedProjectIDs);
				boolean enable = !selectedProjectIDs.isEmpty();
				getQuery().setFieldEnabled(IssueQuery.FieldName.projectIDs, enable);
			}
		});
//		projectCombo = new ProjectComboComposite(projectComposite, SWT.NONE);
//		projectCombo.addSelectionChangedListener(new ISelectionChangedListener()
//		{
//		public void selectionChanged(SelectionChangedEvent e)
//		{
//		final Project selectedProject = projectCombo.getSelectedProject();

//		boolean selectAll = selectedProject.equals(PROJECT_ALL);
//		if (selectAll)
//		getQuery().setProjectID(null);
//		else
//		getQuery().setProjectID(selectedProject.getObjectId());

//		getQuery().setFieldEnabled(IssueQuery.FieldName.projectID, ! selectAll);
//		}
//		});
		
//		prepareIssueProperties();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void updateUI(QueryEvent event, List<FieldChangeCarrier> changedFields)
	{
		for (FieldChangeCarrier changedField : event.getChangedFields())
		{
			if (IssueQuery.FieldName.projectIDs.equals(changedField.getPropertyName()))
			{
				Set<ProjectID> tmpProjectIDs = (Set<ProjectID>) changedField.getNewValue();
				if (tmpProjectIDs == null)
				{
//					projectCombo.setSelection(new StructuredSelection(PROJECT_ALL));
				}
				else
				{
					for (ProjectID projectID : tmpProjectIDs) {
						TreeItem[] treeItems = checkboxTreeViewer.getTree().getItems();
						for (TreeItem treeItem : treeItems) {
							Object o = treeItem.getData();
						}
					}
//					checkboxTreeViewer.setChecked(element, state)
//					final Project newProject = ProjectDAO.sharedInstance().getProject(
//							tmpProjectID, new String[] { Project.FETCH_GROUP_NAME },
//							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
//					);
//					projectCombo.setSelectedProject(newProject);
//					if (! newProject.equals(projectCombo.getSelectedProject()))
//					selectedProject = newProject;

				}
			}
			else if (getEnableFieldName(IssueQuery.FieldName.projectIDs).equals(
					changedField.getPropertyName()))
			{
				Boolean active = (Boolean) changedField.getNewValue();
				setSearchSectionActive(active);
			}
		} // for (FieldChangeCarrier changedField : event.getChangedFields())


	}

//	private static Project PROJECT_ALL = new Project(Organisation.DEV_ORGANISATION_ID, -1);

//	private void prepareIssueProperties(){
//		PROJECT_ALL.getName().setText(Locale.ENGLISH.getLanguage(), "All");
//		projectCombo.addProject(PROJECT_ALL, 0);

//		if (selectedProject == null)
//		selectedProject = PROJECT_ALL;

//		projectCombo.setSelectedProject(selectedProject);

//	}

	private static final Set<String> fieldNames;
	static
	{
		fieldNames = new HashSet<String>(1);
		fieldNames.add(IssueQuery.FieldName.projectIDs);
	}

	@Override
	protected Set<String> getFieldNames()
	{
		return fieldNames;
	}

	/**
	 * Group ID for storing active states in the query.
	 */
	public static final String FILTER_GROUP_ID = "IssueFilterCompositeProjectRelated";

	@Override
	protected String getGroupID()
	{
		return FILTER_GROUP_ID;
	}

}