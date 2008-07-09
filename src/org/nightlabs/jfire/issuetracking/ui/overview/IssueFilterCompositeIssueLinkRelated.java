package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issue.dao.IssueLinkTypeDAO;
import org.nightlabs.jfire.issue.id.IssueLinkTypeID;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkAdderComposite;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueFilterCompositeIssueLinkRelated 
extends AbstractQueryFilterComposite<IssueQuery> 
{	
	private static final Logger logger = Logger.getLogger(IssueFilterCompositeIssueLinkRelated.class);

	private IssueLinkAdderComposite issueLinkAdderComposite;

	private XComboComposite<IssueLinkType> issueLinkTypeCombo;
	private Set<IssueLink> issueLinks;

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
	public IssueFilterCompositeIssueLinkRelated(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode,
			QueryProvider<? super IssueQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
		createComposite(this);
	}

	/**
	 * @param parent
	 *          The parent to instantiate this filter into.
	 * @param style
	 *          The style to apply.
	 * @param queryProvider
	 *          The queryProvider to use. It may be <code>null</code>, but the caller has to
	 *          ensure, that it is set before {@link #getQuery()} is called!
	 */
	public IssueFilterCompositeIssueLinkRelated(Composite parent, int style,
			QueryProvider<? super IssueQuery> queryProvider)
	{
		super(parent, style, queryProvider);
		createComposite(this);
	}

	@Override
	public Class<IssueQuery> getQueryClass() {
		return IssueQuery.class;
	}

	private IssueLinkType selectedIssueLinkType;
	@Override
	protected void createComposite(Composite parent)
	{
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 10;
		parent.setLayout(gridLayout);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		new Label(parent, SWT.NONE).setText("Type: ");
		issueLinkTypeCombo = new XComboComposite<IssueLinkType>(parent, getBorderStyle());
		issueLinkTypeCombo.setLabelProvider(labelProvider);
		issueLinkTypeCombo.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent e)
			{
				selectedIssueLinkType = issueLinkTypeCombo.getSelectedElement();
				getQuery().setIssueLinkTypeID((IssueLinkTypeID) JDOHelper.getObjectId(selectedIssueLinkType));
			}
		});

//		issueLinkAdderComposite = new IssueLinkAdderComposite(parent, SWT.NONE, true, null);
////		issueLinkAdderComposite.addIssueLinkTableItemChangeListener(new IssueLinkTableItemChangeListener()
////		{
////		@Override
////		public void issueLinkItemChanged(IssueLinkItemChangeEvent itemChangedEvent)
////		{
////		issueLinks = issueLinkAdderComposite.getItems();
////		getQuery().setIssueLinks( issueLinks );
////		}
////		});
		
		loadProperties();
	}

	private LabelProvider labelProvider = new LabelProvider() {

		public String getText(Object element) {
			if (element instanceof IssueLinkType) {
				IssueLinkType issueLinkType = (IssueLinkType) element;
				return issueLinkType.getName().getText();
			}
			return "";
		};
	};

	@Override
	protected void resetSearchQueryValues(IssueQuery query)
	{
		query.setIssueLinks(issueLinks);
	}

	@Override
	protected void unsetSearchQueryValues(IssueQuery query)
	{
		query.setIssueLinks(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void updateUI(QueryEvent event)
	{
		if (event.getChangedQuery() == null)
		{
			issueLinks = null;
//			issueLinkAdderComposite.setIssueLinks(null);
		}
		else
		{ // there is a new Query -> the changedFieldList is not null!
			for (FieldChangeCarrier changedField : event.getChangedFields())
			{
				if (IssueQuery.PROPERTY_ISSUE_LINKS.equals(changedField.getPropertyName()))
				{
					issueLinks = (Set<IssueLink>) changedField.getNewValue();
//					issueLinkAdderComposite.setIssueLinks(issueLinks);
				}
			} // for (FieldChangeCarrier changedField : event.getChangedFields())
		} // changedQuery != null		
	}

	private static final String[] FETCH_GROUPS_ISSUE_LINK_TYPE = { IssueLinkType.FETCH_GROUP_NAME, FetchPlan.DEFAULT };

	private void loadProperties(){
		Job loadJob = new Job("Loading Issue Link Properties....") {
			@Override
			protected IStatus run(final ProgressMonitor monitor) {				
				try {
					try {
						final List<IssueLinkType> issueLinkTypeList = new ArrayList<IssueLinkType>(IssueLinkTypeDAO.sharedInstance().
								getIssueLinkTypes(FETCH_GROUPS_ISSUE_LINK_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor));

						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								issueLinkTypeCombo.removeAll();
								for (Iterator<IssueLinkType> it = issueLinkTypeList.iterator(); it.hasNext(); ) {
									IssueLinkType issueLinkType = it.next();
									issueLinkTypeCombo.addElement(issueLinkType);
								}
							}
						});
					}catch (Exception e1) {
						ExceptionHandlerRegistry.asyncHandleException(e1);
						throw new RuntimeException(e1);
					}

					return Status.OK_STATUS;
				} finally {
					logger.debug("Load Job finished.");
				}
			} 
		};

		loadJob.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
		loadJob.schedule();
	} 
}
