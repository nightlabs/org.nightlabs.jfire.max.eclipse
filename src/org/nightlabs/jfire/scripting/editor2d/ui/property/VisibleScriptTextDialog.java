/**
 * 
 */
package org.nightlabs.jfire.scripting.editor2d.ui.property;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.dialog.CenteredTitleDialog;
import org.nightlabs.jfire.scripting.condition.Script;
import org.nightlabs.jseditor.ui.editor.JSEditorComposite;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class VisibleScriptTextDialog 
//extends CenteredDialog 
extends CenteredTitleDialog
{
	private Script script;
	private JSEditorComposite jsEditorComposite;
 
	/**
	 * @param parentShell
	 */
	public VisibleScriptTextDialog(Shell parentShell, Script script) {
		super(parentShell);
		this.script = script;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	protected void evaluateScript() 
	{
//		if (getScript() != null) {
//			ScriptRegistry scriptRegistry = ScriptRegistryDAO.sharedInstance().getScriptRegistry(
//					new String[] {ScriptRegistry.FETCH_GROUP_THIS_SCRIPT_REGISTRY},
//					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
//			List<IScript> scripts = new ArrayList<IScript>();
//			List<Map<String, Object>> parameters = new ArrayList<Map<String,Object>>();
//			// TODO: set right parameters
//			parameters.add(new HashMap<String, Object>());
//			scripts.add(getScript());
//			try {
//				scriptRegistry.executeScripts(scripts, parameters);
//			} catch (Exception e) {
//				setErrorMessage("The script is wrong!");
//				throw new RuntimeException(e);
//			}			
//		}
	}
	
	@Override
	protected Control createDialogArea(Composite parent) 
	{
		setTitle("Edit visible script");
		setMessage("Here you can edit the visible script");
		
		Composite wrapper = new XComposite(parent, SWT.NONE);
		jsEditorComposite = new JSEditorComposite(wrapper, null, SWT.BORDER);
		jsEditorComposite.getDocument().addDocumentListener(new IDocumentListener(){
			@Override
			public void documentChanged(DocumentEvent event) {
				evaluateScript();
			}
			@Override
			public void documentAboutToBeChanged(DocumentEvent event) {
				
			}
		});
		if (script != null && script.getText() != null)
			jsEditorComposite.setDocumentText(script.getText());
		
		return wrapper;
	}
	
	/**
	 * Return the script.
	 * @return the script
	 */
	public Script getScript() {
		return script;
	}

	@Override
	protected void okPressed() {
		script.setText(jsEditorComposite.getDocumentText());
		super.okPressed();
	}

	public static final int ID_DELETE_SCRIPT = 123456;
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		Button deleteScriptButton = createButton(
				parent, ID_DELETE_SCRIPT,
				"Delete Script", 
				false);
		deleteScriptButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setReturnCode(ID_DELETE_SCRIPT);
				close();
			}
		});
	}
	
	@Override
	public void create() {
		super.create();
		getShell().setText("Edit Visible Script");
		getShell().setSize(500, 350);
		getShell().setMinimumSize(400, 300);
	}
}
