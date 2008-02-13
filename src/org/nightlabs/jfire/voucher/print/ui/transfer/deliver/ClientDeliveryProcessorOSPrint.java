package org.nightlabs.jfire.voucher.print.ui.transfer.deliver;

import java.awt.print.PrinterJob;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.nightlabs.jfire.scripting.editor2d.ScriptRootDrawComponent;
import org.nightlabs.jfire.scripting.print.ui.transfer.delivery.AbstractClientDeliveryProcessorOSPrint;
import org.nightlabs.jfire.scripting.print.ui.transfer.delivery.AbstractScriptDataProviderThread;
import org.nightlabs.jfire.trade.ui.transfer.deliver.AbstractClientDeliveryProcessor;
import org.nightlabs.jfire.voucher.editor2d.ui.iofilter.VoucherXStreamFilter;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ClientDeliveryProcessorOSPrint
//extends AbstractClientDeliveryProcessorPrint
extends AbstractClientDeliveryProcessorOSPrint
{
	private static final Logger logger = Logger.getLogger(ClientDeliveryProcessorOSPrint.class);
	
	@Override
	protected AbstractScriptDataProviderThread createScriptDataProviderThread(
			AbstractClientDeliveryProcessor clientDeliveryProcessor)
	{
		return new VoucherDataProviderThread(clientDeliveryProcessor);
	}

	private String requirementCheckKey = null;

	@Override
	public String getRequirementCheckKey()
	{
		return requirementCheckKey;
	}

	@Override
	public void init()
	{
//		super.init();
//
//		requirementCheckKey = null;
//
//		// check, whether all Article's VoucherType s have a layout assigned
//		try {
//			VoucherManager vm = VoucherManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//			Map<ArticleID, PrintabilityStatus> m = vm.getArticleID2PrintabilityStatusMap(new HashSet<ArticleID>(getDelivery().getArticleIDs()));
//			for (PrintabilityStatus s : m.values()) {
//				if (PrintabilityStatus.MISSING_VOUCHER_LAYOUT == s) {
//					requirementCheckKey = PrintabilityStatus.class.getName() + '.' + PrintabilityStatus.MISSING_VOUCHER_LAYOUT.toString();
//					MessageDialog.openError(getDeliveryEntryPage().getShell(), "Missing Voucher Layout", "Cannot print, because at least one VoucherType has no VoucherLayout assigned!");
//				}
//				else if (PrintabilityStatus.OK != s)
//					throw new IllegalStateException("Unexpected PrintabilityStatus: " + s);
//
//				if (PrintabilityStatus.OK != s) {
//					// TODO deactivate the wizard's next button somehow!
//					break;
//				}
//			}
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
	}

	@Override
	protected void printTickets(List<ScriptRootDrawComponent> tickets, PrinterJob printJob)
	{
		long start = 0;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
			logger.debug("print "+tickets.size()+" in printJob");
		}
		
		if (pageFormat != null)
			printJob.setPrintable(getPrintable(tickets), pageFormat);
		else
			printJob.setPrintable(getPrintable(tickets));
		
		try {
			printJob.print();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("printJob.print() took "+(System.currentTimeMillis()-start)+" ms!");
		}
	}
	
	public static final String PRINTER_USE_CASE_VOUCHER_PRINT = "PrinterUseCase-OSVoucherPrint";
	@Override
	protected String getPrinterUseCase() {
		return PRINTER_USE_CASE_VOUCHER_PRINT;
	}

	@Override
	protected ScriptRootDrawComponent getScriptRootDrawComponent(File file)
	{
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					file));
			ScriptRootDrawComponent scriptRootDrawComponent = null;
			try {
				VoucherXStreamFilter xStreamFilter = new VoucherXStreamFilter();
				scriptRootDrawComponent = (ScriptRootDrawComponent) xStreamFilter
						.read(in);
				return scriptRootDrawComponent;
			} finally {
				in.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
