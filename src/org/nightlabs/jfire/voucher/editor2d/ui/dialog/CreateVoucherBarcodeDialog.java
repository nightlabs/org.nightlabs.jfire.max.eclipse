package org.nightlabs.jfire.voucher.editor2d.ui.dialog;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
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
 */
public class CreateVoucherBarcodeDialog
extends org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateBarcodeDialog
{
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
	
	private static final ILabelProvider scriptRegistryLabelProvider = new LabelProvider()
	{
		@Override
		public String getText(Object element)
		{
			if (element instanceof ScriptRegistryItemID) {
				ScriptRegistryItemID scriptRegistryItemID = (ScriptRegistryItemID) element;
				return scriptRegistryItemID.scriptRegistryItemID;
			}
			return null;
		}
		@Override
		public Image getImage(Object element) {
			return null;
		}
	};	
}
