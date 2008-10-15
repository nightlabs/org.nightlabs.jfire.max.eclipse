package org.nightlabs.jfire.scripting.print.ui.transfer.delivery;

import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.nightlabs.base.ui.print.PrinterInterfaceManager;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.print.DrawComponentPrintable;
import org.nightlabs.editor2d.print.DrawComponentPrintable.PrintConstant;
import org.nightlabs.jfire.scripting.editor2d.ScriptRootDrawComponent;
import org.nightlabs.jfire.store.deliver.DeliveryException;
import org.nightlabs.jfire.store.deliver.DeliveryResult;
import org.nightlabs.print.AWTPrinter;
import org.nightlabs.print.PrinterConfiguration;
import org.nightlabs.print.PrinterInterface;
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

	protected PageFormat pageFormat;
	protected PrinterJob configurePrinter()
	{
		long start = System.currentTimeMillis();
		AWTPrinter awtPrinter = getAWTPrinter();

		if (logger.isDebugEnabled()) {
			logger.debug("getting printer took "+(System.currentTimeMillis()-start)+" ms!"); //$NON-NLS-1$ //$NON-NLS-2$
			start = System.currentTimeMillis();
		}

		PrinterConfiguration printerConfiguration = awtPrinter.getConfiguration();

		if (logger.isDebugEnabled()) {
			logger.debug("getting printer configuration took "+(System.currentTimeMillis()-start)+" ms!"); //$NON-NLS-1$ //$NON-NLS-2$
			start = System.currentTimeMillis();
		}

		PrinterJob printJob = awtPrinter.getPrinterJob();
		printJob.setJobName("Ipanema Ticket"); //$NON-NLS-1$

		if (logger.isDebugEnabled()) {
			logger.debug("getting print job took "+(System.currentTimeMillis()-start)+" ms!");				 //$NON-NLS-1$ //$NON-NLS-2$
			start = System.currentTimeMillis();
		}

		if (printerConfiguration != null) {
			if (printerConfiguration.getPageFormat() != null)
				pageFormat = printJob.defaultPage(printerConfiguration.getPageFormat());
			else
				pageFormat = null; // printJob.defaultPage();
		}
		if (logger.isDebugEnabled())
			logger.debug("printJob.defaultPage(...) took "+(System.currentTimeMillis()-start)+" ms!"); //$NON-NLS-1$ //$NON-NLS-2$

		return printJob;
	}

	protected AWTPrinter getAWTPrinter()
	{
		PrinterInterface printer;
		try {
			printer = PrinterInterfaceManager.sharedInstance().getConfiguredPrinterInterface(
					org.nightlabs.print.PrinterInterfaceManager.INTERFACE_FACTORY_AWT,
					getPrinterUseCase()
			);
		} catch (PrinterException e) {
			throw new RuntimeException(e);
		}
		return (AWTPrinter) printer;
	}

	/**
	 * Returns the Printer Use Case.
	 * @return the Printer Use Case
	 */
	protected abstract String getPrinterUseCase();

	protected Printable getPrintable(List<ScriptRootDrawComponent> drawComponents)
	{
		List<DrawComponent> dcs = CollectionUtil.castList(drawComponents);
		return new DrawComponentPrintable(dcs, PrintConstant.FIT_PAGE);
	}

	private List<ScriptRootDrawComponent> buffer;
	protected List<ScriptRootDrawComponent> getBuffer() {
		if (buffer == null) {
			buffer = new ArrayList<ScriptRootDrawComponent>();
		}
		return buffer;
	}

	@Override
	protected void printDocuments(List<ScriptRootDrawComponent> scriptRootDrawComponents, boolean lastEntry)
	throws DeliveryException
	{
		long start = 0;
		if (logger.isDebugEnabled())  {
			start = System.currentTimeMillis();
			logger.info("printDocuments begin!");			 //$NON-NLS-1$
		}

		PrinterJob printJob = configurePrinter();
		printTickets(scriptRootDrawComponents, printJob);

		if (logger.isDebugEnabled())
			logger.info("printJob.print() took "+(System.currentTimeMillis()-start)+" ms!"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Prints the given {@link ScriptRootDrawComponent}s to the given {@link PrinterJob}.
	 *
	 * @param tickets the List of {@link ScriptRootDrawComponent}s to print
	 * @param printJob the {@link PrinterJob} to print the tickets
	 */
	protected abstract void printTickets(List<ScriptRootDrawComponent> tickets, PrinterJob printJob);
}
