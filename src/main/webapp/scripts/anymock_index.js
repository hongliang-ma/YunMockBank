$(function(){

    $('#container').masonry({
        // options
        itemSelector: '.item',
        columnWidth: 240,//一列的宽度 Integer
        singleMode: false, // 如果浮动元素具有相同的宽度，设置为true
        isAnimated: true,
        gutterWidth: 0,//列的间隙 Integer
        isFitWidth: false,// 适应宽度   Boolean
        isResizableL: true,// 是否可调整大小 Boolean
        isRTL: false
    });
    
    $('#anymock3').on('click', function(){
        getFirstUrl();
    });
    
    $('img').poshytip({
        className: 'tip-yellowsimple',
        alignTo: 'target',
        alignX: 'center'
    });
});
