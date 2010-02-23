package org.nightlabs.jfire.trade.ui.legalentity.search.config;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.clientui.ui.layout.GridLayoutConfigComposite;
import org.nightlabs.clientui.ui.layout.IGridLayoutConfig;
import org.nightlabs.jfire.base.ui.layout.AbstractEditLayoutPreferencePage;
import org.nightlabs.jfire.base.ui.person.search.AbstractPersonSearchEditLayoutPreferencePage;
import org.nightlabs.jfire.base.ui.person.search.config.PersonSearchGridLayoutConfig;
import org.nightlabs.jfire.layout.AbstractEditLayoutConfigModule;
import org.nightlabs.jfire.layout.EditLayoutEntry;
import org.nightlabs.jfire.prop.search.config.StructFieldSearchEditLayoutEntry;
import org.nightlabs.jfire.trade.ui.legalentity.search.PersonSearchUseCaseConstants;

public class LegalEntitySearchPreferencePage extends AbstractPersonSearchEditLayoutPreferencePage {

	@Override
	public String getConfigModuleID() {
		return AbstractEditLayoutConfigModule.getCfModID(AbstractEditLayoutConfigModule.CLIENT_TYPE_RCP, PersonSearchUseCaseConstants.USE_CASE_ID_LEGALENTITY_SEARCH);
	}

	@Override
	public String getUseCaseDescription() {
		return "You can configure the search for legal entities here, which is used in the trade perspective.";
	}

	@Override
	protected IGridLayoutConfig createConfigModuleGridLayoutConfig() {
		return new PersonSearchGridLayoutConfig(getPersonSearchConfigModule()) {
			@Override
			protected String getGridDataEntryName(StructFieldSearchEditLayoutEntry entry) {
				String name = super.getGridDataEntryName(entry);
				if (entry.equals(getPersonSearchConfigModule().getQuickSearchEntry()))
					name += " [*]";
				
				return name;
			}
		};
	}
	
	@Override
	protected void createFooterComposite(Composite wrapper, final GridLayoutConfigComposite gridLayoutConfigComposite, final AbstractEditLayoutPreferencePage abstractEditLayoutPreferencePage) {
		Button button = new Button(wrapper, SWT.PUSH);
		button.setText("Set as quick search");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final IGridLayoutConfig currentGridLayoutConfig = abstractEditLayoutPreferencePage.getCurrentGridLayoutConfig();
				if (PersonSearchGridLayoutConfig.class.isAssignableFrom(currentGridLayoutConfig.getClass())) {
					StructFieldSearchEditLayoutEntry layoutEntry =
						((PersonSearchGridLayoutConfig) currentGridLayoutConfig).getSearchEntryForGridDataEntry(gridLayoutConfigComposite.getSelectedGridDataEntry());
					
					if (layoutEntry.getEntryType().equals(EditLayoutEntry.ENTRY_TYPE_SEPARATOR)) {
						MessageDialog.openError(getShell(), "Cannot select separator as quick search item", "You cannot select a separator as quick search item.");
						return;
					}
					
					getPersonSearchConfigModule().setQuickSearchEntry(layoutEntry);
					gridLayoutConfigComposite.refreshEntryTable();
					
					setConfigChanged(true);
				}
			}
		});
	}
}