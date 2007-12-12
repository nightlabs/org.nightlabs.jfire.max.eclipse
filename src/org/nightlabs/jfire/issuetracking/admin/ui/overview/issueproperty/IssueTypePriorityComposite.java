package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.jfire.issue.IssuePriority;

public class IssueTypePriorityComposite 
extends XComposite{

	private I18nTextEditor priorityName;
	private Button wantedIDCheckbox;
	private Label enableIDLabel;
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
		new Label(this, SWT.NONE).setText("Priority Name: ");
		priorityName = new I18nTextEditor(this);
		
		// ID
		Group idGroup = new Group(this, SWT.NONE);
		idGroup.setText("Priority ID");
		idGroup.setLayout(new GridLayout(2, false));
		idGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite checkboxComposite = new Composite(idGroup, SWT.NONE);
		checkboxComposite.setLayout(new GridLayout(2, false));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		checkboxComposite.setLayoutData(gridData);
		wantedIDCheckbox = new Button(checkboxComposite, SWT.CHECK);
		wantedIDCheckbox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				enableCheckingID(!wantedIDCheckbox.getSelection());
			}
		});
		enableIDLabel = new Label(checkboxComposite, SWT.NONE);
		enableIDLabel.setText("Enable user created ID.");

		idLabel = new Label(idGroup, SWT.NONE);
		idLabel.setText("ID: ");
		idText = new Text(idGroup, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		idText.setLayoutData(gridData);
		
		if(issuePriority != null) {
			priorityName.setI18nText(issuePriority.getIssuePriorityText());
			idText.setText(issuePriority.getIssuePriorityID());
		}//if
		
		enableCheckingID(false);
	}
	
	public void enableCheckingID(boolean b) {
		idLabel.setEnabled(b);
		idText.setEnabled(b);
	}
}
