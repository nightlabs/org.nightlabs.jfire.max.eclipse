package org.nightlabs.jfire.voucher.editor2d.tool;

import org.eclipse.gef.Tool;
import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.editor2d.ui.model.IModelCreationFactory;
import org.nightlabs.editor2d.ui.tools.EditorTemplateCreationEntry;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherTextScriptToolEntry 
extends EditorTemplateCreationEntry 
{

	/**
	 * @param label
	 * @param shortDesc
	 * @param template
	 * @param factory
	 * @param iconSmall
	 * @param iconLarge
	 */
	public VoucherTextScriptToolEntry(String label, String shortDesc,
			Object template, IModelCreationFactory factory,
			ImageDescriptor iconSmall, ImageDescriptor iconLarge) {
		super(label, shortDesc, template, factory, iconSmall, iconLarge);
	}

	@Override
	public Tool createTool() {
		return new VoucherTextScriptTool(getModelCreationFactory());
	}
		
}
