package org.nightlabs.jfire.asterisk.ui.asteriskserver;

import org.nightlabs.base.ui.editor.JDOObjectEditorInput;
import org.nightlabs.jfire.pbx.id.PhoneSystemID;

/**
 * Editor input for {@link AsteriskServerEditor}s.
 *
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class AsteriskServerEditorInput extends JDOObjectEditorInput<PhoneSystemID>
{
	/**
	 * @param asteriskServerID The asteriskServer
	 */
	public AsteriskServerEditorInput(PhoneSystemID asteriskServerID)
	{
		super(asteriskServerID);
	}
}