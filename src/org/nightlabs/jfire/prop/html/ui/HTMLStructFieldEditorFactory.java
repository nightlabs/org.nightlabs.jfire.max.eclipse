package org.nightlabs.jfire.prop.html.ui;

import org.nightlabs.jfire.base.ui.prop.structedit.AbstractStructFieldEditorFactory;
import org.nightlabs.jfire.base.ui.prop.structedit.StructFieldEditor;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class HTMLStructFieldEditorFactory extends AbstractStructFieldEditorFactory
{
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.structedit.StructFieldEditorFactory#createStructFieldEditor()
	 */
	@Override
	public StructFieldEditor createStructFieldEditor()
	{
		return new HTMLStructFieldEditor();
	}
}
