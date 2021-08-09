package web.entity;


import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
@Table(name ="ALBUM")
public class Album {
	public Set<BaiHat> getListBaiHat() {
		return listBaiHat;
	}

	

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
	

	
	@Column(name ="YeuThich")
	private int yeuthich;
	
	// tao 2 constructor
	
	public Album()
	{
		this.ten = "Unknown";
		this.ngayph = new Date();
		this.yeuthich = 100;
		
	}
	//tao sau

	// tao get set
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
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "album")
	private Set<BaiHat> listBaiHat = new HashSet<>();
	
	public void setListSach(Set<BaiHat> listBaiHat) {
		this.listBaiHat = listBaiHat;
	}
	public void setListBaiHat(Set<BaiHat> listBaiHat) {
		this.listBaiHat = listBaiHat;
	}

	
	
}
