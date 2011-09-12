package org.nightlabs.jfire.personrelation.ui;

import java.io.ByteArrayInputStream;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.personrelation.PersonRelationType;
import org.nightlabs.jfire.personrelation.ui.tree.DefaultPersonRelationTreeLabelProviderDelegatePersonRelation;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class PersonRelationPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.nightlabs.jfire.personrelation.ui"; //$NON-NLS-1$

	// The shared instance
	private static PersonRelationPlugin plugin;
	
	/**
	 * The constructor
	 */
	public PersonRelationPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static PersonRelationPlugin getDefault() {
		return plugin;
	}

	
	public Image getPersonRelationTypeIcon(PersonRelationType personRelationType) {
		// TODO: Add a listener to remove PersonRelationType-images when the type is changed
		String imageKey = "PersonRelationType-" + personRelationType.getPersonRelationTypeID() + ".16x16";
		ImageRegistry imageRegistry = getImageRegistry();
		Image image = imageRegistry.get(imageKey);
		if (image == null && personRelationType.getIcon16x16Data() != null) {
			try {
				image = new Image(null, new ImageData(new ByteArrayInputStream(personRelationType.getIcon16x16Data())));
				imageRegistry.put(imageKey, image);
			} catch (Exception e) {
				// rather display no image than having an error here...
				image = null;
			}
		}
		if (image != null)
			return image;
		return SharedImages.getSharedImage(PersonRelationPlugin.getDefault(), DefaultPersonRelationTreeLabelProviderDelegatePersonRelation.class);
	}	
}
