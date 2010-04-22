package org.nightlabs.jfire.trade.ui.legalentity.search;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.ui.search.SearchFilterProvider;
import org.nightlabs.jdo.query.ui.search.SearchResultFetcher;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.person.search.PersonTableViewerConfigurationHelper;
import org.nightlabs.jfire.base.ui.prop.IPropertySetTableConfig;
import org.nightlabs.jfire.base.ui.prop.PropertySetTable;
import org.nightlabs.jfire.base.ui.prop.view.AbstractPropertySetTableViewer;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.prop.PropertyManagerRemote;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.dao.TrimmedPropertySetDAO;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.jfire.prop.search.PropSearchFilter;
import org.nightlabs.jfire.prop.view.PropertySetTableViewerConfiguration;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.query.LegalEntityPersonMappingBean;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;
import org.nightlabs.util.ObjectCarrier;

public class LegalEntityTableViewer 
extends AbstractPropertySetTableViewer<LegalEntityPersonMappingBean, LegalEntityTableViewer.TableInputBean, LegalEntity, PropertySetTableViewerConfiguration>
implements SearchResultFetcher {

	public static class TableInputBean {
		public LegalEntity legalEntity;
		public Person person;
	}
	
	class LegalEntityTable extends PropertySetTable<TableInputBean, LegalEntity> {

		public LegalEntityTable(Composite parent, int style) {
			super(parent, style);
		}

		@Override
		protected IPropertySetTableConfig getPropertySetTableConfig() {
			if (getConfiguration() == null) {
				throw new IllegalStateException("The configuration for this PropertySetViewer was not set!");
			}
			return PersonTableViewerConfigurationHelper.createPropertySetTableConfig(getConfiguration());
		}
		
		@Override
		protected PropertySet getPropertySetFromElement(TableInputBean inputElement) {
			if (inputElement instanceof TableInputBean) {
				return inputElement.person;
			}
			return super.getPropertySetFromElement(inputElement);
		}

		@Override
		protected LegalEntity convertInputElement(TableInputBean inputElement) {
			if (inputElement instanceof TableInputBean) {
				return inputElement.legalEntity;
			}
			return null;
		}
		
	}
	
	@Override
	protected LegalEntityTable createPropertySetTable(Composite parent) {
		if (getConfiguration() == null) {
			throw new IllegalStateException("The configuration for this PropertySetViewer was not set!");
		}
		return new LegalEntityTable(parent, SWT.NONE);
	}

	@Override
	public void setInput(Collection<LegalEntityPersonMappingBean> input, ProgressMonitor monitor) {
		monitor.beginTask("Loading legal entity persons", 10);
		try {
			Set<AnchorID> leAnchorIDs = new HashSet<AnchorID>();
			Set<PropertySetID> propertySetIDs = new HashSet<PropertySetID>();
			final Map<AnchorID, TableInputBean> inputByLe = new HashMap<AnchorID, TableInputBean>();
			Map<PropertySetID, TableInputBean> inputByPerson = new HashMap<PropertySetID, TableInputBean>();
			for (LegalEntityPersonMappingBean lePersonMapping : input) {
				leAnchorIDs.add(lePersonMapping.getLegalEntityID());
				propertySetIDs.add(lePersonMapping.getPersonID());
				TableInputBean inputBean = new TableInputBean();
				inputByLe.put(lePersonMapping.getLegalEntityID(), inputBean);
				inputByPerson.put(lePersonMapping.getPersonID(), inputBean);
			}
			Collection<LegalEntity> legalEntities = LegalEntityDAO.sharedInstance().getLegalEntities(leAnchorIDs, new String[] { FetchPlan.DEFAULT },
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 4));
			
			for (LegalEntity legalEntity : legalEntities) {
				TableInputBean inputBean = inputByLe.get(JDOHelper.getObjectId(legalEntity));
				if (inputBean == null) {
					throw new IllegalStateException("Could not find InputBean for queried LegalEntity");
				}
				inputBean.legalEntity = legalEntity;
			}
			monitor.worked(1);
			
			Collection<? extends PropertySet> trimmedPropertySets = TrimmedPropertySetDAO.sharedInstance().getTrimmedPropertySets(
					propertySetIDs, getConfiguration().getAllStructFieldIDs(), null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					new SubProgressMonitor(monitor, 4));
			for (PropertySet propertySet : trimmedPropertySets) {
				TableInputBean inputBean = inputByPerson.get(JDOHelper.getObjectId(propertySet));
				inputBean.person = (Person) propertySet;
			}
			monitor.worked(1);
			
			getPropertySetTable().getDisplay().asyncExec(new Runnable() {
				public void run() {
					getPropertySetTable().setInput(inputByLe.values());
				}
			});
			
			notifiyContentChangedListeners(input);
			
		} finally{
			monitor.done();
		}
	}

	@Override
	public void searchTriggered(final SearchFilterProvider filterProvider) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				setMessage("Searching...");
			}
		});

		Job loadJob = new Job("Loading person search result...") {
			@SuppressWarnings("unchecked")
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				monitor.beginTask(getName(), 10);
				try {
					PropertyManagerRemote propertyManager;
					try {
						propertyManager = JFireEjb3Factory.getRemoteBean(PropertyManagerRemote.class, Login.getLogin().getInitialContextProperties());
					} catch (LoginException e1) {
						throw new RuntimeException(e1);
					}

					final ObjectCarrier<PropSearchFilter> oc = new ObjectCarrier<PropSearchFilter>();
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							oc.setObject((PropSearchFilter) filterProvider.getSearchFilter());
						}
					});
					PropSearchFilter searchFilter = oc.getObject();

					try {
						Set<LegalEntityPersonMappingBean> legalEntityPersonMappings = new HashSet<LegalEntityPersonMappingBean>(
								(Collection<? extends LegalEntityPersonMappingBean>) propertyManager.searchPropertySets(searchFilter,
										new String[] {}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT));
						monitor.worked(1);

						setInput(legalEntityPersonMappings, new SubProgressMonitor(monitor, 9));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				} finally {
					monitor.done();
				}
				return Status.OK_STATUS;
			}
		};
		loadJob.schedule();
	}

}
