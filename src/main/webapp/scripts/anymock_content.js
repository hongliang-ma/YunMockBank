function setshowUserInfoBt(){


    $(".cancel_bt").live("click", function(){
        var edit_Form = $(this).parent('form');
        edit_Form.resetForm();
    });

	   //8583的取消
	    $(".Parser_cancel_bt").live("click", function(){
        var edit_Form = $(this).parent('form');
            edit_Form.resetForm();
        });
    //直接本地的遮盖层
    $("a.overlay_bt[rel]").overlay();

//载入远程的加载遮盖层
$("a.Parser_detail[rel]").overlay({
        mask: 'darkred',
        effect: 'apple',
        onBeforeLoad: function() {
            // grab wrapper element inside content
            var wrap = this.getOverlay().find(".contentWrap");
            // load the page specified in the trigger
            wrap.load(this.getTrigger().attr("href"));
        },
		onLoad:function () {
			$('a.edit8583Liken:first').trigger("click");
        }
    });

    $(".editTextarea").autoTextarea({
        maxHeight: 220
    });

	 $(".edit_MessageParser").autoTextarea({
        maxHeight: 500
    });


      //绑定表单
    $('#userInfo_head_nameform').live("submit", function(){
        $(this).ajaxSubmit({
            beforeSubmit: showRequest,
            success: showResponse,
            dataType: "text"
        });
        return false;
    });
    $('#userInfo_head_matchstrform').live("submit", function(){
        $(this).ajaxSubmit({
            beforeSubmit: showRequest,
            success: showmacthResponse,
            dataType: "text"
        });
        return false;
    });

    $('.userInfo_head_atchwayform').live("submit", function(){
        $.confirm({
            'title': 'anymock修改唯一标识规则',
            'message': '<center><font color=VioletRed size="5">确认要修改匹配规则，该操作影响该渠道，请谨慎操作<img src="/images/question.gif"/></font></center>',
            'buttons': {
                'Yes': {
                    'class': 'blue',
                    'action': function(){
                        $('.userInfo_head_atchwayform').ajaxSubmit({
                            success: showResponse,
                            dataType: "text"
                        });
                    }
                },
                'No': {
                    'class': 'gray',
                    'action': function(){
                        $('.userInfo_head_atchwayform').resetForm();
                    } // Nothing to do in this case. You can as well omit the action property.
                }
            }
        });

        return false;
    });

    $('.userInfo_head_descrption').live("submit", function(){
        $(this).ajaxSubmit({
            beforeSubmit: showRequest,
            success: showResponse,
            dataType: "text"
        });
        return false;
    });

    $('.detailTools_normal').live("submit", function(){
        $(this).ajaxSubmit({
            beforeSubmit: showRequest,
            success: showResponse,
            dataType: "text"
        });
        return false;
    });

    $('.detailTools_MoreKeyValue_head').live("submit", function(){
        $(this).ajaxSubmit({
            beforeSubmit: showRequest,
            success: showResponse,
            dataType: "text"
        });
        return false;
    });

    $('.detailTools_MoreKeyValue').live("submit", function(){
        var sendUrl = $(this).children('input.keyValueUrl').val();
        var form = $(this);
        var recursiveEncoded = anymockSerialize($(this));
        if ($.trim(recursiveEncoded) == "") {
            $(this).resetForm();
            return false;
        }

        $.ajax({
            type: 'post',
            url: sendUrl,
            processData: false, // 设为false是为了防止自动转换数据格
            data: recursiveEncoded,
            dataType: "text",
            success: function(responseText){
                if (responseText != "good") {
                    form.resetForm();
					jAlert('不允许有相同的唯一标识符!', 'anymock错误警告');
                } else{
					 jAlert('修改成功', 'anymock确认');
				}
            }
        });

        return false;
    });

	    $('.MessageParser_view_form').live("submit", function(){
        $(this).ajaxSubmit({
            success: showParserResult,
            dataType: "text"
        });
        return false;
    });

	$('a.edit8583Liken').live('click', function(){
		    var showID = $(this).find("span").text();
		  var editForm = $('#'+showID).parent('form');
		  var divId;
		  editForm.children('div.edit8583Div').each(function(){
		  	divId = $(this).attr('id');
			if (divId == showID) {
				$('#'+divId).removeClass("fn-hide");
                $('div.contentWrap p span').html(showID);
			} else {
				$('#'+divId).addClass("fn-hide");
			}
		  });

		 return false;
	});

        $('form.edit8583Form').live("submit", function(){
        $(this).ajaxSubmit({
            success: showParserResult,
            dataType: "text"
        });
        return false;
    });
   
   
   $('form.uploadForm').live("submit", function() {
  $.ajaxFileUpload ({
    url:'/fileUploadPage.htm', 
    secureuri:false, 
    fileElementId:'file',
    dataType: 'text', 
    success: function (responseText) {
        if (responseText != "good") {
                 form.resetForm();
			     jAlert('文件上传失败!', 'anymock错误警告');
           }else {
                 jAlert('文件上传成功!', 'anymock确认');
                }
    }
  })
});
   
   
   
}

function showResponseOut(responseText, statusText, xhr, $form){
    if (responseText != "good") {
        jAlert('修改失败!', 'anymock错误警告');
    } else {
         jAlert('修改成功', 'anymock确认');
    }
    return false;
}

function showParserResult(responseText, statusText, xhr, $form){
    if (responseText != "good") {
		$form.resetForm();
        jAlert(responseText, 'anymock错误警告');
    } else {
		 jAlert('修改成功', 'anymock确认');
	}
    return false;
}

function showResponse(responseText, statusText, xhr, $form){
    if (responseText != "good") {
        $form.resetForm();
        jAlert('修改失败', 'anymock错误警告');
        return false;
    } else {
         jAlert('修改成功', 'anymock确认');
    }
}


function showmacthResponse(responseText, statusText, xhr, $form){
    if (responseText != "good") {
        $form.resetForm();
        jAlert('不允许有相同的唯一标识符!', 'anymock错误警告');
    } else {
         jAlert('修改成功', 'anymock确认');
    }
}

function showRequest(formData, $form, options){
    for (var i = 0; i < formData.length; i++) {
        if (!formData[i].value) {
            $form.resetForm();
			jAlert('提交的内容不允许为空!!', 'anymock错误警告');
            return false;
        }
    }

    return true;
}

function anymockSerialize($form){
    var detailTools_normal_KeyValue = $form.children('.detailTools_normal_KeyValue');
    var serialPara = new Array();
    var keyArray = new Array();
    detailTools_normal_KeyValue.each(function(){
        var key = $(this).children('.keyName').val();
        var value = $(this).children('.valueName').val();
        key = $.trim(key);
        if (key=="" || value=="") {
            jAlert('匹配值或者返回值不允许为空', 'anymock错误警告');
            return null;
        }
        if ($.inArray(key, keyArray) != -1) {
            jAlert('匹配值必须唯一', 'anymock错误警告');
            return null;
        }
        if (value.indexOf('&') >= 0) {
			value = value.replace(/\&/g, '=AmockspliteA=');
		}		
        serialPara.push(key + "=" + value);
        keyArray.push(key);
    });

    return serialPara.join("&");
}


