/**
 * 
 */
package org.nightlabs.jfire.reporting.trade.ui.articlecontainer.detail.action.print;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.jfire.reporting.trade.ui.resource.Messages;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ArticleContainerReportLocaleDialog extends
		ResizableTitleAreaDialog {

	private static class Table extends AbstractTableComposite<Locale> {

		public Table(Composite parent) {
			super(parent, SWT.NONE);
		}
		
		@Override
		protected void createTableColumns(TableViewer tableViewer,
				org.eclipse.swt.widgets.Table table) {
			table.setHeaderVisible(false);
		}

		@Override
		protected void setTableProvider(TableViewer tableViewer) {
			tableViewer.setContentProvider(new ArrayContentProvider());
			tableViewer.setLabelProvider(new TableLabelProvider() {
				@Override
				public String getColumnText(Object element, int columnIdx) {
					if (element instanceof Locale) {
						return ((Locale) element).getDisplayName(Locale.getDefault());
					}
					return String.valueOf(element);
				}
			});
		}
		
	}
	
	private Collection<Locale> locales;
	private Table localesTable;
	private Locale selectedLocale;
	
	/**
	 * @param shell
	 * @param resourceBundle
	 */
	public ArticleContainerReportLocaleDialog(Shell shell, Collection<Locale> locales) {
		super(shell, null);
		this.locales = locales;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.getString("org.nightlabs.jfire.reporting.trade.ui.articlecontainer.detail.action.print.ArticleContainerReportLocaleDialog.shellText")); //$NON-NLS-1$
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(Messages.getString("org.nightlabs.jfire.reporting.trade.ui.articlecontainer.detail.action.print.ArticleContainerReportLocaleDialog.dialogTitle")); //$NON-NLS-1$
		setMessage(Messages.getString("org.nightlabs.jfire.reporting.trade.ui.articlecontainer.detail.action.print.ArticleContainerReportLocaleDialog.dialogMessage")); //$NON-NLS-1$
		localesTable = new Table(parent);
		localesTable.setInput(locales);
		if (locales.iterator().hasNext()) {
			Locale locale = locales.iterator().next();
			localesTable.setSelectedElements(Collections.singleton(locale));
			selectedLocale = locale;
		}
		localesTable.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				updateOKButtonEnabled();
			}
		});
		localesTable.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				selectedLocale = localesTable.getFirstSelectedElement();
				if (selectedLocale != null) {
					okPressed();
				}
			}
		});
		return localesTable;
	}
	
	@Override
	protected void okPressed() {
		if (localesTable.getFirstSelectedElement() != null) {
			selectedLocale = localesTable.getFirstSelectedElement();
			super.okPressed();
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		updateOKButtonEnabled();
	}
	
	private void updateOKButtonEnabled() {
		if (localesTable != null) {
			setOKButtonEnabled(localesTable.getFirstSelectedElement() != null);
		} else {
			setOKButtonEnabled(false);
		}
	}
	
	private void setOKButtonEnabled(boolean enabled) {
		Button button = getButton(IDialogConstants.OK_ID);
		if (button != null)
			button.setEnabled(enabled);
	}
	
	public Locale getSelectedLocale() {
		return selectedLocale;
	}
}
