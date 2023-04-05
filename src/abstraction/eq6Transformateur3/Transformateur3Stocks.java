package abstraction.eq6Transformateur3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.Feve;

public class Transformateur3Stocks extends Transformateur3Acteur  {

/** Nathan Claeys*/ 
	private HashMap<Feve, Double> stockFeve;
    private HashMap<Chocolat, Double> stockChocolat;
    private List<ChocolatDeMarque> stockProduit;
	/** Mouhamed Sow*/ 
    public Transformateur3Stocks() {
        stockFeve = new HashMap<Feve, Double>();
        stockChocolat = new HashMap<Chocolat, Double>();
        stockProduit = new ArrayList<ChocolatDeMarque>();
    }
    
  public void ajouterFeve(Feve feve, Double quantit�) {
	  if(this.stockFeve.containsKey(feve)) {
		  this.stockFeve.put(feve, this.stockFeve.get(feve)+quantit�) ;  
	  }else {
		  this.stockFeve.put(feve, quantit�) ;
	  }
  }
    
    
   

}