/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.textpart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.reporting.textpart.ReportTextPart;
import org.nightlabs.jfire.reporting.textpart.ReportTextPartConfiguration;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportTextPartConfigurationEditComposite extends XComposite {

	private ReportTextPartConfiguration reportTextPartConfiguration;
	private TextPartTable textPartTable;
	private XComposite textPartEditWrapper;
	private Composite editorWrapper;
	private StackLayout editorWrapperLayout;
	private Map<ReportTextPart, ReportTextPartEditComposite> editComposites = new HashMap<ReportTextPart, ReportTextPartEditComposite>();

	private class TextPartTable extends AbstractTableComposite<ReportTextPart> {

		public TextPartTable(Composite parent, int style) {
			super(parent, style);
		}

		@Override
		protected void createTableColumns(TableViewer tableViewer, Table table) {
		}

		@Override
		protected void setTableProvider(TableViewer tableViewer) {
			tableViewer.setContentProvider(new ArrayContentProvider());
			tableViewer.setLabelProvider(new TableLabelProvider() {
				@Override
				public String getColumnText(Object element, int columnIndex) {
					if (element instanceof ReportTextPart) {
						return ((ReportTextPart) element).getName().getText();
					}
					return null;
				}
			});
		}
	}

	/**
	 * @param parent
	 * @param style
	 */
	public ReportTextPartConfigurationEditComposite(Composite parent, int style) {
		super(parent, style, LayoutMode.NONE);
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutDataMode
	 */
	public ReportTextPartConfigurationEditComposite(Composite parent,
			int style, LayoutDataMode layoutDataMode) {
		super(parent, style, LayoutMode.NONE, layoutDataMode);
	}

	protected void createContents() {
		GridLayout layout = new GridLayout(2, false);
		XComposite.configureLayout(LayoutMode.TIGHT_WRAPPER, layout);
		setLayout(layout);
		textPartTable = new TextPartTable(this, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 150;
		textPartTable.setLayoutData(gd);
		textPartTable.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (textPartTable.getSelectionCount() != 1)
					return;
				switchReportTextPart(textPartTable.getFirstSelectedElement());
			}
		});

		textPartEditWrapper = new XComposite(this, SWT.NONE);

		editorWrapper = new Composite(textPartEditWrapper, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		editorWrapper.setLayoutData(gd);

		editorWrapperLayout = new StackLayout();
		editorWrapper.setLayout(editorWrapperLayout);
	}

	public void setReportTextPartConfiguration(ReportTextPartConfiguration reportTextPartConfiguration) {
		if (reportTextPartConfiguration == null)
			return;
		this.reportTextPartConfiguration = reportTextPartConfiguration;
		List<ReportTextPart> parts = reportTextPartConfiguration.getReportTextParts(); 

		for (ReportTextPartEditComposite composite : editComposites.values()) {
			composite.dispose();
		}
		editComposites.clear();
		for (ReportTextPart part : parts) {
			ReportTextPartEditComposite editComposite = new ReportTextPartEditComposite(editorWrapper, SWT.NONE, part);
			editComposites.put(part, editComposite);
		}

		textPartTable.setInput(parts);
		if (parts.size() > 0) {
			ReportTextPart part = parts.iterator().next();
			textPartTable.setSelection(new StructuredSelection(part));
			switchReportTextPart(part);
		}
	}

	private void switchReportTextPart(ReportTextPart part) {
		ReportTextPartEditComposite editComposite = editComposites.get(part);
		if (editComposite == null)
			throw new IllegalStateException("EditComposite for ReportTextPart "+part.getReportTextPartID()+" is unknown");
		if (editComposite.isDisposed())
			throw new IllegalStateException("EditComposite for ReportTextPart "+part.getReportTextPartID()+" is already disposed");

		editorWrapperLayout.topControl = editComposite;
		editorWrapper.layout();
	}

	public void updateReportTextPartConfiguration() {
		List<ReportTextPart> parts = reportTextPartConfiguration.getReportTextParts(); 
		for (ReportTextPart part : parts) {
			ReportTextPartEditComposite editComposite = editComposites.get(part);
			if (editComposite != null && !editComposite.isDisposed())
				editComposite.updateReportTextPart();
		}
	}
}
