/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://opensource.org/licenses/lgpl-license.php                         *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.reporting.ui.config;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.print.PrinterConfigurationRegistry;
import org.nightlabs.base.ui.print.PrinterUseCase;
import org.nightlabs.base.ui.print.pref.PrinterUseCaseCombo;
import org.nightlabs.jfire.reporting.Birt;
import org.nightlabs.jfire.reporting.ui.config.ReportViewPrintConfigModule.UseCaseConfig;
import org.nightlabs.jfire.reporting.ui.resource.Messages;
import org.nightlabs.jfire.reporting.ui.viewer.ReportViewerRegistry;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class EditUseCaseConfigComposite extends XComposite {

	private String useCaseID;
	private BirtOutputCombo viewFormatCombo;
	private ReportViewerCombo reportViewerCombo;
	private BirtOutputCombo printFormatCombo;
	private PrinterUseCaseCombo printerUseCaseCombo;
	
	/**
	 * @param parent
	 * @param style
	 */
	public EditUseCaseConfigComposite(Composite parent, int style) {
		super(parent, style);
		initGUI();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 */
	public EditUseCaseConfigComposite(Composite parent, int style,
			LayoutMode layoutMode) {
		super(parent, style, layoutMode);
		initGUI();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutDataMode
	 */
	public EditUseCaseConfigComposite(Composite parent, int style,
			LayoutDataMode layoutDataMode) {
		super(parent, style, layoutDataMode);
		initGUI();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public EditUseCaseConfigComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
		initGUI();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 * @param cols
	 */
	public EditUseCaseConfigComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode, int cols) {
		super(parent, style, layoutMode, layoutDataMode, cols);
		initGUI();
	}
	
	protected void initGUI() {
		Label viewFormatLabel = new Label(this, SWT.WRAP);
		viewFormatLabel.setText(Messages.getString("org.nightlabs.jfire.reporting.ui.config.EditUseCaseConfigComposite.viewFormatLabel.text")); //$NON-NLS-1$
		viewFormatCombo = new BirtOutputCombo(this, SWT.READ_ONLY);
		Label reportViewerLabel = new Label(this, SWT.WRAP);
		reportViewerLabel.setText(Messages.getString("org.nightlabs.jfire.reporting.ui.config.EditUseCaseConfigComposite.reportViewerLabel.text")); //$NON-NLS-1$
		reportViewerCombo = new ReportViewerCombo(this, SWT.READ_ONLY);
		reportViewerCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label printFormatLabel = new Label(this, SWT.WRAP);
		printFormatLabel.setText(Messages.getString("org.nightlabs.jfire.reporting.ui.config.EditUseCaseConfigComposite.printFormatLabel.text")); //$NON-NLS-1$
		printFormatCombo = new BirtOutputCombo(this, SWT.READ_ONLY);
		printerUseCaseCombo = new PrinterUseCaseCombo(this, SWT.READ_ONLY);
	}
	
	public void setUseCaseConfig(String reportUseCaseID, UseCaseConfig useCaseConfig) {
		if (useCaseConfig != null) {
			useCaseID = reportUseCaseID;
			if (useCaseID != null) {
				ReportUseCase useCase = ReportUseCaseRegistry.sharedInstance().getReportUseCase(useCaseID);
				if (useCase != null)
					reportViewerCombo.refresh(useCase);
			} else
				reportViewerCombo.refresh(null);
			
			viewFormatCombo.selectElement(Birt.OutputFormat.valueOf(useCaseConfig.getViewerFormat()));
			reportViewerCombo.selectElement(
					ReportViewerRegistry.sharedInstance().getReportViewerEntry(useCaseConfig.getReportViewerID())
			);
			printFormatCombo.selectElement(Birt.OutputFormat.valueOf(useCaseConfig.getPrintFormat()));
			PrinterUseCase printerUseCase = PrinterConfigurationRegistry.sharedInstance().getPrinterUseCase(useCaseConfig.getPrinterUseCase());
			if (printerUseCase != null)
				printerUseCaseCombo.selectElement(printerUseCase);
		}
	}
	
	public UseCaseConfig readUseCaseConfig() {
		UseCaseConfig useCaseConfig = new UseCaseConfig(useCaseID);
		useCaseConfig.setViewerFormat((viewFormatCombo.getSelectedElement() != null) ? viewFormatCombo.getSelectedElement().toString() : Birt.OutputFormat.pdf.toString());
		useCaseConfig.setReportViewerID((reportViewerCombo.getSelectedElement() != null) ? reportViewerCombo.getSelectedElement().getId() : ReportViewerRegistry.DEFAULT_REPORT_VIEWER_ID);
		useCaseConfig.setPrintFormat((printFormatCombo.getSelectedElement() != null) ? printFormatCombo.getSelectedElement().toString() : Birt.OutputFormat.pdf.toString());
		if (printerUseCaseCombo.getSelectedElement() != null)
			useCaseConfig.setPrinterUseCase(printerUseCaseCombo.getSelectedElement().getId());
		return useCaseConfig;
	}

}
