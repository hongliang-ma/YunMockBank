var nav = {
    init: function(navID){
        var timer, nav, currentNavItem, currentSelectedNavItem, navItems = [], me = this;
        nav = $(navID);
        if (!nav.length) {
            return false;
        }
        navItems = nav.children();
        $.each(navItems, function(index, navItem){
            navItem = $(navItem);
            if (navItem.hasClass("mi-nav-item-current")) {
                currentNavItem = currentSelectedNavItem = navItem;
            }
            
            navItem.on("mouseover", function(e){
                if (timer) {
                    clearTimeout(timer);
                    timer = null;
                }
                var currentItem = $(e.currentTarget);
                timer = setTimeout(function(){
                    if (!currentSelectedNavItem) {
                        currentSelectedNavItem = currentNavItem;
                    }
                    if (currentSelectedNavItem) {
                        currentSelectedNavItem.removeClass("mi-nav-item-droplist");
                        currentSelectedNavItem.removeClass("mi-nav-item-current");
                    }
                    currentItem.addClass("mi-nav-item-droplist");
                    currentItem.addClass("mi-nav-item-current");
                    currentSelectedNavItem = currentItem;
                    me.mouseover.call(me, currentItem);
                }, 200);
            });
            
            navItem.on("mouseout", function(e){
                if (timer) {
                    clearTimeout(timer);
                    timer = null;
                }
                timer = setTimeout(function(){
                    if (currentSelectedNavItem) {
                        currentSelectedNavItem.removeClass("mi-nav-item-droplist");
                        currentSelectedNavItem.removeClass("mi-nav-item-current");
                        currentSelectedNavItem = null;
                    }
                    if (currentNavItem) {
                        currentNavItem.addClass("mi-nav-item-current");
                    }
                    me.mouseout.call(me, currentNavItem);
                }, 200);
                
            });
        });
    },
    mouseover : function(navItem){
        if(!navItem){return false;}
        if(!navItem.MInavIframeMask){
            navItem.MInavIframeMask = $(".mi-nav-dropiframe", navItem);
        }
        if(!navItem.MINavDropList){
            navItem.MINavDropList = $(".mi-nav-droplist", navItem);
        }
    //    console.log(navItem.attr("offsetHeight"));
        if(navItem.MInavIframeMask.length && navItem.MINavDropList.length){
            navItem.MInavIframeMask.css({
                width:(navItem.MINavDropList.outerWidth(true) - 2) + "px",
                height:(navItem.MINavDropList.outerHeight(true) - 2) + "px"
            });
        }
    },
    mouseout : function(navItem){
        
    }
};