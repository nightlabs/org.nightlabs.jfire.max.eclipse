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
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issuetracking.admin.ui.resource.Messages;
import org.nightlabs.jfire.security.SecurityReflector;

/**
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 *
 */
public class IssueTypePriorityComposite 
extends XComposite
{
	private I18nTextEditor priorityNameI18nTextEditor;
	private Button autoCreateIDCheckBox;
	private Label idLabel; 
	private Text idText;
	
	private IssuePriority issuePriority;
	
	public IssueTypePriorityComposite(IssuePriority issuePriority, Composite parent, int style) {
		super(parent, style);
		this.issuePriority = issuePriority;
		
		createComposite(this);
	}

	/**
	 * Create the content for this composite.
	 * @param parent The parent composite
	 */
	protected void createComposite(Composite parent) {
		setLayout(new GridLayout(1, false));

		// Name
		new Label(this, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypePriorityComposite.label.priorityName.text")); //$NON-NLS-1$
		priorityNameI18nTextEditor = new I18nTextEditor(this);
		priorityNameI18nTextEditor.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent ev) {
				if (issuePriority == null && autoCreateIDCheckBox.getSelection()) {
					String nameStr = priorityNameI18nTextEditor.getEditText();
					idText.setText(ObjectIDUtil.makeValidIDString(nameStr));
				}
			}
		});
		// ID
		Group idGroup = new Group(this, SWT.NONE);
		idGroup.setText(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypePriorityComposite.group.priorityID.text")); //$NON-NLS-1$
		idGroup.setLayout(new GridLayout(1, false));
		idGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		autoCreateIDCheckBox = new Button(idGroup, SWT.CHECK);
		autoCreateIDCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				enableCheckingID(!autoCreateIDCheckBox.getSelection());
			}
		});
		autoCreateIDCheckBox.setText(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypePriorityComposite.checkbox.autoCreateID.text")); //$NON-NLS-1$

		idLabel = new Label(idGroup, SWT.NONE);
		idLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypePriorityComposite.label.id.text")); //$NON-NLS-1$
		idText = new Text(idGroup, SWT.SINGLE | SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		idText.setLayoutData(gridData);
		
		if(issuePriority != null) {
			priorityNameI18nTextEditor.setI18nText(issuePriority.getIssuePriorityText());
			idText.setText(issuePriority.getIssuePriorityID());
			setAutoCreateID(true);
			autoCreateIDCheckBox.setEnabled(false);
		} else {
			priorityNameI18nTextEditor.setI18nText(new I18nTextBuffer());
			idText.setText(""); //$NON-NLS-1$
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
	
	public IssuePriority getIssuePriority() {
		if (!isComplete())
			return null;
		if (issuePriority == null) {
			issuePriority = new IssuePriority(SecurityReflector.getUserDescriptor().getOrganisationID(), idText.getText());
		}
		issuePriority.getIssuePriorityText().copyFrom(priorityNameI18nTextEditor.getI18nText());
		return issuePriority;
	}

	/**
	 * Check if the editing of the issue priority can be completed (valid data is available)
	 * @return
	 */
	public boolean isComplete() {
		return issuePriority != null || (!"".equals(idText.getText())); //$NON-NLS-1$
	}
	
	public I18nTextEditor getPriorityNameI18nTextEditor() {
		return priorityNameI18nTextEditor;
	}
}
