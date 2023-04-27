package abstraction.eq4Transformateur1;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import abstraction.eq4Transformateur1.Achat.AchatBourse;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IFabricantChocolatDeMarque;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.Feve;

// Francois, Alexian, Amine, Fouad
public class Transformateur1Transformateur extends Stock implements IFabricantChocolatDeMarque  {
	
	protected List<ChocolatDeMarque>chocosProduits;

	protected HashMap<Feve, HashMap<Chocolat, Double>> pourcentageTransfo; // pour les differentes feves, le chocolat qu'elle peuvent contribuer a produire avec le ratio
	
	public Transformateur1Transformateur() {
		super();
		this.chocosProduits = new LinkedList<ChocolatDeMarque>();
	}
	
	//========================================================
	//               FabricantChocolatDeMarque
	//========================================================

	public List<ChocolatDeMarque> getChocolatsProduits() {
		if (this.chocosProduits.size()==0) {
			for (Chocolat c : Chocolat.values()) {
				int pourcentageCacao =  (int) (Filiere.LA_FILIERE.getParametre("pourcentage min cacao "+c.getGamme()).getValeur());
				this.chocosProduits.add(new ChocolatDeMarque(c, "Vccotioi", pourcentageCacao, 0));
			}
		}
		return this.chocosProduits;
	}

	public void initialiser() {
		super.initialiser();
		this.pourcentageTransfo = new HashMap<Feve, HashMap<Chocolat, Double>>();
		this.pourcentageTransfo.put(Feve.F_HQ_BE, new HashMap<Chocolat, Double>());
		double conversion = 1.06;
		this.pourcentageTransfo.get(Feve.F_HQ_BE).put(Chocolat.C_HQ_BE, conversion);// la masse de chocolat obtenue est plus importante que la masse de feve vue l'ajout d'autres ingredients
		this.pourcentageTransfo.put(Feve.F_BQ, new HashMap<Chocolat, Double>());
		double conversionb = 1.58;
		this.pourcentageTransfo.get(Feve.F_BQ).put(Chocolat.C_BQ, conversionb);
	}

	/**
	 * @author fouad/amine
	 *
	 */

	
	public void next() {
		super.next();
		Feve fb = Feve.F_BQ;
		Chocolat cb = Chocolat.C_BQ;
		int transfo = (int) (Math.min(this.stockFeves.get(fb), Math.random()*1000));
		if (transfo>0) {
			this.stockFeves.put(fb, this.stockFeves.get(fb)-transfo);
			this.totalStocksFeves.retirer(this, transfo, this.cryptogramme);
			this.stockChoco.put(cb, this.stockChoco.get(cb)+(transfo)*this.pourcentageTransfo.get(fb).get(cb));
			int pourcentageCacao =  42;
			this.totalStocksChoco.ajouter(this, ((transfo)*this.pourcentageTransfo.get(fb).get(cb)), this.cryptogramme);
			this.journal.ajouter(COLOR_LLGRAY, Color.PINK, "Transfo de "+(transfo<10?" "+transfo:transfo)+" T de "+fb+" en "+Journal.doubleSur(transfo*this.pourcentageTransfo.get(fb).get(cb),3,2)+" T de "+cb);
			this.journal.ajouter(COLOR_LLGRAY, COLOR_BROWN," stock("+fb+")->"+this.stockFeves.get(fb));
			this.journal.ajouter(COLOR_LLGRAY, COLOR_BROWN," stock("+cb+")->"+this.stockChoco.get(cb));
		}
		Feve fh = Feve.F_HQ_BE;
		Chocolat ch = Chocolat.C_HQ_BE;
		int transfoh = (int) (Math.min(this.stockFeves.get(fh), Math.random()*1000));
		if (transfoh>0) {
			this.stockFeves.put(fh, this.stockFeves.get(fh)-transfoh);
			this.totalStocksFeves.retirer(this, transfoh, this.cryptogramme);
			// Tous les chocolats sont directement étiquetés "Vccotioi"
			int pourcentageCacao =  94;
			ChocolatDeMarque cm= new ChocolatDeMarque(ch, "Vccotioi", pourcentageCacao, 15);
			double scm = this.stockChocoMarque.keySet().contains(cm) ?this.stockChocoMarque.get(cm) : 0.0;
			this.stockChocoMarque.put(cm, scm+((transfoh)*this.pourcentageTransfo.get(fh).get(ch)));
			this.totalStocksChocoMarque.ajouter(this, ((transfoh)*this.pourcentageTransfo.get(fh).get(ch)), this.cryptogramme);
			this.journal.ajouter(COLOR_LLGRAY, Color.PINK, "Transfo de "+(transfoh<10?" "+transfoh:transfoh)+" T de "+fh+" en "+Journal.doubleSur(transfo*this.pourcentageTransfo.get(fh).get(ch),3,2)+" T de "+ch);
			this.journal.ajouter(COLOR_LLGRAY, COLOR_BROWN," stock("+fh+")->"+this.stockFeves.get(fh));
			this.journal.ajouter(COLOR_LLGRAY, COLOR_BROWN," stock("+cm+")->"+this.stockChocoMarque.get(cm));
		}
	}
}
	
