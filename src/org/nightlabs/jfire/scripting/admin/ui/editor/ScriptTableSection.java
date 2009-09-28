package org.nightlabs.jfire.scripting.admin.ui.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.entity.editor.EntityEditorUtil;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.scripting.IScriptParameter;
import org.nightlabs.jfire.scripting.Script;
import org.nightlabs.jfire.scripting.ScriptParameter;
import org.nightlabs.jfire.scripting.ScriptParameterSet;
import org.nightlabs.jfire.scripting.admin.ui.ScriptingAdminPlugin;
import org.nightlabs.jfire.scripting.admin.ui.resource.Messages;
import org.nightlabs.jfire.scripting.ui.ModificationListener;
import org.nightlabs.jfire.scripting.ui.ModifyListenerEvent;
import org.nightlabs.jfire.scripting.ui.ScriptParameterTable;
import org.nightlabs.jfire.scripting.ui.ScriptParameterTableOption;


/**
 *
 * @author vince
 *
 */
public class ScriptTableSection
extends ToolBarSectionPart
{

	//	ScriptEditorPageController controller;
	private String selectedParamID;
	private String secondParamId;
	private CreateParameterAction createAction;
	private DeleteParameterAction deleteAction;
	private EditParameterAction editAction;
	private DecreaseOrderNumberAction previousParameterAction;
	private IncreaseOrderNumberAction nextParameterAction;

	private Script script;

	private ScriptParameterTable scriptParameterTable;
	public ScriptTableSection(FormPage page, Composite parent, ScriptEditorPageController controller){
		super(page,parent,ExpandableComposite.EXPANDED
				| ExpandableComposite.TITLE_BAR
				| ExpandableComposite.TWISTIE ,
				Messages.getString("org.nightlabs.jfire.scripting.admin.ui.editor.ScriptTableSection.sectionTitle"));
		//	this.controller=controller;
		getSection().setExpanded(true);

		createClient(getSection(), page.getEditor().getToolkit());

		previousParameterAction= new DecreaseOrderNumberAction();
		nextParameterAction =new IncreaseOrderNumberAction();
		createAction = new CreateParameterAction();
		deleteAction = new DeleteParameterAction();
		editAction = new EditParameterAction();

		getToolBarManager().add(createAction);
		getToolBarManager().add(deleteAction);
		getToolBarManager().add(editAction);
		getToolBarManager().add(previousParameterAction);
		getToolBarManager().add(nextParameterAction);




		updateToolBarManager();
	}

	@Override
	public boolean setFormInput(Object input) {
		this.script = (Script) input;

		scriptParameterTable.setInput(script.getParameterSet());

		return super.setFormInput(input);
	}

	@Override
	public void refresh() {
		if (script == null)
			return; // data not yet loaded => silently ignore

		scriptParameterTable.refresh();

		super.refresh();
	}



	protected void createClient(Section section, FormToolkit toolkit){
		//		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Composite container = EntityEditorUtil.createCompositeClient(toolkit, section, 1);

		XComposite composite=new XComposite(container, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		scriptParameterTable = new ScriptParameterTable(composite, SWT.NONE, ScriptParameterTableOption.editable);
		scriptParameterTable.setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof ScriptParameter && e2 instanceof ScriptParameter) {
					return new Integer(((ScriptParameter) e1).getOrderNumber()).compareTo(((ScriptParameter) e2).getOrderNumber());
				}

				return 0;
			}
		});


          scriptParameterTable.addModificationListener(new ModificationListener() {

			@Override
			public void ModifyTextListener(ModifyListenerEvent event) {
				// TODO Auto-generated method stub
                 markDirty();
			}
		});



	}
	class DecreaseOrderNumberAction extends Action{

		public DecreaseOrderNumberAction(){
			super();
			setId(DecreaseOrderNumberAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(ScriptingAdminPlugin.getDefault(),ScriptTableSection.class,"Up"));
			setToolTipText(Messages.getString("org.nightlabs.jfire.scripting.admin.ui.editor.ScriptTableSection.PreviousParameterAction.toolTipText"));
			setText(Messages.getString("org.nightlabs.jfire.scripting.admin.ui.editor.ScriptTableSection.PreviousParameterAction.text"));
		}

		@Override
		public void run(){

			ScriptParameter selectedScriptParameter = scriptParameterTable.getFirstSelectedElement();
			if(selectedScriptParameter != null){
				selectedParamID = selectedScriptParameter.getScriptParameterID();

				ScriptParameterSet parameterSet = script.getParameterSet();
				SortedSet<IScriptParameter>  sortedParameters = parameterSet.getSortedParameters();

				if(selectedScriptParameter.getOrderNumber() == 0){
					return;
				}

				List<IScriptParameter> parameterList=new ArrayList<IScriptParameter>(sortedParameters);
				for(IScriptParameter parameter : parameterList){
					if(selectedParamID.equals(parameter.getScriptParameterID())){
						int firstOrderNum = parameter.getOrderNumber();
						int secondOrderNum = --firstOrderNum;
						secondParamId = parameterList.get(secondOrderNum).getScriptParameterID();
						parameterSet.swapParameters(selectedParamID, secondParamId);
					}
				}

				scriptParameterTable.setInput(parameterSet);
				markDirty();
			}

		}
	}


	class IncreaseOrderNumberAction extends Action {

		public IncreaseOrderNumberAction(){
			super();
			setId(IncreaseOrderNumberAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(ScriptingAdminPlugin.getDefault(),ScriptTableSection.class,"Down"));
			setToolTipText(Messages.getString("org.nightlabs.jfire.scripting.admin.ui.editor.ScriptTableSection.NextParameterAction.toolTipText"));
			setText(Messages.getString("org.nightlabs.jfire.scripting.admin.ui.editor.ScriptTableSection.NextParameterAction.text"));
		}

		@Override
		public void run() {

			ScriptParameter selectedScriptParameter = scriptParameterTable.getFirstSelectedElement();
			if(selectedScriptParameter != null){
				selectedParamID = selectedScriptParameter.getScriptParameterID();

				ScriptParameterSet parameterSet = script.getParameterSet();
				SortedSet<IScriptParameter>  sortedParameters = parameterSet.getSortedParameters();


				List<IScriptParameter> parameterList=new ArrayList<IScriptParameter>(sortedParameters);


				if(selectedScriptParameter.getOrderNumber() == parameterList.size()-1){
					return;
				}

				for(IScriptParameter parameter : parameterList){
					if(selectedParamID.equals(parameter.getScriptParameterID())){
						int firstOrderNum = parameter.getOrderNumber();
						int secondOrderNum = ++firstOrderNum;
						secondParamId = parameterList.get(secondOrderNum).getScriptParameterID();
						parameterSet.swapParameters(selectedParamID, secondParamId);
					}
				}

				scriptParameterTable.setInput(parameterSet);
				markDirty();
			}


		}

	}

	class  CreateParameterAction extends Action{
		public CreateParameterAction(){
			super();
			setId(CreateParameterAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					ScriptingAdminPlugin.getDefault(),
					ScriptTableSection.class,
					"Create"));
			setToolTipText(Messages.getString("org.nightlabs.jfire.scripting.admin.ui.editor.ScriptTableSection.CreateParameterAction.toolTipText"));
			setText(Messages.getString("org.nightlabs.jfire.scripting.admin.ui.editor.ScriptTableSection.CreateParameterAction.text"));
		}

		@Override
		public void run() {


			ScriptParameterWizard wizard=new ScriptParameterWizard(false,script);
			try {

				DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(getSection().getShell(), wizard);
				int result = dialog.open();
				if(result == Dialog.OK) {

					scriptParameterTable.setInput(script.getParameterSet());
					scriptParameterTable.refresh(true);
					markDirty();
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	class DeleteParameterAction extends Action{
		public DeleteParameterAction(){
			super();
			setId(DeleteParameterAction .class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					ScriptingAdminPlugin.getDefault(),
					ScriptTableSection.class,
			"Delete")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.scripting.admin.ui.editor.ScriptTableSection.DeleteParameterAction.toolTipText"));
			setText(Messages.getString("org.nightlabs.jfire.scripting.admin.ui.editor.ScriptTableSection.DeleteParameterAction.text"));


		}

		@Override
		public void run() {
			boolean confirm= MessageDialog.openConfirm(getSection().getShell(),Messages.getString("org.nightlabs.jfire.scripting.admin.ui.editor.ScriptTableSection.DeleteParameterAction.dialog.confirmDelete.title"),Messages.getString("org.nightlabs.jfire.scripting.admin.ui.editor.ScriptTableSection.DeleteParameterAction.dialog.confirmDelete.description"));

			if(confirm) {



				script.getParameterSet().removeParameter(scriptParameterTable.getFirstSelectedElement().getScriptParameterID());

				scriptParameterTable.refresh(true);
				markDirty();
			}
		}
	}

	class EditParameterAction
	extends Action {
		public EditParameterAction() {
			super();
			setId(EditParameterAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					ScriptingAdminPlugin.getDefault(),
					ScriptTableSection.class,
			"Edit")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.scripting.admin.ui.editor.ScriptTableSection.EditParameterAction.toolTipText"));
			setText(Messages.getString("org.nightlabs.jfire.scripting.admin.ui.editor.ScriptTableSection.EditParameterAction.text"));
		}

		@Override
		public void run() {

			ScriptParameter parameter=scriptParameterTable.getFirstSelectedElement();
			ScriptEditWizard wizard = new ScriptEditWizard(parameter, false,null);

			try {

				DynamicPathWizardDialog dialog =new DynamicPathWizardDialog(getSection().getShell(), wizard);
				if(dialog.open() == Dialog.OK) {

					scriptParameterTable.refresh(true);
					markDirty();
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}


}