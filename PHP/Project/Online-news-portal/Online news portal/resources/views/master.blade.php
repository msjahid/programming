<!DOCTYPE html>
<html>
    <head>
    	<title> @yield('title') </title>
    	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>

    
    <link type="text/css" rel="stylesheet" href="src/css/lightslider.css" />                  
    <script src="src/js/lightslider.js"></script>

  <style>
    .button {
      transition-duration: 0.4s;
      -webkit-transition-duration: 0.4s;
        background-color: #FFFFFF; /* Green */
        border: 2px solid #2F4F4F;
        
        color: #000000;
        padding: 8px 26px;
        text-align: center;
        text-decoration: none;
        display: inline-block;
        font-size: 13px;
        margin: 4px 2px;
        cursor: pointer;
        border-radius: 8px;
    }
    .button:hover {
        background-color: #2F4F4F; 
        color: white;
    }

    .button1 {
      transition-duration: 0.4s;
      -webkit-transition-duration: 0.4s;
        background-color: #FFFFFF; /* Green */
        border: 2px solid #DC143C;
        
        color: #000000;
        padding: 8px 26px;
        text-align: center;
        text-decoration: none;
        display: inline-block;
        font-size: 13px;
        margin: 4px 2px;
        cursor: pointer;
        border-radius: 8px;
    }
    .button1:hover {
        background-color: #DC143C; 
        color: white;
    }
  </style>

    </head>
    <body >
    	@include('includes.header')
        <div class="container">
        	@yield('content')
        </div>
    </body>
</html>
