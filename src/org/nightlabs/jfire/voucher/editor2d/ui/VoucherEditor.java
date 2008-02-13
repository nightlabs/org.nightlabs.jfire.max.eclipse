package org.nightlabs.jfire.voucher.editor2d.ui;

import java.util.Collection;
import java.util.Map;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.part.ControllablePart;
import org.nightlabs.editor2d.ui.AbstractPaletteFactory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.login.part.LSDPartController;
import org.nightlabs.jfire.scripting.editor2d.ui.AbstractScriptingEditor;
import org.nightlabs.jfire.scripting.editor2d.ui.script.IScriptResultChangedListener;
import org.nightlabs.jfire.scripting.editor2d.ui.script.IScriptResultProvider;
import org.nightlabs.jfire.scripting.editor2d.ui.script.ScriptResultsChangedEvent;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.jfire.voucher.editor2d.ui.scripting.VoucherScriptResultProvider;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherEditor
extends AbstractScriptingEditor
implements ControllablePart
{
	public VoucherEditor()
	{
		super();
		VoucherPartController.sharedInstance().registerPart(this, new FillLayout());
		getScriptResultProvider().addScriptResultsChangedListener(scriptResultChangedListener);
	}

//	@Override
//	protected Map<ScriptRegistryItemID, Object> getScriptResults(Collection<ScriptRegistryItemID> scriptIDs) {
//		return getScriptResultProvider().getScriptResults(scriptIDs);
//	}

	@Override
	protected Map<ScriptRegistryItemID, Object> getScriptResults(Collection<ScriptRegistryItemID> scriptIDs) {
		return getScriptResultProvider().getScriptResults();
	}

	protected boolean isScriptResultAvailable() {
		return getScriptResultProvider().getScriptResults() != null;
	}
	
	@Implement
	public boolean canDisplayPart() {
		return isScriptResultAvailable();
	}

	@Override
	protected AbstractPaletteFactory createPaletteFactory() {
		return new VoucherEditorPlatteFactory(getScriptEditor2DFactory());
	}
	
	@Override
	@Implement
	public void createPartControl(Composite parent)
	{
		if (!Login.isLoggedIn())
			LSDPartController.sharedInstance().createPartControl(this, parent);
		else
			VoucherPartController.sharedInstance().createPartControl(this, parent);
	}

	public IScriptResultProvider getScriptResultProvider() {
		return VoucherScriptResultProvider.sharedInstance();
	}
			
	protected IScriptResultChangedListener scriptResultChangedListener = new IScriptResultChangedListener(){
		public void scriptResultsChanged(ScriptResultsChangedEvent event) {
			clearScriptResults();
			assignAllScripts();
		}
	};
}
