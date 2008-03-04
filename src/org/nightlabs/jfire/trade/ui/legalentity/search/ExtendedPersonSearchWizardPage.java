package org.nightlabs.jfire.trade.ui.legalentity.search;

import java.util.Collection;
import java.util.Collections;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.person.search.PersonSearchWizardPage;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.trade.CustomerGroup;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.id.CustomerGroupID;

public class ExtendedPersonSearchWizardPage extends PersonSearchWizardPage
{
	private LegalEntity legalEntity;
	private CustomerGroup defaultCustomerGroup;

	private boolean additionalDataLoaded = true;

	public ExtendedPersonSearchWizardPage(String quickSearchText) {
		super(quickSearchText);
	}

	@Override
	protected void onPersonSelectionChanged() {
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
			throw new IllegalStateException("Wrong thread! This method must be called on the UI thread!");

		final Person selectedPerson = getSelectedPerson();

		loadAdditionalDataJob = new Job("Loading customer data") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				additionalDataLoaded = false;

				// do expensive work
				LegalEntity legalEntity = null;
				try {
					TradeManager tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
					if (selectedPerson != null) {
						legalEntity = tradeManager.getLegalEntityForPerson(selectedPerson,
							new String[] { FetchPlan.DEFAULT, LegalEntity.FETCH_GROUP_DEFAULT_CUSTOMER_GROUP },
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT
						);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				final LegalEntity _legalEntity = legalEntity;

				final Job thisJob = this;
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (thisJob != loadAdditionalDataJob)
							return;

						// update whatever UI
						additionalDataLoaded = true;
						setLegalEntity(_legalEntity);
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

	private void setLegalEntity(LegalEntity legalEntity) {
		System.out.println("LE loaded...");
		this.legalEntity = legalEntity;
		if (legalEntity != null)
			defaultCustomerGroup = legalEntity.getDefaultCustomerGroup();
		else {
			try {
				TradeManager tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
				CustomerGroupID defaultCustomerGroupID = CustomerGroupID.create(IDGenerator.getOrganisationID(), CustomerGroup.CUSTOMER_GROUP_ID_DEFAULT);
				Collection<CustomerGroup> customerGroups = tradeManager.getCustomerGroups(Collections.singleton(defaultCustomerGroupID),
						new String[] { CustomerGroup.FETCH_GROUP_THIS_CUSTOMER_GROUP },	NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

				if (customerGroups.isEmpty())
					throw new IllegalStateException("DefaultCustomerGroup does not exist.");

				defaultCustomerGroup = customerGroups.iterator().next();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public LegalEntity getSelectedLegalEntity() {
		return legalEntity;
	}

	public CustomerGroup getDefaultCustomerGroup() {
		return defaultCustomerGroup;
	}

	@Override
	public boolean canFlipToNextPage() {
		return additionalDataLoaded;
	}

	public void onAdditionalDataLoaded() {

	}
}
