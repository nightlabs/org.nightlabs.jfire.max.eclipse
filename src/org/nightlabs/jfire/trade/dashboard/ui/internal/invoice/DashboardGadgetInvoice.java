/**
 * 
 */
package org.nightlabs.jfire.trade.dashboard.ui.internal.invoice;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.base.dashboard.ui.AbstractDashboardGadget;

/**
 * @author sschefczyk
 *
 */
public class DashboardGadgetInvoice extends AbstractDashboardGadget {

	public DashboardGadgetInvoice() {}

	@Override
	public Composite createControl(Composite parent) {
		XComposite invoiceGadget = createDefaultWrapper(parent);
		
//		appendNewRow(invoiceGadget, "icons/JFire-Logo.81x81.png", Messages.getString("org.nightlabs.jfire.base.dashboard.ui.internal.welcome.DashboardGadgetWelcome.row1.title"),  //$NON-NLS-1$ //$NON-NLS-2$
//				Messages.getString("org.nightlabs.jfire.base.dashboard.ui.internal.welcome.DashboardGadgetWelcome.row1.message")); //$NON-NLS-1$
		
		return invoiceGadget;
	}
	
	@Override
	public void refresh() {
		getGadgetContainer().setTitle(getGadgetContainer().getLayoutEntry().getName());
	}
	
}
