package org.nightlabs.jfire.voucher.admin.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.editor.MessageSectionPart;
import org.nightlabs.jfire.voucher.admin.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherLayoutSection 
extends MessageSectionPart 
{
	public VoucherLayoutSection(IFormPage page, Composite parent) 
	{
		super(page, parent, ExpandableComposite.TITLE_BAR, Messages.getString("org.nightlabs.jfire.voucher.admin.editor.VoucherLayoutSection.title")); //$NON-NLS-1$
		voucherLayoutComposite = new VoucherLayoutComposite(getContainer(), SWT.NONE, null, this);
	}

	private VoucherLayoutComposite voucherLayoutComposite = null;
	public VoucherLayoutComposite getVoucherLayoutComposite() {
		return voucherLayoutComposite;
	}
}
