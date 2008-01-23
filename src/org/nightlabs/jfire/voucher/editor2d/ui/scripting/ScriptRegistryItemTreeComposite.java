package org.nightlabs.jfire.voucher.editor2d.ui.scripting;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.scripting.editor2d.ui.AbstractScriptRegistryItemTreeComposite;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.jfire.voucher.scripting.VoucherScriptingConstants;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ScriptRegistryItemTreeComposite 
extends AbstractScriptRegistryItemTreeComposite 
{

	/**
	 * @param parent
	 * @param style
	 */
	public ScriptRegistryItemTreeComposite(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public ScriptRegistryItemTreeComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) 
	{
		super(parent, style, layoutMode, layoutDataMode);
	}

	@Override
	protected Set<ScriptRegistryItemID> getNodes() 
	{
		Set<ScriptRegistryItemID> voucherNodes = new HashSet<ScriptRegistryItemID>();
		voucherNodes.add(getVoucherNodeID());
		return voucherNodes;		
	}

	@Override
	protected String getZone() {
		return VoucherScriptingConstants.VOUCHER_SCRIPTING_ZONE;
	}

	protected ScriptRegistryItemID getVoucherNodeID() 
	{
		return ScriptRegistryItemID.create(
				getOrganisationID(),
//				Organisation.DEV_ORGANISATION_ID,
				VoucherScriptingConstants.SCRIPT_REGISTRY_ITEM_TYPE_TRADE_VOUCHER,				
				VoucherScriptingConstants.SCRIPT_REGISTRY_ITEM_ID_CATEGORY_VOUCHER);		
	}	
}
