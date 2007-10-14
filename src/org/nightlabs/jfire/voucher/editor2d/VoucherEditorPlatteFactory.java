package org.nightlabs.jfire.voucher.editor2d;

import org.eclipse.gef.palette.ToolEntry;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.scripting.editor2d.BarcodeDrawComponent;
import org.nightlabs.jfire.scripting.editor2d.ScriptEditor2DFactory;
import org.nightlabs.jfire.scripting.editor2d.TextScriptDrawComponent;
import org.nightlabs.jfire.scripting.editor2d.ui.ScriptingEditor2DPlugin;
import org.nightlabs.jfire.scripting.editor2d.ui.ScriptingEditorPaletteFactory;
import org.nightlabs.jfire.scripting.editor2d.ui.tool.BarcodeTool;
import org.nightlabs.jfire.scripting.editor2d.ui.tool.TextScriptTool;
import org.nightlabs.jfire.voucher.editor2d.resource.Messages;
import org.nightlabs.jfire.voucher.editor2d.tool.VoucherBarcodeToolEntry;
import org.nightlabs.jfire.voucher.editor2d.tool.VoucherTextScriptToolEntry;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherEditorPlatteFactory 
extends ScriptingEditorPaletteFactory 
{
	public VoucherEditorPlatteFactory(ScriptEditor2DFactory factory) {
		super(factory);
	}

	protected ToolEntry createBarcodeToolEntry() 
 	{
 		return new VoucherBarcodeToolEntry
 	  (
 	  	Messages.getString("org.nightlabs.jfire.voucher.editor2d.VoucherEditorPlatteFactory.label.barcode"), //$NON-NLS-1$
 	  	Messages.getString("org.nightlabs.jfire.voucher.editor2d.VoucherEditorPlatteFactory.description.barcode"),  //$NON-NLS-1$
 	    BarcodeDrawComponent.class,
 	    getCreationFactory(BarcodeDrawComponent.class), 
 	    SharedImages.getSharedImageDescriptor(ScriptingEditor2DPlugin.getDefault(), 
 	    		BarcodeTool.class),    
 	    null
 	  ); 		
 	}	
	
 	protected ToolEntry createTicketScriptTextToolEntry() 
 	{
 		return new VoucherTextScriptToolEntry
 	  (
 	  	Messages.getString("org.nightlabs.jfire.voucher.editor2d.VoucherEditorPlatteFactory.label.scriptText"), //$NON-NLS-1$
 	  	Messages.getString("org.nightlabs.jfire.voucher.editor2d.VoucherEditorPlatteFactory.description.scriptText"),  //$NON-NLS-1$
 	    TextScriptDrawComponent.class,
 	    getCreationFactory(TextScriptDrawComponent.class), 
 	    SharedImages.getSharedImageDescriptor(ScriptingEditor2DPlugin.getDefault(), 
 	    		TextScriptTool.class),    
 	    null
 	  ); 		
 	}

}
