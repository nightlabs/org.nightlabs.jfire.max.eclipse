package org.nightlabs.jfire.issuetracking.ui.issue.editor.issueMarker;

import java.io.IOException;
import java.util.Collection;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.FileSelectionComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.issuemarker.IssueMarker;
import org.nightlabs.jfire.issue.issuemarker.IssueMarkerDAO;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.IOUtil;

/**
 * A simple wizard to interface with the user in helping to create a new IssueMarker for a related Issue.
 *
 * @author Khaireel Mohamed - khaireel at nightlabs dot de
 * @author Marco หงุ่ยตระกูล-Schulze - marco at nightlabs dot de
 */
public class AddIssueMarkerWizard extends DynamicPathWizard {
	private Issue issue;
	private AddIssueMarkerWizardPage page;

	/**
	 * Creates a new instance of an AddIssueMarkerWizard.
	 */
	public AddIssueMarkerWizard(Issue issue) {
		this.issue = issue;
		setWindowTitle(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.issueMarker.AddIssueMarkerWizard.window.title")); //$NON-NLS-1$
	}

	@Override
	public void addPages() {
		page = new AddIssueMarkerWizardPage();
		addPage(page);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		
		if (page.isCreateNew()) {
			IssueMarker issueMarker = new IssueMarker(null);
			page.configureIssueMarker(issueMarker);
			issueMarker = IssueMarkerDAO.sharedInstance().storeIssueMarker(issueMarker, true, FETCH_GROUPS_ISSUE_MARKER, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
			issue.addIssueMarker(issueMarker);
		} else {
			// Retrieve what was selected from the wizard page, and add it to the referenced Issue.
			for(IssueMarker issueMarker : page.getSelectedIssueMarkers())
				issue.addIssueMarker(issueMarker);
		}

		// QUESTION:
		//    1. Do I perform the save here?
		//    2. Or simply tell the calling class to mark itself dirty?
		//
		// ANSWER: None of the above.
		//    Instead, perform a refresh() on the corresponding table, and then mark
		//    the IssueMarkerSection dirty.

		// That's it! We're done!
		return true;
	}

	private static final String[] FETCH_GROUPS_ISSUE_MARKER = {
		FetchPlan.DEFAULT,
		IssueMarker.FETCH_GROUP_NAME,
		IssueMarker.FETCH_GROUP_DESCRIPTION,
		IssueMarker.FETCH_GROUP_ICON_16X16_DATA
	};

	// --------------------------------------------------------------------------------------------------------------------------------
	/**
	 * The singular page used in this wizard to receive the name and description
	 * of a new IssueMarker.
	 */
	protected class AddIssueMarkerWizardPage extends WizardHopPage {
		
		private Button createNewCheckBox;
		private Button selectFromCheckBox;
		
		// Convenient references.
		private IssueMarkerWizardTable issueMarkerWizardTable;

		private CreateIssueMarkerPage createPage;
		
		/**
		 * Creates a new instance of an AddIssueMarkerWizardPage.
		 */
		public AddIssueMarkerWizardPage() {
			super(AddIssueMarkerWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.issueMarker.AddIssueMarkerWizard.page.name")); //$NON-NLS-1$
			setDescription(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.issueMarker.AddIssueMarkerWizard.page.description")); //$NON-NLS-1$
			createPage = new CreateIssueMarkerPage();
			new WizardHop(this);
		}


		/* (non-Javadoc)
		 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public Control createPageContents(Composite parent) {
			XComposite page = new XComposite(parent, SWT.NONE);
			XComposite comp = new XComposite(page, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL);
			
			createNewCheckBox = new Button(comp, SWT.RADIO);		
			createNewCheckBox.setText("Create a new issue marker");
			createNewCheckBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			createNewCheckBox.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					updateWizardHop();
				}
			});
			
			selectFromCheckBox = new Button(comp, SWT.RADIO);
			selectFromCheckBox.setText("Select from this list");
			selectFromCheckBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			selectFromCheckBox.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					updateWizardHop();
				}
			});
			
			page.setLayoutData(new GridData(GridData.FILL_BOTH));
			page.getGridLayout().numColumns = 1;

			new Label(page, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.issueMarker.AddIssueMarkerWizard.label.markerName")); //$NON-NLS-1$
			issueMarkerWizardTable = new IssueMarkerWizardTable(page, issue.getIssueMarkers());
			issueMarkerWizardTable.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent arg0) {
					if (issueMarkerWizardTable.getFirstSelectedElement() != null) {
						selectFromCheckBox.setSelection(true);
						updateWizardHop();					
					}
				}
			});
			issueMarkerWizardTable.addDoubleClickListener( new IDoubleClickListener() {
				@Override
				public void doubleClick(DoubleClickEvent event) { finish(); }
			});
			selectFromCheckBox.setSelection(true);

			// Load the IssueMarkers references.
			Job job = new Job(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.issueMarker.AddIssueMarkerWizard.job.name")) { //$NON-NLS-1$
				@Override
				protected IStatus run(ProgressMonitor monitor) throws Exception {
//					IssueManagerRemote imr = null;
//					try                 { imr = JFireEjb3Factory.getRemoteBean(IssueManagerRemote.class, Login.getLogin().getInitialContextProperties()); }
//					catch (Exception e) { throw new RuntimeException(e); }
//
//					Set<IssueMarkerID> issueMarkerIDs = imr.getIssueMarkerIDs();
//					final Collection<IssueMarker> issueMarkers = imr.getIssueMarkers(
//							issueMarkerIDs,
//							new String[] {FetchPlan.DEFAULT, IssueMarker.FETCH_GROUP_NAME, IssueMarker.FETCH_GROUP_DESCRIPTION, IssueMarker.FETCH_GROUP_ICON_16X16_DATA},
//							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT
//					);

					final Collection<IssueMarker> issueMarkers = IssueMarkerDAO.sharedInstance().getIssueMarkers(
							FETCH_GROUPS_ISSUE_MARKER,
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							monitor
					);

					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							issueMarkerWizardTable.setInput(issueMarkers);
						}
					});

					return Status.OK_STATUS;
				}
			};

			job.setPriority(Job.SHORT);
			job.schedule();

			return page;
		}

		/**
		 * @return the selected IssueMarkers from the table. Do we enable multiple selection??
		 */
		public Collection<IssueMarker> getSelectedIssueMarkers() {
			return issueMarkerWizardTable.getSelectedElements();
		}
		public void updateWizardHop() {
			if (isCreateNew() && !getWizardHop().getHopPages().contains(createPage)) {
				getWizardHop().addHopPage(createPage);
			} else if (!isCreateNew()) {
				getWizardHop().removeAllHopPages();
			}
			getContainer().updateButtons();
		}
		
		public boolean isCreateNew() {
			return createNewCheckBox.getSelection();
		}
		
		public void configureIssueMarker(IssueMarker issueMarker) {
			createPage.configureIssueMarker(issueMarker);
		}
		
		@Override
		public boolean isPageComplete() {
			return isCreateNew() || getSelectedIssueMarkers().size() > 0;
		}
		
	}
	
	static class CreateIssueMarkerPage extends WizardHopPage {

		private I18nTextEditor name;
		private I18nTextEditor description;
		private FileSelectionComposite icon;
		
		public CreateIssueMarkerPage() {
			super(CreateIssueMarkerPage.class.getName());
			setMessage("Create Issue-Marker");
		}

		@Override
		public Control createPageContents(Composite parent) {
			XComposite page = new XComposite(parent, SWT.NONE, LayoutDataMode.NONE);
			name = new I18nTextEditor(page);
			description = new I18nTextEditor(page);
			icon = new FileSelectionComposite(page, SWT.NONE, FileSelectionComposite.OPEN_FILE, "Select icon", null);
			return page;
		}
		
		public void configureIssueMarker(IssueMarker issueMarker) {
			issueMarker.getName().copyFrom(name.getI18nText());
			issueMarker.getDescription().copyFrom(description.getI18nText());
			try {
				issueMarker.setIcon16x16Data(IOUtil.getBytesFromFile(icon.getFile()));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
	}

}
