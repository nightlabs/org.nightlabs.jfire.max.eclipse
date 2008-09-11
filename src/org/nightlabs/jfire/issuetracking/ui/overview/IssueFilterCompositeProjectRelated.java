package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectDAO;
import org.nightlabs.jfire.issue.project.id.ProjectID;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.issue.create.ProjectComboComposite;
import org.nightlabs.jfire.organisation.Organisation;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueFilterCompositeProjectRelated
extends AbstractQueryFilterComposite<IssueQuery>
{
	private static final Logger logger = Logger.getLogger(IssueFilterCompositeProjectRelated.class);

	private ProjectComboComposite projectCombo;

	private volatile Project selectedProject;

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
//		prepareIssueProperties();
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
		projectCombo = new ProjectComboComposite(projectComposite, getBorderStyle());
		projectCombo.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent e)
			{
				final Project selectedProject = projectCombo.getSelectedProject();

				if (selectedProject.equals(PROJECT_ALL))
					getQuery().setProjectID(null);
				else
					getQuery().setProjectID(selectedProject.getObjectId());
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void updateUI(QueryEvent event, List<FieldChangeCarrier> changedFields)
	{
		boolean sectionActive = false;
		for (FieldChangeCarrier changedField : event.getChangedFields())
		{
			if (IssueQuery.FieldName.projectID.equals(changedField.getPropertyName()))
			{
				ProjectID tmpProjectID = (ProjectID) changedField.getNewValue();
				if (tmpProjectID == null)
				{
					projectCombo.setSelection(new StructuredSelection(PROJECT_ALL));
				}
				else
				{
					final Project newProject = ProjectDAO.sharedInstance().getProject(
							tmpProjectID, new String[] { Project.FETCH_GROUP_NAME },
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
					);
					projectCombo.setSelectedProject(newProject);
					if (! newProject.equals(projectCombo.getSelectedProject()))
						selectedProject = newProject;

					sectionActive |= true;
				}
			}
		} // for (FieldChangeCarrier changedField : event.getChangedFields())

		setSearchSectionActive(sectionActive);
	}

	private static Project PROJECT_ALL = new Project(Organisation.DEV_ORGANISATION_ID, -1);

	private void prepareIssueProperties(){
		PROJECT_ALL.getName().setText(Locale.ENGLISH.getLanguage(), "All");
	}

	private static final Set<String> fieldNames;
	static
	{
		fieldNames = new HashSet<String>(1);
		fieldNames.add(IssueQuery.FieldName.projectID);
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