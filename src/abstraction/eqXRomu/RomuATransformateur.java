package abstraction.eqXRomu;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IFabricantChocolatDeMarque;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.Feve;

public class RomuATransformateur extends RomuActeur implements IFabricantChocolatDeMarque {

	private List<ChocolatDeMarque>chocosProduits;
	protected HashMap<Feve, Double> stockFeves;
	protected HashMap<Chocolat, Double> stockChoco;
	protected HashMap<ChocolatDeMarque, Double> stockChocoMarque;
	protected HashMap<Feve, HashMap<Chocolat, Double>> pourcentageTransfo; // pour les differentes feves, le chocolat qu'elle peuvent contribuer a produire avec le ratio
	protected List<ChocolatDeMarque> chocolatsVillors;


	public RomuATransformateur() {
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
				this.chocosProduits.add(new ChocolatDeMarque(c, "Villors", pourcentageCacao, 0));
			}
		}
		return this.chocosProduits;
	}

	public void initialiser() {
		super.initialiser();
		this.stockFeves=new HashMap<Feve,Double>();
		for (Feve f : this.lesFeves) {
			this.stockFeves.put(f, 20000.0);
			this.totalStocksFeves.ajouter(this, 20000.0, this.cryptogramme);
			this.journal.ajouter("ajout de 20000 de "+f+" au stock de feves --> total="+this.totalStocksFeves.getValeur(this.cryptogramme));
		}
		this.stockChoco=new HashMap<Chocolat,Double>();
		for (Chocolat c : Chocolat.values()) {
			this.stockChoco.put(c, 20000.0);
			this.totalStocksChoco.ajouter(this, 20000.0, this.cryptogramme);
			this.journal.ajouter("ajout de 20000 de "+c+" au stock de chocolat --> total="+this.totalStocksFeves.getValeur(this.cryptogramme));
		}
		this.stockChocoMarque=new HashMap<ChocolatDeMarque,Double>();
		this.pourcentageTransfo = new HashMap<Feve, HashMap<Chocolat, Double>>();
		this.pourcentageTransfo.put(Feve.F_HQ_BE, new HashMap<Chocolat, Double>());
		double conversion = 1.0 + (100.0 - Filiere.LA_FILIERE.getParametre("pourcentage min cacao HQ").getValeur())/100.0;
		this.pourcentageTransfo.get(Feve.F_HQ_BE).put(Chocolat.C_HQ_BE, conversion);// la masse de chocolat obtenue est plus importante que la masse de feve vue l'ajout d'autres ingredients
		this.pourcentageTransfo.put(Feve.F_MQ_BE, new HashMap<Chocolat, Double>());
		conversion = 1.0 + (100.0 - Filiere.LA_FILIERE.getParametre("pourcentage min cacao MQ").getValeur())/100.0;
		this.pourcentageTransfo.get(Feve.F_MQ_BE).put(Chocolat.C_MQ_BE, conversion);
		this.pourcentageTransfo.put(Feve.F_MQ, new HashMap<Chocolat, Double>());
		this.pourcentageTransfo.get(Feve.F_MQ).put(Chocolat.C_MQ, conversion);
		this.pourcentageTransfo.put(Feve.F_BQ, new HashMap<Chocolat, Double>());
		conversion = 1.0 + (100.0 - Filiere.LA_FILIERE.getParametre("pourcentage min cacao BQ").getValeur())/100.0;
		this.pourcentageTransfo.get(Feve.F_BQ).put(Chocolat.C_BQ, conversion);

		this.journal.ajouter(COLOR_LLGRAY, Color.PINK, "Stock initial chocolat de marque : ");
		this.chocolatsVillors=new LinkedList<ChocolatDeMarque>();
		for (Feve f : Feve.values()) {
			for (Chocolat c : this.pourcentageTransfo.get(f).keySet()) {
				int pourcentageCacao =  (int) (Filiere.LA_FILIERE.getParametre("pourcentage min cacao "+c.getGamme()).getValeur());
				ChocolatDeMarque cm= new ChocolatDeMarque(c, "Villors", pourcentageCacao, 0);
				this.chocolatsVillors.add(cm);
				this.stockChocoMarque.put(cm, 40000.0);
				this.totalStocksChocoMarque.ajouter(this, 40000.0, this.cryptogramme);
				this.journal.ajouter(COLOR_LLGRAY, COLOR_BROWN," stock("+cm+")->"+this.stockChocoMarque.get(cm));
			}
		}
	}



	public void next() {
		super.next();
		this.journal.ajouter("=== STOCKS === ");
		for (Feve f : this.lesFeves) {
			this.journal.ajouter(COLOR_LLGRAY, COLOR_BROWN,"Stock de "+Journal.texteSurUneLargeurDe(f+"", 15)+" = "+this.stockFeves.get(f));
		}
		for (Chocolat c : Chocolat.values()) {
			this.journal.ajouter(COLOR_LLGRAY, COLOR_BROWN,"Stock de "+Journal.texteSurUneLargeurDe(c+"", 15)+" = "+this.stockChoco.get(c));
		}
		if (this.stockChocoMarque.keySet().size()>0) {
			for (ChocolatDeMarque cm : this.stockChocoMarque.keySet()) {
				this.journal.ajouter(COLOR_LLGRAY, COLOR_BROWN,"Stock de "+Journal.texteSurUneLargeurDe(cm+"", 15)+" = "+this.stockChocoMarque.get(cm));
			}
		}
		for (Feve f : this.pourcentageTransfo.keySet()) {
			for (Chocolat c : this.pourcentageTransfo.get(f).keySet()) {
				int transfo = (int) (Math.min(this.stockFeves.get(f), Math.random()*30));
				if (transfo>0) {
					this.stockFeves.put(f, this.stockFeves.get(f)-transfo);
					this.totalStocksFeves.retirer(this, transfo, this.cryptogramme);
					// La moitie sera stockee sous forme de chocolat, l'autre moitie directement etiquetee "Villors"
					this.stockChoco.put(c, this.stockChoco.get(c)+((transfo/2.0)*this.pourcentageTransfo.get(f).get(c)));
					int pourcentageCacao =  (int) (Filiere.LA_FILIERE.getParametre("pourcentage min cacao "+c.getGamme()).getValeur());
					ChocolatDeMarque cm= new ChocolatDeMarque(c, "Villors", pourcentageCacao, 0);
					double scm = this.stockChocoMarque.keySet().contains(cm) ?this.stockChocoMarque.get(cm) : 0.0;
					this.stockChocoMarque.put(cm, scm+((transfo/2.0)*this.pourcentageTransfo.get(f).get(c)));
					this.totalStocksChocoMarque.ajouter(this, ((transfo/2.0)*this.pourcentageTransfo.get(f).get(c)), this.cryptogramme);
					this.totalStocksChoco.ajouter(this, ((transfo/2.0)*this.pourcentageTransfo.get(f).get(c)), this.cryptogramme);
					this.journal.ajouter(COLOR_LLGRAY, Color.PINK, "Transfo de "+(transfo<10?" "+transfo:transfo)+" T de "+f+" en "+Journal.doubleSur(transfo*this.pourcentageTransfo.get(f).get(c),3,2)+" T de "+c);
					this.journal.ajouter(COLOR_LLGRAY, COLOR_BROWN," stock("+f+")->"+this.stockFeves.get(f));
					this.journal.ajouter(COLOR_LLGRAY, COLOR_BROWN," stock("+c+")->"+this.stockChoco.get(c));
					this.journal.ajouter(COLOR_LLGRAY, COLOR_BROWN," stock("+cm+")->"+this.stockChocoMarque.get(cm));
				}
			}
		}
	}
}
