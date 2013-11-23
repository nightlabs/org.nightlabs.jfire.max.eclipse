package org.nightlabs.jfire.scripting.admin.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.custom.XCombo;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.entity.editor.EntityEditorUtil;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.scripting.Script;
import org.nightlabs.jfire.scripting.dao.ScriptRegistryItemDAO;
import org.nightlabs.progress.ProgressMonitor;

/**
 *
 * @author vince - vince at guinaree dot com
 *
 */
public class ScriptMetaSection
extends ToolBarSectionPart
{
	private ScriptEditorPageController controller;
	private List<String> languages;
	private Script script;

	public ScriptMetaSection(FormPage page, Composite parent, final ScriptEditorPageController controller){
		super(page, parent,	ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE, "Meta");
		this.controller = controller;
		getSection().setExpanded(true);
		createClient(getSection(), page.getEditor().getToolkit());
	}

	@Override
	public boolean setFormInput(Object input) {
		this.script = (Script) input;
		return super.setFormInput(input);
	}

	private boolean ignoreModifyEvents = false;

	@Override
	public void refresh() {
		ignoreModifyEvents = true;
		try {
			if (script == null)
				return; // data not yet loaded => silently ignore

			// put data from this.script into UI
			resultClassNameText.setText(script.getResultClassName());
			selectLanguage();

			super.refresh();
		} finally {
			ignoreModifyEvents = false;
		}
	}

	private void selectLanguage()
	{
		// ensure that this is executed on the UI thread.
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Thread mismatch! selectLanguage() executed on non-SWT-UI-thread!");

		if (languages == null)
			return; // languages not yet loaded from the server (done asynchronously in a job)

		if (script == null)
			return; // languages are loaded, but no entity to be edited - thus, there is no currently selected language

		int idx = languages.indexOf(script.getLanguage());
		languageCombo.select(idx);
	}

	@Override
	public void commit(boolean onSave) {
		// write UI data into the edited object, i.e. 'this.script'
		script.setResultClassName(resultClassNameText.getText());
		controller.fireModifyEvent(null, script, false);

		super.commit(onSave);
	}

	private XCombo languageCombo;

	protected void createClient(Section section, FormToolkit toolkit) {
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite container = EntityEditorUtil.createCompositeClient(toolkit, section, 1);

		createLanguageLabel(container,1);
		languageCombo = new XCombo(container, SWT.READ_ONLY);
		languageCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		// Add a selectionListener to this combo and mark the editor dirty when the value's changed.
		languageCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (ignoreModifyEvents)
					return;

				script.setLanguage(languageCombo.getText());
				markDirty();
			}
		});

		languageCombo.add(null, "Loading...");
		languageCombo.select(0);
		languageCombo.setEnabled(false);

		final Display display = languageCombo.getDisplay();
		Job loadLanguagesJob = new Job("Loading languages") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception
			{
				// Load data an a Job's thread (NOT UI thread).
				final List<String> l = new ArrayList<String>(
						ScriptRegistryItemDAO.sharedInstance().getlanguages()
				);

				// Get back to the UI thread to display the data.
				display.asyncExec(new Runnable() {
					public void run() {
						ignoreModifyEvents = true;
						try {
							languages = l;

							// on the UI thread
							languageCombo.removeAll();
							for (String language : languages) {
								languageCombo.add(null, language);
							}
							languageCombo.setEnabled(true);

							selectLanguage();
						} finally {
							ignoreModifyEvents = false;
						}
					}
				});
				return Status.OK_STATUS;
			}
		};
		loadLanguagesJob.setPriority(Job.SHORT);
		loadLanguagesJob.schedule();

		createResultLabel(container, 1);
		createResultClassNameText(container);
	}


	private Text resultClassNameText;
	private void createResultClassNameText(Composite container){
		resultClassNameText = new Text(container, XComposite.getBorderStyle(container));

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		resultClassNameText.setLayoutData(gd);
		resultClassNameText.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent e) {
				if (ignoreModifyEvents)
					return;

				markDirty();
			}
		});
	}
	private void createLanguageLabel(Composite container, int span){
		Label label=new Label(container, SWT.LEFT);
		label.setText("Language:");

		GridData grid=new GridData(GridData.FILL_HORIZONTAL);
		grid.horizontalSpan=span;
		label.setLayoutData(grid);
	}
	private void createResultLabel(Composite container, int span){

		Label label =new Label(container, SWT.LEFT);
		label.setText("Result type:");

		GridData gd=new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan=span;
		label.setLayoutData(gd);
	}

}
