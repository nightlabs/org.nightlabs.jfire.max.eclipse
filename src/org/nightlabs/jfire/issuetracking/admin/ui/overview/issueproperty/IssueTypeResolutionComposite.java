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
import org.nightlabs.jfire.issue.IssueResolution;
import org.nightlabs.jfire.issuetracking.admin.ui.resource.Messages;
import org.nightlabs.jfire.security.SecurityReflector;

/**
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 *
 */
public class IssueTypeResolutionComposite
extends XComposite
{
	private I18nTextEditor resolutionNameI18nTextEditor;
	private Button autoCreateIDCheckBox;
	private Label idLabel;
	private Text idText;

	private IssueResolution issueResolution;

	public IssueTypeResolutionComposite(IssueResolution issueResolution, Composite parent, int style) {
		super(parent, style);
		this.issueResolution = issueResolution;

		createComposite(this);
	}

	/**
	 * Create the content for this composite.
	 * @param parent The parent composite
	 */
	protected void createComposite(Composite parent) {
		setLayout(new GridLayout(1, false));

		// Name
		new Label(this, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypeResolutionComposite.label.name.text")); //$NON-NLS-1$
		resolutionNameI18nTextEditor = new I18nTextEditor(this);
		resolutionNameI18nTextEditor.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent ev) {
				if (issueResolution == null && autoCreateIDCheckBox.getSelection()) {
					String nameStr = resolutionNameI18nTextEditor.getEditText();
					idText.setText(ObjectIDUtil.makeValidIDString(nameStr));
				}
			}
		});
		// ID
		Group idGroup = new Group(this, SWT.NONE);
		idGroup.setText(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypeResolutionComposite.group.resolutionID.text")); //$NON-NLS-1$
		idGroup.setLayout(new GridLayout(1, false));
		idGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		autoCreateIDCheckBox = new Button(idGroup, SWT.CHECK);
		autoCreateIDCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				enableCheckingID(!autoCreateIDCheckBox.getSelection());
			}
		});
		autoCreateIDCheckBox.setText(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypeResolutionComposite.checkBox.autoCreateID.text")); //$NON-NLS-1$

		idLabel = new Label(idGroup, SWT.NONE);
		idLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypeResolutionComposite.label.id.text")); //$NON-NLS-1$
		idText = new Text(idGroup, SWT.SINGLE | SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		idText.setLayoutData(gridData);

		if(issueResolution != null) {
			resolutionNameI18nTextEditor.setI18nText(issueResolution.getName());
			idText.setText(issueResolution.getIssueResolutionID());
			setAutoCreateID(true);
			autoCreateIDCheckBox.setEnabled(false);
		} else {
			resolutionNameI18nTextEditor.setI18nText(new I18nTextBuffer());
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

	public IssueResolution getIssueResolution() {
		if (!isComplete())
			return null;
		if (issueResolution == null) {
			issueResolution = new IssueResolution(SecurityReflector.getUserDescriptor().getOrganisationID(), idText.getText());
		}
		issueResolution.getName().copyFrom(resolutionNameI18nTextEditor.getI18nText());
		return issueResolution;
	}

	/**
	 * Check if the editing of the issue priority can be completed (valid data is available)
	 * @return
	 */
	public boolean isComplete() {
		return issueResolution != null || (!"".equals(idText.getText())); //$NON-NLS-1$
	}

	public I18nTextEditor getResolutionNameI18nTextEditor() {
		return resolutionNameI18nTextEditor;
	}
}
