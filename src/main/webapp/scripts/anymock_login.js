function loginstart()
{
	var user=$("#login_name").val();
  var pass=$("#login_password").val();
  if(user=="")
  {
  	jAlert("请输入登录用户名","anymock警告框");
  	$("#login_name").focus();
  	return false
  }
  if(pass=="")
  {
  	jAlert("请输入登录密码","anymock警告框");
  	$("login_password").focus();
  	return false
  }
  $.ajax(
  {type:"POST",
  	url:"/mock/login.do",
  	data:"loginName="+user+"&loginPassword="+pass,
  	success:
  	function(msg)
  	{
  		var patt1=new RegExp("密码不匹配");
  		var patt2=new RegExp("新增用户失败");
  		var patt3=new RegExp("登陆成功");
  		var result=patt1.test(msg);
  		var resultnext=patt2.test(msg);
  		var resultthird=patt3.test(msg);
  		if(result)
  		{
  			jAlert("密码不匹配","anymock警告框")
  		}
  		if(resultnext)
  		{
  			jAlert("新增用户失败","anymock警告框")
  		}
  		if(resultthird)
  		{
  			Logincookie(user+"SPDFS"+pass);
  			window.location.href="/mock/homePage.do";return false
  		}
  	}
  	}
  	)
  	};