package org.nightlabs.jfire.trade.ui.overview.repository;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.store.RepositoryType;
import org.nightlabs.jfire.store.dao.RepositoryTypeDAO;
import org.nightlabs.jfire.store.id.RepositoryTypeID;
import org.nightlabs.jfire.store.query.RepositoryQuery;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntitySearchCreateWizard;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.NLLocale;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class RepositoryFilterComposite
	extends AbstractQueryFilterComposite<RepositoryQuery>
{
	private Button ownerActiveButton;
	private Text ownerText;
	private Button ownerBrowseButton;
	private Button repositoryTypeActiveButton;
	private XComboComposite<RepositoryType> repositoryTypeList;
	protected RepositoryTypeID selectedRepositoryTypeID;

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
	public RepositoryFilterComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode,
			QueryProvider<? super RepositoryQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
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
	public RepositoryFilterComposite(Composite parent, int style,
		QueryProvider<? super RepositoryQuery> queryProvider)
	{
		super(parent, style, queryProvider);
		createComposite();
	}

	@Override
	public Class<RepositoryQuery> getQueryClass() {
		return RepositoryQuery.class;
	}

	@Override
	protected void createComposite()
	{
		setLayout(new GridLayout(2, false));

		final Group ownerGroup = new Group(this, SWT.NONE);
		ownerGroup.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.repository.RepositorySearchComposite.ownerGroup.text")); //$NON-NLS-1$
		ownerGroup.setLayout(new GridLayout(2, false));
		ownerGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ownerActiveButton = new Button(ownerGroup, SWT.CHECK);
		ownerActiveButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.repository.RepositorySearchComposite.ownerActiveButton.text")); //$NON-NLS-1$
		GridData vendorLabelData = new GridData(GridData.FILL_HORIZONTAL);
		vendorLabelData.horizontalSpan = 2;
		ownerActiveButton.setLayoutData(vendorLabelData);
		ownerText = new Text(ownerGroup, getBorderStyle());
		ownerText.setEnabled(false);
		ownerText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ownerText.addSelectionListener(ownerSelectionListener);
		ownerBrowseButton = new Button(ownerGroup, SWT.NONE);
		ownerBrowseButton.setText("Browse"); //$NON-NLS-1$
		ownerBrowseButton.addSelectionListener(ownerSelectionListener);
		ownerBrowseButton.setEnabled(false);
		ownerActiveButton.addSelectionListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				getQuery().setFieldEnabled(RepositoryQuery.FieldName.ownerID, active);
			}
		});

		final Group repositoryTypeGroup = new Group(this, SWT.NONE);
		repositoryTypeGroup.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.repository.RepositorySearchComposite.anchorTypeIdGroup.text")); //$NON-NLS-1$
		repositoryTypeGroup.setLayout(new GridLayout());
		repositoryTypeGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		repositoryTypeActiveButton = new Button(repositoryTypeGroup, SWT.CHECK);
		repositoryTypeActiveButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.repository.RepositorySearchComposite.anchorTypeIdActiveButton.text")); //$NON-NLS-1$
		repositoryTypeActiveButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		repositoryTypeList = new XComboComposite<RepositoryType>(
				repositoryTypeGroup, getBorderStyle(),
				new LabelProvider() {
					@Override
					public String getText(Object element) {
						return ((RepositoryType)element).getName().getText();
					}
				}
		);
		repositoryTypeList.setEnabled(false);

		RepositoryType dummy = new RepositoryType(Messages.getString("org.nightlabs.jfire.trade.ui.overview.repository.RepositorySearchComposite.repositoryType.abc"), Messages.getString("org.nightlabs.jfire.trade.ui.overview.repository.RepositorySearchComposite.repositoryType.abc"), false); //$NON-NLS-1$ //$NON-NLS-2$
		dummy.getName().setText(NLLocale.getDefault().getLanguage(), Messages.getString("org.nightlabs.jfire.trade.ui.overview.repository.RepositorySearchComposite.name.loadingData")); //$NON-NLS-1$
		repositoryTypeList.setInput(Collections.singletonList(dummy));
		repositoryTypeList.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				final RepositoryTypeID selectedRepositoryTypeID = (RepositoryTypeID)
					JDOHelper.getObjectId(repositoryTypeList.getSelectedElement());
				getQuery().setRepositoryTypeID(selectedRepositoryTypeID);
			}
		});

		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.overview.repository.RepositorySearchComposite.job.loadingRepositoryTypes")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor)
					throws Exception
			{
				final List<RepositoryType> repositoryTypes = RepositoryTypeDAO.sharedInstance().getRepositoryTypes(
						new String[] {
								FetchPlan.DEFAULT,
								RepositoryType.FETCH_GROUP_NAME
						},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						monitor
				);

				Collections.sort(repositoryTypes, new Comparator<RepositoryType>() {
					@Override
					public int compare(RepositoryType o1, RepositoryType o2)
					{
						return o1.getName().getText().compareTo(o2.getName().getText());
					}
				});

				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						repositoryTypeList.setInput(repositoryTypes);
					}
				});

				return Status.OK_STATUS;
			}
		};
		job.schedule();

		repositoryTypeActiveButton.addSelectionListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				getQuery().setFieldEnabled(RepositoryQuery.FieldName.repositoryTypeID, active);
			}
		});
	}

	protected AnchorID selectedOwnerID = null;
	private SelectionListener ownerSelectionListener = new SelectionAdapter()
	{
		@Override
		public void widgetSelected(SelectionEvent e)
		{
			LegalEntity _legalEntity = LegalEntitySearchCreateWizard.open(ownerText.getText(), false);
			if (_legalEntity != null) {
				final AnchorID selectedOwnerID = (AnchorID) JDOHelper.getObjectId(_legalEntity);
				getQuery().setOwnerID(selectedOwnerID);

				// TODO perform this expensive code in a job
				LegalEntity legalEntity = LegalEntityDAO.sharedInstance().getLegalEntity(selectedOwnerID,
						new String[] {LegalEntity.FETCH_GROUP_PERSON, FetchPlan.DEFAULT},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new NullProgressMonitor());
				ownerText.setText(legalEntity.getPerson().getDisplayName());
			}
		}
	};

	private static final String Repository_Group_ID = "RepositoryFilterComposite"; //$NON-NLS-1$
	private static final Set<String> fieldNames;
	static
	{
		fieldNames = new HashSet<String>(5);
		fieldNames.add(RepositoryQuery.FieldName.repositoryTypeID);
		fieldNames.add(RepositoryQuery.FieldName.ownerID);
	}

	@Override
	protected Set<String> getFieldNames()
	{
		return fieldNames;
	}

	@Override
	protected String getGroupID()
	{
		return Repository_Group_ID;
	}

	@Override
	protected void updateUI(QueryEvent event, List<FieldChangeCarrier> changedFields)
	{
		for (FieldChangeCarrier fieldChange :changedFields)
		{
			if (RepositoryQuery.FieldName.ownerID.equals(fieldChange.getPropertyName()))
			{
				AnchorID tmpOwnerID = (AnchorID) fieldChange.getNewValue();
				if (tmpOwnerID == null)
				{
					ownerText.setText(""); //$NON-NLS-1$
				}
				else
				{
					final LegalEntity owner = LegalEntityDAO.sharedInstance().getLegalEntity(
						selectedOwnerID,
						new String[] {LegalEntity.FETCH_GROUP_PERSON, FetchPlan.DEFAULT},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new NullProgressMonitor()
					);
					ownerText.setText(owner.getPerson().getDisplayName());
				}
			}
			else if (getEnableFieldName(RepositoryQuery.FieldName.ownerID).equals(
					fieldChange.getPropertyName()))
			{
				final Boolean active = (Boolean) fieldChange.getNewValue();
				ownerText.setEnabled(active);
				ownerBrowseButton.setEnabled(active);
				setSearchSectionActive(ownerActiveButton, active);
			}
			else if (RepositoryQuery.FieldName.repositoryTypeID.equals(fieldChange.getPropertyName()))
			{
				RepositoryTypeID repoTypeID = (RepositoryTypeID) fieldChange.getNewValue();
				if (repoTypeID == null)
				{
					repositoryTypeList.setSelection((RepositoryType) null);
				}
				else
				{
					RepositoryType repoType = RepositoryTypeDAO.sharedInstance().getRepositoryType(
						repoTypeID, new String[] { FetchPlan.DEFAULT, RepositoryType.FETCH_GROUP_NAME	},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
					);
					repositoryTypeList.setSelection(repoType);
				}
			}
			else if (getEnableFieldName(RepositoryQuery.FieldName.repositoryTypeID).equals(
					fieldChange.getPropertyName()))
			{
				final Boolean active = (Boolean) fieldChange.getNewValue();
				repositoryTypeList.setEnabled(active);
				setSearchSectionActive(repositoryTypeActiveButton, active);
			}
		} // for (FieldChangeCarrier fieldChange : event.getChangedFields().values())
	}

}
