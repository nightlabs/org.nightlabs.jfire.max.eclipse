package org.nightlabs.jfire.trade.admin.ui.overview;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.store.Unit;
import org.nightlabs.jfire.store.dao.UnitDAO;
import org.nightlabs.progress.ProgressMonitor;

public class UnitCreateWizard 
extends DynamicPathWizard
implements INewWizard 
{
	private UnitCreateWizardPage unitCreateWizardPage;
	
	public UnitCreateWizard()
	{
		setWindowTitle("Create new unit wizard");
	}
	
	@Override
	public void addPages() {
		unitCreateWizardPage = new UnitCreateWizardPage();
		addPage(unitCreateWizardPage);
	}
	
	private Unit newUnit;
	@Override
	public boolean performFinish() {
		newUnit = new Unit(unitCreateWizardPage.getUnitID(), IDGenerator.nextIDString(Unit.class), unitCreateWizardPage.getDecimalDigits());
		newUnit.getSymbol().copyFrom(unitCreateWizardPage.getUnitSymbolTextEditor().getI18nText());
		newUnit.getName().copyFrom(unitCreateWizardPage.getUnitNameTextEditor().getI18nText());
		
		Job job = new Job("Storing Unit...") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				monitor.beginTask("Storing Unit...", 1);
				UnitDAO.sharedInstance().storeUnit(newUnit, false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		job.schedule();
		
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
	}
}