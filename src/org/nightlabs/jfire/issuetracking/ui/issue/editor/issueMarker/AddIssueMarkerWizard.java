package org.nightlabs.jfire.issuetracking.ui.issue.editor.issueMarker;

import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.language.II18nTextEditor;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.issuemarker.IssueMarker;
import org.nightlabs.jfire.issue.issuemarker.IssueMarkerName;

//      ,-_|\
//     /     \  ]Egoiste in
//     @_,-._/       ]N[ightLabs
//  ======= v =====================================================================================
//  "Science without religion is lame, religion without science is blind." -- A. E. (1879 - 1955).
/**
 * A simple wizard to interface with the user in helping to create a new IssueMarker for a related Issue.
 *
 * @author Khaireel Mohamed - khaireel at nightlabs dot de
 */
public class AddIssueMarkerWizard extends DynamicPathWizard {
	private Issue issue;
	private AddIssueMarkerWizardPage page;

	/**
	 * Creates a new instance of an AddIssueMarkerWizard.
	 */
	public AddIssueMarkerWizard(Issue issue) {
		this.issue = issue;
		setWindowTitle("Add new issue marker");
		setForcePreviousAndNextButtons(false);
	}

	@Override
	public void addPages() {
		page = new AddIssueMarkerWizardPage();
		addPage(page);
	}



//	private static final String[] FETCH_GROUPS_ISSUE = new String[] {
//		FetchPlan.DEFAULT,
//		Issue.FETCH_GROUP_ISSUE_MARKERS,
//	};
//
//	private static final String[] FETCH_GROUPS_ISSUE_MARKER = new String[] {
//		FetchPlan.DEFAULT,
//		IssueMarker.FETCH_GROUP_NAME,
//		IssueMarker.FETCH_GROUP_DESCRIPTION,	// Icon is still missing!
//	};

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		// Test
		IssueMarkerName issueMarkerName = page.getIssueMarkerName();
//		I18nTextBuffer issueMarkerDesc = page.getIssueMarkerDescBuffer();

//		// Get the latest reference the related Issue.
//		final Issue issue = IssueDAO.sharedInstance().getIssue(issueID, FETCH_GROUPS_ISSUE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());

		// Create and persist new IssueMarker.
		IssueMarker issueMarker = new IssueMarker(IDGenerator.getOrganisationID(), IDGenerator.nextID(IssueMarker.class));
		issueMarker.getName().copyFrom( issueMarkerName );
		issueMarker.getDescription().setText(Locale.ENGLISH.getLanguage(), "Some test description.");  //.copyFrom( issueMarkerDesc );
		issue.addIssueMarker(issueMarker);

//		// Store the contents.
//		Job storeJob = new Job("Storing new IssueMarker ...") {
//			@Override
//			protected IStatus run(ProgressMonitor monitor) throws Exception {
//				IssueManagerRemote imr = null;
//				try                 { imr = JFireEjb3Factory.getRemoteBean(IssueManagerRemote.class, Login.getLogin().getInitialContextProperties()); }
//				catch (Exception e) { throw new RuntimeException(e); }
//
//				issueMarker_det = imr.storeIssueMarker(issueMarker, true, FETCH_GROUPS_ISSUE_MARKER, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//
////				issue_det = imr.storeIssue(issue, true, FETCH_GROUPS_ISSUE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//				return Status.OK_STATUS;
//			}
//		};
//
//		storeJob.setUser(true);
//		storeJob.setPriority(Job.SHORT);
//		storeJob.schedule();
		return true;
	}



	// --------------------------------------------------------------------------------------------------------------------------------
	/**
	 * The singular page used in this wizard to receive the name and description
	 * of a new IssueMarker.
	 */
	protected class AddIssueMarkerWizardPage extends WizardHopPage {
		// Convenient references.
		private I18nTextBuffer issueMarkerDescBuffer;
		private II18nTextEditor issueMarkerDescEditor;

		private IssueMarkerNameCombo issueMarkerNameCombo;

		/**
		 * Creates a new instance of an AddIssueMarkerWizardPage.
		 */
		public AddIssueMarkerWizardPage() {
			super(AddIssueMarkerWizardPage.class.getName(), "Add issue marker");
			setDescription("Select an issue marker name and provide a description to mark this issue.");
		}


		/* (non-Javadoc)
		 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public Control createPageContents(Composite parent) {
			XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
			page.setLayoutData(new GridData(GridData.FILL_BOTH));
			page.getGridLayout().numColumns = 1;

			new Label(page, SWT.NONE).setText("Marker name :");
			issueMarkerNameCombo = new IssueMarkerNameCombo(page);


			new Label(page, SWT.NONE).setText("Description :");
//			issueMarkerDescBuffer = new I18nTextBuffer();
//			issueMarkerDescEditor = new I18nTextEditorTable(page);
//			issueMarkerDescEditor.setI18nText(issueMarkerDescBuffer);
//			issueMarkerDescEditor.addModifyListener(new ModifyListener() {
//				public void modifyText(ModifyEvent arg0)	{ getWizard().getContainer().updateButtons(); }
//			});

			return page;
		}

		/**
		 * @return the issueMarkerDescEditor.
		 */
		public II18nTextEditor getIssueMarkerDescEditor()   { return issueMarkerDescEditor; }

		/**
		 * @return the issueMarkerDescBuffer.
		 */
		public I18nTextBuffer getIssueMarkerDescBuffer()    { return issueMarkerDescBuffer; }

		/**
		 * @return the selected IssueMarkerName from the drop-down combo.
		 */
		public IssueMarkerName getIssueMarkerName()         { return issueMarkerNameCombo.getSelectedElement().getName(); }

		@Override
		public boolean isPageComplete() {
			return true; //issueMarkerDescBuffer != null && !issueMarkerDescBuffer.isEmpty();
		}
	}

}
