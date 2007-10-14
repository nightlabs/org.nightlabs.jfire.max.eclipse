/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.oda.jfs.client.ui;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.dialog.CenteredDialog;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.reporting.admin.resource.Messages;
import org.nightlabs.jfire.scripting.Script;
import org.nightlabs.jfire.scripting.ScriptRegistryItem;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.jfire.scripting.ui.ScriptRegistryItemNode;
import org.nightlabs.jfire.scripting.ui.ScriptRegistryItemProvider;
import org.nightlabs.jfire.scripting.ui.ScriptRegistryItemTree;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class SelectedScriptComposite extends XComposite {

	private Group group;
	private Button browse;
	private Text name;
	private Text description;
	
	private ScriptRegistryItemID scriptRegistryItemID;
	
	
	private static class SelectScriptDialog extends CenteredDialog {

		private Script selectedScript;
		
		private ScriptRegistryItemTree itemTree;
		
		public SelectScriptDialog(Shell parentShell) {
			super(parentShell);
			setShellStyle(getShellStyle() | SWT.RESIZE);
		}
		
		/**
		 * {@inheritDoc}
		 * @see org.nightlabs.base.ui.dialog.CenteredDialog#configureShell(org.eclipse.swt.widgets.Shell)
		 */
		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			newShell.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.oda.jfs.client.ui.SelectedScriptComposite.selectSourceScriptShell.title")); //$NON-NLS-1$
			setToCenteredLocationPreferredSize(newShell, 300, 400);
		}		
		
		@Override
		protected Control createDialogArea(Composite parent) {
			itemTree = new ScriptRegistryItemTree(parent, null);
			itemTree.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					ScriptRegistryItemNode node = (ScriptRegistryItemNode) itemTree.getFirstSelectedElement();
					if (node != null) {
						if (node.getRegistryItem() != null && node.getRegistryItem() instanceof Script) {
							selectedScript = (Script) node.getRegistryItem(); 
						} else {
							selectedScript = null;
						}
					} else {
						selectedScript = null;
					}
					getButton(IDialogConstants.OK_ID).setEnabled(selectedScript != null);
				}
			});
			itemTree.setInput(ScriptRegistryItemProvider.sharedInstance().getTopLevelNodes());
			return itemTree;
		}
		
		public static Script openDialog() {
			SelectScriptDialog dlg = new SelectScriptDialog(RCPUtil.getActiveWorkbenchShell());
			if (dlg.open() == Dialog.OK) {
				return dlg.selectedScript;
			}
			return null;
		}
		
	}
	
	/**
	 * @param parent
	 * @param style
	 */
	public SelectedScriptComposite(Composite parent, int style) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
		group = new Group(this, SWT.NONE);
		group.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.oda.jfs.client.ui.SelectedScriptComposite.selectScriptGroup.text")); //$NON-NLS-1$
		GridLayout gl = new GridLayout();
		XComposite.configureLayout(LayoutMode.ORDINARY_WRAPPER, gl);
		gl.numColumns = 2;
		gl.makeColumnsEqualWidth = false;
		group.setLayout(gl);
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		name = new Text(group, SWT.WRAP | SWT.BORDER | SWT.READ_ONLY);
		name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		browse = new Button(group, SWT.PUSH);
		browse.setLayoutData(new GridData());
		browse.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.oda.jfs.client.ui.SelectedScriptComposite.browseButton.text")); //$NON-NLS-1$
		browse.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				Script script = SelectScriptDialog.openDialog();
				if (script != null) {
					setScriptRegistryItemID((ScriptRegistryItemID) JDOHelper.getObjectId(script));
				}
			}
		});
		description = new Text(group, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		description.setLayoutData(gd);
		name.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		description.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
	}

	public void setScriptRegistryItemID(final ScriptRegistryItemID scriptRegistryItemID) {
		this.scriptRegistryItemID = scriptRegistryItemID;
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				name.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.oda.jfs.client.ui.SelectedScriptComposite.nameText.loadingText")); //$NON-NLS-1$
				description.setText(""); //$NON-NLS-1$
			}
		});
		Job loadJob = new Job(Messages.getString("org.nightlabs.jfire.reporting.admin.oda.jfs.client.ui.SelectedScriptComposite.loadScriptJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				final ScriptRegistryItem item = ScriptRegistryItemProvider.sharedInstance().getScriptRegistryItem(
						scriptRegistryItemID, 
						new String[] {
								FetchPlan.DEFAULT, ScriptRegistryItem.FETCH_GROUP_NAME, 
								ScriptRegistryItem.FETCH_GROUP_DESCRIPTION
							},
						getProgressMonitor()
					);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						name.setText(item.getName().getText());
						if (!item.getDescription().isEmpty()) {							
							description.setText(item.getDescription().getText());
						} else {
							description.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.oda.jfs.client.ui.SelectedScriptComposite.description.fallbackText")); //$NON-NLS-1$
						}
					}
				});
				return Status.OK_STATUS;
			}
		};
		loadJob.schedule();
	}
	
	public ScriptRegistryItemID getScriptRegistryItemID() {
		return scriptRegistryItemID;
	}
}
