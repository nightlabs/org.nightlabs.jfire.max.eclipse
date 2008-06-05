package org.nightlabs.jfire.prop.html.ui;

import org.eclipse.jface.wizard.WizardPage;
import org.nightlabs.jfire.base.ui.prop.structedit.AbstractStructFieldFactory;
import org.nightlabs.jfire.prop.StructBlock;
import org.nightlabs.jfire.prop.StructField;
import org.nightlabs.jfire.prop.html.HTMLStructField;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class HTMLStructFieldFactory extends AbstractStructFieldFactory
{
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.structedit.StructFieldFactory#createStructField(org.nightlabs.jfire.prop.StructBlock, org.eclipse.jface.wizard.WizardPage)
	 */
	@Override
	public StructField createStructField(StructBlock block, WizardPage wizardPage)
	{
		return new HTMLStructField(block);
	}
}
