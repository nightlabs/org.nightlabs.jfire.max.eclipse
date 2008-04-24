package org.nightlabs.jfire.issuetracking.ui.issue;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.nightlabs.base.ui.composite.ListComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.util.RCPUtil;

public class IssueFileAttachmentComposite 
extends XComposite 
{
	private ListComposite<FileDescriptor> fileDescriptorListComposite;

	private Map<FileDescriptor, String> fileDescriptorMap = new HashMap<FileDescriptor, String>();

	public IssueFileAttachmentComposite(Composite parent, int compositeStyle, LayoutMode layoutMode) {
		super(parent, compositeStyle, layoutMode);
		createContents();
	}

	private void createContents() {
		XComposite fileListComposite = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		fileListComposite.getGridLayout().numColumns = 2;

		fileDescriptorListComposite = new ListComposite<FileDescriptor>(fileListComposite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		fileDescriptorListComposite.setData(fileDescriptorMap.entrySet());
		fileDescriptorListComposite.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return fileDescriptorMap.get(((FileDescriptor)element));
			}
		});

		GridData gridData = new GridData(GridData.FILL_BOTH);
		fileListComposite.setLayoutData(gridData);

		XComposite buttonComposite = new  XComposite(fileListComposite, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		Button addButton = new Button(buttonComposite, SWT.PUSH);
		addButton.setText("Add");
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event)
			{
				FileDialog fileDialog = new FileDialog(RCPUtil.getActiveShell(), SWT.OPEN);
				String selectedFile = fileDialog.open();
				if (selectedFile != null) {
					File file = new File(selectedFile);
					FileInputStream fis = null;
					try {
						try {
							fis = new FileInputStream(file);
							FileDescriptor fd = fis.getFD();
							fileDescriptorMap.put(fd, file.getName());
							
							fileDescriptorListComposite.removeAll();
							fileDescriptorListComposite.addElements(fileDescriptorMap.keySet());
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					} finally {
						try {
							fis.close();
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				}
			}
		});

		Button removeButton = new Button(buttonComposite, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				fileDescriptorMap.remove(fileDescriptorListComposite.getSelectedElement());
				
				fileDescriptorListComposite.removeAll();
				fileDescriptorListComposite.addElements(fileDescriptorMap.keySet());
			}
		});
		buttonComposite.setLayoutData(new GridData());

		gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		fileListComposite.setLayoutData(gridData);
	}
	
//	public List<FileInputStream> getFileInputStreamList() {
//		Collection<FileInputStream> c = CollectionUtil.castCollection(fileInputStreamMap.values());
//		List<FileInputStream> l = new ArrayList<FileInputStream>(c);
//		return l;
//	}
//
//	public File getFile(String fileText) {
//		return new File(fileText);
//	}
//
//	public Map<String, InputStream> getInputStreamMap() {
//		return fileInputStreamMap;
//	}
//	
//    public void saveFile(InputStream io, String fileName) throws IOException {
//        FileOutputStream fos = new FileOutputStream(fileName);
//        byte[] buf = new byte[256];
//        int read = 0;
//        while ((read = io.read(buf)) > 0) {
//            fos.write(buf, 0, read);
//        }
//    }
//    
//    public org.eclipse.swt.widgets.List getFileListWidget() {
//		return fileListWidget;
//	}
}
