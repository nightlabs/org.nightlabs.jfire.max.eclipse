/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.parameter.ui.dialog;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.eclipse.ui.dialog.ResizableTrayDialog;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.jfire.reporting.parameter.config.AcquisitionParameterConfig;
import org.nightlabs.jfire.reporting.parameter.config.ReportParameterAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ReportParameterAcquisitionUseCase;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ValueConsumerBinding;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ShowXMLInitialisationDialog extends ResizableTrayDialog {

	private ReportParameterAcquisitionSetup acquisitionSetup;
	private Text output;
	
	/**
	 * @param parentShell
	 */
	public ShowXMLInitialisationDialog(Shell parentShell, ReportParameterAcquisitionSetup acquisitionSetup) {
		super(parentShell, null);
		this.acquisitionSetup = acquisitionSetup;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setSize(400, 500);
	}
	
	protected void addLine(StringBuffer buffer, int indent, String line) {
		for (int i = 0; i < indent; i++) {
			buffer.append("\t"); //$NON-NLS-1$
		}
		buffer.append(line+"\n"); //$NON-NLS-1$
	}
	
	protected void writeI18nText(StringBuffer buffer, int indent, String elementName, I18nText text) {
		for (String lang : text.getLanguageIDs()) {
			addLine(buffer, indent+1, "<"+elementName+" language=\""+lang+"\">"+text.getText(lang)+"</"+elementName+">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		}
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		output = new Text(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		output.setLayoutData(new GridData(GridData.FILL_BOTH));
//		output.setLa
		StringBuffer buffer = new StringBuffer();
		int indent = 2;
		int idNo = 0;
		addLine(buffer, indent, "<parameter-acquisition>"); //$NON-NLS-1$
		Map<Object, Integer> object2IdNo = new HashMap<Object, Integer>();
		for (Map.Entry<ReportParameterAcquisitionUseCase, ValueAcquisitionSetup> entry : acquisitionSetup.getValueAcquisitionSetups().entrySet()) {
			ValueAcquisitionSetup setup = entry.getValue();
			boolean isDefault = acquisitionSetup.getDefaultSetup().equals(setup);
			indent++;
			addLine(buffer, indent, "<use-case id=\""+entry.getKey().getReportParameterAcquisitionUseCaseID()+"\" default=\""+String.valueOf(isDefault)+"\">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			writeI18nText(buffer, indent, "name", entry.getKey().getName()); //$NON-NLS-1$
			writeI18nText(buffer, indent, "description", entry.getKey().getDescription()); //$NON-NLS-1$
			
			// parameters
			indent++;
			addLine(buffer, indent, "<parameters>"); //$NON-NLS-1$
			for (AcquisitionParameterConfig paramConfig : setup.getParameterConfigs()) {
				indent++;
				object2IdNo.put(paramConfig, idNo);
				String paramType = paramConfig.getParameterType().replaceAll("<", "&lt;");
				paramType = paramType.replaceAll(">", "&gt;");
				addLine(buffer, indent,
						"<parameter " + //$NON-NLS-1$
						"id=\""+String.valueOf(idNo)+"\" " + //$NON-NLS-1$ //$NON-NLS-2$
						"name=\""+paramConfig.getParameterID()+"\" " + //$NON-NLS-1$ //$NON-NLS-2$
						"type=\""+paramType+"\" " + //$NON-NLS-1$
						"x=\""+String.valueOf(paramConfig.getX())+"\" " + //$NON-NLS-1$ //$NON-NLS-2$
						"y=\""+String.valueOf(paramConfig.getY())+"\" " + //$NON-NLS-1$ //$NON-NLS-2$
						"/>"); //$NON-NLS-1$
				idNo++;
				indent--;
			}
			addLine(buffer, indent, "</parameters>"); //$NON-NLS-1$
			indent--;
			
			// value provider
			indent++;
			addLine(buffer, indent, "<value-provider-configs>"); //$NON-NLS-1$
			for (ValueProviderConfig providerConfig : setup.getValueProviderConfigs()) {
				indent++;
				object2IdNo.put(providerConfig, idNo);
				String providerID = providerConfig.getValueProviderID().replaceAll("<", "&lt;");
				providerID = providerID.replaceAll(">", "&gt;");
				addLine(buffer, indent, "<provider-config " + //$NON-NLS-1$
						"id=\""+String.valueOf(idNo)+"\" " + //$NON-NLS-1$ //$NON-NLS-2$
						"organisationID=\""+providerConfig.getValueProviderOrganisationID()+"\" " + //$NON-NLS-1$ //$NON-NLS-2$
						"categoryID=\""+providerConfig.getValueProviderCategoryID()+"\" " + //$NON-NLS-1$ //$NON-NLS-2$
						"valueProviderID=\""+providerID+"\" " + //$NON-NLS-1$ //$NON-NLS-2$
						"pageIndex=\""+String.valueOf(providerConfig.getPageIndex())+"\" " + //$NON-NLS-1$ //$NON-NLS-2$
						"pageRow=\""+String.valueOf(providerConfig.getPageRow())+"\" " + //$NON-NLS-1$ //$NON-NLS-2$
						"pageColumn=\""+String.valueOf(providerConfig.getPageColumn())+"\" " + //$NON-NLS-1$ //$NON-NLS-2$
						"allowNullOutputValue=\""+String.valueOf(providerConfig.isAllowNullOutputValue())+"\" " + //$NON-NLS-1$ //$NON-NLS-2$
						"showMessageInHeader=\""+String.valueOf(providerConfig.isShowMessageInHeader())+"\" " + //$NON-NLS-1$ //$NON-NLS-2$
						"growVertically=\""+String.valueOf(providerConfig.isGrowVertically())+"\" " + //$NON-NLS-1$ //$NON-NLS-2$
						"x=\""+String.valueOf(providerConfig.getX())+"\" " + //$NON-NLS-1$ //$NON-NLS-2$
						"y=\""+String.valueOf(providerConfig.getY())+"\" " + //$NON-NLS-1$ //$NON-NLS-2$
						">"); //$NON-NLS-1$
				writeI18nText(buffer, indent, "message", providerConfig.getMessage()); //$NON-NLS-1$
				addLine(buffer, indent, "</provider-config>"); //$NON-NLS-1$
				idNo++;
				indent--;
			}
			addLine(buffer, indent, "</value-provider-configs>"); //$NON-NLS-1$
			indent--;
			
			// value provider
			indent++;
			addLine(buffer, indent, "<value-consumer-bindings>"); //$NON-NLS-1$
			for (ValueConsumerBinding binding : setup.getValueConsumerBindings()) {
				indent++;
				addLine(buffer, indent, "<value-consumer-binding>"); //$NON-NLS-1$
				indent++;
				addLine(buffer, indent, "<binding-provider id=\""+String.valueOf(object2IdNo.get(binding.getProvider()))+"\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
				addLine(buffer, indent, "<binding-parameter name=\""+binding.getParameterID()+"\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
				addLine(buffer, indent, "<binding-consumer id=\""+String.valueOf(object2IdNo.get(binding.getConsumer()))+"\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
				indent--;
				addLine(buffer, indent, "</value-consumer-binding>"); //$NON-NLS-1$
				idNo++;
				indent--;
			}
			addLine(buffer, indent, "</value-consumer-bindings>"); //$NON-NLS-1$
			indent--;
			
			addLine(buffer, indent, "</use-case>"); //$NON-NLS-1$
			indent--;
		}
		addLine(buffer, indent, "</parameter-acquisition>"); //$NON-NLS-1$
		output.setText(buffer.toString());
		return output;
	}
	
	public static void open(ReportParameterAcquisitionSetup setup) {
		ShowXMLInitialisationDialog dlg = new ShowXMLInitialisationDialog(RCPUtil.getActiveShell(), setup);
		dlg.open();
	}
}
