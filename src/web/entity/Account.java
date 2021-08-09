package web.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name="ACCOUNT")
public class Account {
	@Id
	@GeneratedValue
	@Column(name="Id")
	private int id;
	
	@Column(name="Password")
	private String password;
	
	@Column(name="Mail")
	private String mail;
	
	
	
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}
	public Account() {
		
	}
	
	
	//constructor
	public Account(String mail,String password) {
		
		this.mail = mail;
		this.password = password;
		
		
		
	}
	//mappedBy thì lấy tên bảng viết thường
	@OneToMany(mappedBy="account",fetch=FetchType.EAGER)
	private Set<Playlist> playlist = new HashSet<>();



	

	

	



	public Set<Playlist> getPlaylist() {
		return playlist;
	}

	public void setPlaylist(Set<Playlist> playlist) {
		this.playlist = playlist;
	}
}
