package it.polito.tdp.meteo;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;

public class Model {

	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	private List<Citta> leCitta;
	private List<Citta> best;
	private MeteoDAO mdao;

	public Model() {

		mdao = new MeteoDAO();
		this.leCitta = mdao.getAllCitta();

	}

	public Double getUmiditaMedia(int mese, Citta citta) {
		return mdao.getUmiditàMedia(mese, citta);
	}

	// Schermo pubblico alla ricorsione
	public List<Citta> calcolaSequenza(Month mese) {
		List<Citta> parziale = new ArrayList<Citta>();
		this.best = null;
		
		// -- costo --
		
		cerca(parziale, 0);
		
		return best;
	}

	// ricorsione privata
	private void cerca(List<Citta> parziale, int livello) {

		if (livello == NUMERO_GIORNI_TOTALI) {
			Double costo = calcolaCosto(parziale);
		} else {
			for (Citta prova : leCitta) {
				if (aggiuntaValida(prova, parziale)) {
					parziale.add(prova);
					cerca(parziale, livello + 1);
					parziale.remove(parziale.size() - 1);
				}
			}
		}

	}

	private boolean aggiuntaValida(Citta prova, List<Citta> parziale) {

		// verifica giorni massimi
		int conta = 0;
		for (Citta precedente : parziale) {
			if (precedente.equals(prova))
				conta++;
		}
		if (conta >= NUMERO_GIORNI_CITTA_MAX)
			return false;
		// verifica giorni minimi
		if (parziale.size() == 0) // primo giorni
			return true;
		if (parziale.size() == 1 || parziale.size() == 2) // secondo o terzo giorno: non posso cambiare
			return parziale.get(parziale.size() - 1).equals(prova);
		if (parziale.get(parziale.size() - 1).equals(prova))
			return true;
		if (parziale.get(parziale.size() - 1).equals(parziale.get(parziale.size() - 2))
				&& parziale.get(parziale.size() - 2).equals(parziale.get(parziale.size() - 3)))
			return true;
		return false;

	}

	private Double calcolaCosto(List<Citta> parziale) {
		double costo = 0.0;

		for (int giorno = 1; giorno <= NUMERO_GIORNI_TOTALI; giorno++) {
			// dove mi trovo
			Citta c = parziale.get(giorno);
			// che umidita ho in quella data citta in quel giorno
			double umidita = c.getRilevamenti().get(giorno).getUmidita();
			costo += umidita;
		}
		for (int giorno = 2; giorno <= NUMERO_GIORNI_TOTALI; giorno++) {
			if (parziale.get(giorno - 1).equals(parziale.get(giorno - 2)))
				costo += COST;
		}
		
		return costo;
	}

	public String trovaSequenza(int mese) {

		return "TODO!";
	}

	private Double punteggioSoluzione(List<SimpleCity> soluzioneCandidata) {

		double score = 0.0;
		return score;
	}

	private boolean controllaParziale(List<SimpleCity> parziale) {

		return true;
	}

	public List<Citta> getCitta() {
		return leCitta;
	}

}
