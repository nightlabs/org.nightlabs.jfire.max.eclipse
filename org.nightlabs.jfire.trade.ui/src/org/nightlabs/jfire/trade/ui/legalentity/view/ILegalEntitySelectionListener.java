/**
 * 
 */
package org.nightlabs.jfire.trade.ui.legalentity.view;

import org.nightlabs.jfire.transfer.id.AnchorID;

/**
 * Used for listeners to the {@link LegalEntityEditorView}.
 * 
 * @author Alexander Bieber <!-- alex [at] nightlabs [dot] de -->
 *
 */
public interface ILegalEntitySelectionListener {

	void legalEntitySelected(AnchorID legalEntityID);
	
}
