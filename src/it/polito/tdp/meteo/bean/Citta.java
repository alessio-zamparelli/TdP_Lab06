package it.polito.tdp.meteo.bean;

import java.util.List;
import java.time.LocalDate;
//import java.util.Date;

public class Citta {

	private String nome;
	private List<Rilevamento> rilevamenti;
	private int counter = 0;

	public Citta(String nome) {
		this.nome = nome;
	}

	public Citta(String nome, List<Rilevamento> rilevamenti) {
		this.nome = nome;
		this.rilevamenti = rilevamenti;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<Rilevamento> getRilevamenti() {
		return rilevamenti;
	}

	public void setRilevamenti(List<Rilevamento> rilevamenti) {
		this.rilevamenti = rilevamenti;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public void increaseCounter() {
		this.counter += 1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Citta other = (Citta) obj;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return nome;
	}

	/**
	 * cerca l'umidita per il giorno e mese selezionati (non l'anno, serve?)
	 * @param partialDate la data da cercare
	 * @return l'umidita per quel giorno o -1,0se non la trova
	 */
	public Double getUmiditaByDate(LocalDate partialDate) {
		return this.rilevamenti.parallelStream().filter(a -> 
				a.getData().getMonth().equals(partialDate.getMonth())
				&& a.getData().getDayOfMonth()==partialDate.getDayOfMonth())
			.map(a -> a.getUmidita()).findAny().orElse(-1.0);
	}

}
