package org.nightlabs.jfire.trade.ui.overview.repository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.LabelProvider;
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
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.overview.search.JDOQueryComposite;
import org.nightlabs.jfire.store.Repository;
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

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
@Deprecated
public class RepositorySearchComposite
	extends JDOQueryComposite<Repository, RepositoryQuery>
{
	public RepositorySearchComposite(AbstractQueryFilterComposite<Repository, RepositoryQuery> parent,
		int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode)
	{
		super(parent, style, layoutMode, layoutDataMode);
		createComposite(this);
	}

	public RepositorySearchComposite(AbstractQueryFilterComposite<Repository, RepositoryQuery> parent,
		int style)
	{
		super(parent, style);
		createComposite(this);
	}

	private Button ownerActiveButton = null;
	private Text ownerText = null;
	private Button ownerBrowseButton = null;
	private Button repositoryTypeActiveButton = null;
	private XComboComposite<RepositoryType> repositoryTypeList = null;
	
	@Override
	protected void createComposite(Composite parent)
	{
		parent.setLayout(new GridLayout(2, false));
		
		final Group ownerGroup = new Group(parent, SWT.NONE);
		ownerGroup.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.repository.RepositorySearchComposite.ownerGroup.text")); //$NON-NLS-1$
		ownerGroup.setLayout(new GridLayout(2, false));
		ownerGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ownerActiveButton = new Button(ownerGroup, SWT.CHECK);
		ownerActiveButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.repository.RepositorySearchComposite.ownerActiveButton.text")); //$NON-NLS-1$
		GridData vendorLabelData = new GridData(GridData.FILL_HORIZONTAL);
		vendorLabelData.horizontalSpan = 2;
		ownerActiveButton.setLayoutData(vendorLabelData);
		ownerText = new Text(ownerGroup, SWT.BORDER);
		ownerText.setEnabled(false);
		ownerText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ownerText.addSelectionListener(ownerSelectionListener);
		ownerBrowseButton = new Button(ownerGroup, SWT.NONE);
		ownerBrowseButton.setText("Browse"); //$NON-NLS-1$
		ownerBrowseButton.addSelectionListener(ownerSelectionListener);
		ownerBrowseButton.setEnabled(false);
		ownerActiveButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				boolean active = ((Button)e.getSource()).getSelection();
				ownerText.setEnabled(active);
				ownerBrowseButton.setEnabled(active);
				if (isUpdatingUI())
					return;
				
				if (active)
				{
					getQuery().setOwnerID(selectedOwnerID);
				}
				else
				{
					getQuery().setOwnerID(null);
				}
			}
		});

		final Group repositoryTypeGroup = new Group(parent, SWT.NONE);
		repositoryTypeGroup.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.repository.RepositorySearchComposite.anchorTypeIdGroup.text")); //$NON-NLS-1$
		repositoryTypeGroup.setLayout(new GridLayout());
		repositoryTypeGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		repositoryTypeActiveButton = new Button(repositoryTypeGroup, SWT.CHECK);
		repositoryTypeActiveButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.repository.RepositorySearchComposite.anchorTypeIdActiveButton.text")); //$NON-NLS-1$
		repositoryTypeActiveButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		repositoryTypeList = new XComboComposite<RepositoryType>(
				repositoryTypeGroup, SWT.BORDER,
				new LabelProvider() {
					@Override
					public String getText(Object element) {
						return ((RepositoryType)element).getName().getText();
					}
				}
		);
		repositoryTypeList.setEnabled(false);

		RepositoryType dummy = new RepositoryType(Messages.getString("org.nightlabs.jfire.trade.ui.overview.repository.RepositorySearchComposite.repositoryType.abc"), Messages.getString("org.nightlabs.jfire.trade.ui.overview.repository.RepositorySearchComposite.repositoryType.abc"), false); //$NON-NLS-1$ //$NON-NLS-2$
		dummy.getName().setText(Locale.getDefault().getLanguage(), Messages.getString("org.nightlabs.jfire.trade.ui.overview.repository.RepositorySearchComposite.name.loadingData")); //$NON-NLS-1$
		repositoryTypeList.setInput(Collections.singletonList(dummy));

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

		repositoryTypeActiveButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				boolean active = ((Button)e.getSource()).getSelection();
				repositoryTypeList.setEnabled(active);
				if (isUpdatingUI())
					return;
				
				if (active)
				{
					getQuery().setRepositoryTypeID((RepositoryTypeID)
						JDOHelper.getObjectId(repositoryTypeList.getSelectedElement()));
				}
				else
				{
					getQuery().setRepositoryTypeID(null);
				}
			}
		});
	}
	
	private AnchorID selectedOwnerID = null;
	private SelectionListener ownerSelectionListener = new SelectionAdapter()
	{
		@Override
		public void widgetSelected(SelectionEvent e)
		{
			LegalEntity _legalEntity = LegalEntitySearchCreateWizard.open(ownerText.getText(), false);
			if (_legalEntity != null) {
				selectedOwnerID = (AnchorID) JDOHelper.getObjectId(_legalEntity);
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

	@Override
	protected void resetSearchQueryValues(RepositoryQuery query)
	{
		query.setOwnerID(selectedOwnerID);
		query.setRepositoryTypeID((RepositoryTypeID) JDOHelper.getObjectId(repositoryTypeList.getSelectedElement()));
	}

	@Override
	protected void unsetSearchQueryValues(RepositoryQuery query)
	{
		query.setOwnerID(null);
		query.setRepositoryTypeID(null);
	}

	@Override
	protected void updateUI(QueryEvent event)
	{
		final boolean wholeQueryChanged = isWholeQueryChanged(event);		
		final RepositoryQuery changedQuery = (RepositoryQuery) event.getChangedQuery();
		
		RepositoryTypeID repositoryTypeID = 
			(RepositoryTypeID) JDOHelper.getObjectId(repositoryTypeList.getSelectedElement());
		
		if (changedQuery == null)
		{
			selectedOwnerID = null;
			repositoryTypeID = null;
		}
		else
		{
			if (wholeQueryChanged || RepositoryQuery.PROPERTY_OWNER_ID.equals(event.getPropertyName()))
			{
				selectedOwnerID = changedQuery.getOwnerID();
			}
			
			if (wholeQueryChanged || RepositoryQuery.PROPERTY_REPOSITORY_TYPE_ID.equals(event.getPropertyName()))
			{
				repositoryTypeID = changedQuery.getRepositoryTypeID();
			}
		}
		
		ownerActiveButton.setSelection(selectedOwnerID != null);
		if (selectedOwnerID != null)
		{
			final LegalEntity owner = LegalEntityDAO.sharedInstance().getLegalEntity(selectedOwnerID,
				new String[] {LegalEntity.FETCH_GROUP_PERSON, FetchPlan.DEFAULT},
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new NullProgressMonitor()
			);
			ownerText.setText(owner.getPerson().getDisplayName());
		}
		else
		{
			ownerText.setText("");
		}
		
		repositoryTypeActiveButton.setSelection(repositoryTypeID != null);
		if (repositoryTypeID != null)
		{
			RepositoryType repoType = RepositoryTypeDAO.sharedInstance().getRepositoryType(
				repositoryTypeID, new String[] { FetchPlan.DEFAULT, RepositoryType.FETCH_GROUP_NAME	},
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
				);
			repositoryTypeList.setSelection(repoType);
		}
		else
		{
			repositoryTypeList.setSelection((RepositoryType)null);
		}
	}
	
}
