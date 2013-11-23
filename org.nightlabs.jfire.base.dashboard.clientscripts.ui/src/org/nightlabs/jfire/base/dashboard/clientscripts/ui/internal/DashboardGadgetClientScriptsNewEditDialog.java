package org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.jfire.base.dashboard.clientscripts.ui.resource.Messages;
import org.nightlabs.jfire.dashboard.DashboardGadgetClientScriptsConfig.ClientScript;

/**
 * 
 * @author Frederik Loeser <!-- frederik [AT] nightlabs [DOT] de -->
 */
public class DashboardGadgetClientScriptsNewEditDialog extends ResizableTitleAreaDialog {

	DashboardGadgetClientScriptsConfigPage.ClientScriptPropertiesWrapper data;
	List<String> clientScriptNames = new ArrayList<String>();
	boolean editScript;
	
	public DashboardGadgetClientScriptsNewEditDialog(final Shell shell, List<ClientScript> clientScripts,
		final DashboardGadgetClientScriptsConfigPage.ClientScriptPropertiesWrapper data) {
		
		super(shell, Messages.RESOURCE_BUNDLE);
		for (ClientScript script : clientScripts) 
			clientScriptNames.add(script.getName());
		if (data.getClientScriptName() != null) {		// if client script is to be edited, old name can still be set
			clientScriptNames.remove(data.getClientScriptName());
			editScript = true;
		}
		this.data = data;
	}
	
	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite parent_ = (Composite) super.createDialogArea(parent);
		setTitle(Messages.getString("org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal.DashboardGadgetClientScriptsNewEditDialog.createDialogArea.dialog.title")); //$NON-NLS-1$
		setMessage(Messages.getString("org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal.DashboardGadgetClientScriptsNewEditDialog.createDialogArea.dialog.message")); //$NON-NLS-1$
		
		final Composite content = new XComposite(parent_, SWT.NONE, LayoutMode.TIGHT_WRAPPER, 2);
//		final Timer[] timer = new Timer[2];
		GridData gd;
		
		final Label labelDescription1 = new Label(content, SWT.NONE);
		gd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd.verticalIndent = 10;
		labelDescription1.setLayoutData(gd);
		labelDescription1.setText(Messages.getString("org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal.DashboardGadgetClientScriptsNewEditDialog.createDialogArea.label1.name")); //$NON-NLS-1$

		final Text textClientScriptName = new Text(content, SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd.verticalIndent = 10;
		textClientScriptName.setLayoutData(gd);
		textClientScriptName.setText(data.getClientScriptName() != null ? data.getClientScriptName() : ""); //$NON-NLS-1$
		textClientScriptName.addListener(SWT.Verify, new Listener() {
			@Override
			public void handleEvent(final Event event) {
//				if(timer[0] != null)
//	                timer[0].cancel();
//				timer[0] = new Timer();
//				timer[0].schedule(new TimerTask() {		// TODO using timer leads to exception (NPE), but I cannot see any stack trace
//					@Override
//					public void run() {
						Display.getCurrent().asyncExec(new Runnable() {
							@Override
							public void run() {
								String newScriptName = event.text;
								if (clientScriptNames.contains(newScriptName)) {
									setErrorMessage(Messages.getString("org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal.DashboardGadgetClientScriptsNewEditDialog.createDialogArea.errorMessage.scriptExists")); //$NON-NLS-1$
									getButton(IDialogConstants.OK_ID).setEnabled(false);
								} else if (newScriptName.equals("")) { //$NON-NLS-1$
									setErrorMessage(Messages.getString("org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal.DashboardGadgetClientScriptsNewEditDialog.createDialogArea.errorMessage.emptyName")); //$NON-NLS-1$
									getButton(IDialogConstants.OK_ID).setEnabled(false);
								} else {
									setErrorMessage(null);
									getButton(IDialogConstants.OK_ID).setEnabled(true);
									data.setClientScriptName(newScriptName);
								}
//								timer[0].cancel();								
							}
						});
//					}
//				}, 1000);
			}
		});
		
		final Label labelDescription2 = new Label(content, SWT.NONE);
		gd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd.verticalIndent = 10;
		labelDescription2.setLayoutData(gd);
		labelDescription2.setText(Messages.getString("org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal.DashboardGadgetClientScriptsNewEditDialog.createDialogArea.label2.name")); //$NON-NLS-1$
		
		final Text textClientScriptContent = new Text(content, SWT.BORDER | SWT.WRAP);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		textClientScriptContent.setLayoutData(gd);
		textClientScriptContent.setText(data.getClientScriptContent() != null ? data.getClientScriptContent() : ""); //$NON-NLS-1$
		textClientScriptContent.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent event) {
//				if(timer[1] != null)
//	                timer[1].cancel();
//				timer[1] = new Timer();
//				timer[1].schedule(new TimerTask() {		// TODO using timer leads to exception (NPE), but I cannot see any stack trace
//					@Override
//					public void run() {
						Display.getCurrent().asyncExec(new Runnable() {
							@Override
							public void run() {
								if (event.getSource() instanceof Text)
									data.setClientScriptContent(((Text) event.getSource()).getText());
//								timer[1].cancel();
							}
						});
//					}
//				}, 1000);
			}
		});
		
		if (editScript)
			textClientScriptContent.setFocus();
		else
			textClientScriptName.setFocus();			
		
		return parent_;
	}
	
	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
    	Button button = super.createButton(parent, id, label, defaultButton);
    	if (!editScript && id == IDialogConstants.OK_ID) {
    		button.setEnabled(false);
    	}
    	return button;
	}
}
