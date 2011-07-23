package org.nightlabs.jfire.trade.ui.overview;

import java.util.Set;

import javax.security.auth.login.LoginException;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.id.ProcessDefinitionID;
import org.nightlabs.jfire.jbpm.query.StatableQuery;
import org.nightlabs.jfire.jbpm.ui.query.AbstractStatableSearchComposite;
import org.nightlabs.jfire.trade.TradeManagerRemote;
import org.nightlabs.progress.ProgressMonitor;

/**
 *
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class StatableFilterSearchComposite
//extends AbstractQueryFilterComposite<StatableQuery>
extends AbstractStatableSearchComposite<StatableQuery>
{
	/**
	 * Creates a new {@link AbstractQueryFilterComposite}.
	 * <p><b>Note</b>: The caller has to call {@link #createComposite()} to create the UI! <br />
	 * 	This is not done in this constructor to omit problems with fields that are not only declared,
	 * 	but also initialised. If these fields are used inside {@link #createComposite()}
	 * 	or new values are assigned to them, one of the following two things may happen:
	 *  <ul>
	 *  	<li>The value assigned to that field is overridden by the initialisation value that is
	 *  			assigned after this constructor is finished</li>
	 *  	<li>The referenced value is not yet properly initialised, because the initialisation is
	 *  			done after the constructor finishes, and hence results in an unexpected exception.</li>
	 *  </ul>
	 * </p>
	 * @param parent
	 *          The parent to instantiate this filter into.
	 * @param style
	 *          The style to apply.
	 * @param layoutMode
	 *          The layout mode to use. See {@link XComposite.LayoutMode}.
	 * @param layoutDataMode
	 *          The layout data mode to use. See {@link XComposite.LayoutDataMode}.
	 * @param queryProvider
	 *          The queryProvider to use. It may be <code>null</code>, but the caller has to
	 *          ensure, that it is set before {@link #getQuery()} is called!
	 */
	public StatableFilterSearchComposite(Composite parent, int style, LayoutMode layoutMode,
		LayoutDataMode layoutDataMode, QueryProvider<? super StatableQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
	}

	/**
	 * Delegates to {@link StatableFilterComposite#StatableFilterComposite(AbstractQueryFilterComposite, int, XComposite.LayoutMode, LayoutDataMode)}
	 */
	public StatableFilterSearchComposite(Composite parent, int style,
		QueryProvider<? super StatableQuery> queryProvider)
	{
		super(parent, style, queryProvider);
	}

	@Override
	protected Set<ProcessDefinitionID> retrieveProcessDefinitionIDs(ProgressMonitor monitor, Class<? extends Statable> statableClass)
	{
		String statableClassName = statableClass.getName();
		TradeManagerRemote tradeManager;
		try {
			tradeManager = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
			Set<ProcessDefinitionID> processDefinitionIDs = tradeManager.getProcessDefinitionIDs(statableClassName);
			return processDefinitionIDs;
		} catch (LoginException e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite#getQueryClass()
	 */
	@Override
	public Class<StatableQuery> getQueryClass() {
		return StatableQuery.class;
	}

}
