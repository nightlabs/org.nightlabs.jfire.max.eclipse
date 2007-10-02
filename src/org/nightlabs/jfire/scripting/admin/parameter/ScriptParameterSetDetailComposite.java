/**
 * 
 */
package org.nightlabs.jfire.scripting.admin.parameter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.jfire.scripting.ui.ScriptParameterTable;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class ScriptParameterSetDetailComposite extends XComposite {

	private XComposite wrapper;
	private I18nTextEditor nameEditor;
	private ScriptParameterTable parameterTable;
	
	
	/**
	 * @param parent
	 * @param style
	 */
	public ScriptParameterSetDetailComposite(Composite parent, int style) {
		super(parent, style);
		init();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 */
	public ScriptParameterSetDetailComposite(Composite parent, int style,
			LayoutMode layoutMode) {
		super(parent, style, layoutMode);
		init();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutDataMode
	 */
	public ScriptParameterSetDetailComposite(Composite parent, int style,
			LayoutDataMode layoutDataMode) {
		super(parent, style, layoutDataMode);
		init();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public ScriptParameterSetDetailComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
		init();
	}
	
	private void init() {
		wrapper = new XComposite(this, SWT.NONE, XComposite.LayoutMode.ORDINARY_WRAPPER);
		nameEditor = new I18nTextEditor(wrapper, "ParameterSet name:");
		parameterTable = new ScriptParameterTable(wrapper, SWT.NONE);
	}

}
