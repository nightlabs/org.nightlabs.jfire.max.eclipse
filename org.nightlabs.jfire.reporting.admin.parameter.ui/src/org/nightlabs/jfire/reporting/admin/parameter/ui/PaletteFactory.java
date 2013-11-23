package org.nightlabs.jfire.reporting.admin.parameter.ui;

import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageDimension;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.jfire.reporting.admin.parameter.ui.editpart.tree.ValueProviderConfigTreeEditPart;
import org.nightlabs.jfire.reporting.admin.parameter.ui.resource.Messages;
import org.nightlabs.jfire.reporting.admin.parameter.ui.tool.ConnectionToolEntry;
import org.nightlabs.jfire.reporting.admin.parameter.ui.tool.ValueProviderToolEntry;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class PaletteFactory
{
	/** Default palette size. */
	protected static final int DEFAULT_PALETTE_SIZE = 125;
	/** Preference ID used to persist the palette location. */
	protected static final String PALETTE_DOCK_LOCATION = "PaletteFactory.Location"; //$NON-NLS-1$
	/** Preference ID used to persist the palette size. */
	protected static final String PALETTE_SIZE = "PaletteFactory.Size"; //$NON-NLS-1$
	/** Preference ID used to persist the flyout palette's state. */
	protected static final String PALETTE_STATE = "PaletteFactory.State"; //$NON-NLS-1$

	public FlyoutPreferences createPalettePreferences()
	{
		// set default flyout palette preference values, in case the preference store
		// does not hold stored values for the given preferences
		getPreferenceStore().setDefault(PALETTE_DOCK_LOCATION, -1);
		getPreferenceStore().setDefault(PALETTE_STATE, -1);
		getPreferenceStore().setDefault(PALETTE_SIZE, DEFAULT_PALETTE_SIZE);

		return new FlyoutPreferences() {
			public int getDockLocation() {
				return getPreferenceStore().getInt(PALETTE_DOCK_LOCATION);
			}
			public int getPaletteState() {
				return getPreferenceStore().getInt(PALETTE_STATE);
			}
			public int getPaletteWidth() {
				return getPreferenceStore().getInt(PALETTE_SIZE);
			}
			public void setDockLocation(int location) {
				getPreferenceStore().setValue(PALETTE_DOCK_LOCATION, location);
			}
			public void setPaletteState(int state) {
				getPreferenceStore().setValue(PALETTE_STATE, state);
			}
			public void setPaletteWidth(int width) {
				getPreferenceStore().setValue(PALETTE_SIZE, width);
			}
		};
	}

	/**
	 * Returns the preference store for the ReportingAdminParameterPlugin.
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#getPreferenceStore()
	 */
	protected IPreferenceStore getPreferenceStore()
	{
		return ReportingAdminParameterPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * Creates the PaletteRoot and adds all palette elements.
	 * Use this factory method to create a new palette for your graphical editor.
	 * @return a new PaletteRoot
	 */
	public PaletteRoot createPalette()
	{
		PaletteRoot palette = new PaletteRoot();
		palette.add(createToolsGroup(palette));
		palette.add(createModelGroup());
		return palette;
	}

	protected PaletteContainer createToolsGroup(PaletteRoot palette)
	{
		PaletteGroup toolGroup = new PaletteGroup(Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.PaletteFactory.paletteGroup.tools.name")); //$NON-NLS-1$

		// Add a selection tool to the group
		ToolEntry tool = createSelectionToolEntry();
		toolGroup.add(tool);
		palette.setDefaultEntry(tool);

		// Add a marquee tool to the group
		toolGroup.add(createMarqueeToolEntry());

		// Add a (unnamed) separator to the group
		toolGroup.add(new PaletteSeparator());

		return toolGroup;
	}

	protected ToolEntry createMarqueeToolEntry()
	{
		return new MarqueeToolEntry();
	}

	protected ToolEntry createSelectionToolEntry()
	{
		return new PanningSelectionToolEntry();
	}

	protected PaletteContainer createModelGroup()
	{
		PaletteDrawer componentsDrawer = new PaletteDrawer(Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.PaletteFactory.paletteGroup.model.name")); //$NON-NLS-1$

		// add Connection Tool
		ToolEntry component = createConnectionToolEntry();
		componentsDrawer.add(component);

		// add ValueProvider Tool
		component = createValueProviderToolEntry();
		componentsDrawer.add(component);

		return componentsDrawer;
	}

	protected ToolEntry createConnectionToolEntry()
	{
		return new ConnectionCreationToolEntry(
				Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.PaletteFactory.connectionTool.label"), //$NON-NLS-1$
				Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.PaletteFactory.connectionTool.shortDesc"), //$NON-NLS-1$
				null,
				SharedImages.getSharedImageDescriptor(
						ReportingAdminParameterPlugin.getDefault(),
						ConnectionToolEntry.class, "",  //$NON-NLS-1$
						ImageDimension._16x16, ImageFormat.gif),
				SharedImages.getSharedImageDescriptor(
						ReportingAdminParameterPlugin.getDefault(),
						ConnectionToolEntry.class, "", //$NON-NLS-1$
						ImageDimension._24x24, ImageFormat.gif)
			);
	}

//	protected ToolEntry createConnectionToolEntry()
//	{
//		return new ConnectionCreationToolEntry(
//				"ConnectionToolLabel",
//				"ConnectionToolShortDesc",
//				null,
//				SharedImages.getSharedImageDescriptor(
//						ReportingAdminParameterPlugin.getDefault(),
//						ConnectionToolEntry.class, "",
//						ImageDimension._16x16, ImageFormat.gif),
//				SharedImages.getSharedImageDescriptor(
//						ReportingAdminParameterPlugin.getDefault(),
//						ConnectionToolEntry.class, "",
//						ImageDimension._24x24, ImageFormat.gif)
//			);
//	}
	
	protected ToolEntry createValueProviderToolEntry()
	{
		return new ValueProviderToolEntry(
				Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.PaletteFactory.providerTool.label"), //$NON-NLS-1$
				Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.PaletteFactory.providerTool.shortDesc"), //$NON-NLS-1$
				ValueProviderConfig.class,
				getCreationFactory(ValueProviderConfig.class),
				SharedImages.getSharedImageDescriptor(
						ReportingAdminParameterPlugin.getDefault(),
						ValueProviderConfigTreeEditPart.class),
				null
		);
	}

	public ModelCreationFactory getCreationFactory(Class targetClass) {
		return new ModelCreationFactory(targetClass, setupProvider);
	}

	private IValueAcquisitionSetupProvider setupProvider;
	public PaletteFactory(IValueAcquisitionSetupProvider setupProvider) {
		this.setupProvider = setupProvider;
	}
	
	protected ValueAcquisitionSetup getValueAcquisitionSetup() {
		return setupProvider.getValueAcquisitionSetup();
	}
	
}
