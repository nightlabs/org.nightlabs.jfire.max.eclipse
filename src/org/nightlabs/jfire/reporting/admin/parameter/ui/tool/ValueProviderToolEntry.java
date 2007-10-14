package org.nightlabs.jfire.reporting.admin.parameter.ui.tool;

import org.eclipse.gef.Tool;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.jfire.reporting.admin.parameter.ui.ModelCreationFactory;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ValueProviderToolEntry 
extends CombinedTemplateCreationEntry 
{

	public ValueProviderToolEntry(String label, String shortDesc,
			Object template, ModelCreationFactory factory, ImageDescriptor iconSmall,
			ImageDescriptor iconLarge) 
	{
		super(label, shortDesc, template, factory, iconSmall, iconLarge);
		this.factory = factory;
	}

	private ModelCreationFactory factory;
	@Override
	public Tool createTool() {
		return new ValueProviderTool(factory);
	}
}
