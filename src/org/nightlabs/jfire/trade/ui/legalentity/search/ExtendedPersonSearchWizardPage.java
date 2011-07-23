package org.nightlabs.jfire.trade.ui.legalentity.search;

import java.util.Collection;
import java.util.Collections;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.base.ui.person.search.PersonSearchWizardPage;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.jfire.trade.CustomerGroup;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.TradeManagerRemote;
import org.nightlabs.jfire.trade.id.CustomerGroupID;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

public class ExtendedPersonSearchWizardPage extends PersonSearchWizardPage
{
	private LegalEntity legalEntity;
	private CustomerGroup defaultCustomerGroup;

//	private boolean additionalDataLoaded = true;

	public ExtendedPersonSearchWizardPage(String quickSearchText) {
		super(quickSearchText);
	}

	public ExtendedPersonSearchWizardPage(String quickSearchText, boolean allowNewLegalEntityCreation, boolean allowEditLegalEntity) {
		super(quickSearchText, allowNewLegalEntityCreation, allowEditLegalEntity);
	}

	@Override
	protected void onPersonSelectionChanged() {
		super.onPersonSelectionChanged();
		getLoadAdditionalDataJob(null).schedule();
	}

	@Override
	protected void personDoubleClicked() {
		Job job = getLoadAdditionalDataJob(new Runnable() {
			@Override
			public void run() {
				getContainer().showPage(getNextPage());
			}
		});
		job.setUser(true);
		job.schedule();
	}

	private Job loadAdditionalDataJob;
	private Job getLoadAdditionalDataJob(final Runnable runOnLoaded) {
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Wrong thread! This method must be called on the UI thread!"); //$NON-NLS-1$

		final Person selectedPerson = getSelectedPerson();

		loadAdditionalDataJob = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.legalentity.search.ExtendedPersonSearchWizardPage.job.loadingCustomerData")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						legalEntity = null;
						defaultCustomerGroup = null;
						getContainer().updateButtons();
					}
				});
//				additionalDataLoaded = false;

				// do expensive work
				LegalEntity __legalEntity = null;
				CustomerGroup __defaultCustomerGroup = null;
				try {
					TradeManagerRemote tradeManager = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
					if (selectedPerson != null && JDOHelper.getObjectId(selectedPerson) != null) {
						// only if there is a person selected/edited we can make it a legal entity on the server
						// if it is a newly (on the client) created person we can't
						__legalEntity = tradeManager.getLegalEntityForPerson(
								(PropertySetID) JDOHelper.getObjectId(selectedPerson),
								new String[] {
										FetchPlan.DEFAULT, LegalEntity.FETCH_GROUP_DEFAULT_CUSTOMER_GROUP
								},
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT
						);
					}

					CustomerGroupID defaultCustomerGroupID = CustomerGroupID.create(IDGenerator.getOrganisationID(), CustomerGroup.CUSTOMER_GROUP_ID_DEFAULT);
					Collection<CustomerGroup> customerGroups = tradeManager.getCustomerGroups(Collections.singleton(defaultCustomerGroupID),
							new String[] { CustomerGroup.FETCH_GROUP_THIS_CUSTOMER_GROUP },	NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

					if (customerGroups.isEmpty())
						throw new IllegalStateException("DefaultCustomerGroup does not exist."); //$NON-NLS-1$

					__defaultCustomerGroup = customerGroups.iterator().next();

				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				final LegalEntity _legalEntity = __legalEntity;
				final CustomerGroup _defaultCustomerGroup = __defaultCustomerGroup;

				final Job thisJob = this;
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (thisJob != loadAdditionalDataJob)
							return;

//						additionalDataLoaded = true;
						legalEntity = _legalEntity;
						defaultCustomerGroup = _defaultCustomerGroup;
						getContainer().updateButtons();

						// give the gc the chance to collect already before we close the wizard (not important but nicer)
						loadAdditionalDataJob = null;

						if (runOnLoaded != null)
							runOnLoaded.run();

						onAdditionalDataLoaded();
					}
				});

				return Status.OK_STATUS;
			}
		};

		return loadAdditionalDataJob;
	}

//	private void setLegalEntity(LegalEntity legalEntity) {
//		this.legalEntity = legalEntity;
//		if (legalEntity != null)
//			defaultCustomerGroup = legalEntity.getDefaultCustomerGroup();
//		else {
//			try {
//				TradeManager tradeManager = JFireEjbFactory.getBean(TradeManager.class, Login.getLogin().getInitialContextProperties());
//				CustomerGroupID defaultCustomerGroupID = CustomerGroupID.create(IDGenerator.getOrganisationID(), CustomerGroup.CUSTOMER_GROUP_ID_DEFAULT);
//				Collection<CustomerGroup> customerGroups = tradeManager.getCustomerGroups(Collections.singleton(defaultCustomerGroupID),
//						new String[] { CustomerGroup.FETCH_GROUP_THIS_CUSTOMER_GROUP },	NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//
//				if (customerGroups.isEmpty())
//					throw new IllegalStateException("DefaultCustomerGroup does not exist."); //$NON-NLS-1$
//
//				defaultCustomerGroup = customerGroups.iterator().next();
//			} catch (Exception e) {
//				throw new RuntimeException(e);
//			}
//		}
//	}

	public LegalEntity getSelectedLegalEntity() {
		return legalEntity;
	}

	public CustomerGroup getDefaultCustomerGroup() {
		return defaultCustomerGroup;
	}

	@Override
	public boolean canFlipToNextPage() {
		return legalEntity != null;
	}

	public void onAdditionalDataLoaded() {
	}

	public boolean isLoadAdditionalDataJobRunning() {
		return loadAdditionalDataJob != null;
	}

//	@Override
//	protected void newPersonPressed() {
//		super.newPersonPressed();
//		legalEntity = null;
//	}
//
//	@Override
//	protected void editPersonPressed() {
//		super.editPersonPressed();
//		legalEntity = null;
//	}
}
