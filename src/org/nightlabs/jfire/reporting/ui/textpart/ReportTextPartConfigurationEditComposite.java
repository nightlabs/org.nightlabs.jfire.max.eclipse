/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.textpart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.language.LanguageChooser;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.reporting.textpart.ReportTextPart;
import org.nightlabs.jfire.reporting.textpart.ReportTextPartConfiguration;
import org.nightlabs.jfire.reporting.ui.resource.Messages;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportTextPartConfigurationEditComposite extends XComposite {

	private ReportTextPartConfiguration reportTextPartConfiguration;
	private TextPartTable textPartTable;
	private Composite editorWrapper;
	private StackLayout editorWrapperLayout;
	private Map<ReportTextPart, ReportTextPartEditComposite> editComposites = new HashMap<ReportTextPart, ReportTextPartEditComposite>();
	private ListenerList changedListeners = new ListenerList();
	private IReportTextPartChangedListener partChangedListener = new IReportTextPartChangedListener() {
		@Override
		public void reportTextPartChanged(ReportTextPartChangedEvent evt) {
			notifyChangedListeners(evt.getReportTextPart());
		}
	};
	private LanguageChooser languageChooser;
	private boolean showTextPartID;
	
	private class TextPartTable extends AbstractTableComposite<ReportTextPart> {

		public TextPartTable(Composite parent, int style) {
			super(parent, style);
		}

		@Override
		protected void createTableColumns(TableViewer tableViewer, Table table) {
			table.setHeaderVisible(false);
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
	 * Create a new {@link ReportTextPartConfigurationEditComposite}.
	 * 
	 * @param parent The parent to use.
	 * @param style The style to apply.
	 */
	public ReportTextPartConfigurationEditComposite(Composite parent, int style, LanguageChooser languageChooser, boolean showTextPartID) {
		super(parent, style, LayoutMode.NONE);
		this.languageChooser = languageChooser;
		this.showTextPartID = showTextPartID;
		createContents();
	}

	/**
	 * Create a new {@link ReportTextPartConfigurationEditComposite}.
	 * 
	 * @param parent The parent to use.
	 * @param style The style to apply.
	 * @param layoutDataMode The layoutDataMode to apply.
	 */
	public ReportTextPartConfigurationEditComposite(Composite parent,
			int style, LayoutDataMode layoutDataMode, LanguageChooser languageChooser, boolean showTextPartID) {
		super(parent, style, LayoutMode.NONE, layoutDataMode);
		this.languageChooser = languageChooser;
		this.showTextPartID = showTextPartID;
		createContents();
	}

	protected synchronized void createContents() {
		setLayout(new FillLayout());
		SashForm sashForm = new SashForm(this, SWT.HORIZONTAL);
		textPartTable = new TextPartTable(sashForm, SWT.NONE);
		textPartTable.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (textPartTable.getSelectionCount() != 1)
					return;
				switchReportTextPart(textPartTable.getFirstSelectedElement());
			}
		});

		editorWrapper = new Composite(sashForm, SWT.NONE);

		editorWrapperLayout = new StackLayout();
		editorWrapper.setLayout(editorWrapperLayout);
		sashForm.setWeights(new int[] {1, 3});
	}

	public synchronized void setReportTextPartConfiguration(ReportTextPartConfiguration reportTextPartConfiguration) {
		if (reportTextPartConfiguration == null)
			return;
		this.reportTextPartConfiguration = reportTextPartConfiguration;
		List<ReportTextPart> parts = reportTextPartConfiguration.getReportTextParts(); 

		for (ReportTextPartEditComposite composite : editComposites.values()) {
			composite.dispose();
		}
		editComposites.clear();
		for (ReportTextPart part : parts) {
			final ReportTextPartEditComposite editComposite = new ReportTextPartEditComposite(
					editorWrapper, SWT.NONE, part, languageChooser, showTextPartID);
			editComposite.addReportTextPartChangedListener(partChangedListener);
			editComposite.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent e) {
					editComposite.removeReportTextPartChangedListener(partChangedListener);
				}
			});
			editComposite.adaptToToolkit();
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
			throw new IllegalStateException("EditComposite for ReportTextPart "+part.getReportTextPartID()+" is unknown"); //$NON-NLS-1$ //$NON-NLS-2$
		if (editComposite.isDisposed())
			throw new IllegalStateException("EditComposite for ReportTextPart "+part.getReportTextPartID()+" is already disposed"); //$NON-NLS-1$ //$NON-NLS-2$

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
	
	public void addReportTextPartConfigurationChangedListener(IReportTextPartConfigurationChangedListener listener) {
		changedListeners.add(listener);
	}
	
	public void removeReportTextPartConfigurationChangedListener(IReportTextPartConfigurationChangedListener listener) {
		changedListeners.remove(listener);
	}
	
	protected void notifyChangedListeners(ReportTextPart reportTextPart) {
		Object[] listeners = changedListeners.getListeners();
		if (listeners.length <= 0)
			return;
		ReportTextPartConfigurationChangedEvent event = new ReportTextPartConfigurationChangedEvent(reportTextPartConfiguration, reportTextPart);
		for (Object listener : listeners) {
			if (listener instanceof IReportTextPartConfigurationChangedListener) {
				((IReportTextPartConfigurationChangedListener) listener).reportTextPartConfigurationChanged(event);
			}
		}
	}
	
}
