package it.polito.tdp.meteo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import it.polito.tdp.meteo.Model.myEntry;
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

		List<SimpleCity> bestSequence = null;
		magicFunction(new ArrayList<>(), LocalDate.of(0000, mese, 1), bestSequence, Double.MAX_VALUE);
		
		return "TODO!";
	}

	private void magicFunction(List<SimpleCity> partialCitySeq, LocalDate partialDate, List<SimpleCity> bestCitySeq,
			Double bestScore) {

		// uscita
		if (partialDate.getDayOfMonth() > NUMERO_GIORNI_TOTALI)
			return;
		// controlli migliore
		else if (controllaParziale(partialCitySeq) && partialDate.getDayOfMonth() == NUMERO_GIORNI_TOTALI) {
			// se la sequenza è valida, quindi ho ngiorni = numerogiornitotale
			Double partialScore = punteggioSoluzione(partialCitySeq);
			if (partialScore < bestScore && partialDate.getDayOfMonth() == NUMERO_GIORNI_TOTALI) {
				// questa che ho trovato è la soluzione migliore
				bestCitySeq = partialCitySeq;
				bestScore = partialScore;
			}
		} else if (controllaParziale(partialCitySeq)) {
			// devo ancora costruire
			for (Citta c : tutteCitta) {
				int umidita = c.getUmiditaByDate(partialDate).intValue();
				if (umidita == -1) // non ho il valore per quella specifica data, provo il prossimo troncando il
									// ramo
					return;
				SimpleCity sc = new SimpleCity(c.getNome(), umidita);
				List<SimpleCity> newPartial = new ArrayList<>(partialCitySeq);
				newPartial.add(sc);
				magicFunction(newPartial, partialDate.plusDays(1), bestCitySeq, bestScore);
			}
		}

	}

	private Double punteggioSoluzione(List<SimpleCity> soluzioneCandidata) {
		// qui do per scontato che la soluzione parziale sia corretta
		// DA ESEGUIRE DOPO CONTROLLAPARZIALE!!!!!!!
		if (soluzioneCandidata.size() < 2)
			return new Double(soluzioneCandidata.get(0).getCosto());
		Double score = 0.0;
		for (int i = 1; i < soluzioneCandidata.size(); i++) {
			if (soluzioneCandidata.get(i - 1).getNome().equals(soluzioneCandidata.get(i).getNome()))
				score += COST;
			score += soluzioneCandidata.get(i).getCosto();
		}

		return score;

	}

	/**
	 * Devo verificare che: stia almeno 3 giorni in quella citta non stia piu di 6
	 * giorni in quella citta
	 * 
	 * @param parziale
	 * @return
	 */
	private boolean controllaParziale(List<SimpleCity> parziale) {
		myEntry inHotel = new myEntry(parziale.get(0).getNome(), parziale.get(0).getCosto());
		Map<String, Integer> inCitta = new HashMap<String, Integer>();
		inCitta.put(parziale.get(0).getNome(), 1);
		for (int i = 1; i < parziale.size(); i++) {
			try { // OCCHIO CHE NON PARTE DA 1!!
				if (inHotel.getKey().contentEquals(parziale.get(i).getNome())
						&& !inHotel.getKey().contentEquals(parziale.get(i + 1).getNome())) {
					// se il tipo è stato in hotel ed è il suo ultimo giorno
					inHotel.setValue(inHotel.getValue() + 1);
					if (inHotel.getValue() < NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN)
						return false;
				} else if (inHotel.getKey().contentEquals(parziale.get(i).getNome())) {
					// è rimasto fermo nella stessa città
					inHotel.setValue(inHotel.getValue() + 1);
				} else {
					// vuol dire che si è spostato, l'hotel è nuovo
					inHotel = new myEntry(parziale.get(i).getNome(), 1);
				}

				if (inCitta.containsKey(parziale.get(i).getNome())) {
					// la citta è gia presente in quelle visitate
					inCitta.put(parziale.get(i).getNome(), inCitta.get(parziale.get(i).getNome()) + 1);
				} else {
					// la citta non è mai stata visitata
					inCitta.put(parziale.get(i).getNome(), 1);
				}
				if (inCitta.get(parziale.get(i).getNome()) > NUMERO_GIORNI_CITTA_MAX)
					return false;
			} catch (IndexOutOfBoundsException e) {
				// nulla da fare, è la prima visita in quella città
			}

		}
		// so far, so good

		return true;
	}

	private class myEntry implements Entry<String, Integer> {

		private String key;
		private Integer value;

		public myEntry(String key, Integer value) {
			super();
			this.key = key;
			this.value = value;
		}

		@Override
		public String getKey() {
			return this.key;
		}

		@Override
		public Integer getValue() {
			return this.value;
		}

		@Override
		public Integer setValue(Integer value) {
			Integer oldValue = this.value;
			this.value = value;
			return oldValue;
		}

	}
}
