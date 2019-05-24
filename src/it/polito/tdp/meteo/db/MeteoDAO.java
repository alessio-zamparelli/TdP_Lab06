package it.polito.tdp.meteo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.Rilevamento;

public class MeteoDAO {

	public List<Rilevamento> getAllRilevamenti() {

		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data").toLocalDate(), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {

		final String sql = "SELECT * FROM situazione WHERE localita=? AND MONTH(`data`) = ?";

		List<Rilevamento> rilevamenti = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, localita);
			st.setInt(2, mese);
			ResultSet rs = st.executeQuery();

			while(rs.next()) {
				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("data").toLocalDate(), rs.getInt("umidita"));
				rilevamenti.add(r);
			}

			conn.close();

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return rilevamenti;
	}

	public Double getAvgRilevamentiLocalitaMese(int mese, String localita) {

		final String sql = "SELECT Localita , AVG(UMIDITA) AS UmiditaMedia FROM situazione "
				+ "WHERE localita=? AND MONTH(data)=? GROUP BY localita";

		double media = -1;
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, localita);
			st.setInt(2, mese);
			ResultSet rs = st.executeQuery();

			if (rs.next())
				media = rs.getDouble("UmiditaMedia");

			conn.close();

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return media;
	}

	public List<Citta> getCitta() {

		final String sql = "SELECT * FROM situazione ORDER BY Localita ASC";

		Map<String, Citta> citta = new HashMap<>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			Citta c;
			while (rs.next()) {

				if (citta.get(rs.getString("Localita")) == null) // la città non esiste
					c = new Citta(rs.getString("Localita"), new ArrayList<Rilevamento>());
				else
					c = citta.get(rs.getString("Localita"));

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data").toLocalDate(), rs.getInt("Umidita"));
				c.getRilevamenti().add(r);
				citta.put(c.getNome(), c);
			}

			conn.close();

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return new ArrayList<Citta>(citta.values());
	}

}
