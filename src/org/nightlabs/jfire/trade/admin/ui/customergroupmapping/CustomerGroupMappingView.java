package org.nightlabs.jfire.trade.admin.ui.customergroupmapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.trade.CustomerGroup;
import org.nightlabs.jfire.trade.CustomerGroupMapping;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.customergroup.CustomerGroupListComposite;
import org.nightlabs.jfire.trade.id.CustomerGroupID;

public class CustomerGroupMappingView
extends LSDViewPart
{
	public static final String ID_VIEW = CustomerGroupMappingView.class.getName();

	private CustomerGroupListComposite partnerCustomerGroupList;
	private CustomerGroupListComposite localCustomerGroupList;
	private CustomerGroupMappingTable customerGroupMappingTable;

	private Button createCustomerGroupMappingButton;
	private Button removeCustomerGroupMappingButton;

	private Set<CustomerGroupID> partnerCustomerGroupIDs = null;
	private Set<CustomerGroupID> localCustomerGroupIDsInMappingsForSelectedPartnerOrganisationID = null;

	private CustomerGroupListComposite.CustomerGroupFilter partnerCustomerGroupFilter = new CustomerGroupListComposite.CustomerGroupFilter() {
		public boolean includeCustomerGroup(CustomerGroup customerGroup)
		{
			if (partnerCustomerGroupIDs == null) {
				Set<CustomerGroupMapping> customerGroupMappings = customerGroupMappingTable.getCustomerGroupMappings(true);
				Set<CustomerGroupID> customerGroupIDs = new HashSet<CustomerGroupID>(customerGroupMappings.size());
				for (CustomerGroupMapping customerGroupMapping : customerGroupMappings)
					customerGroupIDs.add((CustomerGroupID) JDOHelper.getObjectId(customerGroupMapping.getPartnerCustomerGroup()));

				partnerCustomerGroupIDs = customerGroupIDs;
			}

			return !partnerCustomerGroupIDs.contains(JDOHelper.getObjectId(customerGroup));
		}
	};

	private CustomerGroupListComposite.CustomerGroupFilter localCustomerGroupFilter = new CustomerGroupListComposite.CustomerGroupFilter() {
		public boolean includeCustomerGroup(CustomerGroup customerGroup)
		{
			if (selectedPartnerCustomerGroup == null)
				return false;

			if (localCustomerGroupIDsInMappingsForSelectedPartnerOrganisationID == null) {
				Set<CustomerGroupMapping> customerGroupMappings = customerGroupMappingTable.getCustomerGroupMappings(true);
				Set<CustomerGroupID> customerGroupIDs = new HashSet<CustomerGroupID>(customerGroupMappings.size());
				for (CustomerGroupMapping customerGroupMapping : customerGroupMappings) {
					if (selectedPartnerCustomerGroup.getOrganisationID().equals(customerGroupMapping.getPartnerCustomerGroupOrganisationID()))
						customerGroupIDs.add((CustomerGroupID) JDOHelper.getObjectId(customerGroupMapping.getLocalCustomerGroup()));
				}

				localCustomerGroupIDsInMappingsForSelectedPartnerOrganisationID = customerGroupIDs;
			}

			return !localCustomerGroupIDsInMappingsForSelectedPartnerOrganisationID.contains(JDOHelper.getObjectId(customerGroup));
		}
	};

	private CustomerGroup selectedPartnerCustomerGroup;
	private CustomerGroup selectedLocalCustomerGroup;

	public void createPartContents(Composite parent)
	{
		XComposite page = new XComposite(parent, SWT.NONE);

		SashForm sfMainVert = new SashForm(page, SWT.VERTICAL);
		sfMainVert.setLayoutData(new GridData(GridData.FILL_BOTH));

		SashForm sfTopHoriz = new SashForm(sfMainVert, SWT.HORIZONTAL);
		partnerCustomerGroupList = new CustomerGroupListComposite(sfTopHoriz, SWT.NONE, false, partnerCustomerGroupFilter);
		partnerCustomerGroupList.setOrganisationVisible(true);
		partnerCustomerGroupList.setFilterOrganisationIDInverse(true);
		partnerCustomerGroupList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				selectedPartnerCustomerGroup = partnerCustomerGroupList.getSelectedCustomerGroup();
				localCustomerGroupIDsInMappingsForSelectedPartnerOrganisationID = null; // we must nullify it, when the partner-organisationID changes, but currently, we nullify on every change - I'm too lazy ;-)
				localCustomerGroupList.loadCustomerGroups();

				createCustomerGroupMappingButton.setEnabled(selectedPartnerCustomerGroup != null && selectedLocalCustomerGroup != null);
			}
		});

		localCustomerGroupList = new CustomerGroupListComposite(sfTopHoriz, SWT.NONE, false, localCustomerGroupFilter);
		localCustomerGroupList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				selectedLocalCustomerGroup = localCustomerGroupList.getSelectedCustomerGroup();

				createCustomerGroupMappingButton.setEnabled(selectedPartnerCustomerGroup != null && selectedLocalCustomerGroup != null);
			}
		});

		sfTopHoriz.setWeights(new int[] {60, 40});

		XComposite bottom = new XComposite(sfMainVert, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		bottom.getGridLayout().numColumns = 2;

		XComposite btnComp = new XComposite(bottom, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		btnComp.getGridData().grabExcessHorizontalSpace = false;
		btnComp.getGridData().horizontalAlignment = GridData.BEGINNING;

		createCustomerGroupMappingButton = new Button(btnComp, SWT.PUSH);
		createCustomerGroupMappingButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createCustomerGroupMappingButton.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.customergroupmapping.CustomerGroupMappingView.createCustomerGroupMappingButton.text")); //$NON-NLS-1$
		createCustomerGroupMappingButton.setEnabled(false);
		createCustomerGroupMappingButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				createCustomerGroupMappingButton.setEnabled(false);
				createCustomerGroupMapping();
			}
		});

		removeCustomerGroupMappingButton = new Button(btnComp, SWT.PUSH);
		removeCustomerGroupMappingButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeCustomerGroupMappingButton.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.customergroupmapping.CustomerGroupMappingView.removeCustomerGroupMappingButton.text")); //$NON-NLS-1$
		removeCustomerGroupMappingButton.setEnabled(false);
		removeCustomerGroupMappingButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				removeCustomerGroupMappingButton.setEnabled(false);
				removeCustomerGroupMappings();
			}
		});


		customerGroupMappingTable = new CustomerGroupMappingTable(bottom, SWT.NONE);
		customerGroupMappingTable.loadCustomerGroupMappings();
		customerGroupMappingTable.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				boolean enabled = !customerGroupMappingTable.getSelectedElements().isEmpty();

				for (Iterator it = customerGroupMappingTable.getSelectedElements().iterator(); it.hasNext(); ) {
					CustomerGroupMapping tm = (CustomerGroupMapping) it.next();
					if (JDOHelper.getObjectId(tm) != null)
						enabled = false;
				}

				removeCustomerGroupMappingButton.setEnabled(enabled);
			}
		});

		partnerCustomerGroupList.loadCustomerGroups();
	}

	public void createCustomerGroupMapping()
	{
		if (selectedPartnerCustomerGroup == null || selectedLocalCustomerGroup == null)
			return;

		CustomerGroupMapping tm = new CustomerGroupMapping(selectedPartnerCustomerGroup, selectedLocalCustomerGroup);
		customerGroupMappingTable.addClientOnlyCustomerGroupMapping(tm);

		partnerCustomerGroupIDs = null;
		localCustomerGroupIDsInMappingsForSelectedPartnerOrganisationID = null;
		partnerCustomerGroupList.loadCustomerGroups();
		localCustomerGroupList.loadCustomerGroups();
	}

	public void removeCustomerGroupMappings()
	{
		List<CustomerGroupMapping> customerGroupMappingsToDelete = new ArrayList<CustomerGroupMapping>();
		for (Iterator it = customerGroupMappingTable.getSelectedElements().iterator(); it.hasNext();) {
			CustomerGroupMapping tm = (CustomerGroupMapping) it.next();
			if (JDOHelper.getObjectId(tm) == null)
				customerGroupMappingsToDelete.add(tm);
		}

		if (customerGroupMappingsToDelete.isEmpty())
			return;

		customerGroupMappingTable.removeClientOnlyCustomerGroupMappings(customerGroupMappingsToDelete);

		partnerCustomerGroupIDs = null;
		localCustomerGroupIDsInMappingsForSelectedPartnerOrganisationID = null;
		partnerCustomerGroupList.loadCustomerGroups();
		localCustomerGroupList.loadCustomerGroups();
	}

	public void storeClientOnlyCustomerGroupMappingsToServer()
	{
		customerGroupMappingTable.storeClientOnlyCustomerGroupMappingsToServer();
	}

	@Override
	public void setFocus()
	{
		if (customerGroupMappingTable != null)
			customerGroupMappingTable.setFocus();
	}
}
