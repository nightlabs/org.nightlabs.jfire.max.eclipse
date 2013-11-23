/**
 * 
 */
package org.nightlabs.jfire.voucher.editor2d.ui;

import org.nightlabs.editor2d.ui.AbstractNewEditor2DWizard;
import org.nightlabs.jfire.voucher.editor2d.iofilter.VoucherXStreamFilter;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class NewVoucherFileWizard extends AbstractNewEditor2DWizard {

	public NewVoucherFileWizard() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.editor2d.ui.AbstractNewEditor2DWizard#getFileExtension()
	 */
	@Override
	public String getFileExtension() {
		return VoucherXStreamFilter.FILE_EXTENSION;
	}

}
