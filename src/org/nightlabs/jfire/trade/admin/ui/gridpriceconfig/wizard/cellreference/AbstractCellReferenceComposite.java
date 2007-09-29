package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.PriceConfigComposite;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jseditor.editor.JSEditorComposite;

public abstract class AbstractCellReferenceComposite extends XComposite{
	
	private boolean enabledFlag = false;
	private Button enabledButton = null;
	
	protected PriceConfigComposite priceConfigComposite = null;
	
	protected JSEditorComposite srcPreviewComposite = null;
	
	public AbstractCellReferenceComposite(Composite parent, int style) {
		this(null, parent, style);
	}
	
	public AbstractCellReferenceComposite(PriceConfigComposite priceConfigComposite, Composite parent, int style) {
		super(parent, style);
		this.priceConfigComposite = priceConfigComposite;
		
		//Enable Flag
		enabledButton = new Button(this, SWT.CHECK);
		enabledButton.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference.AbstractCellReferenceComposite.enabledButton.text")); //$NON-NLS-1$
		enabledButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				checked(!enabledFlag);
			}
		});
		
		getGridLayout().numColumns = 2;
		
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		enabledButton.setLayoutData(gridData);
	}

	protected abstract void createScript();
	protected abstract void doEnable();
	protected abstract void doDisable();
	
	public void checked(boolean enabledFlag) {
		this.enabledFlag = enabledFlag;
		enabledButton.setSelection(enabledFlag);
		
		if(enabledFlag == false)
			doDisable();
		else
			doEnable();
	}

	public boolean isChecked() {
		return enabledFlag;
	}
}
