package com.freepublicblogs.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.freepublicblogs.common.blog.Blog;
import com.freepublicblogs.common.userInfo.UserInfo;
import com.freepublicblogs.service.Impl.FreePublicBlogServiceImpl;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;

/**
* The main controller class that handles, login, blog creation, saving images, publishing blogs
* and viewing published blogs
*/
@Controller
public class FreePublicBlogController {

	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	
	@RequestMapping(value="/welcome", method = RequestMethod.GET)
	public ModelAndView welcome(HttpServletRequest request)
	{
		HashMap<String, String> userDetailsMap = FreePublicBlogServiceImpl.handleUserLoginRequest(request); 
		if(!userDetailsMap.containsKey("loginPage"))
		{
			Blog blog = FreePublicBlogServiceImpl.retrieveBlog( );
			if(blog == null)
			{
				blog = FreePublicBlogServiceImpl.handleBlogCreation( );
				if(blog == null)
				{
					return new ModelAndView("error", "blog", null);
				}
				
			}
			else
			{
				if(blog.getImageKey( ) != null && !blog.getImageKey().isEmpty( ))
				{
					String imageUrl = FreePublicBlogServiceImpl.resolveImageURL(blog.getImageKey( ));
					if(imageUrl != null)
					{
						blog.setImageURL(imageUrl);
						blog.setImageKey(null);
					}

				}
			}
			return new ModelAndView("createBlog", "blog", blog);
		}
		
		
		return new ModelAndView("loginPage", "redirectUrl", userDetailsMap.get("loginPage"));
		
	}
	
	@RequestMapping(value="/publish", method=RequestMethod.POST)
	public ModelAndView savePageRedirectUser(@ModelAttribute(value="blog") Blog blog,
			BindingResult result, HttpServletRequest request)
	{
		blog = FreePublicBlogServiceImpl.saveBlog(blog);
		
		if(blog == null)
		{
			return new ModelAndView("error", "blog", null);
		}
		else if( blog.getImageKey() != null && !blog.getImageKey().isEmpty( ) )
		{
			String imageUrl = FreePublicBlogServiceImpl.resolveImageURL(blog.getImageKey());
			if(imageUrl != null)
			{
				blog.setImageURL(imageUrl);
			}
		}
		return new ModelAndView("readBlog", "blog", blog);
	}
	
	@RequestMapping(value="/saveBlog", method = RequestMethod.POST, consumes="application/json", produces = "application/json")
	public @ResponseBody Blog saveBlog(@RequestBody Blog blog,
			BindingResult result, HttpServletRequest request)
	{
		blog = FreePublicBlogServiceImpl.saveBlog(blog);
		blog.setImageKey(null);
		return blog;
	}
	
	/**
	 * HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		return new ResponseEntity<Blog>(null, headers, HttpStatus.ACCEPTED);
	 * @param request
	 * @return
	 */
	
	@RequestMapping(value="/getImageUploadLink", method=RequestMethod.GET, produces="text/plain" )
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public String createUploadURLFromAJAXRequest(HttpServletRequest request)
	{
		System.out.println("SAVE IMAGE METHOD");
		String imageUploadUrl = blobstoreService.createUploadUrl("/saveImage.tyin");
		
		return imageUploadUrl;
	}
	
	@RequestMapping(value="/saveImage", method=RequestMethod.POST, produces="text/plain" )
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public String getCallBackFromBlobStore(HttpServletRequest request)
	{
        ImagesService imagesService = ImagesServiceFactory.getImagesService();
        
		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
        List<BlobKey> blobKeys = blobs.get("pageTitleImageFile");
        
        BlobKey blobKey = (BlobKey) blobKeys.get(0);
        
    	ServingUrlOptions urlOptions = ServingUrlOptions.Builder.withBlobKey(blobKey);
        //urlOptions.crop(true);
        String imageUrl = imagesService.getServingUrl( urlOptions ); 
       
        String blobKeyString = blobKey.toString().replace("<BlobKey: ", "");
        blobKeyString.replace(">", "");
        System.out.println(blobKeyString);
        //ImageDataHelper.saveImageKey( blobKeyString );
        
        String response = FreePublicBlogServiceImpl.saveImageKey(blobKeyString);
        
         System.out.println( response + " : URL : " + imageUrl );
        
		return imageUrl;
	}
	
	@RequestMapping(value="/deleteImage", method=RequestMethod.GET, produces="text/plain" )
	public @ResponseBody String deleteImageFromAJAX(HttpServletRequest request)
	{
		if(FreePublicBlogServiceImpl.deleteImage( ))
		{
			return "Image deleted";
		}
		return "ERROR: Image not deleted";
	}
	
	@RequestMapping(value="/saveBlogUrl", method=RequestMethod.POST, produces="text/plain" )
	public @ResponseBody String saveBlogUrl( @RequestBody String blogUrl, 
			BindingResult result, HttpServletRequest request)
	{
		boolean blogUrlSaved = false;
		System.out.println(blogUrl);
		if(blogUrl != null && !blogUrl.isEmpty( ) && blogUrl.matches("^[a-z0-9]*$") )
		{
			blogUrlSaved = FreePublicBlogServiceImpl.saveBlogUrl(blogUrl);
		}
		if( blogUrlSaved)
		{
			return "URL SAVED";
		}
		
		return "URL SAVE FAILED PLEASE TRY AGAIN";
	}
	
	@RequestMapping(value="/*", method=RequestMethod.GET )
	public ModelAndView displayBlog(HttpServletRequest request)
	{
		System.out.println(request.getRequestURI());
		String requestUri = request.getRequestURI().toString( );
		
		Blog blog = new Blog();
		if(requestUri != null && !requestUri.isEmpty( ) && requestUri.contains(".pages"))
		{
			blog = FreePublicBlogServiceImpl.retrieveBlogForDisplayOnly(blog, requestUri);
		}
		else
		{
			return new ModelAndView("error", "blog", blog);
		}
		
		if(blog == null)
		{
			return new ModelAndView("error", "blog", blog);
		}
		else if( blog.getImageKey() != null && !blog.getImageKey().isEmpty( ) )
		{
			String imageUrl = FreePublicBlogServiceImpl.resolveImageURL(blog.getImageKey());
			if(imageUrl != null)
			{
				blog.setImageURL(imageUrl);
			}
		}
		
		return new ModelAndView("readBlog", "blog", blog);
	}

}
