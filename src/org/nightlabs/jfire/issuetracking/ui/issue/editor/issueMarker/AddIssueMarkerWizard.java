package org.nightlabs.jfire.issuetracking.ui.issue.editor.issueMarker;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.language.I18nTextEditorTable;
import org.nightlabs.base.ui.language.II18nTextEditor;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jfire.issue.id.IssueID;

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
	private IssueID issueID;
	private AddIssueMarkerWizardPage page;

	/**
	 * Creates a new instance of an AddIssueMarkerWizard.
	 */
	public AddIssueMarkerWizard(IssueID issueID) {
		this.issueID = issueID;
		this.setWindowTitle("Add new issue marker");
	}

	@Override
	public void addPages() {
		assert issueID != null;
		page = new AddIssueMarkerWizardPage();
		addPage(page);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
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

		/**
		 * Creates a new instance of an AddIssueMarkerWizardPage.
		 */
		public AddIssueMarkerWizardPage() {
			super(AddIssueMarkerWizardPage.class.getName(), "Create a new issue marker");
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

			new Label(page, SWT.NONE).setText("Description :");
			issueMarkerDescBuffer = new I18nTextBuffer();
			issueMarkerDescEditor = new I18nTextEditorTable(page);
			issueMarkerDescEditor.setI18nText(issueMarkerDescBuffer);
			issueMarkerDescEditor.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent arg0)	{ getWizard().getContainer().updateButtons(); }
			});

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

		@Override
		public boolean isPageComplete() {
			return true; //return cashBoxTrayNameBuffer != null && !cashBoxTrayNameBuffer.isEmpty();
		}
	}

}
