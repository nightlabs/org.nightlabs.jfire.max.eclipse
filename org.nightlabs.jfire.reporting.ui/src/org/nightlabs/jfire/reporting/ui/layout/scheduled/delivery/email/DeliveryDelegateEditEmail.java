package org.nightlabs.jfire.reporting.ui.layout.scheduled.delivery.email;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.LabeledText;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.reporting.scheduled.IScheduledReportDeliveryDelegate;
import org.nightlabs.jfire.reporting.scheduled.ScheduledReportDeliveryDelegateEMail;
import org.nightlabs.jfire.reporting.ui.layout.scheduled.delivery.IScheduledReportDeliveryDelegateEdit;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class DeliveryDelegateEditEmail implements IScheduledReportDeliveryDelegateEdit {

	private LabeledText toAddresses;
	private LabeledText fromAddress;
	private LabeledText subject;
	private LabeledText body;
	
	private IDirtyStateManager dirtyStateManager;
	
	private boolean updating = false;
	
	private ScheduledReportDeliveryDelegateEMail deliveryDelegateEMail;
	
	private ModifyListener modifyListener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent arg0) {
			if (dirtyStateManager != null && !updating) {
				dirtyStateManager.markDirty();
			}
		}
	};

	/**
	 * 
	 */
	public DeliveryDelegateEditEmail() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.layout.scheduled.delivery.IScheduledReportDeliveryDelegateEdit#clear()
	 */
	@Override
	public void clear() {
		updating = true;
		try {
			toAddresses.setText("");
			fromAddress.setText("");
			subject.setText("");
			body.setText("");
		} finally {
			updating = false;
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.layout.scheduled.delivery.IScheduledReportDeliveryDelegateEdit#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createControl(Composite parent) {
		XComposite wrapper = new XComposite(parent, SWT.NONE);
		wrapper.getGridLayout().numColumns = 2;
		wrapper.getGridLayout().makeColumnsEqualWidth = true;
		
		toAddresses = new LabeledText(wrapper, "To:");
		toAddresses.addModifyListener(modifyListener);
		
		fromAddress = new LabeledText(wrapper, "From:");
		fromAddress.addModifyListener(modifyListener);
		
		GridData subjectGD = new GridData(GridData.FILL_HORIZONTAL);
		subjectGD.horizontalSpan = 2;
		subject = new LabeledText(wrapper, "Subject:");
		subject.setLayoutData(subjectGD);
		subject.addModifyListener(modifyListener);
		
		body = new LabeledText(wrapper, "Body:", wrapper.getBorderStyle() | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData bodyGD = new GridData(GridData.FILL_HORIZONTAL);
		bodyGD.horizontalSpan = 2;
		body.setLayoutData(bodyGD);
		GridData bodyTextGD = new GridData(GridData.FILL_HORIZONTAL);
		bodyTextGD.heightHint = RCPUtil.getFontHeight(body) * 5;
		body.getTextControl().setLayoutData(bodyTextGD);
		body.addModifyListener(modifyListener);
		
		return wrapper;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.layout.scheduled.delivery.IScheduledReportDeliveryDelegateEdit#getScheduledReportDeliveryDelegate()
	 */
	@Override
	public IScheduledReportDeliveryDelegate getScheduledReportDeliveryDelegate() {
		if (deliveryDelegateEMail == null) {
			deliveryDelegateEMail = new ScheduledReportDeliveryDelegateEMail(IDGenerator.getOrganisationID(), IDGenerator
					.nextID(ScheduledReportDeliveryDelegateEMail.class));
		}
		deliveryDelegateEMail.setToAddresses(toAddresses.getText());
		deliveryDelegateEMail.setFromAddress(fromAddress.getText());
		deliveryDelegateEMail.setSubject(subject.getText());
		deliveryDelegateEMail.setMailBody(body.getText());
		
		return deliveryDelegateEMail;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.layout.scheduled.delivery.IScheduledReportDeliveryDelegateEdit#setDeliveryDelegate(org.nightlabs.jfire.reporting.scheduled.IScheduledReportDeliveryDelegate)
	 */
	@Override
	public void setDeliveryDelegate(IScheduledReportDeliveryDelegate deliveryDelegate) {
		updating = true;
		try {
			if (deliveryDelegate instanceof ScheduledReportDeliveryDelegateEMail) {
				deliveryDelegateEMail = (ScheduledReportDeliveryDelegateEMail) deliveryDelegate;
			}
			if (deliveryDelegateEMail != null) {
				toAddresses.setText(deliveryDelegateEMail.getToAddresses());
				fromAddress.setText(deliveryDelegateEMail.getFromAddress());
				subject.setText(deliveryDelegateEMail.getSubject());
				body.setText(deliveryDelegateEMail.getMailBody());
			}
		} finally {
			updating = false;
		}
	}

	@Override
	public void setDirtyStateManager(IDirtyStateManager dirtyStateManager) {
		this.dirtyStateManager = dirtyStateManager;
	}

}
