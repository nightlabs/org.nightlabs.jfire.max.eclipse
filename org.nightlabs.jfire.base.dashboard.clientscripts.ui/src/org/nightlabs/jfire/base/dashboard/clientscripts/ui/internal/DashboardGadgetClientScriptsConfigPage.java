package org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jfire.base.dashboard.clientscripts.ui.resource.Messages;
import org.nightlabs.jfire.base.dashboard.ui.AbstractDashbardGadgetConfigPage;
import org.nightlabs.jfire.dashboard.DashboardGadgetClientScriptsConfig;
import org.nightlabs.jfire.dashboard.DashboardGadgetClientScriptsConfig.ClientScript;
import org.nightlabs.jfire.dashboard.DashboardGadgetLayoutEntry;

/**
 * 
 * @author Frederik Loeser <!-- frederik [AT] nightlabs [DOT] de -->
 */
public class DashboardGadgetClientScriptsConfigPage extends AbstractDashbardGadgetConfigPage<Object> {

	private I18nTextEditor gadgetTitle;
	
	private Button buttonConfirmProcessing;
	
	private List<ClientScript> clientScripts;
	
	private boolean confirmProcessing;
	
	private TableViewer tableViewer;
	
	private Button buttonRemove;
	
	private Button buttonEdit;
	
	private Button buttonMoveUp;
	
	private Button buttonMoveDown;
	
	private DashboardGadgetClientScriptsConfig config;
	
	public DashboardGadgetClientScriptsConfigPage() {
		super(DashboardGadgetClientScriptsConfigPage.class.getName());
		setTitle(Messages.getString("org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal.DashboardGadgetClientScriptsConfigPage.page.title")); //$NON-NLS-1$
	}
	
	private int amountOfColumns = 3;

	@Override
	public Control createPageContents(final Composite parent) {
		final XComposite wrapper = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, amountOfColumns);
		
		final Label labelDescription1 = new Label(wrapper, SWT.WRAP);
		labelDescription1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, amountOfColumns, 1));
		labelDescription1.setText(Messages.getString("org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal.DashboardGadgetClientScriptsConfigPage.gadget.description.text")); //$NON-NLS-1$
		
		final Label labelTitle = new Label(wrapper, SWT.NONE);
		labelTitle.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		labelTitle.setText(Messages.getString("org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal.DashboardGadgetClientScriptsConfigPage.gadget.title.text")); //$NON-NLS-1$

		gadgetTitle = new I18nTextEditor(wrapper);
		gadgetTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		gadgetTitle.setI18nText(!getLayoutEntry().getEntryName().isEmpty() ? getLayoutEntry().getEntryName() : createInitialName());
		
		createUpperWidgets(wrapper);
		createTableWidget(wrapper);
		createButtonWidgets(wrapper);
		
		tableViewer.setInput(clientScripts);
		
		return wrapper;
	}
	
	private void createUpperWidgets(final Composite parent) {
		GridData gd;
		
		buttonConfirmProcessing = new Button(parent, SWT.CHECK);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, amountOfColumns, 1);
		gd.verticalIndent = 10;
		buttonConfirmProcessing.setLayoutData(gd);
		buttonConfirmProcessing.setText(Messages.getString("org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal.DashboardGadgetClientScriptsConfigPage.buttonConfirmProcessing.text")); //$NON-NLS-1$
		buttonConfirmProcessing.setSelection(confirmProcessing);
		buttonConfirmProcessing.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				confirmProcessing = !confirmProcessing;
			}
		});
		
		final Label labelDescription2 = new Label(parent, SWT.WRAP);
		labelDescription2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, amountOfColumns, 1));
		labelDescription2.setText(Messages.getString("org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal.DashboardGadgetClientScriptsConfigPage.description.text")); //$NON-NLS-1$

	}
	
	private void createTableWidget(final Composite parent) {
		final Table table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.verticalIndent = 5;
		table.setLayoutData(gridData);
		
		table.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event event) {
				updateButtonStates();
			}
		});
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(final MouseEvent e) {
				if (table.getSelectionIndex() > -1)
					editClientScript();
			}
		});
		
		tableViewer = new TableViewer(table);
		
		attachContentProvider();
	    attachLabelProvider();
	}
	
	private void createButtonWidgets(final Composite parent_) {
		final Composite parent = new Composite(parent_, SWT.NONE);
		parent.setLayout(new GridLayout());
		GridData gd;
		
		final Button buttonNew = new Button(parent, SWT.PUSH);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		buttonNew.setLayoutData(gd);
		buttonNew.setText(Messages.getString("org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal.DashboardGadgetClientScriptsConfigPage.buttonNew.text")); //$NON-NLS-1$
		buttonNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				createClientScript();
				if (tableViewer.getTable().getSelectionIndex() > -1)
					updateButtonStates();
			}
		});
		
		buttonRemove = new Button(parent, SWT.PUSH);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		buttonRemove.setLayoutData(gd);
		buttonRemove.setEnabled(false);
		buttonRemove.setText(Messages.getString("org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal.DashboardGadgetClientScriptsConfigPage.buttonRemove.text")); //$NON-NLS-1$
		buttonRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				removeClientScript();
				updateButtonStates();
			}
		});
		
		buttonEdit = new Button(parent, SWT.PUSH);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		buttonEdit.setLayoutData(gd);
		buttonEdit.setEnabled(false);
		buttonEdit.setText(Messages.getString("org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal.DashboardGadgetClientScriptsConfigPage.buttonEdit.text")); //$NON-NLS-1$
		buttonEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				editClientScript();
			}
		});

		buttonMoveUp = new Button(parent, SWT.PUSH);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		buttonMoveUp.setLayoutData(gd);
		buttonMoveUp.setEnabled(false);
		buttonMoveUp.setText(Messages.getString("org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal.DashboardGadgetClientScriptsConfigPage.buttonUp.text")); //$NON-NLS-1$
		buttonMoveUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateItemOrder(true);
				updateButtonStates();
			}
		});

		buttonMoveDown = new Button(parent, SWT.PUSH);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		buttonMoveDown.setLayoutData(gd);
		buttonMoveDown.setEnabled(false);
		buttonMoveDown.setText(Messages.getString("org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal.DashboardGadgetClientScriptsConfigPage.buttonDown.text")); //$NON-NLS-1$
		buttonMoveDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateItemOrder(false);
				updateButtonStates();
			}
		});
	}
	
	static class ClientScriptPropertiesWrapper {
		
		private String clientScriptName;
		private String clientScriptContent;
		
		public ClientScriptPropertiesWrapper() {
		}
		
		public ClientScriptPropertiesWrapper(final String clientScriptName, final String clientScriptContent) {
			this.clientScriptName = clientScriptName;
			this.clientScriptContent = clientScriptContent;
		}

		public String getClientScriptName() {
			return clientScriptName;
		}
		public String getClientScriptContent() {
			return clientScriptContent;
		}
		public void setClientScriptName(final String clientScriptName) {
			this.clientScriptName = clientScriptName;
		}
		public void setClientScriptContent(final String clientScriptContent) {
			this.clientScriptContent = clientScriptContent;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void createClientScript() {
		final ClientScriptPropertiesWrapper data = new ClientScriptPropertiesWrapper();
		if (clientScripts == null)	// if config has not been called yet
			clientScripts = new ArrayList<ClientScript>();
		final DashboardGadgetClientScriptsNewEditDialog dialog = new DashboardGadgetClientScriptsNewEditDialog(getShell(), clientScripts, data);
		
		if (dialog.open() == Window.OK) {
			// Create new ClientScript instance and insert it into the table (not persisted yet)
			final ClientScript clientScript = new ClientScript(data.getClientScriptName(), data.getClientScriptContent());
			
			if (tableViewer.getInput() instanceof List<?>) {
				clientScripts = (List<ClientScript>) tableViewer.getInput();
			}
			
			clientScripts.add(clientScript);
			tableViewer.setInput(clientScripts);
		}
	}
	
	private void editClientScript() {
		final Object data_ = tableViewer.getTable().getItem(tableViewer.getTable().getSelectionIndex()).getData();
		
		if (data_ instanceof ClientScript) {
			final ClientScript clientScript = (ClientScript) data_;
			final ClientScriptPropertiesWrapper data = new ClientScriptPropertiesWrapper(clientScript.getName(), clientScript.getContent());
			final DashboardGadgetClientScriptsNewEditDialog dialog = new DashboardGadgetClientScriptsNewEditDialog(getShell(), clientScripts, data);
			
			if (dialog.open() == Window.OK)
				for (final ClientScript clientScript_ : clientScripts)
					if (clientScript_.getName().equals(clientScript.getName())) {
						clientScript_.setName(data.getClientScriptName());
						clientScript_.setContent(data.getClientScriptContent());
						tableViewer.setInput(clientScripts);	// perhaps update(...) instead of setInput(...)
						break;
					}
		}
	}
	
	private void removeClientScript() {
		final int amount = tableViewer.getTable().getSelectionCount();
		final int[] indices = tableViewer.getTable().getSelectionIndices();
		final ClientScript[] clientScriptsToRemove = new ClientScript[amount];
		
		for (int i = 0; i < amount; i++) {
			final Object data_ = tableViewer.getTable().getItem(indices[i]).getData();
			if (data_ instanceof ClientScript) {
				final ClientScript clientScript = (ClientScript) data_;
				clientScriptsToRemove[i] = clientScript;
			}
		}
		clientScripts.removeAll(Arrays.asList(clientScriptsToRemove));
		tableViewer.remove(clientScriptsToRemove);
	}
	
	private void updateItemOrder(final boolean up) {
		final int sourceIdx = tableViewer.getTable().getSelectionIndex();
		final int targetIdx = up ? sourceIdx - 1 : sourceIdx + 1;
		final Object dataSource = tableViewer.getTable().getItem(sourceIdx).getData();
		final Object dataTarget = tableViewer.getTable().getItem(targetIdx).getData();
		
		if (dataSource instanceof ClientScript && dataTarget instanceof ClientScript) {
			final ClientScript clientScriptSource = (ClientScript) dataSource;
			final ClientScript clientScriptTarget = (ClientScript) dataTarget;
			for (int i = 0; i < clientScripts.size(); i++) {
				final ClientScript clientScript_ = clientScripts.get(i);
				if (clientScript_.getName().equals(up ? clientScriptTarget.getName() : clientScriptSource.getName())) {
					final String nameTmp = clientScript_.getName();
					final String contentTmp = clientScript_.getContent();
					clientScript_.setName(up ? clientScriptSource.getName() : clientScriptTarget.getName());
					clientScript_.setContent(up ? clientScriptSource.getContent() : clientScriptTarget.getContent());
					clientScripts.get(i + 1).setName(nameTmp);
					clientScripts.get(i + 1).setContent(contentTmp);
					break;
				}
			}
			tableViewer.setInput(clientScripts);
			tableViewer.getTable().setSelection(targetIdx);
		}
	}
	
	private void updateButtonStates() {
		final int idx = tableViewer.getTable().getSelectionIndex();
		final int amount = tableViewer.getTable().getSelectionCount();
		final boolean singleItemSelected = amount == 1 ? true : false;
		buttonRemove.setEnabled(amount > 0);
		buttonEdit.setEnabled(singleItemSelected);
		buttonMoveUp.setEnabled(singleItemSelected && idx > 0);
		buttonMoveDown.setEnabled(singleItemSelected && idx < tableViewer.getTable().getItemCount() - 1);
	}
	
	private void attachContentProvider() {
		tableViewer.setContentProvider(new ArrayContentProvider());
	}
	
	private void attachLabelProvider() {
		tableViewer.setLabelProvider(new TableLabelProvider() {
			@Override
			public String getColumnText(final Object element, final int columnIndex) {
				if (element instanceof ClientScript)
					return ((ClientScript) element).getName();
				return null;
			}
		});
	}
	
	private I18nText createInitialName() {
		final I18nTextBuffer textBuffer = new I18nTextBuffer();
		initializeGadgetName(textBuffer, "clientScriptsGadget.title"); //$NON-NLS-1$
		return textBuffer;
	}
	
	private void initializeGadgetName(final I18nText gadgetName, final String nameKeySuffix) {
		gadgetName.readFromProperties(
			Messages.BUNDLE_NAME, 
			DashboardGadgetClientScriptsConfigPage.class.getClassLoader(), 
			DashboardGadgetClientScriptsConfigPage.class.getName() + "." + nameKeySuffix); //$NON-NLS-1$
	}

	@Override
	public void initialize(final DashboardGadgetLayoutEntry<?> layoutEntry) {
		super.initialize(layoutEntry);
		final Object config_ = getLayoutEntry().getConfig();
		if (config_ instanceof DashboardGadgetClientScriptsConfig) {
			config = (DashboardGadgetClientScriptsConfig) config_;
			clientScripts = config.getClientScripts();
			confirmProcessing = config.isConfirmProcessing();
		}
	}
	
	@Override
	public void configure(final DashboardGadgetLayoutEntry layoutEntry) {
		layoutEntry.getEntryName().copyFrom(gadgetTitle != null ? gadgetTitle.getI18nText() : createInitialName());
		
		final DashboardGadgetClientScriptsConfig config = new DashboardGadgetClientScriptsConfig();
		config.setClientScripts(clientScripts);
		config.setConfirmProcessing(confirmProcessing);
		
		layoutEntry.setConfig(config);
	}
}
