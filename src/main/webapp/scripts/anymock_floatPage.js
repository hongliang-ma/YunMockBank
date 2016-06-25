function setFloatPageBt(){
    $(".more_cancel_bt").live('click', function(){
        var edit_Form = $(this).parent('form');
        edit_Form.resetForm();
    });
    
    $('.more_add_bt').live('click', function(){
        var d = new Date();
        var time = d.getHours() + d.getMinutes() + d.getSeconds();
        
        var divInert = '<div class="detailTools_normal_KeyValue">' +
        '匹配值:&nbsp;&nbsp;<textarea cols="20" rows="1" class="edit_input  editTextarea keyName" name="' +
        time +
        1 +
        '"></textarea>' +
        '返回值:&nbsp;&nbsp;<textarea  cols="50" rows="3" class="edit_input  editTextarea valueName" name="' +
        time +
        2 +
        '"></textarea>' +
        '<input class="more_add_bt" type="button" name="addBt" value="新增"/>' +
        '<input class="more_delete_bt" type="button" name="delBt" value="删除"/>' +
        '</div>';
        var parentDiv = $(this).parent(".detailTools_normal_KeyValue");
        parentDiv.after(divInert);
        
        return false;
    });
    
    $('.more_delete_bt').live('click', function(){
        var parentDiv = $(this).parent('.detailTools_normal_KeyValue');
        parentDiv.remove();
        
        return false;
    });
    
    $('a.addAssert').live('click', function(){
        var id = $(this).attr('value');
        var aBt = $(this);
        $.post('changeDetail.htm', {
            action: 'sureAssert',
            innerid: id
        }, function(msg){
            if (msg == 'exise') {
                jAlert('assert组件已经存在!', 'anymock错误警告');
            }
            else {
                if ($('form.assertAdd_Form').size() < 1) {
                    var divInert = '<div class="detailTools_normal_title"> <form class="assertAdd_Form" action="changeDetail.htm?action=addAsert&innerid=' + id + '" method="post">' +
                    '请求消息的类型:<select class="edit_input" name="reqType"><option value="XML">XML报文</option> <option value="FIXED">定长报文</option><option value="KEYVALUE">键值对报文</option>' +
                    '<option value="XMLMAX">键值对与XML混合</option></select>  校验值:<input class="edit_input" type="text" name="expected"/>' +
                    '<span class="mi-button mi-button-mblue operateAssert">' +
                    '<input class="mi-button-text" type="submit"  value="新增"/><input class="mi-button-text cancelAssert" type="button"  value="取消"/></span>' +
                    '</div>';
                    aBt.after(divInert);
                }
            }
        }, "text");
        return false;
    });
    
    $('form.assertAdd_Form').live("submit", function(){
        $(this).ajaxSubmit({
            beforeSubmit: showRequest,
            success: showAssertResult,
            dataType: "text"
        });
        return false;
    });
	

    
    $('#uploadForm').on("submit", function(){
        $(this).ajaxSubmit({
            success: showUpResult,
            dataType: "text"
        });
        return false;
    });
    
    $('form.assertAdd_Form input.cancelAssert').live("click", function(){
        var chooseDiv = $(this).parent('span').parent('form').parent('div');
        chooseDiv.remove();
    });
    
    $('.delAssert').live("click", function(){
        var formSelect = $(this).parent('span').parent('form.detailTools_normal');
        var id = formSelect.attr('action').split('&')[1].split('=')[1];
        $.post('changeDetail.htm', {
            action: 'deleteAssert',
            innerid: id
        }, function(msg){
            if (msg != 'good') {
                jAlert('assert工具删除失败!', 'anymock错误警告');
            }
            else {
                var chooseDiv = formSelect.parent('div');
                chooseDiv.remove();
            }
        }, "text");
    });
}

function showAssertResult(responseText, statusText, xhr, $form){
    if (responseText != "good") {
        $form.resetForm();
        jAlert(responseText, 'anymock错误警告');
    }
    else {
        $('.operateAssert').addClass("fn-hide");
    }
    return false;
}

function showUpResult(responseText, statusText, xhr, $form){
    if (responseText == "true") {
        jAlert('上传成功', 'anymock文件上传结果');
    }
    else {
        jAlert('上传失败', 'anymock文件上传结果');
    }
    return false;
}

function testSendMsg(){
    $('form.sendMsgTest').on('submit', function(){
        var sendUrl = $(this).children('input.sendTestUrl').val();
        var form = $(this);
        var sendMsg = $.trim(form.children('textarea.send_input').val());
        if ($.trim(sendMsg) == "") {
            jAlert('发送的内容不允许为空', 'anymock发送警告');
            return false;
        }
        
        var sendMedhod;
        var sendType = $(this).children('input.sendType').val();
        if (sendType == 'POST' || sendType == '') {
            sendMedhod = 'POST';
        }
        else {
            sendMedhod = 'GET';
        }
        
            $.ajax({
                type: sendMedhod,
                url: sendUrl,
                contentType: "text/xml;",
                processData: false, // 设为false是为了防止自动转换数据格式
                data: sendMsg,
                dataType: "text",
                success: function(responseText){
                    var textarea = form.parent('div').children('textarea.return_Msg');
                    $(textarea).val("");
                    $(textarea).val(responseText);
                }
            });        
        return false;
    });
    
    $('.clientMsgTest').on('submit', function(){
        $(this).ajaxSubmit({
            beforeSubmit: showRequest,
            success: writeMsg,
            dataType: "text"
        });
        return false;
    });
    
}

function writeMsg(responseText, statusText, xhr, $form){
    var textarea = $form.parent('div.simple_overlay').children('textarea.return_Msg');
    textarea.text("");
    textarea.text(responseText);
}
