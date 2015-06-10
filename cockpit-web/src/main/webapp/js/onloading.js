function addcloud() {
    document.body.appendChild(buildBase());
    document.body.appendChild(buildImgs()); //添加loading动画-
}

function buildBase(){
    var bodyWidth = document.documentElement.clientWidth;
    var bodyHeight = Math.max(document.documentElement.clientHeight, document.body.scrollHeight);
    var bgObj = document.createElement("div" );
    bgObj.setAttribute( 'id', 'bgDiv' );
    bgObj.style.position = "absolute";
    bgObj.style.top = "0";
    bgObj.style.background = "#000000";
    bgObj.style.filter = "progid:DXImageTransform.Microsoft.Alpha(style=3,opacity=25,finishOpacity=75" ;
    bgObj.style.opacity = "0.5";
    bgObj.style.left = "0";
    bgObj.style.width = bodyWidth + "px";
    bgObj.style.height = bodyHeight + "px";
    bgObj.style.zIndex = "10000"; //设置它的zindex属性，让这个div在z轴最大，用户点击页面任何东西都不会有反应|
    return bgObj;
}

function hideBase(){
    document.getElementById("bgDiv").style.display = "none";
    document.getElementById("bgDiv").style.zIndex = "-1";
}

function showBase(){
    document.getElementById("bgDiv").style.display = "block";
    document.getElementById("bgDiv").style.zIndex = "10000";
}

function buildImgs(){
    var bodyWidth = document.documentElement.clientWidth;
    var bodyHeight = Math.max(document.documentElement.clientHeight, document.body.scrollHeight);
    var loadingObj = document.createElement("div");
    loadingObj.setAttribute( 'id', 'loadingDiv' );
    loadingObj.style.position = "absolute";
    lw = 400;
    lh = 300;
    lt = (bodyHeight/2) - (lh/2);
    ll = (bodyWidth/2) - (lw/2);
    loadingObj.style.top = lt + "px";
    loadingObj.style.left = ll + "px";
    loadingObj.style.background = "url(images/loading.gif)" ;
    loadingObj.style.width = lw + "px";
    loadingObj.style.height = lh + "px";
    loadingObj.style.zIndex = "10000";
    return loadingObj;
}

function hideImgs(){
    document.getElementById("loadingDiv").style.display = "none";
}

function showImgs(){
    document.getElementById("loadingDiv").style.display = "block";
}

function hideCloud(){
    hideBase();
    hideImgs();
}

function showCloud(){
    showBase();
    showImgs();
}

function removecloud() {
    $( "#loadingDiv").remove();
    $( "#bgDiv").remove();
}
