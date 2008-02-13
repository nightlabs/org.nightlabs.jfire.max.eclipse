/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.jdoql.editor;

import java.util.Collection;
import java.util.Iterator;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.reporting.ui.ReportingPlugin;

/**
 * @author Alexander Bieber <alex [AT] nightlabs [DOT] de>
 *
 */
public class JDOQLEditor extends EditorPart {

	/**
	 * 
	 */
	public JDOQLEditor() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	private XComposite wrapper;
	private Text jdoql;
	private XComposite buttons;
	private Button execButton;
	private JDOQLParameterTable parameterTable;
	private JDOQLResultTable resultTable;
//	private
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE);
		
		buttons = new XComposite(wrapper, SWT.NONE);
		buttons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		execButton = new Button(buttons, SWT.PUSH);
		execButton.setText("Exec"); //$NON-NLS-1$
		execButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
			public void widgetSelected(SelectionEvent arg0) {
				Collection result;
				try {
					result = ReportingPlugin.getReportManager().execJDOQL(jdoql.getText(), parameterTable.getParameterValues(), new String[] {FetchPlan.ALL});
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				if (result != null) {
					for (Iterator iter = result.iterator(); iter.hasNext();) {
						Object element = iter.next();
						String row = element.toString();
						if (element instanceof Object[]) {
							Object[] elements = (Object[])element;
							row = "["; //$NON-NLS-1$
							for (int i = 0; i < elements.length; i++) {
								row = row + (elements[i] != null ? elements[i].toString() : "null"); //$NON-NLS-1$
								if (i != elements.length-1)
									row = row + ", "; //$NON-NLS-1$
							}
							row = row +"]"; //$NON-NLS-1$
						}
						System.out.println("Result-Row: "+row); //$NON-NLS-1$
						
					}
				}
				resultTable.setInput(result);
			}
		});
		
		jdoql = new Text(wrapper, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		jdoql.setLayoutData(new GridData(GridData.FILL_BOTH));
		parameterTable = new JDOQLParameterTable(wrapper, SWT.NONE);
		resultTable = new JDOQLResultTable(wrapper, SWT.NONE);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
