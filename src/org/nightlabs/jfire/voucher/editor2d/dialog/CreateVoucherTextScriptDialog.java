package org.nightlabs.jfire.voucher.editor2d.dialog;

import java.util.Map;

import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.jfire.scripting.editor2d.ui.AbstractScriptRegistryItemTreeComposite;
import org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateTextScriptDialog;
import org.nightlabs.jfire.scripting.editor2d.ui.request.TextScriptCreateRequest;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.jfire.voucher.editor2d.scripting.ScriptRegistryItemTreeComposite;
import org.nightlabs.jfire.voucher.editor2d.scripting.VoucherScriptResultProvider;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class CreateVoucherTextScriptDialog 
extends CreateTextScriptDialog 
{

	/**
	 * @param parentShell
	 * @param request
	 */
	public CreateVoucherTextScriptDialog(Shell parentShell,
			TextScriptCreateRequest request) {
		super(parentShell, request);
	}

	/**
	 * @param parentShell
	 * @param request
	 */
	public CreateVoucherTextScriptDialog(IShellProvider parentShell,
			TextScriptCreateRequest request) {
		super(parentShell, request);
	}

	@Override
	protected AbstractScriptRegistryItemTreeComposite createScriptTreeComposite(
			Composite parent) 
	{
		return new ScriptRegistryItemTreeComposite(parent, SWT.NONE);
	}

	@Override
	protected Map<ScriptRegistryItemID, Object> getScriptRegistryItemID2Result() 
	{
		return VoucherScriptResultProvider.sharedInstance().getScriptResults();
	}

}
