package web.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


import web.entity.*;
@Controller
@Transactional



public class InsertController {
	public static int indexbh =0;
	public static int indexal = 0;
	// tạo session factory trc để mở session mặc định
	@Autowired
	SessionFactory factory;
		
			
			//thêm nhạc
			public static List<Album> album;
			public static List<BaiHat> baihat;
	//list nhạc		
	@RequestMapping("listnhac.htm")
	public String listNhac(ModelMap model,String message)
	{
		
		//hiển thị đăng nhập
		model.addAttribute("login",IndexController.login);	
		
		
		//truy cap csdl
		Session session = factory.getCurrentSession();
		//hk để trung với tên bảng vc thật sử dụng tên class
		String hql = "from BaiHat";
		
		Query query = (Query) session.createQuery(hql);
		//do ve view
		 baihat = query.list();
		model.addAttribute("baihat", baihat);
		
		// load trước album
		hql = " from Album ";
		
		query = session.createQuery(hql);
		//do ve view
		album = query.list();
		
		model.addAttribute("message",message);
		
		return "nhac/listnhac";
	}
	//get thêm nhạc
	@RequestMapping(value = "insertnhac.htm",method = RequestMethod.GET)
	public String  insertNhac(ModelMap model,String message)
	{
		model.addAttribute("baihat", new BaiHat());
		//lấy ds ablum
		//truy cap csdl
		Session session = factory.getCurrentSession();
		//hk để trung với tên bảng vc thật sử dụng tên class
		String hql = " from Album where yeuthich !=0 ";
		
		Query query = session.createQuery(hql);
		//do ve view
		album = query.list();
		model.addAttribute("album", album);
		model.addAttribute("message", message);
		return "nhac/insertnhac";
				
	}
	
	@Autowired
	ServletContext context ;
	//post thêm nhạc
	@RequestMapping(value = "insertnhac.htm",method = RequestMethod.POST)
	public String insertNhac(ModelMap model,@ModelAttribute("baihat") BaiHat bh, @RequestParam("fileaudio") MultipartFile audio, @RequestParam("fileimagecore") MultipartFile imagecore,
			@RequestParam("IdAlbum") int idalbum)
	{
		model.addAttribute("login",IndexController.login);	
		//xử lí file 
		if(audio.isEmpty() || imagecore.isEmpty())
		{
				return insertNhac(model,"Chưa Chọn File");
		}
		if(!imagecore.getContentType().equals("image/jpeg") && !imagecore.getContentType().equals("image/png") && !imagecore.getContentType().equals("image/gif") )
		{
			 return insertNhac(model,"Chọn sai định dạng file image");
		}
		if( !audio.getContentType().equals("audio/mpeg") )
		{
			 return insertNhac(model,"Chọn sai định dạng file audio");
		}
		try {
			//tạo tên để không bị trùng
			long nowTime = System.currentTimeMillis();
			String temp = String.valueOf(nowTime);
			String path = context.getRealPath("img/Images/a"+temp+".jpg");
			String path2 = context.getRealPath("audio/b" + temp+".mp3");
			bh.setImagecore("img/Images/a"+temp+".jpg");
			bh.setAudio("audio/b" + temp+".mp3");
			
			//load lại cai album
			// load trước album
			String hql = " from Album ";
			Session session = factory.getCurrentSession();
			Query query = session.createQuery(hql);
			//do ve view
			List<Album> al = query.list();
			//thêm cai album vào
			bh.setAlbum(al.get(idalbum-1));
			
			//chuyển vị trí
			imagecore.transferTo(new File(path));
			audio.transferTo(new File(path2));
			
		}
		catch(Exception e)
		{
			return insertNhac(model,"Lưu Thất Bại" +e.getMessage());
		}
		
		
		String me="Thêm Mới thành công";
		//xử lí bên trong 
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		try {
			session.save(bh);
			t.commit();
			
			// deay 2s để nó tạo file 
			TimeUnit.SECONDS.sleep(4);
		}
		catch(Exception e) {
			t.rollback();
			me = "thêm mới thất bại";
		}
		finally {
			session.close();
			
		}
		
		return listNhac(model,me);
	}
	//xóa nhạc
	@RequestMapping(value ="deletenhac.htm")
	public String deletenhac(ModelMap model,@RequestParam("id") int id)
	{
		String me = "Xóa thành công";
		String hql = "from BaiHat where id = " + "'"+id+"'";
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		Query query = session.createQuery(hql);
		BaiHat baihat = (BaiHat) query.uniqueResult();

		try {
			session.delete(baihat);
			t.commit();
			
		}
		catch(Exception e) {
			t.rollback();
			me = "xóa thất bại";
		}
		finally {
			session.close();
			
		}
		return listNhac(model,me);
	}
	//update get nhac
	@RequestMapping(value = "updatenhac.htm",method = RequestMethod.GET)
	public String updatenhac(ModelMap model,String message,@RequestParam("id") int id) {
		
		String hql = "FROM BaiHat where id = " + "'"+id+"'";
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		Query query = session.createQuery(hql);
		Object baihat = query.uniqueResult();
		
		//đổ lại album
		hql = "FROM Album where yeuthich!=0";
		query = session.createQuery(hql);
		List<Album> al =  query.list();
		
		model.addAttribute("album",al);
		model.addAttribute("baihat",baihat);
		model.addAttribute("message",message);
		return "nhac/updatenhac";
	}
	//updatenhac post
	@RequestMapping(value = "updatenhac.htm",method = RequestMethod.POST)
	public String update(ModelMap model,@ModelAttribute("baihat") BaiHat bh,@RequestParam("IdAlbum") int idalbum,@RequestParam("id") int id
			, @RequestParam("fileaudio") MultipartFile audio, @RequestParam("fileimagecore") MultipartFile imagecore){
		//xử lí lại file
		//xử lí file 
				if(audio.isEmpty() || imagecore.isEmpty())
				{
						return updatenhac(model,"Chưa Chọn File",id);
				}
				if(!imagecore.getContentType().equals("image/jpeg") && !imagecore.getContentType().equals("image/png") && !imagecore.getContentType().equals("image/gif") )
				{
					 return updatenhac(model,"Chọn sai định dạng file image",id);
				}
				if( !audio.getContentType().equals("audio/mpeg") )
				{
					 return updatenhac(model,"Chọn sai định dạng file audio",id);
				}
				try {
					//tạo tên để không bị trùng
					long nowTime = System.currentTimeMillis();
					String temp = String.valueOf(nowTime);
					String path = context.getRealPath("img/Images/a"+temp+".jpg");
					String path2 = context.getRealPath("audio/b" + temp+".mp3");
					bh.setImagecore("img/Images/a"+temp+".jpg");
					bh.setAudio("audio/b" + temp+".mp3");
					
					//thêm cai album vào
					bh.setAlbum(album.get(idalbum-1));
					
					//chuyển vị trí
					imagecore.transferTo(new File(path));
					audio.transferTo(new File(path2));
					
				}
				catch(Exception e)
				{
					return listNhac(model,"Lưu Thất Bại");
				}
		
		//phần update
		String me = "Update thành công";
		bh.setId(id);
		//thêm cai album vào
		
		bh.setAlbum(album.get(idalbum-1));
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		try {
			session.update(bh);
			// deay 2s để nó tạo file 
			TimeUnit.SECONDS.sleep(2);
			t.commit();
			
			
		}
		catch(Exception e) {
			t.rollback();
			me = "Update thất bại" + String.valueOf(bh.getId());
		}
		finally {
			session.close();
			
		}
		return listNhac(model,me);
	}
	
//-----------------------------------------------------------------// phần albums

	//list album
@RequestMapping("listalbum.htm")
	public String listalbum(ModelMap model,String message)
{
	//hiển thị đăng nhập
	model.addAttribute("login",IndexController.login);	
	
	
	//truy cap csdl
	Session session = factory.getCurrentSession();
	//hk để trung với tên bảng vc thật sử dụng tên class
	String hql = "from Album";
	
	Query query = (Query) session.createQuery(hql);
	//do ve view
	album = query.list();
	model.addAttribute("album", album);

	
	model.addAttribute("message",message);
	
	return "album/listalbum";
}
	//update album get
	@RequestMapping(value = "updatealbum.htm",method = RequestMethod.GET)
	public String updatealbum(ModelMap model,String message,@RequestParam("id") int id) {
		
		String hql = "FROM Album where id = " + "'"+id+"'";
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		Query query = session.createQuery(hql);
		Object album = query.uniqueResult();
		
		model.addAttribute("album",album);
		
		model.addAttribute("message",message);
		return "album/updatealbum";

}
	//update album post
	@RequestMapping(value = "updatealbum.htm",method = RequestMethod.POST)
	public String updatealbum(ModelMap model,@ModelAttribute("album") Album ab,@RequestParam("id") int id
, @RequestParam("fileimagecore") MultipartFile imagecore){
		//xử lí lại file
		//xử lí file 
				if( imagecore.isEmpty())
				{
						return updatealbum(model,"Chưa Chọn File",id);
				}
				if(!imagecore.getContentType().equals("image/jpeg") && !imagecore.getContentType().equals("image/png") && !imagecore.getContentType().equals("image/gif") )
				{
					 return updatealbum(model,"Chọn sai định dạng file image",id);
				}
			
				try {
					//lưu vào không bị trùng
					long nowTime = System.currentTimeMillis();
					String temp = String.valueOf(nowTime);
					String path = context.getRealPath("img/Images/a"+ temp+".jpg");
					
					ab.setImagecore("img/Images/a"+ temp+".jpg");
					
					
					
					
					//chuyển vị trí
					imagecore.transferTo(new File(path));
					
					
				}
				catch(Exception e)
				{
					return listalbum(model,"Lưu Thất Bại");
				}
		
		//phần update
		String me="Thêm Mới thành công " + String.valueOf(album.size());
		ab.setId(id);
		//thêm cai album vào
		
		
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		try {
			session.update(ab);
			// deay 2s để nó tạo file 
			TimeUnit.SECONDS.sleep(6);
			t.commit();
			
			
		}
		catch(Exception e) {
			t.rollback();
			me = "Update thất bại " + e.getMessage() ;
		}
		finally {
			session.close();
			
		}
		return listalbum(model,me);
	}
	//them album get
	@RequestMapping(value = "insertalbum.htm",method = RequestMethod.GET)
	public String insertalbum(ModelMap model,String message)
	{
		model.addAttribute("album", new Album());
		model.addAttribute("message", message);
		return "album/insertalbum";
				
	}
	//thêm album post
	@RequestMapping(value = "insertalbum.htm",method = RequestMethod.POST)
	public String inseralbum(ModelMap model,@ModelAttribute("album") Album ab, @RequestParam("fileimagecore") MultipartFile imagecore)
	{
		//xử lí file 
		if( imagecore.isEmpty())
		{
				return insertalbum(model,"Chưa Chọn File");
		}
		if(!imagecore.getContentType().equals("image/jpeg") && !imagecore.getContentType().equals("image/png") && !imagecore.getContentType().equals("image/gif") )
		{
			 return insertalbum(model,"Chọn sai định dạng file image");
		}
		
		try {
			//lưu vào thuoc tinh cua bean
			//lưu vào không bị trùng
			long nowTime = System.currentTimeMillis();
			String temp = String.valueOf(nowTime);
			String path = context.getRealPath("img/Images/a"+ temp+".jpg");
			
			ab.setImagecore("img/Images/a"+ temp+".jpg");
			
			
			
			
			//chuyển vị trí
			imagecore.transferTo(new File(path));
			
			
		}
		catch(Exception e)
		{
			return insertalbum(model,"Lưu Thất Bại");
		}
		
		
		String me="Thêm Mới thành công " + String.valueOf(album.size());
		//xử lí bên trong 
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		try {
			session.save(ab);
			t.commit();
			
			// deay 2s để nó tạo file 
			TimeUnit.SECONDS.sleep(6);
		}
		catch(Exception e) {
			t.rollback();
			me = "thêm mới thất bại";
		}
		finally {
			session.close();
			
		}
		
		return listalbum(model,me);
	}
	//xóa album
	@RequestMapping(value ="deletealbum.htm")
	public String deletealbum(ModelMap model,@RequestParam("id") int id)
		{
			if(id == 1)
			{
				return listalbum(model,"không thể xóa album này");
			}
			String me = "Xóa thành công";
			String hql = "from Album where id = " + "'"+id+"'";
			Session session = factory.openSession();
			Transaction t = session.beginTransaction();
			Query query = session.createQuery(hql);
			Album ab = (Album) query.uniqueResult();
			//cho hình biến mất và lượt yêu thích =0
			ab.setImagecore("");
			ab.setYeuthich(0);
			try {
				session.update(ab);;
				t.commit();
				
			}
			catch(Exception e) {
				t.rollback();
				me = "xóa thất bại";
			}
			finally {
				session.close();
				
			}
			return listalbum(model,me);
		}


	//tìm kiếm list nhạc	
	@RequestMapping("timkiemlistnhac.htm")
	public String timkiemlistnhac(ModelMap model,String message,@RequestParam("name") String name)
{
	
	//hiển thị đăng nhập
	model.addAttribute("login",IndexController.login);	
	
	
	//truy cap csdl
	Session session = factory.getCurrentSession();
	//hk để trung với tên bảng vc thật sử dụng tên class
	//String hql = "from BaiHat where ten like N'%"  + name +"%'";
	String hql = "from BaiHat where ten like '%"  + name +"%'";
	
	Query query = (Query) session.createQuery(hql);
	//do ve view
	 baihat = query.list();
	model.addAttribute("baihat", baihat);
	
	// load trước album
	hql = " from Album ";
	
	query = session.createQuery(hql);
	//do ve view
	album = query.list();
	
	model.addAttribute("message",message);
	
	return "nhac/listnhac";
}
	//tìm kiếm list album
	@RequestMapping("timkiemlistalbum.htm")
	public String listalbum(ModelMap model,String message,@RequestParam("name") String name)
{
	//hiển thị đăng nhập
	model.addAttribute("login",IndexController.login);	
	
	
	//truy cap csdl
	Session session = factory.getCurrentSession();
	//hk để trung với tên bảng vc thật sử dụng tên class
	//String hql = "from Album where ten like N'%:name%'";
	String hql = "from Album where ten like '%"  + name +"%'";
	
	Query query = (Query) session.createQuery(hql);
	
	//do ve view
	List<Album> al = query.list();
	
	model.addAttribute("album", al);

	
	model.addAttribute("message",message);
	
	return "album/listalbum";
}
}








