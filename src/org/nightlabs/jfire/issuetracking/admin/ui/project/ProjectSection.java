package org.nightlabs.jfire.issuetracking.admin.ui.project;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.jfire.issue.project.Project;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class ProjectSection 
extends ToolBarSectionPart
{
	private Label nameLabel;
	private I18nTextEditor nameText;
	private Label descriptionLabel;
	private I18nTextEditor descriptionText;
	
	public ProjectSection(FormPage page, Composite parent, final ProjectEditorPageController controller) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR, "Projects");
		
		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		nameLabel = new Label(client, SWT.WRAP);
		nameLabel.setLayoutData(new GridData());
		nameLabel.setText("Name:");
		
		nameText = new I18nTextEditor(client);
		
		descriptionLabel = new Label(client, SWT.WRAP);
		descriptionLabel.setLayoutData(new GridData());
		descriptionLabel.setText("Description:");
		
		descriptionText = new I18nTextEditorMultiLine(client, nameText.getLanguageChooser());		
		
		getSection().setClient(client);
	}
	
	private Project project;
	public void setProject(final Project project) {
		this.project = project;
		
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				nameText.setI18nText(project.getName(), EditMode.DIRECT);
				descriptionText.setI18nText(project.getDescription(), EditMode.DIRECT);
			}
		});
		
	}
}