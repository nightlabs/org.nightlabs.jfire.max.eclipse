/**
 *
 */
package org.nightlabs.jfire.reporting.admin.ui.layout.action.rename;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.reporting.ReportManagerRemote;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[ÐOT]de>
 *
 */
public class RenameRegistryItemWizard extends DynamicPathWizard {

	private ReportRegistryItem reportRegistryItem;
	private RenameRegistryItemWizardPage wizardPage;

	/**
	 *
	 */
	public RenameRegistryItemWizard(ReportRegistryItem reportRegistryItem) {
		super();
		if (reportRegistryItem == null)
			throw new IllegalArgumentException("ReportRegistyItem must not be null!!"); //$NON-NLS-1$
		this.reportRegistryItem = reportRegistryItem;
		wizardPage = new RenameRegistryItemWizardPage(reportRegistryItem);
		addPage(wizardPage);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		reportRegistryItem.getName().copyFrom(wizardPage.getNameBuffer());
		reportRegistryItem.getDescription().copyFrom(wizardPage.getDescriptionBuffer());
		try {
			ReportManagerRemote rm = JFireEjb3Factory.getRemoteBean(ReportManagerRemote.class, Login.getLogin().getInitialContextProperties());
			rm.storeRegistryItem(reportRegistryItem, false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	public static int show(ReportRegistryItem reportRegistryItem) {
		RenameRegistryItemWizard wizard = new RenameRegistryItemWizard(reportRegistryItem);
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
		return dialog.open();
	}

}
