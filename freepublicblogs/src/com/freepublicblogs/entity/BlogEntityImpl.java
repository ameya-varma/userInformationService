package com.freepublicblogs.entity;

import com.freepublicblogs.common.blog.Blog;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PreparedQuery.TooManyResultsException;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;

public class BlogEntityImpl {
	
	private DatastoreService datastore  = DatastoreServiceFactory.getDatastoreService( );
	private TransactionOptions options = TransactionOptions.Builder.withXG(true);
	
	public BlogEntityImpl()
	{}
	
	public Blog getBlog(String googleUserId)
	{
		Blog blog = new Blog();
		Entity blogEntity = new Entity("Blog", googleUserId );
		
		Key blogKey = KeyFactory.createKey("Blog", googleUserId );
		
		try
		{
			blogEntity = datastore.get(blogKey);
		}
		catch(EntityNotFoundException e)
		{
			blog = null;
		}
		
		if( blogEntity != null && blog != null)
		{
			blog.setTitle( (String) blogEntity.getProperty("title") );
			blog.setAuthorEmail((String)blogEntity.getProperty("authorEmail"));
			blog.setImageKey((String)blogEntity.getProperty("imageKey"));
			blog.setDescription( (String)blogEntity.getProperty("description") ) ;
		}
		
		
		return blog;

	}
	
	public Blog createBlog(Blog blog, String googleUserId, String googleEmail)
	{
		Entity blogEntity = new Entity("Blog", googleUserId );
		Transaction txn = datastore.beginTransaction(options);

		blogEntity.setProperty("title", "");
		blogEntity.setProperty("authorEmail", googleEmail );
		blogEntity.setProperty("imageKey", "");
		blogEntity.setProperty("description", "");
		
		try{
			datastore.put(txn, blogEntity);
			txn.commit();
		}
		catch(Exception e)
		{
			blogEntity = null;
			blog = null;
		}
		
		if(blogEntity != null)
		{
			blog.setAuthorEmail(googleEmail);
		}
		
		return blog;
	}
	
	
	
	public Blog saveBlog(Blog blog, String googleUserId)
	{
		Entity blogEntity = new Entity("Blog", googleUserId );
		Transaction txn = datastore.beginTransaction(options);

		Key blogKey = KeyFactory.createKey("Blog", googleUserId );
		
		try
		{
			blogEntity = datastore.get(txn, blogKey);
		}
		catch(EntityNotFoundException e)
		{
			blog = null;
		}
		
		if(blog != null && blogEntity != null)
		{
			blogEntity.setProperty("authorEmail", blog.getAuthorEmail());
			blogEntity.setProperty("description", blog.getDescription());
			blogEntity.setProperty("title", blog.getTitle());
			
			blog.setImageKey((String)blogEntity.getProperty("imageKey"));
			try{
				datastore.put(txn, blogEntity);
				txn.commit();
			}
			catch(Exception putException)
			{
				blogEntity = null;
				blog = null;
			}
			
		}
		
		return blog;
	}
	
	public String saveImageKey(String imageKey, String googleUserId)
	{
		Entity blogEntity = new Entity("Blog", googleUserId );
		Transaction txn = datastore.beginTransaction(options);

		Key blogKey = KeyFactory.createKey("Blog", googleUserId );
		
		try
		{
			blogEntity = datastore.get(txn, blogKey);
		}
		catch(EntityNotFoundException e)
		{
			imageKey = null;
		}
		if(blogEntity != null)
		{
			blogEntity.setProperty("imageKey", imageKey);
			datastore.put(txn, blogEntity);
			txn.commit();
		}

		return imageKey;
	}
	
	public boolean deleteImageKey(String googleUserId)
	{
		boolean imageDeleted = true;
		
		Entity blogEntity = new Entity("Blog", googleUserId );
		Transaction txn = datastore.beginTransaction(options);

		Key blogKey = KeyFactory.createKey("Blog", googleUserId );
		
		try
		{
			blogEntity = datastore.get(txn, blogKey);
		}
		catch(EntityNotFoundException e)
		{
			imageDeleted = false;
		}
		if(blogEntity != null)
		{
			try
			{
				BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
				BlobKey blobKeys = new BlobKey((String)blogEntity.getProperty("imageKey")); 
				blobstoreService.delete(blobKeys);
			}
			catch(Exception e)
			{
				imageDeleted = false;
			}
			blogEntity.setProperty("imageKey", "");
			datastore.put(txn, blogEntity);
			txn.commit();
		}
		else
		{
			imageDeleted = false;

		}

		return imageDeleted;

	}
	
	public boolean checkUrlAvalaibility(String urlText)
	{
		boolean urlAvailable = true;
		
		Filter filter = new FilterPredicate("blogUrl", FilterOperator.EQUAL, urlText);
		Query q = new Query("Blog").setFilter(filter);
		PreparedQuery query = datastore.prepare(q);
		Entity blog = new Entity("Blog");
		
		try
		{
			blog = query.asSingleEntity();
		}
		catch(TooManyResultsException e)
		{
			urlAvailable = false;
		}
		
		if(blog != null && blog.getKey() != null && !blog.getKey().toString().isEmpty( ) )
		{
			urlAvailable = false;
		}
		
		return urlAvailable;
	}
	
	public boolean saveBlogUrl(String urlText, String googleUserId)
	{
		boolean urlSaved = true;
		
		Entity blogEntity = new Entity("Blog", googleUserId );
		Transaction txn = datastore.beginTransaction(options);

		Key blogKey = KeyFactory.createKey("Blog", googleUserId );
		
		try
		{
			blogEntity = datastore.get(txn, blogKey);
		}
		catch(EntityNotFoundException e)
		{
			urlSaved = false;
		}
		if(blogEntity != null)
		{
			String finalUrlText = "/" + urlText + ".pages";
			blogEntity.setProperty("blogUrl", finalUrlText);
			datastore.put(txn, blogEntity);
			txn.commit();
		}
		else
		{
			urlSaved = false;

		}

		return urlSaved;	}
	
	public Blog getBlogFromUrl(Blog blog, String urlText)
	{
		Filter filter = new FilterPredicate("blogUrl", FilterOperator.EQUAL, urlText);
		Query q = new Query("Blog").setFilter(filter);
		PreparedQuery query = datastore.prepare(q);
		Entity blogEntity = new Entity("Blog");
		
		try
		{
			blogEntity = query.asSingleEntity();
		}
		catch(TooManyResultsException e)
		{
			blog = null;
		}
		
		if(blogEntity != null && blogEntity.getKey() != null && !blogEntity.getKey().toString().isEmpty( ) )
		{
			blog.setTitle( (String) blogEntity.getProperty("title") );
			blog.setAuthorEmail((String)blogEntity.getProperty("authorEmail"));
			blog.setImageKey((String)blogEntity.getProperty("imageKey"));
			blog.setDescription( (String)blogEntity.getProperty("description") ) ;
		}
		else
		{
			blog = null;
		}
		
		return blog;
	}

}
