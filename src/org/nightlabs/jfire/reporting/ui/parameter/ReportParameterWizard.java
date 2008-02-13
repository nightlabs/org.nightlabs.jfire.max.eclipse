/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.parameter;

import java.util.Map;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.parameter.config.ReportParameterAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.dao.ReportParameterAcquisitionSetupDAO;
import org.nightlabs.jfire.reporting.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * A wizard that presents the UI for the {@link ValueProvider}s registered for
 * a given {@link ReportLayout} in the order defined by the parameter
 * acquisition workflow of this ReportLayout.
 * <p>
 * If the ReportLayout has more than one acquisition use-case defined, the first
 * page of this wizard will provide a selection of the use-case to use.
 * </p>
 * <p>
 * It is recommended to use the static methods
 * {@link #openResult(ReportRegistryItemID)} and {@link #open(ReportRegistryItemID)}
 * rather than instantiating the wizard directly. However this is of course possible,
 * but it is then a good idea to use the {@link Dialog} defined here
 * as WizardDialog.
 * </p>
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportParameterWizard extends DynamicPathWizard{

	/**
	 * This is used as return value of the static method
	 * {@link ReportParameterWizard#openResult(ReportRegistryItemID)}.
	 * <p>
	 * It indicates wether the use finished the wizard and so the parameters
	 * are avaiable and also provides the gathered parameters
	 * </p>
	 */
	public static class Result {
		private boolean acquisitionFinished;
		private Map<String, Object> parameters;
		
		/**
		 * @return the acquisitionFinished
		 */
		public boolean isAcquisitionFinished() {
			return acquisitionFinished;
		}
		/**
		 * @param acquisitionFinished the acquisitionFinished to set
		 */
		public void setAcquisitionFinished(boolean acquisitionFinished) {
			this.acquisitionFinished = acquisitionFinished;
		}
		/**
		 * @return the parameter
		 */
		public Map<String, Object> getParameters() {
			return parameters;
		}
		/**
		 * @param parameter the parameter to set
		 */
		public void setParameters(Map<String, Object> parameter) {
			this.parameters = parameter;
		}
	}

	/**
	 * WizardDialog to be used with the {@link ReportParameterWizard}.
	 * It overrides {@link #open()} and returns {@link Window#OK} directly
	 * when the report has no parameters, or the ReportLayout
	 * has no acquisition setup assigned.
	 */
	public static class Dialog extends DynamicPathWizardDialog {
		public Dialog(DynamicPathWizard wizard) {
			super(wizard);
		}
		/**
		 */
		@Override
		public int open() {
			if (getWizard() instanceof ReportParameterWizard) {
				ReportParameterWizard wiz  = (ReportParameterWizard) getWizard();
				if (wiz.wizardHop != null && wiz.wizardHop.getEntryPage() != null) {
					// if the report layout has an acquisition setup assigned
					// and the workflow provides at least one use-case and ValueProvider
					return super.open();
				}
				// if the report has no acquisition setup or not ValueProvider
				// in its setup or the report has no parameters, return OK
				return Window.OK;
			}
			return super.open();
		}
	};
	
	private ReportRegistryItemID reportLayoutID;
	private ReportParameterWizardHop wizardHop;
	
	/**
	 * Creates a new {@link ReportParameterWizard} for the given reportLayoutID.
	 * @param reportLayoutID The id of the ReportLayout this wizard should query parameters from the user.
	 */
	public ReportParameterWizard(ReportRegistryItemID reportLayoutID) {
		this.reportLayoutID = reportLayoutID;
		ReportParameterAcquisitionSetup setup = null;
		try {
			setup = ReportParameterAcquisitionSetupDAO.sharedInstance().getSetupForReportLayout(
					reportLayoutID, ReportParameterAcquisitionSetupDAO.DEFAULT_FETCH_GROUPS,
					new NullProgressMonitor()
				);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (setup == null)
			return;
		if (setup.getValueAcquisitionSetups().size() > 1) {
			ReportParameterAcquisitionUseCaseWizardPage wizardPage = new ReportParameterAcquisitionUseCaseWizardPage(Messages.getString("org.nightlabs.jfire.reporting.ui.parameter.ReportParameterWizard.wizardPage.pageName"), reportLayoutID); //$NON-NLS-1$
			wizardHop = wizardPage.getReportParameterWizardHop();
		}
		else if (setup.getValueAcquisitionSetups().size() == 1){
			ValueAcquisitionSetup acquisitionSetup = setup.getValueAcquisitionSetups().values().iterator().next();
			wizardHop = new ReportParameterWizardHop();
			ReportParameterAcquisitionUseCaseWizardPage.populateValueProviderSetupPages(acquisitionSetup, wizardHop, true);
		}
		if (wizardHop != null && wizardHop.getEntryPage() == null)
			wizardHop = null;
		if (wizardHop != null)
			addPage(wizardHop.getEntryPage());
	}
	
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		return true;
	}
	
	/**
	 * @return the reportLayoutID
	 */
	public ReportRegistryItemID getReportLayoutID() {
		return reportLayoutID;
	}

	/**
	 * Overrdes {@link #getIdentifier()} to store the size and
	 * position per ReportLayout rather than for this class only.
	 */
	@Override
	public String getIdentifier() {
		return super.getIdentifier()+'#'+reportLayoutID.toString();
	}
	
	/**
	 * Returns the parameters this wizard gathered by now.
	 * This should actually only be called after the wizard has finished.
	 * @return The parameters this wizard gathered by now.
	 */
	public Map<String, Object> getParameters() {
		if (wizardHop == null)
			return null;
		return wizardHop.getParameters();
	}
	
	/**
	 * Opens the {@link ReportParameterWizard} for the given report layout
	 * and returns its {@link Result}. The Result will indicate whether the
	 * wizard finished or the user canceled the wizard. ({@link Result#isAcquistionFinished()})
	 * <p>
	 * The parameters acquired by the wizard will also be avaiable in the Result. ({@link Result#getParameters()}).
	 * This migt be <code>null</code>, if the user canceled the wizard,
	 * or the ReportLayout has no {@link ReportParameterAcquisitionSetup} assigned, or the
	 * Report has not parameters.
	 * </p>
	 * 
	 * @param reportLayoutID The {@link ReportRegistryItemID} to acquire the parameters for.
	 * @return The {@link Result} of the parameter acquisition process.
	 */
	public static Result openResult(ReportRegistryItemID reportLayoutID) {
		if (reportLayoutID == null)
			throw new IllegalArgumentException("reportLayoutID must not be null!"); //$NON-NLS-1$

		ReportParameterWizard wiz = new ReportParameterWizard(reportLayoutID);
		final Dialog dlg = new Dialog(wiz);
		final Result dialogResult = new Result();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				dlg.open();
			}
		});
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				dialogResult.setAcquisitionFinished(dlg.open() == Window.OK);
			}
		});
		if (dialogResult.isAcquisitionFinished())
			dialogResult.setParameters(wiz.getParameters());
		else
			dialogResult.setParameters(null);
		
		return dialogResult;
	}

	/**
	 * Opens the {@link ReportParameterWizard} for the given report layout
	 * and directly returns the parameters acquired by the wizard.
	 * <p>
	 * The result of this method might be <code>null</code>, if the user
	 * aborts the wizard, or the Report layout has no {@link ReportParameterAcquisitionSetup}
	 * assigned, or the ReportLayout has no parameters.
	 * 
	 * @param reportLayoutID The {@link ReportRegistryItemID} to acquire the parameters for.
	 * @return The acquired parameters or <code>null</code>.
	 */
	public static Map<String, Object> open(ReportRegistryItemID reportLayoutID) {
		Result result = openResult(reportLayoutID);
		return result.getParameters();
	}

	
}
