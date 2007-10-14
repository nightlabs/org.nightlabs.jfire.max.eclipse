package org.nightlabs.jfire.reporting.admin.parameter.tool;

import org.eclipse.gef.Tool;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.jfire.reporting.admin.parameter.ModelCreationFactory;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ConnectionToolEntry 
extends CombinedTemplateCreationEntry 
{
	public ConnectionToolEntry(String label, String shortDesc, Object template,
			ModelCreationFactory factory, ImageDescriptor iconSmall,
			ImageDescriptor iconLarge) 
	{
		super(label, shortDesc, template, factory, iconSmall, iconLarge);
		this.factory = factory;
	}

	private ModelCreationFactory factory;
	@Override
	public Tool createTool() {
		return new ConnectionTool(factory);
	}
	
}
