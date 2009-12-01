package org.nightlabs.jfire.asterisk.ui.asteriskserver;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.asterisk.AsteriskServer;
import org.nightlabs.jfire.asterisk.ui.AddCallFilePropertyDialog;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class CreateAsteriskServerCallFilePropertyWizardPage 
extends WizardHopPage
{
	private AsteriskServer asteriskServer;

	public CreateAsteriskServerCallFilePropertyWizardPage(AsteriskServer asteriskServer) {
		super(CreateAsteriskServerCallFilePropertyWizardPage.class.getName(), "Title");
		this.asteriskServer = asteriskServer;
	}

	private CallFilePropertyTable callFilePropertyTable;
	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		mainComposite.getGridLayout().numColumns = 2;

		callFilePropertyTable = new CallFilePropertyTable(mainComposite);
		callFilePropertyTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		XComposite buttonComposite = new  XComposite(mainComposite, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		Button addButton = new Button(buttonComposite, SWT.PUSH);
		addButton.setText("Add");
		addButton.setImage(SharedImages.ADD_16x16.createImage());
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event)
			{
				AddCallFilePropertyDialog addDialog = new AddCallFilePropertyDialog(getShell());
				int returnCode = addDialog.open();
				if (returnCode == Dialog.OK) {
					asteriskServer.setCallFileProperty(addDialog.getKey(), addDialog.getValue());
					callFilePropertyTable.refresh();
				}
			}
		});

		Button removeButton = new Button(buttonComposite, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.setImage(SharedImages.DELETE_16x16.createImage());
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Set<String> keysToDelete = new HashSet<String>();
				for (Map.Entry<String, String> me : callFilePropertyTable.getSelectedElements())
					keysToDelete.add(me.getKey());

				if (keysToDelete.isEmpty())
					return;

				for (String key : keysToDelete)
					asteriskServer.setCallFileProperty(key, null);

				callFilePropertyTable.refresh();
			}
		});

		buttonComposite.setLayoutData(new GridData());
		
		callFilePropertyTable.setInput(asteriskServer);
		
		return mainComposite;
	}

	@Override
	public boolean isPageComplete() {
		return getErrorMessage() == null;
	}
}