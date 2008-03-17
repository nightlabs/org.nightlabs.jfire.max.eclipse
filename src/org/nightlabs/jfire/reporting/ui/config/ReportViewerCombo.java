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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.reporting.ui.viewer.ReportViewerRegistry;
import org.nightlabs.jfire.reporting.ui.viewer.ReportViewerRegistry.ReportViewerEntry;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportViewerCombo extends XComboComposite<ReportViewerEntry> {

	private static class LabelProvider extends TableLabelProvider {

		public String getColumnText(Object element, int arg1) {
			return ((ReportViewerEntry)element).getName();
		}

		@Override
		public String getText(Object element) {
			return getColumnText(element, 0);
		}

	}

	/**
	 *
	 */
	public ReportViewerCombo(Composite parent, int style) {
		super(
				parent,
				SWT.READ_ONLY,
				new LabelProvider()
			);
		setInput(ReportViewerRegistry.sharedInstance().getReportViewerEntries());
	}

	public void refresh(ReportUseCase useCase) {
		removeAll();
		List<ReportViewerEntry> entries = ReportViewerRegistry.sharedInstance().getReportViewerEntries();
		if (useCase != null && useCase.getMinAdapterClasses() != null) {
			List<ReportViewerEntry> filteredEntries = new LinkedList<ReportViewerEntry>();
			entryLoop: for (ReportViewerEntry entry : entries) {
				for (Class<?> useCaseAdapter : useCase.getMinAdapterClasses()) {
					if (!entry.getReportViewerFactory().isAdaptable(useCaseAdapter))
						continue entryLoop;
				}
				filteredEntries.add(entry);
			}
			setInput(filteredEntries);
		}
		else
			setInput(entries);
	}

}
