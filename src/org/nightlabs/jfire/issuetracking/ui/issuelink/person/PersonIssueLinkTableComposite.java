package org.nightlabs.jfire.issuetracking.ui.issuelink.person;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.jdo.FetchPlan;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.config.PersonIssueLinkTableConfigModule;
import org.nightlabs.jfire.issue.dao.IssueLinkDAO;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueTable;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.jfire.table.config.ColumnDescriptor;
import org.nightlabs.jfire.table.config.IColumnConfiguration;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class PersonIssueLinkTableComposite
extends XComposite
{
	private static final String[] ISSUE_LINK_FETCH_GROUPS = new String[] {FetchPlan.DEFAULT, IssueLink.FETCH_GROUP_ISSUE};

	private static final Logger logger = Logger.getLogger(PersonIssueLinkTableComposite.class);

	private IssueTable issueTable;
	private IColumnConfiguration columnConfiguration;
	private String[] fetchGroups;
	private Map<Issue, IssueLink> issue2IssueLink;

	public PersonIssueLinkTableComposite(Composite parent, int style) {
		super(parent, style);
		issue2IssueLink = new HashMap<Issue, IssueLink>();
		createPartContents(this);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.part.ControllablePart#createPartContents(org.eclipse.swt.widgets.Composite)
	 */
	protected void createPartContents(final Composite parent)
	{
		issueTable = new IssueTable(parent, SWT.NONE, false);
		issueTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		issueTable.getTableViewer().getTable().setLinesVisible(false);

		Job loadColumnConfiguration = new Job("Load Column Configuration") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				columnConfiguration = createColumnConfiguration(monitor);
				if (!parent.isDisposed()) {
					parent.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							issueTable.setIssueTableConfigurations(columnConfiguration);
							issueTable.layout(true, true);
						}
					});
				}
				return Status.OK_STATUS;
			}
		};
		loadColumnConfiguration.schedule();
	}

//	protected IColumnConfiguration createColumnConfiguration(ProgressMonitor monitor)
//	{
//		IssueTableConfigModule issueTableCfMod = ConfigUtil.getUserCfMod(
//				IssueTableConfigModule.class,
//				new String[] {FetchPlan.DEFAULT,
//					IssueTableConfigModule.FETCH_GROUP_COLUMNDESCRIPTORS,
//					ColumnDescriptor.FETCH_GROUP_COL_FIELD_NAMES,
//					ColumnDescriptor.FETCH_GROUP_COL_NAME,
//					ColumnDescriptor.FETCH_GROUP_COL_TOOLTIP_DESCRIPTION,
//					ColumnDescriptor.FETCH_GROUP_COL_FETCH_GROUPS},
//				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//				monitor);
//		monitor.done();
//		return issueTableCfMod;
//	}

	protected IColumnConfiguration createColumnConfiguration(ProgressMonitor monitor)
	{
		PersonIssueLinkTableConfigModule issueTableCfMod = ConfigUtil.getUserCfMod(
				PersonIssueLinkTableConfigModule.class,
				new String[] {FetchPlan.DEFAULT,
					PersonIssueLinkTableConfigModule.FETCH_GROUP_COLUMNDESCRIPTORS,
					ColumnDescriptor.FETCH_GROUP_COL_FIELD_NAMES,
					ColumnDescriptor.FETCH_GROUP_COL_NAME,
					ColumnDescriptor.FETCH_GROUP_COL_TOOLTIP_DESCRIPTION,
					ColumnDescriptor.FETCH_GROUP_COL_FETCH_GROUPS},
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				monitor);
		monitor.done();
		return issueTableCfMod;
	}

	protected String[] getFetchGroups()
	{
		if (fetchGroups == null && columnConfiguration != null) {
			String[] columnFetchGroups = columnConfiguration.getAllColumnFetchGroups();
			fetchGroups = new String[columnFetchGroups.length + ISSUE_LINK_FETCH_GROUPS.length];
			for (int i = 0; i < columnFetchGroups.length; i++) {
				fetchGroups[i] = columnFetchGroups[i];
			}
			for (int i = 0; i < ISSUE_LINK_FETCH_GROUPS.length; i++) {
				fetchGroups[i + columnFetchGroups.length] = ISSUE_LINK_FETCH_GROUPS[i];
			}
		}
		return fetchGroups;
	}

	public void setSelectedPersonID(final PropertySetID personID)
	{
		if (issueTable != null && !issueTable.isDisposed() && columnConfiguration != null)
		{
			if (personID == null) {
				if (issueTable.getTableViewer().getContentProvider() != null)
					issueTable.setInput(null);
				return;
			}

//			issueTable.setLoadingMessage("Loading...");
			Job job = new Job("Load Issues for Person") {
				@Override
				protected IStatus run(ProgressMonitor monitor) throws Exception
				{
					issue2IssueLink.clear();
					Collection<IssueLink> issueLinks = IssueLinkDAO.sharedInstance().getIssueLinks(
							personID, getFetchGroups(), NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
					final Collection<Issue> personIssues = new HashSet<Issue>(issueLinks.size());
//					logger.info("getFetchGroups() = "+getFetchGroups());
					for (IssueLink issueLink : issueLinks) {
						personIssues.add(issueLink.getIssue());
						issue2IssueLink.put(issueLink.getIssue(), issueLink);
					}
					if (!issueTable.isDisposed()) {
						issueTable.getDisplay().asyncExec(new Runnable() {
							@Override
							public void run()
							{
								if (issueTable.getTableViewer().getContentProvider() != null)
									issueTable.setInput(personIssues);
							}
						});
					}
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
	}

	public IssueTable getIssueTable() {
		return issueTable;
	}

	public IssueLink getSelectedIssueLink()
	{
		if (issueTable != null && !issueTable.isDisposed())
		{
			if (issueTable.getFirstSelectedElement() instanceof Issue) {
				Issue issue = issueTable.getFirstSelectedElement();
				return issue2IssueLink.get(issue);
			}
		}
		return null;
	}

}
