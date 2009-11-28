package org.nightlabs.jfire.pbx.ui.config;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.config.AbstractWorkstationConfigModulePreferencePage;
import org.nightlabs.jfire.base.ui.config.IConfigModuleController;
import org.nightlabs.jfire.organisation.Organisation;
import org.nightlabs.jfire.pbx.Call;
import org.nightlabs.jfire.pbx.PhoneSystem;
import org.nightlabs.jfire.pbx.config.PhoneSystemConfigModule;
import org.nightlabs.jfire.pbx.dao.PhoneSystemDAO;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.NLLocale;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 * @author Marco หงุ่ยตระกูล-Schulze - marco at nightlabs dot de
 */
public class PhoneSystemWorkstationCfModPreferencePage
extends AbstractWorkstationConfigModulePreferencePage
{
	private Shell shell;
	private Display display;
	private Composite parentComposite;

	private Locale locale = NLLocale.getDefault();
	private XComboComposite<PhoneSystem> phoneSystemCombo;

	@Override
	protected IConfigModuleController createConfigModuleController() {
		return new PhoneSystemConfigModuleController(this);
	}

	@Override
	protected void createPreferencePage(Composite parent) {
		parentComposite = parent;
		shell = getShell();
		display = shell.getDisplay();
		phoneSystemCombo = new XComboComposite<PhoneSystem>(parent, SWT.READ_ONLY, new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((PhoneSystem)element).getName().getText(locale);
			}
		});
		PhoneSystem loadingMessageDummy = new PhoneSystem(Organisation.DEV_ORGANISATION_ID, "_dummy_") {
			private static final long serialVersionUID = 1L;
			@Override
			public void call(Call call) { }
		};
		loadingMessageDummy.getName().setText(Locale.getDefault(), "Loading data...");
		phoneSystemCombo.addElement(loadingMessageDummy);
		phoneSystemCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				phoneSystemSelected();
			}
		});

		Job loadPhoneSystemsJob = new Job("Loading phone systems") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				final List<PhoneSystem> phoneSystems = new ArrayList<PhoneSystem>(PhoneSystemDAO.sharedInstance().getPhoneSystems(
						new String[] {
								FetchPlan.DEFAULT,
								PhoneSystem.FETCH_GROUP_NAME,
						},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						monitor
				));
				final Collator collator = Collator.getInstance();

				Collections.sort(phoneSystems, new Comparator<PhoneSystem>() {
					@Override
					public int compare(PhoneSystem o1, PhoneSystem o2) {
						return collator.compare(o1.getName().getText(locale), o2.getName().getText(locale));
					}
				});

				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						if (phoneSystemCombo.isDisposed())
							return;

						phoneSystemCombo.removeAll();
						phoneSystemCombo.addElements(phoneSystems);
						phoneSystemCombo.selectElement(getConfigModule().getPhoneSystem());
					}
				});
				return Status.OK_STATUS;
			}
		};
		loadPhoneSystemsJob.setPriority(Job.INTERACTIVE);
		loadPhoneSystemsJob.schedule();
	}

	private void phoneSystemSelected()
	{
		getPageDirtyStateManager().markDirty();

//		final PhoneSystem newPhoneSystem = phoneSystemCombo.getSelectedElement();
//		if (Util.equals(newPhoneSystem, getConfigModule().getPhoneSystem()))
//			return;
//
//		final PhoneSystemID newPhoneSystemID = (PhoneSystemID) JDOHelper.getObjectId(newPhoneSystem);
//
//		Job job = new Job("Loading phone system") {
//			@Override
//			protected IStatus run(ProgressMonitor monitor) throws Exception {
//				final PhoneSystem[] phoneSystem = new PhoneSystem[1];
//				try {
//					phoneSystem[0] = PhoneSystemDAO.sharedInstance().getPhoneSystem(newPhoneSystemID,
//							PhoneSystemConfigModuleController.FETCH_GROUPS_PHONE_SYSTEM,
//							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//							monitor
//					);
//				} finally {
//					display.asyncExec(new Runnable() {
//						public void run() {
//							parentComposite.setEnabled(true);
//							if (phoneSystem[0] != null) {
////								callFilePropertyCfModTable.setPhoneSystem(phoneSystem[0]); // this should update the config-module, too
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
	}

	@Override
	public void updateConfigModule() {
		PhoneSystemConfigModule configModule = getConfigModule();
		configModule.setPhoneSystem(phoneSystemCombo.getSelectedElement());
	}

	private PhoneSystemConfigModule getConfigModule() {
		return (PhoneSystemConfigModule) getConfigModuleController().getConfigModule();
	}

	@Override
	protected void updatePreferencePage() {
		PhoneSystemConfigModule configModule = getConfigModule();
		phoneSystemCombo.selectElement(configModule.getPhoneSystem());
	}
}