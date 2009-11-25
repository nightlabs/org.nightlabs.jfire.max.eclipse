package org.nightlabs.jfire.prop.file.ui;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.ListComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.base.ui.prop.structedit.AbstractStructFieldEditor;
import org.nightlabs.jfire.base.ui.prop.structedit.AbstractStructFieldEditorFactory;
import org.nightlabs.jfire.prop.ModifyListener;
import org.nightlabs.jfire.prop.file.FileStructField;
import org.nightlabs.jfire.prop.file.ui.resource.Messages;

public class FileStructFieldEditor extends AbstractStructFieldEditor<FileStructField> {
	private FileStructFieldEditorComposite fileStructFieldEditorComposite;
	private FileStructField fileField;

	public static class FileStructFieldEditorFactory extends AbstractStructFieldEditorFactory {
//		public String getStructFieldEditorClass() {
//			return FileStructFieldEditor.class.getName();
//		}

		@Override
		public FileStructFieldEditor createStructFieldEditor() {
			return new FileStructFieldEditor();
		}
	}

	public FileStructFieldEditor() {

	}

	@Override
	protected Composite createSpecialComposite(Composite parent, int style) {
		fileStructFieldEditorComposite = new FileStructFieldEditorComposite(parent, this);
		return fileStructFieldEditorComposite;
	}

	@Override
	protected void setSpecialData(FileStructField field) {
		fileField = field;
		fileStructFieldEditorComposite.setField(field);

		fileField.addModifyListener(new ModifyListener() {
			public void modifyData() {
				updateErrorMessage();
				setChanged();
			}
		});
	}

	protected void updateErrorMessage() {
		if (!fileField.validateData()) {
			setErrorMessage(fileField.getValidationError());
		}	else {
			setErrorMessage(""); //$NON-NLS-1$
		}
	}

	@Override
	public boolean validateInput() {
		return fileField.validateData();
	}

	@Override
	public String getErrorMessage() {
		return fileField.getValidationError();
	}

	@Override
	public void restoreData() {
		fileField.clearFileFormats();
		fileField.addFileFormat("*"); //$NON-NLS-1$
	}
}

class FileStructFieldEditorComposite extends XComposite implements Serializable {
	private static final long serialVersionUID = 1L;
	private Spinner sizeSpinner;
	private ListComposite<String> formatList;
	private FileStructField fileField;
	private FileStructFieldEditor editor;

	public FileStructFieldEditorComposite(Composite parent, FileStructFieldEditor fileStructFieldEditor) {
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA, 2);

		this.editor = fileStructFieldEditor;
		new Label(this, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.prop.file.ui.FileStructFieldEditor.maximumSizeLabel.text")); //$NON-NLS-1$
		sizeSpinner = new Spinner(this, getBorderStyle());
		sizeSpinner.setMaximum(Integer.MAX_VALUE);
		sizeSpinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				editor.setChanged();
			}
		});

		new Label(this, SWT.NONE); new Label(this, SWT.NONE); // Spacers

		new Label(this, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.prop.file.ui.FileStructFieldEditor.allowedExtensionsLabel.text")); //$NON-NLS-1$
		XComposite extComp = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA, 2);
		extComp.getGridData().horizontalSpan = 2;

		GridData gd = new GridData();

		XComposite editComp = new XComposite(extComp, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA, 3);
		formatList = new ListComposite<String>(editComp, SWT.V_SCROLL);
		formatList.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return "*." + element; //$NON-NLS-1$
			}
		});

		XComposite buttonComp = new XComposite(editComp, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.NONE);
		gd.widthHint = 25;
		final Button addButton = new Button(buttonComp, SWT.PUSH);
		addButton.setText(Messages.getString("org.nightlabs.jfire.prop.file.ui.FileStructFieldEditor.addButton.text")); //$NON-NLS-1$
		addButton.setLayoutData(gd);
		final Button remButton = new Button(buttonComp, SWT.PUSH);
		remButton.setText(Messages.getString("org.nightlabs.jfire.prop.file.ui.FileStructFieldEditor.removeButton.text")); //$NON-NLS-1$
		remButton.setLayoutData(gd);

		final Text newFormat = new Text(editComp, getBorderStyle());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalSpan = 2;
		gd.verticalAlignment = SWT.CENTER;
		newFormat.setLayoutData(gd);

		sizeSpinner.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {}
			public void focusLost(FocusEvent e) {
				fileField.setMaxSizeKB(sizeSpinner.getSelection());
			}
		});

		addButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				if (addExtension(newFormat.getText())) {
					newFormat.setText(""); //$NON-NLS-1$
					newFormat.setFocus();
				}
				editor.setChanged();
			}
		});

		remButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				fileField.removeFileFormat(formatList.getSelectedElement());
				formatList.setInput(fileField.getFileFormats());
				formatList.setSelection(0);
				editor.setChanged();
			}
		});

		newFormat.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				if (addExtension(newFormat.getText())) {
					newFormat.setText(""); //$NON-NLS-1$
					newFormat.setFocus();
				}
			}
			public void widgetSelected(SelectionEvent e) {}
		});
	}

	protected void setField(FileStructField field) {
		if (field == null) {
			setEnabled(false);
			return;
		}

		setEnabled(true);

		this.fileField = field;
		formatList.setInput(fileField.getFileFormats());
		sizeSpinner.setSelection((int) fileField.getMaxSizeKB());
	}

	protected boolean addExtension(String ext) {
		String text = ext;
		Matcher extMatcher = Pattern.compile("(?:\\*\\.|\\.)?([\\w\\*]+)").matcher(text); //$NON-NLS-1$
		if (extMatcher.matches()) {
			text = extMatcher.group(1);
			fileField.addFileFormat(text);
			formatList.setInput(fileField.getFileFormats());
			return true;
		}
		return false;
	}
}
