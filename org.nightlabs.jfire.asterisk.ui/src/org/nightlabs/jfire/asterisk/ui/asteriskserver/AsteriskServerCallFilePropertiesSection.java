package org.nightlabs.jfire.asterisk.ui.asteriskserver;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.asterisk.AsteriskServer;
import org.nightlabs.jfire.asterisk.ui.AddCallFilePropertyDialog;
import org.nightlabs.jfire.asterisk.ui.resource.Messages;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 * @author Marco หงุ่ยตระกูล-Schulze - marco at nightlabs dot de
 */
public class AsteriskServerCallFilePropertiesSection
extends ToolBarSectionPart
{
	private CallFilePropertyTable callFilePropertyTable;

	private Action addAction = new Action(Messages.getString("org.nightlabs.jfire.asterisk.ui.asteriskserver.AsteriskServerCallFilePropertiesSection.AddAction.text")) { //$NON-NLS-1$
		{
			setImageDescriptor(SharedImages.ADD_16x16);
			setToolTipText(Messages.getString("org.nightlabs.jfire.asterisk.ui.asteriskserver.AsteriskServerCallFilePropertiesSection.AddAction.toolTipText")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			AddCallFilePropertyDialog addDialog = new AddCallFilePropertyDialog(getSection().getShell());
			int returnCode = addDialog.open();
			if (returnCode == Dialog.OK) {
				asteriskServer.setCallFileProperty(addDialog.getKey(), addDialog.getValue());
				callFilePropertyTable.refresh();
				markDirty();
			}
		}
	};

	private Action removeAction = new Action(Messages.getString("org.nightlabs.jfire.asterisk.ui.asteriskserver.AsteriskServerCallFilePropertiesSection.RemoveAction.text")) { //$NON-NLS-1$
		{
			setImageDescriptor(SharedImages.DELETE_16x16);
			setToolTipText(Messages.getString("org.nightlabs.jfire.asterisk.ui.asteriskserver.AsteriskServerCallFilePropertiesSection.RemoveAction.toolTipText")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			Set<String> keysToDelete = new HashSet<String>();
			for (Map.Entry<String, String> me : callFilePropertyTable.getSelectedElements())
				keysToDelete.add(me.getKey());

			if (keysToDelete.isEmpty())
				return; // => prevent markDirty()

			for (String key : keysToDelete)
				asteriskServer.setCallFileProperty(key, null);

			callFilePropertyTable.refresh();
			markDirty();
		}
	};

	public AsteriskServerCallFilePropertiesSection(FormPage page, Composite parent) {
		super(
				page, parent,
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR,
				Messages.getString("org.nightlabs.jfire.asterisk.ui.asteriskserver.AsteriskServerCallFilePropertiesSection.title") //$NON-NLS-1$
		);
		createClient(getSection(), page.getEditor().getToolkit());
	}

	private void createClient(Section section, FormToolkit toolkit) {
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite container = getContainer();

		//Property Table
		callFilePropertyTable = new CallFilePropertyTable(container);
		callFilePropertyTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		callFilePropertyTable.addCallFilePropertyModifyListener(new CallFilePropertyModifyListener() {
			@Override
			public void modifyValue(CallFilePropertyModifyEvent event) {
				markDirty();
			}
		});
		callFilePropertyTable.addContextMenuContribution(addAction);
		callFilePropertyTable.addContextMenuContribution(removeAction);

		registerAction(addAction);
		registerAction(removeAction);
		updateToolBarManager();

// @Yo: Section-actions look IMHO better than buttons.
//		//Buttons
//		XComposite buttonComposite = new XComposite(container, SWT.NONE);
//		buttonComposite.getGridLayout().makeColumnsEqualWidth = true;

// @Yo: Why is your add operation so complicated? Take a look at mine above (in the action) ;-)
//		Button addButton = new Button(buttonComposite, SWT.PUSH);
//		addButton.setImage(SharedImages.ADD_16x16.createImage());
//		addButton.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				AddCallFilePropertyDialog addDialog = new AddCallFilePropertyDialog(getSection().getShell());
//				int returnCode = addDialog.open();
//				if (returnCode == Dialog.OK) {
//					Map newMap = new LinkedHashMap<String, String>();
//					newMap.put(addDialog.getKeyString(), "Value");
//
//					Map.Entry<String, String> newEntry = (Map.Entry<String, String>)newMap.entrySet().iterator().next();
//					callFilePropertyTable.addElement(newEntry);
//
//					List<Map.Entry<String, String>> sel = new ArrayList<Map.Entry<String, String>>(1);
//					sel.add(newEntry);
//
//					callFilePropertyTable.getTableViewer().editElement(newEntry, 1);
//				}
//			}
//		});
//
//		Button removeButton = new Button(buttonComposite, SWT.PUSH);
//		removeButton.setImage(SharedImages.DELETE_16x16.createImage());
//		removeButton.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//			}
//		});
	}

	@Override
	public boolean setFormInput(Object input) {
		this.asteriskServer = (AsteriskServer) input;
		callFilePropertyTable.setInput(asteriskServer);
		return super.setFormInput(input);
	}

	@Override
	public void refresh() {
		callFilePropertyTable.refresh(); // This is only relevant, if we have multiple pages and another page modified the data which is displayed in the table.
		super.refresh();
	}

	@Override
	public void commit(boolean onSave) {
		// We don't need to copy anything from the UI into the data model, because this happened already before.
		super.commit(onSave);
	}

	private AsteriskServer asteriskServer;
}