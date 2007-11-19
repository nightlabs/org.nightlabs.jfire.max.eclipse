/* *****************************************************************************
 * org.nightlabs.base.ui - NightLabs Eclipse utilities                            *
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
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.issuetracking.ui.issue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.util.RCPUtil;

/**
 * @author Chairat Kongarayawetchakun <chairat[AT]nightlabs[DOT]de>
 *
 */
public class FileListSelectionComposite 
extends XComposite 
{
	private Map<String, File> fileMap = new HashMap<String, File>();
	private org.eclipse.swt.widgets.List fileListWidget;
	
	public FileListSelectionComposite(Composite parent, int compositeStyle, LayoutMode layoutMode) 
	{
		super(parent, compositeStyle, layoutMode);
		createContents();
	}	

	private void createContents() 
	{
		XComposite fileListComposite = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		fileListComposite.getGridLayout().numColumns = 2;
		
		fileListWidget = new org.eclipse.swt.widgets.List(fileListComposite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		fileListWidget.setLayoutData(gridData);
		
		XComposite buttonComposite = new  XComposite(fileListComposite, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		Button addButton = new Button(buttonComposite, SWT.PUSH);
		addButton.setText("Add");
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog fileDialog = new FileDialog(RCPUtil.getActiveWorkbenchShell());
				String selectedFile = fileDialog.open();
				if (selectedFile != null) {
					File file = getFile(selectedFile);
					fileMap.put(file.getName(), file);
					fileListWidget.add(file.getName());
				}
			}
		});
		
		Button removeButton = new Button(buttonComposite, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				fileMap.remove(fileListWidget.getItem(fileListWidget.getSelectionIndex()));
				fileListWidget.remove(fileListWidget.getSelectionIndex());
			}
		});
		
		buttonComposite.setLayoutData(new GridData());
		
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		fileListComposite.setLayoutData(gridData);
	}
	
	public List<File> getFileList() {
		Collection<File> c = fileMap.values();
		List<File> l = new ArrayList<File>(c);
		return l;
	}
	
	public File getFile(String fileText) {
		return new File(fileText);
	}
}
