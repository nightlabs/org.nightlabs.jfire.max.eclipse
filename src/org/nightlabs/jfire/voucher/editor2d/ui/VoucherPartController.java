package org.nightlabs.jfire.voucher.editor2d.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.part.PartController;
import org.nightlabs.jfire.scripting.editor2d.ui.script.IScriptResultChangedListener;
import org.nightlabs.jfire.scripting.editor2d.ui.script.ScriptResultsChangedEvent;
import org.nightlabs.jfire.voucher.editor2d.ui.scripting.VoucherScriptResultProvider;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherPartController 
extends PartController 
{
	private static VoucherPartController sharedInstance;
	public static VoucherPartController sharedInstance() {
		if (sharedInstance == null) {
			synchronized (VoucherPartController.class) {
				if (sharedInstance == null) {
					sharedInstance = new VoucherPartController();
				}
			}
		}
		return sharedInstance;
	}
	
	protected VoucherPartController() {
		super();
		VoucherScriptResultProvider.sharedInstance().addScriptResultsChangedListener(
				scriptResultChangedListener);		
	}

	@Override
	protected Composite createNewConditionUnsatisfiedComposite(Composite parent) {		
		return new NeedVoucherComposite(parent, SWT.NONE);
	}

	protected IScriptResultChangedListener scriptResultChangedListener = new IScriptResultChangedListener(){	
		public void scriptResultsChanged(ScriptResultsChangedEvent event) {
			updateParts();
		}
	};		
}
