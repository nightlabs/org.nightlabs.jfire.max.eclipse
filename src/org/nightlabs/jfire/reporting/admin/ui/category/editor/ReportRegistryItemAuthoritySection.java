/**
 *
 */
package org.nightlabs.jfire.reporting.admin.ui.category.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.jfire.base.admin.ui.editor.authority.AbstractAuthoritySection;
import org.nightlabs.jfire.base.admin.ui.editor.authority.InheritedSecuringAuthorityResolver;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.security.id.AuthorityID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * Authority section to be used for authority pages for {@link ReportRegistryItem}.
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ReportRegistryItemAuthoritySection extends AbstractAuthoritySection {

	private final AuthorityPageController pageController;

	public ReportRegistryItemAuthoritySection(IFormPage page, Composite parent, AuthorityPageController pageController) {
		super(page, parent);
		this.pageController = pageController;
	}

	@Override
	protected InheritedSecuringAuthorityResolver createInheritedSecuringAuthorityResolver() {
		return new InheritedSecuringAuthorityResolver() {
			@Override
			public AuthorityID getInheritedSecuringAuthorityID(ProgressMonitor monitor) {
				return pageController.getControllerObject().getParentCategory().getSecuringAuthorityID();
			}
		};
	}

	@Override
	public void setPageController(IEntityEditorPageController pageController) {
		setAuthorityPageControllerHelper(((AuthorityPageController)pageController).getAuthorityPageControllerHelper());
	}
}