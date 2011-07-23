package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.PriceConfigComposite;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jseditor.ui.IJSEditor;

public class CellReferencePage extends AbstractCellReferencePage{

	private PriceConfigComposite priceConfigComposite = null;

	public CellReferencePage(IJSEditor targetEditor, PriceConfigComposite priceConfigComposite) {
		super(targetEditor, priceConfigComposite);
		this.priceConfigComposite = priceConfigComposite;
	}

	@Override
	protected List<Composite> createDimensionTabItems(TabFolder tabFolder) {
		List<Composite> pageCompositeList = new ArrayList<Composite>();

		TabItem customerGroupTabItem = new TabItem(tabFolder, SWT.NONE);
		customerGroupTabItem.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference.CellReferencePage.customerGroupTabItem.text")); //$NON-NLS-1$
		CustomerGroupComposite cgc = new CustomerGroupComposite(this, tabFolder);
		customerGroupTabItem.setControl(cgc);
		pageCompositeList.add(cgc);

		TabItem tariffTabItem = new TabItem(tabFolder, SWT.NONE);
		tariffTabItem.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference.CellReferencePage.tariffTabItem.text")); //$NON-NLS-1$
		TariffComposite tc = new TariffComposite(this, tabFolder);
		tariffTabItem.setControl(tc);
		pageCompositeList.add(tc);

		TabItem currencyTabItem = new TabItem(tabFolder, SWT.NONE);
		currencyTabItem.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference.CellReferencePage.currencyTabItem.text")); //$NON-NLS-1$
		CurrencyComposite cc = new CurrencyComposite(this, tabFolder);
		currencyTabItem.setControl(cc);
		pageCompositeList.add(cc);

		ProductTypeComposite pc = new ProductTypeComposite(this, tabFolder, priceConfigComposite);
		if (pc.getCellReferenceProductTypeSelector() == null)
			pc.dispose();
		else {
			TabItem productTypeTabItem = new TabItem(tabFolder, SWT.NONE);
			productTypeTabItem.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference.CellReferencePage.productTypeTabItem.text")); //$NON-NLS-1$

			productTypeTabItem.setControl(pc);
			pageCompositeList.add(pc);
		}

		TabItem priceFragmentTabItem = new TabItem(tabFolder, SWT.NONE);
		priceFragmentTabItem.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference.CellReferencePage.priceFragmentTypeTabItem.text")); //$NON-NLS-1$
		PriceFragmentTypeComposite pfc = new PriceFragmentTypeComposite(this, tabFolder);
		priceFragmentTabItem.setControl(pfc);
		pageCompositeList.add(pfc);

		return pageCompositeList;
	}

	private Map<String, String> scriptMap = new HashMap<String, String>();

	private String generatedScript = null;

	public void setDimensionScript(String dimensionKey, String dimensionScript) {
		scriptMap.put(dimensionKey, dimensionScript);
		generateScript();
	}

	public void clearDimensionScript(String dimensionKey){
		if (scriptMap.containsKey(dimensionKey)) {
			scriptMap.remove(dimensionKey);
			generateScript();
		}//if
	}

	private void generateScript(){
		StringBuilder scriptBuilder = new StringBuilder();

		scriptBuilder
			.append(CellReferenceWizard.NEWLINE)
			.append("cell.resolvePriceCellsAmount") //$NON-NLS-1$
			.append(CellReferenceWizard.L_BRACKET)
			.append(CellReferenceWizard.NEWLINE)
			.append(CellReferenceWizard.TAB);

		for (Iterator<String> itScript = scriptMap.values().iterator(); itScript.hasNext(); ) {
			String script = itScript.next();
			scriptBuilder.append(script);
			if (itScript.hasNext()){
				scriptBuilder.append(",").append(CellReferenceWizard.NEWLINE).append(CellReferenceWizard.TAB); //$NON-NLS-1$
			}
		}

		scriptBuilder
			.append(CellReferenceWizard.NEWLINE)
			.append(CellReferenceWizard.R_BRACKET);

		generatedScript = scriptBuilder.toString();
		getSourcePreviewComposite().setDocumentText(generatedScript);
	}

	public String getGeneratedScript(){
		return generatedScript;
	}
}
