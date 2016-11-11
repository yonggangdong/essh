<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
	$(function() {
		//初始化导航菜单
		initMenu();
	});
	
	/*  初始化导航菜单 */
	function initMenu(){
    	$.post("${ctx}/login!navTree.action", function(data) {
    	    $.each(data, function(i, n) {
    			var menulist = "<div class='easyui-panel' data-options='fit:true,border:false' style='overflow-y:auto;overflow-X: hidden;'><ul>";
    	        $.each(n.children, function(j, o) {//依赖于center界面选项卡layout_center_tabs对象
    	        	menulist += "<li><div><strong><a onClick='javascript:eu.addTab(layout_center_tabs,\""
        			    + o.text+"\",\"${ctx}" + o.attributes.url+ "\",true,\""+o.iconCls+"\")' style='font-size:14px;' > <span class='tree-icon tree-file "+o.iconCls+"'></span>" + o.text + "</a></strong></div></li> ";
    	        });
    	        menulist += '</ul></div>';
    	        
    	        $(".easyui-accordion").accordion('add', {
        			title : n.text,
        			content : menulist,
        			iconCls : n.iconCls
        		});
    	        
    	    });
    	    $('.easyui-accordion div li div strong a').click(function(){
    			$('.easyui-accordion li div').removeClass("selected");
    			$(this).parent().parent().addClass("selected");
    		}).hover(function(){
    			$(this).parent().parent().addClass("hover");
    		},function(){
    			$(this).parent().parent().removeClass("hover");
    		});
    	    
    	},"json"); 
    }
</script>
<!-- 导航菜单 -->
<div class="easyui-accordion" data-options="animate:false,fit:true,border:true">
</div>