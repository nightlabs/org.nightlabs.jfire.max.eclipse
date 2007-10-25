package org.nightlabs.jfire.voucher.editor2d.ui.dialog;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.AbstractListComposite;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.jfire.scripting.editor2d.ui.request.BarcodeCreateRequest;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.jfire.voucher.editor2d.ui.scripting.VoucherScriptResultProvider;
import org.nightlabs.jfire.voucher.scripting.VoucherScriptingConstants;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class CreateVoucherBarcodeDialog 
extends org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateBarcodeDialog 
{

	/**
	 * @param parentShell
	 * @param request
	 */
	public CreateVoucherBarcodeDialog(Shell parentShell, BarcodeCreateRequest request) {
		super(parentShell, request);
	}

	@Override
	protected void createScriptRegistryItemIDCombo(Composite parent) {
		List<ScriptRegistryItemID> scriptIDs = new LinkedList<ScriptRegistryItemID>();
		scriptIDs.add(VoucherScriptingConstants.OID.SCRIPT_REGISTRY_ITEM_ID_SCRIPT_VOUCHER_KEY);
		scriptCombo = new XComboComposite<ScriptRegistryItemID>(parent, 
				AbstractListComposite.getDefaultWidgetStyle(parent), (String) null,
				scriptRegistryLabelProvider);
		scriptCombo.setInput(scriptIDs);
		scriptCombo.selectElementByIndex(0);
	}
	
	@Override
	protected ScriptRegistryItemID getSelectedScriptRegistryItemID() {
		return scriptCombo.getSelectedElement();
	}
	
	@Override
	protected String getValue(ScriptRegistryItemID scriptRegistryItemID) {
		return (String) VoucherScriptResultProvider.sharedInstance().
			getScriptResult(scriptRegistryItemID);
	}
}
