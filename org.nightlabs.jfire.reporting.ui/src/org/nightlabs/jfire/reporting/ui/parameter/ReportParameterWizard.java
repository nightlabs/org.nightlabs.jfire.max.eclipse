/**
 *
 */
package org.nightlabs.jfire.reporting.ui.parameter;

import java.util.Map;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.reporting.layout.ReportLayout;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.parameter.ValueProvider;
import org.nightlabs.jfire.reporting.parameter.config.ReportParameterAcquisitionSetup;

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
	 * It indicates whether the user finished the wizard and so the parameters
	 * are available and also provides the gathered parameters.
	 * </p>
	 */
	public static class WizardResult {
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
		public Dialog(Shell parentShell, DynamicPathWizard wizard) {
			super(parentShell, wizard);
		}
		/**
		 */
		@Override
		public int open() {
			if (getWizard() instanceof ReportParameterWizard) {
				ReportParameterWizard wiz  = (ReportParameterWizard) getWizard();
				if (wiz.wizardHop != null && wiz.wizardHop.hasAcquisitionSetup()) {
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
	 * @param isScheduledReport Whether the parameters should be acquired to configure a scheduled report.
	 * @param initialValues The map with the initial values for the wizard. These will be applied to all provider guis that support that.
	 */
	public ReportParameterWizard(ReportRegistryItemID reportLayoutID, boolean isScheduledReport, Map<String, Object> initialValues) {
		this.reportLayoutID = reportLayoutID;
		wizardHop = new ReportParameterWizardHop(reportLayoutID, isScheduledReport, initialValues);
		if (wizardHop != null && wizardHop.hasAcquisitionSetup() && wizardHop.getEntryPage() != null) {
			addPage(wizardHop.getEntryPage());
		}
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
	 * and returns its {@link WizardResult}. The Result will indicate whether the
	 * wizard finished or the user canceled the wizard. ({@link WizardResult#isAcquistionFinished()})
	 * <p>
	 * The parameters acquired by the wizard will also be avaiable in the Result. ({@link WizardResult#getParameters()}).
	 * This might be <code>null</code>, if the user canceled the wizard,
	 * or the ReportLayout has no {@link ReportParameterAcquisitionSetup} assigned, or the
	 * Report has not parameters.
	 * </p>
	 *
	 * @param reportLayoutID The {@link ReportRegistryItemID} to acquire the parameters for.
	 * @param isScheduledReport Whether the parameters should be acquired to configure a scheduled report.
	 * @param initialValues The map with the initial values for the wizard. These will be applied to all provider guis that support that.
	 * @return The {@link WizardResult} of the parameter acquisition process.
	 */
	public static WizardResult openResult(final Shell parentShell, ReportRegistryItemID reportLayoutID, boolean isScheduledReport, Map<String, Object> initialValues) {
		if (reportLayoutID == null)
			throw new IllegalArgumentException("reportLayoutID must not be null!"); //$NON-NLS-1$

		final ReportParameterWizard wiz = new ReportParameterWizard(reportLayoutID, isScheduledReport, initialValues);
		final WizardResult dialogResult = new WizardResult();
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				Shell shell = (parentShell == null)?RCPUtil.getActiveShell():parentShell;
				Dialog dlg = new Dialog(shell, wiz);
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
	 * and returns its {@link WizardResult}. The Result will indicate whether the
	 * wizard finished or the user canceled the wizard. ({@link WizardResult#isAcquistionFinished()})
	 * <p>
	 * The parameters acquired by the wizard will also be avaiable in the Result. ({@link WizardResult#getParameters()}).
	 * This might be <code>null</code>, if the user canceled the wizard,
	 * or the ReportLayout has no {@link ReportParameterAcquisitionSetup} assigned, or the
	 * Report has not parameters.
	 * </p>
	 *
	 * @param reportLayoutID The {@link ReportRegistryItemID} to acquire the parameters for.
	 * @param isScheduledReport Whether the parameters should be acquired to configure a scheduled report.
	 * @return The {@link WizardResult} of the parameter acquisition process.
	 */
	public static WizardResult openResult(final Shell parentShell, ReportRegistryItemID reportLayoutID, boolean isScheduledReport) {
		return openResult(parentShell, reportLayoutID, isScheduledReport, null);
	}
	
	/**
	 * Opens the {@link ReportParameterWizard} for the given report layout
	 * and returns its {@link WizardResult}. The Result will indicate whether the
	 * wizard finished or the user canceled the wizard. ({@link WizardResult#isAcquistionFinished()})
	 * <p>
	 * The parameters acquired by the wizard will also be avaiable in the Result. ({@link WizardResult#getParameters()}).
	 * This might be <code>null</code>, if the user canceled the wizard,
	 * or the ReportLayout has no {@link ReportParameterAcquisitionSetup} assigned, or the
	 * Report has not parameters.
	 * </p>
	 *
	 * @param reportLayoutID The {@link ReportRegistryItemID} to acquire the parameters for.
	 * @param isScheduledReport Whether the parameters should be acquired to configure a scheduled report.
	 * @return The {@link WizardResult} of the parameter acquisition process.
	 */
	public static WizardResult openResult(ReportRegistryItemID reportLayoutID, boolean isScheduledReport) {
		return openResult(null, reportLayoutID, isScheduledReport, null);
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
	 * @param isScheduledReport Whether the parameters should be acquired to configure a scheduled report.
	 * @return The acquired parameters or <code>null</code>.
	 */
	public static Map<String, Object> open(Shell parentShell, ReportRegistryItemID reportLayoutID, boolean isScheduledReport) {
		WizardResult result = openResult(parentShell, reportLayoutID, isScheduledReport);
		return result.getParameters();
	}


}
