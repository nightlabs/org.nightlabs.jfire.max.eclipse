/**
 * 
 */
package org.nightlabs.jfire.scripting.editor2d.ui.property;

import org.eclipse.jface.viewers.LabelProvider;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ConditionScriptLabelProvider 
extends LabelProvider 
{
	@Override
	public String getText(Object element) 
	{ 
		if (element == null || element instanceof Number) {
			return "No Script";
		}
		else {
			return "Script";
		}
	}
}
