package abstraction.eq6Transformateur3;

import java.util.LinkedList;
import java.util.List;

import abstraction.eqXRomu.appelsOffres.IVendeurAO;
import abstraction.eqXRomu.appelsOffres.PropositionAchatAO;
import abstraction.eqXRomu.produits.ChocolatDeMarque;

public class Transformateur3Vente extends Transformateur3Transformation implements IVendeurAO{
/**Nathan Salbego*/
	
	
	public Transformateur3Vente() {
		super();		
	}

	@Override
	public PropositionAchatAO choisir(List<PropositionAchatAO> propositions) {
		PropositionAchatAO p= propositions.get(0);
		for (int i=1;i<propositions.size();i++) {
			if (p.compareTo(propositions.get(i))<0) {
				p=propositions.get(i);
			}
		}
	return p;}
	/**Cette fontion doit rendre la quantite de chocolat d'un type que nous devons avoir pour le vendre au step step
	 * 
	 * @param step
	 * @param choco
	 * @return
	 */
	protected double demandeTotStep (int step,Object choco) {
		return 0;
	}


}
