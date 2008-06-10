package org.nightlabs.jfire.prop.html.ui;

import org.nightlabs.jfire.base.ui.prop.structedit.AbstractStructFieldEditorFactory;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class HTMLStructFieldEditorFactory extends AbstractStructFieldEditorFactory
{
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.structedit.StructFieldEditorFactory#createStructFieldEditor()
	 */
	@Override
	public HTMLStructFieldEditor createStructFieldEditor()
	{
		return new HTMLStructFieldEditor();
	}
}
