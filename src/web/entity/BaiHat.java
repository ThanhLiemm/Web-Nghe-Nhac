package web.entity;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;



@Entity
@Table(name ="BAIHAT")
public class BaiHat {
	@Id
	@GeneratedValue
	@Column(name="Id")
	private int id;
	
	@Column(name = "Ten")
	private String ten;
	
	@Column(name = "NgayPH")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern ="dd/MM/yyyy")
	private Date ngayph;
	
	@Column(name = "ImageCore")
	private String imagecore;
	

	
	@Column(name = "YeuThich")
	private int yeuthich;
	
	//IdAblum khỏi cân chút nữa dùng quan hệ 1 nhiều
	
	@Column(name = "Audio")
	private String audio;
	
	@Column(name = "TheLoai")
	private String theloai;
	
	@Column(name = "Lyrics")
	private String lyrics;
	

	
	@Column(name = "NhacSi")
	private String nhacsi;
	

	
	@Column(name = "QuocGia")
	private String quocgia;
	
	//tao quan he 1 nhien
	//join column lấy tên cột reference
		@ManyToOne
		@JoinColumn(name = "IdAlbum", nullable = false)
		public Album album;

	public Album getAlbum() {
			return album;
		}

		public void setAlbum(Album album) {
			this.album = album;
		}
//		@Column(name = "IdAlbum")
//		private int idalbum;
//		
//	public int getIdalbum() {
//			return idalbum;
//		}
//
//		public void setIdalbum(int idalbum) {
//			this.idalbum = idalbum;
//		}
	
	//tao 2 constructor
	public BaiHat()
	{
		this.ngayph = new Date();
		this.ten = "Unknown";
		this.casi="Unknown";
		this.nhacsi = "Unknown";
		
	
	}
	//tam thời cái này để sau này viết
	//public BaiHat(String ten,)



	//thêm getter và setter cho tất cả các thuộc tính
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTen() {
		return ten;
	}

	public void setTen(String ten) {
		this.ten = ten;
	}

	public Date getNgayph() {
		return ngayph;
	}

	public void setNgayph(Date ngayph) {
		this.ngayph = ngayph;
	}

	public String getImagecore() {
		return imagecore;
	}

	public void setImagecore(String imagecore) {
		this.imagecore = imagecore;
	}



	public int getYeuthich() {
		return yeuthich;
	}

	public void setYeuthich(int yeuthich) {
		this.yeuthich = yeuthich;
	}

	public String getAudio() {
		return audio;
	}

	public void setAudio(String audio) {
		this.audio = audio;
	}

	public String getTheloai() {
		return theloai;
	}

	public void setTheloai(String theloai) {
		this.theloai = theloai;
	}

	public String getLyrics() {
		return lyrics;
	}

	public void setLyrics(String lyrics) {
		this.lyrics = lyrics;
	}



	public String getNhacsi() {
		return nhacsi;
	}

	public void setNhacsi(String nhacsi) {
		this.nhacsi = nhacsi;
	}

	



	public String getQuocgia() {
		return quocgia;
	}

	public void setQuocgia(String quocgia) {
		this.quocgia = quocgia;
	}
	//them ca sĩ
	@Column(name = "CaSi")
	private String casi;

	public String getCasi() {
		return casi;
	}

	public void setCasi(String casi) {
		this.casi = casi;
	}
	//them 1 cai constructor để lúc thêm cho nhanh
	public BaiHat(String ten,Date ngayph, String imagecore, int yeuthich, String audio, String theloai, String lyrics, String nhacsi, String quocgia, String casi)
	{
		this.ten = ten;
		this.ngayph = ngayph;
		this.imagecore = imagecore;
		this.yeuthich = yeuthich;
		this.audio = audio;
		this.theloai = theloai;
		this.lyrics = lyrics;
		this.nhacsi = nhacsi;
		this.quocgia = quocgia;
		this.casi = casi;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
