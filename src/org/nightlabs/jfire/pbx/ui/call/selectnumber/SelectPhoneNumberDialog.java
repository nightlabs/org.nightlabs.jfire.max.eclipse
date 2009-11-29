package org.nightlabs.jfire.pbx.ui.call.selectnumber;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.jfire.pbx.ui.resource.Messages;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.datafield.PhoneNumberDataField;

public class SelectPhoneNumberDialog extends ResizableTitleAreaDialog
{
	private PropertySet person;
	private List<PhoneNumberDataField> phoneNumberDataFields;
	private TableViewer tableViewer;
	private PhoneNumberDataField selectedPhoneNumberDataField;

	public SelectPhoneNumberDialog(Shell shell, PropertySet person, List<PhoneNumberDataField> phoneNumberDataFields) {
		super(shell, Messages.RESOURCE_BUNDLE);
		this.person = person;
		this.phoneNumberDataFields = phoneNumberDataFields;
	}

	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		Button button = super.createButton(parent, id, label, defaultButton);

		if (OK == id)
			button.setEnabled(false);

		return button;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(
				String.format(Messages.getString("org.nightlabs.jfire.pbx.ui.call.selectnumber.SelectPhoneNumberDialog.windowTitle"), person.getDisplayName()) //$NON-NLS-1$
		);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(
				String.format(Messages.getString("org.nightlabs.jfire.pbx.ui.call.selectnumber.SelectPhoneNumberDialog.title"), person.getDisplayName()) //$NON-NLS-1$
		);

		Composite area = (Composite) super.createDialogArea(parent);
		tableViewer = new TableViewer(area, SWT.BORDER | SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);
		tableViewer.setLabelProvider(new TableLabelProvider() {
			@Override
			public String getColumnText(Object element, int columnIndex) {
				PhoneNumberDataField phoneNumberDataField = (PhoneNumberDataField) element;
				switch (columnIndex) {
					case 0:
						return phoneNumberDataField.getStructField().getStructBlock().getName().getText();
					case 1:
						return phoneNumberDataField.getStructField().getName().getText();
					case 2:
						return phoneNumberDataField.getPhoneNumberAsString();
					default:
						return null;
				}
			}
		});
		tableViewer.setContentProvider(new ArrayContentProvider());

		TableLayout tl = new TableLayout();
		TableColumn tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.pbx.ui.call.selectnumber.SelectPhoneNumberDialog.columnHeader[block].text")); //$NON-NLS-1$
		tl.addColumnData(new ColumnWeightData(33));

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.pbx.ui.call.selectnumber.SelectPhoneNumberDialog.columnHeader[field].text")); //$NON-NLS-1$
		tl.addColumnData(new ColumnWeightData(33));

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.pbx.ui.call.selectnumber.SelectPhoneNumberDialog.columnHeader[number].text")); //$NON-NLS-1$
		tl.addColumnData(new ColumnWeightData(33));

		table.setLayout(tl);
		tableViewer.setInput(phoneNumberDataFields);

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				phoneNumberSelected();
			}
		});

		return area;
	}

	private void phoneNumberSelected()
	{
		getButton(OK).setEnabled(!tableViewer.getSelection().isEmpty());
	}

	@Override
	protected void okPressed() {
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		selectedPhoneNumberDataField = (PhoneNumberDataField) selection.getFirstElement();
		super.okPressed();
	}

	public PhoneNumberDataField getSelectedPhoneNumberDataField() {
		return selectedPhoneNumberDataField;
	}
}
