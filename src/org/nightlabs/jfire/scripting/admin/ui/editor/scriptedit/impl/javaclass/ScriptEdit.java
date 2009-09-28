package org.nightlabs.jfire.scripting.admin.ui.editor.scriptedit.impl.javaclass;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.scripting.Script;
import org.nightlabs.jfire.scripting.admin.ui.editor.scriptedit.AbstractScriptEdit;


/**
 *
 * @author vince
 *
 */
public class ScriptEdit
extends AbstractScriptEdit
{
	protected XComposite wrapper;
    protected Label label;
    protected Text fullyQualifiedClassNameText;
    private Script script;

	@Override
	protected Control _createControl(Composite parent) {
		 GridLayout layout = new GridLayout();
         wrapper = new XComposite(parent, SWT.NONE, LayoutMode.ORDINARY_WRAPPER);
         wrapper.setLayout(layout);
		 label = new Label(wrapper, SWT.NONE);
		 label.setText("Fully qualified class name :");
		 fullyQualifiedClassNameText = new Text(wrapper, SWT.SINGLE  |SWT.BORDER);
		 GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
	     gridData.grabExcessHorizontalSpace = true;
	     fullyQualifiedClassNameText.setLayoutData(gridData);
	     fullyQualifiedClassNameText.setText(getScript().getText());
	     fullyQualifiedClassNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {

				getController().getControllerObject().setText(fullyQualifiedClassNameText.getText());
				getController().markDirty();
			}
		});

	     return wrapper;

	}

}
