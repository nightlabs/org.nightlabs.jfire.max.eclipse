package org.nightlabs.jfire.issuetracking.ui.issue.create;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.DateTimeControl;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.department.DepartmentComboComposite;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectComboComposite;
import org.nightlabs.jfire.security.User;
import org.nightlabs.l10n.DateFormatter;
/**
 * A composite that contains UIs for adding {@link Issue}.
 * 
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class QuickCreateIssueComposite 
extends XComposite
{
	/**
	 * Contructs a composite used for adding {@link Issue}.
	 * 
	 * @param parent -the parent composite
	 * @param style - the SWT style flag 
	 */
	public QuickCreateIssueComposite(Composite parent, int style) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);

		newIssue = new Issue(IDGenerator.getOrganisationID(), IDGenerator.nextID(Issue.class));
		newIssue.setReporter(Login.sharedInstance().getUser(new String[]{User.FETCH_GROUP_NAME}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new org.eclipse.core.runtime.NullProgressMonitor()));
		
		createComposite();
	}

	private ProjectComboComposite projectComboComposite;
	private DepartmentComboComposite departmentComboComposite;
	private DateTimeControl startTimeControl;
	private Text durationText;
	private I18nTextEditor subjectText;
	private I18nTextEditorMultiLine descriptionText;
	
	private Issue newIssue;

	private void createComposite() {
		getGridLayout().numColumns = 1;
		getGridLayout().makeColumnsEqualWidth = false;
		getGridData().grabExcessHorizontalSpace = true;

		XComposite mainComposite = new XComposite(this, SWT.NONE,
				LayoutMode.TIGHT_WRAPPER);
		mainComposite.getGridLayout().numColumns = 4;

		new Label(mainComposite, SWT.NONE).setText("Project");
		
		projectComboComposite = new ProjectComboComposite(mainComposite, SWT.None);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		projectComboComposite.setLayoutData(gridData);
		projectComboComposite.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent e) {
				newIssue.setProject(projectComboComposite.getSelectedProject());
			}
		});

		departmentComboComposite = new DepartmentComboComposite(mainComposite, SWT.None);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		departmentComboComposite.setLayoutData(gridData);
		departmentComboComposite.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent e) {
				newIssue.setDepartment(departmentComboComposite.getSelectedDepartment());
			}
		});

		XComposite timeComposite = new XComposite(mainComposite, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		timeComposite.setLayoutData(gridData);

		new Label(timeComposite, SWT.NONE).setText("Start Time");
		startTimeControl  = new DateTimeControl(timeComposite, SWT.NONE, DateFormatter.FLAGS_DATE_SHORT_TIME_HM);
		startTimeControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		XComposite durationComposite = new XComposite(mainComposite, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		durationComposite.setLayoutData(gridData);

		new Label(durationComposite, SWT.NONE).setText("Duration(DD:hh:mm)");
		durationText = new Text(durationComposite, SWT.BORDER);
		durationText.setTextLimit(8);
		durationText.setText("00:00:00");
		durationText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		XComposite subjectDescriptionComposite = new XComposite(mainComposite, SWT.NONE);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 4;
		subjectDescriptionComposite.setLayoutData(gridData);

		Label subjectLabel = new Label(subjectDescriptionComposite, SWT.WRAP);
		subjectLabel.setLayoutData(new GridData());
		subjectLabel.setText("Subject");

		subjectText = new I18nTextEditor(subjectDescriptionComposite);
		subjectText.setI18nText(newIssue.getSubject(), EditMode.DIRECT);
		
		Label descriptionLabel = new Label(subjectDescriptionComposite, SWT.WRAP);
		descriptionLabel.setLayoutData(new GridData());
		descriptionLabel.setText("Description");

		descriptionText = new I18nTextEditorMultiLine(subjectDescriptionComposite, subjectText.getLanguageChooser());
		descriptionText.setI18nText(newIssue.getDescription(), EditMode.DIRECT);
	}
	
	public Issue getCreatingIssue() {
		return newIssue;
	}
}