package org.nightlabs.jfire.trade.admin.ui.tariffuserset;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.jfire.accounting.Tariff;
import org.nightlabs.jfire.entityuserset.ui.AbstractEntitySection;
import org.nightlabs.jfire.entityuserset.ui.AbstractEntityTable;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class TariffSection extends AbstractEntitySection<Tariff> 
{
	/**
	 * @param page
	 * @param parent
	 * @param title
	 */
	public TariffSection(IFormPage page, Composite parent, String title) {
		super(page, parent, title);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.entityuserset.ui.AbstractEntitySection#createTable(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected AbstractEntityTable<Tariff> createTable(Composite parent) {
		return new TariffTable(parent, SWT.NONE, this);
	}
	
}
