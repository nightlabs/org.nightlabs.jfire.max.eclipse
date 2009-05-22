package org.nightlabs.jfire.issuetracking.ui.issue.editor.issueMarker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageDimension;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.issuemarker.IssueMarker;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;

//      ,-_|\
//     /     \  ]Egoiste in
//     @_,-._/       ]N[ightLabs
//  ======= v =====================================================================================
//  "Science without religion is lame, religion without science is blind." -- A. E. (1879 - 1955).
/**
 * This displays the combo list of possible options for a user to choose in order to mark
 * the currently opened {@link Issue}.
 *
 * @author Khaireel Mohamed - khaireel at nightlabs dot de
 *
 * TODO Revise this with a proper Registry.
 */
public class IssueMarkerNameCombo extends XComboComposite<IssueMarker> {
	/**
	 * Creates a new instance of an IssueMarkerNameCombo.
	 */
	public IssueMarkerNameCombo(Composite parent) {
		//super(parent, SWT.DROP_DOWN | SWT.READ_ONLY, new IMLabelProvider());
		super(parent, SWT.READ_ONLY, new IMLabelProvider());
		setInput( getIssueMarkerEntries() );	// FIXME Revise this with a proper registry. See example with ReportViewerRegistry.sharedInstance().getReportViewerEntries().
		setSelection(0);	// Default selection.
	}

	/**
	 * Populates the elements for the drop-down combo.
	 * TODO Revise this for the general case, and put this in a proper registry.
	 */
	protected Collection<IssueMarker> getIssueMarkerEntries() {
		String[] standardNames = {"Email follow-up", "Phone follow-up", "Suspended"};

		Collection<IssueMarker> defaultIssueMarkers = new ArrayList<IssueMarker>(standardNames.length);
		for(String standardName : standardNames) {
			IssueMarker issueMarker = new IssueMarker(false);
			issueMarker.getName().setText(Locale.ENGLISH.getLanguage(), standardName);
			defaultIssueMarkers.add(issueMarker);
		}

		return defaultIssueMarkers;
	}



	// ------------------------------------------------------------------------------------------------------
	/**
	 * Provides the contents for the IssueMarkerNameCombo.
	 */
	private static class IMLabelProvider extends LabelProvider {
		@Override
		public Image getImage(Object element) {
			IssueMarker issueMarker = (IssueMarker)element;
			String refText = issueMarker.getName().getText();
			String suffix = "Email";

			if (refText.equals("Phone follow-up"))	suffix = "Telephone";
			else if (refText.equals("Suspended"))	suffix = "Suspended";
			return SharedImages.getSharedImage(IssueTrackingPlugin.getDefault(), IssueMarkerSection.class, suffix, ImageDimension._16x16, ImageFormat.gif);
		}

		@Override
		public String getText(Object element) {
			return ((IssueMarker)element).getName().getText();
		}
	}
}
