package org.nightlabs.jfire.personrelation.trade.ui.tucked.compact;

import java.io.Serializable;

import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.personrelation.ui.tree.PersonRelationTreeUtil;
import org.nightlabs.jfire.prop.id.PropertySetID;

/**
 * This is simply a wrapper that places together related information about a known tucked-path. A known tucked-path is one
 * that contains corresponding information of the following:
 *    (i) a path of {@link PropertySetID}s,
 *   (ii) a path of {@link ObjectID}s (except for the first (root) element, which remains to be a {@link PropertySetID}), and
 *  (iii) a reference index of (i) and (ii), which points directly to the index reference of the path kept in the
 *        related Controller's map of multiple paths.
 *
 * Note: Once a tucked-path has been determined by the server, there are no events that can cause it to change!
 *       Thus, it follows that we can quantize it statically, and hence allowing us to access an element in O(1) time.
 * 
 * @author khaireel at nightlabs dot de
 */
public class TuckedPathDosier implements Serializable {
	private static final long serialVersionUID = -9201454449911771147L;
	
	public ObjectID[] pathPSID = null;  // See (i).
	public ObjectID[] pathPRID = null;  // See (ii).
	public int controllerIndexRef = -1; // See (iii).
	
	public String toDebugString(String lnPreamble) {
		String str = "";
		str += String.format("\n%s%s", lnPreamble, "TuckedPathDosier.controllerIndexRef = " + controllerIndexRef);
		str += String.format("\n%s%s", lnPreamble, PersonRelationTreeUtil.showObjectIDs("TuckedPathDosier.path-PS-id", pathPSID, 10));
		str += String.format("\n%s%s", lnPreamble, PersonRelationTreeUtil.showObjectIDs("TuckedPathDosier.path-PR-id", pathPRID, 10));
		return str;
	}
}
