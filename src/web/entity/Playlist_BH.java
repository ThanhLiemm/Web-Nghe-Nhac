package web.entity;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


@Entity

@Table(name ="PLAYLIST_BH")
public class Playlist_BH {

	@Id
	@Column(name = "IdPlaylist")
	
	private int idplaylist;

	
	@Column(name = "IdBaiHat")
	private int idbaihat;

	public int getIdplaylist() {
		return idplaylist;
	}

	public void setIdplaylist(int idplaylist) {
		this.idplaylist = idplaylist;
	}

	public int getIdbaihat() {
		return idbaihat;
	}

	public void setIdbaihat(int idbaihat) {
		this.idbaihat = idbaihat;
	}
	public Playlist_BH()
	{
		
	}
	
}
