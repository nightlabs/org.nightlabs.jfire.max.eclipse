package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.security.SecurityReflector;

/**
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 *
 */
public class IssueTypeSeverityTypeComposite 
extends XComposite
{
	private I18nTextEditor severityTypeNameI18nTextEditor;
	private Button autoCreateIDCheckBox;
	private Label idLabel; 
	private Text idText;
	
	private IssueSeverityType issueSeverityType;
	
	public IssueTypeSeverityTypeComposite(IssueSeverityType issueSeverityType, Composite parent, int style) {
		super(parent, style);
		this.issueSeverityType = issueSeverityType;
		
		createComposite(this);
	}

	/**
	 * Create the content for this composite.
	 * @param parent The parent composite
	 */
	protected void createComposite(Composite parent) {
		setLayout(new GridLayout(1, false));

		// Name
		new Label(this, SWT.NONE).setText("Priority Name: ");
		severityTypeNameI18nTextEditor = new I18nTextEditor(this);
		severityTypeNameI18nTextEditor.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent ev) {
				if (issueSeverityType == null && autoCreateIDCheckBox.getSelection()) {
					String nameStr = severityTypeNameI18nTextEditor.getEditText();
					idText.setText(ObjectIDUtil.makeValidIDString(nameStr));
				}
			}
		});
		// ID
		Group idGroup = new Group(this, SWT.NONE);
		idGroup.setText("Priority ID");
		idGroup.setLayout(new GridLayout(1, false));
		idGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		autoCreateIDCheckBox = new Button(idGroup, SWT.CHECK);
		autoCreateIDCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				enableCheckingID(!autoCreateIDCheckBox.getSelection());
			}
		});
		autoCreateIDCheckBox.setText("Auto-create IssuePriorityID.");

		idLabel = new Label(idGroup, SWT.NONE);
		idLabel.setText("ID: ");
		idText = new Text(idGroup, SWT.SINGLE | SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		idText.setLayoutData(gridData);
		
		if(issueSeverityType != null) {
			severityTypeNameI18nTextEditor.setI18nText(issueSeverityType.getIssueSeverityTypeText());
			idText.setText(issueSeverityType.getIssueSeverityTypeID());
			setAutoCreateID(true);
			autoCreateIDCheckBox.setEnabled(false);
		} else {
			severityTypeNameI18nTextEditor.setI18nText(new I18nTextBuffer());
			idText.setText("");
			setAutoCreateID(true);
		}
		
		enableCheckingID(false);
	}
	
	public void enableCheckingID(boolean b) {
		idLabel.setEnabled(b);
		idText.setEnabled(b);
	}
	
	protected void setAutoCreateID(boolean b) {
		autoCreateIDCheckBox.setSelection(b);
		enableCheckingID(!b);
	}
	
	public IssueSeverityType getIssueSeverityType() {
		if (!isComplete())
			return null;
		if (issueSeverityType == null) {
			issueSeverityType = new IssueSeverityType(SecurityReflector.getUserDescriptor().getOrganisationID(), idText.getText());
		}
		issueSeverityType.getIssueSeverityTypeText().copyFrom(severityTypeNameI18nTextEditor.getI18nText());
		return issueSeverityType;
	}

	/**
	 * Check if the editing of the issue priority can be completed (valid data is available)
	 * @return
	 */
	public boolean isComplete() {
		return issueSeverityType != null || (!"".equals(idText.getText()));
	}
	
	public I18nTextEditor getSeverityTypeNameI18nTextEditor() {
		return severityTypeNameI18nTextEditor;
	}
}
