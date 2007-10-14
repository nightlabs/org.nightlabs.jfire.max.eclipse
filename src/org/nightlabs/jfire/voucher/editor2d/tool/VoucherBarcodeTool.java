package org.nightlabs.jfire.voucher.editor2d.tool;

import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.editor2d.ui.model.IModelCreationFactory;
import org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateBarcodeDialog;
import org.nightlabs.jfire.scripting.editor2d.ui.tool.BarcodeTool;
import org.nightlabs.jfire.voucher.editor2d.dialog.CreateVoucherBarcodeDialog;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherBarcodeTool 
extends BarcodeTool 
{
	/**
	 * @param factory
	 */
	public VoucherBarcodeTool(IModelCreationFactory factory) {
		super(factory);
	}

	@Override
	protected CreateBarcodeDialog createBarcodeDialog() {
		return new CreateVoucherBarcodeDialog(RCPUtil.getActiveWorkbenchShell(),
				getBarcodeCreateRequest());
	}
}
