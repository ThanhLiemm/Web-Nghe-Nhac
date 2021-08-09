package web.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="PLAYLIST")
public class Playlist {
	@Id
	@GeneratedValue
	@Column(name="Id")
	private int id;
	
	@Column(name="PlaylistName")
	private String playlistname;
	
	
	
	
	public Playlist()
	{
		this.ttx=1;
	}
	public Playlist(int id,String playlistname) {
		this.id = id;
		this.playlistname=playlistname;
		
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPlaylistname() {
		return playlistname;
	}
	public void setPlaylistname(String playlistname) {
		this.playlistname = playlistname;
	}
	//join column lấy tên cột reference
	@ManyToOne
	@JoinColumn(name="IdAccount", nullable = false)
	public Account account;




	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	@Column(name = "TrangThaiXoa")
	private int ttx;

	public int getTtx() {
		return ttx;
	}

	public void setTtx(int ttx) {
		this.ttx = ttx;
	}
}
