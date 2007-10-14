package org.nightlabs.jfire.voucher.editor2d.ui.tool;

import org.eclipse.gef.requests.CreationFactory;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateTextScriptDialog;
import org.nightlabs.jfire.scripting.editor2d.ui.tool.TextScriptTool;
import org.nightlabs.jfire.voucher.editor2d.ui.dialog.CreateVoucherTextScriptDialog;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherTextScriptTool 
extends TextScriptTool 
{

	/**
	 * @param aFactory
	 */
	public VoucherTextScriptTool(CreationFactory aFactory) {
		super(aFactory);
	}

	@Override
	protected CreateTextScriptDialog createTextScriptDialog() {
		return new CreateVoucherTextScriptDialog(RCPUtil.getActiveWorkbenchShell(), 
				getScriptTextCreateRequest());
	}

}
