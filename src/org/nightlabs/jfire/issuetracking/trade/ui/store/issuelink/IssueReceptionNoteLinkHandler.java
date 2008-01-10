/**
 * 
 */
package org.nightlabs.jfire.issuetracking.trade.ui.store.issuelink;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.issuetracking.trade.ui.IssueTrackingTradePlugin;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandler;
import org.nightlabs.jfire.store.id.ReceptionNoteID;

/**
 * @author chairatk
 *
 */
public class IssueReceptionNoteLinkHandler 
implements IssueLinkHandler 
{

	public String getLinkObjectDescription(ObjectID objectID) {
		ReceptionNoteID receptionNoteID = (ReceptionNoteID) objectID;
		return String.format(
				"Reception Note  %s",
				(receptionNoteID == null ? "" : receptionNoteID.receptionNoteIDPrefix + '/' + ObjectIDUtil.longObjectIDFieldToString(receptionNoteID.receptionNoteID)));
	}

	public Image getLinkObjectImage(ObjectID objectID) {
		return SharedImages.getSharedImageDescriptor(
				IssueTrackingTradePlugin.getDefault(), 
				IssueReceptionNoteLinkHandler.class, 
				"LinkObject").createImage();
	}

	public void openLinkObject() {
		
	}

}
