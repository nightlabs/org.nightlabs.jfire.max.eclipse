package org.nightlabs.jfire.issuetimetracking.admin.ui;

import javax.jdo.FetchPlan;

import org.eclipse.ui.IEditorInput;
import org.nightlabs.base.ui.editor.JDOObjectEditorInput;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.CurrencyConstants;
import org.nightlabs.jfire.accounting.Price;
import org.nightlabs.jfire.accounting.PriceFragment;
import org.nightlabs.jfire.accounting.dao.CurrencyDAO;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issue.project.ProjectDAO;
import org.nightlabs.jfire.issue.project.id.ProjectID;
import org.nightlabs.jfire.issuetimetracking.ProjectCost;
import org.nightlabs.jfire.issuetimetracking.ProjectCostValue;
import org.nightlabs.jfire.issuetimetracking.dao.ProjectCostDAO;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectEditorInput;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/** 
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 */
public class ProjectCostEditorPageController 
extends ActiveEntityEditorPageController<ProjectCost>
{
	public ProjectCostEditorPageController(EntityEditor editor)
	{
		super(editor);
		this.projectID = (ProjectID) ((JDOObjectEditorInput<?>)editor.getEditorInput()).getJDOObjectID();
	}

	@Override
	protected String[] getEntityFetchGroups() {
		return new String[] {
				FetchPlan.DEFAULT,
				Project.FETCH_GROUP_MEMBERS,
				ProjectCost.FETCH_GROUP_PROJECT,
				ProjectCost.FETCH_GROUP_CURRENCY,
				ProjectCost.FETCH_GROUP_DEFAULT_COST,
				ProjectCost.FETCH_GROUP_DEFAULT_REVENUE,
				ProjectCostValue.FETCH_GROUP_COST,
				ProjectCostValue.FETCH_GROUP_REVENUE,
				Price.FETCH_GROUP_CURRENCY,
				Price.FETCH_GROUP_FRAGMENTS,
				PriceFragment.FETCH_GROUP_PRICE_FRAGMENT_TYPE
		};
	}

	private ProjectID projectID;

	@Override
	protected ProjectCost retrieveEntity(ProgressMonitor monitor) {
		ProjectCost projectCost = ProjectCostDAO.sharedInstance().getProjectCost(projectID, getEntityFetchGroups(), getEntityMaxFetchDepth(), monitor);

		if (projectCost == null) {
			projectCost = new ProjectCost(getProject(), CurrencyDAO.sharedInstance().getCurrency(CurrencyConstants.EUR, new NullProgressMonitor()));
			projectCost = ProjectCostDAO.sharedInstance().storeProjectCost(projectCost, 
					getEntityFetchGroups(),
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					new NullProgressMonitor());
		}

		return projectCost;
	}

	@Override
	protected IEditorInput createNewInstanceEditorInput() {
		return new ProjectEditorInput(projectID);
	}

	@Override
	protected ProjectCost storeEntity(ProjectCost controllerObject,
			ProgressMonitor monitor) {
		monitor.beginTask("Saving Project Cost", 100);
		try {
			ProjectCost projectCost = ProjectCostDAO.sharedInstance().storeProjectCost(controllerObject, getEntityFetchGroups(), getEntityMaxFetchDepth(),
					new SubProgressMonitor(monitor, 50));
			return projectCost;
		} finally {
			monitor.done();
		}
	}

	public ProjectID getProjectID() {
		return projectID;
	}

	public Project getProject() {
		return ProjectDAO.sharedInstance().getProject(projectID, getEntityFetchGroups(), NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
	}
}
