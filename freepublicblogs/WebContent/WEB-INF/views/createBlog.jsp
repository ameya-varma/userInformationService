      <%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
      <!DOCTYPE html>
	    <html lang="en">
	    <head>
	    <title>PublicBlog</title>
	  	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	 	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css" rel="stylesheet">
    <link type="text/css" rel="stylesheet" href="/resources/css/storyBoCustom.css"/>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
  	<script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
			</head>
		<body>
		<div class="row" style="border-bottom: solid thick #33CCFF;">
		<div class="col-md-7 col-sm-12">
			<h3 style="color: #8C9193; padding-left: 20px; padding-top: 20px">Welcome to Public Blogs</h3>
		</div>
		<div class="col-md-5 col-sm-12">
		<div style="padding-right: 20px !important; padding-top: 20px !important;">
		<a href="${logoutUrl}" class="thumbnail">
		<span class="glyphicon glyphicon-off" style="font-size: 20px; color: green; padding-left: 30px;"> 
		Logout</span>
		</a>
		</div>
		<div class="row">
		<c:out value="${blog.authorEmail}"></c:out>
		</div>
		</div>
		</div>
		<div class="container-fluid">
		<div class="col-lg-12 col-md-12">
		
		<form:form id ="blog" method = "POST" commandName="blog" action="/publish.tyin">		
		<div class="row row-elementRow">
		<div class="form-group">
		    <label for="title">Blog Title</label>
		    <form:input type="text" path="title" class="form-control" value="${blog.title}" id="title" maxlength="150"></form:input>
		</div>
		</div>
		
		<div class="row row-elementRow">
		<div class="col-md-12 col-sm-12">
		<label class= "label label-warning">Click on the <span class= "glyphicon glyphicon-ok"></span> to save image </label>
		<br>
		<input type="file" id = "pageTitleImageFile" name="pageTitleImageFile"></input>
		</div>
		</div>
		<div class="row row-elementRow">
		<div class="col-md-12 col-sm-12">
		<div id="previewTitleImageDiv"><img id="previewTitleImage" src="${blog.imageURL}" height="500px" width="500px"/></div>
		</div>
		</div>
		<div class="row row-elementRow">
		<div class= "pull-right">
		<button class="btn btn-default btn-default-saveImage" type="button" id="ti">
		<span class="glyphicon glyphicon-ok glyphicon-ok-saveblog" style="font-size: 20px;"></span>
		</button>
		<button class="btn btn-default btn-default-deleteImage" type="button" id="tid">
		<span class="glyphicon glyphicon-remove glyphicon-remove-deleteblog" style="font-size: 20px;"></span>
		</button>
		</div>
		</div>
		
		
		<div class="row row-elementRow">
		<div class="form-group">
		    <label for="description">Blog Description</label>
		   		<form:textarea cssClass="form-control form-control-ritext" id="description" path="description" value="${blog.description}"/>
		</div>
		</div>
		
		<div class="row row-elementRow">
			<div class="form-group">
				<div class="row row-elementRow">
				<label for="blogUrl"> Enter URL below.  It can only contain numbers and small case letters.  No spaces.  Click Save Url. 
				Blog will not be visible if url is not saved.
				</label>
				</div>
				<div class="row row-elementRow">
					<span class="glyphicon glyphicon-globe">https://freepublicblogs.appspot.com/<input id="blogUrlText" type="text"><b>.pages</b></span>	
					<button type="button" class="btn btn-default btn-default-pub" id = "saveBlogUrl">
					<span class="glyphicon glyphicon-ok"></span>Save Url</button>	
				</div>
			</div>
		</div>
		
		<div class="row row-elementRow">
		<div class="col-md-8 col-sm-12 col-md-offset-2">
		<div class="pull-left">
			<div class="col-md-6 col-sm-6">
			<button type="button" class="btn btn-default btn-default-pub" id="saveBlogButton">
			<span class="glyphicon glyphicon-ok glyphicon-ok-saveblog" style="font-size: 20px;"></span> Save</button>
			</div>
			<div class="col-md-6 col-sm-6">
			<button type="submit" class="btn btn-default btn-default-pub">
			<span class="glyphicon glyphicon-globe" style="color: #8A5C00; font-size: 20px;"></span> Publish Blog</button>
			</div>
		</div>
		</div>
		</div>
				
		</form:form>
		<br><br><br><br>
		</div>
		</div>
	    <script src="//cdn.tinymce.com/4/tinymce.min.js"></script>
		<script>
		tinymce.init({
		mode : "specific_textareas",
        editor_selector : "form-control-ritext"});
		</script>
		<script>
		$(document).ready(function(){

			
			$(".btn-default-saveImage").click(function(){
				
				$.ajax({
					url: "/getImageUploadLink.tyin",
					type: "GET",
				    dataType: "text",
					success: function(imageLink)
					{
							alert("SUCCESS");
							var titleImage = document.getElementById('pageTitleImageFile');	
							var page = "pageTitleImageFile";
						var file = titleImage.files[0];
						var formData = new FormData();
						formData.append( page, file);
						
						$.ajax({
							url: imageLink,
							type: "POST",
							data: formData,
							dataType: "text",
							contentType: false,
						    processData: false,
						    success: function(response)
						    {
						    		alert("success posting image : " + response)
						    		$("#previewTitleImage").attr("src", response);
						    },
						    error : function(e)
						    {
						    	alert("error posting image: " + e);
								console.log(e);
						    }
						});
					},
					error: function(e)
					{
						alert("error : " + e);
						console.log(e);
					}
				
				});

				});
			
			$("#tid").click(function(){
				
				$.ajax({
					url: "/deleteImage.tyin",
					type: "GET",
				    dataType: "text",
					success: function(message)
					{
							alert(message);
							$("#pageTitleImageFile").val( "" );
					    	$("#previewTitleImage").attr("src", null );
					},
					error: function(e)
					{
						alert("error : " + e);
						console.log(e);
					}
				});
				
			});
			
			$("#saveBlogUrl").click(function(){
				var blogUrl = $("#blogUrlText").val();
				alert(blogUrl);
					$.ajax({
						url: "/saveBlogUrl.tyin",
						type: "POST",
						data: blogUrl,
						dataType: "text",
						contentType: "plain/text",
					    success: function(response)
					    {
					    		alert(response);
					    },
					    error : function(e)
					    {
					    	alert("error posting url : " + e);
							console.log(e);
					    }
					});
			});
			
			$("#saveBlogButton").click(function(){
				var title = $("#title").val();
				var description = tinymce.get('description').getContent();
				//var description = $("#description").val();
				alert(title + "  :   " + description ); 
				var blog = { title : title, description : description };
				alert(blog);
					$.ajax({
						url: "/saveBlog.tyin",
						type: "POST",
						data: JSON.stringify(blog),
						dataType: "json",
						contentType: "application/json",
					    success: function(response)
					    {
					    		var blogResponse = response;
					    		$("#title").val(blogResponse.title);
					    		$("#description").attr('value', blogResponse.description)
					    		alert("Success saving blog : " + blogResponse.title );
					    },
					    error : function(e)
					    {
					    	alert("error saving blog : " + e);
							console.log(e);
					    }
					});
			});
		});
		</script>
		</body>
	</html>