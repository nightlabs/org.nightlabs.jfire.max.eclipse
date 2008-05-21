package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.RestorableSectionPart;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.jfire.issue.IssueType;

public class IssueTypeNameSection extends RestorableSectionPart {

	private IssueTypeEditorPageController controller;
	private IssueType issueType;
	
	private I18nTextEditor issueTypeName;
	
	public IssueTypeNameSection(FormPage page, Composite parent, IssueTypeEditorPageController controller) {
		super(parent, page.getEditor().getToolkit(), ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		this.controller = controller;
		getSection().setText("Issue Type");
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());
		
		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 2; 

		new Label(client, SWT.NONE).setText("Issue Type Name: ");
		issueTypeName = new I18nTextEditor(client);
		issueTypeName.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				issueType.getName().copyFrom(issueTypeName.getI18nText());
				markDirty();
			}
		});
		
		getSection().setClient(client);
	}

	public void setIssueType(IssueType issueType){
		this.issueType = issueType;
		issueTypeName.setI18nText(issueType.getName());
	}
}
