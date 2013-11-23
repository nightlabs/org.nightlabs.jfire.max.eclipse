package org.nightlabs.jfire.reporting.admin.ui.layout.editor.xml;

import org.apache.log4j.Logger;
import org.eclipse.birt.report.designer.ui.editors.pages.ReportXMLSourceEditorFormPage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PartInitException;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.JFireRemoteReportEditorInput;

/**
 * @author sschefczyk
 *
 */
public class JFireReportXMLSourceEditorFormPage 
extends ReportXMLSourceEditorFormPage {

	private static final Logger logger = Logger.getLogger(JFireReportXMLSourceEditorFormPage.class);

	public static final String ID_PAGE = JFireReportXMLSourceEditorFormPage.class.getName();

	private JFireRemoteReportEditorInput editorInput;

	@Override
	public void init(IEditorSite site, IEditorInput input)
	throws PartInitException {
		super.init(site, input);
		if (input instanceof JFireRemoteReportEditorInput) {
			this.editorInput = (JFireRemoteReportEditorInput) input;
			logger.info("init with input: " + input);
		} else
			throw new IllegalArgumentException("input NOT instanceof JFireRemoteReportEditorInput!");
	}

	@Override
	public void saveState(IMemento memento) {
		logger.info("saveState:");
		super.saveState(memento);
	}
	
	@Override
	public IEditorInput getEditorInput() {
		IEditorInput editorInput = super.getEditorInput();
		logger.info("getEditorInput() " + editorInput);
		return editorInput;
	}
	
	@Override
	public boolean canLeaveThePage() {
		return super.canLeaveThePage();
	}

	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		if (input instanceof JFireRemoteReportEditorInput) {
			super.doSetInput(input);	
		} else {
			// we only accept JFireRemoteReportEditorInput
		}
	}
	
	@Override
	public void doSave(IProgressMonitor progressMonitor) 
	{
		//store locally as file in jfire-client-runtime:
		logger.info("doSave:");
		super.doSave(progressMonitor);
		//store remote-side:
		JFireRemoteReportEditorInput.saveRemoteLayout(editorInput, progressMonitor);
	}
}
