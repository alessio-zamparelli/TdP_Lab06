package it.polito.tdp.meteo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;
import sun.java2d.pipe.SpanShapeRenderer.Simple;

public class Model {

	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	private List<Citta> tutteCitta;
	private List<SimpleCity> tutteCittaSemplici;
	private MeteoDAO dao;

	public Model() {
		dao = new MeteoDAO();
		// precarico la lista delle citta
		tutteCitta = dao.getCitta();
	}

	/**
	 * Dato un mese int tra 1 e 12 restituisce una stringa contenente l'umidità
	 * media per ogni città presente nel DB
	 * 
	 * @param mese il mese che si desidera
	 * @return l'elenco delle umidità medie città per città
	 */
	public String getUmiditaMedia(int mese) {

		if (tutteCitta.size() == 0)
			tutteCitta = dao.getCitta();

		return "TODO!";
	}

	/**
	 * Wrapper della funzione ricorsiva per il calcolo del percorso
	 * 
	 * @param mese
	 * @return
	 */
	public String trovaSequenza(int mese) {

		// TODO: devo inizializzare le città semplici (la lista globale)
		return "TODO!";
	}

	private void magicFunction(List<SimpleCity> partialCitySeq, LocalDate partialDate, List<SimpleCity> bestCitySeq,
			Double bestScore) {

		// controlli validità
		if (partialDate.getDayOfMonth() > NUMERO_GIORNI_TOTALI)
			return;
		// controlli migliore
		Double partial = punteggioSoluzione(partialCitySeq);
		if (partial < bestScore && partialDate.getDayOfMonth() == NUMERO_GIORNI_TOTALI) {
			// questa che ho trovato è la soluzione migliore
			bestCitySeq = partialCitySeq;
			bestScore = partial;
		}
		// altrimenti continuo a creare la soluzione finale

		for (Citta c : tutteCitta) {
			
			SimpleCity sc = new SimpleCity(c.getNome(), c.getRilevamentiByDate(partialDate));
			List<SimpleCity> newPartial = new ArrayList<>(partialCitySeq);
			newPartial.add(sc);
			magicFunction(newPartial, partialDate.plusDays(1), bestCitySeq, bestScore);

		}
	}

	private Double punteggioSoluzione(List<SimpleCity> soluzioneCandidata) {
		// qui do per scontato che la soluzione parziale sia corretta
		// DA ESEGUIRE DOPO CONTROLLAPARZIALE!!!!!!!
		if (soluzioneCandidata.size() < 2)
			return new Double(soluzioneCandidata.get(0).getCosto());
		Double score = 0.0;
		for (int i = 1; i < soluzioneCandidata.size(); i++) {
			if(soluzioneCandidata.get(i-1).getNome().equals(soluzioneCandidata.get(i).getNome()))
				score += COST;
			score+=soluzioneCandidata.get(i).getCosto();
		}

		return score;

	}

	private boolean controllaParziale(List<SimpleCity> parziale) {

		return true;
	}

}
