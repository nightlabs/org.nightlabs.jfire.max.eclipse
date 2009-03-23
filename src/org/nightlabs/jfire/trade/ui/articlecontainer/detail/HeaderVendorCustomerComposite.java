package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.person.search.PersonEditWizard;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.prop.StructLocal;
import org.nightlabs.jfire.prop.dao.StructLocalDAO;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

public class HeaderVendorCustomerComposite extends XComposite
{
	private Hyperlink vendorLink;
	private Hyperlink customerLink;
	private Hyperlink endCustomerLink;

	private AnchorID vendorID;
	private LegalEntity vendor;

	private AnchorID customerID;
	private LegalEntity customer;

	private AnchorID endCustomerID;
	private LegalEntity endCustomer;

	private Job activeJob;
	
	private static final String[] LEGAL_ENTITY_FETCH_GROUPS = new String[] { FetchPlan.DEFAULT, LegalEntity.FETCH_GROUP_PERSON };
	
	public HeaderVendorCustomerComposite(Composite parent) {
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		this.getGridLayout().numColumns = 3;
		this.getGridLayout().makeColumnsEqualWidth = true;
		this.getGridData().grabExcessHorizontalSpace = true;
		this.getGridData().grabExcessVerticalSpace = false;

		vendorLink = createLegalEntityHyperlink("Vendor");
		vendorLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				legalEntityLinkClicked(vendorID);
			}
		});

		customerLink = createLegalEntityHyperlink("Customer");
		customerLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				legalEntityLinkClicked(customerID);
			}
		});

		endCustomerLink = createLegalEntityHyperlink("End customer");
		endCustomerLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				legalEntityLinkClicked(endCustomerID);
			}
		});
	}

	private Hyperlink createLegalEntityHyperlink(String title) {
		Group legalEntityGroup = new Group(this, SWT.NONE);
		legalEntityGroup.setText(title);
		legalEntityGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		FillLayout fillLayout = new FillLayout();
		fillLayout.marginWidth = 8;
		legalEntityGroup.setLayout(fillLayout);
		Hyperlink hyperlink = new Hyperlink(legalEntityGroup, SWT.NONE);
		hyperlink.setText("");
		return hyperlink;
	}

	private static void updateLegalEntityHyperlink(Hyperlink hyperlink, LegalEntity legalEntity, boolean loading)
	{
		String text;
		if (loading)
			text = "Loading...";
		else {
			if (legalEntity != null && legalEntity.getPerson() != null)
				text = legalEntity.getPerson().getDisplayName();
			else
				text = "";
		}

		hyperlink.setText(text);
	}

	private void updateUI()
	{
		boolean loading = activeJob != null;

		updateLegalEntityHyperlink(vendorLink, vendor, loading);
		updateLegalEntityHyperlink(customerLink, customer, loading);
		updateLegalEntityHyperlink(endCustomerLink, endCustomer, loading);
	}

	public void setArticleContainer(ArticleContainer articleContainer)
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Thread mismatch! This method must be called on the SWT UI Thread!");

		this.activeJob = null;

		vendorID = articleContainer == null ? null : articleContainer.getVendorID();
		vendor = null;

		customerID = articleContainer == null ? null : articleContainer.getCustomerID();
		customer = null;

		endCustomerID = articleContainer == null ? null : articleContainer.getEndCustomerID();
		endCustomer = null;

		final Set<AnchorID> anchorIDs = new HashSet<AnchorID>(3);
		if (vendorID != null)
			anchorIDs.add(vendorID);

		if (customerID != null)
			anchorIDs.add(customerID);

		if (endCustomerID != null)
			anchorIDs.add(endCustomerID);


		if (anchorIDs.isEmpty()) {
			updateUI();
			return; // all empty => nothing to do
		}

		final Display display = getDisplay();
		Job job = new Job("Loading business partners") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception
			{
				final Job thisJob = this;
				if (thisJob != activeJob)
					return Status.CANCEL_STATUS;

				final Collection<? extends LegalEntity> legalEntities = LegalEntityDAO.sharedInstance().getLegalEntities(
						anchorIDs,
						LEGAL_ENTITY_FETCH_GROUPS,
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						monitor
				);

				display.asyncExec(new Runnable() {
					public void run() {
						if (isDisposed())
							return;

						if (thisJob != activeJob)
							return;

						for (LegalEntity legalEntity : legalEntities) {
							AnchorID anchorID = (AnchorID) JDOHelper.getObjectId(legalEntity);
							if (anchorID.equals(vendorID))
								vendor = legalEntity;
							else if (anchorID.equals(customerID))
								customer = legalEntity;
							else if (anchorID.equals(endCustomerID))
								endCustomer = legalEntity;
						}

						activeJob = null;
						updateUI();
					}
				});

				return Status.OK_STATUS;
			}
		};
		this.activeJob = job;

		updateUI();

		job.schedule();
	}

	private void legalEntityLinkClicked(AnchorID legalEntityID)
	{
		if (legalEntityID == null)
			return; // silently ignore

//		MessageDialog.openInformation(getShell(), "Test", "Clicked for legal entity: " + legalEntityID);
		LegalEntity legalEntity = LegalEntityDAO.sharedInstance().getLegalEntity(legalEntityID, 
				new String[] {FetchPlan.DEFAULT, LegalEntity.FETCH_GROUP_PERSON, Person.FETCH_GROUP_FULL_DATA}, 
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
		if (!legalEntity.isAnonymous() && legalEntity != null) 
		{
			Person person = legalEntity.getPerson();
			StructLocal structLocal = StructLocalDAO.sharedInstance().getStructLocal(
					person.getStructLocalObjectID(), new NullProgressMonitor()
			);
			person.inflate(structLocal);			
			PersonEditWizard wizard = new PersonEditWizard(person);
			DynamicPathWizardDialog dlg = new DynamicPathWizardDialog(wizard);
			int returnCode = dlg.open();
			if (returnCode == Window.OK) {
				if (legalEntityID.equals(customerID)) {
					customer = LegalEntityDAO.sharedInstance().getLegalEntity(legalEntityID, LEGAL_ENTITY_FETCH_GROUPS, 
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
				}
				else if (legalEntityID.equals(vendorID)) {
					vendor = LegalEntityDAO.sharedInstance().getLegalEntity(legalEntityID, LEGAL_ENTITY_FETCH_GROUPS, 
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
				}
				else if (legalEntityID.equals(endCustomerID)) {
					endCustomer = LegalEntityDAO.sharedInstance().getLegalEntity(legalEntityID, LEGAL_ENTITY_FETCH_GROUPS, 
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
				}				
				updateUI();
			}
		}
	}
}
