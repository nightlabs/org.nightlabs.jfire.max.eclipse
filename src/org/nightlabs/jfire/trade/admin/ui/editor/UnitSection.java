package org.nightlabs.jfire.trade.admin.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;

public class UnitSection 
extends ToolBarSectionPart
{
	private Text idText;
	private I18nTextEditor nameText;
	private I18nTextEditor symbolText;
	
	private UnitEditorPageController controller;
	
	public UnitSection(IFormPage page, Composite parent, UnitEditorPageController controller ) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE, "Unit");
		this.controller = controller;
	
		getSection().setExpanded(true);
		createClient(getSection(), page.getEditor().getToolkit());
	}
	
	private XComposite client;
	protected void createClient(Section section, FormToolkit toolkit) {
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1;
		
		new Label(client, SWT.NONE).setText("Unit ID: ");
		idText = new Text(client, SWT.BORDER);
		idText.setEditable(false);
		idText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		idText.setText(controller.getControllerObject().getUnitID());
		
		new Label(client, SWT.NONE).setText("Name: ");
		nameText = new I18nTextEditor(client);
		nameText.setI18nText(controller.getControllerObject().getName(), EditMode.DIRECT);
		nameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent m) {
				markDirty();
			}
		});
		
		new Label(client, SWT.NONE).setText("Symbol: ");
		symbolText = new I18nTextEditor(client);
		symbolText.setI18nText(controller.getControllerObject().getSymbol(), EditMode.DIRECT);
		symbolText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent m) {
				markDirty();
			}
		});
		
		getSection().setClient(client);
	}
	
	public UnitEditorPageController getController() {
		return controller;
	}
}