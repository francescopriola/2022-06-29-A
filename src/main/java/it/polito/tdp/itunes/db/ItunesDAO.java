package it.polito.tdp.itunes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.itunes.model.Album;
import it.polito.tdp.itunes.model.Artist;
import it.polito.tdp.itunes.model.Genre;
import it.polito.tdp.itunes.model.MediaType;
import it.polito.tdp.itunes.model.Playlist;
import it.polito.tdp.itunes.model.Track;

public class ItunesDAO {
	
	public Map<Integer, Album> getAllAlbums(){
		final String sql = "SELECT * FROM Album";
		Map<Integer, Album> result = new HashMap<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.put(res.getInt("AlbumId"), new Album(res.getInt("AlbumId"), res.getString("Title")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public List<Artist> getAllArtists(){
		final String sql = "SELECT * FROM Artist";
		List<Artist> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Artist(res.getInt("ArtistId"), res.getString("Name")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public List<Playlist> getAllPlaylists(){
		final String sql = "SELECT * FROM Playlist";
		List<Playlist> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Playlist(res.getInt("PlaylistId"), res.getString("Name")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public List<Track> getAllTracks(){
		final String sql = "SELECT * FROM Track";
		List<Track> result = new ArrayList<Track>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Track(res.getInt("TrackId"), res.getString("Name"), 
						res.getString("Composer"), res.getInt("Milliseconds"), 
						res.getInt("Bytes"),res.getDouble("UnitPrice")));
			
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public List<Genre> getAllGenres(){
		final String sql = "SELECT * FROM Genre";
		List<Genre> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Genre(res.getInt("GenreId"), res.getString("Name")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public List<MediaType> getAllMediaTypes(){
		final String sql = "SELECT * FROM MediaType";
		List<MediaType> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new MediaType(res.getInt("MediaTypeId"), res.getString("Name")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public List<Album> getVertex(int n){
		final String sql = "SELECT a.`AlbumId` as id, a.`Title` as t, COUNT(t.`TrackId`) as n "
				+ "FROM album a, track t "
				+ "WHERE a.`AlbumId` = t.`AlbumId` "
				+ "GROUP BY a.`AlbumId`, a.`Title` "
				+ "HAVING COUNT(t.`TrackId`) > ?";
		List<Album> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, n);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				Album a = new Album(res.getInt("id"), res.getString("t"));
				a.setnCanzoni(res.getInt("n"));
				result.add(a);
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
//	public List<Archi> getEdge(int n, Map<Integer, Album> idMap){
//		String sql = "SELECT t1.`AlbumId` as t1, t2.`AlbumId` as t2, COUNT(DISTINCT t1.`TrackId`) as n1, COUNT(DISTINCT t2.`TrackId`) as n2, ABS(COUNT(DISTINCT t1.`TrackId`) - COUNT(DISTINCT t2.`TrackId`)) as peso "
//				+ "FROM track t1, track t2 "
//				+ "WHERE t1.`AlbumId` > t2.`AlbumId` "
//				+ "GROUP BY t1.`AlbumId`, t2.`AlbumId` "
//				+ "HAVING ABS(COUNT(DISTINCT t1.`TrackId`) - COUNT(DISTINCT t2.`TrackId`)) > 0  "
//				+ "AND COUNT(DISTINCT t1.`TrackId`) > ? AND COUNT(DISTINCT t2.`TrackId`) > ?";
//		
//		List<Archi> result = new LinkedList<>();
//		
//		try {
//			Connection conn = DBConnect.getConnection();
//			PreparedStatement st = conn.prepareStatement(sql);
//			st.setInt(1, n);
//			st.setInt(2, n);
//			ResultSet res = st.executeQuery();
//			
//			while(res.next()) {
//				Album a1 = idMap.get(res.getInt("t1"));
//				a1.setnCanzoni(res.getInt("n1"));
//				Album a2 = idMap.get(res.getInt("t2"));
//				a2.setnCanzoni(res.getInt("n2"));
//				Archi a = null;
//				
//				if(a1.getnCanzoni() < a2.getnCanzoni())
//					a = new Archi(a1, a2, res.getDouble("peso"));
//				else if(a1.getnCanzoni() > a2.getnCanzoni())
//					a = new Archi(a2, a1, res.getDouble("peso"));
//				
//				if(a != null)
//					result.add(a);
//			}
//			conn.close();
//		} catch (SQLException e) {
//			e.printStackTrace();
//			throw new RuntimeException("SQL Error");
//		}
//		return result;
//	}
	
}
