package org.nightlabs.jfire.issuetracking.ui.overview;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
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
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issue.dao.IssueLinkTypeDAO;
import org.nightlabs.jfire.issue.id.IssueLinkTypeID;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.jfire.organisation.Organisation;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueFilterCompositeIssueLinkRelated
	extends AbstractQueryFilterComposite<IssueQuery>
{
	private static final Logger logger = Logger.getLogger(IssueFilterCompositeIssueLinkRelated.class);

	private XComboComposite<IssueLinkType> issueLinkTypeCombo;

	private Object mutex = new Object();

	/**
	 * Used to defer the selection of an element until the loading job is finished.
	 */
	private volatile IssueLinkType selectedIssueLinkType;

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
		prepareProperties();
		createComposite();
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
		prepareProperties();
		createComposite();
	}

	@Override
	public Class<IssueQuery> getQueryClass() {
		return IssueQuery.class;
	}

	@Override
	protected void createComposite()
	{
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 10;
		this.setLayout(gridLayout);
		this.setLayoutData(new GridData(GridData.FILL_BOTH));

		new Label(this, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositeIssueLinkRelated.label.linkType.text")); //$NON-NLS-1$
		issueLinkTypeCombo = new XComboComposite<IssueLinkType>(this, getBorderStyle() | SWT.READ_ONLY);
		issueLinkTypeCombo.setLabelProvider(labelProvider);
		issueLinkTypeCombo.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent e)
			{
				selectedIssueLinkType = issueLinkTypeCombo.getSelectedElement();

				if (selectedIssueLinkType.equals(ISSUE_LINK_TYPE_ALL)) {
					getQuery().setIssueLinkTypeID(null);
					getQuery().setFieldEnabled(IssueQuery.FieldName.issueLinkTypeID, false);
				}
				else
				{
					getQuery().setIssueLinkTypeID((IssueLinkTypeID) JDOHelper.getObjectId(selectedIssueLinkType));
					getQuery().setFieldEnabled(IssueQuery.FieldName.issueLinkTypeID, true);
				}
			}
		});

		loadProperties();
	}

	private LabelProvider labelProvider = new LabelProvider()
	{
		@Override
		public String getText(Object element) {
			if (element instanceof IssueLinkType) {
				IssueLinkType issueLinkType = (IssueLinkType) element;
				return issueLinkType.getName().getText();
			}
			return ""; //$NON-NLS-1$
		};
	};

	@Override
	protected void updateUI(QueryEvent event, List<FieldChangeCarrier> changedFields)
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Thread mismatch! Must be called on UI thread!");

		for (FieldChangeCarrier changedField : event.getChangedFields())
		{
			if (IssueQuery.FieldName.issueLinkTypeID.equals(changedField.getPropertyName()))
			{
				IssueLinkTypeID newIssueLinkTypeID = (IssueLinkTypeID) changedField.getNewValue();
				if (newIssueLinkTypeID == null)
				{
					issueLinkTypeCombo.setSelection(ISSUE_LINK_TYPE_ALL);
					selectedIssueLinkType = null;
					logger.info("updateUI:1: selectedIssueLinkType == NULL");
				}
				else
				{
					final IssueLinkType newIssueLinkType = IssueLinkTypeDAO.sharedInstance().getIssueLinkType(
							newIssueLinkTypeID, new String[] { IssueLinkType.FETCH_GROUP_NAME, FetchPlan.DEFAULT },
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
					);

					issueLinkTypeCombo.setSelection(newIssueLinkType);
					selectedIssueLinkType = newIssueLinkType;
					logger.info("updateUI:2: selectedIssueLinkType == " + selectedIssueLinkType);
				}
			}
			else if (getEnableFieldName(IssueQuery.FieldName.issueLinkTypeID).equals(changedField.getPropertyName()))
			{
				boolean isActive = (Boolean) changedField.getNewValue();
				setSearchSectionActive(isActive/*getQuery().isFieldEnabled(IssueQuery.FieldName.issueLinkTypeID)*/);

				if (!isActive) {
					getQuery().setIssueLinkTypeID(null);
					issueLinkTypeCombo.setSelection(ISSUE_LINK_TYPE_ALL);
					selectedIssueLinkType = null;
				}

				logger.info("updateUI:3: selectedIssueLinkType == " + isActive);
			}
		} // for (FieldChangeCarrier changedField : event.getChangedFields())
	}

	private static final String[] FETCH_GROUPS_ISSUE_LINK_TYPE = { IssueLinkType.FETCH_GROUP_NAME, FetchPlan.DEFAULT };
	private static IssueLinkType ISSUE_LINK_TYPE_ALL = new IssueLinkType(Organisation.DEV_ORGANISATION_ID, "Issue_Link_Type_All"); //$NON-NLS-1$

	private void prepareProperties(){
		ISSUE_LINK_TYPE_ALL.getName().setText(Locale.ENGLISH.getLanguage(), Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositeIssueLinkRelated.issueLinkType.all.text")); //$NON-NLS-1$
	}
	private void loadProperties(){
		Job loadJob = new Job(Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositeIssueLinkRelated.job.loadingIssueLinkProperties.text")) { //$NON-NLS-1$
			@Override
			protected IStatus run(final ProgressMonitor monitor) {
				synchronized (mutex) {
					logger.debug("Load Job running...."); //$NON-NLS-1$
				}
				try {
					try {
						final List<IssueLinkType> issueLinkTypeList = new ArrayList<IssueLinkType>(IssueLinkTypeDAO.sharedInstance().
								getIssueLinkTypes(FETCH_GROUPS_ISSUE_LINK_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor));

						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								issueLinkTypeCombo.removeAll();
								issueLinkTypeCombo.addElement(ISSUE_LINK_TYPE_ALL);
								for (IssueLinkType issueLinkType : issueLinkTypeList) {
									issueLinkTypeCombo.addElement(issueLinkType);
								}
								if (selectedIssueLinkType == null)
									issueLinkTypeCombo.setSelection(ISSUE_LINK_TYPE_ALL);
								else {
									issueLinkTypeCombo.setSelection(selectedIssueLinkType);
								}

								logger.info("Display.getDefault().asyncExec: " + issueLinkTypeList);
							}
						});
					}catch (Exception e1) {
						ExceptionHandlerRegistry.asyncHandleException(e1);
						throw new RuntimeException(e1);
					}

					return Status.OK_STATUS;
				} finally {
					synchronized (mutex) {
						logger.debug("Load Job finished."); //$NON-NLS-1$
					}
				}
			}
		};
		loadJob.schedule();
	}

	private static final Set<String> fieldNames =
		Collections.singleton(IssueQuery.FieldName.issueLinkTypeID);

	@Override
	protected Set<String> getFieldNames()
	{
		return fieldNames;
	}

	/**
	 * Group ID for storing active states in the query.
	 */
	public static final String FILTER_GROUP_ID = "IssueFilterCompositeIssueLinkRelated"; //$NON-NLS-1$

	@Override
	protected String getGroupID()
	{
		return FILTER_GROUP_ID;
	}
}
