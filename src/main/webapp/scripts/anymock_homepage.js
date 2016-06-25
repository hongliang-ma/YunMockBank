
//获取到首页中的某个配置详情
function getDetailInfoMsg(userID) {
    var array  = userID.split('=')[1].split('&')[0];
    $.post("getDetailInfo.htm?action=byUser&"+userID,
		function (data, textStatus){
			$('div#'+array).html(data);
			setshowUserInfoBt();
			setFloatPageBt();
			testSendMsg();
		}, "html");

    return false;
}
//获取到新增页面中的某个配置详情
function getAddConfigs(userID) {
    var array  = userID.split('&')[0].split('=')[1];
    $.post("getDetailInfo.htm?action=byAdd&"+userID,
		function (data, textStatus){
		        $('div#'+array).html(data);
			$("a.overlay_bt[rel]").overlay();

$("a.Parser_detail[rel]").overlay({
        mask: 'darkred',
        effect: 'apple',
        onBeforeLoad: function() {
            // grab wrapper element inside content
            var wrap = this.getOverlay().find(".contentWrap");
            // load the page specified in the trigger
            wrap.load(this.getTrigger().attr("href"));
        }
    });
		}, "html");

    return false;
}

function setDefault(innerid, isTrue) {
    $.post("changeDetail.htm?action=setDefault",{"innerid": innerid, "isTrue":isTrue},
        function (responseText, textStatus){
			jAlert(responseText, 'anymock 设置结果');
			if (responseText.indexOf('OK') != -1) {
				if (isTrue == "true") {
					$('#userInfo_tail_default').attr("value", "取消默认值");
					$('#userInfo_tail_default').attr("onclick", "setDefault('"+innerid+"', 'false')");
				} else if(isTrue == "false"){
					$('#userInfo_tail_default').attr("value", "设置为默认值");
					$('#userInfo_tail_default').attr("onclick", "setDefault('"+innerid+"', 'true')");
				}				
			}
        }, "text");	    
    return false;
}


//复制用户配置,bug
function addNewConfig(innerid, transferId) {
	$.confirm({
		'title'		: 'anymock新增配置',
		'message'	: '<center><font color=VioletRed size="5">确认新增配置<img src="/images/question.gif"/></font></center>',
		'buttons'	: {
			'Yes'	: {
				'class'	: 'blue',
				'action': function(){
				    var cookieName = getCookie();
				    var user=cookieName.replace(/SPDFS\S+\d+/gi,"");
				    $.get("copyUserConfig.htm", { "innerid": innerid, "transferId":transferId},function(data){
					if (data == "errorHappen") {
					     jAlert('配置添加失败', 'anymock和你开玩笑');
					 } else {
					  jAlert('请首页中查找<font color=Navy><strong>' + data+'</strong></font>的配置并修改唯一值，否则无法使用该配置', 'anymock添加成功');
					 }
				    }, "text");
				}
			},
			'No'	: {
				'class'	: 'gray',
				'action': function(){}	// Nothing to do in this case. You can as well omit the action property.
			}
		}
	});
}
//删除用户配置
function delConfig(innerid, transferId) {
	$.confirm({
		'title'		: 'anymock删除配置',
		'message'	: '<center><font color=Navy size="5">确认删除这个配置吗<img src="/images/question.gif"/></font></center>',
		'buttons'	: {
			'Yes'	: {
				'class'	: 'blue',
				'action': function(){
				    $.get("delUserConfig.htm", { "innerid": innerid, "transferId":transferId},function(data){
					 if (data == "errorHappen") {
					     jAlert('删除失败!', 'anymock和你开玩笑');
					 } else {
					  jAlert('删除成功!', 'anymock删除结果');
					    window.location.href="/homePage.htm";
					 }
				    }, "text");
				}
			},
			'No'	: {
				'class'	: 'gray',
				'action': function(){}	// Nothing to do in this case. You can as well omit the action property.
			}
		}
	});

}

//获取异步的参数
function showAysnc($innerid, $KeyList) {
	alert("直接调接口时transferId="+$innerid+"\ntransCodeRule是以下任意项"+$KeyList);
}

function fnFormatDetails ( oTable, nTr ) {
    var aData = oTable.fnGetData( nTr );
    var findDate = aData[7];
    var value = $(findDate).attr('value');
    var array = '';
    var sOut = '<div class="right_content">';
    if (value.indexOf('newName') >= 0) {
	array  = value.split('&')[0].split('=')[1];
    } else {
	array  = value.split('=')[1].split('&')[0];
    }
    sOut += '<div class="showUserInfo" id="'+array+'"> </div> </div>';
    return sOut;
}

$(document).ready(function() {
    nav.init("#J_navMenuListUL");

    $("#example tbody tr").click( function( e ) {
        if ( $(this).hasClass('row_selected') ) {
            $(this).removeClass('row_selected');
        }
        else {
            oTable.$('tr.row_selected').removeClass('row_selected');
            $(this).addClass('row_selected');
        }
    });

    var oTable = $('#example').dataTable( {
        "aaSorting": [[ 6, "desc" ]],
        "bScrollCollapse": true,
        "sScrollY": "100%", 
        "iDisplayLength":"25",
		"bAutoWidth": true,
		  "bJQueryUI": true,
        "aoColumnDefs": [{ "bSearchable": true, "bVisible": false, "aTargets": [ 0 ] } ],
        "sPaginationType": "full_numbers",
        "oLanguage": {
            "sLengthMenu": "显示_MENU_条",
            "sZeroRecords": "没有您要搜索的内容",
            "sInfo": "从_START_ 到 _END_ 条记录——总记录数为 _TOTAL_ 条",
            "sInfoEmtpy": "记录数为0",
            "sInfoFiltered": "(全部记录数 _MAX_  条)",
            "sInfoPostFix": "",
            "sSearch": "输入任意文字搜索",
            "sUrl": "",
            "oPaginate": {
                            "sFirst":    "第一页",
                            "sPrevious": "向前",
                            "sNext":     "往后",
                            "sLast":     "最后一页"
                        }
        }
    } );


    $('#example tbody td a.clickView').live('click', function () {
        var nTr = $(this).parents('tr')[0];
        var aData = oTable.fnGetData( nTr );
        var findDate = aData[7];
        var value = $(findDate).attr('value');
        if ( oTable.fnIsOpen(nTr) )
        {
            /* This row is already open - close it */
            $(this).find('img.changeTitle').attr("src","/images/details_open.png");
            oTable.fnClose(nTr);
            if (value.indexOf('newName') >= 0) {
        	array  = value.split('&')[0].split('=')[1];
            } else {
        	array  = value.split('=')[1];
            }
            $('#'+array).remove();
        }
        else
        {
            /* Open this row */
            $(this).find('img.changeTitle').attr("src","/images/details_close.png");
            oTable.fnOpen(nTr, fnFormatDetails(oTable, nTr), 'details' );
            if (value.indexOf('newName') >= 0) {
        	getAddConfigs(value);
            } else {
        	getDetailInfoMsg(value);
            }
        }
    } );

$('input.showTips').poshytip({
    className: 'tip-yellowsimple',
    liveEvents: true,
    showOn: 'focus',
    alignTo: 'target',
    alignX: 'inner-left',
    offsetX: 0,
    offsetY: 5
});


     $('.mi-button').click(function(){
        $(this).addClass("changebuttioncl");
        }).mouseout(function(){
               $(this).removeClass("changebuttioncl");});

     $('#allCache').click(function(){
	 $.get("refreshCache.htm?type=allCache", function(data){
	     jAlert('全部缓存刷新结束!', 'anymock缓存刷新');
	 }, "text");
	 return false;
     });

     $('#commuCache').click(function(){
	 $.get("refreshCache.htm?type=commuCache",  function(data){
	  jAlert('通讯缓存刷新结束!', 'anymock缓存刷新');
	 }, "text");
	 return false;
     });

     $('#userCache').click(function(){
	 $.get("refreshCache.htm?type=userCache",  function(data){
	     jAlert('用户缓存刷新结束!', 'anymock缓存刷新');
	 }, "text");
	 return false;
     });

     $('#systemCache').click(function(){
	 $.get("refreshCache.htm?type=systemCache",  function(data){
	     jAlert('公共缓存刷新结束!', 'anymock缓存刷新');
	 }, "text");
	 return false;
     });

     $('#addUserCache').click( function(){
	 $.get("refreshCache.htm?type=addUserCache", function(data){
	     jAlert('公共和用户缓存刷新结束!', 'anymock缓存刷新');
	 }, "text");
	 return false;
     });
	  
	  $('#mergeForm').submit( function(){
	   var formRuslt = false;
        if ($('#firstmergeId').val() == ""){
			$('div.firstmergeId').removeClass('fn-hide');
			return  formRuslt;
		} else {
			$('div.firstmergeId').addClass('fn-hide');			
		}
	   if ($('#secondmergeId').val() == ""){
            $('div.secondmergeId').removeClass('fn-hide');
			return  formRuslt;
        } else {
            $('div.secondmergeId').addClass('fn-hide');          
        }
		
		if ($('#firstmergeId').val() == $('#secondmergeId').val()) {
			$('div.sammergeid').removeClass('fn-hide');
		} else {
			$('div.sammergeid').addClass('fn-hide'); 
			 formRuslt = true;
		}
     return formRuslt;
     });
	 	 	
} );





