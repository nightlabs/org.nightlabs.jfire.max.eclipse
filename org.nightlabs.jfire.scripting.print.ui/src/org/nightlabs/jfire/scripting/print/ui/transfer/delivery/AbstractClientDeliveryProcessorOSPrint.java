package org.nightlabs.jfire.scripting.print.ui.transfer.delivery;

import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.nightlabs.base.ui.print.PrinterInterfaceManager;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.print.DrawComponentPageable;
import org.nightlabs.editor2d.print.DrawComponentPrintable.PrintConstant;
import org.nightlabs.jfire.scripting.editor2d.ScriptRootDrawComponent;
import org.nightlabs.jfire.store.deliver.DeliveryException;
import org.nightlabs.jfire.store.deliver.DeliveryResult;
import org.nightlabs.print.PrinterConfiguration;
import org.nightlabs.util.CollectionUtil;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractClientDeliveryProcessorOSPrint
extends AbstractClientDeliveryProcessorPrint
{
	private static final Logger logger = Logger.getLogger(AbstractClientDeliveryProcessorOSPrint.class);

	private long printStart;
	@Override
	protected DeliveryResult printBegin()
	throws DeliveryException
	{
		printStart = System.currentTimeMillis();
		return null;
	}

	@Override
	protected DeliveryResult printEnd()
	throws DeliveryException
	{
		if (logger.isDebugEnabled()) {
			long printDuration = System.currentTimeMillis() - printStart;
			logger.debug("complete print took "+printDuration+" ms!"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return null;
	}
	
	private volatile boolean defaultPageQueried = false;
	protected PageFormat cachedPrinterDefaultPageFormat;
	private PrinterConfiguration printerConfiguration;
	
	/**
	 * Creates the {@link PrinterConfiguration} (if necessary asks the user) 
	 * that should be used for one run of this delivery processor.
	 * This is the (possibly modified) stored configuration for the printer
	 * useCaseID used for this processor (see {@link #getPrinterUseCaseID()}). 
	 * 
	 * @return The {@link PrinterConfiguration} for this run. 
	 */
	protected synchronized PrinterConfiguration getPrinterConfiguration() {
		if (printerConfiguration == null)
			printerConfiguration = PrinterInterfaceManager.sharedInstance().getPrinterConfiguration(getPrinterUseCaseID());
		return printerConfiguration;
	}
	
	/**
	 * Creates a new {@link PrinterJob} and configures it with 
	 * the {@link PrinterConfiguration} obtained via {@link #getPrinterConfiguration()}.
	 * 
	 * @return
	 */
	protected PrinterJob createConfiguredPrinterJob() {
		long start = System.currentTimeMillis();
		PrinterJob printerJob = PrinterJob.getPrinterJob();
		if (logger.isDebugEnabled()) {
			logger.debug("createConfiguredPrinterJob() PrinterJob.getPrinterJob() took: " + (System.currentTimeMillis() - start) + " ms.");
			start = System.currentTimeMillis();
		}
		PrinterConfiguration configuration = getPrinterConfiguration();
		if (!defaultPageQueried) {
			cachedPrinterDefaultPageFormat = printerJob.defaultPage();
			if (logger.isDebugEnabled())
				logger.debug("createConfiguredPrinterJob() printerJob.defaultPage() took: " + (System.currentTimeMillis() - start) + " ms.");
			defaultPageQueried = true;
		}
		try {
			PrinterInterfaceManager.sharedInstance().configurePrinterJob(printerJob, configuration);
		} catch (PrinterException e) {
			throw new RuntimeException(e);
		}
		return printerJob;
	}

	/**
	 * Returns the Printer Use Case ID to be used for this print.
	 * @return the Printer Use Case ID
	 */
	protected abstract String getPrinterUseCaseID();

//	protected Printable getPrintable(List<ScriptRootDrawComponent> drawComponents)
//	{
//		List<DrawComponent> dcs = CollectionUtil.castList(drawComponents);
//		return new DrawComponentPrintable(dcs, PrintConstant.ORIGINAL_SIZE);
//	}
	protected Pageable getPageable(List<? extends ScriptRootDrawComponent> drawComponents, PageFormat pageFormat)
	{
		List<DrawComponent> dcs = CollectionUtil.castList(drawComponents);
		return new DrawComponentPageable(pageFormat, dcs, PrintConstant.ORIGINAL_SIZE, true);
	}

	private List<ScriptRootDrawComponent> buffer;
	protected List<ScriptRootDrawComponent> getBuffer() {
		if (buffer == null) {
			buffer = new ArrayList<ScriptRootDrawComponent>();
		}
		return buffer;
	}
}
