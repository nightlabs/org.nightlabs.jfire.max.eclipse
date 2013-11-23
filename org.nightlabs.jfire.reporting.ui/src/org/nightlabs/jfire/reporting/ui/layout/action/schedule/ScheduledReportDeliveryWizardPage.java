/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.layout.action.schedule;

import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.reporting.scheduled.IScheduledReportDeliveryDelegate;
import org.nightlabs.jfire.reporting.ui.layout.scheduled.delivery.DeliveryDelegateEditComposite;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ScheduledReportDeliveryWizardPage extends WizardHopPage {

	private CreateScheduledReportWizard createReportWizard;
	private DeliveryDelegateEditComposite deliveryDelegateEditComposite;

	/**
	 * @param pageName
	 */
	public ScheduledReportDeliveryWizardPage(CreateScheduledReportWizard createReportWizard) {
		super(ScheduledReportDeliveryWizardPage.class.getName(), "Scheduled report delivery");
		setMessage("Configure the delivery type and properties");
		this.createReportWizard = createReportWizard;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		createReportWizard.getScheduledReport().getName().setText(
				Locale.getDefault(), 
				createReportWizard.getReportLayout().getName().getText() + " (scheduled)");
		deliveryDelegateEditComposite = new DeliveryDelegateEditComposite(parent, SWT.NONE, LayoutDataMode.GRID_DATA, null);
		return deliveryDelegateEditComposite;
	}
	
	public void commitProperties() {
		IScheduledReportDeliveryDelegate deliveryDelegate = deliveryDelegateEditComposite.getDeliveryDelegate();
		createReportWizard.getScheduledReport().setDeliveryDelegate(deliveryDelegate);
	}

}
