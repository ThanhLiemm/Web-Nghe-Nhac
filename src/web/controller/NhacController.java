package web.controller;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import web.entity.*;
@Controller
@Transactional
public class NhacController {
	// tạo session factory trc để mở session mặc định
		@Autowired
		SessionFactory factory;
		//hiển thị nhạc
		@RequestMapping(value="Nhac", method = RequestMethod.GET)
		public String BaiHat(ModelMap model,@RequestParam("Id") String id) {
			model.addAttribute("login",IndexController.login);	
			int temp = Integer.parseInt(id);
			//truy cap csdl
			Session session = factory.getCurrentSession();
			//hk để trung với tên bảng vc thật sử dụng tên class
			String hql = "from BaiHat where id = '"+temp + "'";
			
			Query query = session.createQuery(hql);
			//do ve view
			List<BaiHat> baihat = query.list();
			model.addAttribute("baihat", baihat);
			return "baihat";
		}
		//hiển thị danh sách bài hát trong album
		@RequestMapping(value="Album", method = RequestMethod.GET)
		public String Album(ModelMap model,@RequestParam("Id") String id)
		{
			model.addAttribute("login",IndexController.login);	
			int temp = Integer.parseInt(id);
			//truy cap csdl
			Session session = factory.getCurrentSession();
			//hk để trung với tên bảng vc thật sử dụng tên class
			String hql = "from BaiHat where idalbum = '"+temp + "'";
			
			Query query = session.createQuery(hql);
			//do ve view
			List<BaiHat> baihat = query.list();
			model.addAttribute("baihat", baihat);
			return "baihat";
			
		}
		//hiển thị danh sách album
		@RequestMapping(value="albums", method = RequestMethod.GET)
		public String Albums(ModelMap model)
		{
			model.addAttribute("login",IndexController.login);	
			
			//truy cap csdl
			Session session = factory.getCurrentSession();
			//hk để trung với tên bảng vc thật sử dụng tên class
			String hql = " from Album where id !=1 and yeuthich !=0 order by NEWID() ";
			
			Query query = session.createQuery(hql).setMaxResults(30);
			//do ve view
			List<Album> album = query.list();
			model.addAttribute("album", album);
			return "albums";
			
		}
		//search theo thể loại
		@RequestMapping(value="theloai")
		public String TheLoai(ModelMap model,@RequestParam("Loai") String loai)
		{
			model.addAttribute("login",IndexController.login);	
			
			//truy cap csdl
			Session session = factory.getCurrentSession();
			//hk để trung với tên bảng vc thật sử dụng tên class
			String hql = "from BaiHat where theloai = '"+loai + "' order by NEWID()";
			
			Query query = session.createQuery(hql).setMaxResults(10);
			//do ve view
			List<BaiHat> baihat = query.list();
			model.addAttribute("baihat", baihat);
			return "baihat";
			
		}
		//search theo quốc gia
		@RequestMapping(value="quocgia")
		public String QuocGia(ModelMap model,@RequestParam("QuocGia") String quocgia)
		{
			model.addAttribute("login",IndexController.login);
			//truy cap csdl
			Session session = factory.getCurrentSession();
			//hk để trung với tên bảng vc thật sử dụng tên class
			String hql = "from BaiHat where quocgia = '"+quocgia + "' order by(YeuThich) DESC";
			
			Query query = session.createQuery(hql);
			//do ve view
			List<BaiHat> baihat = query.list();
			model.addAttribute("baihat", baihat);
			return "baihat";
		}
		//hiển thị danh sách playlist
		@RequestMapping(value = "playlist")
		public String playlist(ModelMap model,String message)
		{
			model.addAttribute("login",IndexController.login);
			Session session = factory.getCurrentSession();
			String hql = "from Playlist where idaccount = ' " + IndexController.iduser + "' and ttx !=0 ";
			Query query = session.createQuery(hql);
			List<Playlist> pl = query.list();
			if(pl.size()==0)
			{
				model.addAttribute("message","Chưa Có Đánh Playlist Hãy Tạo");
			}
			
			model.addAttribute("message", message);
			model.addAttribute("playlist", pl);
			return "playlist/list";
		}
		//tao playlist
		@RequestMapping(value = "createplaylist")
		public String createplaylist(ModelMap model, @RequestParam("tenplaylist") String ten)
		{
			model.addAttribute("login",IndexController.login);
			//khởi tạo
			Playlist pl = new Playlist();
			pl.setPlaylistname(ten);
			//lấy ra account để thêm
			Session session = factory.getCurrentSession();
			String hql = "from Account where id = ' " + IndexController.iduser + "'";
			Query query = session.createQuery(hql);
			Account account = (Account)query.uniqueResult();
			pl.setAccount(account);
			
			
			
			//thêm
			session = factory.openSession();
			Transaction t = session.beginTransaction();
			try {
				session.save(pl);
				t.commit();
				
				// deay 2s để nó tạo file 
				TimeUnit.SECONDS.sleep(2);
			}
			catch(Exception e) {
				t.rollback();
				return playlist(model,"Không Tạo Được");
			}
			finally {
				session.close();
				
			}
			
			
			return playlist(model,"Đã Tạo Thành Công");
		}
		//hien thi nhac trong playlist
		@RequestMapping(value = "listplaylist")
		public String listplaylist(ModelMap model, @RequestParam("id") int id)
		{
			model.addAttribute("login",IndexController.login);
			//lấy danh sách playlist_ph
			Session session = factory.getCurrentSession();
			//hk để trung với tên bảng vc thật sử dụng tên class
			String hql = "select idbaihat from Playlist_BH where idplaylist = '"+ id + "'";
			
			Query query = session.createQuery(hql);
			//do ve view
			List<Object[]> pl_bh = query.list();
			//thử
			
			
			//tạo vòng for lấy từng bản nhạc ra
			List<BaiHat> listbaihat = new ArrayList<>();
			
			for(int i =0;i<pl_bh.size();i++)
			{
				
				hql = "from BaiHat where id = '"+ pl_bh.get(i) + "'";
				
				query = session.createQuery(hql);
				//do ve view
				BaiHat bh= new BaiHat();
				bh = (BaiHat)query.uniqueResult();
					
				listbaihat.add(bh);
				
				
			}
			IndexController.idplaylist = id;
			model.addAttribute("baihat",listbaihat);
			
			//gửi addtribute vào
			return "playlist/listnhac";
		}
		//Thêm nhạc chạy vào chọn playlist
		@RequestMapping(value = "insert")
		public String insertplaylisttemp(ModelMap model, @RequestParam("id") int id)
		{
			model.addAttribute("login",IndexController.login);
			//lưu id ai hat
			IndexController.idbaihat = id;
			return playlist(model,"Hãy Chọn Playlist!");
		}
		//chọn bài hat -> chọn playlist
		@RequestMapping(value = "insertplaylist")
		public String insertplaylist(ModelMap model,@RequestParam("id") int id)
		{
			if(IndexController.idbaihat ==0)
			{
				return playlist(model,"Hãy Chọn Bài Hát");
			}
			//lấy idplaylist
			IndexController.idplaylist =id;
			//bắt đầu thêm vào
			Playlist_BH pl_bh = new Playlist_BH();
			pl_bh.setIdbaihat(IndexController.idbaihat);
			pl_bh.setIdplaylist(IndexController.idplaylist);
			//tạo nè
			//thêm
			Session session = factory.openSession();
			Transaction t = session.beginTransaction();
			try {
				session.save(pl_bh);
				t.commit();
				
				// deay 2s để nó tạo file 
				TimeUnit.SECONDS.sleep(2);
			}
			catch(Exception e) {
				//bật lại 
				IndexController.idbaihat =0;
				
				t.rollback();
				return playlist(model,"Không Thêm Được Nhạc Vào PlayList " + e.getMessage());
			}
			finally {
				session.close();
			
			}
			
			IndexController.idbaihat =0;
			
			return listplaylist(model,IndexController.idplaylist);
		}
		//xóa nhạc
		@RequestMapping(value = "deletemusic")
		public String deletemusic(ModelMap model,@RequestParam("id") int id)
		{
			model.addAttribute("login",IndexController.login);
			//tao 
			Playlist_BH pl_bh = new Playlist_BH();
			pl_bh.setIdbaihat(3);
			pl_bh.setIdplaylist(3);
			
//			//tìm trong csdl 
//			Session session = factory.getCurrentSession();
//			//hk để trung với tên bảng vc thật sử dụng tên class
//			String hql = "from Playlist_BH where idbaihat = '"+ id + "' and idplaylist = '"+IndexController.idplaylist + "'";
//			
//			Query query = session.createQuery(hql);
//			//do ve view
//			Playlist_BH pl_bh = (Playlist_BH) query.uniqueResult();
//			//thử
			
			
			//tìm 
			 Session session = factory.openSession();
			Transaction t = session.beginTransaction();
			try {
				session.delete(pl_bh);
				t.commit();
				
				// deay 2s để nó tạo file 
				TimeUnit.SECONDS.sleep(2);
			}
			catch(Exception e) {
				
				
				t.rollback();
				return playlist(model,e.getMessage());
			}
			finally {
				session.close();
			
			}
			//update
			return listplaylist(model,IndexController.idplaylist);
		}
		//xóa playlist
		@RequestMapping(value ="deleteplaylist")
		public String deleteplaylist(ModelMap model, @RequestParam("id") int id)
		{
			model.addAttribute("login",IndexController.login);
			//tìm ra playlist
			model.addAttribute("login",IndexController.login);
			Session session = factory.getCurrentSession();
			String hql = "from Playlist where id= ' " + id + "'";
			Query query = session.createQuery(hql);
			Playlist pl = (Playlist) query.uniqueResult();
			//update ttx = 0;
			pl.setTtx(0);
			session = factory.openSession();
			Transaction t = session.beginTransaction();
			try {
				session.update(pl);
				t.commit();
				
				// deay 2s để nó tạo file 
				TimeUnit.SECONDS.sleep(2);
			}
			catch(Exception e) {
				
				
				t.rollback();
				return playlist(model,"xóa thất bại " + e.getMessage());
			}
			finally {
				session.close();
			
			}
			
			return playlist(model,"đã xóa thành công");
		}
}

