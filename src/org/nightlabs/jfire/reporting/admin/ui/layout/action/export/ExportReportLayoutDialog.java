/**
 *
 */
package org.nightlabs.jfire.reporting.admin.ui.layout.action.export;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;

import javax.jdo.FetchPlan;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.FileSelectionComposite;
import org.nightlabs.base.ui.composite.LabeledText;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.eclipse.ui.dialog.ResizableTrayDialog;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.jfire.reporting.ReportingInitialiser;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.l10n.ReportLayoutL10nUtil;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.l10n.ReportLayoutL10nUtil.PreparedLayoutL10nData;
import org.nightlabs.jfire.reporting.admin.ui.resource.Messages;
import org.nightlabs.jfire.reporting.dao.ReportRegistryItemDAO;
import org.nightlabs.jfire.reporting.layout.ReportLayoutLocalisationData;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.parameter.config.AcquisitionParameterConfig;
import org.nightlabs.jfire.reporting.parameter.config.ReportParameterAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ReportParameterAcquisitionUseCase;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ValueConsumerBinding;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.dao.ReportParameterAcquisitionSetupDAO;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.IOUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Dialog to export a layout as needed for the initialisation in the server.
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ExportReportLayoutDialog extends ResizableTrayDialog {

	private XComposite wrapper;
	private FileSelectionComposite folderComposite;
	private LabeledText layoutFileName;
	private ReportRegistryItemID layoutID;

	private Button needZipButton;

	private static String ZIP_SUFFIX = ".zip";
	private static String REPORT_LAYOUT_SUFFIX = ".rptdesign";

	private ReportParameterAcquisitionSetup parameterSetup;
	/**
	 * @param parentShell
	 */
	public ExportReportLayoutDialog(Shell parentShell, ReportRegistryItemID layoutID ) {
		super(parentShell, null);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.layoutID = layoutID;

//		Job job = new Job("Loading Report Registry Item...") {
//			@Override
//			protected IStatus run(ProgressMonitor monitor) throws Exception {
//				
//				return Status.OK_STATUS;
//			}
//		};
//		job.schedule();

		Job job = new Job("Loading Data...") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				ReportRegistryItemDAO reportRegistryItemDAO = ReportRegistryItemDAO.sharedInstance();
				reportRegistryItem = reportRegistryItemDAO.getReportRegistryItem(
						ExportReportLayoutDialog.this.layoutID, 
						new String[] {FetchPlan.DEFAULT, 
								ReportRegistryItem.FETCH_GROUP_NAME, 
								ReportRegistryItem.FETCH_GROUP_DESCRIPTION, 
								ReportRegistryItem.FETCH_GROUP_PARENT_CATEGORY}, 
								monitor);
				
				ReportParameterAcquisitionSetupDAO parameterSetupDAO = ReportParameterAcquisitionSetupDAO.sharedInstance();
				parameterSetup = parameterSetupDAO.getSetupForReportLayout(
						ExportReportLayoutDialog.this.layoutID, 
						new String[] {FetchPlan.DEFAULT, 
								ReportParameterAcquisitionSetup.FETCH_GROUP_VALUE_ACQUISITION_SETUPS,
								ReportParameterAcquisitionSetup.FETCH_GROUP_DEFAULT_USE_CASE,
								ReportParameterAcquisitionUseCase.FETCH_GROUP_NAME,
								ReportParameterAcquisitionUseCase.FETCH_GROUP_DESCRIPTION,
								ValueAcquisitionSetup.FETCH_GROUP_VALUE_CONSUMER_BINDINGS,
								ValueAcquisitionSetup.FETCH_GROUP_VALUE_PROVIDER_CONFIGS,
								ValueAcquisitionSetup.FETCH_GROUP_PARAMETER_CONFIGS,
								ValueProviderConfig.FETCH_GROUP_MESSAGE,
								ValueConsumerBinding.FETCH_GROUP_CONSUMER,
								ValueConsumerBinding.FETCH_GROUP_PROVIDER}, 
								monitor);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.action.export.ExportReportLayoutDialog.window.title")); //$NON-NLS-1$
		newShell.setSize(400, 400);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE);
		layoutFileName = new LabeledText(wrapper, Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.action.export.ExportReportLayoutDialog.label.selectExportFileName")); //$NON-NLS-1$
		layoutFileName.setText(layoutID.reportRegistryItemID /*+ ZIP_SUFFIX*/);
		folderComposite = new FileSelectionComposite(
				wrapper,
				SWT.NONE, FileSelectionComposite.OPEN_DIR,
				Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.action.export.ExportReportLayoutDialog.label.selectExportFolder"), //$NON-NLS-1$
				Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.action.export.ExportReportLayoutDialog.label.selectFolder")); //$NON-NLS-1$

		needZipButton = new Button(wrapper, SWT.CHECK);
		needZipButton.setText("Export in Zip file");
		needZipButton.setSelection(true);

		//		needZipButton.addSelectionListener(new SelectionAdapter() {
		//			@Override
		//			public void widgetSelected(SelectionEvent e) {
		//				if (needZipButton.getSelection() == true) {
		//					layoutFileName.setText(layoutID.reportRegistryItemID + ZIP_SUFFIX);
		//				}
		//				else {
		//					layoutFileName.setText(layoutID.reportRegistryItemID + REPORT_LAYOUT_SUFFIX);
		//				}
		//			}
		//		});

		return wrapper;
	}

	private ReportRegistryItem reportRegistryItem;
	@Override
	protected void okPressed() {
		ReportLayoutExportInput editorInput = new ReportLayoutExportInput(layoutID);

		String parentName = folderComposite.getFileText();
		if (needZipButton.getSelection() == true) {
			try {
				parentName = IOUtil.createUserTempDir("jfire_report.exported.", "." + layoutFileName.getText()).getPath();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		String reportID = null;
		if (parentName != null) {
			//Report File (.rptdesign)
			File exportFile = new File(parentName, layoutFileName.getText() + REPORT_LAYOUT_SUFFIX);
			reportID = IOUtil.getFileNameWithoutExtension(exportFile.getName());
			try {
				ReportingInitialiser.exportLayoutToTemplateFile(editorInput.getFile(), exportFile);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			//Resource Files(.properties)
			PreparedLayoutL10nData l10nData = ReportLayoutL10nUtil.prepareReportLayoutL10nData(editorInput);
			File resourceFolder = new File(parentName, "resource"); //$NON-NLS-1$
			resourceFolder.mkdirs();
			for (ReportLayoutLocalisationData data : l10nData.getLocalisationBundle().values()) {
				String l10nFileName = reportID;
				if ("".equals(data.getLocale())) //$NON-NLS-1$
					l10nFileName = l10nFileName + ".properties"; //$NON-NLS-1$
				else
					l10nFileName = l10nFileName + "_" + data.getLocale() + ".properties";  //$NON-NLS-1$ //$NON-NLS-2$

				try {
					File dataFile = new File(resourceFolder, l10nFileName);
					dataFile.createNewFile();
					InputStream in = data.createLocalisationDataInputStream();
					BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dataFile));
					try {
						IOUtil.transferStreamData(in, out);
					} finally {
						in.close();
						out.close();
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			//Generates descriptor file(content.xml)
			File descriptorFile = new File(parentName, "content.xml");
			try {
				descriptorFile.createNewFile();

				//Create document
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.newDocument();

				/*****ReportCategory-node*****/
				Element reportCategory = doc.createElement("report-category");
				reportCategory.setAttribute("id", reportRegistryItem.getParentCategoryID().reportRegistryItemID);
				reportCategory.setAttribute("type", layoutID.reportRegistryItemType);
				doc.appendChild(reportCategory);

				//Category-names
				generateI18nElements(doc, reportCategory, "name", reportRegistryItem.getParentCategory().getName());

				//Report
				Element report = doc.createElement("report");
				report.setAttribute("file", exportFile.getName());
				report.setAttribute("id", layoutID.reportRegistryItemID);
				report.setAttribute("engineType", "BIRT");
				report.setAttribute("overwriteOnInit", "true"); //Has to overwrite the old file
				reportCategory.appendChild(report);

				//Report-names
				generateI18nElements(doc, report, "name", reportRegistryItem.getName());

				//Report-descriptions
				generateI18nElements(doc, report, "description", reportRegistryItem.getDescription());

				//Parameter-acquisition
				Element parameterAcquisition = doc.createElement("parameter-acquisition");
				report.appendChild(parameterAcquisition);

				//Use-case
				Map<ReportParameterAcquisitionUseCase, ValueAcquisitionSetup> valueAcquisitionSetups = 
					parameterSetup.getValueAcquisitionSetups();
				for (Entry<ReportParameterAcquisitionUseCase, ValueAcquisitionSetup> setup : valueAcquisitionSetups.entrySet()) {
					Element useCase = doc.createElement("use-case");
					useCase.setAttribute("id", setup.getKey().getReportParameterAcquisitionUseCaseID());
					useCase.setAttribute("default", "true");

					generateI18nElements(doc, useCase, "name", setup.getKey().getName());
					generateI18nElements(doc, useCase, "description", setup.getKey().getDescription());

					//Parameters
					Element parameters = doc.createElement("parameters");
					useCase.appendChild(parameters);

					int idx = 0;
					for (AcquisitionParameterConfig parameterConfig : setup.getValue().getParameterConfigs()) {
						Element parameter = doc.createElement("parameter");
						parameter.setAttribute("id", Integer.toString(idx++));
						parameter.setAttribute("name", parameterConfig.getParameterID());
						parameter.setAttribute("type", parameterConfig.getParameterType());
						parameter.setAttribute("x", Integer.toString(parameterConfig.getX()));
						parameter.setAttribute("y", Integer.toString(parameterConfig.getY()));
						
						parameters.appendChild(parameter);
					}

					//Value-provider-configs
					Element valueProviderConfigs = doc.createElement("value-provider-configs");
					useCase.appendChild(valueProviderConfigs);

					idx = 0;
					for (ValueProviderConfig valueProviderConfig : setup.getValue().getValueProviderConfigs()) {
						Element providerConfig = doc.createElement("provider-config");
						providerConfig.setAttribute("id", Integer.toString(idx++));
						providerConfig.setAttribute("organisationID", valueProviderConfig.getOrganisationID());
						providerConfig.setAttribute("categoryID", valueProviderConfig.getValueProviderCategoryID());
						providerConfig.setAttribute("valueProviderID", valueProviderConfig.getValueProviderID());
						providerConfig.setAttribute("pageIndex", Integer.toString(valueProviderConfig.getPageIndex()));
						providerConfig.setAttribute("pageRow", Integer.toString(valueProviderConfig.getPageRow()));
						providerConfig.setAttribute("pageColumn", Integer.toString(valueProviderConfig.getPageColumn()));
						providerConfig.setAttribute("allowNullOutputValue", Boolean.toString(valueProviderConfig.isAllowNullOutputValue()));
						providerConfig.setAttribute("showMessageInHeader", Boolean.toString(valueProviderConfig.isShowMessageInHeader()));
						providerConfig.setAttribute("growVertically", Boolean.toString(valueProviderConfig.isGrowVertically()));
						providerConfig.setAttribute("x", Integer.toString(valueProviderConfig.getX()));
						providerConfig.setAttribute("y", Integer.toString(valueProviderConfig.getY()));
						
						generateI18nElements(doc, providerConfig, "message", valueProviderConfig.getMessage());
						
						valueProviderConfigs.appendChild(providerConfig);
					}
					
					//Value-consumer-bindings
					Element valueConsumerBindings = doc.createElement("value-consumer-bindings");
					useCase.appendChild(valueConsumerBindings);

					for (ValueConsumerBinding valueConsumerBinding : setup.getValue().getValueConsumerBindings()) {
						Element consumerBinding = doc.createElement("value-consumer-binding");
						
						Element bindingProvider = doc.createElement("binding-provider"); //FIXME
						bindingProvider.setAttribute("id", Long.toString(valueConsumerBinding.getProvider().getValueProviderConfigID()));
						
						Element bindingParameter = doc.createElement("binding-parameter");
						bindingParameter.setAttribute("name", valueConsumerBinding.getParameterID());
						
						Element bindingConsumer = doc.createElement("binding-consumer"); //FIXME
						bindingConsumer.setAttribute("id", Long.toString(valueConsumerBinding.getValueConsumerBindingID()));
						
						consumerBinding.appendChild(bindingProvider);
						consumerBinding.appendChild(bindingParameter);
						consumerBinding.appendChild(bindingConsumer);
						
						valueConsumerBindings.appendChild(consumerBinding);
					}

					parameterAcquisition.appendChild(useCase);	
				}

				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
				transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://www.nightlabs.de/dtd/reporting-initialiser-content_0_5.dtd");
				transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,"-//NightLabs//Reporting Initialiser DTD V 0.5//EN"); 

				//Write the XML document to a file
				//initialize StreamResult with File object to save to file
				StreamResult result = new StreamResult(new StringWriter());
				DOMSource source = new DOMSource(doc);
				transformer.transform(source, result);

				String xmlString = result.getWriter().toString();

				IOUtil.writeTextFile(descriptorFile, xmlString);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			if (needZipButton.getSelection() == true) {
				File outputFilePath = new File(folderComposite.getFile(), layoutFileName.getText() + ZIP_SUFFIX);
				try {
					IOUtil.zipFolder(outputFilePath, IOUtil.getUserTempDir("jfire_report.exported.", "." + layoutFileName.getText()));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		super.okPressed();
	}

	private void generateI18nElements(Document document, Element parentElement, String elementName, I18nText i18nText) {
		for (Entry<String, String> entry : i18nText.getTexts()) {
			Element element = document.createElement(elementName);
			element.setAttribute("language", entry.getKey());
			element.setTextContent(entry.getValue());

			parentElement.appendChild(element);
		}
	}
}
