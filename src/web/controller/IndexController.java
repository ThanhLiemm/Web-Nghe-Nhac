package web.controller;



import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import web.entity.*;
@Controller
@Transactional
public class IndexController {
	public static boolean login = false;
	public static int value ;
	public static long starttime;
	public static long endtime;
	public static String registermail="";
	public static String registerpassword="";
	public static int iduser=0;
	public static int idplaylist = 0;
	public static int idbaihat =0;
	// tạo session factory trc để mở session mặc định
	@Autowired
	SessionFactory factory;
	//tạo mail
	@Autowired
	JavaMailSender mailer;
	//nạp vào trang index
	@RequestMapping("/index")
	public String home(ModelMap model) {
		model.addAttribute("login",login);
		//đổ csdl về
		//try cập cơ sở dữ liệu
		Session session = factory.getCurrentSession();
				//hk để trung với tên bảng vc thật sử dụng tên class
		String hql = "from BaiHat order by(NgayPH)";
				
		Query query = (Query) session.createQuery(hql).setMaxResults(12);
		
		
		//đẩy xuốt view
		
		List<BaiHat> baihat = query.list();
		model.addAttribute("list", baihat);
		
		//phần này load Ablum
		
		hql = "from Album where yeuthich >0 and id !=1 order by(NgayPH) ";
		query = (Query) session.createQuery(hql).setMaxResults(7);
		List<Album> album = query.list();
		model.addAttribute("album",album);
		
		
		
		return "index";
	}
	//kiểm tra đăng nhập 
	@RequestMapping(value="login",method=RequestMethod.GET)
	public String loginForm(ModelMap model) {
		model.addAttribute("message", "Nếu Chứa Có Tài Khoản Hãy Đăng Ký");
		model.addAttribute("login",login);
		return "login";
	}
	//form đăng kí
	@RequestMapping(value="register",method=RequestMethod.GET)
	public String registerForm(ModelMap model) {
		model.addAttribute("login",login);
		return "register";
	}
	//kiểm tra đăng nhập
	@RequestMapping(value="login",method=RequestMethod.POST)
	public String login(ModelMap model,@RequestParam("mail") String mail,@RequestParam("password") String password,HttpSession ss) {
		//try cập cơ sở dữ liệu
		Session session = factory.getCurrentSession();
		//hk để trung với tên bảng vc thật sử dụng tên class
		String hql = "select count(*) from Account Where Mail = '"+mail +"' and Password = '"+password+"'";
		
		Query query = session.createQuery(hql);
		
		long count = (long) query.uniqueResult();
		
		if(count !=0) {
			//lấy user id
			hql = " from Account Where Mail = '"+mail +"' and Password = '"+password+ "'";
			query = session.createQuery(hql);
			Account  acc  = (Account) query.uniqueResult();
			iduser = acc.getId();
			
			
			//khúc chạy vô lại trang index sửa lại một số thông tin đấu hiệu nhận biết là biến cục bộ
			model.addAttribute("message", "Đăng Nhập Thành Công");
			login = true;
			model.addAttribute("login",login);
			if(mail.equals("admin@gmail.com"))
			{
				ss.setAttribute("user","admin");
				return listNhac(model, "đăng nhập thành công");
			}
			ss.setAttribute("user","user");
			return home(model);
		}
		else
		{
			model.addAttribute("message","Đăng Nhập Thất Bại Vui Lòng Kiểm Lai Mail Và Mật Khẩu!!");
			model.addAttribute("mail", mail);
			model.addAttribute("password",password);
			model.addAttribute("login",login);
			
			return "login";
		}
	}
	//kiểm tra đăng kí gửi xác thực
	@RequestMapping(value="register",method=RequestMethod.POST)
	public String register(ModelMap model, @RequestParam("mail") String mail,@RequestParam("password") String password, @RequestParam("repassword") String repassword)
	{
		//kiểm tra không cho để trống
		if(mail.trim().equals("") || password.trim().equals("") || repassword.trim().equals(""))
		{
			model.addAttribute("message","Không Để Trống Dữ Liệu!!");
			model.addAttribute("mail", mail);
			model.addAttribute("password", password);
			model.addAttribute("repassword",repassword);
			return "register";
		}
		//kiểm tra password và repassword có khớp với nhau hay hk
		if(!password.trim().equals(repassword.trim()))
		{
			model.addAttribute("message","Nhập Lại Password Không Khớp!!");
			model.addAttribute("mail", mail);
			model.addAttribute("password", password);
			model.addAttribute("repassword",repassword);
			return "register";
		}
		//kiểm tra mail có chưa
		Session session = factory.getCurrentSession();
		String hql = "select count(*) from Account Where Mail = '"+mail +"' ";
		Query query = session.createQuery(hql);
		long count = (long) query.uniqueResult();
		if(count ==0) //chưa có tài khoản
		{
			//lưu mail và password vào
			registermail = mail;
			registerpassword = password;
			//tạo số ngẫu nhiên
			Random generator = new Random();
			value = generator.nextInt(900000)+100000;
			// gửi số đếm mail 
			MimeMessage maill = mailer.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(maill);
			String from = "thanhliem4121999@gmail.com";
			try {
				helper.setFrom(from,from);
				helper.setTo(mail);
				helper.setReplyTo(from,from);
				helper.setSubject("Mã Tạo Tài Khoản");
				helper.setText(Integer.toString(value),true);
				mailer.send(maill);
			} catch (UnsupportedEncodingException | MessagingException e) {
				// TODO Auto-generated catch block
				model.addAttribute("message", "Có Vấn Đề Lỗi Xảy Ra Vui Lòng Thử Lại Sau");
				model.addAttribute("login",login);
				return "register";
			}
			starttime = System.nanoTime();
			//sử dụng lớp hỗ trợ
			
			//kiểm tra số
			
			
			model.addAttribute("message", "Số Có Hiệu Lực Trong Vòng 1 Phút");
			model.addAttribute("login",login);
			return "authentica";
		}
		else //mail đã đăng kí
		{
			model.addAttribute("message","Email đã tồn tại!!");
			model.addAttribute("mail", mail);
			model.addAttribute("password", password);
			model.addAttribute("repassword",repassword);
			model.addAttribute("login",login);
			return "register";
		}
		
		
	}
	//đăng xuất
	@RequestMapping(value = "logout")
	public String logout(ModelMap model,HttpSession ss) {
		login = false;
		iduser = 0;
		ss.setAttribute("user",null);
		model.addAttribute("login",login);
		return home(model);
	}
	// xác thực email nhập số
	@RequestMapping(value = "confirm")
	public String confirm(ModelMap model, @RequestParam(value="number") String num)
	{
		endtime = System.nanoTime();
		//cover String to int
		int number;
		try {
			number = Integer.parseInt(num);
		} catch(Exception e)
		{
			model.addAttribute("login",login);
			model.addAttribute("message","Số Không Hợp Lệ");
			return "register";
		}
		
		if(number != value || (endtime-starttime)*1000000000 >60)
		{
			model.addAttribute("login",login);
			model.addAttribute("message","Số Không Hợp Lệ");
			return "register";
		}
		//Tạo Tài Khoảng
		Account acc = new Account(registermail,registerpassword);
		//mở giao tác
		Session session = factory.openSession();
		Transaction t  = session.beginTransaction();
		try
		{
			//lưu vào cơ sỡ dữ liệu
			session.save(acc);
			t.commit();
			//hk để trung với tên bảng vc thật sử dụng tên class
			
		}
		catch(Exception e)
		{
			t.rollback();
			model.addAttribute("login",login);
			model.addAttribute("message","Lỗi Khi Lưu");
			return "register";
		}
		finally {
			session.close();
		}
		
		
		model.addAttribute("login",login);
		model.addAttribute("message","Đã Tạo Tài Khoản Thành Công Hãy Đăng Nhập");
		model.addAttribute("mail",registermail);
		model.addAttribute("password",registerpassword);
		return loginForm(model);
	}
	//gửi lại số
	@RequestMapping("resend")
	public String resend(ModelMap model)
	{
		
		//tạo số ngẫu nhiên
		Random generator = new Random();
		value = generator.nextInt(900000)+100000;
		// gửi số đếm mail 
		MimeMessage maill = mailer.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(maill);
		String from = "thanhliem4121999@gmail.com";
		try {
			helper.setFrom(from,from);
			helper.setTo(registermail);
			helper.setReplyTo(from,from);
			helper.setSubject("Mã Tạo Tài Khoản");
			helper.setText(Integer.toString(value),true);
			mailer.send(maill);
		} catch (UnsupportedEncodingException | MessagingException e) {
			// TODO Auto-generated catch block
			model.addAttribute("message", "Có Vấn Đề Lỗi Xảy Ra Vui Lòng Thử Lại Sau");
			model.addAttribute("login",login);
			return "register";
		}
		starttime = System.nanoTime();
		//sử dụng lớp hỗ trợ
		
		//kiểm tra số
		
		
		model.addAttribute("message", "Số Có Hiệu Lực Trong Vòng 1 Phút");
		model.addAttribute("login",login);
		return "authentica";
	}
	//tìm kiếm
	@RequestMapping("timkiem")
	public String timkiem(ModelMap model, @RequestParam("name") String name )
	{
		model.addAttribute("login",IndexController.login);
		//truy cap csdl
		Session session = factory.getCurrentSession();
		//hk để trung với tên bảng vc thật sử dụng tên class
		//String hql = "from BaiHat where ten like N'%"+ name + "%'";
		String hql = "from BaiHat where ten like '%"+ name + "%'";
		
		Query query = session.createQuery(hql);
		//do ve view
		List<BaiHat> baihat = query.list();
		model.addAttribute("baihat", baihat);
		return "baihat";
		
	}
	//gửi mail
	@RequestMapping(value = "sendmail")
	public String sendmail(ModelMap model,@RequestParam("name") String name ,@RequestParam("email") String email ,@RequestParam("subject") String subject ,@RequestParam("message") String message)
	{
		//gửi mail
		String from = email;
		String mail = "thanhliem4121999@gmail.com";
		//gửi chuổi
		String text = name +"/n" + message;
					MimeMessage maill = mailer.createMimeMessage();
					MimeMessageHelper helper = new MimeMessageHelper(maill);
					
					try {
						helper.setFrom(from,from);
						helper.setTo(mail);
						helper.setReplyTo(from,from);
						helper.setSubject(subject);
						helper.setText(text,true);
						mailer.send(maill);
					} catch (UnsupportedEncodingException | MessagingException e) {
						// TODO Auto-generated catch block
						login = false;
						iduser = 0;
						model.addAttribute("message", "Có Vấn Đề Lỗi khi gửi mail");
						model.addAttribute("login",login);
						
						return "login";
					}
		
		
		
					login = false;
					iduser = 0;
					model.addAttribute("message", "gữi mail thành công ");
					model.addAttribute("login",login);
					return "login";
	}
	//temp để chuyển list nhạc
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
		 List<BaiHat> baihat = query.list();
		model.addAttribute("baihat", baihat);
		
		// load trước album
		hql = " from Album ";
		
		query = session.createQuery(hql);
		//do ve view
		List<Album> album = query.list();
		
		model.addAttribute("message",message);
		
		return "nhac/listnhac";
	}
	//tìm kiếm
	
	
	
	

	
	
	
}

