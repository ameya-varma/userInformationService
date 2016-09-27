      <%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
      <!DOCTYPE html>
	    <html lang="en">
	    <head>
	    <title>PublicBlog</title>
	  	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	 	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	 	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
	    <link type="text/css" rel="stylesheet" href="/resources/css/storyBoCustom.css"/>
	    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
			</head>
		<body>
		<div class="row" style="border-bottom: solid thick #33CCFF;">
		<div class="col-md-7 col-sm-12">
			<h3 style="color: #8C9193; padding-left: 20px; padding-top: 20px">Welcome to Public Blogs</h3>
		</div>
		<div class="col-md-5 col-sm-12">
		<div style="padding-right: 20px !important; padding-top: 20px !important;">
		<div class="thumbnail">
		<span class="glyphicon glyphicon-user" style="font-size: 20px; color: green; padding-left: 30px;">
		</span>
		<c:out value="${blog.authorEmail}"></c:out>
		</div>
		</div>
		</div>
		</div>
		<br>
		<div class="container-fluid">
		<div class="col-lg-12 col-md-12">
		<div class="row row-elementRow">
		<div class="form-group">
		<h4><c:out value="${blog.title}"></c:out></h4>
		</div>
		</div>
		<br>
		<div class="row">
		<div class="col-md-12 col-sm-12">
		<div id="previewTitleImageDiv"><img id="previewTitleImage" src="${blog.imageURL}" height="500px" width="500px"/></div>
		</div>
		</div>
		<br>
		<div class="row row-elementRow">
		<div class="form-group">
			<pre>${blog.description}"></pre>
		</div>
		</div>
		</div>
		</div>
		</body>
	</html>