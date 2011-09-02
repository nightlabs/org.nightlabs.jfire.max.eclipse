package org.nightlabs.jfire.auth.ui.ldap.editor;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.statushandlers.StatusManager;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.entity.editor.EntityEditorUtil;
import org.nightlabs.base.ui.form.NightlabsFormsToolkit;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.auth.ui.ldap.LdapUIPlugin;
import org.nightlabs.jfire.auth.ui.ldap.editor.LDAPScriptSetHelper.NamedScript;
import org.nightlabs.jfire.auth.ui.ldap.resource.Messages;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPScriptSet;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPScriptSetDAO;
import org.nightlabs.jfire.base.security.integration.ldap.LDAPServer;
import org.nightlabs.jfire.base.security.integration.ldap.scripts.ILDAPScriptProvider;
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
	
	Map<String, NamedScript> namedScriptsLocal;
	
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
		super(page, parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE, Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerScriptSetSection.sectionTitle")); //$NON-NLS-1$
		createContents(getSection(), page.getEditor().getToolkit());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean setFormInput(Object input) {
		if (!(input instanceof LDAPScriptSet)){
			throw new IllegalArgumentException(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerScriptSetSection.illegalFormInputExceptionText")); //$NON-NLS-1$
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
				&& namedScriptsLocal != null
				&& !namedScriptsLocal.isEmpty()){
			if (scriptsTabFolder != null && !scriptsTabFolder.isDisposed()){
				commitScriptTab(scriptsTabFolder.getSelection());
			}
			ldapScriptSetModel.commitScriptContent(namedScriptsLocal);
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
	 * Sets active tab (or tab index if model is not yet loaded) in a tab folder based on given script ID. 
	 * This script ID should be one of the constants defined in {@link ILDAPScriptProvider}.
	 * 
	 * @param scriptID
	 */
	public void setActiveScriptTab(String scriptID){
		if (scriptID == null
				|| scriptID.isEmpty()
				|| scriptsTabFolder == null
				|| scriptsTabFolder.isDisposed()){
			return;
		}
		if (scriptsTabFolder.getItemCount() == 0){
			selectItemWhenLoaded = LDAPScriptSetHelper.getScriptIndexByID(scriptID);
		}else{
			for (CTabItem tabItem : scriptsTabFolder.getItems()){
				NamedScript namedScript = (NamedScript) tabItem.getData();
				if (scriptID.equals(namedScript.getScriptID())){
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

		Composite parent = EntityEditorUtil.createCompositeClient(toolkit, section, 2);
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
		descriptionExpandable.setText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerScriptSetSection.showDescriptionExpanableLabel")); //$NON-NLS-1$

		scriptDescriptionLabel = toolkit.createLabel(descriptionExpandable, "", SWT.WRAP); //$NON-NLS-1$

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
					descriptionExpandable.setText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerScriptSetSection.hideDescriptionExpanableLabel")); //$NON-NLS-1$
				}else{
					descriptionExpandable.setText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerScriptSetSection.showDescriptionExpanableLabel")); //$NON-NLS-1$
				}
				descriptionExpandable.setLayoutData(gd);
				((Control) e.getSource()).getParent().layout();
			}
		});
		
		
		Button resetScriptButton = toolkit.createButton(parent, Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerScriptSetSection.resetScriptButtonText"), SWT.NONE); //$NON-NLS-1$
		resetScriptButton.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerScriptSetSection.resetScriptButtonTooltip")); //$NON-NLS-1$
		resetScriptButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		resetScriptButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (MessageDialog.openConfirm(RCPUtil.getActiveShell(), 
						Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerScriptSetSection.resetScriptConfirmTitle"),  //$NON-NLS-1$
						Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerScriptSetSection.resetScriptConfirmMessage"))){ //$NON-NLS-1$
					
					CTabItem selectedItem = scriptsTabFolder.getSelection();
					final String scriptID = ((NamedScript) selectedItem.getData()).getScriptID();
					
					ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(RCPUtil.getActiveShell());
					try {
						progressDialog.run(true, false, new IRunnableWithProgress() {
							@Override
							public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
								final String inititalContent = LDAPScriptSetDAO.sharedInstance().getInitialScriptContent(
										ldapScriptSetModel.getLDAPScriptSetID(), scriptID, new ProgressMonitorWrapper(monitor));
								Display.getDefault().asyncExec(new Runnable() {
									@Override
									public void run() {
										if (inititalContent != null){
											jsEditorComposite.setDocumentText(inititalContent);
											markDirty();
										}else{
											StatusManager.getManager().handle(
													new Status(
															Status.ERROR, LdapUIPlugin.PLUGIN_ID, 
															Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerScriptSetSection.scriptContentisNullErrorMessage")), //$NON-NLS-1$
															StatusManager.SHOW);
										}
									}
								});
							}
						});
					} catch (Exception e) {
						StatusManager.getManager().handle(
								new Status(
										Status.ERROR, LdapUIPlugin.PLUGIN_ID, 
										Messages.getString("org.nightlabs.jfire.auth.ui.ldap.editor.LDAPServerScriptSetSection.exceptionGettingScriptMessage"), e), //$NON-NLS-1$
										StatusManager.SHOW);
					}
				}
			}
		});
		
		
		scriptsTabFolder = new CTabFolder(parent, SWT.TOP);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		scriptsTabFolder.setLayoutData(gd);
		scriptsTabFolder.addSelectionListener(scriptSelectionListener);

		jsEditorComposite = new JSEditorComposite(scriptsTabFolder);
		jsEditorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		jsEditorComposite.setToolkit(new NightlabsFormsToolkit(toolkit.getColors()));
		jsEditorComposite.adaptToToolkit();
		jsEditorComposite.addKeyListener(scriptDirtyKeyListener);
		
	}
	
	private void initScriptTabs(){
		int i = 0;
		boolean itemsExist = scriptsTabFolder.getItemCount() > 0;
		namedScriptsLocal = new LinkedHashMap<String, LDAPScriptSetHelper.NamedScript>();
		for (NamedScript namedScript : LDAPScriptSetHelper.getNamedScripts()){
			NamedScript localNamedScript = namedScript.clone();
			String scriptIDLocal = localNamedScript.getScriptID();
			localNamedScript.setScriptContent(new String(ldapScriptSetModel.getScriptContentById(scriptIDLocal)));
			
			CTabItem tabItem = null;
			if (itemsExist){
				tabItem = scriptsTabFolder.getItem(i);
			}else{
				tabItem = new CTabItem(scriptsTabFolder, SWT.NONE);
			}
			tabItem.setText(localNamedScript.getScriptName());
			tabItem.setData(localNamedScript);
			i++;
			
			namedScriptsLocal.put(scriptIDLocal, localNamedScript);
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
