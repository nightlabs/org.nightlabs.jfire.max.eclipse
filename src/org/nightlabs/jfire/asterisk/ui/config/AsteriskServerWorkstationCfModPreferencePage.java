package org.nightlabs.jfire.asterisk.ui.config;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
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
import org.nightlabs.jfire.base.ui.config.AbstractWorkstationConfigModulePreferencePage;
import org.nightlabs.jfire.base.ui.config.IConfigModuleController;
import org.nightlabs.jfire.config.id.ConfigID;
import org.nightlabs.jfire.pbx.PhoneSystem;
import org.nightlabs.jfire.pbx.dao.PhoneSystemDAO;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class AsteriskServerWorkstationCfModPreferencePage
extends AbstractWorkstationConfigModulePreferencePage
{
	private Shell shell;
	private Display display;

	private Action addAction = new Action("Add property...") {
		{
			setImageDescriptor(SharedImages.ADD_16x16);
			setToolTipText("Add a new property.");
		}

		@Override
		public void run() {
			AddCallFilePropertyDialog addDialog = new AddCallFilePropertyDialog(shell);
			int returnCode = addDialog.open();
			if (returnCode == Dialog.OK) {
				String key = addDialog.getKey();
				String value = addDialog.getValue();
				if ("".equals(value))
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

	@Override
	protected void createPreferencePage(Composite parent) {
		shell = getShell();
		display = shell.getDisplay();

		XComposite asteriskServerComp = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL);
		asteriskServerComp.getGridLayout().numColumns = 3;
		Label phoneSystemNameLabel = new Label(asteriskServerComp, SWT.NONE);
		phoneSystemNameLabel.setText("Phone system:");
		phoneSystemNameLabel.setToolTipText("");
		phoneSystemNameText = new Text(asteriskServerComp, XComposite.getBorderStyle(parent) | SWT.READ_ONLY);
		phoneSystemNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		phoneSystemNameText.setToolTipText(phoneSystemNameLabel.getToolTipText());

		reloadButton = new Button(asteriskServerComp, SWT.PUSH);
		reloadButton.setToolTipText("Reload the currently assigned phone system.");
		reloadButton.setImage(SharedImages.getSharedImage(ContactAsteriskPlugin.getDefault(), AsteriskServerWorkstationCfModPreferencePage.class, "reloadButton"));

		callFilePropertyCfModTable = new CallFilePropertyCfModTable(parent, getPageDirtyStateManager());
		callFilePropertyCfModTable.addContextMenuContribution(addAction);

		final Job loadAsteriskServersJob = new Job("Loading asterisk servers") {
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
								String.format("%s (%s)", phoneSystem.getName().getText(), phoneSystem.getClass().getName())
						);
						if (ps instanceof AsteriskServer) {
							setErrorMessage(null);
							callFilePropertyCfModTable.setAsteriskServer((AsteriskServer) phoneSystem);
						}
						else {
							setErrorMessage(
									String.format(
											"The phone system \"%s\" is not an asterisk server! It is an instance of class %s, which is not supported by this configuration page.",
											phoneSystem.getName().getText(),
											phoneSystem.getClass().getName()
									)
							);
							callFilePropertyCfModTable.setAsteriskServer(null);
						}
					}
				});
				return Status.OK_STATUS;
			}
		};
		loadAsteriskServersJob.setPriority(Job.INTERACTIVE);
		loadAsteriskServersJob.schedule();

		reloadButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadAsteriskServersJob.schedule();
			}
		});
	}

//	private void asteriskServerSelected()
//	{
//		final AsteriskServer newAsteriskServer = asteriskServerCombo.getSelectedElement();
//		if (Util.equals(newAsteriskServer, getConfigModule().getAsteriskServer()))
//			return;
//
//		final AsteriskServerID newAsteriskServerID = (AsteriskServerID) JDOHelper.getObjectId(newAsteriskServer);
//
//		Job job = new Job("Loading asterisk server") {
//			@Override
//			protected IStatus run(ProgressMonitor monitor) throws Exception {
//				final AsteriskServer[] asteriskServer = new AsteriskServer[1];
//				try {
//					asteriskServer[0] = AsteriskServerDAO.sharedInstance().getAsteriskServer(newAsteriskServerID,
//							AsteriskServerConfigModuleController.FETCH_GROUPS_ASTERISK_SERVER,
//							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//							monitor
//					);
//				} finally {
//					display.asyncExec(new Runnable() {
//						public void run() {
//							parentComposite.setEnabled(true);
//							if (asteriskServer[0] != null) {
//								callFilePropertyCfModTable.setAsteriskServer(asteriskServer[0]); // this should update the config-module, too
//								getPageDirtyStateManager().markDirty();
//							}
//						}
//					});
//				}
//				return Status.OK_STATUS;
//			}
//		};
//		job.setPriority(Job.INTERACTIVE);
//		job.setUser(true);
//		parentComposite.setEnabled(false);
//		job.schedule();
//	}

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