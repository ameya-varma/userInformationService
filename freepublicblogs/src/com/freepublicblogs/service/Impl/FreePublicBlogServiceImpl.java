package com.freepublicblogs.service.Impl;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.freepublicblogs.common.blog.Blog;
import com.freepublicblogs.common.userInfo.UserInfo;
import com.freepublicblogs.entity.BlogEntityImpl;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
/**
*This class provides implementation for common features
*Author Ameya Varma
*/
public class FreePublicBlogServiceImpl {
	
	private static UserService userService = UserServiceFactory.getUserService( );
	
	/**
	* This method is used to make a REST API call to the UserInformationService. It uses
	* XML to exchange data
	* Author Ameya Varma
	*/
	public static HashMap<String, String> handleUserLoginRequest(HttpServletRequest request)
	{
		UserInfo userInfo = new UserInfo();
		//System.setProperty("proxyHost", "http://127.0.0.1:8080"); 
		//System.setProperty("proxyPort", "8080");
		HashMap<String, String> userDetailsMap = new HashMap<>();
		if(request.getUserPrincipal() == null)
		{
			userDetailsMap.put("loginPage", userService.createLoginURL( request.getRequestURI( ) ) );
			return userDetailsMap;
		}
		else if( !userService.getCurrentUser().getEmail( ).isEmpty( ) && 
				!userService.getCurrentUser().getUserId().isEmpty( ) ) 
		{
 			//REST call for userinfoservice
			userInfo.setGoogleUserEmail( userService.getCurrentUser().getEmail( ) );
			userInfo.setGoogleUserId(userService.getCurrentUser().getUserId());
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.TEXT_XML);
			HttpEntity<UserInfo> requestEntity = new HttpEntity<UserInfo>(userInfo, headers);
			
			//Comment for production
			//String uri = "http://127.0.0.1:8888/userDetails";
			
			//Uncomment for production
			String uri = "https://x2cgger.appspot.com/userDetails";
			
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<UserInfo> userInfo1 = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, UserInfo.class);
			
			
			System.out.println(userInfo1.getBody().getGoogleUserEmail() );
			System.out.println(userInfo1.getBody().getGoogleUserId( ) );
			
			userDetailsMap.put("createBlog", userInfo1.getBody().getGoogleUserId( ) );
			return userDetailsMap;
		}
		
		userDetailsMap.put("loginPage", userService.createLoginURL( request.getRequestURI( ) ) );
		return userDetailsMap;
		
	}
	
	/**
	*This method retrieves blog details
	*/
	public static Blog retrieveBlog()
	{
		Blog blog = null;
		
		BlogEntityImpl blogEntity = new BlogEntityImpl();
		String googleUserId = userService.getCurrentUser().getUserId();
		
		if( googleUserId != null )
		{
			blog = blogEntity.getBlog( googleUserId );
		}
		if(blog != null)
		{
			blog.setLogoutURL(userService.createLogoutURL("https://freepublicblogs.appspot.com"));
		}
		
		return blog;
	}
	
	public static Blog handleBlogCreation( )
	{
		Blog blog = new Blog();
		
		BlogEntityImpl blogEntity = new BlogEntityImpl();
		String googleUserId = userService.getCurrentUser().getUserId();
		String googleEmail = userService.getCurrentUser().getEmail( );
		if( googleUserId != null )
		{
			blog = blogEntity.createBlog(blog, googleUserId, googleEmail);
			if(blog!=null)
			{
				blog.setLogoutURL(userService.createLogoutURL("https://freepublicblogs.appspot.com"));
			}
		}
		
		return blog;
	}
	
	public static Blog saveBlog(Blog blog)
	{
		BlogEntityImpl blogEntity = new BlogEntityImpl();
		String googleUserId = userService.getCurrentUser().getUserId();
		blog.setAuthorEmail(userService.getCurrentUser().getEmail());
		if( googleUserId != null )
		{
			blog = blogEntity.saveBlog(blog, googleUserId);
		}
		
		return blog;
	}
	
	public static String saveImageKey(String imageKey)
	{
		BlogEntityImpl blogEntity = new BlogEntityImpl();
		String googleUserId = userService.getCurrentUser().getUserId();
		if( googleUserId != null )
		{
			imageKey = blogEntity.saveImageKey(imageKey, googleUserId);
		}
		if(imageKey == null)
		{
			imageKey = "ERROR";
		}
		return imageKey;
	}
	
	public static boolean deleteImage()
	{
		BlogEntityImpl blogEntity = new BlogEntityImpl();
		String googleUserId = userService.getCurrentUser().getUserId();
		if( googleUserId != null )
		{
			return blogEntity.deleteImageKey(googleUserId);
		}
		return false;
	}
	
	public static String resolveImageURL(String imageKey)
	{
		
		BlobKey blobKey = new BlobKey(imageKey.replace(">", "") );
		ServingUrlOptions urlOptions = ServingUrlOptions.Builder.withBlobKey( blobKey ).imageSize(200);
		ImagesService imagesService = ImagesServiceFactory.getImagesService();
		System.out.println( blobKey.toString( ) );
		return imagesService.getServingUrl( urlOptions );
	}
	
	public static boolean checkUrlAvalaiblity(String urlText)
	{
		BlogEntityImpl blogEntity = new BlogEntityImpl();
		return blogEntity.checkUrlAvalaibility(urlText);
	}
	
	public static boolean saveBlogUrl(String url)
	{
		boolean blogUrlSaved = false;
		BlogEntityImpl blogEntity = new BlogEntityImpl();
		String googleUserId = userService.getCurrentUser().getUserId();
		if( blogEntity.checkUrlAvalaibility( url ) )
		{
			blogUrlSaved = blogEntity.saveBlogUrl(url, googleUserId);
		}
		return blogUrlSaved;
	}
	
	public static Blog retrieveBlogForDisplayOnly(Blog blog, String url)
	{
		BlogEntityImpl blogEntity = new BlogEntityImpl();
		blog = blogEntity.getBlogFromUrl(blog, url);
		return blog;
	}

}
