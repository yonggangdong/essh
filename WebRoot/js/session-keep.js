window.setInterval(sessionInfo, 15 * 60 * 1000);
function sessionInfo() {
    $.ajax({
        type: "GET",
        url: "${ctx}/login!sessionInfo.action",
        cache: false,
        dataType: "json",
        success: function (data) {

        }});
}