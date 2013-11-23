/**
 * 
 */
package org.nightlabs.jfire.scripting.editor2d.ui.model;

import org.eclipse.jface.viewers.LabelProvider;
import org.nightlabs.jfire.scripting.editor2d.BarcodeDrawComponent.WidthScale;
import org.nightlabs.jfire.scripting.editor2d.ui.resource.Messages;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class WidthScaleLabelProvider
extends LabelProvider
{
	@Override
	public String getText(Object element)
	{
		if (element == WidthScale.SCALE_1)
			return Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.model.WidthScaleLabelProvider.widthScale1"); //$NON-NLS-1$
		if (element == WidthScale.SCALE_2)
			return Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.model.WidthScaleLabelProvider.widthScale2"); //$NON-NLS-1$
		if (element == WidthScale.SCALE_3)
			return Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.model.WidthScaleLabelProvider.widthScale3"); //$NON-NLS-1$
		if (element == WidthScale.SCALE_4)
			return Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.model.WidthScaleLabelProvider.widthScale4"); //$NON-NLS-1$
		
		return super.getText(element);
	}
}
