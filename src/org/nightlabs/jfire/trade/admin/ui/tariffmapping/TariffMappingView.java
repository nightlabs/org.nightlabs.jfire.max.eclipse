package org.nightlabs.jfire.trade.admin.ui.tariffmapping;

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
import org.nightlabs.jfire.accounting.Tariff;
import org.nightlabs.jfire.accounting.TariffMapping;
import org.nightlabs.jfire.accounting.id.TariffID;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.tariff.TariffListComposite;

public class TariffMappingView
extends LSDViewPart
{
	public static final String ID_VIEW = TariffMappingView.class.getName();

	private TariffListComposite partnerTariffList;
	private TariffListComposite localTariffList;
	private TariffMappingTable tariffMappingTable;

	private Button createTariffMappingButton;
	private Button removeTariffMappingButton;

	private Set<TariffID> partnerTariffIDs = null;
	private Set<TariffID> localTariffIDsInMappingsForSelectedPartnerOrganisationID = null;

	private TariffListComposite.TariffFilter partnerTariffFilter = new TariffListComposite.TariffFilter() {
		public boolean includeTariff(Tariff tariff)
		{
			if (partnerTariffIDs == null) {
				Set<TariffMapping> tariffMappings = tariffMappingTable.getTariffMappings(true);
				Set<TariffID> tariffIDs = new HashSet<TariffID>(tariffMappings.size());
				for (TariffMapping tariffMapping : tariffMappings)
					tariffIDs.add((TariffID) JDOHelper.getObjectId(tariffMapping.getPartnerTariff()));

				partnerTariffIDs = tariffIDs;
			}

			return !partnerTariffIDs.contains(JDOHelper.getObjectId(tariff));
		}
	};

	private TariffListComposite.TariffFilter localTariffFilter = new TariffListComposite.TariffFilter() {
		public boolean includeTariff(Tariff tariff)
		{
			if (selectedPartnerTariff == null)
				return false;

			if (localTariffIDsInMappingsForSelectedPartnerOrganisationID == null) {
				Set<TariffMapping> tariffMappings = tariffMappingTable.getTariffMappings(true);
				Set<TariffID> tariffIDs = new HashSet<TariffID>(tariffMappings.size());
				for (TariffMapping tariffMapping : tariffMappings) {
					if (selectedPartnerTariff.getOrganisationID().equals(tariffMapping.getPartnerTariffOrganisationID()))
						tariffIDs.add((TariffID) JDOHelper.getObjectId(tariffMapping.getLocalTariff()));
				}

				localTariffIDsInMappingsForSelectedPartnerOrganisationID = tariffIDs;
			}

			return !localTariffIDsInMappingsForSelectedPartnerOrganisationID.contains(JDOHelper.getObjectId(tariff));
		}
	};

	private Tariff selectedPartnerTariff;
	private Tariff selectedLocalTariff;

	public void createPartContents(Composite parent)
	{
		XComposite page = new XComposite(parent, SWT.NONE);

		SashForm sfMainVert = new SashForm(page, SWT.VERTICAL);
		sfMainVert.setLayoutData(new GridData(GridData.FILL_BOTH));

		SashForm sfTopHoriz = new SashForm(sfMainVert, SWT.HORIZONTAL);
		partnerTariffList = new TariffListComposite(sfTopHoriz, SWT.NONE, false, partnerTariffFilter);
		partnerTariffList.setOrganisationVisible(true);
		partnerTariffList.setFilterOrganisationIDInverse(true);
		partnerTariffList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				selectedPartnerTariff = partnerTariffList.getSelectedTariff();
				localTariffIDsInMappingsForSelectedPartnerOrganisationID = null; // we must nullify it, when the partner-organisationID changes, but currently, we nullify on every change - I'm too lazy ;-)
				localTariffList.loadTariffs();

				createTariffMappingButton.setEnabled(selectedPartnerTariff != null && selectedLocalTariff != null);
			}
		});

		localTariffList = new TariffListComposite(sfTopHoriz, SWT.NONE, false, localTariffFilter);
		localTariffList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				selectedLocalTariff = localTariffList.getSelectedTariff();

				createTariffMappingButton.setEnabled(selectedPartnerTariff != null && selectedLocalTariff != null);
			}
		});

		sfTopHoriz.setWeights(new int[] {50, 50});

		XComposite bottom = new XComposite(sfMainVert, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		bottom.getGridLayout().numColumns = 2;

		tariffMappingTable = new TariffMappingTable(bottom, SWT.NONE);
		tariffMappingTable.loadTariffMappings();
		tariffMappingTable.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				boolean enabled = !tariffMappingTable.getSelectedElements().isEmpty();

				for (Iterator it = tariffMappingTable.getSelectedElements().iterator(); it.hasNext(); ) {
					TariffMapping tm = (TariffMapping) it.next();
					if (JDOHelper.getObjectId(tm) != null)
						enabled = false;
				}

				removeTariffMappingButton.setEnabled(enabled);
			}
		});

		XComposite btnComp = new XComposite(bottom, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		btnComp.getGridData().grabExcessHorizontalSpace = false;
		btnComp.getGridData().horizontalAlignment = GridData.BEGINNING;

		createTariffMappingButton = new Button(btnComp, SWT.PUSH);
		createTariffMappingButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createTariffMappingButton.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.tariffmapping.TariffMappingView.createTariffMappingButton.text")); //$NON-NLS-1$
		createTariffMappingButton.setEnabled(false);
		createTariffMappingButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				createTariffMappingButton.setEnabled(false);
				createTariffMapping();
			}
		});

		removeTariffMappingButton = new Button(btnComp, SWT.PUSH);
		removeTariffMappingButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeTariffMappingButton.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.tariffmapping.TariffMappingView.removeTariffMappingButton.text")); //$NON-NLS-1$
		removeTariffMappingButton.setEnabled(false);
		removeTariffMappingButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				removeTariffMappingButton.setEnabled(false);
				removeTariffMappings();
			}
		});		
		
		partnerTariffList.loadTariffs();
	}

	public void createTariffMapping()
	{
		if (selectedPartnerTariff == null || selectedLocalTariff == null)
			return;

		TariffMapping tm = new TariffMapping(selectedPartnerTariff, selectedLocalTariff);
		tariffMappingTable.addClientOnlyTariffMapping(tm);

		partnerTariffIDs = null;
		localTariffIDsInMappingsForSelectedPartnerOrganisationID = null;
		partnerTariffList.loadTariffs();
		localTariffList.loadTariffs();
	}

	public void removeTariffMappings()
	{
		List<TariffMapping> tariffMappingsToDelete = new ArrayList<TariffMapping>();
		for (Iterator it = tariffMappingTable.getSelectedElements().iterator(); it.hasNext();) {
			TariffMapping tm = (TariffMapping) it.next();
			if (JDOHelper.getObjectId(tm) == null)
				tariffMappingsToDelete.add(tm);
		}

		if (tariffMappingsToDelete.isEmpty())
			return;

		tariffMappingTable.removeClientOnlyTariffMappings(tariffMappingsToDelete);

		partnerTariffIDs = null;
		localTariffIDsInMappingsForSelectedPartnerOrganisationID = null;
		partnerTariffList.loadTariffs();
		localTariffList.loadTariffs();
	}

	public void storeClientOnlyTariffMappingsToServer()
	{
		tariffMappingTable.storeClientOnlyTariffMappingsToServer();
	}

	@Override
	public void setFocus()
	{
		if (tariffMappingTable != null)
			tariffMappingTable.setFocus();
	}
}
