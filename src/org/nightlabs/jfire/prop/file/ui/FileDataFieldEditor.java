package org.nightlabs.jfire.prop.file.ui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditor;
import org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditorFactory;
import org.nightlabs.jfire.base.ui.prop.edit.DataFieldEditor;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.ExpandableBlocksEditor;
import org.nightlabs.jfire.base.ui.prop.edit.fieldbased.FieldBasedEditor;
import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.file.FileDataField;
import org.nightlabs.jfire.prop.file.FileStructField;
import org.nightlabs.jfire.prop.file.ui.resource.Messages;
import org.nightlabs.language.LanguageCf;
import org.nightlabs.util.IOUtil;
import org.nightlabs.util.NLLocale;

/**
 * @author Tobias Langner <!-- tobias[dot]langner[at]nightlabs[dot]de -->
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FileDataFieldEditor
extends AbstractDataFieldEditor<FileDataField>
{
	/**
	 * Use this before extension.
	 */
	private static final String EXTENSION_PREFIX = "*."; //$NON-NLS-1$

	/**
	 * Separate extension in the file dialog using this string.
	 */
	private static final String EXTENSION_SEPARATOR = ";"; //$NON-NLS-1$

	public static class Factory extends AbstractDataFieldEditorFactory<FileDataField> {

		@Override
		public String[] getEditorTypes() {
			return new String[] {ExpandableBlocksEditor.EDITORTYPE_BLOCK_BASED_EXPANDABLE, FieldBasedEditor.EDITORTYPE_FIELD_BASED};
		}

		@Override
		public Class<FileDataField> getPropDataFieldType() {
			return FileDataField.class;
		}

		@Override
		public DataFieldEditor<FileDataField> createPropDataFieldEditor(IStruct struct, FileDataField data) {
			return new FileDataFieldEditor(struct, data);
		}
	}

	private LanguageCf language;

	private Button openButton;
	private Text filenameTextbox;
	private Button openFileChooserButton;
	private Button clearButton;
	private Group group;
	private Label sizeLabel;
	private String fileDialogFilterPath;
	private Desktop desktop;

	public FileDataFieldEditor(IStruct struct, FileDataField data) {
		super(struct, data);
		language = new LanguageCf(NLLocale.getDefault().getLanguage());
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditor#setDataField(org.nightlabs.jfire.prop.DataField)
	 */
	@Override
	protected void setDataField(FileDataField dataField) {
		super.setDataField(dataField);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditor#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createControl(final Composite parent) {
		group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(5, false));

		XComposite.setLayoutDataMode(LayoutDataMode.GRID_DATA, group);

		openButton = new Button(group, SWT.PUSH);
		openButton.setText(Messages.getString("org.nightlabs.jfire.prop.file.ui.FileDataFieldEditor.openButton.text")); //$NON-NLS-1$
		openButton.setToolTipText("Open with the file with the default application.");
//		openButton.setLayoutData(new GridData());
		openButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (getDataField().isEmpty())
					return;

				try {
					File file = getDataField().saveToDir(IOUtil.getTempDir());
					desktop.open(file);
				} catch (IOException x) {
					throw new RuntimeException(x);
				}
			}
		});

		openButton.setEnabled(false);
		if (Desktop.isDesktopSupported()) {
			desktop = Desktop.getDesktop();
			if (desktop.isSupported(Desktop.Action.OPEN))
				openButton.setEnabled(true);
		}
		if (!openButton.isEnabled())
			openButton.setToolTipText("Your operating system or the java version do not support the 'desktop/open' action.");

		filenameTextbox = new Text(group, XComposite.getBorderStyle(parent));
		filenameTextbox.setEditable(false);
		filenameTextbox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		openFileChooserButton = new Button(group, SWT.PUSH);
		openFileChooserButton.setText("...");
		openFileChooserButton.setToolTipText("Browse for a file");
		openFileChooserButton.setLayoutData(new GridData());
		openFileChooserButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				fileChooserButtonPressed();
			}
		});

		clearButton = new Button(group, SWT.PUSH);
		clearButton.setText("&Clear");
		clearButton.setToolTipText("Clear the field, i.e. delete the data.");
		clearButton.setLayoutData(new GridData());
		clearButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				clearButtonPressed();
			}
		});
		clearButton.setEnabled(false);

		sizeLabel = new Label(group, SWT.NONE);

		return group;
	}

	@Override
	public void doRefresh() {
		FileStructField fileStructField = (FileStructField) getStructField();

		group.setText(fileStructField.getName().getText(language.getLanguageID()));

		FileDataField dataField = getDataField();
		if (!dataField.isEmpty()) {
			filenameTextbox.setText(dataField.getFileName());
		}
		else
			filenameTextbox.setText("");

		sizeLabel.setText(
				String.format("(max %d KB)",
						new Object[] { new Long(fileStructField.getMaxSizeKB()) }));
		sizeLabel.pack();
		sizeLabel.getParent().layout(true, true);

		handleManagedBy(dataField.getManagedBy());
	}

	protected void handleManagedBy(String managedBy)
	{
		for (Control child : group.getChildren()) {
			child.setEnabled(managedBy == null);
		}
		if (managedBy != null)
			group.setToolTipText(String.format("This field cannot be modified, because it is managed by a different system: %s", managedBy));
		else
			group.setToolTipText(null);
	}

	/**
	 * Open the file file browse dialog.
	 * @param parent The parent shell
	 * @return the selected file file name or <code>null</code> if
	 * 		no file file was selected
	 */
	private String openFileFileDialog(Shell parent)
	{
		FileStructField fileStructField = (FileStructField) getStructField();
		List<String> extList = fileStructField.getFileFormats();

		String[] extensions = new String[extList.size()+1];
		String[] names = new String[extList.size()+1];
		names[0] = "All supported files";
		int i = 1;
		for (String ext : extList) {
			String extension = EXTENSION_PREFIX + ext.toLowerCase() + EXTENSION_SEPARATOR + EXTENSION_PREFIX + ext.toUpperCase();
			if(extensions[0] == null)
				extensions[0] = extension;
			else
				extensions[0] += EXTENSION_SEPARATOR + extension;
			extensions[i] = extension;
			names[i] = String.format("%s Files", ext.toUpperCase());
			i++;
		}

		FileDialog fileDialog = new FileDialog(parent);
		fileDialog.setText("Choose a file");
		fileDialog.setFilterNames(names);
		fileDialog.setFilterExtensions(extensions);
		fileDialog.setFilterPath(fileDialogFilterPath);
		String filename = fileDialog.open();
		if(filename != null)
			fileDialogFilterPath = filename;
		return filename;
	}

	@Override
	public Control getControl() {
		return group;
	}

	@Override
	public void updatePropertySet() {
		if (!isChanged())
			return;

		FileDataField dataField = getDataField();
		String path = filenameTextbox.getText();
		if (path == null || path.isEmpty()) {
			dataField.clear();
		} else {

			//FIXME: get content type somehow!
			final String contentType;
			final String lowerPath = path.toLowerCase();
			if(lowerPath.endsWith(".png")) //$NON-NLS-1$
				contentType = "file/png"; //$NON-NLS-1$
			else if(lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg")) //$NON-NLS-1$ //$NON-NLS-2$
				contentType = "file/jpeg"; //$NON-NLS-1$
			else if(lowerPath.endsWith(".gif")) //$NON-NLS-1$
				contentType = "file/gif"; //$NON-NLS-1$
			else if(lowerPath.endsWith(".pdf")) //$NON-NLS-1$
				contentType = "application/pdf"; //$NON-NLS-1$
			else
				contentType = "application/unknown"; //$NON-NLS-1$

			// store the file as in the data field.
			final File fileFile = new File(path);

			try {
				dataField.loadFile(fileFile, contentType);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	public LanguageCf getLanguage() {
		return language;
	}

	/**
	 * Called when the file chooser button was pressed.
	 */
	private void fileChooserButtonPressed() {
		String filename = openFileFileDialog(openFileChooserButton.getShell());
		if (filename != null) {
			File file = new File(filename);
			// check if the file fulfills the size requirements
			FileStructField fileStructField = (FileStructField) getStructField();
			if (!fileStructField.validateSize(file.length()/1024)) {
				MessageDialog.openError(
						openFileChooserButton.getShell(),
						"Error: File too big!",
						String.format(
								"The maximum file size is %1$d KB.\n\nThe selected file's size is %2$d KB. Please choose a smaller one.",
								new Object[] { new Long(fileStructField.getMaxSizeKB()), new Long((file.length() / 1024))})
				);
				return;
			}

//			try {
				filenameTextbox.setText(filename);
				setChanged(true);
//				// there is already layout code in displayFile()... I moved this top-level layout stuff to this method, too. Marc
////						Composite top = parent;
////						while (top.getParent() != null)
////							top = top.getParent();
////						top.layout(true, true); // this is necessary, because otherwise a bigger file doesn't cause the widgets to grow and is therefore cut
//			} catch(SWTException swtex) {
//				MessageDialog.openError(
//						openFileChooserButton.getShell(),
//						"",
//						""
//				);
//			}
		}
	}

	/**
	 * Called when the clear button was pressed.
	 */
	private void clearButtonPressed()
	{
		filenameTextbox.setText(""); //$NON-NLS-1$
		setChanged(true);
	}
}


