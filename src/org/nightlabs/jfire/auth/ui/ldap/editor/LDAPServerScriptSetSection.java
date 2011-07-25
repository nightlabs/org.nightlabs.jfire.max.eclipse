package org.nightlabs.jfire.auth.ui.ldap.editor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.entity.editor.EntityEditorUtil;
import org.nightlabs.base.ui.form.NightlabsFormsToolkit;
import org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerScriptSetModel.NamedScript;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPScriptSet;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPServer;
import org.nightlabs.jseditor.ui.rcp.editor.JSEditorComposite;

/**
 * Section of {@link LDAPServerEditorScriptSetPage} for editing scripts in {@link LDAPServer}'s {@link LDAPScriptSet}.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class LDAPServerScriptSetSection extends ToolBarSectionPart {
	
	private LDAPServerScriptSetModel ldapScriptSetModel;

	private Label scriptDescriptionLabel;
	private CTabItem prevSelectedItem;
	private CTabFolder scriptsTabFolder;
	
	private JSEditorComposite jsEditorComposite;
	
	private Map<String, NamedScript> namedScripts;
	
	/**
	 * Set to <code>true</code> while automatic refreshing of UI elements
	 * happens. Some listeners are enabled at this time.
	 */
	private boolean refreshing = false;
	
	private ScriptKeyListener scriptDirtyKeyListener = new ScriptKeyListener();
	
	/**
	 * When {@link LDAPScriptSet} is loaded and model is created we set initial selection to tab folder based on this index. 
	 */
	private int selectItemWhenLoaded = 0;

	
	public LDAPServerScriptSetSection(IFormPage page, Composite parent) {
		super(page, parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE, "Interaction scripts");
		createContents(getSection(), page.getEditor().getToolkit());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean setFormInput(Object input) {
		if (!(input instanceof LDAPScriptSet)){
			throw new IllegalArgumentException("Input must be a LDAPScriptSet object!");
		}
		this.ldapScriptSetModel = new LDAPServerScriptSetModel((LDAPScriptSet) input);
		return super.setFormInput(input);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void commit(boolean onSave) {
		if (ldapScriptSetModel != null 
				&& namedScripts != null
				&& !namedScripts.isEmpty()){
			
			if (scriptsTabFolder != null && !scriptsTabFolder.isDisposed()){
				commitScriptTab(scriptsTabFolder.getSelection());
			}
			ldapScriptSetModel.commitScriptContent(namedScripts);
		}
		super.commit(onSave);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh() {
		refreshing = true;
		try{
			if (ldapScriptSetModel != null){
				
				initScriptTabs();
				
				if (scriptsTabFolder.getSelection() == null){
					scriptsTabFolder.setSelection(selectItemWhenLoaded);
					Event e = new Event();
					e.widget = scriptsTabFolder.getSelection();
					SelectionEvent selectionEvent = new SelectionEvent(e);
					scriptSelectionListener.widgetSelected(selectionEvent);
				}
			}
		}finally{
			refreshing = false;
		}
		super.refresh();
	}
	
	/**
	 * Sets active tab (or tab index if model is not yet loaded) in a tab folder based on given script name. 
	 * This script name should be one of the constants defined in {@link LDAPScriptSetHelper}.
	 * 
	 * @param scriptName
	 */
	public void setActiveScriptTab(String scriptName){
		if (scriptName == null
				|| scriptName.isEmpty()
				|| scriptsTabFolder == null
				|| scriptsTabFolder.isDisposed()){
			return;
		}
		if (scriptsTabFolder.getItemCount() == 0){
			selectItemWhenLoaded = LDAPScriptSetHelper.getScriptNameIndex(scriptName);
		}else{
			for (CTabItem tabItem : scriptsTabFolder.getItems()){
				NamedScript namedScript = (NamedScript) tabItem.getData();
				if (scriptName.equals(namedScript.getScriptName())){
					scriptsTabFolder.setSelection(tabItem);
					Event e = new Event();
					e.widget = tabItem;
					SelectionEvent selectionEvent = new SelectionEvent(e);
					scriptSelectionListener.widgetSelected(selectionEvent);
				}
			}
		}
	}

	private void createContents(Section section, FormToolkit toolkit){
		
		section.setExpanded(true);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite parent = EntityEditorUtil.createCompositeClient(toolkit, section, 1);
		GridLayout gLayout = (GridLayout) parent.getLayout();
		gLayout.verticalSpacing = 10;
		
		
		final ExpandableComposite descriptionExpandable = toolkit.createExpandableComposite(
				parent, ExpandableComposite.CLIENT_INDENT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
				);
		gLayout = new GridLayout(1, false);
		gLayout.marginWidth = 0;
		gLayout.marginHeight = 0;
		descriptionExpandable.setLayout(gLayout);
		GridData gd = new GridData();
		gd.verticalIndent = 15;
		gd.widthHint = 800;
		descriptionExpandable.setLayoutData(gd);

		descriptionExpandable.setExpanded(false);
		descriptionExpandable.setText("Show description and tips");

		scriptDescriptionLabel = toolkit.createLabel(descriptionExpandable, "", SWT.WRAP);

		descriptionExpandable.setClient(scriptDescriptionLabel);
		descriptionExpandable.addExpansionListener(new ExpansionAdapter(){
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				GridData gd = new GridData();
				gd.verticalIndent = 15;
				gd.widthHint = 800;
				if (e.getState()){
					gd.minimumHeight = 90;
					gd.heightHint = 90;
					descriptionExpandable.setText("Hide description and tips");
				}else{
					descriptionExpandable.setText("Show description and tips");
				}
				descriptionExpandable.setLayoutData(gd);
				((Control) e.getSource()).getParent().layout();
			}
		});
		
		
		scriptsTabFolder = new CTabFolder(parent, SWT.TOP);
		scriptsTabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		scriptsTabFolder.addSelectionListener(scriptSelectionListener);

		jsEditorComposite = new JSEditorComposite(scriptsTabFolder);
		jsEditorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		jsEditorComposite.setToolkit(new NightlabsFormsToolkit(toolkit.getColors()));
		jsEditorComposite.adaptToToolkit();
		jsEditorComposite.addKeyListener(scriptDirtyKeyListener);
		
	}
	
	private void initScriptTabs(){
		namedScripts = new HashMap<String, NamedScript>();
		int i = 0;
		boolean itemsExist = scriptsTabFolder.getItemCount() > 0;
		for (String scriptName : LDAPScriptSetHelper.getAllScriptNames()){
			NamedScript namedScript = new NamedScript(scriptName, new String(ldapScriptSetModel.getScriptContentByName(scriptName)));
			namedScript.setScriptDescription(LDAPScriptSetHelper.getScriptDescriptionByName(scriptName));
			
			CTabItem tabItem = null;
			if (itemsExist){
				tabItem = scriptsTabFolder.getItem(i);
			}else{
				tabItem = new CTabItem(scriptsTabFolder, SWT.NONE);
			}
			tabItem.setText(namedScript.getScriptName());
			tabItem.setData(namedScript);
			i++;
			
			namedScripts.put(scriptName, namedScript);
		}
	}

	private void commitScriptTab(CTabItem tabItem){
		NamedScript namedScript = (NamedScript) tabItem.getData();
		namedScript.setScriptContent(jsEditorComposite.getDocumentText());
	}
	
	private SelectionListener scriptSelectionListener = new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			
			if (prevSelectedItem != null){
				commitScriptTab(prevSelectedItem);
				prevSelectedItem.setControl(null);
			}
			
			CTabItem selectedItem = scriptsTabFolder.getSelection();
			NamedScript namedScript = (NamedScript) selectedItem.getData();
			jsEditorComposite.setDocumentText(namedScript.getScriptContent());
			scriptDirtyKeyListener.setInitialValue(namedScript.getScriptContent());
			
			scriptDescriptionLabel.setText(namedScript.getScriptDescription());
			
			selectedItem.setControl(jsEditorComposite);
			
			prevSelectedItem = selectedItem;
		};
	};

	class ScriptKeyListener extends KeyAdapter{
		private String previousValue;
		public void setInitialValue(String initialValue){
			this.previousValue = initialValue;
		}
		@Override
		public void keyPressed(KeyEvent keyevent) {
			if (!refreshing
					&& !jsEditorComposite.getDocumentText().equals(previousValue)){
				markDirty();
				previousValue = jsEditorComposite.getDocumentText();
			}
		}
	}
}
