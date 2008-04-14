package org.nightlabs.jfire.voucher.editor2d.ui.scripting;

import java.util.Map;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.nightlabs.base.ui.progress.XProgressMonitor;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.id.CurrencyID;
import org.nightlabs.jfire.scripting.editor2d.ui.script.AbstractScriptResultProvider;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.voucher.editor2d.ui.VoucherEditorPlugin;
import org.nightlabs.jfire.voucher.scripting.PreviewParameterSet;
import org.nightlabs.jfire.voucher.scripting.PreviewParameterValuesResult;
import org.nightlabs.jfire.voucher.store.VoucherType;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherScriptResultProvider
extends AbstractScriptResultProvider
{
	private static VoucherScriptResultProvider sharedInstance;
	public static VoucherScriptResultProvider sharedInstance() {
		if (sharedInstance == null) {
			synchronized (VoucherScriptResultProvider.class) {
				if (sharedInstance == null)
					sharedInstance = new VoucherScriptResultProvider();
			}
		}
		return sharedInstance;
	}
	
	protected VoucherScriptResultProvider() {
	}

	private Map<ScriptRegistryItemID, Object> scriptResults = null;
//	public Map<ScriptRegistryItemID, Object> getScriptResults(Set<ScriptRegistryItemID> scriptIDs,
//			IParameterProvider parameterProvider, ProgressMonitor monitor)
	public Map<ScriptRegistryItemID, Object> getScriptResults()
	{
		if (scriptResults == null && getSelectedObject() != null && getSelectedCurrency() != null)
		{
			Map<ProductTypeID, Map<ScriptRegistryItemID, Object>> voucherScriptingResult =
				getVoucherScriptingResult(getPreviewParameterSet(), new XProgressMonitor());
			scriptResults = voucherScriptingResult.values().iterator().next();
			return scriptResults;
		}
		return scriptResults;
	}

	private VoucherType selectedVoucherType;
	public VoucherType getSelectedObject() {
		return selectedVoucherType;
	}
	public void setSelectedObject(VoucherType selectedObject)
	{
		this.selectedVoucherType = selectedObject;
//		getScriptResults(Collections.emptySet(), null, new NullProgressMonitor());
		getScriptResults();
		notifyListener();
	}
	
	private Currency selectedCurrency;
	public Currency getSelectedCurrency() {
		return selectedCurrency;
	}
	public void setSelectedCurrency(Currency selectedCurrency) {
		this.selectedCurrency = selectedCurrency;
	}
	
	protected PreviewParameterSet getPreviewParameterSet()
	{
		return new PreviewParameterSet(
				(ProductTypeID)JDOHelper.getObjectId(getSelectedObject()),
				(CurrencyID)JDOHelper.getObjectId(getSelectedCurrency()));
	}
	
	protected Map<ProductTypeID, Map<ScriptRegistryItemID, Object>> getVoucherScriptingResult(
			PreviewParameterSet previewParameterSet, IProgressMonitor monitor)	
	{
		try {
			monitor.beginTask("Loading VoucherScriptResult", 2);
			monitor.worked(1);
			Map<ProductTypeID, Map<ScriptRegistryItemID, Object>> result = VoucherEditorPlugin.
				getDefault().getVoucherManager().getPreviewVoucherData(previewParameterSet);
			monitor.worked(1);
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public PreviewParameterValuesResult getPreviewParameterValuesResult(VoucherType voucherType)
	{
		ProductTypeID productTypeID = (ProductTypeID)JDOHelper.getObjectId(voucherType);
		try {
			return VoucherEditorPlugin.getDefault().getVoucherManager().getPreviewParameterValues(productTypeID);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
			
}
