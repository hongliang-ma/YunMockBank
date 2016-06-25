//构建一个Select字符串
var orderSysValue = new Array();
var orderUserValue = new Array();
var orderTransferSysValue = new Array();
var orderTransferUserValue = new Array();

//构造一个下拉框
var buildSelect = function(id, title, name, arrselect, event){
    var selectBefore = '';
    if (event == "") {
        selectBefore += '<tr id="' + id + '"><td align="right">' + title + ':</td><td align="left"><select  name="' + name + '" class="txtBox">';
    }
    else {
        selectBefore += '<tr id="' + id + '"><td align="right">' + title + ':</td><td align="left"><select  name="' + name + '" class="txtBox" onchange="' + event + '()" >';
    }
    var selectMiddle = '';
    selectMiddle += '<option value="">请选择</option>';
    $.each(arrselect, function(key, val){
        selectMiddle += '<option value="' + key + '">' + val + '</option>';
    });
    var selectEnd = '</select></td><td align="left"><span id="'+name+'"></span>&nbsp;</td></tr>';

    return selectBefore + selectMiddle + selectEnd;
}
//构建一个input字符串
var buildInput = function(classname, title, name, showTitle){
    return '<tr  class="' + classname + '"><td align="right">' + title + ':</td><td align="left"><input class="showTips txtBox" type="text" name="' + name +
    '" value=""  title="'+showTitle+'"/></td></td><td align="left"><span id="'+name+'"></span>&nbsp;</td></tr>';
}

//构建一个掩藏的对象
var buildHidden = function(classname, name, value){
    return '<tr  class="' + classname + '"><td><input  type="hidden" name="' + name +
    '" value="' +
    value +
    '"/></td></tr>';
}

//构建一个掩藏的对象
var buildNotingTd = function(classname, title){
    return '<tr  class="' + classname + '"><td align="center">' + title + ':</td></tr>';
}

//构建一个Textarea字符串
var buildTextarea = function(classname, title, name, showTitle){
    return '<tr  class="' + classname + '"><td align="right">' + title + ':</td><td align="left"><textarea class="showTips txtBox editTextarea"  name="' + name +
    '" title="'+showTitle+'"></textarea></td></td><td align="left"><span id="'+name+'"></span>&nbsp;</td></tr>';
}

//构建一个fileUp字符串
var buildFileUp = function(classname, title, name ){
    return '<tr  class="' + classname + '"><td align="right">' + title + ':</td><td align="left"><input class="txtBox" type="file" name="' + name +
    '" value=""/></td></tr>';
}

//构建一个链接对象的对象
var buildLinke = function(classname, httpURl, urlDes){
    return '<tr  class="' + classname + '"><td align="right"><a href="' + httpURl +
    '" target="_blank">' +
    urlDes +
    '</a></td></tr>';
}

//添加系统公共顺序
function orderSysValueSet(arrayToSet, elementToSet, opeartType, nodeToOpera){
    if (opeartType == 'Add') {
        if (jQuery.inArray(nodeToOpera, arrayToSet) == -1) {
            arrayToSet.push(nodeToOpera);
        }
    }
    else {
        var index = jQuery.inArray(nodeToOpera, arrayToSet);
        if (-1 != index) {
            arrayToSet.splice(index, 1);
        }
    }
    $('#'+elementToSet).val(arrayToSet);
}

//添加用户配置顺序
function orderUserValueSet(arrayToSet, elementToSet, opeartType, nodeToOpera){
    if (opeartType == 'Add') {
        if (jQuery.inArray(nodeToOpera, arrayToSet) == -1) {
            arrayToSet.push(nodeToOpera);
        }
    }
    else {
        if ('AllRemove' == nodeToOpera) {
            arrayToSet = [];
        }
        else {
            var index = jQuery.inArray(nodeToOpera, arrayToSet);
            if (-1 != index) {
                arrayToSet.splice(index, 1);
            }
        }
    }

    $('#'+elementToSet).val(arrayToSet);
}

function systemCodeRuleChoose(){
    var systemCodeRuleValue = $('#systemToolsCodeRule option:selected').val();
    var appendstr = '';
    var parentDiv = $('table#systemTools');
    if (systemCodeRuleValue == 'IS8583') {       
	   if (!$('#systemMsgLengthParse').attr("checked")) {
	   	   orderSysValueSet(orderSysValue, 'orderSysValue',"Del", "CodeRule");
            $('.systemToolsCodeRuleSet,#systemToolsCodeRule,.systemCodeRuleSet, #sysisbinary, #syscallflag, #sysmtitypeSet, #sysstrbitset').remove();
			$('#systemCodeRule').removeAttr("checked");
	       alert('请先选择8583长度位解析规则!');
		   return false;
	   }
	   else {
	   	 $('.systemCodeRuleSet, #sysisbinary, #syscallflag, #sysmtitypeSet, #sysstrbitset').remove();
	   	appendstr += buildTextarea('systemCodeRuleSet', '8583基本配置', 'sys-CodeRule-j8583', '请下载模板，按照模板配置');
	   	appendstr += buildLinke('systemCodeRuleSet', '/images/8583baseConfig.xml', '8583基本配置模板下载');
	   	appendstr += buildSelect('sysisbinary', '8583是否为二进制', 'sys-CodeRule-isbinary', {
	   		'true': '是二进制',
	   		'false': '不是二进制'
	   	});
	   	appendstr += buildInput('systemCodeRuleSet', '8583TPDU值设置', 'sys-CodeRule-tpdu', '详见j8583文档');
	   	appendstr += buildInput('systemCodeRuleSet', '8583长度位数', 'sys-CodeRule-typelength', '详见j8583文档');
		appendstr += buildSelect('sysmtitypeSet', '8583MTI编码格式', 'sys-CodeRule-mtitype', {
                'ASCII': 'ASCII编码格式',
                'BCD': 'BCD编码格式'
            });
		appendstr += buildSelect('sysstrbitset', '8583BitMap的编码格式', 'sys-CodeRule-strbitset', {
                'Binary_64': 'Binary_64',
                'Binary_128': 'Binary_128',
				'Hex_64': 'Hex_64',
				'Hex_128': 'Hex_128'
            });
	   	appendstr += buildInput('systemCodeRuleSet', '唯一值取值说明', 'sys-CodeRule-Description', '请输入描述，便于其他人了解取值方式');
	   }
	} else if (systemCodeRuleValue == "XML") {
            $('.systemCodeRuleSet, #sysisbinary, #syscallflag, #sysmtitypeSet, #sysstrbitset').remove();
            appendstr += buildTextarea('systemCodeRuleSet', '设置唯一值规则', 'sys-CodeRule-codeRule', '通过该值找到用户配置，规则为Xpath,例如/Cartoon/Message/CPReq/name/text()');
            appendstr += buildSelect('syscallflag', '同异步判断', 'sys-CodeRule-callflag', {
                'true': '异步调用,作为客户端，直接调用',
                'false': '同步调用,作为服务器端，需要先解析'
            });
			appendstr += buildInput('systemCodeRuleSet', '唯一值取值说明', 'sys-CodeRule-Description', '请输入描述，便于其他人了解取值方式');
        }  else  if (systemCodeRuleValue == "") {
                $('.systemCodeRuleSet, #sysisbinary, #syscallflag, #sysmtitypeSet, #sysstrbitset').remove();
       } else if (systemCodeRuleValue == "FIXED"){
                $('.systemCodeRuleSet, #sysisbinary, #syscallflag, #sysmtitypeSet, #sysstrbitset').remove();
                appendstr += buildTextarea('systemCodeRuleSet', '设置唯一值规则', 'sys-CodeRule-codeRule', '定长取值方法是直接输入取值的区间，第一个是0');
				appendstr += buildInput('systemCodeRuleSet', '唯一值取值说明', 'sys-CodeRule-Description', '请输入描述，便于其他人了解取值方式');
            }  else if (systemCodeRuleValue == "KEYVALUE"){
                $('.systemCodeRuleSet, #sysisbinary, #syscallflag, #sysmtitypeSet, #sysstrbitset').remove();
                appendstr += buildTextarea('systemCodeRuleSet', '设置唯一值规则', 'sys-CodeRule-codeRule', '键值对报文取值，取值方法是[key],参数为要取值的键值对的key');
                appendstr += buildInput('systemCodeRuleSet', '唯一值取值说明', 'sys-CodeRule-Description', '请输入描述，便于其他人了解取值方式');
            } else if (systemCodeRuleValue == "XMLMAX"){
                $('.systemCodeRuleSet, #sysisbinary, #syscallflag, #sysmtitypeSet, #sysstrbitset').remove();
                appendstr += buildTextarea('systemCodeRuleSet', '设置唯一值规则', 'sys-CodeRule-codeRule', '取值方法是先使用[key]取出XML，再用Xpath 取出唯一值');
                appendstr += buildInput('systemCodeRuleSet', '唯一值取值说明', 'sys-CodeRule-Description', '请输入描述，便于其他人了解取值方式');
            }else if (systemCodeRuleValue == "REGULAR"){
                $('.systemCodeRuleSet, #sysisbinary, #syscallflag, #sysmtitypeSet, #sysstrbitset').remove();
                appendstr += buildTextarea('systemCodeRuleSet', '设置唯一值规则', 'sys-CodeRule-codeRule', '使用正则表达式寻找');
                appendstr += buildInput('systemCodeRuleSet', '唯一值取值说明', 'sys-CodeRule-Description', '请输入描述，便于其他人了解取值方式');
            }

    parentDiv.append(appendstr);
    return false;
}


function userCodeRuleChoose(){
    var userCodeRuleValue = $('#userToolsCodeRule').find("option:selected").val();
    var appendstr = '';
    var parentDiv = $('table#userTools');
    if (userCodeRuleValue == 'IS8583') {
        $('.userCodeRuleSet, #userisbinary, #usercallflag,#usermtitypeSet, #userstrbitset').remove();
		 appendstr += buildTextarea('userCodeRuleSet', '8583基本配置', 'CodeRule-j8583', '请下载模板，按照模板配置');
		 appendstr += buildLinke('userCodeRuleSet', '/images/8583baseConfig.xml', '8583基本配置模板下载');
        appendstr += buildSelect('userisbinary', '8583是否为二进制', 'CodeRule-isbinary', {
            'true': '是二进制',
            'false': '不是二进制'
        });
        appendstr += buildInput('userCodeRuleSet', '8583TPDU值设置', 'CodeRule-tpdu','详见j8583文档');
        appendstr += buildInput('userCodeRuleSet', '8583长度位数', 'CodeRule-typelength','详见j8583文档');
appendstr += buildSelect('usermtitypeSet', '8583MTI编码格式', 'CodeRule-mtitype', {
                'ASCII': 'ASCII编码格式',
                'BCD': 'BCD编码格式'
            });
appendstr += buildSelect('userstrbitset', '8583BitMap的编码格式', 'CodeRule-strbitset', {
                'Binary_64': 'Binary_64',
                'Binary_128': 'Binary_128',
                'Hex_64': 'Hex_64',
                'Hex_128': 'Hex_128'
            });
    }
    else if (userCodeRuleValue == "XML") {
            $('.userCodeRuleSet, #userisbinary, #usercallflag,#usermtitypeSet, #userstrbitset').remove();
            appendstr += buildTextarea('userCodeRuleSet', '返回值查找规则', 'CodeRule-codeRule', '通过该值找到用户配置，规则为Xpath,例如/Cartoon/Message/CPReq/name/text()');
            appendstr += buildSelect('usercallflag', '同异步判断', 'CodeRule-callflag', {
                'true': '异步调用,作为客户端，直接调用',
                'false': '同步调用,作为服务器端，需要先解析'
            });
        }  else  if (userCodeRuleValue == "") {
                $('.userCodeRuleSet, #userisbinary, #usercallflag,#usermtitypeSet, #userstrbitset').remove();
            }  else if (userCodeRuleValue == "FIXED") {
                $('.userCodeRuleSet, #userisbinary, #usercallflag,#usermtitypeSet, #userstrbitset').remove();
                appendstr += buildTextarea('userCodeRuleSet', '返回值查找规则', 'CodeRule-codeRule', '定长取值方法是直接输入取值的区间，第一个是0');
            }  else if (userCodeRuleValue == "KEYVALUE") {
                $('.userCodeRuleSet, #userisbinary, #usercallflag,#usermtitypeSet, #userstrbitset').remove();
                appendstr += buildTextarea('userCodeRuleSet', '返回值查找规则', 'CodeRule-codeRule', '键值对报文取值，取值方法是[key],参数为要取值的键值对的key');
            }  else if (userCodeRuleValue == "XMLMAX") {
                $('.userCodeRuleSet, #userisbinary, #usercallflag,#usermtitypeSet, #userstrbitset').remove();
                appendstr += buildTextarea('userCodeRuleSet', '返回值查找规则', 'CodeRule-codeRule', '取值方法是先使用[key]取出XML，再用Xpath 取出唯一值');
            }  else if (userCodeRuleValue == "REGULAR") {
                $('.userCodeRuleSet, #userisbinary, #usercallflag,#usermtitypeSet, #userstrbitset').remove();
                appendstr += buildTextarea('userCodeRuleSet', '返回值查找规则', 'CodeRule-codeRule', '使用正则表达式寻找');
            }
    parentDiv.append(appendstr);
    return false;
}

function userMsgLengthParseChoose(){
    var userCodeRuleValue = $('#userMsgLengthParseChoose').find("option:selected").val();
    var appendstr = '';
    var parentDiv = $('table#userTools');
    if (userCodeRuleValue == 'true') {
        $('.MsgLengthParse, #lengthEncoding').remove();
        appendstr += buildInput('MsgLengthParse', '长度位间隔', 'MsgLengthParse-interChar','详见j8583文档');
        appendstr += buildInput('MsgLengthParse', '长度位长度', 'MsgLengthParse-length','详见j8583文档');

    }
    else
        if (userCodeRuleValue == "false") {
            $('.MsgLengthParse, #lengthEncoding').remove();
            appendstr += buildInput('MsgLengthParse', '长度位间隔', 'MsgLengthParse-interChar','详见j8583文档');
            appendstr += buildInput('MsgLengthParse', '长度位长度', 'MsgLengthParse-length','详见j8583文档');
            appendstr += buildSelect('lengthEncoding', '长度为编码方式', 'MsgLengthParse-lengthEncoding', {
                'BCD': 'BCD编码',
                'ASCII': 'ASCII格式',
                'ASCII10': 'ASCII10格式',
                'ASCII_8': 'ASCII_8格式',
                'ASCII_10': 'ASCII_10格式',
                'ASCIIP': 'ASCIIP格式'
            });
        }
        else {
            $('.MsgLengthParse, #lengthEncoding').remove();
        }
    parentDiv.append(appendstr);
    return false;
}

function MsgLengthBuildSetChoose(){
    var userCodeRuleValue = $('#MsgLengthBuildSetChoose').find("option:selected").val();
    var appendstr = '';
    var parentDiv = $('table#userTools');
    if (userCodeRuleValue == 'true') {
        $('.MsgLengthBuild, #lengthEncod').remove();
        appendstr += buildInput('MsgLengthBuild', '长度位间隔', 'MsgLengthBuild-interChar','详见j8583文档');
        appendstr += buildInput('MsgLengthBuild', '长度位长度', 'MsgLengthBuild-length','详见j8583文档');

    }
    else
        if (userCodeRuleValue == "false") {
            $('.MsgLengthBuild, #lengthEncod').remove();
            appendstr += buildInput('MsgLengthBuild', '长度位间隔', 'MsgLengthBuild-interChar','详见j8583文档');
            appendstr += buildInput('MsgLengthBuild', '长度位长度', 'MsgLengthBuild-length','详见j8583文档');
            appendstr += buildSelect('lengthEncod', '长度为编码方式', 'MsgLengthBuild-lengthEncoding', {
                'BCD': 'BCD编码',
                'ASCII': 'ASCII格式',
                'ASCII10': 'ASCII10格式',
                'ASCII_8': 'ASCII_8格式',
                'ASCII_10': 'ASCII_10格式',
                'ASCIIP': 'ASCIIP格式'
            });
        }
        else {
            $('.MsgLengthBuild, #lengthEncod').remove();
        }
    parentDiv.append(appendstr);
    return false;
}

function DecoderChoose(){
    var userCodeRuleValue = $('#Decoderstate').find("option:selected").val();
    var appendstr = '';
    var parentDiv = $('table#userTools');
    if (userCodeRuleValue == 'XML' || userCodeRuleValue == "ORIGVALUE") {
        $('.DecoderBuild, #DecoderEncodetype').remove();
        appendstr += buildInput('DecoderBuild', 'XML取值方式', 'Decoder-codeRule','使用Xpath取值');
        appendstr += buildSelect('DecoderEncodetype', '解码方式', 'Decoder-Encodetype', {
            'base64': 'base64解码',
            '': '不使用解码器'
        });
    }
    else
        if (userCodeRuleValue == "KEYVALUE") {
            $('.DecoderBuild, #DecoderEncodetype').remove();
            appendstr += buildSelect('DecoderEncodetype', '解码方式', 'Decoder-Encodetype', {
                'base64': 'base64解码',
                '': '不使用解码器'
            });
        }
        else {
            $('.DecoderBuild, #DecoderEncodetype').remove();
        }
    parentDiv.append(appendstr);
    return false;
}

function MappingValueAction(){
    var userCodeRuleValue = $('#MappingValueAction').find("option:selected").val();
    var appendstr = '';
    var parentDiv = $('table#userTools');
    if (userCodeRuleValue == 'KEYVALUE') {
        $('.MappingValueActionInput').remove();
        appendstr += buildInput('MappingValueActionInput', '键值对取值关键字段', 'MappingValueAction-key', '报文中的关键字');
    }
    else if (userCodeRuleValue == "XMLMAX") {
            $('.MappingValueActionInput').remove();
            appendstr += buildInput('MappingValueActionInput', '取值方式', 'MappingValueAction-codeRule');
        }  else {
            $('.MappingValueActionInput').remove();
        }
    parentDiv.append(appendstr);
    return false;
}

//转发部分
function transfer_systemCodeRuleChoose(){
    var systemCodeRuleValue = $('#transfer_systemToolsCodeRule option:selected').val();
    var appendstr = '';
    var parentDiv = $('table#transfer_systemTools');
    if (systemCodeRuleValue == 'IS8583') {
        $('.transfer_systemCodeRuleSet, #transfer_sysisbinary, #transfer_syscallflag,#transfer_sysmtitypeSet,#transfer_sysstrbitset').remove();
        appendstr += buildTextarea('transfer_systemCodeRuleSet', '8583基本配置', 'transfer_sys-CodeRule-j8583', '请下载模板，按照模板配置');
        appendstr += buildLinke('transfer_systemCodeRuleSet', '/images/8583baseConfig.xml', '8583基本配置模板下载');
        appendstr += buildSelect('transfer_sysisbinary', '8583是否为二进制', 'transfer_sys-CodeRule-isbinary', {
            'true': '是二进制',
            'false': '不是二进制'
        });
        appendstr += buildInput('transfer_systemCodeRuleSet', '8583TPDU值设置', 'transfer_sys-CodeRule-tpdu','详见j8583文档');
        appendstr += buildInput('transfer_systemCodeRuleSet', '8583长度位数', 'transfer_sys-CodeRule-typelength','详见j8583文档');
		appendstr += buildSelect('transfer_sysmtitypeSet', '8583MTI编码格式', 'transfer_sys-CodeRule-mtitype', {
		                'ASCII': 'ASCII编码格式',
		                'BCD': 'BCD编码格式'
		            });
		appendstr += buildSelect('transfer_sysstrbitset', '8583BitMap的编码格式', 'transfer_sys-CodeRule-strbitset', {
		                'Binary_64': 'Binary_64',
		                'Binary_128': 'Binary_128',
		                'Hex_64': 'Hex_64',
		                'Hex_128': 'Hex_128'
		            });		
        appendstr += buildInput('transfer_systemCodeRuleSet', '唯一值取值说明', 'transfer_sys-CodeRule-Description', '请输入描述，便于其他人了解取值方式');
    } else if (systemCodeRuleValue == "XML") {
            $('.transfer_systemCodeRuleSet, #transfer_sysisbinary, #transfer_syscallflag,#transfer_sysmtitypeSet,#transfer_sysstrbitset').remove();
            appendstr += buildTextarea('transfer_systemCodeRuleSet', '设置唯一值规则', 'transfer_sys-CodeRule-codeRule', '通过该值找到用户配置，规则为Xpath,例如/Cartoon/Message/CPReq/name/text()');
            appendstr += buildSelect('transfer_syscallflag', '同异步判断', 'transfer_sys-CodeRule-callflag', {
                'true': '异步调用,作为客户端，直接调用',
                'false': '同步调用,作为服务器端，需要先解析'
            });
            appendstr += buildInput('transfer_systemCodeRuleSet', '唯一值取值说明', 'transfer_sys-CodeRule-Description', '请输入描述，便于其他人了解取值方式');
        }  else  if (systemCodeRuleValue == "") {
                $('.transfer_systemCodeRuleSet, #transfer_sysisbinary, #transfer_syscallflag,#transfer_sysmtitypeSet,#transfer_sysstrbitset').remove();
       } else if (systemCodeRuleValue == "FIXED") {
                $('.transfer_systemCodeRuleSet, #transfer_sysisbinary, #transfer_syscallflag,#transfer_sysmtitypeSet,#transfer_sysstrbitset').remove();
                appendstr += buildTextarea('transfer_systemCodeRuleSet', '设置唯一值规则', 'transfer_sys-CodeRule-codeRule','定长取值方法是直接输入取值的区间，第一个是0');
                appendstr += buildInput('transfer_systemCodeRuleSet', '唯一值取值说明', 'transfer_sys-CodeRule-Description', '请输入描述，便于其他人了解取值方式');
            }	else if (systemCodeRuleValue == "KEYVALUE") {
                $('.transfer_systemCodeRuleSet, #transfer_sysisbinary, #transfer_syscallflag,#transfer_sysmtitypeSet,#transfer_sysstrbitset').remove();
                appendstr += buildTextarea('transfer_systemCodeRuleSet', '设置唯一值规则', 'transfer_sys-CodeRule-codeRule', '键值对报文取值，取值方法是[key],参数为要取值的键值对的key');
                appendstr += buildInput('transfer_systemCodeRuleSet', '唯一值取值说明', 'transfer_sys-CodeRule-Description', '请输入描述，便于其他人了解取值方式');
            } else if (systemCodeRuleValue == "XMLMAX") {
                $('.transfer_systemCodeRuleSet, #transfer_sysisbinary, #transfer_syscallflag,#transfer_sysmtitypeSet,#transfer_sysstrbitset').remove();
                appendstr += buildTextarea('transfer_systemCodeRuleSet', '设置唯一值规则', 'transfer_sys-CodeRule-codeRule', '取值方法是先使用[key]取出XML，再用Xpath 取出唯一值');
                appendstr += buildInput('transfer_systemCodeRuleSet', '唯一值取值说明', 'transfer_sys-CodeRule-Description', '请输入描述，便于其他人了解取值方式');
            } else if (systemCodeRuleValue == "REGULAR") {
                $('.transfer_systemCodeRuleSet, #transfer_sysisbinary, #transfer_syscallflag,#transfer_sysmtitypeSet,#transfer_sysstrbitset').remove();
                appendstr += buildTextarea('transfer_systemCodeRuleSet', '设置唯一值规则', 'transfer_sys-CodeRule-codeRule','使用正则表达式寻找')
                appendstr += buildInput('transfer_systemCodeRuleSet', '唯一值取值说明', 'transfer_sys-CodeRule-Description', '请输入描述，便于其他人了解取值方式');
            }

    parentDiv.append(appendstr);
    return false;
}


function transfer_userCodeRuleChoose(){
    var userCodeRuleValue = $('#transfer_userToolsCodeRule').find("option:selected").val();
    var appendstr = '';
    var parentDiv = $('table#transfer_userTools');
    if (userCodeRuleValue == 'IS8583') {
        $('.transfer_userCodeRuleSet, #transfer_userisbinary, #transfer_usercallflag,#transfer_usermtitypeSet,#transfer_userstrbitset').remove();
         appendstr += buildTextarea('transfer_userCodeRuleSet', '8583基本配置', 'transfer_CodeRule-j8583', '请下载模板，按照模板配置');
         appendstr += buildLinke('transfer_userCodeRuleSet', '/images/8583baseConfig.xml', '8583基本配置模板下载');
        appendstr += buildSelect('transfer_userisbinary', '8583是否为二进制', 'transfer_CodeRule-isbinary', {
            'true': '是二进制',
            'false': '不是二进制'
        });
        appendstr += buildInput('transfer_userCodeRuleSet', '8583TPDU值设置', 'transfer_CodeRule-tpdu','详见j8583文档');
        appendstr += buildInput('transfer_userCodeRuleSet', '8583长度位数', 'transfer_CodeRule-typelength','详见j8583文档');
appendstr += buildSelect('transfer_usermtitypeSet', '8583MTI编码格式', 'transfer_CodeRule-mtitype', {
                'ASCII': 'ASCII编码格式',
                'BCD': 'BCD编码格式'
            });
appendstr += buildSelect('transfer_userstrbitset', '8583BitMap的编码格式', 'transfer_CodeRule-strbitset', {
                'Binary_64': 'Binary_64',
                'Binary_128': 'Binary_128',
                'Hex_64': 'Hex_64',
                'Hex_128': 'Hex_128'
            });
    }
    else if (userCodeRuleValue == "XML") {
            $('.transfer_userCodeRuleSet, #transfer_userisbinary, #transfer_usercallflag,#transfer_usermtitypeSet,#transfer_userstrbitset').remove();
            appendstr += buildTextarea('transfer_userCodeRuleSet', '返回值查找规则', 'transfer_CodeRule-codeRule');
            appendstr += buildSelect('transfer_usercallflag', '同异步判断', 'transfer_CodeRule-callflag', {
                'true': '异步调用，作为客户端，直接调用',
                'false': '同步调用，作为服务器端，需要先解析'
            });
        }
        else  if (userCodeRuleValue == "") {
                $('.transfer_userCodeRuleSet, #transfer_userisbinary, #transfer_usercallflag,#transfer_usermtitypeSet,#transfer_userstrbitset').remove();
            }  else   if (userCodeRuleValue == "FIXED"){
                $('.transfer_userCodeRuleSet, #transfer_userisbinary, #transfer_usercallflag,#transfer_usermtitypeSet,#transfer_userstrbitset').remove();
                appendstr += buildTextarea('transfer_userCodeRuleSet', '返回值查找规则', 'transfer_CodeRule-codeRule', '定长取值方法是直接输入取值的区间，第一个是0');
            }else   if (userCodeRuleValue == "KEYVALUE"){
                $('.transfer_userCodeRuleSet, #transfer_userisbinary, #transfer_usercallflag,#transfer_usermtitypeSet,#transfer_userstrbitset').remove();
                appendstr += buildTextarea('transfer_userCodeRuleSet', '返回值查找规则', 'transfer_CodeRule-codeRule', '键值对报文取值，取值方法是[key],参数为要取值的键值对的key');
            }else   if (userCodeRuleValue == "XMLMAX"){
                $('.transfer_userCodeRuleSet, #transfer_userisbinary, #transfer_usercallflag,#transfer_usermtitypeSet,#transfer_userstrbitset').remove();
                appendstr += buildTextarea('transfer_userCodeRuleSet', '返回值查找规则', 'transfer_CodeRule-codeRule', '取值方法是先使用[key]取出XML，再用Xpath 取出唯一值');
            }else   if (userCodeRuleValue == "REGULAR"){
                $('.transfer_userCodeRuleSet, #transfer_userisbinary, #transfer_usercallflag,#transfer_usermtitypeSet,#transfer_userstrbitset').remove();
                appendstr += buildTextarea('transfer_userCodeRuleSet', '返回值查找规则', 'transfer_CodeRule-codeRule', '使用正则表达式寻找');
            }
    parentDiv.append(appendstr);
    return false;
}

function transfer_MsgLengthBuildSetChoose(){
    var userCodeRuleValue = $('#transfer_MsgLengthBuildSetChoose').find("option:selected").val();
    var appendstr = '';
    var parentDiv = $('table#transfer_userTools');
    if (userCodeRuleValue == 'true') {
        $('.transfer_MsgLengthBuild, #transfer_lengthEncod').remove();
        appendstr += buildInput('transfer_MsgLengthBuild', '长度位间隔', 'transfer_MsgLengthBuild-interChar','详见j8583文档');
        appendstr += buildInput('transfer_MsgLengthBuild', '长度位长度', 'transfer_MsgLengthBuild-length','详见j8583文档');

    }
    else
        if (userCodeRuleValue == "false") {
            $('.transfer_MsgLengthBuild, #transfer_lengthEncod').remove();
            appendstr += buildInput('transfer_MsgLengthBuild', '长度位间隔', 'transfer_MsgLengthBuild-interChar','详见j8583文档');
            appendstr += buildInput('transfer_MsgLengthBuild', '长度位长度', 'transfer_MsgLengthBuild-length','详见j8583文档');
            appendstr += buildSelect('transfer_lengthEncod', '长度为编码方式', 'transfer_MsgLengthBuild-lengthEncoding', {
                'BCD': 'BCD编码',
                'ASCII': 'ASCII格式',
                'ASCII10': 'ASCII10格式',
                'ASCII_8': 'ASCII_8格式',
                'ASCII_10': 'ASCII_10格式',
                'ASCIIP': 'ASCIIP格式'
            });
        }
        else {
            $('.transfer_MsgLengthBuild, #transfer_lengthEncod').remove();
        }
    parentDiv.append(appendstr);
    return false;
}

function transfer_MappingValueAction(){
    var userCodeRuleValue = $('#transfer_MappingValueAction').find("option:selected").val();
    var appendstr = '';
    var parentDiv = $('table#transfer_userTools');
    if (userCodeRuleValue == 'KEYVALUE') {
        $('.transfer_MappingValueActionInput').remove();
        appendstr += buildInput('transfer_MappingValueActionInput', '键值对取值关键字段', 'transfer_MappingValueAction-key', '报文中的关键字');
    }
    else
        if (userCodeRuleValue == "XMLMAX") {
            $('.transfer_MappingValueActionInput').remove();
            appendstr += buildInput('transfer_MappingValueActionInput', '取值方式', 'transfer_MappingValueAction-codeRule');
        }
        else {
            $('.transfer_MappingValueActionInput').remove();
        }
    parentDiv.append(appendstr);
    return false;
}
//转发部分结束

//查看是否是客户端，如果是客户端的话做一些操作
function checkisClient(){
	var chooseValue = $('#serverChoose').val();
	if (chooseValue == 'CLIENT') {		
		$('.inputDis').attr('disabled', 'disabled');
		orderSysValueSet(orderSysValue, 'orderSysValue',"Add", "CodeRule");
        orderUserValueSet(orderUserValue,'orderUserValue','Add', 'CodeRule');
	}
	 return false;
}

//提交表单，在此之前，先检查参数是否合法
function onFinishCallback(){
       if(validateAllSteps()){
        $('form').submit();
       }
}

//校验每一个步骤
  function leaveAStepCallback(obj){
    var step_num= obj.attr('rel');
    return validateSteps(step_num);
  }


//校验所有的步骤
function validateAllSteps(){
    var isStepValid = true;

        if(validateStep4() == false ){
          isStepValid = false;
          $('#wizard').smartWizard('setError',{stepnum:4,iserror:true});
        }else{
          $('#wizard').smartWizard('setError',{stepnum:4,iserror:false});
        }
		
    return isStepValid;
}

//校验某一个步骤
 function validateSteps(step){
          var isStepValid = true;
      // validate step 1
      if(step == 1){
        if(validateStep1() == false ){
          isStepValid = false;
          $('#wizard').smartWizard('setError',{stepnum:step,iserror:true});
        }else{
          $('#wizard').smartWizard('setError',{stepnum:step,iserror:false});
        }
		checkisClient();	
      }

	        // validate step2
      if(step == 2){
        if(validateStep2() == false ){
          isStepValid = false;
          $('#wizard').smartWizard('setError',{stepnum:step,iserror:true});
        }else{
          $('#wizard').smartWizard('setError',{stepnum:step,iserror:false});
        }
      }

      // validate step3
      if(step == 3){
        if(validateStep3() == false ){
          isStepValid = false;
          $('#wizard').smartWizard('setError',{stepnum:step,iserror:true});
        }else{
          $('#wizard').smartWizard('setError',{stepnum:step,iserror:false});
        }
      }

    // validate step5
      if(step == 5){
        if(validateStep5() == false ){
          isStepValid = false;
          $('#wizard').smartWizard('setError',{stepnum:step,iserror:true});
        }else{
          $('#wizard').smartWizard('setError',{stepnum:step,iserror:false});
        }
      }
	 
	     // validate step6
      if(step == 6){
        if(validateStep6() == false ){
          isStepValid = false;
          $('#wizard').smartWizard('setError',{stepnum:step,iserror:true});
        }else{
          $('#wizard').smartWizard('setError',{stepnum:step,iserror:false});
        }
      }
	  
         // validate step7
      if(step == 7){
        if(validateStep7() == false ){
          isStepValid = false;
          $('#wizard').smartWizard('setError',{stepnum:step,iserror:true});
        }else{
          $('#wizard').smartWizard('setError',{stepnum:step,iserror:false});
        }
      }

      return isStepValid;
    }

function validateStep1(){
    var isValid = true;
    var URI = $('input[name="URI"]').val();
	if(!URI && URI.length <= 0){
         isValid = false;
         $('#URI').html('请输入URL地址').show();
		 return isValid;
       }else{
         $('#URI').html('').hide();
       }
    var description = $('input[name="description"]').val();
	   if(!description && description.length <= 0){
         isValid = false;
         $('#description').html('请输入通讯描述信息').show();
		 return isValid;
       }else{
         $('#description').html('').hide();
       }
    var anymock_labels = $('input[name="anymock_labels"]').val();
       if(!anymock_labels && anymock_labels.length <= 0){
         isValid = false;
         $('#anymock_labels').html('请输入业务标签,便于搜索').show();
		 return isValid;
       }else{
         $('#anymock_labels').html('').hide();
       }
    var chooseValue = $('#serverChoose').val();	
    if (chooseValue == 'SERVER') {
		if (URI.indexOf('localhost') == -1){
			 isValid = false;
			 $('#URI').html('服务器地址必须包含localhost').show();	
			 return isValid;	
		} else {
			 $('#URI').html('').hide();
		}
	}
	
	var protocol = $('select[name="protocol_type"] option:selected').val();
	if (URI.toUpperCase().indexOf(protocol+':')) {
		isValid = false;
		 $('#URI').html('选择的协议为'+protocol+'和输入url的协议不匹配').show();
	     return isValid;
       }else{
         $('#URI').html('').hide();
       }	
	
	if (isValid && (chooseValue != 'CLIENT')) {
		$('#commTips').removeClass('fn-hide');
	}
	
    return isValid;
}

function validateStep2(){
    var isValid = true;
	
	var chooseValue = $('#serverChoose').val();
    if (chooseValue == 'CLIENT') {
		$('.systemToolsCodeRuleSet').remove();
		var parentDiv = $('table#systemTools');
        var appendstr = buildHidden('systemToolsCodeRuleSet', 'sys-CodeRule-seletctTag', 'true');
        appendstr += buildHidden('systemToolsCodeRuleSet', 'sys-CodeRule-state', 'KEYVALUE');
        appendstr += buildHidden('systemToolsCodeRuleSet', 'sys-CodeRule-codeRule', '[transferId]');
        appendstr += buildHidden('systemToolsCodeRuleSet', 'sys-CodeRule-Description', '以transferId作为唯一码');
		parentDiv.append(appendstr);
       return isValid;
    }

	$('#systemTools input, #systemTools textarea').each(function(){
		var inputValue = $(this).val();
		var inputID = $(this).attr("name");

		if (!inputValue && inputValue.length <= 0) {
			 isValid = false;
			  $('#'+inputID).html('请输入内容').show();
			    return isValid;
		} else{
			 $('#'+inputID).html('').hide();
		}
	});

    $('#systemTools select').each(function(){
        var inputValue = $(this).val();
        var inputID = $(this).attr("name");

        if (!inputValue && inputValue=="") {
             isValid = false;
              $('#'+inputID).html('请选择参数').show();
			    return isValid;
        } else{
             $('#'+inputID).html('').hide();
        }
    });

	if (!$('#systemCodeRule').attr('checked')) {
		  isValid = false;
		  alert('唯一值选择规则为必选规则!');
		 return isValid;
	}
	
	   if (isValid && (chooseValue != 'CLIENT')) {
	   	$('#commTips').addClass('fn-hide');
		  $('#fullTip').addClass('fn-hide');
	   }
	   
	    var sysRuleValue = $('#systemToolsCodeRule').find("option:selected").val();
		if (sysRuleValue === 'IS8583') {
			$('#userCodeRule').addClass('fn-hide');
		} else {
			$('#userCodeRule').removeClass('fn-hide');
		}
	
    return isValid;
	
}

function validateStep3(){
    var isValid = true;
    var templatename = $('input[name="user_templatename"]').val();
    if(!templatename && templatename.length <= 0){
         isValid = false;
         $('#user_templatename').html('配置的名字必须输入').show();
		   return isValid;
       }else{
         $('#user_templatename').html('').hide();
       }
	
	var chooseValue = $('#serverChoose').val();
    if (chooseValue == 'CLIENT') {
       return isValid;
    }
		   
    var matchstr = $('input[name="user_matchstr"]').val();
       if(!matchstr && matchstr.length <= 0){
         isValid = false;
         $('#user_matchstr').html('唯一值必须输入').show();
		   return isValid;
       }else{
         $('#user_matchstr').html('').hide();
       }
	   
    if (isValid) {
		$('#commTips').removeClass('fn-hide');
		$('#fullTip').removeClass('fn-hide');
	}
    return isValid;
}


function validateStep4(){
    var isValid = true;
       
	var chooseValue = $('#serverChoose').val();
    if (chooseValue == 'CLIENT') {
		 $('.userCodeRuleSetHead').remove();
		var parentDiv = $('table#userTools');
        var appendstr = buildHidden('userCodeRuleSetHead', 'CodeRule-seletctTag', 'false');
        appendstr += buildHidden('userCodeRuleSetHead', 'CodeRule-classid', 'CodeRule');
        appendstr += buildHidden('userCodeRuleSetHead', 'CodeRule-cname', '协议码解析');
        appendstr += buildHidden('userCodeRuleSetHead', 'CodeRule-state', 'KEYVALUE');
        appendstr += buildHidden('userCodeRuleSetHead', 'CodeRule-codeRule', '[transCodeRule]');
        
        parentDiv.append(appendstr);
       return isValid;
    }
	
    $('#userTools input, #userTools textarea').each(function(){
        var inputValue = $(this).val();
        var inputID = $(this).attr("name");

        if (inputID != "XMLParse-namespace" && !inputValue && inputValue.length <= 0) {
             isValid = false;
              $('#'+inputID).html('请输入内容').show();
			    return isValid;
        } else{
             $('#'+inputID).html('').hide();
        }
    });

    $('#userTools select').each(function(){
        var inputValue = $(this).val();
        var inputID = $(this).attr("name");

        if (!inputValue && inputValue=="") {
             isValid = false;
              $('#'+inputID).html('请选择参数').show();
			    return isValid;
        } else{
             $('#'+inputID).html('').hide();
        }
    });
	
    var sysRuleValue = $('#systemToolsCodeRule').find("option:selected").val();
    if (!$('#userCodeRule').attr('checked')) {
		if (sysRuleValue != 'IS8583') {
			isValid = false;
			alert('返回值必须配置!');
			return isValid;
		}
    }
	   if (isValid) {
	   	$('#commTips').addClass('fn-hide');
			$('#fullTip').addClass('fn-hide');
	   }
    return isValid;
}

function validateStep5(){	
    var isValid = true;
    var URI = $('input[name="transfer_URI"]').val();
    if(!URI && URI.length <= 0){
         isValid = false;
         $('#transfer_URI').html('请输入URL地址').show();
		   return isValid;
       }else{
         $('#transfer_URI').html('').hide();
       }
    var description = $('input[name="transfer_description"]').val();
       if(!description && description.length <= 0){
         isValid = false;
         $('#transfer_description').html('请输入通讯描述信息').show();
		   return isValid;
       }else{
         $('#transfer_description').html('').hide();
       }
    var anymock_labels = $('input[name="transfer_anymock_labels"]').val();
       if(!anymock_labels && anymock_labels.length <= 0){
         isValid = false;
         $('#transfer_anymock_labels').html('请输入业务标签,便于搜索').show();
		   return isValid;
       }else{
         $('#transfer_anymock_labels').html('').hide();
       }
    
    var protocol = $('select[name="transfer_protocol_type"] option:selected').val();
    if (URI.toUpperCase().indexOf(protocol+':')) {
        isValid = false;
         $('#transfer_URI').html('选择的协议为'+protocol+'和输入url的协议不匹配').show();
         return isValid;
       }else{
         $('#transfer_URI').html('').hide();
       }

	   
	  orderSysValueSet(orderTransferSysValue, 'transfer_orderSysValue',"Add", "CodeRule");
	  
    return isValid;
}



function validateStep6(){
    var isValid = true;
    var templatename = $('input[name="transfer_user_templatename"]').val();
    if(!templatename && templatename.length <= 0){
         isValid = false;
         $('#transfer_user_templatename').html('配置的名字必须输入').show();
		   return isValid;
       }else{
         $('#transfer_user_templatename').html('').hide();
       }
	   
	     $('.transfer_systemToolsCodeRuleSet').remove();
        var parentDiv = $('table#transfer_userTools');
        var appendstr = buildHidden('transfer_systemToolsCodeRuleSet', 'transfer_sys-CodeRule-seletctTag', 'true');
        appendstr += buildHidden('transfer_systemToolsCodeRuleSet', 'transfer_sys-CodeRule-state', 'KEYVALUE');
        appendstr += buildHidden('transfer_systemToolsCodeRuleSet', 'transfer_sys-CodeRule-codeRule', '[transferId]');
        appendstr += buildHidden('transfer_systemToolsCodeRuleSet', 'transfer_sys-CodeRule-Description', '以transferId作为唯一码');
        parentDiv.append(appendstr);
    if (isValid) {
		$('#commTips').removeClass('fn-hide');
		$('#fullTip').removeClass('fn-hide');
	}
	
	      
        var sysRuleValue = $('#transfer_systemToolsCodeRule').find("option:selected").val();
        if (sysRuleValue === 'IS8583') {
            $('#transfer_userCodeRule').addClass('fn-hide');
        } else {
            $('#transfer_userCodeRule').removeClass('fn-hide');
        }
  
    return isValid;
}

function validateStep7(){
    var isValid = true;

    $('#transfer_userTools input, #transfer_userTools textarea').each(function(){
        var inputValue = $(this).val();
        var inputID = $(this).attr("name");

        if (inputID != "transfer_XMLParse-namespace" && !inputValue && inputValue.length <= 0) {
             isValid = false;
              $('#'+inputID).html('请输入内容').show();
			    return isValid;
        } else{
             $('#'+inputID).html('').hide();
        }
    });

    $('#transfer_userTools select').each(function(){
        var inputValue = $(this).val();
        var inputID = $(this).attr("name");

        if (!inputValue && inputValue=="") {
             isValid = false;
              $('#'+inputID).html('请选择参数').show();
			    return isValid;
        } else{
             $('#'+inputID).html('').hide();
        }
    });
    var sysRuleValue = $('#transfer_systemToolsCodeRule').find("option:selected").val();
    if (!$('#transfer_userCodeRule').attr('checked')) {
        if (sysRuleValue != 'IS8583') {
            isValid = false;
            alert('返回值必须配置!');
			  return isValid;
        }
    }
	
	   if (isValid) {
	   	$('#commTips').addClass('fn-hide');
		$('#fullTip').addClass('fn-hide');
	   }

    return isValid;
}


$(document).ready(function(){
    nav.init("#J_navMenuListUL");
    $('#wizard').smartWizard({
		keyNavigation: false, 
		onLeaveStep:leaveAStepCallback,
        onFinish: onFinishCallback
    });
    $(".editTextarea").autoTextarea({
        maxHeight: 220
    });

	//设置提示符
$('#setUpConfigNormal input[type="text"], #setUpConfigNormal textarea, textarea.showTips, input.textarea').poshytip({
    className: 'tip-yellowsimple',
	liveEvents: true,
    showOn: 'focus',
    alignTo: 'target',
    alignX: 'inner-left',
    offsetX: 0,
    offsetY: 5
});

    //开始普通部分
    //开始设置协议码,
    $('#systemCodeRule').live('click', function(){
        $('.systemToolsCodeRuleSet,#systemToolsCodeRule,.systemCodeRuleSet, #sysisbinary, #syscallflag, #sysmtitypeSet, #sysstrbitset').remove();
        var parentDiv = $('table#systemTools');
        if ($(this).attr('checked')) {
            //选中
            var appendstr = buildHidden('systemToolsCodeRuleSet', 'sys-CodeRule-seletctTag', 'true');
            appendstr += buildSelect('systemToolsCodeRule', '消息类型', 'sys-CodeRule-state', {
                'XML': 'XML报文',
                'FIXED': '定长报文',
                'KEYVALUE': '键值对报文',
                'XMLMAX': '键值对XML混合报文',
                'REGULAR': '正则表达式',
                'IS8583': '标准8583'
            }, 'systemCodeRuleChoose');
            parentDiv.append(appendstr);
            orderSysValueSet(orderSysValue, 'orderSysValue',"Add", "CodeRule");
        }
        else {
            //没有选择
            $('.systemToolsCodeRuleSet,#systemToolsCodeRule,.systemCodeRuleSet, #sysisbinary, #syscallflag, #sysmtitypeSet, #sysstrbitset').remove();
            orderSysValueSet(orderSysValue, 'orderSysValue',"Del", "CodeRule");
        }
    });

    $('#systemLengthCodeRule').live('click', function(){
        var parentDiv = $('table#systemTools');
        $('.sysLengthCodeRule').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildInput('sysLengthCodeRule', '定长截取规则', 'sys-LengthCodeRule-codeRule', '截取指定的长度,截取方法是报文取值的区间,第一个是0,例如0-3');
            parentDiv.append(appendstr);
            orderSysValueSet(orderSysValue, 'orderSysValue',"Add", "LengthCodeRule");
        }
        else {
            $('.sysLengthCodeRule').remove();
            orderSysValueSet(orderSysValue, 'orderSysValue',"Del", "LengthCodeRule");
        }
    });
	
    $('#systemBuildToString').live('click', function(){
        var parentDiv = $('table#systemTools');
        $('.sysBuildToString').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildInput('sysBuildToString', '特殊键值对关键字段', 'sys-BuildToString-key', 'Map报文的Key,多个Key按照报文的组装顺序用逗号隔开');
            parentDiv.append(appendstr);
            orderSysValueSet(orderSysValue, 'orderSysValue',"Add", "BuildToString");
        }
        else {
            $('.sysBuildToString').remove();
            orderSysValueSet(orderSysValue, 'orderSysValue',"Del", "BuildToString");
        }
    });

    $('#systemMsgLengthParse').live('click', function(){
        var parentDiv = $('table#systemTools');
        $('.sysMsgLengthParse, #sysfilterreverse, #syslengthEncoding').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildSelect('sysfilterreverse', '加解码方式', 'sys-MsgLengthParse-filterreverse', {
                'true': '截取字段',
                'false': '添加字段'
            });
            appendstr += buildInput('sysMsgLengthParse', '长度位长度', 'sys-MsgLengthParse-length', '详见8583文档');
            appendstr += buildInput('sysMsgLengthParse', '长度位间隔', 'sys-MsgLengthParse-interChar', '详见8583文档');
            appendstr += buildSelect('syslengthEncoding', '长度位加码方式', 'sys-MsgLengthParse-lengthEncoding', {
                'BCD': 'BCD格式',
                'ASCII': 'ASCII格式',
                'ASCII10': 'ASCII10格式',
                'ASCII_8': 'ASCII_8格式',
                'ASCII_10': 'ASCII_10格式',
                'ASCIIP': 'ASCIIP格式'
            });
            parentDiv.append(appendstr);
            orderSysValueSet(orderSysValue, 'orderSysValue',"Add", "MsgLengthParse");
        }
        else {
            $('.sysMsgLengthParse, #sysfilterreverse, #syslengthEncoding').remove();
            orderSysValueSet(orderSysValue, 'orderSysValue',"Del", "MsgLengthParse");
        }
    });

    $('#systemDecoder').live('click', function(){
        var parentDiv = $('table#systemTools');
        $('.sysDecoder, #sysDecoderEncodetype, #sysDecoderstate').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildSelect('sysDecoderEncodetype', '解码器类型', 'sys-Decoder-Encodetype', {
                'base64': 'base64解码',
                'no': '不选择解码器'
            });
            appendstr += buildInput('sysDecoder', '编码方式', 'sys-Decoder-codeRule','取值方式根据请求类型来确定，XML采用Xpath来确定，键值对使用[Key]来确定，原文则不用填写任何内容');
            appendstr += buildSelect('sysDecoderstate', '请求消息类型', 'sys-Decoder-state', {
                'xml': 'XML报文',
                'Keyvalue': '键值对报文',
                'OrigValue': '原始值OrigValue'
            });
            parentDiv.append(appendstr);
            orderSysValueSet(orderSysValue, 'orderSysValue',"Add", "Decoder");
        }
        else {
            $('.sysDecoder, #sysDecoderEncodetype, #sysDecoderstate').remove();
            orderSysValueSet(orderSysValue, 'orderSysValue',"Del", "Decoder");
        }
    });

    $('#systemXMLSubAdaptor').live('click', function(){
        var parentDiv = $('table#systemTools');
        $('.sysXMLSubAdaptor').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildInput('sysXMLSubAdaptor', '父截取规则', 'sys-XMLSubAdaptor-codeRule', 'XML的取值规则，使用Xpath');
            appendstr += buildInput('sysXMLSubAdaptor', '子截取规则', 'sys-XMLSubAdaptor-subRule','键值对取值规则');
            appendstr += buildInput('sysXMLSubAdaptor', '分割符', 'sys-XMLSubAdaptor-split','键值对分隔符');
            parentDiv.append(appendstr);
            orderSysValueSet(orderSysValue, 'orderSysValue',"Add", "XMLSubAdaptor");
        }
        else {
            $('.sysXMLSubAdaptor').remove();
            orderSysValueSet(orderSysValue, 'orderSysValue',"Del", "XMLSubAdaptor");
        }
    });
	
	    $('#systemStringSubAdaptor').live('click', function(){
        var parentDiv = $('table#systemTools');
        $('.sysSubAdaptorSet ,#sysSubAdaptorSetstate, #sysSubAdaptorSetRule').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('sysSubAdaptorSet', 'sys-StringSubAdaptor-classid', 'StringSubAdaptor');
            appendstr += buildHidden('sysSubAdaptorSet', 'sys-StringSubAdaptor-cname', ' 字符串截取工具');
            appendstr += buildInput('sysSubAdaptorSet', '截取方式','sys-StringSubAdaptor-codeRule','指定待截取字符串');
            appendstr += buildSelect('sysSubAdaptorSetstate', '字符串截取的类型', 'sys-StringSubAdaptor-state', {
                '1': '字符串左边截取',
                '2': '字符串右边截取',
                '3': '指定字符串'
            });
            appendstr += buildSelect('sysSubAdaptorSetRule', '截取结果处理', 'sys-StringSubAdaptor-isRule', {
                'TRUE': '设置成协议码',
                'FALSE': '放入发送报文中'
            });
            parentDiv.append(appendstr);
            orderSysValueSet(orderSysValue,'orderSysValue','Add', 'StringSubAdaptor');
        }
        else {
              $('.sysSubAdaptorSet ,#sysSubAdaptorSetstate, #sysSubAdaptorSetRule').remove();
            orderSysValueSet(orderSysValue, 'orderSysValue','Del', 'StringSubAdaptor');
        }
    });
	
	    $('#systemMapToKeyValue').live('click', function(){
        var parentDiv = $('table#systemTools');
        $('.sysMapToKeyValue').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('sysMapToKeyValue', 'sys-MapToKeyValue-classid', 'MapToKeyValue');
			  appendstr += buildHidden('sysMapToKeyValue', 'sys-MapToKeyValue-cname', ' MAP转键值对');
                appendstr += buildNotingTd('sysMapToKeyValue', 'MAP报文转键值对已经添加');
            parentDiv.append(appendstr);
            orderSysValueSet(orderSysValue,'orderSysValue','Add', 'MapToKeyValue');
        }
        else {
            $('.sysMapToKeyValue').remove();
             orderSysValueSet(orderSysValue, 'orderSysValue','Del', 'MapToKeyValue');
        }
    });

    //开始用户部分
    $('#usercancelAll').live('click', function(){
        if ($(this).attr('checked')) {
            $('table#userTools tr').remove();
            $("div#setUpUSerTools  .usercheckbox").removeAttr("checked");
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'AllRemove');
        }
    });

    $('#userGetMore_Bt').live('click', function(){
        $("div#setUpUSerToolsMore").slideToggle('normal');
        return false;
    });

    $('#userCodeRule').live('click', function(){
        var parentDiv = $('table#userTools');
        $('#userToolsCodeRule, .userCodeRuleSetHead, .userCodeRuleSet, #userisbinary, #usercallflag,#usermtitypeSet, #userstrbitset').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('userCodeRuleSetHead', 'CodeRule-seletctTag', 'false');
            appendstr += buildHidden('userCodeRuleSetHead', 'CodeRule-classid', 'CodeRule');
            appendstr += buildHidden('userCodeRuleSetHead', 'CodeRule-cname', '协议码解析');
            appendstr += buildSelect('userToolsCodeRule', '消息类型', 'CodeRule-state', {
                'XML': 'XML报文',
                'FIXED': '定长报文',
                'KEYVALUE': '键值对报文',
                'XMLMAX': '键值对XML混合报文',
                'REGULAR': '正则表达式',
                'IS8583': '标准8583'
            }, 'userCodeRuleChoose');
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'CodeRule');
        }
        else {
            $('#userToolsCodeRule, .userCodeRuleSetHead, .userCodeRuleSet, #userisbinary, #usercallflag,#usermtitypeSet, #userstrbitset').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'CodeRule');
        }
    });

    $('#userXMLParse').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.userXMLParseSet, #userToolsXMLParse').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('userXMLParseSet', 'XMLParse-classid', 'XMLParse');
            appendstr += buildHidden('userXMLParseSet', 'XMLParse-cname', 'XML协议码解析');
            appendstr += buildSelect('userToolsXMLParse', '消息类型', 'XMLParse-state', {
                'XML': 'XML报文',
                'FIXED': '定长报文'
            });
            appendstr += buildInput('userXMLParseSet', 'XM解析返回匹配值', 'XMLParse-key','匹配字符');
            appendstr += buildTextarea('userXMLParseSet', 'XM解析返回报文', 'XMLParse-value','返回报文配置');
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'XMLParse');
        }
        else {
            $('.userXMLParseSet, #userToolsXMLParse').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'XMLParse');
        }
    });

    $('#userSerialParse').live('click', function(){		
        var parentDiv = $('table#userTools');
        $('.userSerialParseSet, #userToolsSerialParse').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('userSerialParseSet', 'SerialParse-classid', 'SerialParse');
            appendstr += buildHidden('userSerialParseSet', 'SerialParse-cname', '定长解析');
            appendstr += buildSelect('userToolsSerialParse', '定长类报文内容', 'SerialParse-state', {
                'STRING': '普通字符串',
                'BYTE': '二进制字符串'
            });
            appendstr += buildTextarea('userSerialParseSet', '错误返回设置', 'SerialParse-template__error','发生错误时，返回的报文');
            appendstr += buildInput('userSerialParseSet', '定长解析返回匹配值', 'SerialParse-key','匹配字符');
            appendstr += buildTextarea('userSerialParseSet', '定长解析返回报文', 'SerialParse-value','返回报文配置');
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'SerialParse');
        }
        else {
            $('.userSerialParseSet, #userToolsSerialParse').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'SerialParse');
        }
    });

    $('#userKeyValueParse').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.userKeyValueParseSet, #userToolsKeyValueParse').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('userKeyValueParseSet', 'KeyValueParse-classid', 'KeyValueParse');
            appendstr += buildHidden('userKeyValueParseSet', 'KeyValueParse-cname', '键值对解析');
            appendstr += buildSelect('userToolsKeyValueParse', '键值对报文内容', 'KeyValueParse-state', {
                'KEYVALUE': '普通键值对',
                'XMLMAX': '键值对XML'
            });
            appendstr += buildInput('userKeyValueParseSet', '键值对解析返回匹配值', 'KeyValueParse-key','匹配字符');
            appendstr += buildTextarea('userKeyValueParseSet', '键值对解析返回报文', 'KeyValueParse-value','返回报文配置');
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'KeyValueParse');
        }
        else {
            $('.userKeyValueParseSet, #userToolsKeyValueParse').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'KeyValueParse');
        }
    });

    $('#userDelayAction').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.userDelayActionSet').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('userDelayActionSet', 'DelayAction-classid', 'DelayAction');
            appendstr += buildHidden('userDelayActionSet', 'DelayAction-cname', '延时工具');
            appendstr += buildInput('userDelayActionSet', '延时时间(单位毫秒)', 'DelayAction-delay','用来延长报文返回时间');
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'DelayAction');
        }
        else {
            $('.userDelayActionSet').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'DelayAction');
        }
    });

    $('#userLengthCodeRule').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.userLengthCodeRuleSet').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('userLengthCodeRuleSet', 'LengthCodeRule-classid', 'LengthCodeRule');
            appendstr += buildHidden('userLengthCodeRuleSet', 'LengthCodeRule-cname', '长度截取工具');
            appendstr += buildInput('userLengthCodeRuleSet', '长度截取规则', 'LengthCodeRule-codeRule','截取方法是报文取值的区间，第一个是0,例如0-3');
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'LengthCodeRule');
        }
        else {
            $('.userLengthCodeRuleSet').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'LengthCodeRule');
        }
    });

    $('#userBuildLengthAdaptor').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.userBuildLengthAdaptorSet').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('userBuildLengthAdaptorSet', 'BuildLengthAdaptor-classid', 'BuildLengthAdaptor');
            appendstr += buildHidden('userBuildLengthAdaptorSet', 'BuildLengthAdaptor-cname', '字符串添加工具');
            appendstr += buildInput('userBuildLengthAdaptorSet', '期望报文长度', 'BuildLengthAdaptor-Msglength','期望发送报文的长度,直接设置数字');
            appendstr += buildInput('userBuildLengthAdaptorSet', '长度不足添加字段', 'BuildLengthAdaptor-MsgType','不足长度时，添加的字段，一般为0');
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'BuildLengthAdaptor');
        }
        else {
            $('.userBuildLengthAdaptorSet').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'BuildLengthAdaptor');
        }
    });

    $('#userMessageParser').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.userMessageParserSet, #userToolsMessageParser, #userMessageParserContent,#usersysmtitypeSet').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('userMessageParserSet', 'MessageParser-classid', 'MessageParser');
            appendstr += buildHidden('userMessageParserSet', 'MessageParser-cname', '8583解析报文配置');
            appendstr += buildInput('userMessageParserSet', 'MTI长度', 'MessageParser-MTIlength','详见8583');
			appendstr += buildSelect('usersysmtitypeSet', '8583MTI编码格式', 'MessageParser-MTItype', {
                'ASCII': 'ASCII编码格式',
                'BCD': 'BCD编码格式'
            });
            appendstr += buildSelect('userToolsMessageParser', 'bitmap编码格式', 'MessageParser-bitmap', {
                'Binary_64': 'Binary_64',
                'Binary_128': 'Binary_128',
                'Hex_64': 'Hex_64',
                'Hex_128': 'Hex_128'
            });
            appendstr += buildSelect('userMessageParserContent', '内容类型', 'MessageParser-isbinary', {
                'true': '二进制内容',
                'false': '非二进制内容'
            });
            appendstr += buildInput('userMessageParserSet', 'TPDU值', 'MessageParser-TPDU','详见8583');
            appendstr += buildTextarea('userMessageParserSet', 'transMap请求/返回对应', 'MessageParser-transMap', ' 格式为:请求MTI:返回MTI,多个配置用分号隔开');
            appendstr += buildTextarea('userMessageParserSet', 'J8583配置', 'MessageParser-value','请根据下载的模板配置');
			 appendstr += buildLinke('userMessageParserSet', '/images/8583Parser.xml', '8583配置模板下载');
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'MessageParser');
        }
        else {
            $('.userMessageParserSet, #userToolsMessageParser, #userMessageParserContent,#usersysmtitypeSet').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'MessageParser');
        }
    });

    $('#userMessageBuild').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.userMessageBuildSet').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('userMessageBuildSet', 'MessageBuild-classid', 'MessageBuild');
            appendstr += buildHidden('userMessageBuildSet', 'MessageBuild-cname', '8583组装报文');
            appendstr += buildNotingTd('userMessageBuildSet', '8583 组装报文已添加');
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'MessageBuild');
        }
        else {
            $('.userMessageBuildSet').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'MessageBuild');
        }
    });

    $('#userMsgLengthParse').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.MsgLengthParseSet, .MsgLengthParse, #lengthEncoding, #userMsgLengthParseChoose').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('MsgLengthParseSet', 'MsgLengthParse-classid', 'MsgLengthParse');
            appendstr += buildHidden('MsgLengthParseSet', 'MsgLengthParse-cname', '8583长度位解析配置');
            appendstr += buildSelect('userMsgLengthParseChoose', '长度为解析方式', 'MsgLengthParse-filterreverse', {
                'true': '截取长度位',
                'false': '添加长度位'
            }, 'userMsgLengthParseChoose');
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'MsgLengthParse');
        }
        else {
            $('.MsgLengthParseSet, .MsgLengthParse, #lengthEncoding, #userMsgLengthParseChoose').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'MsgLengthParse');
        }
    });

    $('#userMsgLengthBuild, #user8583MsgLengthBuild').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.MsgLengthBuildSet, .MsgLengthBuild, #lengthEncod, #MsgLengthBuildSetChoose').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('MsgLengthBuildSet', 'MsgLengthBuild-classid', 'MsgLengthBuild');
            appendstr += buildHidden('MsgLengthBuildSet', 'MsgLengthBuild-cname', '8583长度位添加配置');
            appendstr += buildSelect('MsgLengthBuildSetChoose', '长度为添加方式', 'MsgLengthBuild-filterreverse', {
                'true': '截取长度位',
                'false': '添加长度位'
            }, 'MsgLengthBuildSetChoose');
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'MsgLengthBuild');
        }
        else {
            $('.MsgLengthBuildSet, .MsgLengthBuild, #lengthEncod, #MsgLengthBuildSetChoose').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'MsgLengthBuild');
        }
    });


    $('#userDecoder').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.DecoderSet, .DecoderBuild, #Decoderstate, #DecoderEncodetype').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('DecoderSet', 'Decoder-classid', 'Decoder');
            appendstr += buildHidden('DecoderSet', 'Decoder-cname', '解码/密配置');
            appendstr += buildSelect('Decoderstate', '请求消息类型', 'Decoder-state', {
                'XML': 'XML报文',
                'KEYVALUE': '键值对报文',
                'ORIGVALUE': 'OrigValue直接取值'
            }, 'DecoderChoose');
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'Decoder');
        }
        else {
            $('.EncoderSet, .DecoderBuild, #Decoderstate,#DecoderEncodetype').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'Decoder');
        }
    });

    $('#userDESMacAdaptor').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.DESMacAdaptorSet').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('DESMacAdaptorSet', 'DESMacAdaptor-classid', 'DESMacAdaptor');
            appendstr += buildHidden('DESMacAdaptorSet', 'DESMacAdaptor-cname', 'DES加密工具');
            appendstr += buildNotingTd('DESMacAdaptorSet', 'DES加密工具已添加');
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'DESMacAdaptor');
        }
        else {
            $('.DESMacAdaptorSet').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'DESMacAdaptor');
        }
    });

    $('#userKTFileUploadAdaptor').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.KTFileUploadAdaptorSet').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('KTFileUploadAdaptorSet', 'KTFileUploadAdaptor-classid', 'KTFileUploadAdaptor');
            appendstr += buildHidden('KTFileUploadAdaptorSet', 'KTFileUploadAdaptor-cname', '卡通文件上传配置');
            appendstr += buildNotingTd('KTFileUploadAdaptorSet', '普通文件上传已添加');
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'KTFileUploadAdaptor');
        }
        else {
            $('.KTFileUploadAdaptorSet').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'KTFileUploadAdaptor');
        }
    });

    $('#userFileUploadAdaptor').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.FileUploadAdaptorSet').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('FileUploadAdaptorSet', 'FileUploadAdaptor-classid', 'FileUploadAdaptor');
            appendstr += buildHidden('FileUploadAdaptorSet', 'FileUploadAdaptor-cname', '普通文件上传配置');
            appendstr += buildNotingTd('FileUploadAdaptorSet', '普通文件上传已添加');
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'FileUploadAdaptor');
        }
        else {
            $('.FileUploadAdaptorSet').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'FileUploadAdaptor');
        }
    });


    $('#userFileReaderAdaptor').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.FileReaderAdaptorSet').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('FileReaderAdaptorSet', 'FileReaderAdaptor-classid', 'FileReaderAdaptor');
            appendstr += buildHidden('FileReaderAdaptorSet', 'FileReaderAdaptor-cname', '服务器文件读取配置');
            appendstr += buildNotingTd('FileReaderAdaptorSet', '服务器文件读取已添加');
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'FileReaderAdaptor');
        }
        else {
            $('.FileReaderAdaptorSet').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'FileReaderAdaptor');
        }
    });

    $('#userDBConnectionAdaptor').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.DBConnectionAdaptorSet, #DBConnectionAdaptorChoose').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('DBConnectionAdaptorSet', 'DBConnectionAdaptor-classid', 'DBConnectionAdaptor');
            appendstr += buildHidden('DBConnectionAdaptorSet', 'DBConnectionAdaptor-cname', '数据库链接配置');
            appendstr += buildSelect('DBConnectionreqType', '消息类型', 'DBConnectionAdaptor-reqType', {
                'PARAM': '键值对报文',
                'CONTENT': '普通文本'
            });
            appendstr += buildInput('DBConnectionAdaptorSet', '数据库名字', 'DBConnectionAdaptor-dbName','数据库表名字');
            appendstr += buildInput('DBConnectionAdaptorSet', '用户名', 'DBConnectionAdaptor-userName','登陆账户');
            appendstr += buildInput('DBConnectionAdaptorSet', '密码', 'DBConnectionAdaptor-password','登陆密码');
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'DBConnectionAdaptor');
        }
        else {
            $('.DBConnectionAdaptorSet, #DBConnectionreqType').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'DBConnectionAdaptor');
        }
    });

    $('#userLoggerToDataAdaptor').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.LoggerToDataAdaptorSet, #LoggerToDataAdaptor').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('LoggerToDataAdaptorSet', 'LoggerToDataAdaptor-classid', 'LoggerToDataAdaptor');
            appendstr += buildHidden('LoggerToDataAdaptorSet', 'LoggerToDataAdaptor-cname', '日志记录到文件');
            appendstr += buildSelect('LoggerToDataAdaptor', '写入方式', 'LoggerToDataAdaptor-msgType', {
                'String': '字符串方式写入',
                'byte': '字节的方式写入'
            });
            appendstr += buildInput('LoggerToDataAdaptorSet', '日志文件存放路径', 'LoggerToDataAdaptor-directory','linux文件的文件路径');
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'LoggerToDataAdaptor');
        }
        else {
            $('.LoggerToDataAdaptorSet, #LoggerToDataAdaptor').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'LoggerToDataAdaptor');
        }
    });

    $('#userMappingValueAction, #user8583MappingValueAction').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.MappingValueActionSet, .MappingValueActionInput, #MappingValueAction').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('MappingValueActionSet', 'MappingValueAction-classid', 'MappingValueAction');
            appendstr += buildHidden('MappingValueActionSet', 'MappingValueAction-cname', '请求报文中获取MAP');
            appendstr += buildSelect('MappingValueAction', '请求报文', 'MappingValueAction-state', {
                'KEYVALUE': '键值对报文',
                'XML': 'XML报文',
                'XMLMAX': '键值对XML混合报文'
            }, 'MappingValueAction');
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'MappingValueAction');
        }
        else {
            $('.MappingValueActionSet, .MappingValueActionInput, #MappingValueAction').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'MappingValueAction');
        }
    });

    $('#userXMLSubAdaptor').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.XMLSubAdaptorSet').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('XMLSubAdaptorSet', 'XMLSubAdaptor-classid', 'XMLSubAdaptor');
            appendstr += buildHidden('XMLSubAdaptorSet', 'XMLSubAdaptor-cname', '截取XML协议码');
            appendstr += buildInput('XMLSubAdaptorSet', '截取主规则', 'XMLSubAdaptor-codeRule','XML的取值规则，使用Xpath');
            appendstr += buildInput('XMLSubAdaptorSet', '截取子规则', 'XMLSubAdaptor-subRule','键值对取值规则，直接填写key');
            appendstr += buildInput('XMLSubAdaptorSet', '分割符', 'XMLSubAdaptor-split','键值对分割符号');
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'XMLSubAdaptor');
        }
        else {
            $('.XMLSubAdaptorSet').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'XMLSubAdaptor');
        }
    });

    $('#userXMLToMapping').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.XMLToMappingSet').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('XMLToMappingSet', 'XMLToMapping-classid', 'XMLToMapping');
            appendstr += buildHidden('XMLToMappingSet', 'XMLToMapping-cname', 'XML转换Map');
            appendstr += buildNotingTd('XMLToMappingSet', 'XML转换Map工具已添加');
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'XMLToMapping');
        }
        else {
            $('.XMLToMappingSet').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'XMLToMapping');
        }
    });

    $('#userDESSH1Signer').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.DESSH1SignerSet, #DESSH1Signer,#DESSH1Signerbase64Flag,#DESSH1signFlag, #DESSH1Signerchunk，#DESSH1encryptWholeMsg').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('DESSH1SignerSet', 'DESSH1Signer-classid', 'DESSH1Signer');
            appendstr += buildHidden('DESSH1SignerSet', 'DESSH1Signer-cname', '3Des加密');
			  appendstr += buildSelect('DESSH1encryptWholeMsg', '是否全文加密', 'DESSH1Signer-encryptWholeMsg', {
                'true': '需要base64,建议选择需要',
                'false': '不需要'
            });			
			appendstr += buildSelect('DESSH1signFlag', '是否签名', 'DESSH1Signer-signFlag', {
                'false': '不需要',
			    'true': '需要签名'               
            });
            
            appendstr += buildSelect('DESSH1Signer', '编码格式', 'DESSH1Signer-encoding', {
                'GBK': 'GBK编码',
                'GB2312': 'GB2312编码',
                'UTF-8': 'UTF-8编码',
                'ASCII': 'ASCII编码',
                'UNICODE': 'UNICODE编码',
                'ISO-8859-1': 'ISO-8859-1编码'
            });
            appendstr += buildSelect('DESSH1Signerbase64Flag', '是否做base64编码', 'DESSH1Signer-base64Flag', {
                'true': '需要base64,建议选择需要',
                'false': '不需要'
            });
            appendstr += buildSelect('DESSH1Signerchunk', 'Base64编码后是否分段', 'DESSH1Signer-chunkFlag', {
                'true': '需要分段',
                'false': '不需要,建议选择不需要'
            });
            appendstr += buildInput('DESSH1SignerSet', '需要签名的XML标签', 'DESSH1Signer-signTagName');
            appendstr += buildInput('DESSH1SignerSet', '签名XML标签的父标签', 'DESSH1Signer-signParentTagName');
			appendstr += buildInput('DESSH1SignerSet', '私钥', 'DESSH1Signer-basekey');
			appendstr += buildInput('DESSH1SignerSet', '公钥', 'DESSH1Signer-workkey');
			appendstr += buildInput('DESSH1SignerSet', '需要摘要的标签', 'DESSH1Signer-shaTarget');
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'DESSH1Signer');
        }
        else {
            $('.DESSH1SignerSet, #DESSH1Signer,#DESSH1SignerFlag, #DESSH1signFlag, #DESSH1Signerchunk, #DESSH1encryptWholeMsg').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'DESSH1Signer');
        }
    });

    $('#userStringSubAdaptor').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.StringSubAdaptorSet ,#StringSubAdaptorType, #StringSubAdaptor').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('StringSubAdaptorSet', 'StringSubAdaptor-classid', 'StringSubAdaptor');
            appendstr += buildHidden('StringSubAdaptorSet', 'StringSubAdaptor-cname', ' 字符串截取工具');
            appendstr += buildInput('StringSubAdaptorSet', '截取方式', 'StringSubAdaptor-codeRule','指定要截取的字符串');
            appendstr += buildSelect('StringSubAdaptorType', '字符串截取的类型', 'StringSubAdaptor-state', {
                '1': '字符串左边截取',
                '2': '字符串右边截取',
                '3': '指定字符串'
            });
            appendstr += buildSelect('StringSubAdaptor', '截取结果处理', 'StringSubAdaptor-isRule', {
                'TRUE': '设置成协议码',
                'FALSE': '放入发送报文中'
            });
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'StringSubAdaptor');
        }
        else {
            $('.StringSubAdaptorSet ,#StringSubAdaptorType, #StringSubAdaptor').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'StringSubAdaptor');
        }
    });

    $('#userMoenyConvert').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.MoenyConvertSet, #MoenyConvert').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('MoenyConvertSet', 'MoenyConvert-classid', 'MoenyConvert');
            appendstr += buildHidden('MoenyConvertSet', 'MoenyConvert-cname', 'XML转换Map');
            appendstr += buildSelect('MoenyConvert', '报文类型', 'MoenyConvert-reqType', {
                'PARAMETER': '键值对报文',
                'CONTENT': '普通字符串文本',
                'CONTENTPARAMETER': '键值对+文本方式'
            });
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'MoenyConvert');
        }
        else {
            $('.MoenyConvertSet, #MoenyConvert').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'MoenyConvert');
        }
    });

    $('#userMD5Signer').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.MD5SignerSet, #MD5Signer, #MD5Signermode').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('MD5SignerSet', 'MD5Signer-classid', 'MD5Signer');
            appendstr += buildHidden('MD5SignerSet', 'MD5Signer-cname', 'MD5加签');
            appendstr += buildInput('MD5SignerSet', '需要加码的字符串', 'MD5Signer-node_template','根据报文,可以使用Xpath，定长+普通字符');
            appendstr += buildInput('MD5SignerSet', 'String的方法名', 'MD5Signer-stringmethod','处理该字符串的java的String方法名');
            appendstr += buildSelect('MD5Signer', '加码字符串获取方式', 'MD5Signer-style', {
                'FORWARD': '返回报文获取',
                'ORIGIN': '请求报文中获取'
            });
            appendstr += buildSelect('MD5Signermode', '取值模式', 'MD5Signer-mode', {
                '0': 'XML',
                '1': '定长'
            });
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'MD5Signer');
        }
        else {
            $('.MD5SignerSet, #MD5Signer, #MD5Signermode').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'MD5Signer');
        }
    });


    $('#userCallBackMessageParser').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.CallBackMessageParserSet, #userToolsCallBackMessageParser, #CallBackMessageParserisbinary,#callBacksysmtitypeSet').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('CallBackMessageParserSet', 'CallBackMessageParser-classid', 'CallBackMessageParser');
            appendstr += buildHidden('CallBackMessageParserSet', 'CallBackMessageParser-cname', '异步8583解析报文配置');
            appendstr += buildInput('CallBackMessageParserSet', 'MTI长度', 'CallBackMessageParser-MTIlength','详见J8583');
appendstr += buildSelect('callBacksysmtitypeSet', '8583MTI编码格式', 'CallBackMessageParser-MTItype', {
                'ASCII': 'ASCII编码格式',
                'BCD': 'BCD编码格式'
            });
            appendstr += buildSelect('CallBackMessageParserbitmap', 'bitmap类型', 'CallBackMessageParser-bitmap', {
                'Binary_64': 'Binary_64',
                'Binary_128': 'Binary_128',
                'Hex_64': 'Hex_64',
                'Hex_128': 'Hex_128'
            });
            appendstr += buildSelect('CallBackMessageParserisbinary', '内容类型', 'CallBackMessageParser-isbinary', {
                'true': '二进制内容',
                'false': '非二进制内容'
            });
            appendstr += buildInput('CallBackMessageParserSet', 'TPDU值', 'CallBackMessageParser-TPDU','详见J8583');
            appendstr += buildTextarea('CallBackMessageParserSet', 'transMap请求/返回对应', 'CallBackMessageParser-transMap',' 格式为:请求MTI:返回MTI,多个配置用分号隔开');
            appendstr += buildTextarea('CallBackMessageParserSet', 'J8583配置', 'CallBackMessageParser-value','请下载模板按模板配置');
			 appendstr += buildLinke('CallBackMessageParserSet', '/images/8583Parser.xml', '8583配置模板下载');
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'CallBackMessageParser');
        }
        else {
            $('.CallBackMessageParserSet, #CallBackMessageParserbitmap, #CallBackMessageParserisbinary,#callBacksysmtitypeSet').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'CallBackMessageParser');
        }
    });

    $('#userCallBackMessageBuild').live('click', function(){
        var parentDiv = $('table#userTools');
        $('.CallBackMessageBuildSet').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('CallBackMessageBuildSet', 'CallBackMessageBuild-classid', 'CallBackMessageBuild');
            appendstr += buildHidden('CallBackMessageBuildSet', 'CallBackMessageBuild-cname', '8583组装报文');
            appendstr += buildNotingTd('CallBackMessageBuildSet', '异步8583 组装报文已添加');
            parentDiv.append(appendstr);
            orderUserValueSet(orderUserValue,'orderUserValue','Add', 'CallBackMessageBuild');
        }
        else {
            $('.CallBackMessageBuildSet').remove();
            orderUserValueSet(orderUserValue,'orderUserValue','Del', 'CallBackMessageBuild');
        }
    });
	
	//开始转发的配置
    //开始普通部分
    //开始设置协议码,
    $('#transfer_systemCodeRule').live('click', function(){
        $('.transfer_systemToolsCodeRuleSet,#transfer_systemToolsCodeRule,.transfer_systemCodeRuleSet, #transfer_sysisbinary, #transfer_syscallflag,#transfer_sysmtitypeSet,#transfer_sysstrbitset').remove();
        var parentDiv = $('table#transfer_systemTools');
        if ($(this).attr('checked')) {
            //选中
            var appendstr = buildHidden('transfer_systemToolsCodeRuleSet', 'transfer_sys-CodeRule-seletctTag', 'true');
            appendstr += buildSelect('transfer_systemToolsCodeRule', '消息类型', 'transfer_sys-CodeRule-state', {
                'XML': 'XML报文',
                'FIXED': '定长报文',
                'KEYVALUE': '键值对报文',
                'XMLMAX': '键值对XML混合报文',
                'REGULAR': '正则表达式',
                'IS8583': '标准8583'
            }, 'transfer_systemCodeRuleChoose');
            parentDiv.append(appendstr);
            orderSysValueSet(orderTransferSysValue, 'transfer_orderSysValue',"Add", "CodeRule");
        }
        else {
            //没有选择
           $('.transfer_systemToolsCodeRuleSet,#transfer_systemToolsCodeRule,.transfer_systemCodeRuleSet, #transfer_sysisbinary, #transfer_syscallflag,#transfer_sysmtitypeSet,#transfer_sysstrbitset').remove();
            orderSysValueSet(orderTransferSysValue, 'transfer_orderSysValue',"Del", "CodeRule");
        }
    });

    //开始用户部分
    $('#transfer_usercancelAll').live('click', function(){
        if ($(this).attr('checked')) {
            $('table#transfer_userTools tr').remove();
            $("div#transfer_setUpUSerTools  .transfer_usercheckbox").removeAttr("checked");
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Del', 'AllRemove');
        }
    });

    $('#transfer_userGetMore_Bt').live('click', function(){
        $("div#transfer_setUpUSerToolsMore").slideToggle('normal');
        return false;
    });

    $('#transfer_userCodeRule').live('click', function(){
        var parentDiv = $('table#transfer_userTools');
        $('#transfer_userToolsCodeRule, .transfer_userCodeRuleSetHead, .transfer_userCodeRuleSet, #transfer_userisbinary, #transfer_usercallflag,#transfer_usermtitypeSet,#transfer_userstrbitset').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('transfer_userCodeRuleSetHead', 'transfer_CodeRule-seletctTag', 'false');
            appendstr += buildHidden('transfer_userCodeRuleSetHead', 'transfer_CodeRule-classid', 'CodeRule');
            appendstr += buildHidden('transfer_userCodeRuleSetHead', 'transfer_CodeRule-cname', '协议码解析');
            appendstr += buildSelect('transfer_userToolsCodeRule', '消息类型', 'transfer_CodeRule-state', {
                'XML': 'XML报文',
                'FIXED': '定长报文',
                'KEYVALUE': '键值对报文',
                'XMLMAX': '键值对XML混合报文',
                'REGULAR': '正则表达式',
                'IS8583': '标准8583'
            }, 'transfer_userCodeRuleChoose');
            parentDiv.append(appendstr);
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Add', 'CodeRule');
        }
        else {
            $('#transfer_userToolsCodeRule, .transfer_userCodeRuleSetHead, .transfer_userCodeRuleSet, #transfer_userisbinary, #transfer_usercallflag,#transfer_usermtitypeSet,#transfer_userstrbitset').remove();
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Del', 'CodeRule');
        }
    });

    $('#transfer_userXMLParse').live('click', function(){
        var parentDiv = $('table#transfer_userTools');
        $('.transfer_userXMLParseSet, #transfer_userToolsXMLParse').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('transfer_userXMLParseSet', 'transfer_XMLParse-classid', 'XMLParse');
            appendstr += buildHidden('transfer_userXMLParseSet', 'transfer_XMLParse-cname', 'XML协议码解析');
            appendstr += buildSelect('transfer_userToolsXMLParse', '消息类型', 'transfer_XMLParse-state', {
                'XML': 'XML报文',
                'FIXED': '定长报文'
            });
            appendstr += buildInput('transfer_userXMLParseSet', 'XM解析返回匹配值', 'transfer_XMLParse-key');
            appendstr += buildTextarea('transfer_userXMLParseSet', 'XM解析返回报文', 'transfer_XMLParse-value');
            parentDiv.append(appendstr);
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Add', 'XMLParse');
        }
        else {
            $('.transfer_userXMLParseSet, #transfer_userToolsXMLParse').remove();
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Del', 'XMLParse');
        }
    });

    $('#transfer_userSerialParse').live('click', function(){        
        var parentDiv = $('table#transfer_userTools');
        $('.transfer_userSerialParseSet, #transfer_userToolsSerialParse').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('transfer_userSerialParseSet', 'transfer_SerialParse-classid', 'SerialParse');
            appendstr += buildHidden('transfer_userSerialParseSet', 'transfer_SerialParse-cname', '定长解析');
            appendstr += buildSelect('transfer_userToolsSerialParse', '定长类报文内容', 'transfer_SerialParse-state', {
                'STRING': '普通字符串',
                'BYTE': '二进制字符串'
            });
            appendstr += buildInput('transfer_userSerialParseSet', '定长解析返回匹配值', 'transfer_SerialParse-key');
            appendstr += buildTextarea('transfer_userSerialParseSet', '定长解析返回报文', 'transfer_SerialParse-value');
            parentDiv.append(appendstr);
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Add', 'SerialParse');
        }
        else {
            $('.transfer_userSerialParseSet, #transfer_userToolsSerialParse').remove();
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Del', 'SerialParse');
        }
    });

    $('#transfer_userKeyValueParse').live('click', function(){
        var parentDiv = $('table#transfer_userTools');
        $('.transfer_userKeyValueParseSet,#transfer_userToolsKeyValueParse').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('transfer_userKeyValueParseSet', 'KeyValueParse-classid', 'KeyValueParse');
            appendstr += buildHidden('transfer_userKeyValueParseSet', 'KeyValueParse-cname', '键值对解析');
            appendstr += buildSelect('transfer_userToolsKeyValueParse', '键值对报文内容', 'transfer_KeyValueParse-state', {
                'KEYVALUE': '普通键值对',
                'XMLMAX': '键值对XML'
            });
            appendstr += buildInput('transfer_userKeyValueParseSet', '键值对解析返回匹配值', 'transfer_KeyValueParse-key','匹配字符');
            appendstr += buildTextarea('transfer_userKeyValueParseSet', '键值对解析返回报文', 'KeyValueParse-value','返回报文配置');
            parentDiv.append(appendstr);
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Add', 'KeyValueParse');
        }
        else {
            $('.transfer_userKeyValueParseSet, #transfer_userToolsKeyValueParse').remove();
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Del', 'KeyValueParse');
        }
    });

    $('#transfer_userDelayAction').live('click', function(){
        var parentDiv = $('table#transfer_userTools');
        $('.transfer_userDelayActionSet').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('transfer_userDelayActionSet', 'transfer_DelayAction-classid', 'DelayAction');
            appendstr += buildHidden('transfer_userDelayActionSet', 'transfer_DelayAction-cname', '延时工具');
            appendstr += buildInput('transfer_userDelayActionSet', '延时时间(单位毫秒)', 'transfer_DelayAction-delay');
            parentDiv.append(appendstr);
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Add', 'DelayAction');
        }
        else {
            $('.transfer_userDelayActionSet').remove();
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Del', 'DelayAction');
        }
    });


    $('#transfer_userBuildLengthAdaptor').live('click', function(){
        var parentDiv = $('table#transfer_userTools');
        $('.transfer_userBuildLengthAdaptorSet').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('transfer_userBuildLengthAdaptorSet', 'transfer_BuildLengthAdaptor-classid', 'BuildLengthAdaptor');
            appendstr += buildHidden('transfer_userBuildLengthAdaptorSet', 'transfer_BuildLengthAdaptor-cname', '字符串添加工具');
            appendstr += buildInput('transfer_userBuildLengthAdaptorSet', '期望报文长度', 'transfer_BuildLengthAdaptor-Msglength','期望发送报文的长度,直接设置数字');
            appendstr += buildInput('transfer_userBuildLengthAdaptorSet', '长度不足添加字段', 'transfer_BuildLengthAdaptor-MsgType','不足长度时，添加的字段，一般为0');
            parentDiv.append(appendstr);
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Add', 'BuildLengthAdaptor');
        }
        else {
            $('.transfer_userBuildLengthAdaptorSet').remove();
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Del', 'BuildLengthAdaptor');
        }
    });

    $('#transfer_userMessageBuild').live('click', function(){
        var parentDiv = $('table#transfer_userTools');
        $('.transfer_userMessageBuildSet').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('transfer_userMessageBuildSet', 'transfer_MessageBuild-classid', 'MessageBuild');
            appendstr += buildHidden('transfer_userMessageBuildSet', 'transfer_MessageBuild-cname', '8583组装报文');
            appendstr += buildNotingTd('transfer_userMessageBuildSet', '8583 组装报文已添加');
            parentDiv.append(appendstr);
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Add', 'MessageBuild');
        }
        else {
            $('.transfer_userMessageBuildSet').remove();
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Del', 'MessageBuild');
        }
    });

    $('#transfer_userMsgLengthBuild').live('click', function(){
        var parentDiv = $('table#transfer_userTools');
        $('.transfer_MsgLengthBuildSet, .transfer_MsgLengthBuild, #transfer_lengthEncod, #transfer_MsgLengthBuildSetChoose').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('transfer_MsgLengthBuildSet', 'transfer_MsgLengthBuild-classid', 'MsgLengthBuild');
            appendstr += buildHidden('transfer_MsgLengthBuildSet', 'transfer_MsgLengthBuild-cname', '8583长度位添加配置');
            appendstr += buildSelect('transfer_MsgLengthBuildSetChoose', '长度为添加方式', 'transfer_MsgLengthBuild-filterreverse', {
                'true': '截取长度位',
                'false': '添加长度位'
            }, 'transfer_MsgLengthBuildSetChoose');
            parentDiv.append(appendstr);
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Add', 'MsgLengthBuild');
        }
        else {
            $('.transfer_MsgLengthBuildSet, .transfer_MsgLengthBuild, #transfer_lengthEncod, #transfer_MsgLengthBuildSetChoose').remove();
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Del', 'MsgLengthBuild');
        }
    });


    $('#transfer_userDESMacAdaptor').live('click', function(){
        var parentDiv = $('table#transfer_userTools');
        $('.transfer_DESMacAdaptorSet').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('transfer_DESMacAdaptorSet', 'transfer_DESMacAdaptor-classid', 'DESMacAdaptor');
            appendstr += buildHidden('transfer_DESMacAdaptorSet', 'transfer_DESMacAdaptor-cname', 'DES加密工具');
            appendstr += buildNotingTd('transfer_DESMacAdaptorSet', 'DES加密工具已添加');
            parentDiv.append(appendstr);
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Add', 'DESMacAdaptor');
        }
        else {
            $('.transfer_DESMacAdaptorSet').remove();
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Del', 'DESMacAdaptor');
        }
    });
	
    $('.transfer_userMappingValueAction').live('click', function(){
        var parentDiv = $('table#transfer_userTools');
        $('.transfer_MappingValueActionSet, .transfer_MappingValueActionInput, #transfer_MappingValueAction').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('transfer_MappingValueActionSet', 'transfer_MappingValueAction-classid', 'MappingValueAction');
            appendstr += buildHidden('transfer_MappingValueActionSet', 'transfer_MappingValueAction-cname', '请求报文中获取MAP');
            appendstr += buildSelect('transfer_MappingValueAction', '请求报文', 'transfer_MappingValueAction-state', {
                'KEYVALUE': '键值对报文',
                'XML': 'XML报文',
                'XMLMAX': '键值对XML混合报文'
            }, 'transfer_MappingValueAction');
            parentDiv.append(appendstr);
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Add', 'MappingValueAction');
        }
        else {
            $('.transfer_MappingValueActionSet, .transfer_MappingValueActionInput, #transfer_MappingValueAction').remove();
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Del', 'MappingValueAction');
        }
    });

    $('.transfer_userXMLToMapping').live('click', function(){
        var parentDiv = $('table#transfer_userTools');
        $('.transfer_XMLToMappingSet').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('transfer_XMLToMappingSet', 'transfer_XMLToMapping-classid', 'XMLToMapping');
            appendstr += buildHidden('transfer_XMLToMappingSet', 'transfer_XMLToMapping-cname', 'XML转换Map');
            appendstr += buildNotingTd('transfer_XMLToMappingSet', 'XML转换Map工具已添加');
            parentDiv.append(appendstr);
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Add', 'XMLToMapping');
        }
        else {
            $('.transfer_XMLToMappingSet').remove();
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Del', 'XMLToMapping');
        }
    });

    $('.transfer_userDESSH1Signer').live('click', function(){
        var parentDiv = $('table#transfer_userTools');
     $('.transfer_DESSH1SignerSet, #transfer_DESSH1Signer,#transfer_DESSH1Signerbase64Flag,#transfer_DESSH1signFlag, #transfer_DESSH1Signerchunk，#transfer_DESSH1encryptWholeMsg').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('transfer_DESSH1SignerSet', 'transfer_DESSH1Signer-classid', 'DESSH1Signer');
            appendstr += buildHidden('transfer_DESSH1SignerSet', 'transfer_DESSH1Signer-cname', '3Des加密');
              appendstr += buildSelect('transfer_DESSH1encryptWholeMsg', '是否全文加密', 'transfer_DESSH1Signer-encryptWholeMsg', {
                'true': '需要base64,建议选择需要',
                'false': '不需要'
            });         
            appendstr += buildSelect('transfer_DESSH1signFlag', '是否签名', 'transfer_DESSH1Signer-signFlag', {
                'false': '不需要',
                'true': '需要签名'               
            });
            
            appendstr += buildSelect('transfer_DESSH1Signer', '编码格式', 'transfer_DESSH1Signer-encoding', {
                'GBK': 'GBK编码',
                'GB2312': 'GB2312编码',
                'UTF-8': 'UTF-8编码',
                'ASCII': 'ASCII编码',
                'UNICODE': 'UNICODE编码',
                'ISO-8859-1': 'ISO-8859-1编码'
            });
            appendstr += buildSelect('transfer_DESSH1Signerbase64Flag', '是否做base64编码', 'transfer_DESSH1Signer-base64Flag', {
                'true': '需要base64,建议选择需要',
                'false': '不需要'
            });
            appendstr += buildSelect('transfer_DESSH1Signerchunk', 'Base64编码后是否分段', 'transfer_DESSH1Signer-chunkFlag', {
                'true': '需要分段',
                'false': '不需要,建议选择不需要'
            });
            appendstr += buildInput('transfer_DESSH1SignerSet', '需要签名的XML标签', 'transfer_DESSH1Signer-signTagName');
            appendstr += buildInput('transfer_DESSH1SignerSet', '签名XML标签的父标签', 'transfer_DESSH1Signer-signParentTagName');
            appendstr += buildInput('transfer_DESSH1SignerSet', '私钥', 'transfer_DESSH1Signer-basekey');
            appendstr += buildInput('transfer_DESSH1SignerSet', '公钥', 'transfer_DESSH1Signer-workkey');
            appendstr += buildInput('transfer_DESSH1SignerSet', '需要摘要的标签', 'transfer_DESSH1Signer-shaTarget');
            parentDiv.append(appendstr);
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Add', 'DESSH1Signer');
        }
        else {
            $('.transfer_DESSH1SignerSet, #transfer_DESSH1Signer,#transfer_DESSH1SignerFlag, #transfer_DESSH1signFlag, #transfer_DESSH1Signerchunk, #transfer_DESSH1encryptWholeMsg').remove();
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Del', 'DESSH1Signer');
        }
    });

 
    $('.transfer_userMoenyConvert').live('click', function(){
        var parentDiv = $('table#transfer_userTools');
        $('.transfer_MoenyConvertSet, #transfer_MoenyConvert').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('transfer_MoenyConvertSet', 'transfer_MoenyConvert-classid', 'MoenyConvert');
            appendstr += buildHidden('transfer_MoenyConvertSet', 'transfer_MoenyConvert-cname', 'XML转换Map');
            appendstr += buildSelect('transfer_MoenyConvert', '报文类型', 'transfer_MoenyConvert-reqType', {
                'PARAMETER': '键值对报文',
                'CONTENT': '普通字符串文本',
                'CONTENTPARAMETER': '键值对+文本方式'
            });
            parentDiv.append(appendstr);
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Add', 'MoenyConvert');
        }
        else {
            $('.transfer_MoenyConvertSet, #transfer_MoenyConvert').remove();
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Del', 'MoenyConvert');
        }
    });

    $('.transfer_userMD5Signer').live('click', function(){
        var parentDiv = $('table#transfer_userTools');
        $('.transfer_MD5SignerSet, #transfer_MD5Signer, #transfer_MD5Signermode').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('transfer_MD5SignerSet', 'transfer_MD5Signer-classid', 'MD5Signer');
            appendstr += buildHidden('transfer_MD5SignerSet', 'transfer_MD5Signer-cname', 'MD5加签');
            appendstr += buildInput('transfer_MD5SignerSet', '需要加码的字符串', 'transfer_MD5Signer-node_template','根据报文,可以使用Xpath，定长+普通字符');
            appendstr += buildInput('transfer_MD5SignerSet', 'String的方法名', 'transfer_MD5Signer-stringmethod','处理该字符串的java的String方法名');
            appendstr += buildSelect('transfer_MD5Signer', '加码字符串获取方式', 'transfer_MD5Signer-style', {
                'FORWARD': '返回报文获取',
                'ORIGIN': '请求报文中获取'
            });
            appendstr += buildSelect('transfer_MD5Signermode', '取值模式', 'transfer_MD5Signer-mode', {
                '0': 'XML',
                '1': '定长'
            });
            parentDiv.append(appendstr);
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Add', 'MD5Signer');
        }
        else {
            $('.transfer_MD5SignerSet, #transfer_MD5Signer, #transfer_MD5Signermode').remove();
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Del', 'MD5Signer');
        }
    });

    $('.transfer_userCallBackMessageBuild').live('click', function(){
        var parentDiv = $('table#transfer_userTools');
        $('.transfer_CallBackMessageBuildSet').remove();
        if ($(this).attr('checked')) {
            var appendstr = buildHidden('transfer_CallBackMessageBuildSet', 'transfer_CallBackMessageBuild-classid', 'CallBackMessageBuild');
            appendstr += buildHidden('transfer_CallBackMessageBuildSet', 'transfer_CallBackMessageBuild-cname', '8583组装报文');
            appendstr += buildNotingTd('transfer_CallBackMessageBuildSet', '异步8583 组装报文已添加');
            parentDiv.append(appendstr);
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Add', 'CallBackMessageBuild');
        }
        else {
            $('.transfer_CallBackMessageBuildSet').remove();
            orderUserValueSet(orderTransferUserValue,'transfer_orderUserValue','Del', 'CallBackMessageBuild');
        }
    });
});
