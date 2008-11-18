/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.textpart;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.EditorPart;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.form.NightlabsFormsToolkit;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.language.LanguageChooserCombo;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.JFireRemoteReportEditorInput;
import org.nightlabs.jfire.reporting.dao.ReportRegistryItemDAO;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.textpart.ReportTextPart;
import org.nightlabs.jfire.reporting.textpart.ReportTextPartConfiguration;
import org.nightlabs.jfire.reporting.textpart.dao.ReportTextPartConfigurationDAO;
import org.nightlabs.jfire.reporting.ui.textpart.IReportTextPartConfigurationChangedListener;
import org.nightlabs.jfire.reporting.ui.textpart.ReportTextPartConfigurationChangedEvent;
import org.nightlabs.jfire.reporting.ui.textpart.ReportTextPartConfigurationEditComposite;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportTextPartConfigurationEditor extends EditorPart {

	private static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT, ReportTextPartConfiguration.FETCH_GROUP_REPORT_TEXT_PARTS,
		ReportTextPart.FETCH_GROUP_NAME, ReportTextPart.FETCH_GROUP_CONTENT
	};
	
	private ScrolledForm form;
	private NightlabsFormsToolkit toolkit;
	private ReportTextPartConfigurationEditComposite configurationEditComposite;
	private boolean dirty;
	private IReportTextPartConfigurationChangedListener changedListener = new IReportTextPartConfigurationChangedListener() {
		@Override
		public void reportTextPartConfigurationChanged(ReportTextPartConfigurationChangedEvent evt) {
			markDirty();
		}
	};
	
	private volatile ReportTextPartConfiguration reportTextPartConfiguration;

	private AddReportTextPartAction addReportTextPartAction;
	
	/**
	 * 
	 */
	public ReportTextPartConfigurationEditor() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		synchronized (this) {
			if (configurationEditComposite != null && !configurationEditComposite.isDisposed()) {
				configurationEditComposite.updateReportTextPartConfiguration();
			}
			if (monitor == null) {
				// Have to check, BIRT passes null
				monitor = new NullProgressMonitor();
			}
			reportTextPartConfiguration = ReportTextPartConfigurationDAO.sharedInstance().storeReportTextPartConfiguration(
					reportTextPartConfiguration, true, FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new ProgressMonitorWrapper(monitor));
			updateConfigurationEditComposite();
		}
		dirty = false;
	}

	protected void updateConfigurationEditComposite() {
		if (configurationEditComposite == null)
			return;
		configurationEditComposite.setReportTextPartConfiguration(reportTextPartConfiguration);		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		JFireRemoteReportEditorInput reportEditorInput = (JFireRemoteReportEditorInput) input;
		final ReportRegistryItemID reportRegistryItemID = reportEditorInput.getReportRegistryItemID();
		
		Job loadJob = new Job("Loading ReportTextPartConfiguration") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				monitor.beginTask("Loading ReportTextPartConfiguration", 10);
				ReportTextPartConfiguration config = ReportTextPartConfigurationDAO.sharedInstance().getReportTextPartConfiguration(
						reportRegistryItemID, true, FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 7));
				synchronized (this) {
					reportTextPartConfiguration = config;
					if (reportTextPartConfiguration == null) {
						reportTextPartConfiguration = new ReportTextPartConfiguration(
								IDGenerator.getOrganisationID(),
								IDGenerator.nextID(ReportTextPartConfiguration.class)
							);
						ReportRegistryItem item = ReportRegistryItemDAO.sharedInstance().getReportRegistryItem(
								reportRegistryItemID, new String[] {FetchPlan.DEFAULT}, new SubProgressMonitor(monitor, 3));
						reportTextPartConfiguration.setReportRegistryItem(item);
					}
					if (configurationEditComposite != null && !configurationEditComposite.isDisposed()) {
						configurationEditComposite.getDisplay().asyncExec(new Runnable() {
							public void run() {
								updateConfigurationEditComposite();
							}
						});
					}
					monitor.done();
				}
				return Status.OK_STATUS;
			}
		};
		loadJob.schedule();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return dirty;
	}

	protected void markDirty() {
		dirty = true;
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		synchronized (this) {
			toolkit = new NightlabsFormsToolkit(parent.getDisplay());
			form = toolkit.createScrolledForm(parent);
			GridLayout gl = new GridLayout();
			XComposite.configureLayout(LayoutMode.ORDINARY_WRAPPER, gl);
			form.getBody().setLayout(gl);
			
			ToolBarSectionPart section = new ToolBarSectionPart(
					toolkit, form.getBody(), 
					ExpandableComposite.TITLE_BAR,
					"Text part configuration"
				);
			XComposite comp = new XComposite(section.getSection(), SWT.NONE);
			comp.setToolkit(toolkit);
			section.getSection().setClient(comp);
			
			form.setText("Text part configuration");
			
			addReportTextPartAction = new AddReportTextPartAction(this);
			section.registerAction(addReportTextPartAction);
			section.updateToolBarManager();
			
			LanguageChooserCombo languageChooser = new LanguageChooserCombo(comp);
			languageChooser.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_END | GridData.HORIZONTAL_ALIGN_END));
			configurationEditComposite = new ReportTextPartConfigurationEditComposite(comp, SWT.NONE, languageChooser, true);
			configurationEditComposite.addReportTextPartConfigurationChangedListener(changedListener);
			configurationEditComposite.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent e) {
					configurationEditComposite.removeReportTextPartConfigurationChangedListener(changedListener);
				}
			});
			configurationEditComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
			comp.adaptToToolkit();
			
			if (reportTextPartConfiguration != null) {
				updateConfigurationEditComposite();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		configurationEditComposite.setFocus();
	}
	
	protected ReportTextPartConfigurationEditComposite getConfigurationEditComposite() {
		return configurationEditComposite;
	}
	
	protected ReportTextPartConfiguration getReportTextPartConfiguration() {
		return reportTextPartConfiguration;
	}
}
