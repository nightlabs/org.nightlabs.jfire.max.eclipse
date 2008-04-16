package org.nightlabs.jfire.trade.ui.overview.repository;

import java.util.Collection;

import javax.jdo.FetchPlan;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.base.ui.overview.search.JDOQuerySearchEntryViewer;
import org.nightlabs.jfire.base.ui.overview.search.SearchEntryViewer;
import org.nightlabs.jfire.store.Repository;
import org.nightlabs.jfire.store.RepositoryType;
import org.nightlabs.jfire.store.dao.RepositoryDAO;
import org.nightlabs.jfire.store.query.RepositoryQuery;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.progress.ProgressMonitor;

/**
 * implementation of a {@link JDOQuerySearchEntryViewer} for searching and
 * displaying {@link Repository}s
 * 
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class RepositoryEntryViewer
	extends JDOQuerySearchEntryViewer<Repository, RepositoryQuery>
{
	public RepositoryEntryViewer(Entry entry) {
		super(entry);
	}

	@Override
	public AbstractTableComposite<Repository> createListComposite(Composite parent) {
		return new RepositoryListComposite(parent, SWT.NONE);
	}

	public static final String[] FETCH_GROUPS_REPOSITORIES = new String[] {
//		Repository.FETCH_GROUP_THIS_REPOSITORY, // we don't know what will be added - hence in order to keep it small, we specify individually
		Repository.FETCH_GROUP_NAME,
		Repository.FETCH_GROUP_OWNER,
		Repository.FETCH_GROUP_REPOSITORY_TYPE,
		RepositoryType.FETCH_GROUP_NAME,
		FetchPlan.DEFAULT,
		LegalEntity.FETCH_GROUP_PERSON
	};

	@Override
	protected Collection<Repository> doSearch(QueryCollection<? extends RepositoryQuery> queryMap,
		ProgressMonitor monitor)
	{
		return RepositoryDAO.sharedInstance().getRepositoriesForQueries(
			queryMap,
			FETCH_GROUPS_REPOSITORIES,
			NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
			monitor);
	}

	@Override
	public Class<Repository> getResultType()
	{
		return Repository.class;
	}

	@Override
	protected Class<? extends SearchEntryViewer<Repository, RepositoryQuery>> getViewerClass()
	{
		return RepositoryEntryViewer.class;
	}

}
