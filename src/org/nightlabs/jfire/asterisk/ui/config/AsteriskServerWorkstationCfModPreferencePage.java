package org.nightlabs.jfire.asterisk.ui.config;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.asterisk.AsteriskServer;
import org.nightlabs.jfire.asterisk.config.AsteriskConfigModule;
import org.nightlabs.jfire.asterisk.ui.AddCallFilePropertyDialog;
import org.nightlabs.jfire.asterisk.ui.ContactAsteriskPlugin;
import org.nightlabs.jfire.asterisk.ui.resource.Messages;
import org.nightlabs.jfire.base.ui.config.AbstractWorkstationConfigModulePreferencePage;
import org.nightlabs.jfire.base.ui.config.IConfigModuleController;
import org.nightlabs.jfire.config.id.ConfigID;
import org.nightlabs.jfire.pbx.PhoneSystem;
import org.nightlabs.jfire.pbx.dao.PhoneSystemDAO;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 * @author marco หงุ่ยตระกูล-Schulze - marco at nightlabs dot de
 */
public class AsteriskServerWorkstationCfModPreferencePage
extends AbstractWorkstationConfigModulePreferencePage
{
	private Shell shell;
	private Display display;

	private Action addAction = new Action(Messages.getString("org.nightlabs.jfire.asterisk.ui.config.AsteriskServerWorkstationCfModPreferencePage.AddAction.text")) { //$NON-NLS-1$
		{
			setImageDescriptor(SharedImages.ADD_16x16);
			setToolTipText(Messages.getString("org.nightlabs.jfire.asterisk.ui.config.AsteriskServerWorkstationCfModPreferencePage.AddAction.toolTipText")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			AddCallFilePropertyDialog addDialog = new AddCallFilePropertyDialog(shell);
			int returnCode = addDialog.open();
			if (returnCode == Dialog.OK) {
				String key = addDialog.getKey();
				String value = addDialog.getValue();
				if ("".equals(value)) //$NON-NLS-1$
					value = null;

				AsteriskConfigModule cfMod = getConfigModule();
				cfMod.addOverrideCallFilePropertyKey(key);
				cfMod.setCallFileProperty(key, value);
				callFilePropertyCfModTable.refresh();
				getPageDirtyStateManager().markDirty();
			}
		}
	};

	private PhoneSystem phoneSystem;
	private Text phoneSystemNameText;
	private Button reloadButton;
	private CallFilePropertyCfModTable callFilePropertyCfModTable;

	@Override
	protected IConfigModuleController createConfigModuleController() {
		return new AsteriskServerConfigModuleController(this);
	}

	private XComposite editArea;
	private StackLayout editAreaLayout;
	private Label unsupportedPhoneSystemLabel;

	@Override
	protected void createPreferencePage(Composite parent) {
		shell = getShell();
		display = shell.getDisplay();

		XComposite asteriskServerComp = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL);
		asteriskServerComp.getGridLayout().numColumns = 3;
		Label phoneSystemNameLabel = new Label(asteriskServerComp, SWT.NONE);
		phoneSystemNameLabel.setText(Messages.getString("org.nightlabs.jfire.asterisk.ui.config.AsteriskServerWorkstationCfModPreferencePage.phoneSystemNameLabel.text")); //$NON-NLS-1$
		phoneSystemNameLabel.setToolTipText(Messages.getString("org.nightlabs.jfire.asterisk.ui.config.AsteriskServerWorkstationCfModPreferencePage.phoneSystemNameLabel.toolTipText")); //$NON-NLS-1$
		phoneSystemNameText = new Text(asteriskServerComp, XComposite.getBorderStyle(parent) | SWT.READ_ONLY);
		phoneSystemNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		phoneSystemNameText.setToolTipText(phoneSystemNameLabel.getToolTipText());

		reloadButton = new Button(asteriskServerComp, SWT.PUSH);
		reloadButton.setToolTipText(Messages.getString("org.nightlabs.jfire.asterisk.ui.config.AsteriskServerWorkstationCfModPreferencePage.reloadButton.toolTipText")); //$NON-NLS-1$
		reloadButton.setImage(SharedImages.getSharedImage(ContactAsteriskPlugin.getDefault(), AsteriskServerWorkstationCfModPreferencePage.class, "reloadButton")); //$NON-NLS-1$

		editArea = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		editAreaLayout = new StackLayout();
		editArea.setLayout(editAreaLayout);
		callFilePropertyCfModTable = new CallFilePropertyCfModTable(editArea, getPageDirtyStateManager());
		callFilePropertyCfModTable.addContextMenuContribution(addAction);
		unsupportedPhoneSystemLabel = new Label(editArea, SWT.WRAP);

		phoneSystemNameText.setText(Messages.getString("org.nightlabs.jfire.asterisk.ui.config.AsteriskServerWorkstationCfModPreferencePage.phoneSystemNameText[loading].text")); //$NON-NLS-1$
		unsupportedPhoneSystemLabel.setText(phoneSystemNameText.getText());
		editAreaLayout.topControl = unsupportedPhoneSystemLabel;

		final Job loadAsteriskServerJob = new Job(Messages.getString("org.nightlabs.jfire.asterisk.ui.config.AsteriskServerWorkstationCfModPreferencePage.loadAsteriskServerJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				ConfigID configID = getConfigModuleController().getConfigID();

				final PhoneSystem ps = PhoneSystemDAO.sharedInstance().getPhoneSystem(
						configID,
						new String[] {
								FetchPlan.DEFAULT,
								PhoneSystem.FETCH_GROUP_NAME,
								AsteriskServer.FETCH_GROUP_CALL_FILE_PROPERTIES,
						},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						monitor
				);

				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						if (phoneSystemNameText.isDisposed())
							return;

						phoneSystem = ps;
						phoneSystemNameText.setText(
								String.format(Messages.getString("org.nightlabs.jfire.asterisk.ui.config.AsteriskServerWorkstationCfModPreferencePage.phoneSystemNameText.text"), phoneSystem.getName().getText(), phoneSystem.getClass().getName()) //$NON-NLS-1$
						);
						if (ps instanceof AsteriskServer) {
							editAreaLayout.topControl = callFilePropertyCfModTable;
							callFilePropertyCfModTable.setAsteriskServer((AsteriskServer) phoneSystem);
							editArea.layout();
						}
						else {
							editAreaLayout.topControl = unsupportedPhoneSystemLabel;
							unsupportedPhoneSystemLabel.setText(
									String.format(
											Messages.getString("org.nightlabs.jfire.asterisk.ui.config.AsteriskServerWorkstationCfModPreferencePage.unsupportedPhoneSystemLabel.text"), //$NON-NLS-1$
											phoneSystem.getName().getText(),
											phoneSystem.getClass().getName()
									)
							);
							callFilePropertyCfModTable.setAsteriskServer(null);
							editArea.layout();
						}
					}
				});
				return Status.OK_STATUS;
			}
		};
		loadAsteriskServerJob.setPriority(Job.INTERACTIVE);
		loadAsteriskServerJob.schedule();

		reloadButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				phoneSystemNameText.setText(Messages.getString("org.nightlabs.jfire.asterisk.ui.config.AsteriskServerWorkstationCfModPreferencePage.phoneSystemNameText[loading].text")); //$NON-NLS-1$
				unsupportedPhoneSystemLabel.setText(phoneSystemNameText.getText());
				editAreaLayout.topControl = unsupportedPhoneSystemLabel;

				loadAsteriskServerJob.schedule();
			}
		});
	}

	@Override
	public void updateConfigModule() {
//		AsteriskConfigModule configModule = getConfigModule();
	}

	private AsteriskConfigModule getConfigModule() {
		return (AsteriskConfigModule) getConfigModuleController().getConfigModule();
	}

	@Override
	protected void updatePreferencePage() {
		AsteriskConfigModule configModule = getConfigModule();
		callFilePropertyCfModTable.setConfigModule(configModule);
	}
}