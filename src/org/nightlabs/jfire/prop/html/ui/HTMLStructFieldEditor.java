package org.nightlabs.jfire.prop.html.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.jfire.base.ui.prop.structedit.AbstractStructFieldEditor;
import org.nightlabs.jfire.prop.html.HTMLStructField;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class HTMLStructFieldEditor extends AbstractStructFieldEditor<HTMLStructField>
{
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.structedit.AbstractStructFieldEditor#createSpecialComposite(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	protected Composite createSpecialComposite(Composite parent, int style)
	{
		// FIXME: remove this.
		Composite c = new Composite(parent, style);
		c.setLayout(new FillLayout());
		Label l = new Label(c, SWT.NONE);
		l.setText("Hallo! Mein special composite");
		return c;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.structedit.AbstractStructFieldEditor#setSpecialData(org.nightlabs.jfire.prop.StructField)
	 */
	@Override
	protected void setSpecialData(HTMLStructField field)
	{
	}
}
