/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.layout.editor.l10n;

import java.util.Collection;
import java.util.Locale;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface IReportLayoutL10nManager {

	Collection<Locale> getBundleLocales();
	void saveLocalisationBundle(IProgressMonitor monitor);
	
}
