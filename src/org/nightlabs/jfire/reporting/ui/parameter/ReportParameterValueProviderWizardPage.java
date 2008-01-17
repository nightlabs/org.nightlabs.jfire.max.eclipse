/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.parameter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageDimension;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.reporting.parameter.config.AcquisitionParameterConfig;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ValueConsumerBinding;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;
import org.nightlabs.jfire.reporting.ui.ReportingPlugin;
import org.nightlabs.jfire.reporting.ui.resource.Messages;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportParameterValueProviderWizardPage extends WizardHopPage {

	private SortedMap<Integer, SortedSet<ValueProviderConfig>> pageProviderConfigs;
	private boolean contentsCreated = false;
	private ValueAcquisitionSetup valueAcquisitionSetup;
	private IReportParameterController parameterController;
	private Map<ValueProviderID, IValueProviderGUI<?>> valueProviderGUIs = new HashMap<ValueProviderID, IValueProviderGUI<?>>();
	private Set<IValueProviderGUIListener> valueProviderGUIListeners = new HashSet<IValueProviderGUIListener>();
	
	/**
	 * @param pageName
	 */
	public ReportParameterValueProviderWizardPage(
			String pageName, 
			ValueAcquisitionSetup valueAcquisitionSetup, 
			SortedMap<Integer, SortedSet<ValueProviderConfig>> pageProviderConfigs,
			IReportParameterController parameterController
		) 
	{
		super(pageName);
		init(pageProviderConfigs, valueAcquisitionSetup, parameterController);
	}

	/**
	 * @param pageName
	 * @param title
	 */
	public ReportParameterValueProviderWizardPage(String pageName, String title, ValueAcquisitionSetup valueAcquisitionSetup, SortedMap<Integer, SortedSet<ValueProviderConfig>> pageProviderConfigs, IReportParameterController parameterController) {
		super(pageName, title);
		init(pageProviderConfigs, valueAcquisitionSetup, parameterController);
	}

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public ReportParameterValueProviderWizardPage(
			String pageName, String title,
			ImageDescriptor titleImage, 
			ValueAcquisitionSetup valueAcquisitionSetup,
			SortedMap<Integer, SortedSet<ValueProviderConfig>> pageProviderConfigs, 
			IReportParameterController parameterController 
		) 
	{
		super(pageName, title, titleImage);
		init(pageProviderConfigs, valueAcquisitionSetup, parameterController);
		if (titleImage != null)
			setImageDescriptor(titleImage);
	}
	
	protected void init(SortedMap<Integer, SortedSet<ValueProviderConfig>> pageProviderConfigs, ValueAcquisitionSetup valueAcquisitionSetup, IReportParameterController parameterController)  {
		setImageDescriptor(SharedImages.getSharedImageDescriptor(ReportingPlugin.getDefault(), ReportParameterValueProviderWizardPage.class, null, ImageDimension._75x70));
		this.pageProviderConfigs = pageProviderConfigs;
		this.valueAcquisitionSetup = valueAcquisitionSetup; 
		this.parameterController = parameterController;
	}

	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		XComposite wrapper = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		setTitle(Messages.getString("org.nightlabs.jfire.reporting.ui.parameter.ReportParameterValueProviderWizardPage.title")); //$NON-NLS-1$
		String description = ""; //$NON-NLS-1$
		// iterate throw the rows
		for (SortedSet<ValueProviderConfig> configRow : pageProviderConfigs.values()) {
			// each row gets its own grid-layout-composite
			XComposite rowWrapper = new XComposite(wrapper, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
			rowWrapper.getGridLayout().numColumns = configRow.size();
			rowWrapper.getGridLayout().makeColumnsEqualWidth = true;
			// now create the contents of this row
			for (final ValueProviderConfig config : configRow) {
				
				final ValueProviderID providerID = ValueProviderID.create(
						config.getValueProviderOrganisationID(), 
						config.getValueProviderCategoryID(), 
						config.getValueProviderID()
				);
				IValueProviderGUIFactory factory = ValueProviderGUIRegistry.sharedInstance().getValueProviderGUIFactory(providerID);
				if (factory == null) {			
					Label configLabel = new Label(rowWrapper, SWT.WRAP);
					configLabel.setText(Messages.getString("org.nightlabs.jfire.reporting.ui.parameter.ReportParameterValueProviderWizardPage.configLabel.text")+config.getConsumerKey()); //$NON-NLS-1$
					configLabel.setLayoutData(new GridData());
					continue;
				}
				
				final IValueProviderGUI gui = factory.createValueProviderGUI(config);
				valueProviderGUIs.put(providerID, gui);
				Control guiControl = gui.createGUI(rowWrapper);
				final IValueProviderGUIListener listener = new IValueProviderGUIListener() {
					public void providerOutputValueChanged() {
						if (gui.isAcquisitionComplete()) {
							Object output = gui.getOutputValue();
							if (parameterController != null)
								parameterController.getProviderValues().put(providerID, output);
							ValueConsumerBinding binding = valueAcquisitionSetup.getValueProviderBinding(config);
							if (binding != null) {
								if (binding.getConsumer() instanceof ValueProviderConfig) {
									ValueProviderConfig providerConfig = (ValueProviderConfig) binding.getConsumer();
									ValueProviderID bindingProviderID = ValueProviderID.create(
											providerConfig.getValueProviderOrganisationID(), 
											providerConfig.getValueProviderCategoryID(), 
											providerConfig.getValueProviderID()
									);
									if (valueProviderGUIs.containsKey(bindingProviderID)) {
										IValueProviderGUI providerGUI = valueProviderGUIs.get(bindingProviderID);
										providerGUI.setInputParameterValue(binding.getParameterID(), output);
									}
								} else if (binding.getConsumer() instanceof AcquisitionParameterConfig) {
									AcquisitionParameterConfig consumer = (AcquisitionParameterConfig) binding.getConsumer();
									if (parameterController != null) {
										parameterController.setParameterValue(consumer.getParameterID(), output);
									}
								}							
							}
						}
						else {
							if (parameterController != null)
								parameterController.getProviderValues().remove(providerID);
						}
						getContainer().updateButtons();
					}
				};
				gui.addValueProviderGUIListener(listener);
				valueProviderGUIListeners.add(listener);
				guiControl.addDisposeListener(new DisposeListener() {
					public void widgetDisposed(DisposeEvent event) {
						gui.removeValueProviderGUIListener(listener);
					}
				});
				if (config.isShowMessageInHeader()) {
					if (!"".equals(description)) //$NON-NLS-1$
						description = description + "\n"; //$NON-NLS-1$
					description = description + ValueProviderConfigUtil.getValueProviderMessage(config);
				}
				setDescription(description);
			}
		}
		contentsCreated = true;
		return wrapper;
	}

	@Override
	public void onShow() {
		super.onShow();
		for (SortedSet<ValueProviderConfig> configRows : pageProviderConfigs.values()) {
			for (ValueProviderConfig providerConfig : configRows) {
				ValueProviderID providerID = providerConfig.getConfigValueProviderID();
				IValueProviderGUI providerGUI = valueProviderGUIs.get(providerID);
				Map<String, ValueConsumerBinding> bindings = valueAcquisitionSetup.getValueConsumerBindings(providerConfig);
				if (bindings == null)
					continue; // This provider has no input values. TODO: Verify this!
				for (ValueConsumerBinding binding : bindings.values()) {
					if (parameterController != null) {
						Object paramValue = parameterController.getProviderValues().get(binding.getValueProviderID());
						if (paramValue != null && providerGUI != null)
							providerGUI.setInputParameterValue(binding.getParameterID(), paramValue);
					}
				}
			}
		}
	}
	
	@Override
	public void onHide() {
		for (IValueProviderGUIListener listener : valueProviderGUIListeners) {
			listener.providerOutputValueChanged();
		}
		super.onHide();
	}
	
	@Override
	public boolean isPageComplete() {
		if (!contentsCreated)
			return false;
		for (IValueProviderGUI gui : valueProviderGUIs.values()) {
			if (!gui.isAcquisitionComplete())
				return false;
		}
		return true;
	}

}
