package org.nightlabs.jfire.voucher.editor2d.ui.iofilter;

import org.nightlabs.editor2d.iofilter.XStreamFilter;
import org.nightlabs.jfire.voucher.editor2d.ui.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherXStreamFilter 
extends XStreamFilter 
{
	public static final String FILE_EXTENSION = "v2d"; //$NON-NLS-1$
	
	@Override
	protected String initDescription() {
		return Messages.getString("org.nightlabs.jfire.voucher.editor2d.ui.iofilter.VoucherXStreamFilter.description"); //$NON-NLS-1$
	}

	@Override
	protected String[] initFileExtensions() {
		return new String[] { FILE_EXTENSION };
	}

	@Override
	protected String initName() {
		return Messages.getString("org.nightlabs.jfire.voucher.editor2d.ui.iofilter.VoucherXStreamFilter.name"); //$NON-NLS-1$
	}
}
