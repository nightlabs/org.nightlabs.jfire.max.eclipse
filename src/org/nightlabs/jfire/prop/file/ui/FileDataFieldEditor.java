package org.nightlabs.jfire.prop.file.ui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.io.FileEditorInput;
import org.nightlabs.base.ui.part.PartAdapter;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditor;
import org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditorFactory;
import org.nightlabs.jfire.base.ui.prop.edit.DataFieldEditor;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.ExpandableBlocksEditor;
import org.nightlabs.jfire.base.ui.prop.edit.fieldbased.FieldBasedEditor;
import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.file.FileDataField;
import org.nightlabs.jfire.prop.file.FileStructField;
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

	private Shell shell;
	private LanguageCf language;

	private Button openButton;
	private Button saveToDiskButton;
	private Text filenameTextbox;
	private Button openFileChooserButton;
	private Button clearButton;
	private Group group;
	private Label sizeLabel;
	private String fileDialogFilterPath;

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

	private static Set<File> dirsToDelete = new HashSet<File>();

	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				synchronized(dirsToDelete) {
					for (File dir : dirsToDelete)
						IOUtil.deleteDirectoryRecursively(dir);
				}
			}
		});
	}

	private boolean determineOpenButtonEnabled()
	{
		boolean result = true;
		if (!isChanged() && getDataField().isEmpty())
			result = false;

		if (isChanged() && filenameTextbox.getText().isEmpty())
			result = false;

		if (openButton.isEnabled() != result)
			openButton.setEnabled(result);

		return result;
	}

	private boolean determineSaveToDiskButtonEnabled()
	{
		boolean result = true;
		if (!isChanged() && getDataField().isEmpty())
			result = false;

		if (isChanged() && filenameTextbox.getText().isEmpty())
			result = false;

		if (saveToDiskButton.isEnabled() != result)
			saveToDiskButton.setEnabled(result);

		return result;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditor#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createControl(final Composite parent) {
		shell = parent.getShell();
		group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(6, false));

		XComposite.setLayoutDataMode(LayoutDataMode.GRID_DATA, group);

		openButton = new Button(group, SWT.PUSH);
		openButton.setEnabled(false);
//		openButton.setText("Open");
		openButton.setImage(SharedImages.getSharedImage(Activator.getDefault(), FileDataFieldEditor.class, "openButton"));
		openButton.setToolTipText("Open with the file with the default editor or application.");
		openButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openButtonPressed();
			}
		});


		saveToDiskButton = new Button(group, SWT.PUSH);
		saveToDiskButton.setEnabled(false);
		saveToDiskButton.setImage(SharedImages.getSharedImage(Activator.getDefault(), FileDataFieldEditor.class, "saveToDiskButton"));
		saveToDiskButton.setToolTipText("Save the file locally to a medium of the local computer.");
		saveToDiskButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveToDiskButtonPressed();
			}
		});


		filenameTextbox = new Text(group, XComposite.getBorderStyle(parent));
		filenameTextbox.setEditable(false);
		filenameTextbox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		openFileChooserButton = new Button(group, SWT.PUSH);
//		openFileChooserButton.setText("...");
		openFileChooserButton.setImage(SharedImages.getSharedImage(Activator.getDefault(), FileDataFieldEditor.class, "openFileChooserButton"));
		openFileChooserButton.setToolTipText("Browse for a file and load it into this field.");
		openFileChooserButton.setLayoutData(new GridData());
		openFileChooserButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fileChooserButtonPressed();
			}
		});

		clearButton = new Button(group, SWT.PUSH);
		clearButton.setImage(SharedImages.getSharedImage(Activator.getDefault(), FileDataFieldEditor.class, "clearButton"));
//		clearButton.setText("&Clear");
		clearButton.setToolTipText("Clear the field, i.e. delete the data.");
		clearButton.setLayoutData(new GridData());
		clearButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearButtonPressed();
			}
		});
		clearButton.setEnabled(false);

		sizeLabel = new Label(group, SWT.NONE);

		return group;
	}

	private void openButtonPressed()
	{
		if (!determineOpenButtonEnabled())
			return;

		try {
			String fileName = null;
			File _tmpDir = null;
			File _file;
			if (isChanged()) {
				fileName = filenameTextbox.getText();
				_file = new File(fileName);
			}
			else {
				_tmpDir = new File(IOUtil.getTempDir(), Long.toString(System.currentTimeMillis(), 36) + ".jfire");
				synchronized(dirsToDelete) {
					dirsToDelete.add(_tmpDir);
				}
				_tmpDir.mkdir();
				_file = getDataField().saveToDir(_tmpDir);
			}
			final File tmpDir = _tmpDir;
			final File file = _file;

			if (!file.exists()) {
				showFileDoesNotExistDialog(file);
				return;
			}

			IEditorDescriptor defaultEditor = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getAbsolutePath());
			if (defaultEditor != null) {
				final IEditorPart editor = RCPUtil.openEditor(
						new FileEditorInput(file),
						defaultEditor.getId()
				);
				if (editor != null && tmpDir != null) {
					final IWorkbenchPage activeWorkbenchPage = RCPUtil.getActiveWorkbenchPage();
					activeWorkbenchPage.addPartListener(new PartAdapter() {
						@Override
						public void partClosed(IWorkbenchPartReference reference) {
							IWorkbenchPart part = reference.getPart(false);
							if (editor == part) {
								activeWorkbenchPage.removePartListener(this);
								if (IOUtil.deleteDirectoryRecursively(tmpDir)) {
									synchronized(dirsToDelete) {
										dirsToDelete.remove(tmpDir);
									}
								}
							}
						}
					});
				}
			}
			else {
				if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN))
					Desktop.getDesktop().open(file);
				else
					MessageDialog.openError(shell, "Cannot open!", "No default editor registered and Java-Desktop-API not supported!");
			}
		} catch (RuntimeException x) {
			throw x;
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}

	private void showFileDoesNotExistDialog(File file)
	{
		MessageDialog.openError(
				shell,
				"File does not exist!",
				String.format("The file \"%s\" does not exist! Cannot open non-existing file!", file.getAbsolutePath())
		);
	}

	private void saveToDiskButtonPressed()
	{
		if (!determineSaveToDiskButtonEnabled())
			return;

		File localFile = null;
		String fileName;
		if (isChanged()) {
			localFile = new File(filenameTextbox.getText());
			if (!localFile.exists()) {
				showFileDoesNotExistDialog(localFile);
				return;
			}

			fileName = localFile.getName();
		}
		else
			fileName = getDataField().getFileName();

		FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
		fileDialog.setFileName(fileName);
		String saveFilePath = fileDialog.open();
		if (saveFilePath == null)
			return; // User cancelled.

		File saveFile = new File(saveFilePath);

		try {
			if (localFile != null)
				IOUtil.copyFile(localFile, saveFile);
			else
				getDataField().saveToFile(saveFile);
		} catch (IOException x) {
			throw new RuntimeException(x);
		}
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
		determineOpenButtonEnabled();
		determineSaveToDiskButtonEnabled();
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
				determineOpenButtonEnabled();
				determineSaveToDiskButtonEnabled();
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
		determineOpenButtonEnabled();
		determineSaveToDiskButtonEnabled();
	}
}


