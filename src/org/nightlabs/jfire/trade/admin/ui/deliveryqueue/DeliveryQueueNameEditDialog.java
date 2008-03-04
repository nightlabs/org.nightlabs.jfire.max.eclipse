package org.nightlabs.jfire.trade.admin.ui.deliveryqueue;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.dialog.CenteredDialog;
import org.nightlabs.base.ui.language.I18nTextEditorTable;
import org.nightlabs.base.ui.language.II18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.store.deliver.DeliveryQueue;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

public class DeliveryQueueNameEditDialog extends CenteredDialog {

	private II18nTextEditor deliveryQueueNameEditor;
	private DeliveryQueue deliveryQueue;

	public DeliveryQueueNameEditDialog(Shell shell, DeliveryQueue deliveryQueue) {
		super(shell);
		this.deliveryQueue = deliveryQueue;
	}

	@Override
	protected Point getInitialSize() {
		Point s = super.getInitialSize();
		return new Point(Math.max(400, s.x), Math.max(500, s.y));
	}
	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		deliveryQueueNameEditor = new I18nTextEditorTable(composite, Messages.getString("org.nightlabs.jfire.trade.admin.ui.deliveryqueue.DeliveryQueueNameEditDialog.deliveryQueueNameEditor.caption")); //$NON-NLS-1$
		deliveryQueueNameEditor.setI18nText(deliveryQueue.getName(), EditMode.BUFFERED);

		return composite;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getShell().setDefaultButton(null);
	}

	@Override
	protected void okPressed() {
		if (deliveryQueueNameEditor.getI18nText().getTexts().isEmpty())
			MessageDialog.openError(RCPUtil.getActiveShell(), Messages.getString("org.nightlabs.jfire.trade.admin.ui.deliveryqueue.DeliveryQueueNameEditDialog.emptyDeliveryQueueNameDialog.title"), Messages.getString("org.nightlabs.jfire.trade.admin.ui.deliveryqueue.DeliveryQueueNameEditDialog.emptyDeliveryQueueNameDialog.message")); //$NON-NLS-1$ //$NON-NLS-2$
		else {
			deliveryQueueNameEditor.copyToOriginal();
			super.okPressed();
		}
	}
}
